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
    public partial class ScanConfigControl : UserControl
    {
        /// <summary>
        /// 読取パターン
        /// </summary>
        private Pattern _pattern;

        private ScanItem _scanItem;

        
        /// <summary>
        /// 読取設定コントロールを初期化する
        /// </summary>
        public ScanConfigControl()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 完了ボタンの押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void okButton_Click(object sender, EventArgs e)
        {
            // 変更を適用する

            // ライブビュー表示
            this._pattern.LiveView = this.viewCheckBox.CheckState == CheckState.Checked ? 1 : 0;

            // 照明制御
            this._scanItem.LED = this.ledCheckBox.CheckState == CheckState.Checked ? 1 : 0;

            // マーカー表示
            this._scanItem.Marker = this.markarCheckBox.CheckState == CheckState.Checked ? 1 : 0;

            // カメラ照明
            this._scanItem.CameraLED = this.cameraCheckBox.CheckState == CheckState.Checked ? 1 : 0;

            // 照明パターン
            this._scanItem.Illuminaton = this.illumCheckBox1.CheckState == CheckState.Checked ? 1 : 0;
            this._scanItem.Illuminaton |= this.illumCheckBox2.CheckState == CheckState.Checked ? 2 : 0;
            this._scanItem.Illuminaton |= this.illumCheckBox3.CheckState == CheckState.Checked ? 4 : 0;
            this._scanItem.Illuminaton |= this.illumCheckBox4.CheckState == CheckState.Checked ? 8 : 0;

            // ローアングル制御方向
            this._scanItem.LowDirection = this.rowCheckBox1.CheckState == CheckState.Checked ? 1 : 0;
            this._scanItem.LowDirection |= this.rowCheckBox2.CheckState == CheckState.Checked ? 2 : 0;
            this._scanItem.LowDirection |= this.rowCheckBox3.CheckState == CheckState.Checked ? 4 : 0;
            this._scanItem.LowDirection |= this.rowCheckBox4.CheckState == CheckState.Checked ? 8 : 0;

            // ターゲット輝度設定
            this._scanItem.Brightness[0] = (int)this.brtUpDown1.Value;
            this._scanItem.Brightness[1] = (int)this.brtUpDown2.Value;
            this._scanItem.Brightness[2] = (int)this.brtUpDown3.Value;
            this._scanItem.Brightness[3] = (int)this.brtUpDown4.Value;
            
            // 読取距離表示LED制御
            this._scanItem.DistanceLED = this.disComboBox.SelectedIndex;

            // 3D照明凸凹設定
            switch (this.illum3dComboBox.SelectedIndex)
            {
                case 0:
                    this._scanItem.Illuminaton3D = 1;
                    break;
                case 1:
                    this._scanItem.Illuminaton3D = 2;
                    break;
                case 2:
                    this._scanItem.Illuminaton3D = 3;
                    break;
                default:
                    this._scanItem.Illuminaton3D = 1;
                    break;
            }
            
            MainForm.showScan();
            MainForm.scanCtrl.updateControl();
        }

        /// <summary>
        /// 読取設定コントロールを初期化する
        /// </summary>
        /// <param name="pattern"></param>
        /// <param name="scanItem"></param>
        public void setUpControl(Pattern pattern, ScanItem scanItem)
        {
            this._pattern = pattern;
            this._scanItem = scanItem;

            // ライブビュー表示
            this.viewCheckBox.Checked = this._pattern.LiveView == 1;

            // 照明制御
            this.ledCheckBox.Checked = this._scanItem.LED == 1;

            HandyScanner hs = new HandyScanner();
            if (hs.HandyType != HandyScanner.HT_BT_W370) {
                // BT-W370以外の場合、ライブビュー表以外を非表示
                this.SuspendLayout();
                this.label1.Visible = false;
                this.label3.Visible = false;
                this.label4.Visible = false;
                this.label5.Visible = false;
                this.markarCheckBox.Visible = false;
                this.cameraCheckBox.Visible = false;
                this.illumCheckBox1.Visible = false;
                this.illumCheckBox2.Visible = false;
                this.illumCheckBox3.Visible = false;
                this.illumCheckBox4.Visible = false;
                this.rowCheckBox1.Visible = false;
                this.rowCheckBox2.Visible = false;
                this.rowCheckBox3.Visible = false;
                this.rowCheckBox4.Visible = false;
                this.brtUpDown1.Visible = false;
                this.brtUpDown2.Visible = false;
                this.brtUpDown3.Visible = false;
                this.brtUpDown4.Visible = false;
                this.disComboBox.Visible = false;
                this.illum3dComboBox.Visible = false;
                this.okButton.Location = new Point(2, 227);
                this.ResumeLayout(false);
                return;
            }

            // マーカー表示
            this.markarCheckBox.Checked = this._scanItem.Marker == 1;

            // カメラ照明
            this.cameraCheckBox.Checked = this._scanItem.CameraLED == 1;

            // 照明パターン
            this.illumCheckBox1.Checked = (this._scanItem.Illuminaton & 1) == 1; // 内部照明
            this.illumCheckBox2.Checked = (this._scanItem.Illuminaton & 2) == 2; // マルチ照明
            this.illumCheckBox3.Checked = (this._scanItem.Illuminaton & 4) == 4; // ローアングル照明
            this.illumCheckBox4.Checked = (this._scanItem.Illuminaton & 8) == 8; // 3D照明

            // ローアングル制御方向
            this.rowCheckBox1.Checked = (this._scanItem.LowDirection & 1) == 1;
            this.rowCheckBox2.Checked = (this._scanItem.LowDirection & 2) == 2;
            this.rowCheckBox3.Checked = (this._scanItem.LowDirection & 4) == 4;
            this.rowCheckBox4.Checked = (this._scanItem.LowDirection & 8) == 8;

            // ターゲット輝度設定
            this.brtUpDown1.Value = this._scanItem.Brightness[0];
            this.brtUpDown2.Value = this._scanItem.Brightness[1];
            this.brtUpDown3.Value = this._scanItem.Brightness[2];
            this.brtUpDown4.Value = this._scanItem.Brightness[3];

            this.brtUpDown1.Enabled = this.illumCheckBox1.Checked;
            this.brtUpDown2.Enabled = this.illumCheckBox2.Checked;
            this.brtUpDown3.Enabled = this.illumCheckBox3.Checked;
            this.brtUpDown4.Enabled = this.illumCheckBox4.Checked;

            // 読取距離表示LED制御
            this.disComboBox.SelectedIndex = this._scanItem.DistanceLED == 0 ? 0 : 1;

            // 3D照明凸凹設定
            switch (this._scanItem.Illuminaton3D)
            {
                case 1:
                    this.illum3dComboBox.SelectedIndex = 0;
                    break;
                case 2:
                    this.illum3dComboBox.SelectedIndex = 1;
                    break;
                case 3:
                    this.illum3dComboBox.SelectedIndex = 2;
                    break;
                default:
                    this.illum3dComboBox.SelectedIndex = 0;
                    break;
            }
        }

        /// <summary>
        /// 照明パターンの変更時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void illumCheckBox_Changed(object sender, EventArgs e)
        {
            this.brtUpDown1.Enabled = this.illumCheckBox1.Checked;
            this.brtUpDown2.Enabled = this.illumCheckBox2.Checked;
            this.brtUpDown3.Enabled = this.illumCheckBox3.Checked;
            this.brtUpDown4.Enabled = this.illumCheckBox4.Checked;

            this.rowCheckBox1.Enabled = this.illumCheckBox3.Checked;
            this.rowCheckBox2.Enabled = this.illumCheckBox3.Checked;
            this.rowCheckBox3.Enabled = this.illumCheckBox3.Checked;
            this.rowCheckBox4.Enabled = this.illumCheckBox3.Checked;

            this.illum3dComboBox.Enabled = this.illumCheckBox4.Checked;
        }
    }
}
