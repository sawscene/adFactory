using System;
using System.IO;
using System.Text;
using System.Windows;
using LicensePublishTool.Utils;

namespace LicensePublishTool
{
    /// <summary>
    /// App.xaml の相互作用ロジック
    /// </summary>
    public partial class App : Application
    {
        private const string DisplayName = "License Publishing Tool";
        private const string LicenseFileName = "adFactory.license";
        
        /// <summary>
        /// 
        /// </summary>
        /// <param name="e"></param>
        protected override void OnStartup(StartupEventArgs e)
        {
            try
            {
                base.OnStartup(e);

                foreach (string srcPath in e.Args)
                {
                    string src;
                    using (StreamReader sr = new StreamReader(srcPath, Encoding.GetEncoding("Shift_JIS")))
                    {
                        src = sr.ReadToEnd();
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.Append('#'); 
                    sb.Append(DateTime.Now.ToString("s"));
                    sb.Append(Environment.NewLine);
                    sb.Append(src);

                    byte[] iv;

                    string dest = CryptoUtils.Encrypt(sb.ToString(), out iv);
                    string destPath = Path.Combine(Path.GetDirectoryName(srcPath), App.LicenseFileName);

                    using (StreamWriter sw = new StreamWriter(destPath, false, Encoding.GetEncoding("ISO-8859-1")))
                    {
                        sw.Write(Encoding.GetEncoding("ISO-8859-1").GetString(iv));
                        sw.Write(dest);
                    }

                    // 読み込みテスト
                    string test;

                    using (FileStream fs = new FileStream(destPath, FileMode.Open, FileAccess.Read))
                    {
                        byte[] testIV = new byte[16];
                        int readBytes = fs.Read(testIV, 0, 16);

                        byte[] buffer = new byte[fs.Length - readBytes];
                        fs.Read(buffer, 0, buffer.Length);

                        test = CryptoUtils.Decrypt(Encoding.GetEncoding("ISO-8859-1").GetString(buffer), testIV);
                    }

                    test = test.Substring(test.IndexOf(Environment.NewLine) + 2);
                    if (src != test)
                    {
                        throw new Exception("読み込みテストにてエラーが発生しました。");
                    }
                }

                if (e.Args.Length > 0)
                {
                    MessageBox.Show("ライセンスファイルを生成しました。", App.DisplayName, MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, App.DisplayName, MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                Current.Shutdown();
            }
        }
    }
}
