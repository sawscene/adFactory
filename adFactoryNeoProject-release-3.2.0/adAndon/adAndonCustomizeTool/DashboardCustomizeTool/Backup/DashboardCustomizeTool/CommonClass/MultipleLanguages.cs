using System;
using System.Collections.Generic;
using System.Text;

namespace DashboardCustomizeTool.CommonClass
{
    class MultipleLanguages
    {

        private const string LANGUAGE_SELECT_JP = "JP";  //���{��
        private const string LANGUAGE_SELECT_US = "US";  //�p��
        private const string LANGUAGE_SELECT_SC = "SC";�@//�ȑ̎�
        private const string LANGUAGE_SELECT_TC = "TC";  //�ɑ̎�

        /// <summary>
        /// DashBoardCustomizeTool�����ɋN�����Ă��܂��B
        /// </summary>
        private string _errorMessageDouble;

        /// <summary>
        /// �A�v���P�[�V�����N�����A�ݒ�t�@�C���̏��擾�ɂăG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        private string _errorMessage_Item_IniFile;
        /// <summary>
        /// �A�v���P�[�V�����N�����A�K�v�ȃ��W���[�����s�����Ă��邽�ߋN���ł��Ȃ��Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        private string _errorMessageFujiFlexaDll;
        /// <summary>
        /// �A�v���P�[�V�����N�����A��ʕ\���̂��߂̏��擾�ɂăG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        private string _errorMessageLayoutIniFile;
        /// <summary>
        /// �A�v���P�[�V�����N�����A��`�t�@�C���ɒ�`����Ă��Ȃ��A�C�e���̕\���ݒ肪����Ă���ꍇ�ɖ{���b�Z�[�W��\�����܂��B
        /// </summary>
        private string _errorMessageLayoutIniFileDifference;
        /// <summary>
        /// ���C�A�E�g�ۑ����ɃG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����ۑ��𒆒f���܂��B

        /// </summary>
        private string _errorMessageSaveLayout;
        /// <summary>
        ///�C���|�[�g/�G�N�X�|�[�g���̃t�@�C���I���_�C�A���O�̃t�@�C����ŕ\��
        /// </summary>
        private string _fileReadDlgFilter;
        /// <summary>
        /// ���C�A�E�g�ݒ�����C���|�[�g����ۂ̃_�C�A���O�^�C�g���ł��B
        /// </summary>
        private string _fileReadDlgTitle;
        /// <summary>
        /// ���C�A�E�g�ݒ�����G�N�X�|�[�g����ۂ̃_�C�A���O�^�C�g���ł��B
        /// </summary>
        private string _fileSaveDlgTitle;

        /// <summary>
        /// �A�v���P�[�V�����N������Dashboard���N�����Ă���Ɩ{���b�Z�[�W��\����ADashboard�I�����Ă���A�v���P�[�V�������N�����܂��B
        /// </summary>
        private string _messageDashboardEnd;
        /// <summary>
        /// �C���|�[�g���s���̊m�F���b�Z�[�W
        /// </summary>
        private string _messageImportLayout;
        /// <summary>
        ///���C�A�E�g�ݒ�����G�N�X�|�[�g����ہA�ҏW�������C�A�E�g���ۑ�����Ă��Ȃ��ꍇ�A�{���b�Z�[�W��\�����܂��B
        /// </summary>
        private string _messageLayoutCfg;
        /// <summary>
        ///���C�A�E�g��ύX��A�ۑ������ɏI�����s�����Ƃ����ۂɖ{���b�Z�[�W��\�����܂��B 
        /// </summary>
        private string _messageSaveLayout;


        //�R���g���[����
        /// <summary>
        /// �A�v���P�[�V�����^�C�g��
        /// </summary>
        private string _appTitleText;
        /// <summary>
        /// �R���e�L�X�g���j���[���́i�A�C�e���̒ǉ�)
        /// </summary>
        private string _addToolStripItemText;
       
