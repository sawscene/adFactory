using DashboardCustomizeTool.CommonClass;
using System;
using System.Collections.Generic;
using DashboardCustomizeTool.Data;

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
        /// adMonitor レイアウト設定ファイルを読み込む。
        /// </summary>
        /// <param name="dashboardNo">1 または 2</param>
        /// <param name="fullPath"></param>
        /// <param name="iniList"></param>
        /// <returns></returns>
        public List<LayoutData> DashboardIniRead(int dashboardNo, string fullPath, List<LayoutData> iniList)
        {
            if (dashboardNo == 2)
            {
                return this.Dashboard2IniRead(fullPath, iniList);
            }
            else
            {
                return this.DashboardIniRead(fullPath, iniList);
            }
        }

        /// <summary>
        /// Dashboard.ini読み込みメソッド
        /// </summary>
        private List<DashboardCustomizeTool.LayoutData> DashboardIniRead(string fullPath, List<DashboardCustomizeTool.LayoutData> iniList)
        {
            // iniファイル読み込み
            // ファイルを指定して初期化
            IniFile2 ini = new IniFile2(fullPath);
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
                }
                else
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
        /// Dashboard2.ini読み込みメソッド
        /// </summary>
        private List<DashboardCustomizeTool.LayoutData> Dashboard2IniRead(string fullPath, List<DashboardCustomizeTool.LayoutData> iniList)
        {
            // iniファイル読み込み
            // ファイルを指定して初期化
            IniFile2 ini = new IniFile2(fullPath);
            for (int i = 0; i < iniList.Count; i++)
            {
                DashboardCustomizeTool.LayoutData Data = iniList[i];

                if (string.IsNullOrEmpty(ini[Data.name.ToString(), IniManager.DISP]))
                {
                    Data.disp = LAYOUT_NOTDISPLAY;
                    Data.top = ini[Data.plugin.ToString(), IniManager.TOP];
                    Data.left = ini[Data.plugin.ToString(), IniManager.LEFT];
                    Data.bottom = ini[Data.plugin.ToString(), IniManager.BOTTOM];
                    Data.right = ini[Data.plugin.ToString(), IniManager.RIGHT];
                }
                else
                {
                    Data.disp = ini[Data.plugin.ToString(), IniManager.DISP];
                    Data.top = ini[Data.plugin.ToString(), IniManager.TOP];
                    Data.left = ini[Data.plugin.ToString(), IniManager.LEFT];
                    Data.bottom = ini[Data.plugin.ToString(), IniManager.BOTTOM];
                    Data.right = ini[Data.plugin.ToString(), IniManager.RIGHT];
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
        public IniFile2 inilocal(string fullPath)
        {
            //ファイルを指定して初期化
            IniFile2 ini = new IniFile2(fullPath);
            return ini;
        }

        /// <summary>
        /// iniファイル書込みメソッド
        /// </summary>
        public void IniWrite(string session,string key,string info, string fullPath)
        {
            // ファイルを指定して初期化
            IniFile2 ini = new IniFile2(fullPath);
            string value = string.Empty;
            value = ini[session, key] = info.ToString();
            ini.Write();
            Console.WriteLine(value);
        }

        /// <summary>
        /// iniファイル書込みメソッド
        /// </summary>
        public void IniWriteLayout(LayoutData data, string fullPath)
        {
            // ファイルを指定して初期化
            IniFile2 ini = new IniFile2(fullPath);
            string value = string.Empty;
            if (data.disp == LAYOUT_DISPLAY)
            {
                value = ini[data.plugin, IniManager.DISP] = data.disp;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.TOP] = data.top;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.LEFT] = data.left;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.BOTTOM] = data.bottom;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.RIGHT] = data.right;
                Console.WriteLine(value);
            }
            else
            {
                value = ini[data.plugin, IniManager.DISP] = data.disp;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.TOP] = null;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.LEFT] = null;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.BOTTOM] = null;
                Console.WriteLine(value);
                value = ini[data.plugin, IniManager.RIGHT] = null;
                Console.WriteLine(value);
            }
            ini.Write();
        }
    }
}