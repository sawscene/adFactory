using DashboardCustomizeTool.CommonClass;
using System;
using System.IO;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// �A�v���P�[�V�����ݒ�N���X
    /// </summary>
    public class Settings
    {
        private const string WINDIR_NAME = "windir";

        /// <summary>�v���O�����t�H���_��</summary>
        private const string BIN_FOLDER = "bin";
        /// <summary>�ݒ�t�H���_��</summary>
        private const string CONF_FOLDER = "conf";
        /// <summary>�e���|�����t�H���_��</summary>
        private const string TEMP_FOLDER = "temp";

        /// <summary>�A�v���P�[�V���� �t�@�C����</summary>
        private const string APP_EXE = "DashboardCustomizeTool.exe";
        /// <summary>�A�v���P�[�V�����ݒ� �t�@�C����</summary>
        private const string APP_INI = "DashboardCustomizeTool.ini";

        /// <summary>�v���O�C�����X�g �t�@�C����</summary>
        private const string ITEM_LIST_FILE = "adAndonCustomizeToolItemList.ini";

        /// <summary>�C���|�[�g �t�@�C����</summary>
        private const string LAYOUT_CFG_FILE = "adAndonCustomizeToolLayoutInfo.cfg";
        /// <summary>���C�A�E�g��� �t�@�C����</summary>
        public const string LAYOUT_XML_FILE = "adAndonCustomizeToolLayoutInfo.xml";
        /// <summary>���C�A�E�g��� �e���|�����t�@�C����</summary>
        private const string LAYOUT_TMP_FILE = "adAndonCustomizeToolLayoutInfo.tmp";

        /// <summary>adMonitor ���C�A�E�g��� �t�@�C���� (��ver)</summary>
        private const string DASHBOARD_INI = "Dashboard.ini";
        /// <summary>adMonitor ���C�A�E�g��� �o�b�N�A�b�v�t�@�C���� (��ver)</summary>
        public const string DASHBOARD_BACKUP_INI = "DashboardBackup.ini";

        /// <summary>adMonitor ���C�A�E�g��� �t�@�C����</summary>
        private const string DASHBOARD2_INI = "Dashboard2.ini";
        /// <summary>adMonitor ���C�A�E�g��� �o�b�N�A�b�v�t�@�C����</summary>
        public const string DASHBOARD2_BACKUP_INI = "Dashboard2Backup.ini";

        /// <summary>
        /// adFactory �Z�b�g�A�b�v�p�X (ADFACTORY_HOME)
        /// </summary>
        private string adFactoryHome;

        /// <summary>
        /// adFactory �e���|�����t�H���_�p�X
        /// </summary>
        private string adFactoryTemp;

        /// <summary>
        /// �A�v���P�[�V���� �t�@�C��
        /// </summary>
        private string appExeFilePath;
        
        /// <summary>
        /// �A�v���P�[�V�����ݒ� �t�@�C���p�X
        /// </summary>
        private string appIniFilePath;
        
        /// <summary>
        /// �v���O�C�����X�g �t�@�C���p�X
        /// </summary>
        private string itemListFilePath;
        
        /// <summary>
        /// �C���|�[�g �t�@�C���p�X
        /// </summary>
        private string layoutCfgFilePath;
        
        /// <summary>
        /// �C���|�[�g �f�t�H���g�t�@�C���p�X
        /// </summary>
        private string defaultLayoutCfgFilePath;
        
        /// <summary>
        /// ���C�A�E�g��� �t�@�C���p�X
        /// </summary>
        private string layoutXmlFilePath;
        
        /// <summary>
        /// ���C�A�E�g��� �e���|�����t�@�C���p�X
        /// </summary>
        private string layoutTmpFilePath;
        
        /// <summary>
        /// adMonitor ���C�A�E�g��� �t�@�C���p�X (��ver)
        /// </summary>
        private string dashboardIniFilePath;
        
        /// <summary>
        /// adMonitor ���C�A�E�g��� �o�b�N�A�b�v�t�@�C���p�X (��ver)
        /// </summary>
        private string dashboardBackupIniFilePath;
        
        /// <summary>
        /// adMonitor ���C�A�E�g��� �t�@�C���p�X
        /// </summary>
        private string dashboard2IniFilePath;
        
        /// <summary>
        /// adMonitor ���C�A�E�g��� �o�b�N�A�b�v�t�@�C���p�X
        /// </summary>
        private string dashboard2BackupIniFilePath;

        /// <summary>
        /// �ۑ����̃f�t�H���g�p�X
        /// </summary>
        private string defaultSaveFolderPath;

        /// <summary>
        /// adFactory �Z�b�g�A�b�v�p�X (ADFACTORY_HOME)
        /// </summary>
        public string AdFactoryHome
        {
            get { return this.adFactoryHome; }
            set { this.adFactoryHome = value; }
        }

        /// <summary>
        /// adFactory �e���|�����t�H���_
        /// </summary>
        public string AdFactoryTemp
        {
            get { return this.adFactoryTemp; }
            set { this.adFactoryTemp = value; }
        }

        /// <summary>
        /// �A�v���P�[�V���� �t�@�C��
        /// </summary>
        public string AppExeFilePath
        {
            get { return this.appExeFilePath; }
            set { this.appExeFilePath = value; }
        }

        /// <summary>
        /// �A�v���P�[�V�����ݒ� �t�@�C���p�X 
        /// </summary>
        public string AppIniFilePath
        {
            get { return this.appIniFilePath; }
            set { this.appIniFilePath = value; }
        }

        /// <summary>
        /// �v���O�C�����X�g �t�@�C���p�X
        /// </summary>
        public string ItemListFilePath
        {
            get { return this.itemListFilePath; }
            set { this.itemListFilePath = value; }
        }

        /// <summary>
        /// �C���|�[�g �t�@�C���p�X
        /// </summary>
        public string LayoutCfgFilePath
        {
            get { return this.layoutCfgFilePath; }
            set { this.layoutCfgFilePath = value; }
        }

        /// <summary>
        /// �C���|�[�g �f�t�H���g�t�@�C���p�X
        /// </summary>
        public string DefaultLayoutCfgFilePath
        {
            get { return this.defaultLayoutCfgFilePath; }
            set { this.defaultLayoutCfgFilePath = value; }
        }

        /// <summary>
        /// ���C�A�E�g��� �t�@�C���p�X
        /// </summary>
        public string LayoutXmlFilePath
        {
            get { return this.layoutXmlFilePath; }
            set { this.layoutXmlFilePath = value; }
        }

        /// <summary>
        /// ���C�A�E�g��� �e���|�����t�@�C���p�X
        /// </summary>
        public string LayoutTmpFilePath
        {
            get { return this.layoutTmpFilePath; }
            set { this.layoutTmpFilePath = value; }
        }

        /// <summary>
        /// adMonitor ���C�A�E�g��� �t�@�C���p�X (��ver)
        /// </summary>
        public string DashboardIniFilePath
        {
            get { return this.dashboardIniFilePath; }
            set { this.dashboardIniFilePath = value; }
        }

        /// <summary>
        /// adMonitor ���C�A�E�g��� �o�b�N�A�b�v�t�@�C���p�X (��ver)
        /// </summary>
        public string DashboardBackupIniFilePath
        {
            get { return this.dashboardBackupIniFilePath; }
            set { this.dashboardBackupIniFilePath = value; }
        }

        /// <summary>
        /// adMonitor ���C�A�E�g��� �t�@�C���p�X
        /// </summary>
        public string Dashboard2IniFilePath
        {
            get { return this.dashboard2IniFilePath; }
            set { this.dashboard2IniFilePath = value; }
        }

        /// <summary>
        /// adMonitor ���C�A�E�g��� �o�b�N�A�b�v�t�@�C���p�X
        /// </summary>
        public string Dashboard2BackupIniFilePath
        {
            get { return this.dashboard2BackupIniFilePath; }
            set { this.dashboard2BackupIniFilePath = value; }
        }

        /// <summary>
        /// �f�t�H���g�̕ۑ��t�H���_�p�X
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
        /// �A�v���P�[�V�����ݒ�
        /// </summary>
        private Settings()
        {
            ProcessLogger.StartMethod();

            // adFactory �Z�b�g�A�b�v�p�X
            this.adFactoryHome = System.Environment.ExpandEnvironmentVariables("%ADFACTORY_HOME%");

            // adFactory �e���|�����t�H���_
            this.adFactoryTemp = Path.Combine(this.adFactoryHome, TEMP_FOLDER);

            // �A�v���P�[�V���� �t�@�C���p�X
            this.appExeFilePath = Path.Combine(this.adFactoryHome, BIN_FOLDER, APP_EXE);
            // �A�v���P�[�V�����ݒ� �t�@�C���p�X
            this.appIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, APP_INI);

            // �v���O�C�����X�g �t�@�C���p�X
            this.itemListFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, ITEM_LIST_FILE);

            // �C���|�[�g �t�@�C���p�X
            this.layoutCfgFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, LAYOUT_CFG_FILE);
            // �C���|�[�g �f�t�H���g�t�@�C���p�X
            this.defaultLayoutCfgFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, LAYOUT_CFG_FILE);

            // ���C�A�E�g��� �t�@�C���p�X
            this.layoutXmlFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, LAYOUT_XML_FILE);
            // ���C�A�E�g��� �e���|�����t�@�C���p�X
            this.layoutTmpFilePath = Path.Combine(this.adFactoryHome, TEMP_FOLDER, LAYOUT_TMP_FILE);

            // adMonitor ���C�A�E�g��� �t�@�C���p�X (��ver)
            this.dashboardIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD_INI);
            this.dashboardBackupIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD_BACKUP_INI);

            // adMonitor ���C�A�E�g��� �t�@�C���p�X
            this.dashboard2IniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD2_INI);
            this.dashboard2BackupIniFilePath = Path.Combine(this.adFactoryHome, CONF_FOLDER, DASHBOARD2_BACKUP_INI);

            // �f�t�H���g�̕ۑ��t�H���_�p�X (�ݒ�t�H���_)
            this.defaultSaveFolderPath = Path.Combine(this.adFactoryHome, CONF_FOLDER);

            ProcessLogger.EndMethod();
        }
    }
}
