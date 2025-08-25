using System;
using System.Windows.Forms;
using System.Collections.Generic;
using System.Text;
using DashboardCustomizeTool.CommonClass;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// �A�v���P�[�V�����ݒ�N���X
    /// </summary>
    public class Settings
    {
        //Fuji�֌W
        private const string DASHBOARF_NAME = "Dashboard";
        private const string WINDIR_NAME = "windir";
        private const string FUJINC_SECTIONL_SETUP = "SETUP";
        private const string FUJINC_KYEL_INSTALL = "Install";

        //�p�X�֌W
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
        /// �O��̕ۑ����̃t�H���_�p�X
        /// </summary>
        private string _initialdirectory;
        /// <summary>
        /// �ۑ����̃f�t�H���g�p�X
        /// </summary>
        private string _initialdirectorydefault;
        /// <summary>
        /// �A�v���P�[�V�����ݒ�t�@�C���t���p�X
        /// </summary>
        private string _itemlistfilefullpath;
        /// <summary>
        /// �A�v���P�[�V�����ݒ�t�@�C����
        /// </summary>
        private string _itemlistfilename;
        /// <summary>
        /// ���C�A�E�g���XML�t�@�C���t���p�X
        /// </summary>
        private string _layoutxmlfilefullpath;
       
        /// <summary>
        /// ���C�A�E�g���XML�t�@�C����
        /// </summary>
        private string _layoutxmlfilename;
        /// <summary>
        /// ���C�A�E�g���ini�t�@�C���t���p�X
        /// </summary>
        private string _layoutinifilefullpath;
        /// <summary>
        /// ���C�A�E�g���f�t�H���gini�t�@�C���p�X
        /// </summary>
        private string _defaultlayoutinifilefullpath;
        /// <summary>
        /// ���C�A�E�g���ini�t�@�C����
        /// </summary>
        private string _layoutinifilename;
        /// <summary>
        /// ���C�A�E�g���f�t�H���gcfg�t�@�C���p�X
        /// </summary>
        private string _defaultlayoutcfgfilefullpath;
        /// <summary>
        /// ���C�A�E�g���cfg�t�@�C���t���p�X
        /// </summary>
        private string _layoutcfgfilefullpath;
        /// <summary>
        /// ���C�A�E�g���cfg�t�@�C����
        /// </summary>
        private string _layoutcfgfilename;
      
        /// <summary>
        /// Fuji Flexa��fujinc.ini�t�@�C���t���p�X
        /// </summary>
        private string _fujincinifilefullpath;

        /// <summary>
        /// Fuji Flexa�̃��C�A�E�g���ini�t�@�C���t���p�X
        /// </summary>
        private string _layoutiniflexafilefullpath;

        /// <summary>
        /// Fuji Flexa�̃Z�b�g�A�b�v�p�X
        /// </summary>
        private string _fujiflexasetuppath;



        /// <summary>
        // adFactory��DashboarLayoutTool.ini�t�@�C���t���p�X
        /// </summary>
        private string _dashboardToolIniFullpath;

        /// <summary>
        // adFactory��DashboarLayoutTool.exe�t�@�C���t���p�X
        /// </summary>
        private string _dashboardExeFullpath;



        /// <summary>
        /// cfg�O��̕ۑ����̃t�H���_�p�X
        /// </summary>
        public string InitialDirectory
        {
            get { return _initialdirectory; }
            set { _initialdirectory = value; }
        }
        /// <summary>
        /// cfg�ۑ����̃f�t�H���g�p�X
        /// </summary>
        public string InitialDirectoryDefault
        {
            get { return _initialdirectorydefault; }
            set { _initialdirectorydefault = value; }
        }
        /// <summary>
        /// �A�v���P�[�V�����ݒ�t�@�C���p�X 
        /// </summary>
        public string ItemListFileFullPath
        {
            get { return _itemlistfilefullpath; }
            set { _itemlistfilefullpath = value; }
        }
        /// <summary>
        /// �A�v���P�[�V�����ݒ�t�@�C���� DashboardCustomizeToolItemList.ini
        /// </summary>
        public string ItemListFileName
        {
            get { return _itemlistfilename; }
            set { _itemlistfilename = value; }
        }
       
        /// <summary>
        /// ���C�A�E�g���XML�t�@�C���p�X
        /// </summary>
        public string LayoutXmlFilrFullPath
        {
            get { return _layoutxmlfilefullpath; }
            set { _layoutxmlfilefullpath = value; }
        }
        /// <summary>
        /// ���C�A�E�g���XML�t�@�C����
        /// </summary>
        public string LayoutXmlFilrName
        {
            get { return _layoutxmlfilename; }
            set { _layoutxmlfilename = value; }
        }
        /// <summary>
        /// ���C�A�E�g���INI�t�@�C���p�X
        /// </summary>
        public string LayoutIniFilrFullPath
        {
            get { return _layoutinifilefullpath; }
            set { _layoutinifilefullpath = value; }
        }
        /// <summary>
        /// ���C�A�E�g���f�t�H���gINI�t�@�C���p�X
        /// </summary>
        public string DefaultLayoutIniFilrFullPath
        {
            get { return _defaultlayoutinifilefullpath; }
            set { _defaultlayoutinifilefullpath = value; }
        }
        /// <summary>
        /// ���C�A�E�g���INI�t�@�C����
        /// </summary>
        public string LayoutIniFileName
        {
            get { return _layoutinifilename; }
            set { _layoutinifilename = value; }
        }


        /// <summary>
        /// ���C�A�E�g���cfg�t�@�C���p�X
        /// </summary>
        public string LayoutCfgFilrFullPath
        {
            get { return _layoutcfgfilefullpath; }
            set { _layoutcfgfilefullpath = value; }
        }
        /// <summary>
        /// ���C�A�E�g���f�t�H���gcfg�t�@�C���p�X
        /// </summary>
        public string DefaultLayoutCfgFilrFullPath
        {
            get { return _defaultlayoutcfgfilefullpath; }
            set { _defaultlayoutcfgfilefullpath = value; }
        }
        /// <summary>
        /// ���C�A�E�g���cfg�t�@�C����
        /// </summary>
        public string LayoutCfgFileName
        {
            get { return _layoutcfgfilename; }
            set { _layoutcfgfilename = value; }
        }

        /// <summary>
        /// Fuji Flexa ���C�A�E�g���ini�t�@�C���t���p�X
        /// </summary>
        public string LayoutIniFlexaFileFullPath
        {
            get { return _layoutiniflexafilefullpath; }
            set { _layoutiniflexafilefullpath = value; }
        }

        /// <summary>
        /// Fuji Flexa��fujinc.ini�t�@�C���t���p�X
        /// </summary>
        public string FujincIniFileFullPath
        {
            get { return _fujincinifilefullpath; }
            set { _fujincinifilefullpath = value; }
        }

        /// <summary>
        /// Fuji Flexa��fSetup�p�X
        /// </summary>
        public string FujiFlexaSetUpPath
        {
            get { return _fujiflexasetuppath; }
            set { _fujiflexasetuppath = value; }
        }

        /// <summary>
        /// adFactory��DashboarLayoutTool.ini�t�@�C���t���p�X
        /// </summary>
        public string DashboardToolIniFullpath
        {
            get { return _dashboardToolIniFullpath; }
            set { _dashboardToolIniFullpath = value; }
        }        

        /// <summary>
        /// adFactory��DashboarLayoutTool.exe�t�@�C���t���p�X
        /// </summary>
        public string DashboardExeFullpath
        {
            get { return _dashboardExeFullpath; }
            set { _dashboardExeFullpath = value; }
        }

        /// <summary>
        /// �A�v���P�[�V�����ݒ�
        /// </summary>
        public Settings()
        {
            ProcessLogger.StartMethod();

            //�����l�ݒ�
            //WINDOSW�t�H���_���擾
            string winDir = Environment.GetEnvironmentVariable(WINDIR_NAME);

            //�A�v���P�[�V�����p�X(�����I)
            string apppath = (Program.EXE_PATH);
          
            //�A�v���P�[�V�����ݒ�t�@�C����
            _itemlistfilename = ITEMLIST_NAME;
         
            //���C�A�E�g���XML�t�@�C����
            _layoutxmlfilename = XML_NAME;
           
            //���C�A�E�g���ini�t�@�C����
            _layoutinifilename = INI_NAME;
          
            //���C�A�E�g���cfg�t�@�C����
            _layoutcfgfilename = CFG_NAME;

            //FujiFlexa�Z�b�g�A�b�v�p�X
            _fujiflexasetuppath = System.Environment.ExpandEnvironmentVariables("%ADFACTORY_HOME%");
     
            //���C�A�E�g���f�t�H���gcfg�t�@�C���t���p�X
            _defaultlayoutcfgfilefullpath = _fujiflexasetuppath + CFG_APP_PATH;

            //���C�A�E�g���cfg�t�@�C���t���p�X
            _layoutcfgfilefullpath = _fujiflexasetuppath + CFG_APP_PATH;

            //�A�v���P�[�V�����ݒ�t�@�C���t���p�X
            _itemlistfilefullpath = _fujiflexasetuppath + ITEMLIST_FLEXA_PATH;

            //���C�A�E�g���XML�t�@�C���t���p�X
            _layoutxmlfilefullpath = _fujiflexasetuppath + XML_APP_PATH;

            //Flexa�z���̃��C�A�E�g���ini�t�@�C���t���p�X
            _layoutiniflexafilefullpath = _fujiflexasetuppath + INI_FLEXA_PATH;

            //andonApp�̐ݒ�t�@�C��
            _layoutinifilefullpath = _fujiflexasetuppath + ANDON_INI_PATH;

            // �A�v���P�[�V�����t���p�X
            _dashboardExeFullpath = _fujiflexasetuppath + "\\" + "bin\\DashboardCustomizeTool.exe";

            IniManager inimanager = new IniManager();
            IniFile ini = new IniFile(ItemListFileFullPath);

            //�O��̕ۑ���t�H���_���擾
            //�O��̕ۑ����̃t�H���_�p�X
            _initialdirectory = ini[APPITEM_INI_INITIAL_DIRECTORY, APPITEM_INI_INITIAL_DIRECTORY];

            //�f�t�H���g�p�X�iFlexa����ini���Ȃ񂩂Ŏ擾�\�H��������������ύX
            _initialdirectorydefault = _fujiflexasetuppath + BACKUP_PATH;

            ProcessLogger.EndMethod();
        }
     
    }
}
