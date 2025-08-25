using System.IO;
using System.Windows;
using Microsoft.Win32;
using WorkSupportTool.Common;
using WorkSupportTool.Net;
using WorkSupportTool.Utils;

namespace WorkSupportTool
{
    /// <summary>
    /// MainWindow.xaml の相互作用ロジック
    /// </summary>
    public partial class MainWindow : Window
    {
        /// <summary>
        /// コンストラクタ
        /// </summary>
        public MainWindow()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 閉じる
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonClose_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Logging.StartMethod(typeof(MainWindow), "buttonClose_Click");
            
                Close();
            }
            finally
            {
                Logging.EndMethod(typeof(MainWindow), "buttonClose_Click");
            }
        }

        /// <summary>
        /// アップロード
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonUpload_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Logging.StartMethod(typeof(MainWindow), "buttonUpload_Click");

                string filePath = this.textBoxFileName.Text;

                if (!File.Exists(filePath))
                {
                    MessageBox.Show(this, "ファイルが見つかりません。", Constants.DisplayName, MessageBoxButton.OK, MessageBoxImage.Error);
                    return;
                }

                FtpClient ftpClient = new FtpClient();
                if (ftpClient.UploadFile(filePath, true))
                {
                    MessageBox.Show(this, "ファイルをアップロードしました。", Constants.DisplayName, MessageBoxButton.OK, MessageBoxImage.Information);
                }
            }
            finally
            {
                Logging.EndMethod(typeof(MainWindow), "buttonUpload_Click");
            }

        }

        /// <summary>
        /// ファイル選択
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonChoice_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Logging.StartMethod(typeof(MainWindow), "buttonChoice_Click");

                OpenFileDialog dialog = new OpenFileDialog();
                dialog.Title = "ファイルを選択";
                dialog.Filter = "修正済みのファイル(*.tsv)|*.tsv";
                dialog.InitialDirectory = ConfigManager.GetValueString(Constants.Download, null);
                if (dialog.ShowDialog() == true)
                {
                    this.textBoxFileName.Text = dialog.FileName;
                }
            }
            finally
            {
                Logging.EndMethod(typeof(MainWindow), "buttonChoice_Click");
            }

        }
    }
}
