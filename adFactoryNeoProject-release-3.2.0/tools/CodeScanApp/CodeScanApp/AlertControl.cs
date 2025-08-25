//********************************************************************
//  FileName : AlertControl.cs
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
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace CodeScanApp
{
    /// <summary>
    /// アラートコントロール
    /// </summary>
    public partial class AlertControl : UserControl
    {

        /// <summary>
        /// エラーメッセージ
        /// </summary>
        public string Message
        { 
            get{ return messageLabel.Text; } 
            set{ messageLabel.Text = value; }
        }

        /// <summary>
        /// コンストラクター
        /// </summary>
        public AlertControl()
        {
            this.InitializeComponent();
        }

        /// <summary>
        /// 終了ボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void exitButton_Click(object sender, EventArgs e)
        {
            // アプリケーションを終了
            Application.Exit();
        }
    }
}
