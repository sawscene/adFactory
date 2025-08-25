//********************************************************************
//  FileName : ScanControl.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System;
using System.Linq;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using CodeScanApp.Common;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;

namespace CodeScanApp
{
    /// <summary>
    /// 読取コントロール
    /// </summary>
    public partial class ScanControl : UserControl
    {	
        [DllImport("coredll.dll", EntryPoint = "DeleteObject")]
		public static extern bool DeleteObject(IntPtr hObject);

        /// <summary>
        /// デバイス間通信
        /// </summary>
        private DeviceComm _deviceComm;

        /// <summary>
        /// メッセージウインドウ
        /// </summary>
		private MsgWindow _msgWindow;

        /// <summary>
        /// 読取項目一覧
        /// </summary>
        private List<TextBox> _textBoxList;
        public List<TextBox> TextBoxList
        { 
            get { return this._textBoxList; }
        }
        
        /// <summary>
        /// 現在の読取項目(インデックス)
        /// </summary>
        public int Selected { get; set; }

        /// <summary>
        /// 読取パターン
        /// </summary>
        private Pattern _pattern;
       
        /// <summary>
        /// スレッド
        /// </summary>
        private Thread _thread;

        /// <summary>
        /// 読取中フラグ
        /// </summary>
        private bool _isScanning;

        /// <summary>
        /// コンストラクター
        /// </summary>
        public ScanControl()
        {
            this.InitializeComponent();

            this._deviceComm = new DeviceComm();
			this._msgWindow = new MsgWindow(this._deviceComm);
            this._textBoxList = new List<TextBox>();
            this._isScanning = false;
        }

        /// <summary>
        /// 読取コントロールを初期化する
        /// </summary>
        /// <param name="modelName">機種名</param>
        /// <param name="workName">工程名</param>
        public void setUpControl(string modelName, string workName)
        {
            this.panel.Controls.Clear();
            this._textBoxList.Clear();
            this.Selected = 0;
            this._isScanning = false;

            this._pattern = Config.Patterns.Find(p => p.ModelName == modelName && p.WorkName == workName);
            if (this._pattern == null) {
                return;
            }

            this._msgWindow.Pattern = this._pattern;
            this.patternLabel.Text = this._pattern.Name;

            Font boldFont = new Font("Tahoma", hs.toFontSize(14), FontStyle.Bold);
            Font regularFont = new Font("Tahoma", hs.toFontSize(18), FontStyle.Regular);
            int width = System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width - 30;

            int num = 0;
            foreach  (ScanItem item in this._pattern.Items)
            {
                Label label = new Label();
                label.Text = item.Name;
                label.Location = hs.isVGAScreen() ? new Point(2, 104 * num) : new Point(2, 62 * num);
                label.Size = hs.isVGAScreen() ? new Size(width, 40) : new Size(width, 24);
                label.Font = boldFont;

                TextBox textBox = new TextBox();
                textBox.Name = item.Name;
                textBox.Tag = item;
                textBox.Location = hs.isVGAScreen() ? new Point(2, 104 * num + 40) : new Point(2, 62 * num + 24);
                textBox.Size = hs.isVGAScreen() ? new Size(width, 64) : new Size(width, 36);
                textBox.Font = regularFont;
                textBox.ReadOnly = true;
                textBox.GotFocus += new EventHandler(TextBox_GotFocus);
                textBox.LostFocus += new EventHandler(TextBox_LostFocus);
                textBox.BackColor = Color.White;

                this._textBoxList.Add(textBox);

                this.panel.Controls.Add(label);
                this.panel.Controls.Add(textBox);

                num++;
            }

            this.updateControl();

            this._deviceComm.Open();
        }
        
        /// <summary>
        /// フォーカスを受け取った時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void TextBox_GotFocus(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox) sender;
            textBox.BackColor = Color.Yellow;
            int index = _textBoxList.IndexOf(textBox);

            if (index == this.Selected && this._isScanning)
            {
                return;
            }

            this.Selected = index;
            ScanItem item = (ScanItem) textBox.Tag;
           
            Int32 ret = Bt.ScanLib.Control.btScanDisable();
            if (Bt.LibDef.BT_OK != ret)
            {
                Utils.PopupAlert("btScanDisable", ret);
            }

