using ExcelImport;
using NLog;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Xml;
using Excel = Microsoft.Office.Interop.Excel;

namespace adWorkbookAddIn.Source
{
    public partial class WorkProcedureForm : Form
    {
        private static readonly Logger logger = LogManager.GetCurrentClassLogger();

        private readonly Excel.Application app;
        private readonly XmlDocument excelFormat;
        private readonly WorkProcedureService wps;
        private readonly XmlNode workParameterNode;

        private Excel.Worksheet workflowSheet;
        private Excel.Worksheet selectedSheet;
        private Excel.Worksheet tempSheet;
        private double selectedSheetZoom;

        private readonly Dictionary<int, string> sheetNames = new Dictionary<int, string>();
        private int selectedSheetIndexCount;
        private bool isTempSheetChanged;

        private int workNo;
        private List<int?> workProcedureNo;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public WorkProcedureForm()
        {
            InitializeComponent();

            app = Globals.ThisAddIn.Application;
            excelFormat = Globals.ThisAddIn.ExcelFormat;

            wps = new WorkProcedureService();
            workParameterNode = excelFormat.DocumentElement.SelectSingleNode("/root/workflow/workParameter");

            // フォームの設定
            Excel.Window window = app.ActiveWindow;
            int width = Properties.Settings.Default.WORK_PROCEDURE_FORM_WIDTH;
            int height = Properties.Settings.Default.WORK_PROCEDURE_FORM_HEIGHT;
            int x = (int)(window.Left * 1.333) + 170;
            int y = (int)(window.Top * 1.333) + 158;
            Size = new Size(width, height);
            Location = new Point(x, y);
            Marshal.ReleaseComObject(window);

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkProcedureForm_Load(object sender, EventArgs e)
        {
            // コピー先のシートの拡大率を保持
            selectedSheetZoom = app.ActiveWindow.Zoom;

            XmlNode workflowNode = excelFormat.DocumentElement.SelectSingleNode("/root/workflow");
            string workflowSheetName = workflowNode.Attributes.GetNamedItem("sheet").Value;
            try
            {
                workflowSheet = app.ActiveWorkbook.Sheets[workflowSheetName];
            }
            catch (COMException)
            {
                throw new ImportException(string.Format(LocaleUtil.GetString("ExcelUtils.Validate.MissingMasterSheet"), workflowSheetName));
            }

            XmlNode workNameNode = excelFormat.DocumentElement.SelectSingleNode("/root/work/workName");
            string workNameCell = workNameNode.Attributes.GetNamedItem("cell").Value;

            // 現在の工程ラベルの設定
            string workName = app.ActiveSheet.Range[workNameCell].Value;
            int labelWidth = addWorkProcedureButton.Location.X - currentWorkLabel.Location.X;
            currentWorkLabel.Text += workName;
            currentWorkLabel.Size = new Size(labelWidth, currentWorkLabel.Size.Height);

            // 編集対象のシートの設定
            selectedSheet = app.ActiveWorkbook.Sheets[app.ActiveSheet.Name];

            // 編集対象のシートを一時シートへコピー
            selectedSheet.Copy(After: app.Worksheets[app.Worksheets.Count]);
            selectedSheet.Activate();

            // 一時シートの設定
            tempSheet = app.Worksheets[app.Worksheets.Count];
            tempSheet.Name = DateTime.Now.ToString("yyyyMMddHHmmss");
            tempSheet.Visible = Excel.XlSheetVisibility.xlSheetHidden;
            tempSheet.Change += TempSheet_SheetChange;
            isTempSheetChanged = false;

            // 工程名の設定（コンボボックス）
            XmlNode workNode = excelFormat.DocumentElement.SelectSingleNode("/root/work");
            string workNamePattern = workNode.Attributes.GetNamedItem("sheet").Value;
            List<string> workNames = new List<string>();
            foreach (Excel.Worksheet sheet in app.ActiveWorkbook.Sheets)
            {
                if (Regex.IsMatch(sheet.Name, workNamePattern))
                {
                    string sheetName = sheet.Range[workNameCell].Value;
                    workNames.Add(sheetName != null ? sheetName : "");
                    sheetNames.Add(workNames.Count - 1, sheet.Name);
                }
            }
            Delegate originalHandler = LogUtils.GetEventHandler(workComboBox, workComboBox.GetType().GetEvent("SelectedIndexChanged"));
            workComboBox.SelectedIndexChanged -= (EventHandler)originalHandler;
            try
            {
                workComboBox.Items.AddRange(workNames.ToArray());
                workComboBox.SelectedIndex = workComboBox.FindStringExact(selectedSheet.Range[workNameCell].Value);
            }
            finally
            {
                workComboBox.SelectedIndexChanged += (EventHandler)originalHandler;
            }

            // 作業手順名の取得
            string[] workProcedureNames = wps.GetWorkProcedureNames(selectedSheet).ToArray();
            selectedSheetIndexCount = workProcedureNames.Length;

            // 作業手順名の設定（コンボボックス）
            workProcedureComboBox.Items.Add(LocaleUtil.GetString("key.emptyWorkProcedure"));
            workProcedureComboBox.Items.AddRange(workProcedureNames);
            workProcedureComboBox.SelectedIndex = 0;

            // 作業手順名の設定（リストボックス）
            workProcedureListBox.Items.AddRange(workProcedureNames);
            workProcedureListBox.Items.Add(LocaleUtil.GetString("key.addToEnd"));
            workProcedureListBox.SelectedIndex = 0;

            workNo = workNames.IndexOf(app.ActiveSheet.Range[workNameCell].Value) + 1;
            workProcedureNo = wps.GetWorkProcedureNo(selectedSheet);
        }

        /// <summary>
        /// コンボボックスのインデックス変更時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            // 作業手順名をクリア（コンボボックス）
            workProcedureComboBox.Items.Clear();

            // 作業手順名の設定（コンボボックス）
            string sheetName = sheetNames[workComboBox.SelectedIndex];
            string[] workProcedureNames = wps.GetWorkProcedureNames(app.Sheets[sheetName]).ToArray();
            workProcedureComboBox.Items.Add(LocaleUtil.GetString("key.emptyWorkProcedure"));
            workProcedureComboBox.Items.AddRange(workProcedureNames);
            workProcedureComboBox.SelectedIndex = 0;
        }

