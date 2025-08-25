using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;


namespace DashboardCustomizeTool
{
    /// <summary>
    /// ���b�Z�[�W�{�b�N�X�\���p�N���X
    /// </summary>
    class ShowMsgBoxAPI
    {
        /// <summary>
        /// ���b�Z�[�W�{�b�N�X�\���֐�
        /// </summary>
        /// <param name="formname">�e�t���[��</param>
        /// <param name="titel">�\���^�C�g��</param>
        /// <param name="text">���b�Z�[�W</param>
        /// <param name="buttons">�\���{�^��</param>
        /// <param name="icon">�\���A�C�R��</param>
        /// <returns>�I�����ꂽ�{�^�����</returns>
        public DialogResult ShowMsgBox(Form formname, string titel, string text, MessageBoxButtons buttons, MessageBoxIcon icon)
        {
            //�����ꂽ�{�^���̏���߂�
            return MsgBox.Show(formname, text, titel, buttons, icon);
        }
   }

    /// <summary>
    /// Win API
    /// </summary>
    public class WinAPI
    {
        [DllImport("user32.dll")]
        public static extern IntPtr GetWindowLong(IntPtr hWnd, int nIndex);
        [DllImport("kernel32.dll")]
        public static extern IntPtr GetCurrentThreadId();
        [DllImport("user32.dll")]
        public static extern IntPtr SetWindowsHookEx(int idHook, HOOKPROC lpfn, IntPtr hInstance, IntPtr threadId);
        [DllImport("user32.dll")]
        public static extern bool UnhookWindowsHookEx(IntPtr hHook);
        [DllImport("user32.dll")]
        public static extern IntPtr CallNextHookEx(IntPtr hHook, int nCode, IntPtr wParam, IntPtr lParam);
        [DllImport("user32.dll")]
        public static extern bool SetWindowPos(IntPtr hWnd, int hWndInsertAfter, int X, int Y, int cx, int cy, uint uFlags);
        [DllImport("user32.dll")]
        public static extern bool GetWindowRect(IntPtr hWnd, out RECT lpRect);

        public delegate IntPtr HOOKPROC(int nCode, IntPtr wParam, IntPtr lParam);

        public const int GWL_HINSTANCE = (-6);
        public const int WH_CBT = 5;
        public const int HCBT_ACTIVATE = 5;

        public const int SWP_NOSIZE = 0x0001;
        public const int SWP_NOZORDER = 0x0004;
        public const int SWP_NOACTIVATE = 0x0010;

        public struct RECT
        {
            public RECT(int left, int top, int right, int bottom)
            {
                Left = left;
                Top = top;
                Right = right;
                Bottom = bottom;
            }

            public int Left;
            public int Top;
            public int Right;
            public int Bottom;
        }
    }

    /// <summary>
    /// �I�[�i�[�E�B���h�E�̐^���ɕ\������� MessageBox
    /// </summary>
    public class MsgBox
    {

        /// <summary>
        /// �e�E�B���h�E
        /// </summary>
        private IWin32Window m_ownerWindow = null;

        /// <summary>
        /// �t�b�N�n���h��
        /// </summary>
        private IntPtr m_hHook = (IntPtr)0;

        /// <summary>
        /// ���b�Z�[�W�{�b�N�X��\������
        /// </summary>
        /// <param name="owner"></param>
        /// <param name="messageBoxText"></param>
        /// <param name="caption"></param>
        /// <param name="button"></param>
        /// <param name="icon"></param>
        /// <returns></returns>
        public static DialogResult Show(
            IWin32Window owner,
            string messageBoxText,
            string caption,
            MessageBoxButtons button,
            MessageBoxIcon icon)
        {
            MsgBox mbox = new MsgBox(owner);
            return mbox.Show(messageBoxText, caption, button, icon);
        }

        /// <summary>
        /// �R���X�g���N�^
        /// </summary>
        /// <param name="window">�e�E�B���h�E</param>
        private MsgBox(IWin32Window window)
        {
            m_ownerWindow = window;
        }

        /// <summary>
        /// ���b�Z�[�W�{�b�N�X��\������
        /// </summary>
        /// <param name="messageBoxText"></param>
        /// <param name="caption"></param>
        /// <param name="button"></param>
        /// <param name="icon"></param>
        /// <returns></returns>
        private DialogResult Show(
            string messageBoxText,
            string caption,
            MessageBoxButtons button,
            MessageBoxIcon icon)
        {
            // �t�b�N��ݒ肷��B
            IntPtr hInstance = WinAPI.GetWindowLong(m_ownerWindow.Handle, WinAPI.GWL_HINSTANCE);
            IntPtr threadId = WinAPI.GetCurrentThreadId();
            m_hHook = WinAPI.SetWindowsHookEx(WinAPI.WH_CBT, new WinAPI.HOOKPROC(HookProc), hInstance, threadId);

            return MessageBox.Show(m_ownerWindow, messageBoxText, caption, button, icon);
        }

        /// <summary>
        /// �t�b�N�v���V�[�W��
        /// </summary>
        /// <param name="nCode"></param>
        /// <param name="wParam"></param>
        /// <param name="lParam"></param>
        /// <returns></returns>
        private IntPtr HookProc(int nCode, IntPtr wParam, IntPtr lParam)
        {

            if (nCode == WinAPI.HCBT_ACTIVATE)
            {
                WinAPI.RECT rcForm = new WinAPI.RECT(0, 0, 0, 0);
                WinAPI.RECT rcMsgBox = new WinAPI.RECT(0, 0, 0, 0);

                WinAPI.GetWindowRect(m_ownerWindow.Handle, out rcForm);
                WinAPI.GetWindowRect(wParam, out rcMsgBox);

                // �Z���^�[�ʒu���v�Z����B
                int x = (rcForm.Left + (rcForm.Right - rcForm.Left) / 2) - ((rcMsgBox.Right - rcMsgBox.Left) / 2);
                int y = (rcForm.Top + (rcForm.Bottom - rcForm.Top) / 2) - ((rcMsgBox.Bottom - rcMsgBox.Top) / 2);

                WinAPI.SetWindowPos(wParam, 0, x, y, 0, 0, WinAPI.SWP_NOSIZE | WinAPI.SWP_NOZORDER | WinAPI.SWP_NOACTIVATE);

                IntPtr result = WinAPI.CallNextHookEx(m_hHook, nCode, wParam, lParam);

                // �t�b�N����������B
                WinAPI.UnhookWindowsHookEx(m_hHook);
                m_hHook = (IntPtr)0;

                return result;

            }
            else
            {
                return WinAPI.CallNextHookEx(m_hHook, nCode, wParam, lParam);
            }
        }
    }
}