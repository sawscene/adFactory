//********************************************************************
//  FileName : MenuControl.cs
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
using CodeScanApp.Common;

namespace CodeScanApp
{
    /// <summary>
    /// メニューコントロール
    /// </summary>
    public partial class ModelControl : UserControl
    {
        /// <summary>
        /// コンストラクター
        /// </summary>
        public ModelControl()
        {
            this.InitializeComponent();

            foreach (Pattern pattern in Config.Patterns)
            {
                if (!this.listBox.Items.Contains(pattern.ModelName))
                {
                    this.listBox.Items.Add(pattern.ModelName);
                }
            }
        }

        /// <summary>
        /// 読取パターン選択時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void patternListBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            MainForm.workCtrl.setUpControl((string)this.listBox.SelectedItem);

            MainForm.showWork();
        }

        /// <summary>
        /// 終了ボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void exitButton_Click(object sender, EventArgs e)
        {
            if (DialogResult.Yes == MessageBox.Show("ハンディー読取アプリケーションを終了します。よろしいですか?", "確認", MessageBoxButtons.YesNo, MessageBoxIcon.Question, MessageBoxDefaultButton.Button2))
            {
                // アプリケーションを終了
                Application.Exit();
            }
        }

        /// <summary>
        /// 設定ボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void configButton_Click(object sender, EventArgs e)
        {
            MainForm.showConfig();
        }
    }
}
