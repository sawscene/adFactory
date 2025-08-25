//********************************************************************
//  FileName : Config.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.IO;
using Bt.FileLib;
using Bt;


namespace CodeScanApp.Common
{
    /// <summary>
    /// 設定
    /// </summary>
    class Config
    {
        /// <summary>
        /// カレントディレクトリのパス
        /// </summary>
        private static string _path = Path.Combine("FlashDisk", "CodeScanApp");

        /// <summary>
        /// 標準OCR設定ファイル
        /// </summary>
        public static string DefaultOCRSetting { get; set; }

        /// <summary>
        /// 読取パターン設定一覧
        /// </summary>
        private static List<Pattern> _patterns = new List<Pattern>();
        public static List<Pattern> Patterns
        {
            get { return _patterns; }
        }
        
        /// <summary>
        /// 接続タイプ 0: 無線LAN、1: Bluetooth
        /// </summary>
        public static int ConnectType { get; set; }

        /// <summary>
        /// Bluetooth接続先
        /// </summary>
        private static Bt.LibDef.BT_BLUETOOTH_TARGET _bluetoothHost = new Bt.LibDef.BT_BLUETOOTH_TARGET();
        public static Bt.LibDef.BT_BLUETOOTH_TARGET BluetoothHost
        {
            get { return _bluetoothHost; }
            set { _bluetoothHost = value; }
        }
        
