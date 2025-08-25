
namespace DashboardCustomizeTool.CommonClass
{
    class MultipleLanguages
    {
        #region メッセージ
        /// <summary>
        /// アプリケーション起動時、設定ファイルの情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessageDouble { get; set; }

        /// <summary>
        /// アプリケーション起動時、設定ファイルの情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessage_Item_IniFile { get; set; }

        /// <summary>
        /// アプリケーション起動時、必要なモジュールが不足しているため起動できないときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessageFujiFlexaDll { get; set; }

        /// <summary>
        /// アプリケーション起動時、画面表示のための情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessageLayoutIniFile { get; set; }

        /// <summary>
        /// アプリケーション起動時、定義ファイルに定義されていないアイテムの表示設定がされている場合に本メッセージを表示します。
        /// </summary>
        public string ErrorMessageLayoutIniFileDifference { get; set; }

        /// <summary>
        /// レイアウト保存時にエラーが発生したときに本メッセージダイアログを表示し保存を中断します。
        /// </summary>
        public string ErrorMessageSaveLayout { get; set; }

        /// <summary>
        /// レイアウト保存失敗時のエラーメッセージ
        /// </summary>
        public string ErrorMessageSaveLayoutFile { get; set; }

        /// <summary>
        /// 開く失敗時のメッセージ。
        /// </summary>
        public string ErrorMessageOpen { get; set; }

        /// <summary>
        /// インポート失敗時のメッセージ。
        /// </summary>
        public string ErrorMessageImport { get; set; }

        /// <summary>
        /// エクスポート失敗時のメッセージ。
        /// </summary>
        public string ErrorMessageExport { get; set; }

        /// <summary>
        /// インポート/エクスポート時のファイル選択ダイアログのファイル種で表示
        /// </summary>
        public string FileReadDlgFilter { get; set; }

        /// <summary>
        /// レイアウト設定情報をエクスポートする際のダイアログタイトル
        /// </summary>
        public string FileReadDlgTitle { get; set; }

        /// <summary>
        /// レイアウト設定情報をエクスポートする際のダイアログタイトル
        /// </summary>
        public string FileSaveDlgTitle { get; set; }

        /// <summary>
        /// アプリケーション起動時にDashboardが起動時のメッセージ
        /// </summary>
        public string MessageDashboardEnd { get; set; }

        /// <summary>
        /// インポート実行時の確認メッセージ
        /// </summary>
        public string MessageImportLayout { get; set; }

        /// <summary>
        /// レイアウト設定情報をエクスポート時レイアウトの保存確認メッセージ
        /// </summary>
        public string MessageLayoutCfg { get; set; }

        /// <summary>
        /// 終了時にレイアウトが変更されていた場合のメッセージ
        /// </summary>
        public string MessageSaveLayout { get; set; }

        /// <summary>
        /// 名前を付けて保存時の上書き確認メッセージ
        /// </summary>
        public string MessageSaveAsExsistFile { get; set; }

        /// <summary>
        /// インポート完了メッセージ。
        /// </summary>
        public string MessageImportComplete { get; set; }

        /// <summary>
        /// エクスポート完了メッセージ
        /// </summary>
        public string MessageExportComplete { get; set; }

        /// <summary>
        /// 開く完了メッセージ。
        /// </summary>
        public string MessageOpenComplete { get; set; }

        /// <summary>
        /// 保存完了メッセージ
        /// </summary>
        public string MessageSaveComplete { get; set; }

        /// <summary>
        /// 設備識別名で保存完了メッセージ
        /// </summary>
        public string MessageSaveAsComplete { get; set; }
        #endregion

        #region コントロールのテキスト
        /// <summary>
        /// ツール名称
        /// </summary>
        public string AppTitleText { get; set; }

        /// <summary>
        /// コンテキストメニュー名称（アイテムの追加
        /// </summary>
        public string AddToolStripItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（アイテムの追加）
        /// </summary>
        public string AddToolStripMenuItemText { get; set; }

