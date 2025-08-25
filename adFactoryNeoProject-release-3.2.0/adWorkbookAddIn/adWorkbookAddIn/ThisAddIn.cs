using Excel = Microsoft.Office.Interop.Excel;
using Microsoft.Office.Tools;
using adWorkbookAddIn.Source;
using System.Runtime.InteropServices;
using System.Xml;
using System;
using System.IO;
using ExcelImport;
using System.Windows.Forms;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using NLog;
using System.Windows.Controls;

namespace adWorkbookAddIn
{
    public partial class ThisAddIn
    {
        private static readonly Logger logger = LogManager.GetCurrentClassLogger();

        public string AdFactoryHome { get; private set; }
        public XmlDocument ExcelFormat { get; private set; }
        public FileRoster FileRoster { get; private set; }
        public long? LoginOrganizationId { get; set; }
        public string LoginOrganizationLoginId { get; set; }

        private readonly Dictionary<Excel.Workbook, CustomTaskPane> workTaskPanes = new Dictionary<Excel.Workbook, CustomTaskPane>();
        private readonly Dictionary<Excel.Workbook, CustomTaskPane> checkItemsTaskPanes = new Dictionary<Excel.Workbook, CustomTaskPane>();
        private readonly Dictionary<Excel.Workbook, CustomTaskPane> drawingSupportTaskPanes = new Dictionary<Excel.Workbook, CustomTaskPane>();
        private readonly Dictionary<Excel.Workbook, CustomTaskPane> helpViewerTaskPanes = new Dictionary<Excel.Workbook, CustomTaskPane>();

        private readonly Dictionary<Excel.Workbook, bool> isValidWorkbookMap = new Dictionary<Excel.Workbook, bool>();

        /// <summary>
        /// Excel起動時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ThisAddIn_Startup(object sender, EventArgs e)
        {
            AdFactoryHome = Environment.GetEnvironmentVariable("ADFACTORY_HOME");

            try
            {
                ExcelFormat = new XmlDocument();
                ExcelFormat.Load(Path.Combine(AdFactoryHome, "adWorkbook", "conf", "excelFormat.xml"));
            }
            catch (Exception ex)
            {
                logger.Error(ex);
                ExcelFormat = null;
                Globals.Ribbons.CustomRibbon.ToggleButtonEnabled(false);
                return;
            }

            FileRoster = new FileRoster();

            Application.WorkbookOpen += Application_WorkbookOpen;
            Application.WorkbookBeforeClose += Application_WorkbookBeforeClose;
            Application.WorkbookActivate += Application_WorkbookActivate;

            Globals.Ribbons.CustomRibbon.ApplyLocaleToControl();
        }

        /// <summary>
        /// Excel終了時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ThisAddIn_Shutdown(object sender, EventArgs e)
        {
            if (ExcelFormat != null)
            {
                Application.WorkbookOpen -= Application_WorkbookOpen;
                Application.WorkbookBeforeClose -= Application_WorkbookBeforeClose;
                Application.WorkbookActivate -= Application_WorkbookActivate;
            }
            Marshal.ReleaseComObject(Application);
        }

