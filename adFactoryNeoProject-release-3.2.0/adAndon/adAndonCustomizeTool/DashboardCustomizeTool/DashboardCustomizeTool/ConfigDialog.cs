using DashboardCustomizeTool.CommonClass;
using System;
using System.Drawing;
using System.Windows.Forms;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// 設定ダイアログ
    /// </summary>
    public partial class ConfigDialog : Form
    {
        private MultipleLanguages languageSettings = MultipleLanguages.GetInstance();

        /// <summary>
        /// レイアウトエリアの幅 (W)
        /// </summary>
        private int layoutAreaWidth;

        /// <summary>
        /// レイアウトエリアの高さ (H)
        /// </summary>
        private int layoutAreaHeight;

        /// <summary>
        /// レイアウトエリアの幅 (W)
        /// </summary>
        public int LayoutAreaWidth
        {
            get
            {
                return this.layoutAreaWidth;
            }
            set
            {
                int newValue = value;
                if (value < this.numWidth.Minimum)
                {
                    newValue = Decimal.ToInt32(this.numWidth.Minimum);
                }
                else if (value > this.numWidth.Maximum)
                {
                    newValue = Decimal.ToInt32(this.numWidth.Maximum);
                }
                this.layoutAreaWidth = newValue;
                this.numWidth.Value = newValue;
            }
        }

        /// <summary>
        /// レイアウトエリアの高さ (H)
        /// </summary>
        public int LayoutAreaHeight
        {
            get
            {
                return this.layoutAreaHeight;
            }
            set
            {
                int newValue = value;
                if (value < this.numHeight.Minimum)
                {
                    newValue = Decimal.ToInt32(this.numHeight.Minimum);
                }
                else if (value > this.numHeight.Maximum)
                {
                    newValue = Decimal.ToInt32(this.numHeight.Maximum);
                }
                this.layoutAreaHeight = newValue;
                this.numHeight.Value = newValue;
            }
        }

        /// <summary>
        /// 設定ダイアログ
        /// </summary>
        public ConfigDialog()
        {
            InitializeComponent();
        }

        /// <summary>
        /// Load イベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ConfigDialog_Load(object sender, EventArgs e)
        {
            // テキストを設定する。
            this.Text = languageSettings.ConfigDialogTitle;// 設定
            this.lblMonitorSize.Text = languageSettings.LayoutAreaSizeText;// 全体サイズ
            this.lblWidth.Text = languageSettings.LayoutAreaWidthText;// W
            this.lblHeight.Text = languageSettings.LayoutAreaHeightText;// H
            this.btnOk.Text = languageSettings.OkButtonText;// OK
            this.btnCancel.Text = languageSettings.CancelButtonText;// キャンセル

            // ダイアログの幅を変更する。
            int width = this.tableLayoutPanel.Size.Width;
            int height = this.ClientSize.Height;
            this.ClientSize = new Size(width, height);
        }

        /// <summary>
        /// 全体サイズ (W) Enterイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void numWidth_Enter(object sender, EventArgs e)
        {
            // テキストを全て選択状態にする。
            this.numWidth.Select(0, this.numWidth.Text.Length);
        }

        /// <summary>
        /// 全体サイズ (H) Enterイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void numHeight_Enter(object sender, EventArgs e)
        {
            // テキストを全て選択状態にする。
            this.numHeight.Select(0, this.numHeight.Text.Length);
        }

        /// <summary>
        /// OKボタン Clickイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnOk_Click(object sender, EventArgs e)
        {
            // 結果をセットして閉じる。
            this.layoutAreaWidth = Decimal.ToInt32(this.numWidth.Value);
            this.layoutAreaHeight = Decimal.ToInt32(this.numHeight.Value);
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