        /// <summary>
        /// ���j���[���X�g���́i�A�C�e���̒ǉ��j
        /// </summary>
        private string _addToolStripMenuItemText;
        /// <summary>
        /// �R���e�L�X�g���j���[���́i�A�C�e���̍폜�j
        /// </summary>
        private string _deleteToolStripItemText;
        /// <summary>
        /// ���j���[���X�g���́i�A�C�e���̍폜�j
        /// </summary>
        private string _deleteToolStripMenuItemText;
        /// <summary>
        /// ���j���[���X�g���́i�ҏW�j
        /// </summary>
        private string _editEToolStripMenuItemText;
        /// <summary>
        /// ���j���[���X�g���́i�I���j
        /// </summary>
        private string _endDToolStripMenuItemText;
        /// <summary>
        /// ���j���[���X�g���́i�G�N�X�|�[�g�j
        /// </summary>
        private string _exportToolStripMenuItemText;
        /// <summary>
        /// ���j���[���X�g���́i�t�@�C���j
        /// </summary>
        private string _fileFToolStripMenuItemText;
        /// <summary>
        /// ���j���[���X�g���́i�C���|�[�g�j
        /// </summary>
        private string _importToolStripMenuItemText;
        /// <summary>
        /// ���j���[���X�g���́i�ۑ��j
        /// </summary>
        private string _saveToolStripMenuItemText;


        /// <summary>
        /// �A�v���P�[�V�����N�����A�ݒ�t�@�C���̏��擾�ɂăG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        public string ErrorMessageDouble
        {
            get { return _errorMessageDouble; }
            set { _errorMessageDouble = value; }
        }
        /// <summary>
        /// �A�v���P�[�V�����N�����A�ݒ�t�@�C���̏��擾�ɂăG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        public string ErrorMessage_Item_IniFile
        {
            get { return _errorMessage_Item_IniFile; }
            set { _errorMessage_Item_IniFile = value; }
        }
        /// <summary>
        /// �A�v���P�[�V�����N�����A�K�v�ȃ��W���[�����s�����Ă��邽�ߋN���ł��Ȃ��Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        public string ErrorMessageFujiFlexaDll
        {
            get { return _errorMessageFujiFlexaDll; }
            set { _errorMessageFujiFlexaDll = value; }
        }

        /// <summary>
        /// �A�v���P�[�V�����N�����A��ʕ\���̂��߂̏��擾�ɂăG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����AOK�{�^���ɂ��A�v���P�[�V�������I�����܂��B
        /// </summary>
        public string ErrorMessageLayoutIniFile
        {
            get { return _errorMessageLayoutIniFile; }
            set { _errorMessageLayoutIniFile = value; }
        }

        /// <summary>
        /// �A�v���P�[�V�����N�����A��`�t�@�C���ɒ�`����Ă��Ȃ��A�C�e���̕\���ݒ肪����Ă���ꍇ�ɖ{���b�Z�[�W��\�����܂��B
        /// </summary>
        public string ErrorMessageLayoutIniFileDifference
        {
            get { return _errorMessageLayoutIniFileDifference; }
            set { _errorMessageLayoutIniFileDifference = value; }
        }

        /// <summary>
        /// ���C�A�E�g�ۑ����ɃG���[�����������Ƃ��ɖ{���b�Z�[�W�_�C�A���O��\�����ۑ��𒆒f���܂��B
        /// </summary>
        public string ErrorMessageSaveLayout
        {
            get { return _errorMessageSaveLayout; }
            set { _errorMessageSaveLayout = value; }
        }

        /// <summary>
        /// �C���|�[�g/�G�N�X�|�[�g���̃t�@�C���I���_�C�A���O�̃t�@�C����ŕ\��
        /// </summary>
        public string FileReadDlgFilter
        {
            get { return _fileReadDlgFilter; }
            set { _fileReadDlgFilter = value; }
        }

        /// <summary>
        /// ���C�A�E�g�ݒ�����G�N�X�|�[�g����ۂ̃_�C�A���O�^�C�g��
        /// </summary>
        public string FileReadDlgTitle
        {
            get { return _fileReadDlgTitle; }
            set { _fileReadDlgTitle = value; }
        }

        /// <summary>
        /// ���C�A�E�g�ݒ�����G�N�X�|�[�g����ۂ̃_�C�A���O�^�C�g��
        /// </summary>
        public string FileSaveDlgTitle
        {
            get { return _fileSaveDlgTitle; }
            set { _fileSaveDlgTitle = value; }
        }

        /// <summary>
        /// �A�v���P�[�V�����N������Dashboard���N�����̃��b�Z�[�W
        /// </summary>
        public string MessageDashboardEnd
        {
            get { return _messageDashboardEnd; }
            set { _messageDashboardEnd = value; }
        }

        /// <summary>
        /// �C���|�[�g���s���̊m�F���b�Z�[�W
        /// </summary>
        public string MessageImportLayout
        {
            get { return _messageImportLayout; }
            set { _messageImportLayout = value; }
        }