        /// <summary>
        /// ワークブックが開いたときの処理
        /// </summary>
        /// <param name="Wb">ワークブック</param>
        private void Application_WorkbookOpen(Excel.Workbook Wb)
        {
            if (!ExcelImport.ExcelImport.ValidateWorkbook(ExcelFormat, Wb))
            {
                isValidWorkbookMap[Wb] = false;
                return;
            }
            isValidWorkbookMap[Wb] = true;

            try
            {
                // JSONファイルを開く
                FileRoster.OnOpen(Wb);

                // [images]フォルダーを作成
                string parent = ExcelUtils.GetLocalPath(Wb.Path);
                string path = Path.Combine(parent, "images");

                if (!Directory.Exists(path))
                {
                    // [images]フォルダーを作成
                    logger.Info("[images]フォルダーを作成: {0}", path);
                    Directory.CreateDirectory(path);
                }
            }
            catch (Exception ex)
            {
                logger.Error(ex);
                MessageBox.Show(
                    LocaleUtil.GetString("ExcelUtils.Validate.InvalidExternalFile"),
                    LocaleUtil.GetString("key.dataCheck"),
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
            }

            if (!workTaskPanes.ContainsKey(Wb))
            {
                workTaskPanes.Add(Wb, null);
            }

            if (!checkItemsTaskPanes.ContainsKey(Wb))
            {
                checkItemsTaskPanes.Add(Wb, null);
            }

            if (!drawingSupportTaskPanes.ContainsKey(Wb))
            {
                drawingSupportTaskPanes.Add(Wb, null);
            }

            if (!helpViewerTaskPanes.ContainsKey(Wb))
            {
                helpViewerTaskPanes.Add(Wb, null);
            }

            Wb.SheetActivate += Workbook_SheetActivate;
            Wb.AfterSave += Workbook_AfterSave;
            Wb.SheetSelectionChange += Workbook_SheetSelectionChange;
        }

        /// <summary>
        /// ワークブックを閉じる前の処理
        /// </summary>
        /// <param name="Wb">ワークブック</param>
        /// <param name="Cancel">キャンセル判定</param>
        private void Application_WorkbookBeforeClose(Excel.Workbook Wb, ref bool Cancel)
        {
            if (!isValidWorkbookMap.TryGetValue(Wb, out bool isValid))
            {
                return;
            }
            isValidWorkbookMap.Remove(Wb);
            if (!isValid)
            {
                return;
            }

            try
            {
                FileRoster.OnClose(Wb);
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }

            drawingSupportTaskPanes.Remove(Wb);
            if (drawingSupportTaskPanes.Count == 0)
            {
                DrawingSupportControl.DesignSupportControl_ControlClosing();
            }

            helpViewerTaskPanes.Remove(Wb);


            Wb.AfterSave -= Workbook_AfterSave;
            Wb.SheetSelectionChange -= Workbook_SheetSelectionChange;
        }

        /// <summary>
        /// ワークブックが有効になったときの処理
        /// </summary>
        /// <param name="Wb">ワークブック</param>
        private void Application_WorkbookActivate(Excel.Workbook Wb)
        {
            if (!isValidWorkbookMap.TryGetValue(Wb, out bool isValid))
            {
                isValid = false;
            }
            Globals.Ribbons.CustomRibbon.ToggleButtonEnabled(isValid);
        }

        /// <summary>
        /// シートが有効になったときの処理
        /// </summary>
        /// <param name="Sh">ワークシート</param>
        private void Workbook_SheetActivate(object Sh)
        {
            Excel.Workbook Wb = Application.ActiveWorkbook;
            if (workTaskPanes[Wb] != null && workTaskPanes[Wb].Visible)
            {
                workTaskPanes[Wb].Visible = false;
            }
            if (checkItemsTaskPanes[Wb] != null && checkItemsTaskPanes[Wb].Visible)
            {
                checkItemsTaskPanes[Wb].Visible = false;
            }
        }

        /// <summary>
        /// ワークブックが保存された後の処理
        /// </summary>
        /// <param name="Success">成功判定</param>
        private void Workbook_AfterSave(bool Success)
        {
            try
            {
                FileRoster.OnSave(Application.ActiveWorkbook);
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
        }

        /// <summary>
        /// 選択セルが変更されたときの処理
        /// </summary>
        /// <param name="Sh">ワークシート</param>
        /// <param name="Target">セル範囲</param>
        private void Workbook_SheetSelectionChange(object Sh, Excel.Range Target)
        {
            Excel.Worksheet sheet = (Excel.Worksheet)Sh;
            XmlNode workNode = ExcelFormat.DocumentElement.SelectSingleNode("/root/work");
            if (!Regex.IsMatch(sheet.Name, workNode.Attributes.GetNamedItem("sheet").Value))
            {
                return;
            }

            Excel.Workbook Wb = Application.ActiveWorkbook;
            Wb.SheetSelectionChange -= Workbook_SheetSelectionChange;
            try
            {
                // 選択中のセルの設定
                string targetCell = Target.Cells[1, 1].Address[false, false];

                StringBuilder sb = new StringBuilder();
                sb.Append(ExcelUtils.GetCell("workName", workNode, null).ToString());
                sb.Append(":");
                sb.Append(ExcelUtils.GetCell("workEnd", workNode, null).ToString());
                if (Application.Intersect(sheet.Range[targetCell], sheet.Range[sb.ToString()]) != null)
                {
                    ShowWorkControl();
                    return;
                }
                else
                {
                    XmlNode workProcedureNode = ExcelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure");
                    CellIterator workProcedureCells = new CellIterator(null, workProcedureNode);
                    bool inWorkProcedure = false;
                    while (workProcedureCells.HasNext(sheet))
                    {
                        sb.Clear();
                        sb.Append(workProcedureCells.GetCell().ToString());
                        sb.Append(":");
                        sb.Append(ExcelUtils.GetCell("workProcedureEnd", workProcedureNode, workProcedureCells.GetCell()).ToString());
                        if (Application.Intersect(sheet.Range[targetCell], sheet.Range[sb.ToString()]) != null)
                        {
                            inWorkProcedure = true;
                            break;
                        }
                        workProcedureCells.Next(sheet);
                    }

                    if (inWorkProcedure)
                    {
                        XmlNode checkItemsNode = ExcelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure/checkItems");
                        CellIterator checkItemsCells = new CellIterator(workProcedureCells.GetCell(), checkItemsNode);
                        int checkItemsCount = int.Parse(checkItemsNode.Attributes.GetNamedItem("count")?.Value);
                        for (int i = 1; i <= checkItemsCount && checkItemsCells.HasNext(sheet); i++)
                        {
                            if (ExcelUtils.GetCellStringNonBlank("tag", checkItemsNode, sheet, checkItemsCells.GetCell()).value == null)
                            {
                                checkItemsCells.Next(sheet);
                                continue;
                            }

                            sb.Clear();
                            sb.Append(checkItemsCells.GetCell().ToString());
                            sb.Append(":");
                            string endCell = ExcelUtils.GetCell("checkItemsEnd", checkItemsNode, checkItemsCells.GetCell()).ToString();

                            for (int j = i; j <= checkItemsCount; j++)
                            {
                                checkItemsCells.Next(sheet);
                                if (checkItemsCells.HasNext(sheet)
                                    || String.IsNullOrWhiteSpace(ExcelUtils.GetCellString("checkItemName", checkItemsNode, sheet, checkItemsCells.GetCell()).value)
                                    || !String.IsNullOrWhiteSpace(ExcelUtils.GetCellString("tag", checkItemsNode, sheet, checkItemsCells.GetCell()).value)
                                    || j == checkItemsCount)
                                {
                                    // 次行に、項目名なし もしくは、タグありの場合
                                    sb.Append(endCell);
                                    i = j;
                                    break;
                                }
                                endCell = ExcelUtils.GetCell("checkItemsEnd", checkItemsNode, checkItemsCells.GetCell()).ToString();
                            }

                            if (checkItemsTaskPanes.ContainsKey(Wb) && checkItemsTaskPanes[Wb] != null)
                            {
                                CheckItemsControl control = (CheckItemsControl)checkItemsTaskPanes[Wb].Control;
                                control.ChangeTag();
                            }

                            if (Application.Intersect(sheet.Range[targetCell], sheet.Range[sb.ToString()]) != null)
                            {
                                ShowCheckItemsControl(sheet.Range[sb.ToString()]);
                                return;
                            }
                        }
                    }
                }

                if (workTaskPanes[Wb] != null && workTaskPanes[Wb].Visible)
                {
                    workTaskPanes[Wb].Visible = false;
                }

                if (checkItemsTaskPanes[Wb] != null && checkItemsTaskPanes[Wb].Visible)
                {
                    checkItemsTaskPanes[Wb].Visible = false;
                }
            }
            finally
            {
                Wb.SheetSelectionChange += Workbook_SheetSelectionChange;
                Marshal.ReleaseComObject(Wb);
                Marshal.ReleaseComObject(sheet);
            }
        }

        /// <summary>
        /// 工程作業ウィンドウの表示
        /// </summary>
        private void ShowWorkControl()
        {
            try
            {
                Excel.Workbook Wb = Application.ActiveWorkbook;
                if (checkItemsTaskPanes.ContainsKey(Wb))
                {
                    if (checkItemsTaskPanes[Wb] != null && checkItemsTaskPanes[Wb].Visible)
                    {
                        checkItemsTaskPanes[Wb].Visible = false;
                    }
                }
                if (workTaskPanes.ContainsKey(Wb))
                {
                    WorkControl control;
                    if (workTaskPanes[Wb] == null)
                    {
                        control = new WorkControl();
                        workTaskPanes[Wb] = CustomTaskPanes.Add(control, LocaleUtil.GetString(control.Name));
                        workTaskPanes[Wb].Width = 370;
                    }
                    control = (WorkControl)workTaskPanes[Wb].Control;
                    control.InitializeTaskPaneData();

                    if (!workTaskPanes[Wb].Visible)
                    {
                        workTaskPanes[Wb].Visible = true;
                    }
                }
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
        }

        /// <summary>
        /// チェック項目作業ウィンドウの表示
        /// </summary>
        /// <param name="range">セル範囲</param>
        private void ShowCheckItemsControl(Excel.Range range)
        {
            try
            {
                Excel.Workbook Wb = Application.ActiveWorkbook;
                if (workTaskPanes.ContainsKey(Wb))
                {
                    if (workTaskPanes[Wb] != null && workTaskPanes[Wb].Visible)
                    {
                        workTaskPanes[Wb].Visible = false;
                    }
                }

                if (checkItemsTaskPanes.ContainsKey(Wb))
                {
                    CheckItemsControl control;
                    if (checkItemsTaskPanes[Wb] == null)
                    {
                        control = new CheckItemsControl();
                        checkItemsTaskPanes[Wb] = CustomTaskPanes.Add(control, LocaleUtil.GetString(control.Name));
                        checkItemsTaskPanes[Wb].Width = 370;
                    }

                    control = (CheckItemsControl)checkItemsTaskPanes[Wb].Control;
                    control.InitializeTaskPaneData(range);

                    if (!checkItemsTaskPanes[Wb].Visible)
                    {
                        checkItemsTaskPanes[Wb].Visible = true;
                    }
                }
            }
            catch(Exception ex)
            {
                logger.Error(ex);
            }
        }

        /// <summary>
        /// 作図支援作業ウィンドウの表示
        /// </summary>
        public void ShowDrawingSupportControl()
        {
            Excel.Workbook Wb = Application.ActiveWorkbook;
            if (drawingSupportTaskPanes[Wb] == null)
            {
                DrawingSupportControl control = new DrawingSupportControl();
                drawingSupportTaskPanes[Wb] = CustomTaskPanes.Add(control, LocaleUtil.GetString(control.Name));
                drawingSupportTaskPanes[Wb].Width = 400;
            }
            drawingSupportTaskPanes[Wb].Visible = !drawingSupportTaskPanes[Wb].Visible;
        }

        /// <summary>
        /// ヘルプを表示する。
        /// </summary>
        public void ShowHelpViewer()
        {
            Excel.Workbook Wb = Application.ActiveWorkbook;
            if (helpViewerTaskPanes[Wb] == null)
            {
                HelpViewer viewer = new HelpViewer();
                helpViewerTaskPanes[Wb] = CustomTaskPanes.Add(viewer, LocaleUtil.GetString(viewer.Name));
                helpViewerTaskPanes[Wb].Width = 400;
            }
            helpViewerTaskPanes[Wb].Visible = !helpViewerTaskPanes[Wb].Visible;
        }

        #region VSTO で生成されたコード

        /// <summary>
        /// デザイナーのサポートに必要なメソッドです。
        /// コード エディターで変更しないでください。
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }

        #endregion
    }
}
