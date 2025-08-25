using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.IO;
using DashboardCustomizeTool.CommonClass;
using log4net;

namespace DashboardCustomizeTool
{
    static class Program
    {
        private const string DASHBOAR_NAME = "adAndonApp";
        private const string WINDIR_NAME = "windir";
        private const string TOOL_NAME = "DashboardCustomizeTool";

        static public string EXE_PATH = @"\Bin\";

        //DLL名
        private const string DOCKINGAPI　=@"\Dockingcontrollayout.dll";
        private const string ZIPDLL = @"\Ionic.Zip.dll";

        // 起動引数
        private const string FILE_PATH_OPTION = "-f";

        /// <summary>
        /// アプリケーションのメイン エントリ ポイントです。
        /// </summary>
        [STAThread]
        static void Main(string[] args)
        {
            ProcessLogger.Logger = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

            ProcessLogger.StartApplication();

            string cfgFilePath = string.Empty;// cfgファイルのパス

            if (args.Length > 0)
            {
                ProcessLogger.Logger.InfoFormat("main args={0}", args);

                // 起動引数に"-f"があれば、その次の引数がcfgファイルのパス。
                // cfgファイルのパスが指定されている場合、conf/adAndonCustomizeToolLayoutInfo.cfgではなく、指定されたファイルを開く。
                int fileOptionId = Array.IndexOf(args, FILE_PATH_OPTION);
                int filePathId = fileOptionId + 1;
                if (fileOptionId >= 0 && args.Length > filePathId)
                {
                    cfgFilePath = args[filePathId];
                    if (!File.Exists(cfgFilePath))
                    {
                        // 存在しないパスの場合は無視する。
                        cfgFilePath = string.Empty;
                    }
                }
            }

            // 設定ファイルを読み込む
            ConfigIni configIni = ConfigIni.GetInstance();
            configIni.LoadFile();

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            //表示言語クラスの呼び出し
            MultipleLanguages languageSettings = MultipleLanguages.GetInstance();

            //メッセージボックス表示元のフォーム
            Form formbase = new Form();
            formbase.StartPosition = FormStartPosition.CenterScreen;
            formbase.TopMost = true;
            //起動処理関係
            Language language = new Language();
            ShowMsgBoxAPI mg = new ShowMsgBoxAPI();
            DashboardCustomizeTool.CommonClass.ErrorMessage errormsessagr = new DashboardCustomizeTool.CommonClass.ErrorMessage();

            Settings appsettings = Settings.GetInstance();
         
            string maintitel = string.Empty;

            try
            {
                // adFactory側のインストールパス（\Bin\）のフォルダ確認
                if (Directory.Exists(appsettings.AdFactoryHome + EXE_PATH))
                {
                    // フォルダがあるならDLLパスを指定
                    Environment.CurrentDirectory = appsettings.AdFactoryHome + EXE_PATH;
                    string dockingControlLayoutPath = Environment.CurrentDirectory + DOCKINGAPI;
                    string zipdllpath = EXE_PATH + ZIPDLL;

                    // 多言語対応
                    // 設定に言語の指定があれば指定の言語で、ない場合はシステムの言語。
                    string lang = configIni.GetConfigData.Language;
                    if (string.IsNullOrEmpty(lang))
                    {
                        string cultureName = language.SetDisplayLanguage();
                        configIni.GetConfigData.Language = cultureName;
                    }
                    else
                    {
                        if (lang.Equals("ja-JP"))
                        {
                            MainForm.Language = Define.LANGUAGE_SELECT_JP;
                        }
                        else
                        {
                            MainForm.Language = Define.LANGUAGE_SELECT_US;
                        }
                    }
                    // 言語を取得したら言語を設定する
                    languageSettings.setLanguageText();

                    // ドッキングレイアウトDLLの有無を確認
                    if (File.Exists(dockingControlLayoutPath) == true && File.Exists(dockingControlLayoutPath) == true)
                    {
                        // あるなら次の処理へ
                    }
                    else
                    {
                        // エラーメッセージを表示して終了
                        // エラーメッセージ表示
                        string message = languageSettings.ErrorMessageFujiFlexaDll;
                        mg.ShowMsgBox(formbase, TOOL_NAME, message, MessageBoxButtons.OK, MessageBoxIcon.Error);
                        ProcessLogger.Logger.Error("環境構築が不十分。インストールフォルダにBINフォルダがない。");
                        return;
                    }
                }
                else
                {
                    // adFactory環境が構築されていない為、起動不可
                     throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0001);
                }

                //二重起動をチェックする
                if (System.Diagnostics.Process.GetProcessesByName(
                    System.Diagnostics.Process.GetCurrentProcess().ProcessName).Length > 1)
                {
                    //多重起動時は処理終了
                    string message = languageSettings.ErrorMessageDouble;
                    mg.ShowMsgBox(formbase, TOOL_NAME, message, MessageBoxButtons.OK, MessageBoxIcon.Error);
                    ProcessLogger.Logger.Error("多重起動エラー");
                    return;
                }

                // 起動時にconfigフォルダにフレームリストファイルがあるか確認する。
                if (!File.Exists(appsettings.ItemListFilePath))
                {
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0042);
                }

                // ダッシュボードの起動確認
                if (System.Diagnostics.Process.GetProcessesByName(DASHBOAR_NAME).Length > 0)
                {
                    //Processオブジェクトを作成する
                    System.Diagnostics.Process[] p = System.Diagnostics.Process.GetProcessesByName(DASHBOAR_NAME);

                    //メッセージ表示
                    maintitel = languageSettings.MessageDashboardEnd;
                    DialogResult sele = mg.ShowMsgBox(formbase, TOOL_NAME, maintitel, MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                    switch (sele)
                    {
                        case DialogResult.Yes:
                            //プロセスの応答を確認
                            if (p[0].Responding)
                            {
                                //応答があるなら終了処理
                                if(!(p[0].CloseMainWindow()))
                                {
                                    p[0].Kill();  // 終了しなかった場合は強制終了する
                                }
                            }
                            else
                            {
                                //応答なしなら強制終了
                                p[0].Kill();                         
                            }
                            break;
                        default:
                            return;
                    }
                }
            }
            catch (Exception e)
            {
                errormsessagr.CreateErrorMessage(e, true, formbase);
                ProcessLogger.ExceptionOccurred(e);
                return;
            }
      
            //初期化処理完了後起動
            Application.Run(new MainForm(cfgFilePath));

            ProcessLogger.EndApplication();
        }
    }
}

