//********************************************************************
//  FileName : Pattern.cs
//  History 
//    1.0.0.0  : 2021.01.08 新規作成
//             :
//
//    All Rights Reserved. Copyright (C) 2021, Adtek Fuji Co., Ltd.
//********************************************************************
using System.Collections.Generic;

namespace CodeScanApp.Common
{
    /// <summary>
    /// 読取パターン設定
    /// </summary>
    public class Pattern
    {
        /// <summary>
        /// パターン番号
        /// </summary>
        public int ID { get; set; }

        /// <summary>
        /// パターン名
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// 機種名
        /// </summary>
        public string ModelName { get; set; }

        /// <summary>
        /// 工程名
        /// </summary>
        public string WorkName { get; set; }

        /// <summary>
        /// 撮像ライブビューの表示
        /// </summary>
        public int LiveView { get; set; }

        /// <summary>
        /// 読取パターン設定一覧
        /// </summary>
        private List<ScanItem> _items = new List<ScanItem>();
        public List<ScanItem> Items
        {
            get { return _items; }
            set { _items = value; }
        }

        /// <summary>
        /// コンストラクター
        /// </summary>
        /// <param name="id"></param>
        /// <param name="name"></param>
        /// <param name="modelName"></param>
        /// <param name="workName"></param>
        public Pattern(int id, string name, string modelName, string workName)
        {
            this.ID = id;
            this.Name = name;
            this.ModelName = modelName;
            this.WorkName = workName;
        }
    }
}
