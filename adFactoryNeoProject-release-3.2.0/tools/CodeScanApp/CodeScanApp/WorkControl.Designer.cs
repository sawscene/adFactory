using CodeScanApp.Common;
namespace CodeScanApp
{
    partial class WorkControl
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

            this.modelLabel = new System.Windows.Forms.Label();
            this.returnButton = new System.Windows.Forms.Button();
            this.listBox = new System.Windows.Forms.ListBox();
            //this.configButton = new System.Windows.Forms.Button();
            this.SuspendLayout();

            ///
            /// workLabel
            ///
            this.modelLabel.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.modelLabel.Location = hs.toPoint(2, 2);
            this.modelLabel.Name = "workLabel";
            this.modelLabel.Size = hs.toSize(238, 28);
            // 
            // returnButton
            // 
            this.returnButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.returnButton.Location = hs.toFooterPoint(2, 227);
            this.returnButton.Name = "returnButton";
            this.returnButton.Size = hs.toButtonSize(100, 40);
            this.returnButton.TabIndex = 3;
            this.returnButton.Text = "戻る";
            this.returnButton.Click += new System.EventHandler(this.returnButton_Click);
            // 
            // listBox
            // 
            this.listBox.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(22F), System.Drawing.FontStyle.Regular);
            this.listBox.Location = hs.toPoint(2, 40);
            this.listBox.Name = "listBox";
            this.listBox.Size = hs.toSize(234, 198);
            this.listBox.TabIndex = 2;
            this.listBox.SelectedIndexChanged += new System.EventHandler(this.listBox_SelectedIndexChanged);
            // 
            // configButton
            // 
            //this.configButton.Font = new System.Drawing.Font("Tahoma", ht.ToDeviceFontSize(20F), System.Drawing.FontStyle.Regular);
            //this.configButton.Location = ht.ToDeviceFooterPoint(138, 227);
            //this.configButton.Name = "configButton";
            //this.configButton.Size = ht.ToDeviceButtonSize(100, 40);
            //this.configButton.TabIndex = 5;
            //this.configButton.Text = "設定";
            //this.configButton.Click += new System.EventHandler(this.configButton_Click);
            // 
            // WorkControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.Controls.Add(this.modelLabel);
            //this.Controls.Add(this.configButton);
            this.Controls.Add(this.returnButton);
            this.Controls.Add(this.listBox);
            this.Name = "workControl";
            this.Size = hs.toSize(238, 267);
            this.ResumeLayout(false);
        }

        #endregion
    
        private System.Windows.Forms.Label modelLabel;
        private System.Windows.Forms.Button returnButton;
        private System.Windows.Forms.ListBox listBox;
        // private System.Windows.Forms.Button configButton;
    }
}
