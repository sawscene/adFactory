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
    public partial class WorkControl : UserControl
    {
        public WorkControl()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 工程選択時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void listBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            MainForm.scanCtrl.setUpControl(this.modelLabel.Text, (string) this.listBox.SelectedItem);

            MainForm.showScan();
        }

        /// <summary>
        /// 戻るボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void returnButton_Click(object sender, EventArgs e)
        {
            MainForm.showModel();
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

        /// <summary>
        /// 工程選択画面を初期化する。
        /// </summary>
        /// <param name="pettern"></param>
        public void setUpControl(String modelName)
        {
            this.listBox.Items.Clear();

            this.modelLabel.Text = modelName;

            foreach (Pattern pattern in Config.Patterns)
            {
                if (String.Equals(pattern.ModelName, modelName)) {
                    this.listBox.Items.Add(pattern.WorkName);
                }
            }
        }
    }
}
