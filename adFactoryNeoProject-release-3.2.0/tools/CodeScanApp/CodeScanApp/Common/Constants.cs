//********************************************************************
//  FileName : Constants.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System;
using System.Linq;
using System.Collections.Generic;
using System.Text;

namespace CodeScanApp.Common
{
    /// <summary>
    /// 定数
    /// </summary>
    static class Constants
    {
        /// <summary>
        /// 送信データフォーマット
        /// </summary>
        public const string SEND_FORMAT = "CodeScan;{0};{1};{2};\r";

        /// <summary>
        /// 設定ファイル名
        /// </summary>
        public const string APP_CONFIG = "CodeScanApp.ini";

        /// <summary>
        /// 標準OCR設定ファイル名
        /// </summary>
        public const string OCR_SETTING = "OCRSetting.ini";
        
        public const string APP = "APP";
        public const string CONNECT_TYPE = "CONNECT_TYPE";
        public const string WLAN_HOST = "WLAN_HOST";
        public const string HOST_NAME = "HOST_NAME";
        public const string HOST_ADDR = "HOST_ADDR";
        public const string WLAN_HISTORY = "WLAN_HISTORY";
        
        /// <summary>
        /// ポート番号
        /// </summary>
        public const int WLAN_PORT = 10007;

        /// <summary>
        /// 読取パターン設定ファイル名
        /// </summary>
        public const string PATTERN_CONFIG = "KeyenceScan.ini";

        public const string NAME = "NAME";
        public const string LIVE_VIEW = "LIVE_VIEW";
        public const string TARGET="TARGET";
        public const string FLD = "FLD";
        public const string INI = "_INI";
        public const string TAG = "_TAG";
        public const string LED = "_LED";
        public const string FIND = "_FIND";
        public const string ILL = "_ILL";
        public const string BRT1 = "_BRT1";
        public const string BRT2 = "_BRT2";
        public const string BRT3 = "_BRT3";
        public const string BRT4 = "_BRT4";
        public const string DIST = "_DIST";
        public const string LOW = "_LOW";
        public const string ILL3D = "_ILL3D";
        public const string MARKER = "_MARKER";
        public const string CAMERA = "_CAMERA";
        public const string PID = "_PID";

        public const string ALERT_NOT_FOUND_CONFIG_FILE = "{0}が[\\FlashDisk\\HandyScanApp]に存在しません。";
        public const string ALERT_FAILED_READ_FILE = "何らかの原因により、{0}を読み込みできまんでした。";
    }
}
