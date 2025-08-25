using DashboardCustomizeTool.CommonClass;
using System;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// 設備識別子名で保存ダイアログ
    /// </summary>
    public partial class SaveAsDialog : Form
    {
        /// <summary>
        /// メッセージ
        /// </summary>
        private MultipleLanguages languageSettings = MultipleLanguages.GetInstance();

        /// <summary>
        /// アプリケーション設定
        /// </summary>
        private Settings appSettings = Settings.GetInstance();

        /// <summary>
        /// 設備識別子名
        /// </summary>
        public string EquipmentIdentName { get; set; }

        /// <summary>
        /// adMonitor 設定ファイルパス
        /// </summary>
        public string DashboardFilePath { get; set; }

        /// <summary>
        /// レイアウトツール 設定ファイルパス
        /// </summary>
        public string LayoutInfoFilePath { get; set; }

        /// <summary>
        /// 設備識別子名で保存ダイアログ
        /// </summary>
        public SaveAsDialog()
        {
            InitializeComponent();
        }

        /// <summary>
        /// Load イベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveAsDialog_Load(object sender, EventArgs e)
        {
            // テキストを設定する。
            this.Text = languageSettings.SaveAsDialogTitle;// 設備識別子名で保存
            this.lblEquipmentIdentName.Text = languageSettings.EquipmentIdentNameText;// 設備識別名
            this.btnSave.Text = languageSettings.SaveButtonText;// 保存
            this.btnCancel.Text = languageSettings.CancelButtonText;// キャンセル

            // ダイアログの幅を変更する。
            int width = this.tableLayoutPanel.Size.Width;
            int height = this.ClientSize.Height;
            this.ClientSize = new Size(width, height);
        }

        /// <summary>
        /// 保存ボタン Clickイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnSave_Click(object sender, EventArgs e)
        {
            string identName = txtEquipmentIdentName.Text;

            // adMonitor レイアウト設定ファイルパス
            StringBuilder sb1 = new StringBuilder();
            sb1.Append(Path.GetFileNameWithoutExtension(appSettings.Dashboard2IniFilePath));
            sb1.Append("_");
            sb1.Append(identName);
            sb1.Append(Path.GetExtension(appSettings.Dashboard2IniFilePath));
            string dashboardPath = Path.Combine(Path.GetDirectoryName(appSettings.Dashboard2IniFilePath), sb1.ToString());

            // レイアウトツール 設定ファイルパス
            StringBuilder sb2 = new StringBuilder();
            sb2.Append(Path.GetFileNameWithoutExtension(appSettings.LayoutCfgFilePath));
            sb2.Append("_");
            sb2.Append(identName);
            sb2.Append(Path.GetExtension(appSettings.LayoutCfgFilePath));
            string layoutInfoPath = Path.Combine(Path.GetDirectoryName(appSettings.LayoutCfgFilePath), sb2.ToString());

            // 設定ファイルが既に存在する場合は上書き確認を行う。
            if (File.Exists(dashboardPath) || File.Exists(layoutInfoPath))
            {
                ShowMsgBoxAPI mg = new ShowMsgBoxAPI();
                DialogResult result = mg.ShowMsgBox(this, this.Text, languageSettings.MessageSaveAsExsistFile, MessageBoxButtons.YesNo, MessageBoxIcon.Exclamation);
                if (DialogResult.No.Equals(result))
                {
                    return;
                }
            }

            // 結果をセットして閉じる。
            this.EquipmentIdentName = identName;
            this.DashboardFilePath = dashboardPath;
            this.LayoutInfoFilePath = layoutInfoPath;
            this.DialogResult = DialogResult.OK;
            this.Close();
        }

        /// <summary>
        /// キャンセルボタン Clickイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