        /// <summary>
        /// ���C�A�E�g�ݒ�����G�N�X�|�[�g�����C�A�E�g�̕ۑ��m�F���b�Z�[
        /// </summary>
        public string MessageLayoutCfg
        {
            get { return _messageLayoutCfg; }
            set { _messageLayoutCfg = value; }
        }

        /// <summary>
        /// �I�����Ƀ��C�A�E�g���ύX����Ă����ꍇ�̃��b�Z�[�W
        /// </summary>
        public string MessageSaveLayout
        {
            get { return _messageSaveLayout; }
            set { _messageSaveLayout = value; }
        }

        //�R���g���[����


        /// <summary>
        /// �c�[������
        /// </summary>
        public string AppTitleText
        {
            get { return _appTitleText; }
            set { _appTitleText = value; }
        }
        /// <summary>
        /// �R���e�L�X�g���j���[���́i�A�C�e���̒ǉ�
        /// </summary>
        public string AddToolStripItemText
        {
            get { return _addToolStripItemText; }
            set { _addToolStripItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�A�C�e���̒ǉ��j
        /// </summary>
        public string AddToolStripMenuItemText
        {
            get { return _addToolStripMenuItemText; }
            set { _addToolStripMenuItemText = value; }
        }

        /// <summary>
        /// �R���e�L�X�g���j���[���́i�A�C�e���̍폜�j
        /// </summary>
        public string DeleteToolStripItemText
        {
            get { return _deleteToolStripItemText; }
            set { _deleteToolStripItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�A�C�e���̍폜�j
        /// </summary>
        public string DeleteToolStripMenuItemText
        {
            get { return _deleteToolStripMenuItemText; }
            set { _deleteToolStripMenuItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�ҏW�j
        /// </summary>
        public string EditEToolStripMenuItemText
        {
            get { return _editEToolStripMenuItemText; }
            set { _editEToolStripMenuItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�I���j
        /// </summary>
        public string EndDToolStripMenuItemText
        {
            get { return _endDToolStripMenuItemText; }
            set { _endDToolStripMenuItemText = value; }
        }


        /// <summary>
        /// ���j���[���X�g���́i�G�N�X�|�[�g�j
        /// </summary>
        public string ExportToolStripMenuItemText
        {
            get { return _exportToolStripMenuItemText; }
            set { _exportToolStripMenuItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�t�@�C���j
        /// </summary>
        public string FileFToolStripMenuItemText
        {
            get { return _fileFToolStripMenuItemText; }
            set { _fileFToolStripMenuItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�C���|�[�g�j
        /// </summary>
        public string ImportToolStripMenuItemText
        {
            get { return _importToolStripMenuItemText; }
            set { _importToolStripMenuItemText = value; }
        }

        /// <summary>
        /// ���j���[���X�g���́i�ۑ��j
        /// </summary>
        public string SaveToolStripMenuItemText
        {
            get { return _saveToolStripMenuItemText; }
            set { _saveToolStripMenuItemText = value; }
        }

        public MultipleLanguages()
        {
            ProcessLogger.StartMethod();

            ProcessLogger.Logger.Info("LanguageType" + MainForm.Language);

            //�w�肵������̒l�ŃG���[���b�Z�[�W�ݒ�
            switch (MainForm.Language)
            {
                //���{��
                case LANGUAGE_SELECT_JP:
                    //���b�Z�[�W�ݒ�
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resource_jp.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resource_jp.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resource_jp.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resource_jp.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resource_jp.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resource_jp.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resource_jp.MessageSaveLayout;
                    //�R���g���[�����ݒ�
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resource_jp.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resource_jp.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resource_jp.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.SaveToolStripMenuItem;

                    break;

                //�ȑ̎�
                case LANGUAGE_SELECT_SC:
                    //���b�Z�[�W�ݒ�
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageSaveLayout;
                    //�R���g���[�����ݒ�
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.SaveToolStripMenuItem;

                    break;
                //�ɑ̎�
                case LANGUAGE_SELECT_TC:
                    //���b�Z�[�W�ݒ�
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageSaveLayout;
                    //�R���g���[�����ݒ�
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.SaveToolStripMenuItem;

                    break;
                //�p��
                default:
                    //���b�Z�[�W�ݒ�
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resources.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resources.MessageSaveLayout;
                    //�R���g���[�����ݒ�
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resources.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.SaveToolStripMenuItem;

                    break;
            }

            ProcessLogger.EndMethod();

        }

    }
}