        /// <summary>
        /// コンテキストメニュー名称（アイテムの削除）
        /// </summary>
        public string DeleteToolStripItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（アイテムの削除）
        /// </summary>
        public string DeleteToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（編集）
        /// </summary>
        public string EditEToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（終了）
        /// </summary>
        public string EndDToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（エクスポート）
        /// </summary>
        public string ExportToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（設定）
        /// </summary>
        public string ConfigToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（ファイル）
        /// </summary>
        public string FileFToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（インポート）
        /// </summary>
        public string ImportToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（開く）
        /// </summary>
        public string OpenToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（保存）
        /// </summary>
        public string SaveToolStripMenuItemText { get; set; }

        /// <summary>
        /// メニューリスト名称（設備識別子名で保存）
        /// </summary>
        public string SaveAsToolStripMenuItemText { get; set; }
        #endregion

        #region 設定ダイアログのテキスト
        /// <summary>
        /// 設定
        /// </summary>
        public string ConfigDialogTitle { get; set; }
        /// <summary>
        /// 全体サイズ
        /// </summary>
        public string LayoutAreaSizeText { get; set; }
        /// <summary>
        /// W
        /// </summary>
        public string LayoutAreaWidthText { get; set; }
        /// <summary>
        /// H
        /// </summary>
        public string LayoutAreaHeightText { get; set; }
        #endregion

        #region 開くダイアログのテキスト
        /// <summary>
        /// 開く
        /// </summary>
        public string OpenDialogTitle { get; set; }
        #endregion

        #region 設備識別子名で保存ダイアログのテキスト
        /// <summary>
        /// 設備識別子名で保存
        /// </summary>
        public string SaveAsDialogTitle { get; set; }
        /// <summary>
        /// 設備識別名
        /// </summary>
        public string EquipmentIdentNameText { get; set; }
        #endregion

        #region ボタンのテキスト
        /// <summary>
        /// OK
        /// </summary>
        public string OkButtonText { get; set; }
        /// <summary>
        /// キャンセル
        /// </summary>
        public string CancelButtonText { get; set; }
        /// <summary>
        /// 保存
        /// </summary>
        public string SaveButtonText { get; set; }
        #endregion

        private static MultipleLanguages instance = null;
        public static MultipleLanguages GetInstance()
        {
            if (instance == null)
            {
                instance = new MultipleLanguages();
                instance.setLanguageText();
            }
            return instance;
        }

        public MultipleLanguages()
        {
        }

