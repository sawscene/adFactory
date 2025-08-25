//********************************************************************
//  FileName : ScanItem.cs
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

namespace CodeScanApp.Common
{
    /// <summary>
    /// 読取アイテム
    /// </summary>
    public class ScanItem
    {
        /// <summary>
        /// フィールド名
        /// </summary>
        public string Name          { get; set; }
        /// <summary>
        /// OCR設定ファイル名
        /// </summary>
        public string IniFile       { get; set; }
        /// <summary>
        /// タグ
        /// </summary>
        public string Tag           { get; set; }
        /// <summary>
        /// LED照明
        /// </summary>
        public int LED              { get; set; }
        /// <summary>
        /// 区切り文字
        /// </summary>
        public string Delimiter     { get; set; }
        /// <summary>
        /// 区切り位置
        /// </summary>
        public int Section          { get; set; } 
        /// <summary>
        /// 開始位置
        /// </summary>
        public int Frist            { get; set; }
        /// <summary>
        /// 文字列長
        /// </summary>
        public int Length           { get; set; }
        /// <summary>
        /// 照明パターン
        /// </summary>
        public int Illuminaton      { get; set; }
        /// <summary>
        /// ターゲット輝度設定
        /// </summary>
        private int[] _brightness = new int[4];
        public int[] Brightness     
        { 
            get
            {
                return this._brightness;
            }
        }
        /// <summary>
        /// 読み取り距離表示LED制御
        /// </summary>
        public int DistanceLED      { get; set; }
        /// <summary>
        /// ローアングル照明方向
        /// </summary>
        public int LowDirection     { get; set; }
        /// <summary>
        /// 3D 照明 凹凸設定
        /// </summary>
        public int Illuminaton3D    { get; set; }
        /// <summary>
        /// マーカー
        /// </summary>
        public int Marker           { get; set; }
        /// <summary>
        /// カメラ照明
        /// </summary>
        public int CameraLED        { get; set; }
        /// <summary>
        /// パーツIDかどうか
        /// </summary>
        public bool IsPartsID       { get; set; }
    }
}
