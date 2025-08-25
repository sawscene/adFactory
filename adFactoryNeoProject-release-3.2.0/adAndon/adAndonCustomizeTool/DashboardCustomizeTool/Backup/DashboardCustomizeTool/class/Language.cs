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
    /// <summary>
    /// 初期処理
    /// </summary>
    class Language
    {
        private const int MAX_LENGTH = 260; //StringBuilderのMAX値
        private const int INITIAL_LENGTH = 0; //StringBuilder初期値
        private const int SELECT_LENGTH = 2; //文字数

        [DllImport("user32.dll")]
        public static extern IntPtr GetForegroundWindow();

        //表示言語処理
        //SetDisplayLanguage呼び出し
        /// <summary>
        /// サテライトDLL取得
        /// </summary>
        /// <returns></returns>
        public void SetDisplayLanguage()
        {
            Settings appsettings = new Settings();
            string lingual = string.Empty;

            //exeがあるか確認
            //フルパス情報の取得方法の検討が必要
            if (System.IO.File.Exists(appsettings.DashboardExeFullpath))
            {
                // TODO:言語対応は今回はしない。とりあえず日本語を固定で設定する
                MainForm.Language = Define.LANGUAGE_SELECT_JP;
            }
        }
    }
}