            // OCR設定ファイルを適用
            string iniFile = Config.DefaultOCRSetting;
            if (!String.IsNullOrEmpty(item.IniFile) && File.Exists(item.IniFile))
            {
                iniFile = item.IniFile;
            }

            ret = Bt.ScanLib.Setting.btScanLoadOCRConfig(new StringBuilder(iniFile));
            if (Bt.LibDef.BT_OK != ret)
            {
                Utils.PopupAlert("btScanLoadOCRConfig ", ret);
                this.returnButton.Focus();
                return;
            }

            // 照明制御を取得
            IntPtr pValue = new IntPtr();
            Int32 defValue = 0;
            pValue = Marshal.AllocCoTaskMem(Marshal.SizeOf(defValue));
            ret = Bt.ScanLib.Setting.btScanGetProperty(Bt.LibDef.BT_SCAN_PROP_ILLUMINATION, pValue);
            Int32 led = Marshal.ReadInt32(pValue);
            Marshal.FreeCoTaskMem(pValue);
            if (Bt.LibDef.BT_OK != ret)
            {
                Utils.PopupAlert("btScanGetProperty (BT_SCAN_PROP_ILLUMINATION)", ret);
            }
            
            if (led != item.LED)
            {
                // 照明制御を設定
                pValue = Marshal.AllocCoTaskMem(Marshal.SizeOf(defValue));
                Marshal.WriteInt32(pValue, item.LED);
                ret =  Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_ILLUMINATION, pValue);
                Marshal.FreeCoTaskMem(pValue);
                if (Bt.LibDef.BT_OK != ret)
                {
                    Utils.PopupAlert("btScanSetProperty (BT_SCAN_PROP_ILLUMINATION)", ret);
                }
            }
          
            if (this.hs.HandyType == HandyScanner.HT_BT_W370)
            {
                pValue = Marshal.AllocCoTaskMem(Marshal.SizeOf(defValue));

                // 読み取り中ライブビューを無効にする
                Int32 data = (Int32)this._pattern.LiveView;
                IntPtr pValueSet = Marshal.AllocCoTaskMem(Marshal.SizeOf(data));
		        Marshal.WriteInt32(pValueSet, (Int32)data);
		        ret = Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_LIVEVIEW_DECODING, pValueSet);
		        Marshal.FreeCoTaskMem(pValueSet);
		        if (ret != Bt.LibDef.BT_OK)
		        {
                    Utils.PopupAlert("btScanSetProperty ", ret);
                    return;
		        }

