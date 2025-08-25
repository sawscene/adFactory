using ExcelImport;
using System;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class UploadProgressForm : Form
    {
        public Action Action { get; set; }
        private readonly string titleText;
        private int totalProgressMax;
        private int currentProgressMax;
        private bool isCancelled = false;
        private bool doClose = false;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="isDryRun">true:データチェックのみ、false:アップロード</param>
        public UploadProgressForm(bool isDryRun)
        {
            InitializeComponent();
            if (isDryRun)
            {
                Text = LocaleUtil.GetString("key.dataCheck");
                titleText = LocaleUtil.GetString("key.dataChecking");
            }
            else
            {
                Text = LocaleUtil.GetString("key.import");
                titleText = LocaleUtil.GetString("key.importing");
            }
        }

        /// <summary>
        /// 全体の進捗の総件数を設定
        /// </summary>
        /// <param name="value">全体の進捗の総件数</param>
        public void SetTotalProgressMax(int value)
        {
            totalProgressMax = value;
        }

        /// <summary>
        /// 全体の進捗の処理済の件数を設定
        /// </summary>
        /// <param name="value">全体の進捗の処理済の件数</param>
        public void SetTotalProgressValue(int value)
        {
            if (isCancelled)
            {
                throw new CancelledException();
            }

            titleLabel.Text = titleText + "(" + value + "/" + totalProgressMax + ")";

            currentProgressMax = 0;
            currentProgressBar.Value = 0;
            currentProgressLabel.Text = "";

            Application.DoEvents();
        }

        /// <summary>
        /// 工程の総件数を設定
        /// </summary>
        /// <param name="value">工程の総件数</param>
        public void SetCurrentProgressMax(int value)
        {
            currentProgressMax = value;
            currentProgressBar.Maximum = currentProgressMax;
        }

        /// <summary>
        /// 工程の処理済の件数を設定
        /// </summary>
        /// <param name="value">工程の処理済の件数</param>
        /// <param name="caption">表題</param>
        public void SetCurrentProgressValue(int value, string caption)
        {
            if (isCancelled)
            {
                throw new CancelledException();
            }

            currentProgressBar.Value = value;
            currentProgressLabel.Text = caption + "(" + value + "/" + currentProgressMax + ")";

            Application.DoEvents();
        }

        /// <summary>
        /// キャンセルボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CancelButton_Click(object sender, EventArgs e)
        {
            isCancelled = true;
            cancelButton.Enabled = false;
        }

        /// <summary>
        /// アップロードプログレスフォームの表示時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void UploadProgressForm_Shown(object sender, EventArgs e)
        {
            Action();
            doClose = true;
            Close();
        }

        /// <summary>
        /// アップロードプログレスフォームが閉じたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void UploadProgressForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (doClose)
            {
                return;
            }
            isCancelled = true;
            cancelButton.Enabled = false;
            e.Cancel = true;
        }
    }
}