        /// <summary>
        /// 一時シート変更時の処理
        /// </summary>
        /// <param name="Target">変更範囲</param>
        private void TempSheet_SheetChange(Excel.Range Target)
        {
            isTempSheetChanged = true;
        }

        /// <summary>
        /// リストボックスのインデックス変更時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkProcedureListBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            int index = workProcedureListBox.SelectedIndex;
            int lastIndex = workProcedureListBox.Items.Count - 1;

            // インデックスが末尾（末尾に追加）を選択している場合、
            // 作成以外のボタンを無効化
            bool isSelectedLastIndex = (index == lastIndex);
            deleteWorkProcedureButton.Enabled = !isSelectedLastIndex;
            downWorkProcedureButton.Enabled = !isSelectedLastIndex;
            upWorkProcedureButton.Enabled = !isSelectedLastIndex;
        }

        /// <summary>
        /// 作成ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void AddWorkProcedureButton_Click(object sender, EventArgs e)
        {
            int listBoxIndex = workProcedureListBox.SelectedIndex;
            int comboBoxIndex = workProcedureComboBox.SelectedIndex;
            string baseName = (comboBoxIndex == 0) ? LocaleUtil.GetString("key.workProcedure") + "{0:D}" : workProcedureComboBox.Items[comboBoxIndex] + " ({0:D})";
            string procedureName = CreateNewName(baseName);

            if (comboBoxIndex == 0)
            {
                try
                {
                    wps.AddWorkProcedure(tempSheet, listBoxIndex, procedureName);
                }
                catch (Exception ex)
                {
                    logger.Error(ex);
                    return;
                }
            }
            else
            {
                Excel.Worksheet targetSheet = app.Sheets[sheetNames[workComboBox.SelectedIndex]];
                targetSheet.Activate();

                // 一時的にコピー先のシートの拡大率に合わせる
                double targetSheetZoom = app.ActiveWindow.Zoom;
                app.ActiveWindow.Zoom = selectedSheetZoom;
                
                // コピー実行
                wps.CopyWorkProcedure(targetSheet, tempSheet, comboBoxIndex - 1, listBoxIndex, procedureName);

                // 拡大率を戻し、コピー先シートをアクティブにする
                app.ActiveWindow.Zoom = targetSheetZoom;
                selectedSheet.Activate();
            }
            workProcedureListBox.Items.Insert(listBoxIndex, procedureName);
            workProcedureListBox.SelectedIndex = listBoxIndex + 1;

            workProcedureNo.Insert(listBoxIndex, null);
        }

