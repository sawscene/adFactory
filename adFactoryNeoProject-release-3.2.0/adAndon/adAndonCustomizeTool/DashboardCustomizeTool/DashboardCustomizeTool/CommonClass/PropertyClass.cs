using DashboardCustomizeTool.CommonClass;
using System;
using System.IO;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// アプリケーション設定クラス
    /// </summary>
    public class Settings
    {
        private const string WINDIR_NAME = "windir";

        /// <summary>プログラムフォルダ名</summary>
        private const string BIN_FOLDER = "bin";
        /// <summary>設定フォルダ名</summary>
        private const string CONF_FOLDER = "conf";
        /// <summary>テンポラリフォルダ名</summary>
        private const string TEMP_FOLDER = "temp";

        /// <summary>アプリケーション ファイル名</summary>
        private const string APP_EXE = "DashboardCustomizeTool.exe";
        /// <summary>アプリケーション設定 ファイル名</summary>
        private const string APP_INI = "DashboardCustomizeTool.ini";

        /// <summary>プラグインリスト ファイル名</summary>
        private const string ITEM_LIST_FILE = "adAndonCustomizeToolItemList.ini";

        /// <summary>インポート ファイル名</summary>
        private const string LAYOUT_CFG_FILE = "adAndonCustomizeToolLayoutInfo.cfg";
        /// <summary>レイアウト情報 ファイル名</summary>
        public const string LAYOUT_XML_FILE = "adAndonCustomizeToolLayoutInfo.xml";
        /// <summary>レイアウト情報 テンポラリファイル名</summary>
        private const string LAYOUT_TMP_FILE = "adAndonCustomizeToolLayoutInfo.tmp";

        /// <summary>adMonitor レイアウト情報 ファイル名 (旧ver)</summary>
        private const string DASHBOARD_INI = "Dashboard.ini";
        /// <summary>adMonitor レイアウト情報 バックアップファイル名 (旧ver)</summary>
        public const string DASHBOARD_BACKUP_INI = "DashboardBackup.ini";

        /// <summary>adMonitor レイアウト情報 ファイル名</summary>
        private const string DASHBOARD2_INI = "Dashboard2.ini";
        /// <summary>adMonitor レイアウト情報 バックアップファイル名</summary>
        public const string DASHBOARD2_BACKUP_INI = "Dashboard2Backup.ini";

        /// <summary>
        /// adFactory セットアップパス (ADFACTORY_HOME)
        /// </summary>
        private string adFactoryHome;

        /// <summary>
        /// adFactory テンポラリフォルダパス
        /// </summary>
        private string adFactoryTemp;

        /// <summary>
        /// アプリケーション ファイル
        /// </summary>
        private string appExeFilePath;
        
        /// <summary>
        /// アプリケーション設定 ファイルパス
        /// </summary>
        private string appIniFilePath;
        
        /// <summary>
        /// プラグインリスト ファイルパス
        /// </summary>
        private string itemListFilePath;
        
        /// <summary>
        /// インポート ファイルパス
        /// </summary>
        private string layoutCfgFilePath;
        
        /// <summary>
        /// インポート デフォルトファイルパス
        /// </summary>
        private string defaultLayoutCfgFilePath;
        
        /// <summary>
        /// レイアウト情報 ファイルパス
        /// </summary>
        private string layoutXmlFilePath;
        
        /// <summary>
        /// レイアウト情報 テンポラリファイルパス
        /// </summary>
        private string layoutTmpFilePath;
        
        /// <summary>
        /// adMonitor レイアウト情報 ファイルパス (旧ver)
        /// </summary>
        private string dashboardIniFilePath;
        
        /// <summary>
        /// adMonitor レイアウト情報 バックアップファイルパス (旧ver)
        /// </summary>
        private string dashboardBackupIniFilePath;
        
        /// <summary>
        /// adMonitor レイアウト情報 ファイルパス
        /// </summary>
        private string dashboard2IniFilePath;
        
        /// <summary>
        /// adMonitor レイアウト情報 バックアップファイルパス
        /// </summary>
        private string dashboard2BackupIniFilePath;

        /// <summary>
        /// 保存時のデフォルトパス
        /// </summary>
        private string defaultSaveFolderPath;

        /// <summary>
        /// adFactory セットアップパス (ADFACTORY_HOME)
        /// </summary>
        public string AdFactoryHome
        {
            get { return this.adFactoryHome; }
            set { this.adFactoryHome = value; }
        }

        /// <summary>
        /// adFactory テンポラリフォルダ
        /// </summary>
        public string AdFactoryTemp
        {
            get { return this.adFactoryTemp; }
            set { this.adFactoryTemp = value; }
        }

        /// <summary>
        /// アプリケーション ファイル
        /// </summary>
        public string AppExeFilePath
        {
            get { return this.appExeFilePath; }
            set { this.appExeFilePath = value; }
        }

        /// <summary>
        /// アプリケーション設定 ファイルパス 
        /// </summary>
        public string AppIniFilePath
        {
            get { return this.appIniFilePath; }
            set { this.appIniFilePath = value; }
        }

        /// <summary>
        /// プラグインリスト ファイルパス
        /// </summary>
        public string ItemListFilePath
        {
            get { return this.itemListFilePath; }
            set { this.itemListFilePath = value; }
        }

        /// <summary>
        /// インポート ファイルパス
        /// </summary>
        public string LayoutCfgFilePath
        {
            get { return this.layoutCfgFilePath; }
            set { this.layoutCfgFilePath = value; }
        }

        /// <summary>
        /// インポート デフォルトファイルパス
        /// </summary>
        public string DefaultLayoutCfgFilePath
        {
            get { return this.defaultLayoutCfgFilePath; }
            set { this.defaultLayoutCfgFilePath = value; }
        }

        /// <summary>
        /// レイアウト情報 ファイルパス
        /// </summary>
        public string LayoutXmlFilePath
        {
            get { return this.layoutXmlFilePath; }
            set { this.layoutXmlFilePath = value; }
        }

        /// <summary>
        /// レイアウト情報 テンポラリファイルパス
        /// </summary>
        public string LayoutTmpFilePath
        {
            get { return this.layoutTmpFilePath; }
            set { this.layoutTmpFilePath = value; }
        }

        /// <summary>
        /// adMonitor レイアウト情報 ファイルパス (旧ver)
        /// </summary>
        public string DashboardIniFilePath
        {
            get { return this.dashboardIniFilePath; }
            set { this.dashboardIniFilePath = value; }
        }

        /// <summary>
        /// adMonitor レイアウト情報 バックアップファイルパス (旧ver)
        /// </summary>
        public string DashboardBackupIniFilePath
        {
            get { return this.dashboardBackupIniFilePath; }
            set { this.dashboardBackupIniFilePath = value; }
        }

        /// <summary>
        /// adMonitor レイアウト情報 ファイルパス
        /// </summary>
        public string Dashboard2IniFilePath
        {
            get { return this.dashboard2IniFilePath; }
            set { this.dashboard2IniFilePath = value; }
        }

        /// <summary>
        /// adMonitor レイアウト情報 バックアップファイルパス
        /// </summary>
        public string Dashboard2BackupIniFilePath
        {
            get { return this.dashboard2BackupIniFilePath; }
            set { this.dashboard2BackupIniFilePath = value; }
        }

        /// <summary>
        /// デフォルトの保存フォルダパス
        /// </summary>
        public string DefaultSaveFolderPath
        {
            get { return this.defaultSaveFolderPath; }
            set { this.defaultSaveFolderPath = value; }
        }

        private static Settings instance = null;
        public static Settings GetInstance()
        {
            if (instance == null)
            {
                instance = new Settings();
            }
            return instance;
        }

        /// <summary>
        /// アプリケーション設定
        /// </summary>
        private Settings()
        {
            ProcessLogger.StartMethod();

            // adFactory セットアップパス
            this.adFactoryHome = System.Environment.ExpandEnvironmentVariables("%ADFACTORY_HOME%");

            // adFactory テンポラリフォルダ
            this.adFactoryTemp = Path.Combine(this.adFactoryHome, TEMP_FOLDER);

            // アプリケーション ファイルパス
            this.appExeFilePath = Path.Combine(this.adFactoryHome, BIN_FOLDER, APP_EXE);
            // アプリケーション設定 ファイルパス
            this.appIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, APP_INI);

            // プラグインリスト ファイルパス
            this.itemListFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, ITEM_LIST_FILE);

            // インポート ファイルパス
            this.layoutCfgFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, LAYOUT_CFG_FILE);
            // インポート デフォルトファイルパス
            this.defaultLayoutCfgFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, LAYOUT_CFG_FILE);

            // レイアウト情報 ファイルパス
            this.layoutXmlFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, LAYOUT_XML_FILE);
            // レイアウト情報 テンポラリファイルパス
            this.layoutTmpFilePath = Path.Combine(this.adFactoryHome, TEMP_FOLDER, LAYOUT_TMP_FILE);

            // adMonitor レイアウト情報 ファイルパス (旧ver)
            this.dashboardIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD_INI);
            this.dashboardBackupIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD_BACKUP_INI);

            // adMonitor レイアウト情報 ファイルパス
            this.dashboard2IniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD2_INI);
            this.dashboard2BackupIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD2_BACKUP_INI);

            // デフォルトの保存フォルダパス (設定フォルダ)
            this.defaultSaveFolderPath = Path.Combine(this.adFactoryHome, CONF_FOLDER);

            ProcessLogger.EndMethod();
        }
    }
}
