using System;
using System.Windows.Forms;
using System.IO;
using System.Drawing.Printing;
using System.Windows.Controls;

namespace adWorkbookAddIn.Source
{
    public partial class HelpViewer : System.Windows.Forms.UserControl
    {
        /// <summary>
        /// コンストラクタ
        /// </summary>
        public HelpViewer()
        {
            InitializeComponent();

            this.backButton.Text = "<<" + LocaleUtil.GetString("backButton");
            this.backButton.ToolTipText = LocaleUtil.GetString("backButton");
            this.forwardButton.Text = LocaleUtil.GetString("forwardButton") + ">>";
            this.forwardButton.ToolTipText = LocaleUtil.GetString("forwardButton");
            this.indexButton.Text = LocaleUtil.GetString("indexButton");
            this.indexButton.ToolTipText = LocaleUtil.GetString("indexButton");
            this.topButton.Text = LocaleUtil.GetString("topButton");
            this.topButton.ToolTipText = LocaleUtil.GetString("topButton");
            this.printButton.Text = LocaleUtil.GetString("printButton");
            this.printButton.ToolTipText = LocaleUtil.GetString("printButton");

            // ヘルプを開く
            String filePath = Path.Combine(Globals.ThisAddIn.AdFactoryHome, "adWorkbook", "help", "index.html");
            this.webBrowser.Navigate(filePath);

            this.webBrowser.CanGoBackChanged += this.webBrowser_CanGoBackChanged;
            this.webBrowser.CanGoForwardChanged += this.webBrowser_CanGoForwardChanged;
        }

        /// <summary>
        /// 印刷
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void printButton_Click(object sender, EventArgs e)
        {
            this.webBrowser.ShowPrintPreviewDialog();
        }

        /// <summary>
        /// 戻るボタンの更新
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void webBrowser_CanGoBackChanged(object sender, EventArgs e)
        {
            this.backButton.Enabled = this.webBrowser.CanGoBack;
        }

        /// <summary>
        /// 戻る
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void backButton_Click(object sender, EventArgs e)
        {
            if (this.webBrowser.CanGoBack)
            {
                this.webBrowser.GoBack();
            }
        }

        /// <summary>
        /// 進むボタンの更新
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void webBrowser_CanGoForwardChanged(object sender, EventArgs e)
        {
            this.forwardButton.Enabled = this.webBrowser.CanGoForward;
        }
        
        /// <summary>
        /// 進む
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void forwardButton_Click(object sender, EventArgs e)
        {
            if (this.webBrowser.CanGoForward)
            {
                this.webBrowser.GoForward();
            }
        }

        /// <summary>
        /// 目次
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void indexButton_Click(object sender, EventArgs e)
        {
            String filePath = Path.Combine(Globals.ThisAddIn.AdFactoryHome, "adWorkbook", "help", "index.html");
            this.webBrowser.Navigate(filePath);
        }

        /// <summary>
        /// ページ先頭
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void topButton_Click(object sender, EventArgs e)
        {
            if (this.webBrowser.Url != null && !String.IsNullOrEmpty(this.webBrowser.Url.AbsolutePath))
            {
                this.webBrowser.Navigate(this.webBrowser.Url.AbsolutePath);
            }
        }
    }
}