        /// <summary>
        /// 無線LAN接続先
        /// </summary>
        private static String _wlanHost = "";
        public static String WLANHost 
        {
            get { return _wlanHost; }
            set { _wlanHost = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        private static List<string> _wlanHistory = new List<string>();
        public static List<string> WLANHistory
        {
            get { return _wlanHistory; }
        }

        /// <summary>
        /// INIファイルから設定値を取得する。
        /// </summary>
        /// <param name="filePath">ファイルパス</param>
        /// <param name="section">セクション名</param>
        /// <param name="key">キー名</param>
        /// <param name="def">デフォルト値</param>
        /// <returns>成功時：設定値、失敗時：デフォルト値<</returns>
        private static string ReadIniValue(StringBuilder filePath, StringBuilder section, StringBuilder key, StringBuilder def)
        {
			Byte[] buff = new Byte[LibDef.BT_INI_VALUE_MAXLEN];	// 取得値
            Int32 ret = Bt.FileLib.Ini.btIniReadString(section, key, def, buff, LibDef.BT_INI_VALUE_MAXLEN, filePath);
            return Encoding.Unicode.GetString(buff, 0, (ret * 2));
        }

        /// <summary>
        /// INIファイルから設定値を取得する。
        /// </summary>
        /// <param name="filePath">ファイルパス</param>
        /// <param name="section">セクション名</param>
        /// <param name="key">キー名</param>
        /// <param name="def">デフォルト値</param>
        /// <returns>成功時：設定値、失敗時：デフォルト値</returns>
        private static int ReadIniValue(StringBuilder filePath, StringBuilder section, StringBuilder key, int def)
        {
			Byte[] buff = new Byte[LibDef.BT_INI_VALUE_MAXLEN];	// 取得値
            return Bt.FileLib.Ini.btIniReadInt(section, key, def, filePath);
        }
        
        /// <summary>
        /// INIファイルから設定値を書き込む。
        /// </summary>
        /// <param name="filePath">ファイルパス</param>
        /// <param name="section">セクション名</param>
        /// <param name="key">キー名</param>
        /// <param name="value">設定値</param>
        /// <returns></returns>
        private static Int32 WriteIniValue(StringBuilder filePath, StringBuilder section, StringBuilder key, StringBuilder value)
        {
		    Int32 ret = Bt.FileLib.Ini.btIniWriteString(section, key, value, filePath);
            if (Bt.LibDef.BT_OK != ret)
            {
                Utils.PopupAlert("btIniWriteString", ret);
            }
            return ret;
        }
       
        /// <summary>
        /// 設定ファイルから設定値を読み込む。
        /// </summary>
        /// <returns>エラーメッセージを返す</returns>
        public static string LoadConfig()
        {
            HandyScanner hs = new HandyScanner();
            StringBuilder def = new StringBuilder("");

            // アプリケーション設定ファイルの読み込み
            StringBuilder appFilePath = new StringBuilder(Path.Combine(_path, Constants.APP_CONFIG));
            
            DefaultOCRSetting = Path.Combine(_path, Constants.OCR_SETTING);

            if (File.Exists(appFilePath.ToString()))
            {
                StringBuilder section = new StringBuilder(Constants.APP);
                ConnectType = ReadIniValue(appFilePath, section, new StringBuilder(Constants.CONNECT_TYPE), 0);
                _wlanHost = ReadIniValue(appFilePath, section, new StringBuilder(Constants.WLAN_HOST), def);

                _bluetoothHost.name = ReadIniValue(appFilePath, section, new StringBuilder(Constants.HOST_NAME), def);
                _bluetoothHost.addr = ReadIniValue(appFilePath, section, new StringBuilder(Constants.HOST_ADDR), def);

                String value = ReadIniValue(appFilePath, section, new StringBuilder(Constants.WLAN_HISTORY), def);
                if (!String.IsNullOrEmpty(value))
                {
                    foreach (String host in value.Split(','))
                    {
                        _wlanHistory.Add(host);
                    }
                }
            }

            // 読取パターン設定ファイルの読み込み
            StringBuilder patternFilePath = new StringBuilder(Path.Combine(_path, Constants.PATTERN_CONFIG));
            StringBuilder nameKey = new StringBuilder(Constants.NAME);
            char[] separators = new char[] { '[', ']' };

            if (!File.Exists(patternFilePath.ToString()))
            {
                // 読取パターン設定ファイルが存在しない。
                return String.Format(Constants.ALERT_NOT_FOUND_CONFIG_FILE, Constants.PATTERN_CONFIG);
            }
            
            for (int i = 0; i < 100; i++)
            {
                StringBuilder section = new StringBuilder(Convert.ToString(i + 1));
                
                string target = ReadIniValue(patternFilePath, section, new StringBuilder(Constants.TARGET), def);
                if (!String.IsNullOrEmpty(target) && hs.HandyType != target) {
                    continue;
                }

                string patternName = ReadIniValue(patternFilePath, section, nameKey, def);
                if (String.IsNullOrEmpty(patternName)) {
                    continue;
                }
                
                int index = patternName.Trim().IndexOf(' ');
                if (index == -1) {
                    continue;
                }

                string modelName = patternName.Substring(0, index);
                string workName = patternName.Substring(index + 1);

                Pattern pattern = new Pattern(i + 1, patternName, modelName, workName);

                pattern.LiveView = ReadIniValue(patternFilePath, section, new StringBuilder(Constants.LIVE_VIEW), 1);
                if (!(pattern.LiveView == 0 || pattern.LiveView == 1))
                {
                    pattern.LiveView = 1;
                }

                for (int n = 0; n < 10; n++)
                {
                    // 読取項目名
                    StringBuilder itemKey = new StringBuilder(Constants.FLD + Convert.ToString(n + 1));
                    string name = ReadIniValue(patternFilePath, section, itemKey, def);
                    if (String.IsNullOrEmpty(name)) {
                        continue;
                    }

                    ScanItem item = new ScanItem();
                    item.Name = name;
                    
                    // OCR設定ファイル名
                    StringBuilder iniKey = new StringBuilder(itemKey  + Constants.INI);
                    string iniFile = ReadIniValue(patternFilePath, section, iniKey, def);
                    if (!String.IsNullOrEmpty(iniFile))
                    {
                        item.IniFile = Path.Combine(_path, iniFile);
                        if (!File.Exists(item.IniFile))
                        {
                            // OCR設定ファイルが存在しない。
                            return String.Format(Constants.ALERT_NOT_FOUND_CONFIG_FILE, iniFile);
                        }
                    }

                    // タグ
                    StringBuilder tagKey = new StringBuilder(itemKey  + Constants.TAG);
                    string tag = ReadIniValue(patternFilePath, section, tagKey, def);
                    if (!String.IsNullOrEmpty(tag)) {
                        item.Tag = tag;
                    }

                    // LED照明
                    StringBuilder ledKey = new StringBuilder(itemKey  + Constants.LED);
                    item.LED = ReadIniValue(patternFilePath, section, ledKey, 0);
                    if (item.LED < 0 || item.LED > 1)
                    {
                        item.LED = 0;
                    }

                    // シリアル番号の切り出し情報
                    StringBuilder findKey = new StringBuilder(itemKey  + Constants.FIND);
                    string findInfo = ReadIniValue(patternFilePath, section, findKey, def);
                    if (!String.IsNullOrEmpty(tag)) {
                        string[] values = findInfo.Split(separators);
                        if (values.Length >= 8)
                        {
                            item.Delimiter = values[1].Trim();
                            item.Section = int.Parse(values[3]);
                            item.Frist = int.Parse(values[5]);
                            item.Length = int.Parse(values[7]);
                        }
                    }

                    if (hs.HandyType == HandyScanner.HT_BT_W370) {
                        item.Illuminaton = (byte)(ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.ILL), 7)) & 0x0F;
                        if (item.Illuminaton == 0)
                        {
                            item.Illuminaton = 7;
                        }

                        item.Brightness[0] = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.BRT1), 128);
                        if (item.Brightness[0] < 0 || item.Brightness[0] > 255)
                        {
                            item.Brightness[0] = 128;
                        }
                        
                        item.Brightness[1] = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.BRT2), 128);
                        if (item.Brightness[1] < 0 || item.Brightness[1] > 255)
                        {
                            item.Brightness[1] = 128;
                        }
                        
