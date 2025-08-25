using System;
using System.Windows.Forms;
using System.Collections.Generic;
using System.Text;
using DashboardCustomizeTool.CommonClass;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// アプリケーション設定クラス
    /// </summary>
    public class Settings
    {
        //Fuji関係
        private const string DASHBOARF_NAME = "Dashboard";
        private const string WINDIR_NAME = "windir";
        private const string FUJINC_SECTIONL_SETUP = "SETUP";
        private const string FUJINC_KYEL_INSTALL = "Install";

        //パス関係
        private const string BACKUP_PATH = @"\conf";
        private const string ITEMLIST_NAME = "adAndonCustomizeToolItemList.ini";
        private const string XML_APP_PATH = @"\conf\adAndonCustomizeToolLayoutInfo.xml";
        private const string XML_NAME = "adAndonCustomizeToolLayoutInfo.xml";
        private const string INI_NAME  = "Dashboard.ini";
        private const string CFG_APP_PATH = @"\conf\adAndonCustomizeToolLayoutInfo.cfg";
        private const string CFG_NAME = "adAndonCustomizeToolLayoutInfo.cfg";
        private const string ITEMLIST_FLEXA_PATH = @"\conf\adAndonCustomizeToolItemList.ini";
        private const string ANDON_INI_PATH = @"\conf\DashboardBackup.ini";
        private const string INI_FLEXA_PATH = @"\conf\Dashboard.ini";
        private const string APPITEM_INI_INITIAL_DIRECTORY = "InitialDirectory";

        /// <summary>
        /// 前回の保存時のフォルダパス
        /// </summary>
        private string _initialdirectory;
        /// <summary>
        /// 保存時のデフォルトパス
        /// </summary>
        private string _initialdirectorydefault;
        /// <summary>
        /// アプリケーション設定ファイルフルパス
        /// </summary>
        private string _itemlistfilefullpath;
        /// <summary>
        /// アプリケーション設定ファイル名
        /// </summary>
        private string _itemlistfilename;
        /// <summary>
        /// レイアウト情報XMLファイルフルパス
        /// </summary>
        private string _layoutxmlfilefullpath;
       
        /// <summary>
        /// レイアウト情報XMLファイル名
        /// </summary>
        private string _layoutxmlfilename;
        /// <summary>
        /// レイアウト情報iniファイルフルパス
        /// </summary>
        private string _layoutinifilefullpath;
        /// <summary>
        /// レイアウト情報デフォルトiniファイルパス
        /// </summary>
        private string _defaultlayoutinifilefullpath;
        /// <summary>
        /// レイアウト情報iniファイル名
        /// </summary>
        private string _layoutinifilename;
        /// <summary>
        /// レイアウト情報デフォルトcfgファイルパス
        /// </summary>
        private string _defaultlayoutcfgfilefullpath;
        /// <summary>
        /// レイアウト情報cfgファイルフルパス
        /// </summary>
        private string _layoutcfgfilefullpath;
        /// <summary>
        /// レイアウト情報cfgファイル名
        /// </summary>
        private string _layoutcfgfilename;
      
        /// <summary>
        /// Fuji Flexaのfujinc.iniファイルフルパス
        /// </summary>
        private string _fujincinifilefullpath;

        /// <summary>
        /// Fuji Flexaのレイアウト情報iniファイルフルパス
        /// </summary>
        private string _layoutiniflexafilefullpath;

        /// <summary>
        /// Fuji Flexaのセットアップパス
        /// </summary>
        private string _fujiflexasetuppath;



        /// <summary>
        // adFactoryのDashboarLayoutTool.iniファイルフルパス
        /// </summary>
        private string _dashboardToolIniFullpath;

        /// <summary>
        // adFactoryのDashboarLayoutTool.exeファイルフルパス
        /// </summary>
        private string _dashboardExeFullpath;



        /// <summary>
        /// cfg前回の保存時のフォルダパス
        /// </summary>
        public string InitialDirectory
        {
            get { return _initialdirectory; }
            set { _initialdirectory = value; }
        }
        /// <summary>
        /// cfg保存時のデフォルトパス
        /// </summary>
        public string InitialDirectoryDefault
        {
            get { return _initialdirectorydefault; }
            set { _initialdirectorydefault = value; }
        }
        /// <summary>
        /// アプリケーション設定ファイルパス 
        /// </summary>
        public string ItemListFileFullPath
        {
            get { return _itemlistfilefullpath; }
            set { _itemlistfilefullpath = value; }
        }
        /// <summary>
        /// アプリケーション設定ファイル名 DashboardCustomizeToolItemList.ini
        /// </summary>
        public string ItemListFileName
        {
            get { return _itemlistfilename; }
            set { _itemlistfilename = value; }
        }
       
        /// <summary>
        /// レイアウト情報XMLファイルパス
        /// </summary>
        public string LayoutXmlFilrFullPath
        {
            get { return _layoutxmlfilefullpath; }
            set { _layoutxmlfilefullpath = value; }
        }
        /// <summary>
        /// レイアウト情報XMLファイル名
        /// </summary>
        public string LayoutXmlFilrName
        {
            get { return _layoutxmlfilename; }
            set { _layoutxmlfilename = value; }
        }
        /// <summary>
        /// レイアウト情報INIファイルパス
        /// </summary>
        public string LayoutIniFilrFullPath
        {
            get { return _layoutinifilefullpath; }
            set { _layoutinifilefullpath = value; }
        }
        /// <summary>
        /// レイアウト情報デフォルトINIファイルパス
        /// </summary>
        public string DefaultLayoutIniFilrFullPath
        {
            get { return _defaultlayoutinifilefullpath; }
            set { _defaultlayoutinifilefullpath = value; }
        }
        /// <summary>
        /// レイアウト情報INIファイル名
        /// </summary>
        public string LayoutIniFileName
        {
            get { return _layoutinifilename; }
            set { _layoutinifilename = value; }
        }


        /// <summary>
        /// レイアウト情報cfgファイルパス
        /// </summary>
        public string LayoutCfgFilrFullPath
        {
            get { return _layoutcfgfilefullpath; }
            set { _layoutcfgfilefullpath = value; }
        }
        /// <summary>
        /// レイアウト情報デフォルトcfgファイルパス
        /// </summary>
        public string DefaultLayoutCfgFilrFullPath
        {
            get { return _defaultlayoutcfgfilefullpath; }
            set { _defaultlayoutcfgfilefullpath = value; }
        }
        /// <summary>
        /// レイアウト情報cfgファイル名
        /// </summary>
        public string LayoutCfgFileName
        {
            get { return _layoutcfgfilename; }
            set { _layoutcfgfilename = value; }
        }

        /// <summary>
        /// Fuji Flexa レイアウト情報iniファイルフルパス
        /// </summary>
        public string LayoutIniFlexaFileFullPath
        {
            get { return _layoutiniflexafilefullpath; }
            set { _layoutiniflexafilefullpath = value; }
        }

        /// <summary>
        /// Fuji Flexaのfujinc.iniファイルフルパス
        /// </summary>
        public string FujincIniFileFullPath
        {
            get { return _fujincinifilefullpath; }
            set { _fujincinifilefullpath = value; }
        }

        /// <summary>
        /// Fuji FlexaのfSetupパス
        /// </summary>
        public string FujiFlexaSetUpPath
        {
            get { return _fujiflexasetuppath; }
            set { _fujiflexasetuppath = value; }
        }

        /// <summary>
        /// adFactoryのDashboarLayoutTool.iniファイルフルパス
        /// </summary>
        public string DashboardToolIniFullpath
        {
            get { return _dashboardToolIniFullpath; }
            set { _dashboardToolIniFullpath = value; }
        }        

        /// <summary>
        /// adFactoryのDashboarLayoutTool.exeファイルフルパス
        /// </summary>
        public string DashboardExeFullpath
        {
            get { return _dashboardExeFullpath; }
            set { _dashboardExeFullpath = value; }
        }

        /// <summary>
        /// アプリケーション設定
        /// </summary>
        public Settings()
        {
            ProcessLogger.StartMethod();

            //初期値設定
            //WINDOSWフォルダを取得
            string winDir = Environment.GetEnvironmentVariable(WINDIR_NAME);

            //アプリケーションパス(部分的)
            string apppath = (Program.EXE_PATH);
          
            //アプリケーション設定ファイル名
            _itemlistfilename = ITEMLIST_NAME;
         
            //レイアウト情報XMLファイル名
            _layoutxmlfilename = XML_NAME;
           
            //レイアウト情報iniファイル名
            _layoutinifilename = INI_NAME;
          
            //レイアウト情報cfgファイル名
            _layoutcfgfilename = CFG_NAME;

            //FujiFlexaセットアップパス
            _fujiflexasetuppath = System.Environment.ExpandEnvironmentVariables("%ADFACTORY_HOME%");
     
            //レイアウト情報デフォルトcfgファイルフルパス
            _defaultlayoutcfgfilefullpath = _fujiflexasetuppath + CFG_APP_PATH;

            //レイアウト情報cfgファイルフルパス
            _layoutcfgfilefullpath = _fujiflexasetuppath + CFG_APP_PATH;

            //アプリケーション設定ファイルフルパス
            _itemlistfilefullpath = _fujiflexasetuppath + ITEMLIST_FLEXA_PATH;

            //レイアウト情報XMLファイルフルパス
            _layoutxmlfilefullpath = _fujiflexasetuppath + XML_APP_PATH;

            //Flexa配下のレイアウト情報iniファイルフルパス
            _layoutiniflexafilefullpath = _fujiflexasetuppath + INI_FLEXA_PATH;

            //andonAppの設定ファイル
            _layoutinifilefullpath = _fujiflexasetuppath + ANDON_INI_PATH;

            // アプリケーションフルパス
            _dashboardExeFullpath = _fujiflexasetuppath + "\\" + "bin\\DashboardCustomizeTool.exe";

            IniManager inimanager = new IniManager();
            IniFile ini = new IniFile(ItemListFileFullPath);

            //前回の保存先フォルダ情報取得
            //前回の保存時のフォルダパス
            _initialdirectory = ini[APPITEM_INI_INITIAL_DIRECTORY, APPITEM_INI_INITIAL_DIRECTORY];

            //デフォルトパス（Flexa側のiniかなんかで取得可能？環境が判明したら変更
            _initialdirectorydefault = _fujiflexasetuppath + BACKUP_PATH;

            ProcessLogger.EndMethod();
        }
     
    }
}
