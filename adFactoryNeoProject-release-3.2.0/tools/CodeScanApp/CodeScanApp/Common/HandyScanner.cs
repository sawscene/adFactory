using System;
using System.Linq;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Drawing;

namespace CodeScanApp.Common
{
    /// <summary>
    /// ハンディー端末ユーティリティー
    /// </summary>
    public class HandyScanner
    {
        public const string HT_BT_W300 = "BT-W300";
        public const string HT_BT_W370 = "BT-W370";

        /// <summary>
        /// 機種
        /// </summary>
        public string HandyType { get; set; }
        
        /// <summary>
        /// モニター解像度がVGA(480x640)かどうかを返す。
        /// </summary>
        /// <returns></returns>
        public bool isVGAScreen() {
            return String.Compare(this.HandyType, HT_BT_W300) >= 0;
        }
        
        /// <summary>
        /// コンストラクタ
        /// </summary>
        public HandyScanner()
        {
            // 機種を取得
            IntPtr pValue = Marshal.AllocCoTaskMem(32);
            Int32 ret = Bt.SysLib.Terminal.btGetHandyParameter(Bt.LibDef.BT_SYS_PRM_HTTYPE, pValue);
            this.HandyType = Marshal.PtrToStringUni(pValue);
            Marshal.FreeCoTaskMem(pValue);

            // this.HandyType = HT_BT_W370;
        }

        /// <summary>
        /// 機種に適合したコントール位置を返す。
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public Point toPoint(int x, int y)
        {
            return isVGAScreen() ? new Point(x * 2, (int)(y * 2.1)) : new Point(x, y);
        }

        /// <summary>
        /// 機種に適合したコントール位置を返す。
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public Point toFooterPoint(int x, int y)
        {
            return isVGAScreen() ? new Point(x * 2, 510) : new Point(x, y);
        }
        
        /// <summary>
        /// 機種に適合したコントールサイズを返す。
        /// </summary>
        /// <param name="width"></param>
        /// <param name="height"></param>
        /// <returns></returns>
        public Size toSize(int width, int height)
        {
            return isVGAScreen() ? new Size(width * 2, (int)(height * 2.2)) : new Size(width, height);
        }
        
        /// <summary>
        /// 機種に適合したボタンサイズを返す。
        /// </summary>
        /// <param name="width"></param>
        /// <param name="height"></param>
        /// <returns></returns>
        public Size toButtonSize(int width, int height)
        {
            return isVGAScreen() ? new Size(width * 2, (int)(height * 1.8)) : new Size(width, height);
        }

        /// <summary>
        /// 機種に適合したフォントサイズを返す。
        /// </summary>
        /// <param name="emSize"></param>
        /// <returns></returns>
        public float toFontSize(float emSize)
        {
            return isVGAScreen() ? emSize * 1.8F : emSize;
        }
    
    }
}
