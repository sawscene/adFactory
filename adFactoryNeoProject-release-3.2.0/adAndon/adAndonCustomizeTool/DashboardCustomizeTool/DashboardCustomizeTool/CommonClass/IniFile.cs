using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

namespace DashboardCustomizeTool.CommonClass
{
    class IniFile_
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="lpApplicationName"></param>
        /// <param name="lpKeyName"></param>
        /// <param name="lpDefault"></param>
        /// <param name="lpReturnedstring"></param>
        /// <param name="nSize"></param>
        /// <param name="lpFileName"></param>
        /// <returns></returns>
        [DllImport("kernel32.dll")]
        private static extern int GetPrivateProfileString(
            string lpApplicationName,
            string lpKeyName,
            string lpDefault,
            StringBuilder lpReturnedstring,
            int nSize,
            string lpFileName);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="lpApplicationName"></param>
        /// <param name="lpKeyName"></param>
        /// <param name="lpstring"></param>
        /// <param name="lpFileName"></param>
        /// <returns></returns>
        [DllImport("kernel32.dll")]
        private static extern int WritePrivateProfileString(
            string lpApplicationName,
            string lpKeyName,
            string lpstring,
            string lpFileName);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="lpszReturnBuffer"></param>
        /// <param name="nSize"></param>
        /// <param name="lpFileName"></param>
        /// <returns></returns>
        [DllImport("kernel32.dll")]
        static extern int GetPrivateProfileSectionNames(
            IntPtr lpszReturnBuffer,
            uint nSize,
            string lpFileName);

        /// <summary>
        /// 
        /// </summary>
        private string filePath;

        /// <summary>
        /// ファイル名を指定して初期化する。
        /// ファイルが存在しない場合は初回書き込み時に作成される。
        /// </summary>
        public IniFile_(string filePath)
        {
            this.filePath = filePath;
        }

        /// <summary>
        /// sectionとkeyからiniファイルの設定値を取得、設定する。 
        /// </summary>
        /// <returns>指定したsectionとkeyの組合せが無い場合は""が返る。</returns>
        public string this[string section, string key]
        {
            set
            {
                WritePrivateProfileString(section, key, value, filePath);
            }
            get
            {
                StringBuilder sb = new StringBuilder(256);
                GetPrivateProfileString(section, key, string.Empty, sb, sb.Capacity, filePath);
                return sb.ToString();
            }
        }

        /// <summary>
        /// 全てのセッションの情報を取得する。
        /// </summary>
        /// <returns></returns>
        public string[] GetSection()
        {
            string[] stArrayData = new string[0];

            if (File.Exists(filePath))
            {
                IntPtr ptr = Marshal.StringToHGlobalAnsi(new String('\0', 1024));
                int length = GetPrivateProfileSectionNames(ptr, 1024, filePath);

                if (0 < length)
                {
                    String result = Marshal.PtrToStringAnsi(ptr, length);
                    stArrayData = result.Split('\0');
                }

                Marshal.FreeHGlobal(ptr);
            }
            return stArrayData;
        }
    }
}