        /// <summary>
        /// 新しい名前（連番）を生成
        /// </summary>
        /// <param name="baseName">基本となる名前</param>
        /// <returns>作業手順名</returns>
        private string CreateNewName(string baseName)
        {
            int cnt = 1;
            string newName;
            do
            {
                newName = string.Format(baseName, cnt++);
            } while (workProcedureListBox.Items.Contains(newName));
            return newName;
        }

        /// <summary>
        /// 削除ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DeleteWorkProcedureButton_Click(object sender, EventArgs e)
        {
            int index = workProcedureListBox.SelectedIndex;
            int lastIndex = workProcedureListBox.Items.Count - 1;
            if (index == lastIndex)
            {
                return;
            }

            int? workNo = null;
            CellIterator workParameterCells = new CellIterator(null, workParameterNode);
            for (; ; workParameterCells.Next(workflowSheet))
            {
                WorkParameter workParameter = GetWorkParameter(workParameterNode, workflowSheet, workParameterCells);
                if (workParameter == null)
                {
                    break;
                }

                if (workParameter.workNo.value != null)
                {
                    workNo = workParameter.workNo.value;
                }
                int? workProcedureNo = workParameter.workProcedureNo.value;

                if (workNo != null && workNo == this.workNo &&
                    workProcedureNo != null && workProcedureNo == this.workProcedureNo[index])
                {
                    MessageBox.Show(
                        LocaleUtil.GetString("WorkProcedureForm.cannotDelete"),
                        LocaleUtil.GetString("WorkProcedureForms"),
                        MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }
            }

            wps.DeleteWorkProcedure(tempSheet, index);
            workProcedureListBox.Items.RemoveAt(index);
            workProcedureListBox.SelectedIndex = index;

            workProcedureNo.RemoveAt(index);
        }

        /// <summary>
        /// 下矢印ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DownWorkProcedureButton_Click(object sender, EventArgs e)
        {
            int index = workProcedureListBox.SelectedIndex;
            int lastIndex = workProcedureListBox.Items.Count - 1;
            if (index == lastIndex || index == lastIndex - 1)
            {
                return;
            }

            wps.MoveWorkProcedure(tempSheet, index, index + 2);
            string item = (string)workProcedureListBox.Items[index];
            workProcedureListBox.Items.RemoveAt(index);
            workProcedureListBox.Items.Insert(index + 1, item);
            workProcedureListBox.SelectedIndex = index + 1;

            int? no = workProcedureNo[index];
            workProcedureNo.RemoveAt(index);
            workProcedureNo.Insert(index + 1, no);
        }

        /// <summary>
        /// 上矢印ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void UpWorkProcedureButton_Click(object sender, EventArgs e)
        {
            int index = workProcedureListBox.SelectedIndex;
            int lastIndex = workProcedureListBox.Items.Count - 1;
            if (index == 0 || index == lastIndex)
            {
                return;
            }

            wps.MoveWorkProcedure(tempSheet, index, index - 1);
            string item = (string)workProcedureListBox.Items[index];
            workProcedureListBox.Items.RemoveAt(index);
            workProcedureListBox.Items.Insert(index - 1, item);
            workProcedureListBox.SelectedIndex = index - 1;

            int? no = workProcedureNo[index];
            workProcedureNo.RemoveAt(index);
            workProcedureNo.Insert(index - 1, no);
        }

        /// <summary>
        /// OKボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OkButton_Click(object sender, EventArgs e)
        {
            if (isTempSheetChanged)
            {
                // 作業手順NOに連番を割り当てる
                XmlNode workProcedureNode = excelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure");
                CellIterator workProcedureCells = new CellIterator(null, workProcedureNode);
                for (int i = 1; i <= workProcedureNo.Count; i++)
                {
                    string cell = ExcelUtils.GetCell("workProcedureNo", workProcedureNode, workProcedureCells.GetCell()).ToString();
                    tempSheet.Range[cell].Value = i;
                    workProcedureCells.Next(tempSheet);
                }

                // 変更された作業手順NOをマスターシートへ反映する
                int? workNo = null;
                CellIterator workParameterCells = new CellIterator(null, workParameterNode);
                for (; ; workParameterCells.Next(workflowSheet))
                {
                    WorkParameter workParameter = GetWorkParameter(workParameterNode, workflowSheet, workParameterCells);
                    if (workParameter == null)
                    {
                        break;
                    }

                    if (workParameter.workNo.value != null)
                    {
                        workNo = workParameter.workNo.value;
                    }

                    if (workNo != null && workNo == this.workNo &&
                        workParameter.workProcedureNo.value != null)
                    {
                        string cell = ExcelUtils.GetCell("workProcedureNo", workParameterNode, workParameterCells.GetCell()).ToString();
                        workflowSheet.Range[cell].Value = workProcedureNo.IndexOf(workParameter.workProcedureNo.value) + 1;
                    }
                }

                // 作業手順の置換（一時シート → アクティブシート）
                int srcLastIndex = workProcedureListBox.Items.Count - 2;
                wps.ReplaceWorkProcedure(tempSheet, selectedSheet, srcLastIndex, selectedSheetIndexCount - 1);
            }

            // 編集対象のシートに反映されたため変更フラグをfalseに変更
            isTempSheetChanged = false;
            Close();
        }

        /// <summary>
        /// キャンセルボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CancelButton_Click(object sender, EventArgs e)
        {
            Close();
        }

        /// <summary>
        /// フォームのサイズが変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkProcedureForm_Resize(object sender, EventArgs e)
        {
            int labelWidth = addWorkProcedureButton.Location.X - currentWorkLabel.Location.X;
            currentWorkLabel.Size = new Size(labelWidth, currentWorkLabel.Size.Height);
        }

        /// <summary>
        /// フォームが閉じる前の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkProcedureForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (isTempSheetChanged)
            {
                DialogResult result = MessageBox.Show(
                    LocaleUtil.GetString("key.confirmDiscardEdited"),
                    LocaleUtil.GetString("key.confirm"), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                if (result == DialogResult.No)
                {
                    e.Cancel = true;
                }
            }
        }

        /// <summary>
        /// フォームが閉じた後の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkProcedureForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            // サイズを保持
            Properties.Settings.Default.WORK_PROCEDURE_FORM_WIDTH = Size.Width;
            Properties.Settings.Default.WORK_PROCEDURE_FORM_HEIGHT = Size.Height;

            // イベントハンドラーの解除
            tempSheet.Change -= TempSheet_SheetChange;

            // 一時シートの削除
            app.DisplayAlerts = false;
            tempSheet.Delete();
            app.DisplayAlerts = true;

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(tempSheet);
            Marshal.ReleaseComObject(selectedSheet);
        }

