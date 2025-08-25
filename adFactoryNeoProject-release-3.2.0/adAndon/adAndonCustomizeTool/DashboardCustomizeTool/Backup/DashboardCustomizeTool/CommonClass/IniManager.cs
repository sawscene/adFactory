using System.Runtime.InteropServices;
using System.Text;
using System;
using System.Data;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using Infragistics.Win.UltraWinDock;
using System.IO;


namespace DashboardCustomizeTool
{
    /// <summary>
    /// iniファイルクラス
    /// </summary>
    class IniManager
    {

        // メンバー変数の定義
        private const string DISP = "Disp";
        private const string TOP = "Top";
        private const string LEFT = "Left";
        private const string BOTTOM = "Bottom";
        private const string RIGHT = "Right";
        private const string LAYOUT_DISPLAY = "1";
        private const string LAYOUT_NOTDISPLAY = "0";
     
        /// <summary>
        /// Dashboard.ini読み込みメソッド
        /// </summary>
        public List<DashboardCustomizeTool.LayoutData> DashboardIniRead(string fullPath, List<DashboardCustomizeTool.LayoutData> iniList)
        {
            
            //iniファイル読み込み
            //ファイルを指定して初期化
            IniFile ini = new IniFile(fullPath);
            for (int i = 0; i < iniList.Count; i++)
            {
                  DashboardCustomizeTool.LayoutData Data = iniList[i];

                  if (string.IsNullOrEmpty(ini[Data.name.ToString(), IniManager.DISP]))
                  {
                      Data.disp = LAYOUT_NOTDISPLAY;
                      Data.top = ini[Data.name.ToString(), IniManager.TOP];
                      Data.left = ini[Data.name.ToString(), IniManager.LEFT];
                      Data.bottom = ini[Data.name.ToString(), IniManager.BOTTOM];
                      Data.right = ini[Data.name.ToString(), IniManager.RIGHT];

                  }else
                  {
                      Data.disp = ini[Data.name.ToString(), IniManager.DISP];
                      Data.top = ini[Data.name.ToString(), IniManager.TOP];
                      Data.left = ini[Data.name.ToString(), IniManager.LEFT];
                      Data.bottom = ini[Data.name.ToString(), IniManager.BOTTOM];
                      Data.right = ini[Data.name.ToString(), IniManager.RIGHT];
                  }
                  iniList[i] = Data;            
            }  
            return iniList;
        }

        /// <summary>
        /// iniファイルの読み込みメソッド
        /// </summary>
        /// <param name="fullPath"></param>
        /// <returns></returns>
        public IniFile inilocal(string fullPath)
        {
             //ファイルを指定して初期化
            IniFile ini = new IniFile(fullPath);
            return ini;
        }

        /// <summary>
        /// iniファイル書込みメソッド
        /// </summary>
        public void IniWrite(string session,string key,string info, string fullPath)
        {
            //ファイルを指定して初期化
            IniFile ini = new IniFile(fullPath);
            string value = string.Empty;
            value = ini[session, key] = info.ToString();
             Console.WriteLine(value);

            }
   
        /// <summary>
        /// iniファイル書込みメソッド
        /// </summary>
        public void IniWriteLayout(LayoutData data, string fullPath)
        {
           
            //ファイルを指定して初期化
            IniFile ini = new IniFile(fullPath);
            string value = string.Empty;
            if (data.disp == LAYOUT_DISPLAY)
            {
                value = ini[data.name, IniManager.DISP] = data.disp;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.TOP] = data.top;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.LEFT] =data.left;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.BOTTOM] = data.bottom;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.RIGHT] = data.right;
                Console.WriteLine(value);
            }
            else
            {
                value = ini[data.name, IniManager.DISP] = data.disp;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.TOP] = null;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.LEFT] = null;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.BOTTOM] = null;
                Console.WriteLine(value);
                value = ini[data.name, IniManager.RIGHT] = null;
                Console.WriteLine(value);
            }
        }
    }
    /// <summary>
    /// INIファイルを読み書きするクラス
    /// </summary>
    public class IniFile
    {
        [DllImport("kernel32.dll")]
        private static extern int GetPrivateProfileString(
            string lpApplicationName,
            string lpKeyName,
            string lpDefault,
            StringBuilder lpReturnedstring,
            int nSize,
            string lpFileName);

        [DllImport("kernel32.dll")]
        private static extern int WritePrivateProfileString(
            string lpApplicationName,
            string lpKeyName,
            string lpstring,
            string lpFileName);

        [DllImport("kernel32.dll")]
        static extern int GetPrivateProfileSectionNames(
            IntPtr lpszReturnBuffer,
            uint nSize,
            string lpFileName);

        string filePath;

        /// <summary>
        /// ファイル名を指定して初期化します。
        /// ファイルが存在しない場合は初回書き込み時に作成されます。
        /// </summary>
        public IniFile(string filePath)
        {
            this.filePath = filePath;
        }

        /// <summary>
        /// sectionとkeyからiniファイルの設定値を取得、設定します。 
        /// </summary>
        /// <returns>指定したsectionとkeyの組合せが無い場合は""が返ります。</returns>
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