        /// <summary>
        /// 
        /// </summary>
        public void setLanguageText()
        {
            ProcessLogger.StartMethod();
            ProcessLogger.Logger.Info("LanguageType" + MainForm.Language);

            // 指定した言語の文言を設定する。
            switch (MainForm.Language)
            {
                case Define.LANGUAGE_SELECT_JP:// 日本語
                    // メッセージ
                    this.ErrorMessageDouble = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageDouble;
                    this.ErrorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessage_Item_IniFile;
                    this.ErrorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageFujiFlexaDll;
                    this.ErrorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageLayoutIniFile;
                    this.ErrorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageLayoutIniFileDifference;
                    this.ErrorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageSaveLayout;
                    this.ErrorMessageSaveLayoutFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageSaveLayoutFile;
                    this.ErrorMessageOpen = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageOpen;
                    this.ErrorMessageImport = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageImport;
                    this.ErrorMessageExport = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageExport;
                    this.FileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resource_jp.FileReadDlgFilter;
                    this.FileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resource_jp.FileReadDlgTitle;
                    this.FileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resource_jp.FileSaveDlgTitle;
                    this.MessageDashboardEnd = global::DashboardCustomizeTool.Properties.Resource_jp.MessageDashboardEnd;
                    this.MessageImportLayout = global::DashboardCustomizeTool.Properties.Resource_jp.MessageImportLayout;
                    this.MessageLayoutCfg = global::DashboardCustomizeTool.Properties.Resource_jp.MessageLayoutCfg;
                    this.MessageSaveLayout = global::DashboardCustomizeTool.Properties.Resource_jp.MessageSaveLayout;
                    this.MessageSaveAsExsistFile = global::DashboardCustomizeTool.Properties.Resource_jp.MessageSaveAsExsistFile;
                    this.MessageImportComplete = global::DashboardCustomizeTool.Properties.Resource_jp.MessageImportComplete;
                    this.MessageExportComplete = global::DashboardCustomizeTool.Properties.Resource_jp.MessageExportComplete;
                    this.MessageOpenComplete = global::DashboardCustomizeTool.Properties.Resource_jp.MessageOpenComplete;
                    this.MessageSaveComplete = global::DashboardCustomizeTool.Properties.Resource_jp.MessageSaveComplete;
                    this.MessageSaveAsComplete = global::DashboardCustomizeTool.Properties.Resource_jp.MessageSaveAsComplete;
                    // コントロールのテキスト
                    this.AppTitleText = global::DashboardCustomizeTool.Properties.Resource_jp.AppTitle;
                    this.AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resource_jp.AddToolStripItem;
                    this.AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.AddToolStripMenuItem;
                    this.DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resource_jp.DeleteToolStripItem;
                    this.DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.DeleteToolStripMenuItem;
                    this.EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.EditToolStripMenuItem;
                    this.EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.EndToolStripMenuItem;
                    this.ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ExportToolStripMenuItem;
                    this.ConfigToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ConfigToolStripMenuItem;
                    this.FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.FileFToolStripMenuItem;
                    this.ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ImportToolStripMenuItem;
                    this.OpenToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.OpenToolStripMenuItem;
                    this.SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.SaveToolStripMenuItem;
                    this.SaveAsToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.SaveAsToolStripMenuItem;
                    // 設定ダイアログのテキスト
                    this.ConfigDialogTitle = global::DashboardCustomizeTool.Properties.Resource_jp.ConfigDialogTitle;
                    this.LayoutAreaSizeText = global::DashboardCustomizeTool.Properties.Resource_jp.LayoutAreaSizeText;
                    this.LayoutAreaWidthText = global::DashboardCustomizeTool.Properties.Resource_jp.LayoutAreaWidthText;
                    this.LayoutAreaHeightText = global::DashboardCustomizeTool.Properties.Resource_jp.LayoutAreaHeightText;
                    // 開くダイアログのテキスト
                    this.OpenDialogTitle = global::DashboardCustomizeTool.Properties.Resource_jp.OpenDialogTitle;
                    // 設備識別子名で保存ダイアログのテキスト
                    this.SaveAsDialogTitle = global::DashboardCustomizeTool.Properties.Resource_jp.SaveAsDialogTitle;
                    this.EquipmentIdentNameText = global::DashboardCustomizeTool.Properties.Resource_jp.EquipmentIdentNameText;
                    // ボタンのテキスト
                    this.OkButtonText = global::DashboardCustomizeTool.Properties.Resource_jp.OkButtonText;
                    this.CancelButtonText = global::DashboardCustomizeTool.Properties.Resource_jp.CancelButtonText;
                    this.SaveButtonText = global::DashboardCustomizeTool.Properties.Resource_jp.SaveButtonText;

                    break;
                case Define.LANGUAGE_SELECT_SC:// 中国語(簡体)
                    //　メッセージ
                    this.ErrorMessageDouble = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageDouble;
                    this.ErrorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessage_Item_IniFile;
                    this.ErrorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageFujiFlexaDll;
                    this.ErrorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageLayoutIniFile;
                    this.ErrorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageLayoutIniFileDifference;
                    this.ErrorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageSaveLayout;
                    this.ErrorMessageSaveLayoutFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageSaveLayoutFile;
                    this.ErrorMessageOpen = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageOpen;
                    this.ErrorMessageImport = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageImport;
                    this.ErrorMessageExport = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageExport;
                    this.FileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileReadDlgFilter;
                    this.FileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileReadDlgTitle;
                    this.FileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileSaveDlgTitle;
                    this.MessageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageDashboardEnd;
                    this.MessageImportLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageImportLayout;
                    this.MessageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageLayoutCfg;
                    this.MessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageSaveLayout;
                    this.MessageSaveAsExsistFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageSaveAsExsistFile;
                    this.MessageImportComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageImportComplete;
                    this.MessageExportComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageExportComplete;
                    this.MessageOpenComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageOpenComplete;
                    this.MessageSaveComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageSaveComplete;
                    this.MessageSaveAsComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageSaveAsComplete;
                    //コントロール名設定
                    this.AppTitleText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AppTitle;
                    this.AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AddToolStripItem;
                    this.AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AddToolStripMenuItem;
                    this.DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.DeleteToolStripItem;
                    this.DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.DeleteToolStripMenuItem;
                    this.EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EditToolStripMenuItem;
                    this.EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EndToolStripMenuItem;
                    this.ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ExportToolStripMenuItem;
                    this.ConfigToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ConfigToolStripMenuItem;
                    this.FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileFToolStripMenuItem;
                    this.ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ImportToolStripMenuItem;
                    this.OpenToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.OpenToolStripMenuItem;
                    this.SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.SaveToolStripMenuItem;
                    this.SaveAsToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.SaveAsToolStripMenuItem;
                    // 設定ダイアログのテキスト
                    this.ConfigDialogTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ConfigDialogTitle;
                    this.LayoutAreaSizeText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.LayoutAreaSizeText;
                    this.LayoutAreaWidthText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.LayoutAreaWidthText;
                    this.LayoutAreaHeightText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.LayoutAreaHeightText;
                    // 開くダイアログのテキスト
                    this.OpenDialogTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.OpenDialogTitle;
                    // 設備識別子名で保存ダイアログのテキスト
                    this.SaveAsDialogTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.SaveAsDialogTitle;
                    this.EquipmentIdentNameText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EquipmentIdentNameText;
                    // ボタンのテキスト
                    this.OkButtonText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.OkButtonText;
                    this.CancelButtonText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.CancelButtonText;
                    this.SaveButtonText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.SaveButtonText;

                    break;
                case Define.LANGUAGE_SELECT_TC:// 中国語(繁体)
                    // メッセージ
                    this.ErrorMessageDouble = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageDouble;
                    this.ErrorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessage_Item_IniFile;
                    this.ErrorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageFujiFlexaDll;
                    this.ErrorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageLayoutIniFile;
                    this.ErrorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageLayoutIniFileDifference;
                    this.ErrorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageSaveLayout;
                    this.ErrorMessageSaveLayoutFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageSaveLayoutFile;
                    this.ErrorMessageOpen = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageOpen;
                    this.ErrorMessageImport = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageImport;
                    this.ErrorMessageExport = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageExport;
                    this.FileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileReadDlgFilter;
                    this.FileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileReadDlgTitle;
                    this.FileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileSaveDlgTitle;
                    this.MessageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageDashboardEnd;
                    this.MessageImportLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageImportLayout;
                    this.MessageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageLayoutCfg;
                    this.MessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageSaveLayout;
                    this.MessageSaveAsExsistFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageSaveAsExsistFile;
                    this.MessageImportComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageImportComplete;
                    this.MessageExportComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageExportComplete;
                    this.MessageOpenComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageOpenComplete;
                    this.MessageSaveComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageSaveComplete;
                    this.MessageSaveAsComplete = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageSaveAsComplete;
                    // メイン画面のテキスト
                    this.AppTitleText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AppTitle;
                    this.AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AddToolStripItem;
                    this.AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AddToolStripMenuItem;
                    this.DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.DeleteToolStripItem;
                    this.DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.DeleteToolStripMenuItem;
                    this.EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EditToolStripMenuItem;
                    this.EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EndToolStripMenuItem;
                    this.ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ExportToolStripMenuItem;
                    this.ConfigToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ConfigToolStripMenuItem;
                    this.FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileFToolStripMenuItem;
                    this.ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ImportToolStripMenuItem;
                    this.OpenToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.OpenToolStripMenuItem;
                    this.SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.SaveToolStripMenuItem;
                    this.SaveAsToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.SaveAsToolStripMenuItem;
                    // 設定ダイアログのテキスト
                    this.ConfigDialogTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ConfigDialogTitle;
                    this.LayoutAreaSizeText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.LayoutAreaSizeText;
                    this.LayoutAreaWidthText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.LayoutAreaWidthText;
                    this.LayoutAreaHeightText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.LayoutAreaHeightText;
                    // 開くダイアログのテキスト
                    this.OpenDialogTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.OpenDialogTitle;
                    // 設備識別子名で保存ダイアログのテキスト
                    this.SaveAsDialogTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.SaveAsDialogTitle;
                    this.EquipmentIdentNameText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EquipmentIdentNameText;
                    // ボタンのテキスト
                    this.OkButtonText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.OkButtonText;
                    this.CancelButtonText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.CancelButtonText;
                    this.SaveButtonText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.SaveButtonText;

                    break;
                default:// 英語
                    // メッセージ
                    this.ErrorMessageDouble = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageDouble;
                    this.ErrorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessage_Item_IniFile;
                    this.ErrorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageFujiFlexaDll;
                    this.ErrorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageLayoutIniFile;
                    this.ErrorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageLayoutIniFileDifference;
                    this.ErrorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageSaveLayout;
                    this.ErrorMessageSaveLayoutFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageSaveLayoutFile;
                    this.ErrorMessageOpen = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageOpen;
                    this.ErrorMessageImport = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageImport;
                    this.ErrorMessageExport = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageExport;
                    this.FileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources.FileReadDlgFilter;
                    this.FileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources.FileReadDlgTitle;
                    this.FileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources.FileSaveDlgTitle;
                    this.MessageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources.MessageDashboardEnd;
                    this.MessageImportLayout = global::DashboardCustomizeTool.Properties.Resources.MessageImportLayout;
                    this.MessageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources.MessageLayoutCfg;
                    this.MessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources.MessageSaveLayout;
                    this.MessageSaveAsExsistFile = global::DashboardCustomizeTool.Properties.Resources.MessageSaveAsExsistFile;
                    this.MessageImportComplete = global::DashboardCustomizeTool.Properties.Resources.MessageImportComplete;
                    this.MessageExportComplete = global::DashboardCustomizeTool.Properties.Resources.MessageExportComplete;
                    this.MessageOpenComplete = global::DashboardCustomizeTool.Properties.Resources.MessageOpenComplete;
                    this.MessageSaveComplete = global::DashboardCustomizeTool.Properties.Resources.MessageSaveComplete;
                    this.MessageSaveAsComplete = global::DashboardCustomizeTool.Properties.Resources.MessageSaveAsComplete;
                    // メイン画面のテキスト
                    this.AppTitleText = global::DashboardCustomizeTool.Properties.Resources.AppTitle;
                    this.AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources.AddToolStripItem;
                    this.AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.AddToolStripMenuItem;
                    this.DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources.DeleteToolStripItem;
                    this.DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.DeleteToolStripMenuItem;
                    this.EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.EditToolStripMenuItem;
                    this.EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.EndToolStripMenuItem;
                    this.ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ExportToolStripMenuItem;
                    this.ConfigToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ConfigToolStripMenuItem;
                    this.FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.FileFToolStripMenuItem;
                    this.ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ImportToolStripMenuItem;
                    this.OpenToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.OpenToolStripMenuItem;
                    this.SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.SaveToolStripMenuItem;
                    this.SaveAsToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.SaveAsToolStripMenuItem;
                    // 設定ダイアログのテキスト
                    this.ConfigDialogTitle = global::DashboardCustomizeTool.Properties.Resources.ConfigDialogTitle;
                    this.LayoutAreaSizeText = global::DashboardCustomizeTool.Properties.Resources.LayoutAreaSizeText;
                    this.LayoutAreaWidthText = global::DashboardCustomizeTool.Properties.Resources.LayoutAreaWidthText;
                    this.LayoutAreaHeightText = global::DashboardCustomizeTool.Properties.Resources.LayoutAreaHeightText;
                    // 開くダイアログのテキスト
                    this.OpenDialogTitle = global::DashboardCustomizeTool.Properties.Resources.OpenDialogTitle;
                    // 設備識別子名で保存ダイアログのテキスト
                    this.SaveAsDialogTitle = global::DashboardCustomizeTool.Properties.Resources.SaveAsDialogTitle;
                    this.EquipmentIdentNameText = global::DashboardCustomizeTool.Properties.Resources.EquipmentIdentNameText;
                    // ボタンのテキスト
                    this.OkButtonText = global::DashboardCustomizeTool.Properties.Resources.OkButtonText;
                    this.CancelButtonText = global::DashboardCustomizeTool.Properties.Resources.CancelButtonText;
                    this.SaveButtonText = global::DashboardCustomizeTool.Properties.Resources.SaveButtonText;

                    break;
            }

            ProcessLogger.EndMethod();
        }
    }
}