        /// <summary>
        /// 作業パラメータの取得
        /// </summary>
        /// <param name="workParameterNode">作業パラメータノード</param>
        /// <param name="workflowSheet">マスターシート</param>
        /// <param name="workParameterCells">作業パラメータセル</param>
        /// <returns>作業パラメータ</returns>
        private WorkParameter GetWorkParameter(XmlNode workParameterNode, Excel.Worksheet workflowSheet, CellIterator workParameterCells)
        {
            WorkParameter workParameter = new WorkParameter
            {
                itemNumber = ExcelUtils.GetCellStringNonBlank("itemNumber", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                workNo = ExcelUtils.GetCellInt("workNo", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                taktTime = ExcelUtils.GetCellStringNonBlank("taktTime", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                workProcedureNo = ExcelUtils.GetCellInt("workProcedureNo", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                fileName = ExcelUtils.GetCellStringNonBlank("fileName", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                checkItemNo = ExcelUtils.GetCellInt("checkItemNo", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                key = ExcelUtils.GetCellStringNonBlank("key", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                value = ExcelUtils.GetCellStringNonBlank("value", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                minValue = ExcelUtils.GetCellDouble("minValue", workParameterNode, workflowSheet, workParameterCells.GetCell()),
                maxValue = ExcelUtils.GetCellDouble("maxValue", workParameterNode, workflowSheet, workParameterCells.GetCell())
            };
            if (workParameter.itemNumber.value == null &&
                !workParameter.workNo.value.HasValue &&
                workParameter.taktTime.value == null &&
                !workParameter.workProcedureNo.value.HasValue &&
                workParameter.fileName.value == null &&
                !workParameter.checkItemNo.value.HasValue &&
                workParameter.key.value == null &&
                workParameter.value.value == null &&
                !workParameter.minValue.value.HasValue &&
                !workParameter.maxValue.value.HasValue)
            {
                return null;
            }
            return workParameter;
        }
    }
}
