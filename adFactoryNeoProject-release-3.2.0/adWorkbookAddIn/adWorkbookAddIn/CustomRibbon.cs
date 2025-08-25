using Microsoft.Office.Tools.Ribbon;
using System.Windows.Forms;
using System.Net.Http;
using Excel = Microsoft.Office.Interop.Excel;
using adWorkbookAddIn.Source;
using System.Xml;
using ExcelImport;
using System;
using NLog;
using System.Text.RegularExpressions;

namespace adWorkbookAddIn
{
    public partial class CustomRibbon
    {
        private static readonly Logger logger = LogManager.GetCurrentClassLogger();

        /// <summary>
        /// ログインボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void LoginButton_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                ShowLoginForm();
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// 作業手順ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OperateWorkProcedure_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                Excel.Worksheet sheet = Globals.ThisAddIn.Application.ActiveSheet;
                XmlNode workNode = Globals.ThisAddIn.ExcelFormat.DocumentElement.SelectSingleNode("/root/work");
                string workSheetNamePattern = workNode.Attributes.GetNamedItem("sheet").Value;
                if (Regex.Matches(sheet.Name, workSheetNamePattern).Count > 0)
                {
                    WorkProcedureForm form = new WorkProcedureForm();
                    form.ShowDialog();
                }
                else
                {
                    MessageBox.Show(
                        LocaleUtil.GetString("WorkProcedureForm.notWorkSheet"),
                        LocaleUtil.GetString("WorkProcedureForm.workProcedureLabel"));
                }
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// 作図支援ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DrawingSupportButton_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                Globals.ThisAddIn.ShowDrawingSupportControl();
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// アップロードボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void UploadButton_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                try
                {
                    if (!IsLoggedIn())
                    {
                        if (!ShowLoginForm())
                        {
                            return;
                        }
                    }
                }
                catch (Exception ex)
                {
                    logger.Error(ex);
                    MessageBox.Show(
                        LocaleUtil.GetString("key.connectionFailed"),
                        LocaleUtil.GetString("key.error"),
                        MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                XmlDocument excelFormat = Globals.ThisAddIn.ExcelFormat;

                var excelApplication = Globals.ThisAddIn.Application;
                Excel.Workbook workbook = excelApplication.ActiveWorkbook;

                bool done = false;
                UploadProgressForm progress = new UploadProgressForm(false);

                progress.Action = () => {
                    try
                    {
                        ExcelImport.ExcelImport.DoImport(progress, excelFormat, workbook, false);
                    }
                    catch (ImportException ex)
                    {
                        logger.Error(ex);
                        ShowCell(workbook, ex);

                        MessageBox.Show(
                            LocaleUtil.GetString("key.importFailed") + "\r\n\r\n" + ex.Message,
                            LocaleUtil.GetString("key.import"),
                            MessageBoxButtons.OK, MessageBoxIcon.Warning);
                        return;
                    }
                    catch (CancelledException)
                    {
                        return;
                    }
                    catch (Exception ex)
                    {
                        logger.Error(ex);

                        MessageBox.Show(
                            LocaleUtil.GetString("key.importFailed") + "\r\n\r\n" + ex.Message,
                            LocaleUtil.GetString("key.import"),
                            MessageBoxButtons.OK, MessageBoxIcon.Warning);
                        return;
                    }
                    done = true;
                };

                progress.ShowDialog();

                if (done)
                {
                    MessageBox.Show(
                        LocaleUtil.GetString("key.importCompleted"),
                        LocaleUtil.GetString("key.import"),
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// データチェックボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DataCheckButton_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                XmlDocument excelFormat = Globals.ThisAddIn.ExcelFormat;

                var excelApplication = Globals.ThisAddIn.Application;
                Excel.Workbook workbook = excelApplication.ActiveWorkbook;

                bool done = false;
                UploadProgressForm progress = new UploadProgressForm(true);
                progress.Action = () => {
                    try
                    {
                        ExcelImport.ExcelImport.DoImport(progress, excelFormat, workbook, true);
                    }
                    catch (ImportException ex)
                    {
                        logger.Error(ex);
                        ShowCell(workbook, ex);
                        MessageBox.Show(
                            LocaleUtil.GetString("key.dataCheckFailed") + "\r\n\r\n" + ex.Message,
                            LocaleUtil.GetString("key.dataCheck"),
                            MessageBoxButtons.OK, MessageBoxIcon.Warning);
                        return;
                    }
                    catch (CancelledException)
                    {
                        return;
                    }
                    catch (Exception ex)
                    {
                        logger.Error(ex);

                        if ("System.Net.Http".Equals(ex.Source))
                        {
                            // サーバーとの通信中にエラーが発生しました。
                            MessageBox.Show(
                                LocaleUtil.GetString("CommunicationError"),
                                LocaleUtil.GetString("key.dataCheck"),
                                MessageBoxButtons.OK, MessageBoxIcon.Error);
                            return;
                        }

                        MessageBox.Show(
                            LocaleUtil.GetString("key.dataCheckFailed"),
                            LocaleUtil.GetString("key.dataCheck"),
                            MessageBoxButtons.OK, MessageBoxIcon.Warning);
                        return;
                    }
                    done = true;
                };

                progress.ShowDialog();

                if (done)
                {
                    MessageBox.Show(
                        LocaleUtil.GetString("key.dataCheckCompleted"),
                        LocaleUtil.GetString("key.dataCheck"),
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// バージョン情報ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void AboutButton_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                VersionInfomationForm form = new VersionInfomationForm();
                form.ShowDialog();
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// ヘルプボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ManualButton_Click(object sender, RibbonControlEventArgs e)
        {
            logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click start.");
            try
            {
                Globals.ThisAddIn.ShowHelpViewer();
            }
            catch (Exception ex)
            {
                logger.Error(ex);
            }
            finally
            {
                logger.Info($"{((RibbonButton)sender).Name} \"{((RibbonButton)sender).Label}\" Click end.");
            }
        }

        /// <summary>
        /// ログイン状態を確認する
        /// </summary>
        /// <returns>現在はログインしている場合はtrue</returns>
        private static bool IsLoggedIn()
        {
            if (!Globals.ThisAddIn.LoginOrganizationId.HasValue)
            {
                return false;
            }
            try
            {
                using (HttpClient client = AdFactoryClient.NewHttpClient())
                {
                    long? organizationId = ExcelImport.AdFactoryClient.FindUserIdByName(client, Globals.ThisAddIn.LoginOrganizationLoginId);
                    if (!organizationId.HasValue || organizationId.Value != Globals.ThisAddIn.LoginOrganizationId.Value)
                    {
                        return false;
                    }
                }
            }
            catch (Exception ex)
            {
                logger.Error(ex);
                throw ex;
            }
            return true;
        }

        /// <summary>
        /// ログイン・ダイアログを表示する
        /// </summary>
        /// <returns>ログイン成功の場合はtrue</returns>
        private static bool ShowLoginForm()
        {
            LoginForm form = new LoginForm();
            try
            {
                return form.ShowDialog() == DialogResult.OK;
            }
            finally
            {
                form.Dispose();
            }
        }

        /// <summary>
        /// データチェックでエラーになったセルを選択
        /// </summary>
        /// <param name="workbook">ワークブック</param>
        /// <param name="target">エラー情報</param>
        private void ShowCell(Excel.Workbook workbook, ImportException target)
        {
            if (target.SheetName == null || target.Cell == null)
            {
                return;
            }

            Excel.Worksheet sheet = workbook.Sheets[target.SheetName];
            sheet.Activate();
            Excel.Range cell = sheet.Cells[target.Cell.row, target.Cell.column];
            cell.Select();

            try
            {
                // 条件付き書式によりセルの色を変える
                sheet.Calculate();
            }
            catch (Exception e)
            {
                logger.Error(e);
            }
        }

        /// <summary>
        /// ボタンの有効／無効の切り替え
        /// </summary>
        /// <param name="status">true:表示、false:非表示</param>
        public void ToggleButtonEnabled(bool status)
        {
            loginButton.Enabled = status;
            uploadButton.Enabled = status;
            operateWorkProcedure.Enabled = status;
            drawingSupportButton.Enabled = status;
            dataCheckButton.Enabled = status;
            aboutButton.Enabled = status;
            manualButton.Enabled = status;
        }

        /// <summary>
        /// ローカライズされた文字列を適用する
        /// </summary>
        public void ApplyLocaleToControl()
        {
            LocaleUtil.ApplyLocaleToRibbon(this);
        }
    }
}
