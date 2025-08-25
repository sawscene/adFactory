//********************************************************************
//  FileName : MainForm.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System;
using System.Linq;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using CodeScanApp.Common;
using System.IO;

namespace CodeScanApp
{
    /// <summary>
    /// メイン画面
    /// </summary>
    public partial class MainForm : Form
    {
        public static ModelControl modelCtrl;
        public static WorkControl workCtrl;
        public static ScanControl scanCtrl;
        public static ConfigControl configCtrl;
        public static AlertControl alertCtrl;
        public static ScanConfigControl scanConfigCtrl;
        

        /// <summary>
        /// コンストラクター
        /// </summary>
        public MainForm()
        {
            this.InitializeComponent();
            
            Cursor.Current = Cursors.WaitCursor;

            string error = Config.LoadConfig();

            MainForm.modelCtrl = new ModelControl();
            MainForm.workCtrl = new WorkControl();
            MainForm.scanCtrl = new ScanControl();
            MainForm.configCtrl = new ConfigControl();
            MainForm.scanConfigCtrl = new ScanConfigControl();
            MainForm.alertCtrl = new AlertControl();


            this.mainPanel.Controls.Add(modelCtrl);
            this.mainPanel.Controls.Add(workCtrl);
            this.mainPanel.Controls.Add(scanCtrl);
            this.mainPanel.Controls.Add(configCtrl);
            this.mainPanel.Controls.Add(scanConfigCtrl);
            this.mainPanel.Controls.Add(alertCtrl);

            if (String.IsNullOrEmpty(error))
            {
                bool isShow = false;
                if (0 == Config.ConnectType)
                {
                    isShow = String.IsNullOrEmpty(Config.WLANHost);
                }
                else 
                {
                    isShow = String.IsNullOrEmpty(Config.BluetoothHost.addr);
                }

                if (isShow)
                {
                    // 設定画面を表示
                    MainForm.showConfig();
                    return;
                }

                MainForm.showModel();

            } else {
                MainForm.scanCtrl.Visible = false;
                MainForm.workCtrl.Visible = false;
                MainForm.modelCtrl.Visible = false;
                MainForm.configCtrl.Visible = false;
                MainForm.scanConfigCtrl.Visible = false;
                MainForm.alertCtrl.Visible = true;

                MainForm.alertCtrl.Message = error;
            }

            Cursor.Current = Cursors.Default;

            Program._form.Close();
        }

        /// <summary>
        /// 機種選択画面を表示する。
        /// </summary>
        public static void showModel()
        {
            MainForm.scanCtrl.Visible = false;
            MainForm.workCtrl.Visible = false;
            MainForm.modelCtrl.Visible = true;
            MainForm.configCtrl.Visible = false;
            MainForm.scanConfigCtrl.Visible = false;
            MainForm.alertCtrl.Visible = false;
            MainForm.modelCtrl.hostLabel.Text = Config.WLANHost;

        }

       /// <summary>
        /// 工程選択画面を表示する。
        /// </summary>
        public static void showWork()
        {
            MainForm.scanCtrl.Visible = false;
            MainForm.workCtrl.Visible = true;
            MainForm.modelCtrl.Visible = false;
            MainForm.configCtrl.Visible = false;
            MainForm.scanConfigCtrl.Visible = false;
            MainForm.alertCtrl.Visible = false;
        }
        
        /// <summary>
        /// 読取画面を表示する。
        /// </summary>
        public static void showScan()
        {
            MainForm.scanCtrl.Visible = true;
            MainForm.workCtrl.Visible = false;
            MainForm.modelCtrl.Visible = false;
            MainForm.configCtrl.Visible = false;
            MainForm.scanConfigCtrl.Visible = false;
            MainForm.alertCtrl.Visible = false;
        }

        /// <summary>
        /// 設定画面を表示する。
        /// </summary>
        public static void showConfig()
        {
            MainForm.scanCtrl.Visible = false;
            MainForm.workCtrl.Visible = false;
            MainForm.modelCtrl.Visible = false;
            MainForm.configCtrl.Visible = true;
            MainForm.scanConfigCtrl.Visible = false;
            MainForm.alertCtrl.Visible = false;

            MainForm.configCtrl.update();
        }

        /// <summary>
        /// 設定画面を表示する。
        /// </summary>
        public static void showScanConfig()
        {
            MainForm.scanCtrl.Visible = false;
            MainForm.workCtrl.Visible = false;
            MainForm.modelCtrl.Visible = false;
            MainForm.configCtrl.Visible = false;
            MainForm.scanConfigCtrl.Visible = true;
            MainForm.alertCtrl.Visible = false;

            MainForm.configCtrl.update();
        }

        /// <summary>
        /// カレントディレクトリのパスを取得する。
        /// </summary>
        /// <returns></returns>
        private string GetCurrentPath()
        {
            string path = this.GetType().Assembly.GetModules()[0].FullyQualifiedName;
            int index = path.LastIndexOf(@"\");
            return path.Substring(0, index);
        }
    }
}