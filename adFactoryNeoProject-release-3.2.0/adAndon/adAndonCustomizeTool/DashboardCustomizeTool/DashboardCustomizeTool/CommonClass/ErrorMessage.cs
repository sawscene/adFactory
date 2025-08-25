using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace DashboardCustomizeTool.CommonClass
{
    /// <summary>
    /// エラーメッセージクラス
    /// </summary>
    class ErrorMessage
    {
        //エラーメッセージタイトル
        public const string MAINTITEL_NAME = "DashboardCustomizeTool";
        public const string MSGCODE_FORMAT = "{0:0000}";
        public const int LOG_LV_HIGHT = 1;
        public const int LOG_LV_LOWT = 0;
        //エラーコード

        //起動
        public const string ERROR_MSGCODE_0001 = "1"; //DLL不足
      
        //ログインエラー

        public const string ERROR_MSGCODE_0011 = "11";//ログオン失敗時

        //アプリケーション設定エラー
        public const string ERROR_MSGCODE_0021 = "21";　//アプリケーション設定ファイルが不在
        public const string ERROR_MSGCODE_0022 = "22";　//アプリケーション設定ファイルアイテムなし
        public const string ERROR_MSGCODE_0023 = "23";　//アプリケーション設定ファイルキー項目不正
        public const string ERROR_MSGCODE_0024 = "24";　//16進数変換エラー

        //レイアウト情報cfgファイルエラー
        public const string ERROR_MSGCODE_0031 = "31"; //cfgファイルがない場合
        public const string ERROR_MSGCODE_0032 = "32"; //解凍したファイルがない場合
    
        //レイアウト情報iniファイルエラー
        public const string ERROR_MSGCODE_0041 = "41"; //iniファイルがない場合
        public const string ERROR_MSGCODE_0042 = "42"; //iniファイル内キー項目不正

        //レイアウト情報xmlファイルエラー
        public const string ERROR_MSGCODE_0051 = "51"; //XMLロードエラー(レイアウトの追加方法次第で変更の可能性　有)

        //レイアウト情報ファイルの項目がアプリケーション設定と違う場合
        public const string MSGCODE_8041 = "8041";

        private const string ERROR_LOGCODE = ": 6199";

        /// <summary>
        /// エラーメッセージ作成
        /// </summary>
        /// <returns></returns>
        public void CreateErrorMessage(Exception e, bool logdisp, Form formname)
        {
            ProcessLogger.StartMethod();

            //エラーメッセージ取得
            string msgboxmessage = SetErrorMessage(e.Message);
            //トレースログ用メッセージ
            string errorlogmessage = string.Empty;
            //トレースログ用エラーコード
            string errorlogcode = string.Empty;
            ShowMsgBoxAPI mg = new ShowMsgBoxAPI();

            //エラーメッセージ表示
       
            int num = 0;
            if (int.TryParse(e.Message, out num))
            {

                //トレースログ用エラーメッセージ作成
                errorlogmessage = e.StackTrace + ERROR_LOGCODE + String.Format(MSGCODE_FORMAT, int.Parse(e.Message));
            }
            else
            {
                string message = e.StackTrace.Replace("\r", "").Replace("\n", "");
                errorlogmessage =message + ERROR_LOGCODE + String.Format(MSGCODE_FORMAT, int.Parse(ERROR_MSGCODE_0024));
            }

            //トレースログ書き出しLV(true：(出力レベル＝Hight) false：(出力レベル＝Low)
            if (logdisp)
            {
                mg.ShowMsgBox(formname, MAINTITEL_NAME, msgboxmessage, MessageBoxButtons.OK, MessageBoxIcon.Error);

                //トレースログ出力
                ProcessLogger.Logger.Info("トレースログ出力レベル:" + LOG_LV_HIGHT + " エラーメッセージ：" + errorlogmessage);
            }
            else
            {
                mg.ShowMsgBox(formname, MAINTITEL_NAME, msgboxmessage, MessageBoxButtons.OK, MessageBoxIcon.Warning);

                //トレースログ出力
                ProcessLogger.Logger.Info("トレースログ出力レベル:" + LOG_LV_LOWT + " エラーメッセージ：" + errorlogmessage);
            }

            ProcessLogger.EndMethod();
        }

        private string SetErrorMessage(string errorcode)
        {
            ProcessLogger.StartMethod();

            string message = string.Empty;
            MultipleLanguages languageSettings = MultipleLanguages.GetInstance();
 
            //指定したエラーコードの値でエラーメッセージ設定
            switch (errorcode)
            {
                case ERROR_MSGCODE_0001 :
                    //FujiFlexaDll不足エラー
                    message = languageSettings.ErrorMessageFujiFlexaDll;
                    break;
                case ERROR_MSGCODE_0021:
                    //アプリケーション設定ファイルエラー
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;
                case ERROR_MSGCODE_0022:
                    //アプリケーション設定ファイルエラー
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;
                case ERROR_MSGCODE_0023:
                    //アプリケーション設定ファイルエラー
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;

                case ERROR_MSGCODE_0031:
                    //レイアウト情報cfgファイルエラー
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                case ERROR_MSGCODE_0032:
                    //レイアウト情報cfgファイルエラー
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                case MSGCODE_8041:
                    //レイアウト情報ファイルの項目がアプリケーション設定と違う場合
                    message = languageSettings.ErrorMessageLayoutIniFileDifference;
                    break;
                case ERROR_MSGCODE_0041:
                    //デフォルトレイアウト情報iniファイルが存在しない為、保存処理が失敗
                    message = languageSettings.ErrorMessageSaveLayout;
                    break;
                
                case ERROR_MSGCODE_0042:
                    //デフォルトレイアウト情報iniファイルが存在しない為、保存処理が失敗
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                case ERROR_MSGCODE_0051:
                    //デフォルトレイアウト情報iniファイルが存在しない為、保存処理が失敗
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                default:
                    //マーカカラーが不正な場合のエラー
                    //アプリケーション設定ファイルエラー
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;
            }

            ProcessLogger.Logger.Info("メッセージ内容：" + message);

            ProcessLogger.EndMethod();

            return message;
        }

    }
    /// <summary>
    /// 例外クラス
    /// </summary>
    class MyException : Exception
    {
        /// <summary>
        /// トレースログ用の例外を発生させる。
        /// </summary>
        /// <param name="message">例外内容</param>
        public MyException(string message)
            : base(message)
        {
        }
    }
}