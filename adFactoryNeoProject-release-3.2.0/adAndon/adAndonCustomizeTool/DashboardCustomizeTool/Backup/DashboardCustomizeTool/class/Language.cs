using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.IO;
using System.Windows.Forms.ComponentModel.Com2Interop;
using System.Globalization;
using System.Threading;
using System.Resources;

using System.Reflection;
using DashboardCustomizeTool.CommonClass;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// ��������
    /// </summary>
    class Language
    {
        private const int MAX_LENGTH = 260; //StringBuilder��MAX�l
        private const int INITIAL_LENGTH = 0; //StringBuilder�����l
        private const int SELECT_LENGTH = 2; //������

        [DllImport("user32.dll")]
        public static extern IntPtr GetForegroundWindow();

        //�\�����ꏈ��
        //SetDisplayLanguage�Ăяo��
        /// <summary>
        /// �T�e���C�gDLL�擾
        /// </summary>
        /// <returns></returns>
        public void SetDisplayLanguage()
        {
            Settings appsettings = new Settings();
            string lingual = string.Empty;

            //exe�����邩�m�F
            //�t���p�X���̎擾���@�̌������K�v
            if (System.IO.File.Exists(appsettings.DashboardExeFullpath))
            {
                // TODO:����Ή��͍���͂��Ȃ��B�Ƃ肠�������{����Œ�Őݒ肷��
                MainForm.Language = Define.LANGUAGE_SELECT_JP;
            }
        }
    }
}
