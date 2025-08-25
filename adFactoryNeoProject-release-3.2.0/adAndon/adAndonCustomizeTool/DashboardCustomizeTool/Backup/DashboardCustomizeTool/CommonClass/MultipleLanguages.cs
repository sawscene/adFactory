using System;
using System.Collections.Generic;
using System.Text;

namespace DashboardCustomizeTool.CommonClass
{
    class MultipleLanguages
    {

        private const string LANGUAGE_SELECT_JP = "JP";  //日本語
        private const string LANGUAGE_SELECT_US = "US";  //英語
        private const string LANGUAGE_SELECT_SC = "SC";　//簡体字
        private const string LANGUAGE_SELECT_TC = "TC";  //繁体字

        /// <summary>
        /// DashBoardCustomizeToolが既に起動しています。
        /// </summary>
        private string _errorMessageDouble;

        /// <summary>
        /// アプリケーション起動時、設定ファイルの情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        private string _errorMessage_Item_IniFile;
        /// <summary>
        /// アプリケーション起動時、必要なモジュールが不足しているため起動できないときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        private string _errorMessageFujiFlexaDll;
        /// <summary>
        /// アプリケーション起動時、画面表示のための情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        private string _errorMessageLayoutIniFile;
        /// <summary>
        /// アプリケーション起動時、定義ファイルに定義されていないアイテムの表示設定がされている場合に本メッセージを表示します。
        /// </summary>
        private string _errorMessageLayoutIniFileDifference;
        /// <summary>
        /// レイアウト保存時にエラーが発生したときに本メッセージダイアログを表示し保存を中断します。

        /// </summary>
        private string _errorMessageSaveLayout;
        /// <summary>
        ///インポート/エクスポート時のファイル選択ダイアログのファイル種で表示
        /// </summary>
        private string _fileReadDlgFilter;
        /// <summary>
        /// レイアウト設定情報をインポートする際のダイアログタイトルです。
        /// </summary>
        private string _fileReadDlgTitle;
        /// <summary>
        /// レイアウト設定情報をエクスポートする際のダイアログタイトルです。
        /// </summary>
        private string _fileSaveDlgTitle;

        /// <summary>
        /// アプリケーション起動時にDashboardが起動していると本メッセージを表示後、Dashboard終了してからアプリケーションを起動します。
        /// </summary>
        private string _messageDashboardEnd;
        /// <summary>
        /// インポート実行時の確認メッセージ
        /// </summary>
        private string _messageImportLayout;
        /// <summary>
        ///レイアウト設定情報をエクスポートする際、編集したレイアウトが保存されていない場合、本メッセージを表示します。
        /// </summary>
        private string _messageLayoutCfg;
        /// <summary>
        ///レイアウトを変更後、保存せずに終了を行おうとした際に本メッセージを表示します。 
        /// </summary>
        private string _messageSaveLayout;


        //コントロール名
        /// <summary>
        /// アプリケーションタイトル
        /// </summary>
        private string _appTitleText;
        /// <summary>
        /// コンテキストメニュー名称（アイテムの追加)
        /// </summary>
        private string _addToolStripItemText;
       
        /// <summary>
        /// メニューリスト名称（アイテムの追加）
        /// </summary>
        private string _addToolStripMenuItemText;
        /// <summary>
        /// コンテキストメニュー名称（アイテムの削除）
        /// </summary>
        private string _deleteToolStripItemText;
        /// <summary>
        /// メニューリスト名称（アイテムの削除）
        /// </summary>
        private string _deleteToolStripMenuItemText;
        /// <summary>
        /// メニューリスト名称（編集）
        /// </summary>
        private string _editEToolStripMenuItemText;
        /// <summary>
        /// メニューリスト名称（終了）
        /// </summary>
        private string _endDToolStripMenuItemText;
        /// <summary>
        /// メニューリスト名称（エクスポート）
        /// </summary>
        private string _exportToolStripMenuItemText;
        /// <summary>
        /// メニューリスト名称（ファイル）
        /// </summary>
        private string _fileFToolStripMenuItemText;
        /// <summary>
        /// メニューリスト名称（インポート）
        /// </summary>
        private string _importToolStripMenuItemText;
        /// <summary>
        /// メニューリスト名称（保存）
        /// </summary>
        private string _saveToolStripMenuItemText;


