using ExcelImport;
using System;
using System.IO;
using System.Reflection;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class VersionInfomationForm : Form
    {
        private readonly Assembly assembly;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public VersionInfomationForm()
        {
            InitializeComponent();

            //assembly = Assembly.LoadFrom(
                // Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "adWorkbookAddIn.dll")); // デバッグ用 TODO: Delete
            //    Path.Combine(Globals.ThisAddIn.AdFactoryHome, "adWorkbook", "bin", "adWorkbookAddIn.dll"));

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void VersionInfomationForm_Load(object sender, EventArgs e)
        {
            productNameLabel.Text = LocaleUtil.GetString("key.productName");
            //Version version = assembly.GetName().Version;
            //versionLabel.Text = $"Ver.{version.Major}.{version.Minor}.{version.Build}";

            String iniFilePath  = Path.Combine(Globals.ThisAddIn.AdFactoryHome, "adWorkbook", "version.ini");
            versionLabel.Text = $"Ver.{IniFile.ReadIniValue("Version", "Ver", "", iniFilePath)}";
        }

        /// <summary>
        /// OKボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OkButton_Click(object sender, EventArgs e)
        {
            Close();
        }
    }
}
