using System;
using System.Runtime.InteropServices;
using CodeScanApp.Common;
using System.Drawing;
namespace CodeScanApp
{
    partial class ModelControl
    {
        /// <summary> 
        /// 必要なデザイナ変数です。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// 使用中のリソースをすべてクリーンアップします。
        /// </summary>
        /// <param name="disposing">マネージ リソースが破棄される場合 true、破棄されない場合は false です。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region コンポーネント デザイナで生成されたコード

        /// <summary> 
        /// デザイナ サポートに必要なメソッドです。このメソッドの内容を 
        /// コード エディタで変更しないでください。
        /// </summary>
        private void InitializeComponent()
        {
            HandyScanner hs = new HandyScanner();

            this.label = new System.Windows.Forms.Label();
            this.exitButton = new System.Windows.Forms.Button();
            this.listBox = new System.Windows.Forms.ListBox();
            this.configButton = new System.Windows.Forms.Button();
            this.hostLabel = new System.Windows.Forms.Label();
            this.SuspendLayout();
            ///
            /// label
            ///
            this.label.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(18F), System.Drawing.FontStyle.Regular);
            this.label.Location = hs.toPoint(2, 2);
            this.label.Name = "workLabel";
            this.label.Size = hs.toSize(238, 28);
            this.label.Text = "機種を選択してください";
            // 
            // exitButton
            // 
            this.exitButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.exitButton.Location = hs.toFooterPoint(2, 227);
            this.exitButton.Name = "exitButton";
            this.exitButton.Size = hs.toButtonSize(100, 40);
            this.exitButton.TabIndex = 3;
            this.exitButton.Text = "終了";
            this.exitButton.Click += new System.EventHandler(this.exitButton_Click);
            // 
            // listBox
            // 
            this.listBox.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(22F), System.Drawing.FontStyle.Regular);
            this.listBox.Location = hs.toPoint(2, 40);
            this.listBox.Name = "patternListBox";
            this.listBox.Size = hs.isVGAScreen() ? hs.toSize(234, 173) : new System.Drawing.Size(234, 176);
            this.listBox.TabIndex = 2;
            this.listBox.SelectedIndexChanged += new System.EventHandler(this.patternListBox_SelectedIndexChanged);
            //
            // hostLabel
            //
            this.hostLabel.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(14F), System.Drawing.FontStyle.Regular);
            this.hostLabel.Location = hs.isVGAScreen() ?new System.Drawing.Point(2, 464) : new System.Drawing.Point(2, 200);
            this.hostLabel.Name = "hostLabel";
            this.hostLabel.Size = hs.toSize(236, 18);
            this.hostLabel.Text = Config.WLANHost;
            this.hostLabel.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // configButton
            // 
            this.configButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.configButton.Location = hs.toFooterPoint(138, 227);
            this.configButton.Name = "configButton";
            this.configButton.Size = hs.toButtonSize(100, 40);
            this.configButton.TabIndex = 5;
            this.configButton.Text = "設定";
            this.configButton.Click += new System.EventHandler(this.configButton_Click);
            // 
            // ModelControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.Controls.Add(this.label);
            this.Controls.Add(this.configButton);
            this.Controls.Add(this.exitButton);
            this.Controls.Add(this.listBox);
            this.Controls.Add(this.hostLabel);
            this.Name = "modelControl";
            this.Size = hs.toSize(238, 267);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Label label;
        private System.Windows.Forms.Button exitButton;
        private System.Windows.Forms.ListBox listBox;
        private System.Windows.Forms.Button configButton;
        public System.Windows.Forms.Label hostLabel;
    }
}
