using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace DashboardCustomizeTool.CommonClass
{
    /// <summary>
    /// �G���[���b�Z�[�W�N���X
    /// </summary>
    class ErrorMessage
    {
        //�G���[���b�Z�[�W�^�C�g��
        public const string MAINTITEL_NAME = "DashboardCustomizeTool";
        public const string MSGCODE_FORMAT = "{0:0000}";
        public const int LOG_LV_HIGHT = 1;
        public const int LOG_LV_LOWT = 0;
        //�G���[�R�[�h

        //�N��
        public const string ERROR_MSGCODE_0001 = "1"; //DLL�s��
      
        //���O�C���G���[

        public const string ERROR_MSGCODE_0011 = "11";//���O�I�����s��

        //�A�v���P�[�V�����ݒ�G���[
        public const string ERROR_MSGCODE_0021 = "21";�@//�A�v���P�[�V�����ݒ�t�@�C�����s��
        public const string ERROR_MSGCODE_0022 = "22";�@//�A�v���P�[�V�����ݒ�t�@�C���A�C�e���Ȃ�
        public const string ERROR_MSGCODE_0023 = "23";�@//�A�v���P�[�V�����ݒ�t�@�C���L�[���ڕs��
        public const string ERROR_MSGCODE_0024 = "24";�@//16�i���ϊ��G���[

        //���C�A�E�g���cfg�t�@�C���G���[
        public const string ERROR_MSGCODE_0031 = "31"; //cfg�t�@�C�����Ȃ��ꍇ
        public const string ERROR_MSGCODE_0032 = "32"; //�𓀂����t�@�C�����Ȃ��ꍇ
    
        //���C�A�E�g���ini�t�@�C���G���[
        public const string ERROR_MSGCODE_0041 = "41"; //ini�t�@�C�����Ȃ��ꍇ
        public const string ERROR_MSGCODE_0042 = "42"; //ini�t�@�C�����L�[���ڕs��

        //���C�A�E�g���xml�t�@�C���G���[
        public const string ERROR_MSGCODE_0051 = "51"; //XML���[�h�G���[(���C�A�E�g�̒ǉ����@����ŕύX�̉\���@�L)

        //���C�A�E�g���t�@�C���̍��ڂ��A�v���P�[�V�����ݒ�ƈႤ�ꍇ
        public const string MSGCODE_8041 = "8041";

        private const string ERROR_LOGCODE = ": 6199";

        /// <summary>
        /// �G���[���b�Z�[�W�쐬
        /// </summary>
        /// <returns></returns>
        public void CreateErrorMessage(Exception e, bool logdisp, Form formname)
        {
            ProcessLogger.StartMethod();

            //�G���[���b�Z�[�W�擾
            string msgboxmessage = SetErrorMessage(e.Message);
            //�g���[�X���O�p���b�Z�[�W
            string errorlogmessage = string.Empty;
            //�g���[�X���O�p�G���[�R�[�h
            string errorlogcode = string.Empty;
            ShowMsgBoxAPI mg = new ShowMsgBoxAPI();

            //�G���[���b�Z�[�W�\��
       
            int num = 0;
            if (int.TryParse(e.Message, out num))
            {

                //�g���[�X���O�p�G���[���b�Z�[�W�쐬
                errorlogmessage = e.StackTrace + ERROR_LOGCODE + String.Format(MSGCODE_FORMAT, int.Parse(e.Message));
            }
            else
            {
                string message = e.StackTrace.Replace("\r", "").Replace("\n", "");
                errorlogmessage =message + ERROR_LOGCODE + String.Format(MSGCODE_FORMAT, int.Parse(ERROR_MSGCODE_0024));
            }

            //�g���[�X���O�����o��LV(true�F(�o�̓��x����Hight) false�F(�o�̓��x����Low)
            if (logdisp)
            {
                mg.ShowMsgBox(formname, MAINTITEL_NAME, msgboxmessage, MessageBoxButtons.OK, MessageBoxIcon.Error);

                //�g���[�X���O�o��
                ProcessLogger.Logger.Info("�g���[�X���O�o�̓��x��:" + LOG_LV_HIGHT + " �G���[���b�Z�[�W�F" + errorlogmessage);
            }
            else
            {
                mg.ShowMsgBox(formname, MAINTITEL_NAME, msgboxmessage, MessageBoxButtons.OK, MessageBoxIcon.Warning);

                //�g���[�X���O�o��
                ProcessLogger.Logger.Info("�g���[�X���O�o�̓��x��:" + LOG_LV_LOWT + " �G���[���b�Z�[�W�F" + errorlogmessage);
            }

            ProcessLogger.EndMethod();
        }

        private string SetErrorMessage(string errorcode)
        {
            ProcessLogger.StartMethod();

            string message = string.Empty;
            MultipleLanguages languageSettings = MultipleLanguages.GetInstance();
 
            //�w�肵���G���[�R�[�h�̒l�ŃG���[���b�Z�[�W�ݒ�
            switch (errorcode)
            {
                case ERROR_MSGCODE_0001 :
                    //FujiFlexaDll�s���G���[
                    message = languageSettings.ErrorMessageFujiFlexaDll;
                    break;
                case ERROR_MSGCODE_0021:
                    //�A�v���P�[�V�����ݒ�t�@�C���G���[
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;
                case ERROR_MSGCODE_0022:
                    //�A�v���P�[�V�����ݒ�t�@�C���G���[
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;
                case ERROR_MSGCODE_0023:
                    //�A�v���P�[�V�����ݒ�t�@�C���G���[
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;

                case ERROR_MSGCODE_0031:
                    //���C�A�E�g���cfg�t�@�C���G���[
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                case ERROR_MSGCODE_0032:
                    //���C�A�E�g���cfg�t�@�C���G���[
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                case MSGCODE_8041:
                    //���C�A�E�g���t�@�C���̍��ڂ��A�v���P�[�V�����ݒ�ƈႤ�ꍇ
                    message = languageSettings.ErrorMessageLayoutIniFileDifference;
                    break;
                case ERROR_MSGCODE_0041:
                    //�f�t�H���g���C�A�E�g���ini�t�@�C�������݂��Ȃ��ׁA�ۑ����������s
                    message = languageSettings.ErrorMessageSaveLayout;
                    break;
                
                case ERROR_MSGCODE_0042:
                    //�f�t�H���g���C�A�E�g���ini�t�@�C�������݂��Ȃ��ׁA�ۑ����������s
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                case ERROR_MSGCODE_0051:
                    //�f�t�H���g���C�A�E�g���ini�t�@�C�������݂��Ȃ��ׁA�ۑ����������s
                    message = languageSettings.ErrorMessageLayoutIniFile;
                    break;
                default:
                    //�}�[�J�J���[���s���ȏꍇ�̃G���[
                    //�A�v���P�[�V�����ݒ�t�@�C���G���[
                    message = languageSettings.ErrorMessage_Item_IniFile;
                    break;
            }

            ProcessLogger.Logger.Info("���b�Z�[�W���e�F" + message);

            ProcessLogger.EndMethod();

            return message;
        }

    }
    /// <summary>
    /// ��O�N���X
    /// </summary>
    class MyException : Exception
    {
        /// <summary>
        /// �g���[�X���O�p�̗�O�𔭐�������B
        /// </summary>
        /// <param name="message">��O���e</param>
        public MyException(string message)
            : base(message)
        {
        }
    }
}