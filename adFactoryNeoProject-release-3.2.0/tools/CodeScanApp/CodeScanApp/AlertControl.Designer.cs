using CodeScanApp.Common;
namespace CodeScanApp
{
    partial class AlertControl
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

            this.exitButton = new System.Windows.Forms.Button();
            this.messageLabel = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // exitButton
            // 
            this.exitButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.exitButton.Location = hs.toFooterPoint(0, 227);
            this.exitButton.Name = "exitButton";
            this.exitButton.Size = hs.toButtonSize(100, 40);
            this.exitButton.TabIndex = 3;
            this.exitButton.Text = "終了";
            this.exitButton.Click += new System.EventHandler(this.exitButton_Click);
            // 
            // messageLabel
            // 
            this.messageLabel.Font = new System.Drawing.Font("Tahoma", 14F, System.Drawing.FontStyle.Bold);
            this.messageLabel.Location = hs.toPoint(2, 2);
            this.messageLabel.Name = "messageLabel";
            this.messageLabel.Size = hs.toSize(234, 197);
            this.messageLabel.Text = "KeyenceScan.iniが[\\FlashDisk\\HandyScanApp]に存在しません。";
            // 
            // AlertControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.Controls.Add(this.messageLabel);
            this.Controls.Add(this.exitButton);
            this.Name = "alertControl";
            this.Size = hs.toSize(238, 267);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button exitButton;
        private System.Windows.Forms.Label messageLabel;
    }
}
