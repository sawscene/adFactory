using CodeScanApp.Common;
using System.Drawing;
namespace CodeScanApp
{
    partial class ScanControl
    {
        HandyScanner hs = new HandyScanner();

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
            // Size viewSize = hs.isVGAScreen() ? new Size(400, 300) : new Size(160, 120);
            Size viewSize = hs.isVGAScreen() ? new Size(400, 260) : new Size(160, 120);

            this.pictureBox = new System.Windows.Forms.PictureBox();
            this.configButton = new System.Windows.Forms.Button();
            this.resetButton = new System.Windows.Forms.Button();
            this.returnButton = new System.Windows.Forms.Button();
            this.patternLabel = new System.Windows.Forms.Label();
            this.panel = new System.Windows.Forms.Panel();
            this.SuspendLayout();
            //
            // pictureBox
            //
            this.pictureBox.Location = hs.isVGAScreen() ? new Point((480 / 2) - (viewSize.Width / 2), 1) : new Point((240 / 2) - (viewSize.Width / 2), 0);
            this.pictureBox.Name = "pictureBox";
            this.pictureBox.Size = viewSize;
            this.pictureBox.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            // this.pictureBox.BackColor = Color.Black;
            // 
            // configButton
            // 
            this.configButton.Font = new Font("Tahoma", hs.toFontSize(20F), FontStyle.Regular);
            this.configButton.Location = hs.toFooterPoint(83, 227);
            this.configButton.Name = "configButton";
            this.configButton.Size = hs.toButtonSize(74, 40);
            this.configButton.TabIndex = 5;
            this.configButton.Text = "設定";
            this.configButton.Click += new System.EventHandler(this.configButton_Click);
            // 
            // resetButton
            // 
            this.resetButton.Font = new Font("Tahoma", hs.toFontSize(20F), FontStyle.Regular);
            this.resetButton.Location = hs.toFooterPoint(164, 227);
            this.resetButton.Name = "resetButton";
            this.resetButton.Size = hs.toButtonSize(74, 40);
            this.resetButton.TabIndex = 4;
            this.resetButton.Text = "リセット";
            this.resetButton.Click += new System.EventHandler(this.resetButton_Click);
            // 
            // returnButton
            // 
            this.returnButton.Font = new Font("Tahoma", hs.toFontSize(20F), FontStyle.Regular);
            this.returnButton.Location = hs.toFooterPoint(2, 227);
            this.returnButton.Name = "returnButton";
            this.returnButton.Size = hs.toButtonSize(74, 40);
            this.returnButton.TabIndex = 3;
            this.returnButton.Text = "戻る";
            this.returnButton.Click += new System.EventHandler(this.returnButton_Click);
            // 
            // patternLabel
            // 
            this.patternLabel.Font = new Font("Tahoma", hs.toFontSize(14F), FontStyle.Bold);
            this.patternLabel.Location = new Point(2, 2 + viewSize.Height);
            this.patternLabel.Name = "patternLabel";
            this.patternLabel.Size = hs.toSize(238, 20);
            this.patternLabel.Text = "パターン名";
            // 
            // panel
            // 
            this.panel.AutoScroll = true;
            this.panel.Location = new Point(2, hs.isVGAScreen() ? 48 + viewSize.Height : 24 + viewSize.Height);
            this.panel.Name = "panel";
            this.panel.Size = hs.isVGAScreen() ? new Size(System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width - 6, 180) : new Size(238, 80);
            // 
            // ScanControl
            // 
            this.AutoScaleDimensions = new SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.Controls.Add(this.pictureBox);
            this.Controls.Add(this.panel);
            this.Controls.Add(this.patternLabel);
            this.Controls.Add(this.configButton);
            this.Controls.Add(this.resetButton);
            this.Controls.Add(this.returnButton);
            this.Name = "scanControl";
            this.Size = hs.toSize(238, 267);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button configButton;
        private System.Windows.Forms.Button resetButton;
        private System.Windows.Forms.Button returnButton;
        private System.Windows.Forms.Label patternLabel;
        private System.Windows.Forms.Panel panel;
		private System.Windows.Forms.PictureBox pictureBox;
    }
}
