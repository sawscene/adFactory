//********************************************************************
//  FileName : Utils.cs
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
using System.Windows.Forms;
using System.Drawing;

namespace CodeScanApp.Common
{
    /// <summary>
    /// ユーティリティー
    /// </summary>
    public class Utils
    {
        /// <summary>
        /// メッセージを表示する。
        /// </summary>
        /// <param name="message">警告メッセージ</param>
        /// <returns>確認結果</returns>
        public static DialogResult PopupInfo(string message)
        {
            return MessageBox.Show(message, "情報", MessageBoxButtons.OK, MessageBoxIcon.Asterisk, MessageBoxDefaultButton.Button1);
        }
        
        /// <summary>
        /// エラーメッセージを表示する。
        /// </summary>
        /// <param name="api">関数名</param>
        /// <param name="ret">戻り値</param>
        /// <returns>確認結果</returns>
        public static DialogResult PopupAlert(string api, Int32 ret)
        {
            string message = String.Format("ライブラリエラーが発生しました\n関数名:{0}\n戻り値:{1}", api, ret.ToString());
            return MessageBox.Show(message, "エラー", MessageBoxButtons.OK, MessageBoxIcon.Hand, MessageBoxDefaultButton.Button1);
        }

        /// <summary>
        /// エラーメッセージを表示する。
        /// </summary>
        /// <param name="message">警告メッセージ</param>
        /// <returns>確認結果</returns>
        public static DialogResult PopupAlert(string message)
        {
            return MessageBox.Show(message, "エラー", MessageBoxButtons.OK, MessageBoxIcon.Asterisk, MessageBoxDefaultButton.Button1);
        }
        
        /// <summary>
        /// 警告メッセージを表示する。
        /// </summary>
        /// <param name="message">警告メッセージ</param>
        /// <returns>確認結果</returns>
        public static DialogResult PopupWarn(string message)
        {
            return MessageBox.Show(message, "警告", MessageBoxButtons.OK, MessageBoxIcon.Exclamation, MessageBoxDefaultButton.Button1);
        }
    }
}
