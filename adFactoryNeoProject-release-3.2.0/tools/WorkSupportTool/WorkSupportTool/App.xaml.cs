using System;
using System.Collections.Generic;
using System.IO;
using System.Windows;
using System.Windows.Threading;
using WorkSupportTool.Common;
using WorkSupportTool.Net;
using WorkSupportTool.Utils;

namespace WorkSupportTool
{
    /// <summary>
    /// App.xaml の相互作用ロジック
    /// </summary>
    public partial class App : Application
    {
        private int mode;

        /// <summary>
        /// アプリケーションの開始
        /// Debug用の起動引数
        /// -projectno=C:\adFactory\downloads\projectno.tsv -order=C:\adFactory\downloads\order.tsv
        /// </summary>
        /// <param name="e"></param>
        protected override void OnStartup(StartupEventArgs e)
        {
            try
            {
                Logging.Logger.Info("Starting the application.");
                Logging.StartMethod(typeof(App), "OnStartup");

                Application.Current.DispatcherUnhandledException += new DispatcherUnhandledExceptionEventHandler(DispatcherUnhandledException);

                base.OnStartup(e);

                if (e.Args.Length > 0)
                {
                    this.RunMasterDownload(e.Args);
                    Current.Shutdown();
                    return;
                }

                this.mode = ConfigManager.GetValueInt(Constants.Mode, 1);

                if (1 == mode)
                {
                    // 連携結果のダウンロード
                    this.RunDownload();
                    Current.Shutdown();
                }
                else
                {
                    // 作業データのアップロード
                    MainWindow window = new MainWindow();
                    window.Show();
                }
            }
            finally
            {
                Logging.EndMethod(typeof(App), "OnStartup");
            }
        }

        /// <summary>
        /// アプリケーションの終了
        /// </summary>
        /// <param name="e"></param>
        protected override void OnExit(ExitEventArgs e)
        {
            try
            {
                Logging.StartMethod(typeof(App), "OnExit");

                base.OnExit(e);
                
                Application.Current.DispatcherUnhandledException -= new DispatcherUnhandledExceptionEventHandler(DispatcherUnhandledException);
                
                Logging.Logger.Info("Exit the application.");
            }
            catch (Exception)
            {
                // ここでは、例外を無視する
            }
            finally
            {
                Logging.EndMethod(typeof(App), "OnExit");
            }
        }

        /// <summary>
        /// 例外処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DispatcherUnhandledException(object sender, DispatcherUnhandledExceptionEventArgs e)
        {
            try
            {
                Logging.StartMethod(typeof(App), "DispatcherUnhandledException");
                Logging.ExceptionOccurred(e.Exception);

                e.Handled = true;

                if (2 == mode)
                {
                    MessageBox.Show(e.Exception.Message, Constants.DisplayName, MessageBoxButton.OK, MessageBoxImage.Error);
                }

                Current.Shutdown();
            }
            catch (Exception)
            {
                // ここでは、例外を無視する
            }
            finally
            {
                Logging.EndMethod(typeof(App), "DispatcherUnhandledException");
            }
        }

        /// <summary>
        /// 連携結果のダウンロードを実行する
        /// </summary>
        private void RunDownload()
        {
            try
            {
                Logging.StartMethod(typeof(App), "RunDownload"); 

                string destPath = ConfigManager.GetValueString(Constants.Download, @"C:\Work");
                if (!Directory.Exists(destPath))
                {
                    Directory.CreateDirectory(destPath);
                }

                // 「エラー無し」ファイルを削除
                string statusFile = Path.Combine(destPath, Constants.StatusOK);
                File.Delete(statusFile);

                bool isStatusOK = true;
    
                FtpClient ftpClient = new FtpClient();
                FtpFileInfo[] list = ftpClient.GetFileList();
                
                if (list != null)
                {
                    foreach (FtpFileInfo file in list)
                    {
                        string ext = Path.GetExtension(file.Name);
                        if (file.IsDirectory || ext.CompareTo(Constants.TsvExtension) != 0)
                        {
                            continue;
                        }

                        isStatusOK = false;

                        if (!ftpClient.IsDownLoadTargetFileName(file.Name))
                        {
                            continue;
                        }

                        if (ftpClient.DownloadFile(file.Name, null, false) == false)
                        {
                            break;
                        }

                        if (ftpClient.DeleteFile(file.Name) == false)
                        {
                            break;
                        }
                    }
                }

                if (isStatusOK)
                {
                    using (FileStream fs = new FileStream(statusFile, FileMode.Create, FileAccess.Write))
                    {
                        StreamWriter sw = new StreamWriter(fs);
                        sw.Write(DateTime.Now);
                        sw.Flush();
                        sw.Close();
                    }
                }
            }
            finally
            {
                Logging.EndMethod(typeof(App), "RunDownload"); 
            }
        }

        /// <summary>
        /// マスターデータのダウンロードを実行する
        /// </summary>
        /// <param name="args"></param>
        private void RunMasterDownload(string[] args)
        {
            try
            {
                Logging.StartMethod(typeof(App), "RunMasterDownload");

                foreach (string arg in args)
                {
                    Logging.Logger.InfoFormat("Parameter: {0}", arg);
                }

                FtpClient ftpClient = new FtpClient();
                ftpClient.DownloadUri = ConfigManager.GetValueString(Constants.Master, String.Empty);
                FtpFileInfo[] list = ftpClient.GetFileList();

                if (list != null)
                {
                    foreach (FtpFileInfo file in list)
                    {
                        string ext = Path.GetExtension(file.Name);
                        if (file.IsDirectory || ext.CompareTo(Constants.TsvExtension) != 0)
                        {
                            continue;
                        }

                        string destPath = null;
                        foreach (string arg in args)
                        {
                            string prefix = arg.Substring(1, arg.IndexOf('=') - 1);
                            if (file.Name.StartsWith(prefix))
                            {
                                destPath = Path.GetFullPath(arg.Substring(arg.IndexOf('=') + 1));
                            }
                        }

                        if (String.IsNullOrEmpty(destPath))
                        {
                            continue;
                        }

                        string destDir = Path.GetDirectoryName(destPath);
                        if (!Directory.Exists(destDir))
                        {
                            Directory.CreateDirectory(destDir);
                        }

                        if (ftpClient.DownloadFile(file.Name, destPath, true) == false)
                        {
                            break;
                        }
                    }
                }

            }
            finally
            {
                Logging.EndMethod(typeof(App), "RunMasterDownload");
            }
        }
    }
}
