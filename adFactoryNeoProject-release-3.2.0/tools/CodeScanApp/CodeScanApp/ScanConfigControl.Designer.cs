namespace CodeScanApp
{
    partial class ScanConfigControl
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
            this.okButton = new System.Windows.Forms.Button();
            this.viewCheckBox = new System.Windows.Forms.CheckBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.markarCheckBox = new System.Windows.Forms.CheckBox();
            this.cameraCheckBox = new System.Windows.Forms.CheckBox();
            this.illumCheckBox1 = new System.Windows.Forms.CheckBox();
            this.illumCheckBox2 = new System.Windows.Forms.CheckBox();
            this.illumCheckBox3 = new System.Windows.Forms.CheckBox();
            this.illumCheckBox4 = new System.Windows.Forms.CheckBox();
            this.disComboBox = new System.Windows.Forms.ComboBox();
            this.rowCheckBox1 = new System.Windows.Forms.CheckBox();
            this.rowCheckBox2 = new System.Windows.Forms.CheckBox();
            this.rowCheckBox3 = new System.Windows.Forms.CheckBox();
            this.rowCheckBox4 = new System.Windows.Forms.CheckBox();
            this.illum3dComboBox = new System.Windows.Forms.ComboBox();
            this.ledCheckBox = new System.Windows.Forms.CheckBox();
            this.brtUpDown1 = new System.Windows.Forms.NumericUpDown();
            this.brtUpDown2 = new System.Windows.Forms.NumericUpDown();
            this.brtUpDown3 = new System.Windows.Forms.NumericUpDown();
            this.brtUpDown4 = new System.Windows.Forms.NumericUpDown();
            this.SuspendLayout();
            // 
            // okButton
            // 
            this.okButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.okButton.Font = new System.Drawing.Font("Tahoma", 20F, System.Drawing.FontStyle.Bold);
            this.okButton.Location = new System.Drawing.Point(4, 545);
            this.okButton.Name = "okButton";
            this.okButton.Size = new System.Drawing.Size(100, 40);
            this.okButton.TabIndex = 0;
            this.okButton.Text = "戻る";
            this.okButton.Click += new System.EventHandler(this.okButton_Click);
            // 
            // viewCheckBox
            // 
            this.viewCheckBox.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.viewCheckBox.Location = new System.Drawing.Point(4, 20);
            this.viewCheckBox.Name = "viewCheckBox";
            this.viewCheckBox.Size = new System.Drawing.Size(230, 20);
            this.viewCheckBox.TabIndex = 1;
            this.viewCheckBox.Text = "ライブビュー表示";
            // 
            // label1
            // 
            this.label1.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.label1.Location = new System.Drawing.Point(4, 180);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(220, 24);
            this.label1.Text = "照明パターン・輝度";
            // 
            // label3
            // 
            this.label3.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.label3.Location = new System.Drawing.Point(4, 500);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(280, 24);
            this.label3.Text = "読取距離表示LED制御";
            // 
            // label4
            // 
            this.label4.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.label4.Location = new System.Drawing.Point(4, 375);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(220, 24);
            this.label4.Text = "ローアングル照明方向";
            // 
            // label5
            // 
            this.label5.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.label5.Location = new System.Drawing.Point(4, 455);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(220, 24);
            this.label5.Text = "3D照明 凹凸設定";
            // 
            // markarCheckBox
            // 
            this.markarCheckBox.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.markarCheckBox.Location = new System.Drawing.Point(4, 95);
            this.markarCheckBox.Name = "markarCheckBox";
            this.markarCheckBox.Size = new System.Drawing.Size(220, 24);
            this.markarCheckBox.TabIndex = 7;
            this.markarCheckBox.Text = "マーカー表示";
            // 
            // cameraCheckBox
            // 
            this.cameraCheckBox.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.cameraCheckBox.Location = new System.Drawing.Point(4, 135);
            this.cameraCheckBox.Name = "cameraCheckBox";
            this.cameraCheckBox.Size = new System.Drawing.Size(220, 24);
            this.cameraCheckBox.TabIndex = 13;
            this.cameraCheckBox.Text = "カメラ照明";
            // 
            // illumCheckBox1
            // 
            this.illumCheckBox1.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.illumCheckBox1.Location = new System.Drawing.Point(20, 215);
            this.illumCheckBox1.Name = "illumCheckBox1";
            this.illumCheckBox1.Size = new System.Drawing.Size(100, 20);
            this.illumCheckBox1.TabIndex = 14;
            this.illumCheckBox1.Text = "1:内部";
            this.illumCheckBox1.CheckStateChanged += new System.EventHandler(this.illumCheckBox_Changed);
            // 
            // illumCheckBox2
            // 
            this.illumCheckBox2.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.illumCheckBox2.Location = new System.Drawing.Point(20, 255);
            this.illumCheckBox2.Name = "illumCheckBox2";
            this.illumCheckBox2.Size = new System.Drawing.Size(120, 20);
            this.illumCheckBox2.TabIndex = 15;
            this.illumCheckBox2.Text = "2:マルチ";
            this.illumCheckBox2.CheckStateChanged += new System.EventHandler(this.illumCheckBox_Changed);
            // 
            // illumCheckBox3
            // 
            this.illumCheckBox3.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.illumCheckBox3.Location = new System.Drawing.Point(20, 295);
            this.illumCheckBox3.Name = "illumCheckBox3";
            this.illumCheckBox3.Size = new System.Drawing.Size(150, 20);
            this.illumCheckBox3.TabIndex = 16;
            this.illumCheckBox3.Text = "4:ローアングル";
            this.illumCheckBox3.CheckStateChanged += new System.EventHandler(this.illumCheckBox_Changed);
            // 
            // illumCheckBox4
            // 
            this.illumCheckBox4.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.illumCheckBox4.Location = new System.Drawing.Point(20, 335);
            this.illumCheckBox4.Name = "illumCheckBox4";
            this.illumCheckBox4.Size = new System.Drawing.Size(80, 20);
            this.illumCheckBox4.TabIndex = 17;
            this.illumCheckBox4.Text = "8:3D";
            this.illumCheckBox4.CheckStateChanged += new System.EventHandler(this.illumCheckBox_Changed);
            // 
            // disComboBox
            // 
            this.disComboBox.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.disComboBox.Items.Add("0:LED点灯しない");
            this.disComboBox.Items.Add("1:LED点灯する");
            this.disComboBox.Location = new System.Drawing.Point(274, 496);
            this.disComboBox.Name = "disComboBox";
            this.disComboBox.Size = new System.Drawing.Size(190, 32);
            this.disComboBox.TabIndex = 19;
            // 
            // rowCheckBox1
            // 
            this.rowCheckBox1.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.rowCheckBox1.Location = new System.Drawing.Point(20, 410);
            this.rowCheckBox1.Name = "rowCheckBox1";
            this.rowCheckBox1.Size = new System.Drawing.Size(100, 24);
            this.rowCheckBox1.TabIndex = 20;
            this.rowCheckBox1.Text = "1:上";
            // 
            // rowCheckBox2
            // 
            this.rowCheckBox2.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.rowCheckBox2.Location = new System.Drawing.Point(110, 410);
            this.rowCheckBox2.Name = "rowCheckBox2";
            this.rowCheckBox2.Size = new System.Drawing.Size(100, 24);
            this.rowCheckBox2.TabIndex = 21;
            this.rowCheckBox2.Text = "2:右";
            // 
            // rowCheckBox3
            // 
            this.rowCheckBox3.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.rowCheckBox3.Location = new System.Drawing.Point(200, 410);
            this.rowCheckBox3.Name = "rowCheckBox3";
            this.rowCheckBox3.Size = new System.Drawing.Size(100, 24);
            this.rowCheckBox3.TabIndex = 22;
            this.rowCheckBox3.Text = "4:左";
            // 
            // rowCheckBox4
            // 
            this.rowCheckBox4.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.rowCheckBox4.Location = new System.Drawing.Point(295, 410);
            this.rowCheckBox4.Name = "rowCheckBox4";
            this.rowCheckBox4.Size = new System.Drawing.Size(100, 24);
            this.rowCheckBox4.TabIndex = 23;
            this.rowCheckBox4.Text = "8:下";
            // 
            // illum3dComboBox
            // 
            this.illum3dComboBox.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.illum3dComboBox.Items.Add("1:凹");
            this.illum3dComboBox.Items.Add("2:凸");
            this.illum3dComboBox.Items.Add("3凸凹");
            this.illum3dComboBox.Location = new System.Drawing.Point(274, 451);
            this.illum3dComboBox.Name = "illum3dComboBox";
            this.illum3dComboBox.Size = new System.Drawing.Size(190, 32);
            this.illum3dComboBox.TabIndex = 19;
            // 
            // ledCheckBox
            // 
            this.ledCheckBox.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Bold);
            this.ledCheckBox.Location = new System.Drawing.Point(4, 60);
            this.ledCheckBox.Name = "ledCheckBox";
            this.ledCheckBox.Size = new System.Drawing.Size(220, 20);
            this.ledCheckBox.TabIndex = 29;
            this.ledCheckBox.Text = "照明制御";
            // 
            // brtUpDown1
            // 
            this.brtUpDown1.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.brtUpDown1.Increment = new decimal(new int[] {
            10,
            0,
            0,
            0});
            this.brtUpDown1.Location = new System.Drawing.Point(274, 207);
            this.brtUpDown1.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.brtUpDown1.Name = "brtUpDown1";
            this.brtUpDown1.Size = new System.Drawing.Size(190, 33);
            this.brtUpDown1.TabIndex = 36;
            // 
            // brtUpDown2
            // 
            this.brtUpDown2.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.brtUpDown2.Location = new System.Drawing.Point(274, 249);
            this.brtUpDown2.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.brtUpDown2.Name = "brtUpDown2";
            this.brtUpDown2.Size = new System.Drawing.Size(190, 33);
            this.brtUpDown2.TabIndex = 36;
            // 
            // brtUpDown3
            // 
            this.brtUpDown3.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.brtUpDown3.Location = new System.Drawing.Point(274, 289);
            this.brtUpDown3.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.brtUpDown3.Name = "brtUpDown3";
            this.brtUpDown3.Size = new System.Drawing.Size(190, 33);
            this.brtUpDown3.TabIndex = 36;
            // 
            // brtUpDown4
            // 
            this.brtUpDown4.Font = new System.Drawing.Font("Tahoma", 16F, System.Drawing.FontStyle.Regular);
            this.brtUpDown4.Location = new System.Drawing.Point(274, 329);
            this.brtUpDown4.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.brtUpDown4.Name = "brtUpDown4";
            this.brtUpDown4.Size = new System.Drawing.Size(190, 33);
            this.brtUpDown4.TabIndex = 36;
            // 
            // ScanConfigControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.Controls.Add(this.brtUpDown4);
            this.Controls.Add(this.brtUpDown3);
            this.Controls.Add(this.brtUpDown2);
            this.Controls.Add(this.brtUpDown1);
            this.Controls.Add(this.ledCheckBox);
            this.Controls.Add(this.rowCheckBox4);
            this.Controls.Add(this.rowCheckBox3);
            this.Controls.Add(this.rowCheckBox2);
            this.Controls.Add(this.rowCheckBox1);
            this.Controls.Add(this.illum3dComboBox);
            this.Controls.Add(this.disComboBox);
            this.Controls.Add(this.illumCheckBox4);
            this.Controls.Add(this.illumCheckBox3);
            this.Controls.Add(this.illumCheckBox2);
            this.Controls.Add(this.illumCheckBox1);
            this.Controls.Add(this.cameraCheckBox);
            this.Controls.Add(this.markarCheckBox);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.viewCheckBox);
            this.Controls.Add(this.okButton);
            this.Name = "ScanConfigControl";
            this.Size = new System.Drawing.Size(476, 588);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button okButton;
        private System.Windows.Forms.CheckBox viewCheckBox;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.CheckBox markarCheckBox;
        private System.Windows.Forms.CheckBox cameraCheckBox;
        private System.Windows.Forms.CheckBox illumCheckBox1;
        private System.Windows.Forms.CheckBox illumCheckBox2;
        private System.Windows.Forms.CheckBox illumCheckBox3;
        private System.Windows.Forms.CheckBox illumCheckBox4;
        private System.Windows.Forms.ComboBox disComboBox;
        private System.Windows.Forms.CheckBox rowCheckBox1;
        private System.Windows.Forms.CheckBox rowCheckBox2;
        private System.Windows.Forms.CheckBox rowCheckBox3;
        private System.Windows.Forms.CheckBox rowCheckBox4;
        private System.Windows.Forms.ComboBox illum3dComboBox;
        private System.Windows.Forms.CheckBox ledCheckBox;
        private System.Windows.Forms.NumericUpDown brtUpDown1;
        private System.Windows.Forms.NumericUpDown brtUpDown2;
        private System.Windows.Forms.NumericUpDown brtUpDown3;
        private System.Windows.Forms.NumericUpDown brtUpDown4;
    }
}
