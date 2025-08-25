//********************************************************************
//  FileName : DeviceComm.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System;
using System.Linq;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.Net.Sockets;
using System.Threading;

namespace CodeScanApp.Common
{
    /// <summary>
    /// デバイス間通信
    /// </summary>
    public class DeviceComm
    {
        /// <summary>
        /// 接続済かどうか
        /// </summary>
        private static bool isConnected = false;

        /// <summary>
        /// TCPクライアント
        /// </summary>
        private TcpClient tcpClient;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public DeviceComm()
        {
        }

        /// <summary>
        /// 通信サービスをオープンする。
        /// </summary>
        /// <returns></returns>
        public bool Open()
        {
            if (0 == Config.ConnectType)
            {
                Int32 ret = Bt.CommLib.Wlan.btWLANOpen();
			    if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_ALREADY_OPEN == ret))
			    {
					Utils.PopupAlert("btWLANOpen", ret);
					return false;
				}
            }
            else
            {
                Int32 ret = Bt.CommLib.Bluetooth.btBluetoothOpen();
                if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_ALREADY_OPEN == ret))
			    {
                    if (Bt.LibDef.BT_ERR == ret)
                    {
                        if (this.Close())
                        {
                            ret = Bt.CommLib.Bluetooth.btBluetoothOpen();
                        }
                    }

                    Utils.PopupAlert("btBluetoothOpen", ret);
                    return false;
                }
            }

            return true;
        }

        /// <summary>
        /// 通信サービスをクローズする。
        /// </summary>
        /// <returns></returns>
        public bool Close()
        {
            try
            {
                if (0 == Config.ConnectType)
                {
                    if (null != tcpClient)
                    {
                        this.tcpClient.Close();
                        (this.tcpClient as IDisposable).Dispose();
                        this.tcpClient = null;
                    }
                    
                    //Int32 ret = Bt.CommLib.Wlan.btWLANClose();
                    //if (Bt.LibDef.BT_OK != ret)
                    //{
                    //    Utils.PopupAlert("btWLANClose", ret);
                    //    return false;
                    //}
                }
                else
                {
                    Int32 ret;
                    if (isConnected)
                    {
                        ret = Bt.CommLib.Bluetooth.btBluetoothSPPDisconnect();
                        if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_SPP_NOTCONNECT == ret))
                        {
                            Utils.PopupAlert("btBluetoothSPPDisconnect", ret);
                        }

                        isConnected = false;
                    }

                    // ここでbtBluetoothClose()を呼び出すと強制終了する
                    //ret = Bt.CommLib.Bluetooth.btBluetoothClose();
                    //if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_NOTOPEN == ret))
                    //{
                    //    Utils.PopupAlert("btBluetoothClose", ret);
                    //}
                }

            }
            catch (Exception)
            {
            }

            return true;
        }
        
        /// <summary>
        /// 接続テストを行う。
        /// </summary>
        /// <returns>true:接続OK、false:接続NG</returns>
        public bool Test()
        {
            bool testResult = true;

			try
			{
                if (0 == Config.ConnectType)
                {
                    TcpClient testClient = new TcpClient(Config.WLANHost, Constants.WLAN_PORT);
                    testClient.Close();
                    (testClient as IDisposable).Dispose();
                }
                else
                {
                    if (isConnected)
                    {
                        Bt.CommLib.Bluetooth.btBluetoothSPPDisconnect();
                        isConnected = false;
                    }

				    UInt32 timeoutSet = 30000;
				    Int32 ret = Bt.CommLib.Bluetooth.btBluetoothSPPConnect(Config.BluetoothHost, timeoutSet);
                    if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_SPP_CONNECTED == ret))
				    {
                        Utils.PopupAlert("btBluetoothSPPConnect", ret);
					    testResult = false;
				    }

                    if (testResult)
                    {
                        isConnected = true;
                        ret = Bt.CommLib.Bluetooth.btBluetoothSPPDisconnect();
                        if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_NOTOPEN == ret))
			            {
                            Utils.PopupAlert("btBluetoothSPPDisconnect", ret);
                        }
                    }
                    else
                    {
                        this.Close();
                        
                        // ここでbtBluetoothClose()を呼び出すと強制終了する
                        Bt.CommLib.Bluetooth.btBluetoothClose();

                        this.Open();
                    }

                    isConnected = false;
                }
            }
            catch (Exception ex)
			{
				throw ex;
			}
            return testResult;
        }

        /// <summary>
        /// データを送信する。
        /// </summary>
        /// <param name="data">送信データ</param>
        public bool Send(string data)
		{
			Int32 ret = Bt.LibDef.BT_ERR;
            Int32 sendRet = Bt.LibDef.BT_ERR;

			try
			{
                if (0 == Config.ConnectType)
                {
                    if (null == this.tcpClient)
                    {
                        this.tcpClient = new TcpClient(Config.WLANHost, Constants.WLAN_PORT);
                    }
                    
                    Byte[] bSendBuff = System.Text.Encoding.GetEncoding("Shift_JIS").GetBytes(data);

                    NetworkStream stream = this.tcpClient.GetStream();
                    stream.Write(bSendBuff, 0, bSendBuff.Length);
                    //stream.Close();

                    ret = Bt.LibDef.BT_OK;
                }
                else
                {
                    if (!isConnected)
                    {
				        UInt32 timeoutSet = 30000;
				        ret = Bt.CommLib.Bluetooth.btBluetoothSPPConnect(Config.BluetoothHost, timeoutSet);
                        if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_SPP_CONNECTED == ret))
				        {
                            Utils.PopupAlert("btBluetoothSPPConnect", ret);
					        return false;
				        }

                        isConnected = true;
                    }

                    Byte[] bSendBuff = System.Text.Encoding.GetEncoding("Shift_JIS").GetBytes(data);
				    UInt32 dataLen = (UInt32) bSendBuff.Length;
				    IntPtr pSendBuff = Marshal.AllocCoTaskMem((Int32)dataLen);
                    UInt32 sentLen = 0;

				    Marshal.Copy(bSendBuff, 0, pSendBuff, (Int32)dataLen);

				    sendRet = Bt.CommLib.Bluetooth.btBluetoothSPPSend(pSendBuff, dataLen, ref sentLen);

                    Marshal.FreeCoTaskMem(pSendBuff);

				    if (Bt.LibDef.BT_OK != sendRet)
				    {
                        this.Close();
                        
                        // ここでbtBluetoothClose()を呼び出すと強制終了する
                        Bt.CommLib.Bluetooth.btBluetoothClose();

                        this.Open();

                        int retry = 0;
                        for ( ; retry < 3; retry++)
                        {
                            Thread.Sleep(100);

                            // データを再送信
                            if (Retry(data))
                            {
                                break;
                            }
                        }

                        if (3 == retry)
                        {
                            Utils.PopupAlert("btBluetoothSPPSend", sendRet);
                        }
				    }

                }
			}
			catch (Exception ex)
			{
                this.Close();
                
                // ここでbtBluetoothClose()を呼び出すと強制終了する
                Bt.CommLib.Bluetooth.btBluetoothClose();

                this.Open();
                
                Utils.PopupAlert(ex.Message);
			}

            return Bt.LibDef.BT_OK == sendRet;
		}

        /// <summary>
        /// データを再送信する。
        /// </summary>
        /// <param name="data">送信データ</param>
        public bool Retry(string data)
		{
			Int32 ret = Bt.LibDef.BT_ERR;
            Int32 sendRet = Bt.LibDef.BT_ERR;

			try
			{
                if (0 == Config.ConnectType)
                {
                    if (null == this.tcpClient)
                    {
                        this.tcpClient = new TcpClient(Config.WLANHost, Constants.WLAN_PORT);
                    }
                    
                    Byte[] bSendBuff = System.Text.Encoding.GetEncoding("Shift_JIS").GetBytes(data);

                    NetworkStream stream = this.tcpClient.GetStream();
                    stream.Write(bSendBuff, 0, bSendBuff.Length);
                    //stream.Close();

                    ret = Bt.LibDef.BT_OK;
                }
                else
                {
                    if (!isConnected)
                    {
				        UInt32 timeoutSet = 30000;
				        ret = Bt.CommLib.Bluetooth.btBluetoothSPPConnect(Config.BluetoothHost, timeoutSet);
                        if (!(Bt.LibDef.BT_OK == ret || Bt.LibDef.BT_ERR_COMM_SPP_CONNECTED == ret))
				        {
					        return false;
				        }

                        isConnected = true;
                    }

                    Byte[] bSendBuff = System.Text.Encoding.GetEncoding("Shift_JIS").GetBytes(data);
				    UInt32 dataLen = (UInt32) bSendBuff.Length;
				    IntPtr pSendBuff = Marshal.AllocCoTaskMem((Int32)dataLen);
                    UInt32 sentLen = 0;

				    Marshal.Copy(bSendBuff, 0, pSendBuff, (Int32)dataLen);

				    sendRet = Bt.CommLib.Bluetooth.btBluetoothSPPSend(pSendBuff, dataLen, ref sentLen);

                    Marshal.FreeCoTaskMem(pSendBuff);

				    if (Bt.LibDef.BT_OK != sendRet)
				    {
                        Bt.CommLib.Bluetooth.btBluetoothSPPDisconnect();
                        isConnected = false;
                    }
                }
			}
			catch (Exception)
			{
                isConnected = false;

                if (null != tcpClient)
                {
                    this.tcpClient.Close();
                    (this.tcpClient as IDisposable).Dispose();
                    this.tcpClient = null;
                }
			}

            return Bt.LibDef.BT_OK == sendRet;
		}
    }
}
