//********************************************************************
//  FileName : ConfigControl.cs
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
using System.Threading;

namespace CodeScanApp
{
    public partial class ConfigControl : UserControl
    {
        /// <summary>
        /// デバイス間通信
        /// </summary>
        private DeviceComm _deviceComm;
        
        /// <summary>
        /// コンストラクタ
        /// </summary>
        public ConfigControl()
        {
            this.InitializeComponent();

            this._deviceComm = new DeviceComm();
        }
        
        /// <summary>
        /// ホスト名選択時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void hostListBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            string hostName = (string) this.hostListBox.SelectedItem;
            if (!String.IsNullOrEmpty(hostName))
            {
                this.hostTextBox.Text = hostName;
            }
        }
        
        /// <summary>
        /// デバイス名選択時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void deviceListBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            string deviceName = (string) this.deviceListBox.SelectedItem;
            if (null == deviceName)
            {
                return;
            }

            string[] values = deviceName.Split(':');
            if (2 == values.Length)
            {
                Bt.LibDef.BT_BLUETOOTH_TARGET host = new Bt.LibDef.BT_BLUETOOTH_TARGET();
                host.name = values[0];
                host.addr = values[1];
                Config.BluetoothHost = host;
            }
        }

        /// <summary>
        /// 戻るボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void returnButton_Click(object sender, EventArgs e)
        {
            try
            {
                // Bluetoothデバイスの問い合わせを停止する
                //Int32 ret = Bt.CommLib.Bluetooth.btBluetoothCancelInquiry();
            }
            catch(Exception)
            {
            }
            finally
            {
                this._deviceComm.Close();
                
                Config.SaveConfig();
                MainForm.showModel();
            }
        }

        /// <summary>
        /// 設定画面を更新する。
        /// </summary>
        public void update()
        {
            try 
            {
                Int32 ret = 0;

                if (0 == Config.ConnectType)
                {
                    this.wlanButton.Checked = true;
                }
                else
                {
                    this.bluetoothButton.Checked = true;
                }

                this.hostTextBox.Text = Config.WLANHost;

                this.hostListBox.Items.Clear();
                this.deviceListBox.Items.Clear();

                foreach (String host in Config.WLANHistory)
                {
                    this.hostListBox.Items.Add(host);
                }

                this._deviceComm.Open();

                UInt32 devNum = 0;
                
                // ペアリング済みデバイス数を取得
                ret = Bt.CommLib.Bluetooth.btBluetoothGetPairDevice(ref devNum);
                if (Bt.LibDef.BT_ERR_COMM_NOTOPEN == ret)
                {
                    this.bluetoothButton.Enabled = false;
                    this.deviceListBox.Enabled = false;
                    goto END;
                }

                if (Bt.LibDef.BT_OK != ret)
                {
                    Utils.PopupAlert("btBluetoothGetPairDevice", ret);
                    goto END;
                }

                if (0 >= devNum)
                {
                    goto END;
                }

			    // ペアリング済みデバイスを取得
			    Bt.LibDef.BT_BLUETOOTH_TARGET device = new Bt.LibDef.BT_BLUETOOTH_TARGET();
			    for (UInt32 index = 0; index < devNum; index++)
			    {
				    ret = Bt.CommLib.Bluetooth.btBluetoothGetPairDeviceResult(index, ref device);
				    if (Bt.LibDef.BT_OK != ret)
				    {
                        Utils.PopupAlert("btBluetoothGetPairDeviceResult", ret);
					    goto END;
				    }

                    string deviceName = String.Format("{0}:{1}", device.name, device.addr);
				    this.deviceListBox.Items.Add(deviceName);
			    }

            END:
                if (!String.IsNullOrEmpty(Config.BluetoothHost.addr)) {
                    string currentDevice = String.Format("{0}:{1}", Config.BluetoothHost.name, Config.BluetoothHost.addr);
                    this.deviceListBox.SelectedItem = currentDevice;
                }

                //this.Inquiry();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString(), "エラー");
            }
            finally
            {
            }
        }

        /// <summary>
        /// 無線LAN接続が選択された
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void wlanButton_CheckedChanged(object sender, EventArgs e)
        {
            Config.ConnectType = 0;
            this.hostTextBox.Enabled = true;
            this.deviceListBox.Enabled = false;
        }

        /// <summary>
        /// Bluetooth接続が選択された
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void bluetoothButton_CheckedChanged(object sender, EventArgs e)
        {
            Config.ConnectType = 1;
            this.hostTextBox.Enabled = false;
            this.deviceListBox.Enabled = true;
        }

        /// <summary>
        /// 削除ボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void removeButton_Click(object sender, EventArgs e)
        {
            try
            {
                string deviceName = (string) this.deviceListBox.SelectedItem;
                string[] values = deviceName.Split(':');
                if (2 == values.Length)
                {
                    Bt.LibDef.BT_BLUETOOTH_TARGET host = new Bt.LibDef.BT_BLUETOOTH_TARGET();
                    host.name = values[0];
                    host.addr = values[1];
                    if (DialogResult.Yes == MessageBox.Show("選択されたデバイスを削除します。よろしいですか?", "確認", MessageBoxButtons.YesNo, MessageBoxIcon.Question, MessageBoxDefaultButton.Button2))
                    {
                        Int32 ret = Bt.CommLib.Bluetooth.btBluetoothUnPairing(host);
                        if (Bt.LibDef.BT_OK == ret)
				        {   
                            if (String.Equals(Config.BluetoothHost.addr, host.addr))
                            {
                                Config.BluetoothHost = new Bt.LibDef.BT_BLUETOOTH_TARGET();
                                Config.SaveConfig();
                            }

                            this.deviceListBox.Items.Remove(deviceName);

                            if (0 < this.deviceListBox.Items.Count)
                            {
                                this.deviceListBox.SelectedIndex = 0;
                            }
                        }
                        else
                        {
                            Utils.PopupAlert("btBluetoothUnPairing", ret);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Utils.PopupAlert("不明なエラーが発生しました。\n" + ex.Message);
            }
        }

        /// <summary>
        /// 接続ボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void testButton_Click(object sender, EventArgs e)
        {
            if (0 == Config.ConnectType)
            {
                if (string.IsNullOrEmpty(Config.WLANHost)) {
                    Utils.PopupWarn("タブレットのコンピュータ名を入力してください。");
                    return;
                }
            }
            else
            {
                if (0 == this.deviceListBox.Items.Count)
                {
                    Utils.PopupWarn("タブレットとの接続(ペアリング)を行ってください。");
                    return;
                }

                if (string.IsNullOrEmpty(Config.BluetoothHost.addr)) {
                    Utils.PopupWarn("デバイスを選択してください。");
                    return;
                }
            }

            try 
            {
                if (this._deviceComm.Test())
                {
                    Utils.PopupInfo("接続に成功しました。");

                    if (0 == Config.ConnectType) {
                        if (this.hostListBox.Items.Contains(Config.WLANHost))
                        {
                            this.hostListBox.Items.Remove(Config.WLANHost);
                        }
                        
                        this.hostListBox.Items.Insert(0, Config.WLANHost);

                        if (this.hostListBox.Items.Count > 10)
                        {
                            // 10件まで
                            this.hostListBox.Items.RemoveAt(this.hostListBox.Items.Count - 1);
                        }
                        
                        Config.WLANHistory.Clear();
                        foreach (Object item in this.hostListBox.Items)
                        {
                            Config.WLANHistory.Add(item.ToString());
                        }
                    }
                }
                else
                {
                    Utils.PopupWarn("接続に失敗しました。");
                }
            }
            catch (Exception ex)
            {
                Utils.PopupWarn("接続に失敗しました。\n" + ex.Message);
            }
            finally
            {
            }
        }

        /// <summary>
        /// コンピュータ名が入力された
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void hostTextBox_TextChanged(object sender, EventArgs e)
        {
            Config.WLANHost = this.hostTextBox.Text;
        }

        /// <summary>
        /// Bluetoothデバイスを問い合わせる。
        /// </summary>
        private static void DoInquiry()
		{
			UInt32 devNum = 0;

			try
			{
				Int32 ret = Bt.CommLib.Bluetooth.btBluetoothInquiry(ref devNum);

                MainForm.configCtrl.deviceListBox.Invoke(new Action(() => {
                    if (!String.IsNullOrEmpty(Config.BluetoothHost.addr)) {
                        string currentDevice = String.Format("{0}:{1}", Config.BluetoothHost.name, Config.BluetoothHost.addr);
                        MainForm.configCtrl.deviceListBox.SelectedItem = currentDevice;
                    }
                }));

				if (ret == Bt.LibDef.BT_OK)
				{
                    for (int i = 0; i < devNum; i++)
					{
						Bt.LibDef.BT_BLUETOOTH_TARGET target = new Bt.LibDef.BT_BLUETOOTH_TARGET();
						ret = Bt.CommLib.Bluetooth.btBluetoothGetInquiryResult((UInt32)i, ref target);
						if (Bt.LibDef.BT_OK != ret)
						{
                            Utils.PopupAlert("btBluetoothGetInquiryResult", ret);
                            break;
						}

                        for (int index = 0; index <  MainForm.configCtrl.deviceListBox.Items.Count; index++)
                        {
                            string deviceName = (string) MainForm.configCtrl.deviceListBox.Items[index];
                            string[] values = deviceName.Split(':');
                            if (2 == values.Length)
                            {
                                if (String.Equals(values[1], target.addr))
                                {
                                    deviceName = String.Format("{0}:{1}", target.name, target.addr);
                                    MainForm.configCtrl.deviceListBox.Invoke(new Action(() => {
                                        MainForm.configCtrl.deviceListBox.Items[index] = deviceName;
                                    }));
                                    break;
                                }
                            }
                        }
					}

				}
				else if (ret == Bt.LibDef.BT_ERR_COMM_CANCEL_INQUIRY)
				{
				}
				else
				{
                    Utils.PopupAlert("btBluetoothInquiry", ret);
				}
			}
			catch (Exception ex)
			{
                Utils.PopupAlert(ex.Message);
			}
		}

        /// <summary>
        /// Bluetoothデバイスの問い合わせを実行する。
        /// </summary>
        private void Inquiry()
		{
			try
			{
				Thread th = new Thread(new ThreadStart(DoInquiry));
				th.IsBackground = true;
				th.Start();
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString());
			}
		}


    }
}