                        item.Brightness[2] = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.BRT3), 180);
                        if (item.Brightness[2] < 0 || item.Brightness[2] > 255)
                        {
                            item.Brightness[2] = 180;
                        }
                        
                        item.Brightness[3] = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.BRT4), 80);
                        if (item.Brightness[3] < 0 || item.Brightness[3] > 255)
                        {
                            item.Brightness[3] = 80;
                        }

                        item.DistanceLED = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.DIST), 1);
                        if (item.DistanceLED < 0 || item.DistanceLED > 1)
                        {
                            item.DistanceLED = 1;
                        }

                        item.LowDirection = (byte)(ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.LOW), 8)) & 0x0F;
                        if (item.LowDirection == 0)
                        {
                            item.LowDirection = 8;
                        }
                        
                        item.Illuminaton3D = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.ILL3D), 1);
                        if (item.DistanceLED < 1 || item.DistanceLED > 3)
                        {
                            item.DistanceLED = 1;
                        }

                        item.Marker = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey + Constants.MARKER), 1);
                        if (item.Marker < 0 || item.Marker > 1)
                        {
                            item.Marker = 1;
                        }
                        
                        item.CameraLED = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey  + Constants.CAMERA), 0);
                        if (item.CameraLED < 0 || item.CameraLED > 1)
                        {
                            item.CameraLED = 0;
                        }
                    }

                    // パーツIDかどうか
                    item.IsPartsID = ReadIniValue(patternFilePath, section, new StringBuilder(itemKey  + Constants.PID), 0) == 1;
                    
                    pattern.Items.Add(item);
                }

                _patterns.Add(pattern);
            }

            return string.Empty;
        }

        /// <summary>
        /// 設定値を設定ファイルに保存する。
        /// </summary>
        /// <returns></returns>
        public static bool SaveConfig()
        {
            Int32 ret = 0;
            StringBuilder def = new StringBuilder("");

            // アプリケーション設定ファイルの書き込み
            StringBuilder appFilePath = new StringBuilder(Path.Combine(_path, Constants.APP_CONFIG));
            StringBuilder section = new StringBuilder(Constants.APP);

            ret = WriteIniValue(appFilePath, section, new StringBuilder(Constants.CONNECT_TYPE), new StringBuilder(ConnectType.ToString()));
            if (Bt.LibDef.BT_OK != ret)
            {
                return false;
            }
            
            ret = WriteIniValue(appFilePath, section, new StringBuilder(Constants.WLAN_HOST), new StringBuilder(_wlanHost));
            if (Bt.LibDef.BT_OK != ret)
            {
                return false;
            }
            
            ret = WriteIniValue(appFilePath, section, new StringBuilder(Constants.HOST_NAME), new StringBuilder(_bluetoothHost.name));
            if (Bt.LibDef.BT_OK != ret)
            {
                return false;
            }

            ret = WriteIniValue(appFilePath, section, new StringBuilder(Constants.HOST_ADDR), new StringBuilder(_bluetoothHost.addr));
            if (Bt.LibDef.BT_OK != ret)
            {
                return false;
            }

            if (_wlanHistory.Count > 0)
            {
                ret = WriteIniValue(appFilePath, section, new StringBuilder(Constants.WLAN_HISTORY), new StringBuilder(String.Join(",", _wlanHistory.ToArray())));
                if (Bt.LibDef.BT_OK != ret)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
