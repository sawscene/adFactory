using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.IO;
using System.Windows.Forms.ComponentModel.Com2Interop;
using System.Globalization;
using System.Threading;
using System.Resources;

using System.Reflection;
using DashboardCustomizeTool.CommonClass;

namespace DashboardCustomizeTool
{
	class Language
	{
        private const int MAX_LENGTH = 260; //StringBuilderのMAX値
        private const int INITIAL_LENGTH = 0; //StringBuilder初期値
        private const int SELECT_LENGTH = 2; //文字数

        [DllImport("user32.dll")]
        public static extern IntPtr GetForegroundWindow();

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, EntryPoint = "GetSystemDefaultLCID")]
        private static extern int GetSystemDefaultLCID();

        //表示言語処理
        //SetDisplayLanguage呼び出し
        /// <summary>
        /// サテライトDLL取得
        /// </summary>
        /// <returns></returns>
        public string SetDisplayLanguage()
        {
            string cultureName = string.Empty;
            Settings appsettings = Settings.GetInstance();

            //exeがあるか確認
            //フルパス情報の取得方法の検討が必要
            if (System.IO.File.Exists(appsettings.AppExeFilePath))
            {
                // システムロケールを取得
                //      ※.地域と言語の「管理」タブの「システムローケールの変更(C)...」の設定を取得
                int lcid = GetSystemDefaultLCID();
                CultureInfo culture = new CultureInfo(lcid);

                cultureName = culture.Name;

                if (cultureName.Equals("ja-JP"))
                {
                    MainForm.Language = Define.LANGUAGE_SELECT_JP;
                }
                else
                {
                    MainForm.Language = Define.LANGUAGE_SELECT_US;
                }
            }
            return cultureName;
        }
    }
}