        /// <summary>
        /// アプリケーション起動時、設定ファイルの情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessageDouble
        {
            get { return _errorMessageDouble; }
            set { _errorMessageDouble = value; }
        }
        /// <summary>
        /// アプリケーション起動時、設定ファイルの情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessage_Item_IniFile
        {
            get { return _errorMessage_Item_IniFile; }
            set { _errorMessage_Item_IniFile = value; }
        }
        /// <summary>
        /// アプリケーション起動時、必要なモジュールが不足しているため起動できないときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessageFujiFlexaDll
        {
            get { return _errorMessageFujiFlexaDll; }
            set { _errorMessageFujiFlexaDll = value; }
        }

        /// <summary>
        /// アプリケーション起動時、画面表示のための情報取得にてエラーが発生したときに本メッセージダイアログを表示し、OKボタンによりアプリケーションを終了します。
        /// </summary>
        public string ErrorMessageLayoutIniFile
        {
            get { return _errorMessageLayoutIniFile; }
            set { _errorMessageLayoutIniFile = value; }
        }

        /// <summary>
        /// アプリケーション起動時、定義ファイルに定義されていないアイテムの表示設定がされている場合に本メッセージを表示します。
        /// </summary>
        public string ErrorMessageLayoutIniFileDifference
        {
            get { return _errorMessageLayoutIniFileDifference; }
            set { _errorMessageLayoutIniFileDifference = value; }
        }

        /// <summary>
        /// レイアウト保存時にエラーが発生したときに本メッセージダイアログを表示し保存を中断します。
        /// </summary>
        public string ErrorMessageSaveLayout
        {
            get { return _errorMessageSaveLayout; }
            set { _errorMessageSaveLayout = value; }
        }

        /// <summary>
        /// インポート/エクスポート時のファイル選択ダイアログのファイル種で表示
        /// </summary>
        public string FileReadDlgFilter
        {
            get { return _fileReadDlgFilter; }
            set { _fileReadDlgFilter = value; }
        }

        /// <summary>
        /// レイアウト設定情報をエクスポートする際のダイアログタイトル
        /// </summary>
        public string FileReadDlgTitle
        {
            get { return _fileReadDlgTitle; }
            set { _fileReadDlgTitle = value; }
        }

        /// <summary>
        /// レイアウト設定情報をエクスポートする際のダイアログタイトル
        /// </summary>
        public string FileSaveDlgTitle
        {
            get { return _fileSaveDlgTitle; }
            set { _fileSaveDlgTitle = value; }
        }

        /// <summary>
        /// アプリケーション起動時にDashboardが起動時のメッセージ
        /// </summary>
        public string MessageDashboardEnd
        {
            get { return _messageDashboardEnd; }
            set { _messageDashboardEnd = value; }
        }

        /// <summary>
        /// インポート実行時の確認メッセージ
        /// </summary>
        public string MessageImportLayout
        {
            get { return _messageImportLayout; }
            set { _messageImportLayout = value; }
        }

        /// <summary>
        /// レイアウト設定情報をエクスポート時レイアウトの保存確認メッセー
        /// </summary>
        public string MessageLayoutCfg
        {
            get { return _messageLayoutCfg; }
            set { _messageLayoutCfg = value; }
        }

        /// <summary>
        /// 終了時にレイアウトが変更されていた場合のメッセージ
        /// </summary>
        public string MessageSaveLayout
        {
            get { return _messageSaveLayout; }
            set { _messageSaveLayout = value; }
        }

        //コントロール名