                // 照明パターンを設定
                Marshal.WriteInt32(pValue, item.Illuminaton);
                ret = Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_EXT_ILLUMINATION, pValue);
                if (Bt.LibDef.BT_OK != ret)
                {
                    Utils.PopupAlert("btScanSetProperty (BT_SCAN_PROP_EXT_ILLUMINATION) ", ret);
                }

                // ターゲット輝度設定を設定
                Bt.LibDef.BT_SCAN_EXT_LIGHT_BRIGHTNESS brightness = new Bt.LibDef.BT_SCAN_EXT_LIGHT_BRIGHTNESS();
                brightness.BrightnessNolight = 128;
                brightness.BrightnessNormal = (short)item.Brightness[0];
                brightness.BrightnessMulti = (short)item.Brightness[1];
                brightness.BrightnessLow = (short)item.Brightness[2];
                brightness.Brightness3D = (short)item.Brightness[3];
                brightness.Area = 1;

                IntPtr pBrightness = Marshal.AllocCoTaskMem(Marshal.SizeOf(brightness));
                Marshal.StructureToPtr(brightness, pBrightness, false);
                ret =  Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_TARGET_BRIGHTNESS, pBrightness);
                if (Bt.LibDef.BT_OK != ret)
                {
                    Utils.PopupAlert("btScanSetProperty (BT_SCAN_PROP_TARGET_BRIGHTNESS) ", ret);
                }
                Marshal.FreeCoTaskMem(pBrightness);

                // 読み取り距離表示LED制御を設定
                Marshal.WriteInt32(pValue, item.DistanceLED);
                ret =  Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_DISTANCE_LED, pValue);
                if (Bt.LibDef.BT_OK != ret)
                {
                    Utils.PopupAlert("btScanSetProperty (BT_SCAN_PROP_DISTANCE_LED)", ret);
                }
                
                if ((item.Illuminaton & 4) == 4 && item.LowDirection != 0) {
                    // ローアングル照明方向を設定
                    Marshal.WriteInt32(pValue, item.LowDirection);
                    ret =  Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_EXT_LOW_DIRECTION, pValue);
                    if (Bt.LibDef.BT_OK != ret)
                    {
                        Utils.PopupAlert("btScanSetProperty (BT_SCAN_PROP_EXT_LOW_DIRECTION)", ret);
                    }
                }

                if ((item.Illuminaton & 8) == 8) {
                    // 3D 照明 凹凸設定を設定
                    Marshal.WriteInt32(pValue, item.Illuminaton3D);
                    ret =  Bt.ScanLib.Setting.btScanSetProperty(Bt.LibDef.BT_SCAN_PROP_3D_ILLUMINATION, pValue);
                    if (Bt.LibDef.BT_OK != ret)
                    {
                        Utils.PopupAlert("btScanSetProperty (BT_SCAN_PROP_3D_ILLUMINATION)", ret);
                    }
                }
                
                Marshal.FreeCoTaskMem(pValue);
            }

            // 読取開始
			ret = Bt.ScanLib.Control.btScanEnable();
			if (Bt.LibDef.BT_OK != ret)
	        {
                Utils.PopupAlert("btScanEnable ", ret);
                this.returnButton.Focus();
                return;
			}

            this._isScanning = true;

            if (this.hs.HandyType == HandyScanner.HT_BT_W370)
            {
                ret = Bt.ScanLib.Control.btScanMarker(item.Marker, item.CameraLED);
                if (Bt.LibDef.BT_OK != ret)
                {
                    Utils.PopupAlert("btScanMarker ", ret);
                }

            } else {

                if (this._pattern.LiveView == 1)
                {
                    this._thread = new Thread(new ThreadStart(liveView));
                    this._thread.Start();
                }
            }
        }

        /// <summary>
        /// コントロールを更新する。
        /// </summary>
        public void updateControl()
        {
            this.SuspendLayout();

            if (this._pattern.LiveView == 1)
            {
                this.pictureBox.Visible = true;

                // Size viewSize = hs.isVGAScreen() ? new Size(400, 300) : new Size(160, 120);
                Size viewSize = hs.isVGAScreen() ? new Size(400, 260) : new Size(160, 120);

                this.patternLabel.Location = new Point(2, 2 + viewSize.Height);
                this.panel.Location = new Point(2, hs.isVGAScreen() ? 48 + viewSize.Height : 24 + viewSize.Height);
                this.panel.Size = hs.isVGAScreen() ? new Size(System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width - 6, 180) : new Size(238, 80);
            }
            else
            {
                this.pictureBox.Visible = false;

                this.patternLabel.Location = new System.Drawing.Point(2, 2);
                this.panel.Location = new Point(2, hs.isVGAScreen() ? 48 : 24);
                this.panel.Size = hs.isVGAScreen() ? new Size(System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width - 6, 390) : new Size(238, 180);
            }

            this.ResumeLayout(false);

            if (_textBoxList.Count > Selected)
            {
                _textBoxList[Selected].Focus();
            }
        }

        /// <summary>
        /// フォーカスを失った時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void TextBox_LostFocus(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox) sender;
            textBox.BackColor = Color.White;
        }

        /// <summary>
        /// 戻るボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void returnButton_Click(object sender, EventArgs e)
        {
            if (this.hs.HandyType == HandyScanner.HT_BT_W370)
            {
                // BT-W370の場合、撮像ライブビューを強制的に終了するため
                Bt.ScanLib.Control.btScanSoftTrigger(1);
                Bt.ScanLib.Control.btScanSoftTrigger(0);
            }

            Int32 ret = Bt.ScanLib.Control.btScanDisable();
            //if (Bt.LibDef.BT_OK != ret)
            //{
            //    Utils.PopupAlert("btScanDisable", ret);
            //}
                
            this._deviceComm.Close();
            
            MainForm.showWork();
        }

        /// <summary>
        /// リセットボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void resetButton_Click(object sender, EventArgs e)
        {
            this.reset();
        }

        /// <summary>
        /// 読取設定画面を表示する
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void configButton_Click(object sender, EventArgs e)
        {
            if (this.hs.HandyType == HandyScanner.HT_BT_W370)
            {
                // BT-W370の場合、撮像ライブビューを強制的に終了するため
                Bt.ScanLib.Control.btScanSoftTrigger(1);
                Bt.ScanLib.Control.btScanSoftTrigger(0);
            }
            
            Int32 ret = Bt.ScanLib.Control.btScanDisable();
            //if (Bt.LibDef.BT_OK != ret)
            //{
            //    Utils.PopupAlert("btScanDisable", ret);
            //}

            TextBox textBox = this.TextBoxList[this.Selected];
            ScanItem scanItem = (ScanItem) textBox.Tag;
            MainForm.scanConfigCtrl.setUpControl(this._pattern, scanItem); 
            MainForm.showScanConfig();
        }

        //---------------------------------------------------------------------------------
	    // メッセージウインドウ
        //---------------------------------------------------------------------------------
	    public class MsgWindow : Microsoft.WindowsCE.Forms.MessageWindow
	    {
            /// <summary>
            /// デバイス間通信
            /// </summary>
            private DeviceComm _deviceComm;

            /// <summary>
            /// 読取パターン
            /// </summary>
            public Pattern Pattern { get; set; }

            /// <summary>
            /// コンストラクタ
            /// </summary>
		    public MsgWindow(DeviceComm deviceComm)
		    {
                this._deviceComm = deviceComm;
		    }

            /// <summary>
            /// ウィンドウプロシージャ
            /// </summary>
            /// <param name="msg">ウィンドウメッセージ</param>
		    protected override void WndProc(ref Microsoft.WindowsCE.Forms.Message msg)
		    {
			    switch (msg.Msg)
			    {
				    case (Int32) Bt.LibDef.WM_BT_SCAN:
                        MainForm.scanCtrl._isScanning = false;

					    if (msg.WParam.ToInt32() == (Int32)Bt.LibDef.BTMSG_WPARAM.WP_SCN_SUCCESS)
					    {
						    // 読み取り成功の場合
                            ScanCode();
					    }
                        else if (msg.WParam.ToInt32() != (Int32)Bt.LibDef.BTMSG_WPARAM.WP_SCN_CANCEL)
                        {
                            MainForm.scanCtrl.pictureBox.Focus();
                            if (MainForm.scanCtrl.TextBoxList.Count > MainForm.scanCtrl.Selected)
                            {
                                TextBox next = MainForm.scanCtrl.TextBoxList[MainForm.scanCtrl.Selected];
                                next.Focus();
                            }
                        }
					    break;
                    
			    }
			    base.WndProc(ref msg);
		    }

            /// <summary>
            /// 読取ったコードを取得する
            /// </summary>
            public void ScanCode()
			{
				Int32 ret = 0;
                string code = "";

				//Bt.LibDef.BT_SCAN_REPORT report = new Bt.LibDef.BT_SCAN_REPORT();
				//Bt.LibDef.BT_SCAN_QR_REPORT qrReport = new Bt.LibDef.BT_SCAN_QR_REPORT();

                try
                {
                    // 一括読取
					Int32 codeLen = Bt.ScanLib.Control.btScanGetStringSize();
					if (codeLen <= 0)
					{
                        Utils.PopupAlert("btScanGetStringSize", codeLen);
						return;
					}

					Byte[] data = new Byte[codeLen];
                    UInt16 symbol = 0;

					ret = Bt.ScanLib.Control.btScanGetString(data, ref symbol);
					if (ret != Bt.LibDef.BT_OK)
					{
                        Utils.PopupAlert("btScanGetString", ret);
						return;
					}

					code = System.Text.Encoding.GetEncoding(932).GetString(data, 0, codeLen);

                    TextBox textBox = MainForm.scanCtrl.TextBoxList[MainForm.scanCtrl.Selected];
                    ScanItem scanItem = (ScanItem) textBox.Tag;

                    if (scanItem.IsPartsID)
                    {
                        string[] values = code.Split(' ');
                        if (!(values.Length >= 2 && String.Equals(values[0],  MainForm.scanCtrl._pattern.ModelName)))
                        {
                            Utils.PopupWarn("読み取ったラベルの機種名が一致しません");
						    return;
                        }

                    } else {
                                                          
                        // 区切り文字
                        if (!String.IsNullOrEmpty(scanItem.Delimiter) && !String.Equals(scanItem.Delimiter, "-1"))
                        {
                            string[] values = code.Split(scanItem.Delimiter[0]);

                            // 区切り位置
                            if (0 < scanItem.Section && values.Length > (scanItem.Section - 1))
                            {
                                code = values[scanItem.Section - 1];
                            }
                        }
                        
                        // 開始位置
                        if (0 < scanItem.Frist && code.Length > (scanItem.Frist - 1))
                        {
                           code = code.Substring(scanItem.Frist - 1);
                        }

                        // 文字列長
                        if (0 < scanItem.Length && code.Length >= scanItem.Length)
                        {
                           code = code.Substring(0, scanItem.Length);
                        }
                    }

                    textBox.Text = code;

                    // 送信データフォーマットに整形
                    string sendData = "";
                    if (!String.IsNullOrEmpty(scanItem.Tag))
                    {
                        sendData = String.Format(Constants.SEND_FORMAT, scanItem.Tag, code, this.Pattern.ModelName);
                    }
                    else
                    {
                        sendData = code + "\r";
                    }

                    // 送信データをPCに送信
                    if (!this._deviceComm.Send(sendData)) {
                        if (MainForm.scanCtrl.hs.HandyType == HandyScanner.HT_BT_W370)
                        {
                            // BT-W370の場合、撮像ライブビューを強制的に終了するため
                            Bt.ScanLib.Control.btScanSoftTrigger(1);
                            Bt.ScanLib.Control.btScanSoftTrigger(0);
                        }
                    }
                    
                    // 読取終了
                    ret = Bt.ScanLib.Control.btScanDisable();
                    if (Bt.LibDef.BT_OK != ret)
                    {
                        Utils.PopupAlert("btScanDisable", ret);
                    }
                    
                    if (MainForm.scanCtrl.TextBoxList.Count > MainForm.scanCtrl.Selected + 1)
                    {
                        MainForm.scanCtrl.Selected++;
                        TextBox next = MainForm.scanCtrl.TextBoxList[MainForm.scanCtrl.Selected];
                        next.Focus();
                    }
                    else 
                    {
                        for (int index = 0; index < MainForm.scanCtrl.TextBoxList.Count(); index++)
                        {
                            TextBox next = MainForm.scanCtrl.TextBoxList[index];
                            if (String.IsNullOrEmpty(next.Text))
                            {
                                // 未読取の項目
                                MainForm.scanCtrl.Selected = index;
                                next.Focus();
                                return;
                            }
                        }

                        // 最後まで到達
                        MainForm.scanCtrl.reset();
                    }
                }
                catch (MissingMethodException)
                {
                    MessageBox.Show("本端末では使用できないAPIを含んでいます。", "エラー");
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.ToString(), "エラー");
                }
			}
        }

        /// <summary>
        /// 読取画面をリセットする。
        /// </summary>
        private void reset()
        {
            this._isScanning = false;

            for (int index = 0; index < MainForm.scanCtrl.TextBoxList.Count(); index++)
            {
                TextBox textBox = MainForm.scanCtrl.TextBoxList[index];
                textBox.Text = "";
            }

            this.resetButton.Focus();

            MainForm.scanCtrl.Selected = 0;
            TextBox next = MainForm.scanCtrl.TextBoxList[MainForm.scanCtrl.Selected];
            next.Focus();
        }
    
        /// <summary>
        /// 撮像ライブビュー
        /// </summary>
        private void liveView()
        {
            Int32 ret;

     		Bt.LibDef.RECT rect = new Bt.LibDef.RECT();
			IntPtr pRect = Marshal.AllocCoTaskMem(Marshal.SizeOf(rect));
			IntPtr pBitmap = new IntPtr();
            
            while (true)
			{
				ret = Bt.ScanLib.Utility.btImageLive(ref pBitmap, pRect);
				if (ret != Bt.LibDef.BT_OK)
				{
                   break;
				}

                Bitmap bmp = Image.FromHbitmap(pBitmap);

                Graphics gx = Graphics.FromImage(bmp);
                Point[] points = { new Point(bmp.Width / 2 - 300 , bmp.Height / 2),  new Point(bmp.Width / 2 + 300, bmp.Height / 2) };
                gx.DrawLines(new Pen(Color.White, 10), points);
                gx.Dispose();

                this.Invoke(new Action(delegate
                {
                  	this.pictureBox.Image = bmp;
				    this.pictureBox.Refresh();

                }));

                DeleteObject(pBitmap);

				GC.Collect();
            }

            return;
        }
    }
}
