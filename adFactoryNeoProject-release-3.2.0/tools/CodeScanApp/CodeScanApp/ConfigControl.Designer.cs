using CodeScanApp.Common;
using System.Drawing;
namespace CodeScanApp
{
    partial class ConfigControl
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

            this.deviceListBox = new System.Windows.Forms.ListBox();
            this.returnButton = new System.Windows.Forms.Button();
            this.bluetoothButton = new System.Windows.Forms.RadioButton();
            this.wlanButton = new System.Windows.Forms.RadioButton();
            this.hostTextBox = new System.Windows.Forms.TextBox();
            this.testButton = new System.Windows.Forms.Button();
            this.removeButton = new System.Windows.Forms.Button();
            this.hostListBox = new System.Windows.Forms.ListBox();
            this.SuspendLayout();
            // 
            // deviceListBox
            // 
            this.deviceListBox.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(16F), System.Drawing.FontStyle.Regular);
            this.deviceListBox.Location = hs.isVGAScreen() ? new Point(4, 348) :  new Point(2, 161);
            this.deviceListBox.Name = "deviceListBox";
            this.deviceListBox.Size = hs.isVGAScreen() ? new Size(System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width - 10, 140) : new Size(234, 54);
            this.deviceListBox.TabIndex = 3;
            this.deviceListBox.SelectedIndexChanged += new System.EventHandler(this.deviceListBox_SelectedIndexChanged);
            // 
            // returnButton
            // 
            this.returnButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.returnButton.Location = hs.toFooterPoint(2, 227);
            this.returnButton.Name = "returnButton";
            this.returnButton.Size = hs.toButtonSize(100, 40);
            this.returnButton.TabIndex = 4;
            this.returnButton.Text = "戻る";
            this.returnButton.Click += new System.EventHandler(this.returnButton_Click);
            // 
            // bluetoothButton
            // 
            this.bluetoothButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(14F), System.Drawing.FontStyle.Bold);
            this.bluetoothButton.Location = hs.toPoint(2, 137);
            this.bluetoothButton.Name = "bluetoothButton";
            this.bluetoothButton.Size = hs.toSize(234, 20);
            this.bluetoothButton.TabIndex = 5;
            this.bluetoothButton.Text = "Bluetooth";
            this.bluetoothButton.CheckedChanged += new System.EventHandler(this.bluetoothButton_CheckedChanged);
            // 
            // wlanButton
            // 
            this.wlanButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(14F), System.Drawing.FontStyle.Bold);
            this.wlanButton.Location = hs.toPoint(2, 2);
            this.wlanButton.Name = "wlanButton";
            this.wlanButton.Size = hs.toSize(232, 20);
            this.wlanButton.TabIndex = 6;
            this.wlanButton.Text = "無線LAN";
            this.wlanButton.CheckedChanged += new System.EventHandler(this.wlanButton_CheckedChanged);
            // 
            // hostTextBox
            // 
            this.hostTextBox.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(16F), System.Drawing.FontStyle.Bold);
            this.hostTextBox.Location = hs.toPoint(2, 24);
            this.hostTextBox.MaxLength = 63;
            this.hostTextBox.Name = "hostTextBox";
            this.hostTextBox.Size = hs.toSize(234, 32);
            this.hostTextBox.TabIndex = 7;
            this.hostTextBox.TextChanged += new System.EventHandler(this.hostTextBox_TextChanged);
            // 
            // testButton
            // 
            this.testButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(20F), System.Drawing.FontStyle.Regular);
            this.testButton.Location = hs.toFooterPoint(138, 227);
            this.testButton.Name = "testButton";
            this.testButton.Size = hs.toButtonSize(100, 40);
            this.testButton.TabIndex = 8;
            this.testButton.Text = "接続";
            this.testButton.Click += new System.EventHandler(this.testButton_Click);
            // 
            // removeButton
            // 
            this.removeButton.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(14F), System.Drawing.FontStyle.Regular);
            this.removeButton.Location = hs.toPoint(156, 131);
            this.removeButton.Name = "removeButton";
            this.removeButton.Size = hs.toSize(80, 28);
            this.removeButton.TabIndex = 8;
            this.removeButton.Text = "削除";
            this.removeButton.Click += new System.EventHandler(this.removeButton_Click);
            // 
            // hostListBox
            // 
            this.hostListBox.Font = new System.Drawing.Font("Tahoma", hs.toFontSize(16F), System.Drawing.FontStyle.Regular);
            this.hostListBox.Location = hs.isVGAScreen() ? new Point(4, 114) :  new Point(2, 59);
            this.hostListBox.Name = "hostListBox";
            this.hostListBox.Size = hs.isVGAScreen() ? new Size(System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width - 10, 140) : new Size(234, 54);
            this.hostListBox.TabIndex = 3;
            this.hostListBox.SelectedIndexChanged += new System.EventHandler(this.hostListBox_SelectedIndexChanged);
            // 
            // ConfigControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.Controls.Add(this.removeButton);
            this.Controls.Add(this.testButton);
            this.Controls.Add(this.hostTextBox);
            this.Controls.Add(this.wlanButton);
            this.Controls.Add(this.bluetoothButton);
            this.Controls.Add(this.returnButton);
            this.Controls.Add(this.hostListBox);
            this.Controls.Add(this.deviceListBox);
            this.Name = "ConfigControl";
            this.Size = hs.toSize(238, 267);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.ListBox deviceListBox;
        private System.Windows.Forms.Button returnButton;
        private System.Windows.Forms.RadioButton bluetoothButton;
        private System.Windows.Forms.RadioButton wlanButton;
        private System.Windows.Forms.TextBox hostTextBox;
        private System.Windows.Forms.Button testButton;
        private System.Windows.Forms.Button removeButton;
        private System.Windows.Forms.ListBox hostListBox;
    }
}