        /// <summary>
        /// ツール名称
        /// </summary>
        public string AppTitleText
        {
            get { return _appTitleText; }
            set { _appTitleText = value; }
        }
        /// <summary>
        /// コンテキストメニュー名称（アイテムの追加
        /// </summary>
        public string AddToolStripItemText
        {
            get { return _addToolStripItemText; }
            set { _addToolStripItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（アイテムの追加）
        /// </summary>
        public string AddToolStripMenuItemText
        {
            get { return _addToolStripMenuItemText; }
            set { _addToolStripMenuItemText = value; }
        }

        /// <summary>
        /// コンテキストメニュー名称（アイテムの削除）
        /// </summary>
        public string DeleteToolStripItemText
        {
            get { return _deleteToolStripItemText; }
            set { _deleteToolStripItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（アイテムの削除）
        /// </summary>
        public string DeleteToolStripMenuItemText
        {
            get { return _deleteToolStripMenuItemText; }
            set { _deleteToolStripMenuItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（編集）
        /// </summary>
        public string EditEToolStripMenuItemText
        {
            get { return _editEToolStripMenuItemText; }
            set { _editEToolStripMenuItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（終了）
        /// </summary>
        public string EndDToolStripMenuItemText
        {
            get { return _endDToolStripMenuItemText; }
            set { _endDToolStripMenuItemText = value; }
        }


        /// <summary>
        /// メニューリスト名称（エクスポート）
        /// </summary>
        public string ExportToolStripMenuItemText
        {
            get { return _exportToolStripMenuItemText; }
            set { _exportToolStripMenuItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（ファイル）
        /// </summary>
        public string FileFToolStripMenuItemText
        {
            get { return _fileFToolStripMenuItemText; }
            set { _fileFToolStripMenuItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（インポート）
        /// </summary>
        public string ImportToolStripMenuItemText
        {
            get { return _importToolStripMenuItemText; }
            set { _importToolStripMenuItemText = value; }
        }

        /// <summary>
        /// メニューリスト名称（保存）
        /// </summary>
        public string SaveToolStripMenuItemText
        {
            get { return _saveToolStripMenuItemText; }
            set { _saveToolStripMenuItemText = value; }
        }

        public MultipleLanguages()
        {
            ProcessLogger.StartMethod();

            ProcessLogger.Logger.Info("LanguageType" + MainForm.Language);

            //指定した言語の値でエラーメッセージ設定
            switch (MainForm.Language)
            {
                //日本語
                case LANGUAGE_SELECT_JP:
                    //メッセージ設定
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resource_jp.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resource_jp.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resource_jp.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resource_jp.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resource_jp.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resource_jp.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resource_jp.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resource_jp.MessageSaveLayout;
                    //コントロール名設定
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resource_jp.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resource_jp.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resource_jp.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resource_jp.SaveToolStripMenuItem;

                    break;

                //簡体字
                case LANGUAGE_SELECT_SC:
                    //メッセージ設定
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.MessageSaveLayout;
                    //コントロール名設定
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHS.SaveToolStripMenuItem;

                    break;
                //繁体字
                case LANGUAGE_SELECT_TC:
                    //メッセージ設定
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.MessageSaveLayout;
                    //コントロール名設定
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources_zh_CHT.SaveToolStripMenuItem;

                    break;
                //英語
                default:
                    //メッセージ設定
                    _errorMessageDouble = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageDouble;
                    _errorMessage_Item_IniFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessage_Item_IniFile;
                    _errorMessageFujiFlexaDll = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageFujiFlexaDll;
                    _errorMessageLayoutIniFile = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageLayoutIniFile;
                    _errorMessageLayoutIniFileDifference = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageLayoutIniFileDifference;
                    _errorMessageSaveLayout = global::DashboardCustomizeTool.Properties.Resources.ErrorMessageSaveLayout;
                    _fileReadDlgFilter = global::DashboardCustomizeTool.Properties.Resources.FileReadDlgFilter;
                    _fileReadDlgTitle = global::DashboardCustomizeTool.Properties.Resources.FileReadDlgTitle;
                    _fileSaveDlgTitle = global::DashboardCustomizeTool.Properties.Resources.FileSaveDlgTitle;
                    _messageDashboardEnd = global::DashboardCustomizeTool.Properties.Resources.MessageDashboardEnd;
                    _messageImportLayout = global::DashboardCustomizeTool.Properties.Resources.MessageImportLayout;
                    _messageLayoutCfg = global::DashboardCustomizeTool.Properties.Resources.MessageLayoutCfg;
                    _messageSaveLayout = global::DashboardCustomizeTool.Properties.Resources.MessageSaveLayout;
                    //コントロール名設定
                    AppTitleText = global::DashboardCustomizeTool.Properties.Resources.AppTitle;
                    AddToolStripItemText = global::DashboardCustomizeTool.Properties.Resources.AddToolStripItem;
                    AddToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.AddToolStripMenuItem;
                    DeleteToolStripItemText = global::DashboardCustomizeTool.Properties.Resources.DeleteToolStripItem;
                    DeleteToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.DeleteToolStripMenuItem;
                    EditEToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.EditEToolStripMenuItem;
                    EndDToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.EndDToolStripMenuItem;
                    ExportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ExportToolStripMenuItem;
                    FileFToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.FileFToolStripMenuItem;
                    ImportToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.ImportToolStripMenuItem;
                    SaveToolStripMenuItemText = global::DashboardCustomizeTool.Properties.Resources.SaveToolStripMenuItem;

                    break;
            }

            ProcessLogger.EndMethod();

        }

    }
}
