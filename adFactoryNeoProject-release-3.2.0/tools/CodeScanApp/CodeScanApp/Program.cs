//********************************************************************
//  FileName : Program.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System;
using System.Linq;
using System.Collections.Generic;
using System.Windows.Forms;

namespace CodeScanApp
{
    static class Program
    {
        public static WaitForm _form;

        /// <summary>
        /// アプリケーションのメイン エントリ ポイントです。
        /// </summary>
        [MTAThread]
        static void Main()
        {
            _form = new WaitForm();
            _form.Show();

            Application.Run(new MainForm());
        }
    }
}