using DashboardCustomizeTool.CommonClass;
using DashboardCustomizeTool.Data;
using Infragistics.Win.UltraWinDock;
using Ionic.Zip;
using Ionic.Zlib;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Xml;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// レイアウト情報構造体
    /// </summary>
    struct LayoutData
    {
        /// <summary>
        /// フレーム名 (日本語)
        /// </summary>
        public string name;
        /// <summary>
        /// フレーム名 (英語)
        /// </summary>
        public string name_en;
        /// <summary>
        /// プラグイン名
        /// </summary>
        public string plugin;
        /// <summary>
        /// 表示色
        /// </summary>
        public string color;
        /// <summary>
        /// 表示状態 ('0':非表示、'1':表示)
        /// </summary>
        public string disp;
        /// <summary>
        /// フレーム座標 (TOP)
        /// </summary>
        public string top;
        /// <summary>
        /// フレーム座標 (LEFT)
        /// </summary>
        public string left; 
        /// <summary>
        /// フレーム座標 (BOTTOM)
        /// </summary>
        public string bottom;
        /// <summary>
        /// フレーム座標 (RIGHT)
        /// </summary>
        public string right;

        public string language;

        public override string ToString()
        {
            switch (language)
            {
                case Define.LANGUAGE_SELECT_JP:
                    return name;
                default:
                    return name_en;
            }
        }
    }

    /// <summary>
    /// メインフォーム
    /// </summary>
    public partial class MainForm : Form
    {
        enum FuncResult
        {
            Failure = -1,
            Cancel = 0,
            Success = 1
        }

        /// <summary>
        /// フレームリスト
        /// </summary>
        private List<LayoutData> frameList = new List<LayoutData>();

        /// <summary>
        /// 変更前レイアウト情報
        /// </summary>
        private List<LayoutData> befLayoutList = new List<LayoutData>();

        /// <summary>
        /// 変更前レイアウト情報の更新許可
        /// </summary>
        private bool isChangeflag = false;

        //メンバー変数
        //セルのデフォルト色
        private Color DEFAULT_CELL_COLOR = ColorTranslator.FromHtml("#FFFFFF");
        //アプリケーション設定ファイル用定数
        private const string APPITEM_INI_SECION_ITEMLIST =   "ItemList";
        private const string APPITEM_INI_KEYCOUNT =   "ItemCount";
        private const string APPITEM_INI_ITEM_NAME = "Item{0:000}";
        private const string APPITEM_INI_ITEM_EN = "Item{0:000}_en";
        private const string APPITEM_INI_ITEM_PLUGIN = "Item{0:000}Plugin";
        private const string APPITEM_INI_ITEM_COLOR = "Item{0:000}Color";
        //レイアウト情報用定数
        private const string LAYOUT_INI_SECTIONL_SUSTEM = "System";
        private const string LAYOUT_INI_SECTIONL_SOCKET = "Socket";
        private const string LAYOUT_DISPLAY = "1";
        private const string LAYOUT_NOTDISPLAY = "0";
        //ZIP
        private const string CFG_DEFAULT_NAME = "adAndonCustomizeToolLayoutInfo.cfg";
        private const string CFG_ENCODEING = "UTF-8";
        private const string ZIP_EXTENSION = ".zip";
        private const string CFG_EXTENSION = ".cfg";
        private const string INITIALDIRECTORY = "InitialDirectory";
        //XMLの項目キー
        private const string XML_ID = "Text";
        private const string XML_OBJECTVALUE = "objectValue";
        private const string PANE_SPACE = "PaneSpace_1";//非表示用ペイン名
        //
        private const string CFG_IDENTNAME_REGEX = @"^adAndonCustomizeToolLayoutInfo_(?<identName>.*)\.cfg$";
        private const string CFG_IDENTNAME_GROUP = "identName";
        //クラス
        private ShowMsgBoxAPI mg = new ShowMsgBoxAPI();
        private Settings appSettings = Settings.GetInstance();
        private ErrorMessage errormsessagr = new ErrorMessage();
        //表示言語クラスの呼び出し
        private MultipleLanguages languageSettings = MultipleLanguages.GetInstance();

        private ConfigIni configIni = ConfigIni.GetInstance();
        private List<string> tagList = new List<string>();

        private ToolStripStatusLabel lblMonitorSizeTitle = new ToolStripStatusLabel();
        private ToolStripStatusLabel lblMonitorSizeValue = new ToolStripStatusLabel();
        private ToolStripStatusLabel lblCfgFilePathValue = new ToolStripStatusLabel();

        /// <summary>
        /// トレースハンドル
        /// </summary>
        static public uint TraceHandle = 0;

        /// <summary>
        /// エラーコード　
        /// </summary>
        private int errorCode = 0;
        /// <summary>
        ///   エラーコード正常値
        /// </summary>
        public const int ProcessNormal = 0;
        /// <summary>
        /// エラーコード不正値
        /// </summary>
        public const int ProcessAbnormal =1;
        /// <summary>
        /// 例外
        /// </summary>
        public const int ProcessException =-1;

        /// <summary>
        /// 言語(デフォルト英語)
        /// </summary>
        static public String Language = Define.LANGUAGE_SELECT_US;

        /// <summary>
        /// cfgファイルパス
        /// </summary>
        private string cfgFilePath;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="cfgPath">cfgファイルパス</param>
        public MainForm(string cfgPath)
        {
            InitializeComponent();

            this.cfgFilePath = cfgPath;
        }

        /// <summary>
        /// フォーム表示前の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form_Load(object sender, System.EventArgs e)
        {
            ProcessLogger.StartMethod();

            ConfigData config = configIni.GetConfigData;// 設定

            // レイアウト領域の設定
            this.layoutBasePanel.BackColor = Color.Gray;// レイアウトベースパネルの背景色
            this.manageLayoutInfo.BackColor = Color.White;// レイアウト領域の背景色
            this.manageLayoutInfo.setMonitorSize(config.MonitorWidth, config.MonitorHeight);
            this.layoutPanelResize();

            // ステータスバーの設定
            this.lblMonitorSizeTitle.Text = this.languageSettings.LayoutAreaSizeText;// 全体サイズ
            this.statusStrip.Items.Add(this.lblMonitorSizeTitle);
            this.statusStrip.Items.Add(this.lblMonitorSizeValue);
            this.statusStrip.Items.Add(this.lblCfgFilePathValue);

            // 多言語設定で取得したコントロール名を設定
            this.ControlNameSettings();

            //ここでメインフォームを前表示に切り替える（メッセージ表示用）
            this.TopMost = true;
            this.TopMost = false;
            List<LayoutData> layoutDataList = new List<LayoutData>();

            // フレームリストを読み込む。
            bool isLoadFrame = this.loadFrameList();
            if (!isLoadFrame)
            {
                this.errorCode = MainForm.ProcessAbnormal;
                return;
            }

            try
            {
                // コントロールの再描画許可
                this.SetStyle(ControlStyles.ResizeRedraw, true);

                string layoutIniFullPath = this.appSettings.DashboardBackupIniFilePath;// Dashboard.ini
                if (System.IO.File.Exists(this.appSettings.Dashboard2BackupIniFilePath))
                {
                    layoutIniFullPath = this.appSettings.Dashboard2BackupIniFilePath;// Dashboard2.ini
                }

                // レイアウトツール 設定ファイルをテンポラリに読み込む。
                if (string.IsNullOrEmpty(this.cfgFilePath))
                {
                    this.cfgFilePath = this.appSettings.LayoutCfgFilePath;
                }
                this.loadLayoutCfg(this.cfgFilePath);

                // 開いているファイル
                this.setLayoutCfgPath(this.cfgFilePath);

                // 変更前のレイアウト情報を更新
                this.isChangeflag = true;
                this.manageLayoutInfo.Invalidate();

                this.checkItemAppLayout();
            }
            catch (Exception ex)
            {
                //エラー処理
                this.errorCode = MainForm.ProcessAbnormal;
                //トレース、エラメッセージ処理へ
                errormsessagr.CreateErrorMessage(ex, true, this);
                ProcessLogger.ExceptionOccurred(ex);
                return;
            }
            finally
            {
                this.WindowState = FormWindowState.Maximized;
                ProcessLogger.EndMethod();
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form_Shown(object sender, EventArgs e)
        {
            ProcessLogger.StartMethod();

            //エラーがあれば処理終了
            if (!(this.errorCode == MainForm.ProcessNormal))
            {
                //例外などで処理が終了していない場合ここで終了させる
                Application.Exit();
            }
            else
            {
                //初期設定が完了後に設定
                this.appItemGridView.AlternatingRowsDefaultCellStyle.BackColor = Color.Silver;
                //起動時に現在のレイアウトの座標情報を取得する。
                this.isChangeflag = true;
            }

            ProcessLogger.EndMethod();

        }

        /// <summary>
        /// 開くボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OpenToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // 変更がある場合は保存確認する。
            bool isChange = this.layoutChangeCheck();
            if (isChange)
            {
                DialogResult dlgRet = mg.ShowMsgBox(this, this.Text, languageSettings.MessageSaveLayout, MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                switch (dlgRet)
                {
                    case DialogResult.Yes:
                        // レイアウトを保存して開く
                        FuncResult saveResult = this.saveLayoutInfo();
                        if (FuncResult.Failure.Equals(saveResult))
                        {
                            mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageSaveLayoutFile, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                            return;
                        }
                        break;
                    case DialogResult.No:
                        // レイアウトを保存せず開く
                        break;
                    default:
                        // キャンセル
                        return;
                }
            }

            // 設定ファイルは上書きせずにレイアウトを読み込む。
            FuncResult ret = this.openLayoutInfo();
            switch (ret)
            {
                case FuncResult.Success:
                    mg.ShowMsgBox(this, this.Text, languageSettings.MessageOpenComplete, MessageBoxButtons.OK, MessageBoxIcon.Information);
                    break;
                case FuncResult.Failure:
                    mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageOpen, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    break;
                default:
                    break;
            }
        }

        /// <summary>
        /// 保存ボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // レイアウトを保存する。
            FuncResult ret = this.saveLayoutInfo();
            switch (ret)
            {
                case FuncResult.Success:
                    mg.ShowMsgBox(this, this.Text, languageSettings.MessageSaveComplete, MessageBoxButtons.OK, MessageBoxIcon.Information);
                    break;
                case FuncResult.Failure:
                    mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageSaveLayoutFile, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    break;
                default:
                    break;
            }
        }

        /// <summary>
        /// 設備識別子名で保存ボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // レイアウトを設備識別子名で保存する。
            FuncResult ret = this.saveAsLayoutInfo();
            switch (ret)
            {
                case FuncResult.Success:
                    mg.ShowMsgBox(this, this.Text, languageSettings.MessageSaveComplete, MessageBoxButtons.OK, MessageBoxIcon.Information);
                    break;
                case FuncResult.Failure:
                    mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageSaveLayoutFile, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    break;
                default:
                    break;
            }
        }

        /// <summary>
        /// インポートボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ImportToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // レイアウトを読み込む。
            FuncResult ret = this.importLayoutInfo();
            switch (ret)
            {
                case FuncResult.Success:
                    mg.ShowMsgBox(this, this.Text, languageSettings.MessageImportComplete, MessageBoxButtons.OK, MessageBoxIcon.Information);
                    break;
                case FuncResult.Failure:
                    mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageImport, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    break;
                default:
                    break;
            }
        }
        
        /// <summary>
        /// エクスポートボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ExportToolStripMenuItem_Click(object sender, EventArgs e)
        {
            FuncResult ret = this.exportLayoutInfo();
            switch (ret)
            {
                case FuncResult.Success:
                    mg.ShowMsgBox(this, this.Text, languageSettings.MessageExportComplete, MessageBoxButtons.OK, MessageBoxIcon.Information);
                    break;
                case FuncResult.Failure:
                    mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageExport, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    break;
                default:
                    break;
            }
        }

        /// <summary>
        /// 設定ボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ConfigToolStripMenuItem_Click(object sender, EventArgs e)
        {
            using (ConfigDialog dialog = new ConfigDialog())
            {
                dialog.StartPosition = FormStartPosition.CenterParent;
                dialog.LayoutAreaWidth = configIni.GetConfigData.MonitorWidth;
                dialog.LayoutAreaHeight = configIni.GetConfigData.MonitorHeight;

                // 設定ダイアログを表示
                DialogResult result = dialog.ShowDialog(this);
                if (DialogResult.Cancel.Equals(result))
                {
                    return;
                }

                int layoutAreaWidth = dialog.LayoutAreaWidth;
                int layoutAreaHeight = dialog.LayoutAreaHeight;

                // 設定を更新して保存する。
                ConfigData config = configIni.GetConfigData;
                config.MonitorWidth = layoutAreaWidth;
                config.MonitorHeight = layoutAreaHeight;
                configIni.SaveFile();

                // レイアウト領域のサイズ設定
                this.manageLayoutInfo.setMonitorSize(config.MonitorWidth, config.MonitorHeight);
                this.layoutPanelResize();
            }
        }
        
        /// <summary>
        /// 終了ボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EndToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //アプリ終了処理呼び出し
            this.ExitApp();
        }

        /// <summary>
        /// フォームの閉じるボタンを押下時のイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form_FormClosing(object sender, FormClosingEventArgs e)
        {
            //メッセージを表示する。
            mg = new ShowMsgBoxAPI();
            bool blayout = false;
            //エラーで終了の場合はそのまま終了する
            if (errorCode == MainForm.ProcessNormal)
            {
                //通常終了ならレイアウトの変更チェックを行う
                blayout = this.layoutChangeCheck();
            }
            //レイアウト変更がされた場合
            if (blayout)
            {
                //レイアウトを保存するかの確認メッセージ表示
                string maintitel = languageSettings.MessageSaveLayout;
                //iniファイル、XMLファイルの保存処理
                DialogResult sele = mg.ShowMsgBox(this, this.Text, maintitel, MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                switch (sele)
                {
                    case DialogResult.Yes:
                        this.saveLayoutInfo();
                        //アプリケーションを終了する。
                        ProcessLogger.Logger.Info("Application Ending");
                        break;
                    case DialogResult.No:
                        ProcessLogger.Logger.Info("Go on ...");
                        break;
                    case DialogResult.None:
                        e.Cancel = true;
                        break;

                    case DialogResult.Cancel:
                        //Cancel 
                        e.Cancel = true;
                        break;
                    default:
                        //トレース処理終了
                        ProcessLogger.Logger.Info("Trace Process End");
                        break;
                }
            }
            else
            {
                //トレース処理終了
                ProcessLogger.Logger.Info("Trace Process End");
            }
        }

        /// <summary>
        /// 追加ボタンクリックイベント（メニュー）
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AddAToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //レイアウトアイテム追加処理呼び出し
            this.AddLayoutItem();

        }

        /// <summary>
        /// 削除ボタンクリックイベント（メニュー）
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DeleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //選択されたレイアウトのアイテムを削除する処理の呼び出し
            this.RemoveLayoutItem();
        }

        /// <summary>
        /// アイテム追加ボタンクリックイベント（セル上）
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AddToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            //レイアウトにアイテム追加処理呼び出し
            this.AddLayoutItem();
        }

        /// <summary>
        /// アイテム削除ボタンクリックイベント（セル上）
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DeleteToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            //選択されたレイアウトのアイテムを削除する処理の呼び出し
            this.RemoveLayoutItem();
        }

        /// <summary>
        /// セル選択変更時
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void appItemGridView_SelectionChanged(object sender, EventArgs e)
        {
            this.SelectLayout();
        }

        /// <summary>
        /// レイアウトがアクティブ化したときの処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ultraDockManager_PaneActivate(object sender, ControlPaneEventArgs e)
        {
            //DLL化したら削除予定
            //選択されたコントロールがアイテム一覧以外の時レイアウトの枠線表示を初期化する。
            if (!(string.Equals(e.Pane.Text, this.appItemGridView.Name)))
            {
                foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
                {
                    //枠線をWheatに修正する。
                    pane.Settings.PaneAppearance.BackColor = Color.Wheat;
                }
            }
            //セルの選択を切り替える
            //ペインを格納しているコントロールのコレクションないを検索
            for (int i = 0; i < this.appItemGridView.Rows.Count; i++)
            {
                LayoutData dat = (LayoutData)this.appItemGridView.Rows[i].Cells[0].Value;
                if (e.Pane.Control == null || e.Pane.Control.Tag == null)
                {
                    continue;
                }
                if (e.Pane.Control.Tag.ToString() == dat.plugin)
                {
                    //アクティブになったレイアウトに連動して選択されているセルが変更される。
                    this.appItemGridView.Rows[i].Selected = true;
                    //枠線を表示する
                    e.Pane.Settings.PaneAppearance.BackColor = Color.Red;
                    //デフォルト値に設定すると何故かうまくいかないため、アクティブ時に設定
                    e.Pane.Settings.PaddingBottom = 2;
                    e.Pane.Settings.PaddingLeft = 2;
                    e.Pane.Settings.PaddingRight = 2;
                    e.Pane.Settings.PaddingTop = 2;
                }
            }
        }

        /// <summary>
        /// DataGridViewのCellMouseClickイベント・ハンドラ
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void appItemGridView_CellMouseClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            // 右ボタンのクリックか？
            if (e.Button == MouseButtons.Right)
            {
                // ヘッダ以外のセルか？
                if (e.ColumnIndex >= 0 && e.RowIndex >= 0)
                {
                    // 右クリックされたセル
                    DataGridViewCell cell = this.appItemGridView[e.ColumnIndex, e.RowIndex];
                    // セルの選択状態を反転
                    cell.Selected = true;
                    this.LayoutActive();
                    //もしもすでにアイテムが追加させれている状態なら追加ボタンを非アクティブにする。                 
                    if (Convert.ToBoolean(this.appItemGridView[e.ColumnIndex,e.RowIndex].Tag))
                    {
                        //アイテム削除をアクティブにする
                        this.contextMenuStrip.Items[0].Enabled = false;
                        this.contextMenuStrip.Items[1].Enabled = true;
                    }
                    else
                    {
                        //アイテム追加をアクティブにする
                        this.contextMenuStrip.Items[0].Enabled = true;
                        this.contextMenuStrip.Items[1].Enabled = false;
                    }
                    this.contextMenuStrip.Show(System.Windows.Forms.Cursor.Position.X, System.Windows.Forms.Cursor.Position.Y);
                }
            }
        }

        /// <summary>
        /// セルの枠線表示処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dataGridView1_CellPainting(object sender, System.Windows.Forms.DataGridViewCellPaintingEventArgs e)
        {
            //枠を表示させるセルの選択（アイテム名）
            if (e.ColumnIndex >= 0 && e.RowIndex >= 0 && (e.PaintParts & DataGridViewPaintParts.Background) == DataGridViewPaintParts.Background)
            {
                //境界線を取得して境界線を塗りつぶす
                e.Graphics.FillRectangle(new SolidBrush(e.CellStyle.BackColor), e.CellBounds);
                //セルが選択されているか確認
                if ((e.PaintParts & DataGridViewPaintParts.SelectionBackground) == DataGridViewPaintParts.SelectionBackground && (e.State & DataGridViewElementStates.Selected) == DataGridViewElementStates.Selected)
                {
                    //境界線を赤色に、表示させる境界線の幅指定
                    e.Graphics.DrawRectangle(new Pen(Color.Red, 2), e.CellBounds.X + 1, e.CellBounds.Y + 1, e.CellBounds.Width - 3, e.CellBounds.Height - 3);
                }
                DataGridViewPaintParts pParts = e.PaintParts & ~DataGridViewPaintParts.Background;
                e.Paint(e.ClipBounds, pParts);
                //描画が完了したことを知らせる
                e.Handled = true;
            }
            else
            {
                //境界線を取得して境界線を塗りつぶす
                e.Graphics.FillRectangle(new SolidBrush(e.CellStyle.BackColor), e.CellBounds);
                //セルが選択されているか確認
                if (Convert.ToBoolean(appItemGridView[0, e.RowIndex].Tag))
                {
                    //境界線を赤色に、表示させる境界線の幅指定
                    e.Graphics.DrawRectangle(new Pen(Color.Black, 2), e.CellBounds.X + 1, e.CellBounds.Y + 1, e.CellBounds.Width - 3, e.CellBounds.Height - 3);
                }
                DataGridViewPaintParts pParts = e.PaintParts & ~DataGridViewPaintParts.Background;
                e.Paint(e.ClipBounds, pParts);
                //描画が完了したことを知らせる
                e.Handled = true;
            }
        }

        /// <summary>
        /// 編集ボタン押下時に表示内容を変更する処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EditEToolStripMenuItem_DropDownOpened(object sender, EventArgs e)
        {
            if (this.appItemGridView.SelectedCells.Count > 0)
            {
                //選択されている行の情報取得
                DataGridViewCell Cell = appItemGridView.SelectedCells[0];
                //レイアウトが表示されているかチェック
                if (Convert.ToBoolean(Cell.Tag))
                {
                    //レイアウトが追加されているなら削除をアクティブに
                    this.AddToolStripMenuItem.Enabled = false;
                    this.DeleteToolStripMenuItem.Enabled = true;
                }
                else
                {
                    //それ以外なら追加
                    this.AddToolStripMenuItem.Enabled = true;
                    this.DeleteToolStripMenuItem.Enabled = false;
                }
            }
            else
            {
                this.AddToolStripMenuItem.Enabled = false;
                this.DeleteToolStripMenuItem.Enabled = false;
            }
        }

        /// <summary>
        /// フレームリストを読み込み、リスト表示する。
        /// </summary>
        public bool loadFrameList()
        {
            bool result = false;
            try
            {
                // フレームリストファイルの存在を確認する。
                if (!File.Exists(appSettings.ItemListFilePath))
                {
                     throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0021);
                }

                // フレームリストファイルを読み込む。
                IniManager iniManager = new IniManager();
                IniFile2 ini = new IniFile2(appSettings.ItemListFilePath);

                //セッション名指定
                string sSectionName = MainForm.APPITEM_INI_SECION_ITEMLIST;
                string sItemCountKey = MainForm.APPITEM_INI_KEYCOUNT;
                //アイテム数を取得
                string itemcount = ini[sSectionName, sItemCountKey].ToString();
                int sItemCount = 0;
                //アイテム数が取得できたか確認
                if ((string.IsNullOrEmpty(itemcount)) || int.Parse(itemcount) < 1)
                {
                    //例外処理
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0022);
                }
                else
                {
                    sItemCount = int.Parse(itemcount);
                }

                //iniファイル内のItemListをすべて読み込む
                for (int i = 1; i <= sItemCount; i++)
                {
                    string sItemName = string.Format(MainForm.APPITEM_INI_ITEM_NAME, i);
                    string sItemEn = string.Format(MainForm.APPITEM_INI_ITEM_EN, i);
                    string sItemPlugin = string.Format(MainForm.APPITEM_INI_ITEM_PLUGIN, i);
                    string sItemColor = string.Format(MainForm.APPITEM_INI_ITEM_COLOR, i);
                    //取得するキー名を設定
                    string siniItemName = ini[sSectionName, sItemName];
                    string siniItemEn = ini[sSectionName, sItemEn];
                    string siniItemPlugin = ini[sSectionName, sItemPlugin];
                    string sIniItemColor = ini[sSectionName, sItemColor];
                    ProcessLogger.Logger.InfoFormat("*** ini name={0}, name_en={1}, plugin={2}, color={3}", siniItemName, siniItemEn, siniItemPlugin, sIniItemColor);
                    LayoutData dat = new LayoutData();
                    dat.name = ini[sSectionName, sItemName];
                    dat.name_en = ini[sSectionName, sItemEn];
                    dat.plugin = ini[sSectionName, sItemPlugin];
                    dat.color = ini[sSectionName, sItemColor];
                    dat.language = MainForm.Language;
                    //iniファイルを正常に取得できたか確認
                    //キー項目に値があるかチェック

                    if (((!string.IsNullOrEmpty(siniItemName)) && (!(string.IsNullOrEmpty(sIniItemColor))) && siniItemName.Length  <= 128 && sIniItemColor.Length == 7) == true)
                    {
                        this.frameList.Add(dat);

                        //値があればアイテムを追加する
                        this.appItemGridView.Rows.Add(dat);
                        this.appItemGridView.Rows[this.appItemGridView.Rows.Count - 1].HeaderCell.Tag = sIniItemColor;
                    }
                    else
                    {
                        //エラーコード設定
                        throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0023);
                    }
                }

                //タグにカラー情報をバインド
                for (int i = 0; i < this.appItemGridView.Rows.Count; i++)
                {
                    //セルに各種情報を指定
                    this.appItemGridView[0, i].Tag = false;
                    this.appItemGridView.Rows[i].HeaderCell.Style.BackColor = DEFAULT_CELL_COLOR;
                    this.appItemGridView.Rows[i].HeaderCell.Style.ForeColor = DEFAULT_CELL_COLOR;
                    this.appItemGridView.Rows[i].HeaderCell.Style.SelectionBackColor = DEFAULT_CELL_COLOR;
                    this.appItemGridView.Rows[i].HeaderCell.Style.SelectionForeColor = DEFAULT_CELL_COLOR;
                }

                result = true;
            }
            catch (Exception e)
            {
                errormsessagr.CreateErrorMessage(e, true, this);
                ProcessLogger.ExceptionOccurred(e);
            }
            return result;
        }

        /// <summary>
        /// レイアウトの削除処理
        /// </summary>
        private void RemoveLayoutItem()
        {
            //セルの色表示を初期化する。
            DataGridViewCell CellColor = appItemGridView.SelectedCells[0];
            //選択時の背景色を初期化
            appItemGridView.SelectedCells[0].Tag = false;
            //選択しているセルの情報取得
            DataGridViewCell CellName = appItemGridView.SelectedCells[0];
            LayoutData dat = (LayoutData)CellName.Value;
            this.manageLayoutInfo.RemoveLayoutItem(dat.plugin);
            //カラーの枠線の更新のため、再描画する。
            this.appItemGridView.Refresh();
        }

        /// <summary>
        /// アイテム一覧で選択された場合の処理
        /// </summary>
        private void SelectLayout()
        {
            ProcessLogger.StartMethod();

            if (this.appItemGridView.SelectedCells.Count > 0)
            {
                DataGridViewCell CellName = appItemGridView.SelectedCells[0];
                //セルに表示されているアイテム名
                LayoutData dat = (LayoutData)CellName.Value;
                this.manageLayoutInfo.PaneActiveBorder(dat.plugin);
            }
            else
            {
                ProcessLogger.Logger.Warn("No Select");
                this.manageLayoutInfo.PaneActiveBorder("");
            }

            ProcessLogger.EndMethod();
        }

        /// <summary>
        /// レイアウトにアイテムを追加する処理
        /// </summary>
        private void AddLayoutItem()
        {
            //ヘッダー部分の背景色の設定変更
            Color backcolor;
            backcolor = ColorTranslator.FromHtml(this.appItemGridView.Rows[appItemGridView.SelectedCells[0].RowIndex].HeaderCell.Tag.ToString());
            this.appItemGridView.Rows[appItemGridView.SelectedCells[0].RowIndex].HeaderCell.Style.BackColor = backcolor;
            this.appItemGridView.Rows[appItemGridView.SelectedCells[0].RowIndex].HeaderCell.Style.ForeColor = backcolor;         
            this.appItemGridView.Rows[appItemGridView.SelectedCells[0].RowIndex].HeaderCell.Style.SelectionBackColor = backcolor;
            this.appItemGridView.Rows[appItemGridView.SelectedCells[0].RowIndex].HeaderCell.Style.SelectionForeColor = backcolor;
            appItemGridView.SelectedCells[0].Tag = true;
            //レイアウトの追加処理
            LayoutData dat = (LayoutData)appItemGridView.SelectedCells[0].Value;
            this.manageLayoutInfo.AddLayoutItem(dat.plugin, dat.ToString(), backcolor);
            //画面の再描画
            this.appItemGridView.Refresh();
            //選択表示処理
            SelectLayout();
        }

        /// <summary>
        /// 表示されていないレイアウトを追加するメソッド
        /// </summary>
        /// <param name="iniList"></param>
        private int autoAddLayoutItem(string layoutXmlPath)
        {
            try
            {
                if (File.Exists(layoutXmlPath))
                {
                    XmlNode root = XmlFile.LoadFile(layoutXmlPath);

                    // レイアウト情報を更新変換する。
                    this.updateDockableControlPanes(ref root);

                    // 更新後のレイアウト情報をテンポラリファイルに保存する。
                    XmlFile.SaveFile(this.appSettings.LayoutTmpFilePath, root);
                    FileUtils.overwriteCopyFile(this.appSettings.LayoutTmpFilePath, layoutXmlPath);

                    //一番最初に空のペインを追加する(ペインの下部追加対策)
                    this.manageLayoutInfo.AddLayoutItem(PANE_SPACE, PANE_SPACE, this.DEFAULT_CELL_COLOR);
                    //非表示状態にする。
                    this.manageLayoutInfo.RemoveLayoutItem(PANE_SPACE);

                    //表示しているアイテム件数を取得
                    for (int i = this.tagList.Count - 1; i >= 0; i--)
                    {
                        //iniファイルのアイテム情報取得
                        foreach (LayoutData data in this.frameList)
                        {
                            //XMLとiniで一致するアイテム名だけ追加する。
                            if (string.Equals(data.plugin, this.tagList[i]))
                            {
                                //ＸＭＬと一致するアイテムを追加
                                //レイアウトに送る、背景色情報取得
                                Color backcolor = ColorTranslator.FromHtml(data.color.ToString());

                                this.manageLayoutInfo.AddLayoutItem(data.plugin, data.ToString(), backcolor);
                                this.CelldisplaycolorCheck(data.plugin, appItemGridView);
                                this.appItemGridView.Refresh();
                                break;
                            }
                        }
                    }
                }
                return MainForm.ProcessNormal;
            }
            catch (Exception e)
            {
                ProcessLogger.ExceptionOccurred(e);
                return MainForm.ProcessAbnormal;
            }
        }

        /// <summary>
        /// レイアウトをセルにあわせてアクティブかするメソッド
        /// </summary>
        public void LayoutActive()
        {
            //アクティブなセルを捜索アクティブな行を取得
            foreach (DataGridViewRow row in this.appItemGridView.Rows)
            {
                //選択されているセルを検知
                if (row.Selected)
                {
                    //選択されているセル関連するペインを赤枠表示にする
                    LayoutData dat = (LayoutData)row.Cells[0].Value;
                    this.manageLayoutInfo.PaneActiveBorder(dat.plugin);
                }
            }
        }

        /// <summary>
        /// セルの色表示のチェック
        /// </summary>
        /// <param name="controlTag">タグ名</param>
        /// <param name="view"></param>
        public void CelldisplaycolorCheck(string controlTag, DataGridView view)
        {
            //表示されているペインの項目のセルの色が表示されているかチェック
            foreach (DataGridViewRow row in this.appItemGridView.Rows)
            {
                //セルとペインをの表示名とValueの値を比べる
                LayoutData dat = (LayoutData)row.Cells[0].Value;
                if (string.Equals(controlTag, dat.plugin))
                {
                    //ヘッダーにマーカ色を追加
                    row.HeaderCell.Style.BackColor = this.CellColorSetting(row.HeaderCell);
                    row.HeaderCell.Style.ForeColor = this.CellColorSetting(row.HeaderCell);
                    row.HeaderCell.Style.SelectionBackColor = this.CellColorSetting(row.HeaderCell);
                    row.HeaderCell.Style.SelectionForeColor = this.CellColorSetting(row.HeaderCell);
                    row.Cells[0].Tag = true;
                }
            }
        }

        /// <summary>
        /// 16進数元にカラー情報を作成するメソッド
        /// </summary>
        /// <param name="cell">16進数（例　#FF0099）</param>
        /// <returns></returns>
        public Color CellColorSetting(DataGridViewCell cell)
        {
            //背景色を設定
            //取得したセル（ItemColor）から背景色取得
            Color backcolor;
            backcolor = ColorTranslator.FromHtml(cell.Tag.ToString());
            //レイアウト表示名、レイアウト背景色、セルの背景色を指定
            return backcolor;
        }

        /// <summary>
        /// ペインの閉じるボタンクリック時のイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="pn"></param>
        private void manageLayoutInfo1_MyClick(object sender, Dockingcontrollayout.ManageLayoutInfo.PaneHidden pn)
        {
            //アイテム一覧の表示を変更する。
            foreach (DataGridViewRow row in this.appItemGridView.Rows)
            {
                //閉じられたコントロール名を取得し、アイテム一覧と比較する
                LayoutData dat = (LayoutData)row.Cells[0].Value;
                if (string.Equals(dat.plugin, pn.PaneName))
                {
                    //ヘッダーのマーカ表示を初期表示に変更
                    row.HeaderCell.Style.BackColor = DEFAULT_CELL_COLOR;
                    row.HeaderCell.Style.ForeColor = DEFAULT_CELL_COLOR;
                    row.HeaderCell.Style.SelectionBackColor = DEFAULT_CELL_COLOR;
                    row.HeaderCell.Style.SelectionForeColor = DEFAULT_CELL_COLOR;
                    row.Cells[0].Tag = false;
                    //削除したペインと同じセルを選択
                    row.Selected = true;
                    //赤枠を表示させる
                    this.manageLayoutInfo.PaneActiveBorder(pn.PaneName);
                    this.appItemGridView.Refresh();
                    //レイアウト側のアクティブ解除
                    this.appItemGridView.Select();
                }
            }
        }

        /// <summary>
        /// データグリッドビューのセルの選択変更イベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="cla"></param>
        private void manageLayoutInfo1_PaneClick(object sender, Dockingcontrollayout.ManageLayoutInfo.PaneHidden cla)
        {
            //データグリッドビューのセルの選択を変更する。
            //アイテム一覧の表示を変更する。
            foreach (DataGridViewRow row in this.appItemGridView.Rows)
            {
                //閉じられたコントロール名を取得し、アイテム一覧と比較する
                LayoutData dat = (LayoutData)row.Cells[0].Value;
                if (string.Equals(dat.plugin, cla.PaneName))
                {
                    //アクティブになったペインにあわせてセルに枠線表示
                    row.Selected = true;
                    //赤枠を表示させる
                    this.manageLayoutInfo.PaneActiveBorder(cla.PaneName);
                }
            }
        }

        /// <summary>
        /// ファイル管理用メソッド　（ファイルチェック、読み取り切り替え、フォルダ生成）
        /// </summary>
        /// <param name="filepath"></param>
        /// <returns></returns>
        private void FileCheck(string filepath,　bool onflag)
        {      
            //読み取り専用解除
            string path = filepath; // ファイル
            // ファイル属性を取得
            FileAttributes fas = File.GetAttributes(path);
            //読み取り専用の切り替え
            if (onflag)
            {
                // ファイル属性に読み取り専用を追加
                fas = fas | FileAttributes.ReadOnly;
            }
            else
            {
                // ファイル属性から読み取り専用を削除
                fas = fas & ~FileAttributes.ReadOnly;
            }
            // ファイル属性を設定
            File.SetAttributes(path, fas);
        }

        /// <summary>
        /// レイアウトの変更チェック
        /// </summary>
        private bool layoutChangeCheck()
        {
            bool result = false;

            //保存時のレイアウト情報取得
            foreach (LayoutData layoutData in this.befLayoutList)
            {
                List<string> layoutList = this.manageLayoutInfo.CheckPanelItem(layoutData.plugin);

                // 非表示のレイアウトならチェックをスルーする
                if (layoutList.Count > 0)
                {
                    // 変更前レイアウト情報で非表示になっている場合は変更あり
                    if (string.Equals(layoutData.disp, LAYOUT_NOTDISPLAY))
                    {
                        result = true;
                        break;
                    }

                    // 座標が同じか確認
                    if (!string.Equals(layoutList[0], layoutData.top) || !string.Equals(layoutList[1], layoutData.left) || !string.Equals(layoutList[2], layoutData.bottom) || !string.Equals(layoutList[3], layoutData.right))
                    {
                        result = true;
                        break;
                    }
                }
            }

            return result;
        }

        //---------------------------------------------------------------------------
        //Copyright (c) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.
        //---------------------------------------------------------------------------
        /// <summary>
        /// Zipファイルを圧縮する 
        /// </summary>
        /// <param name="path1">圧縮対象のディレクトリ</param>
        /// <param name="path2">出力先のZIPファイルまでのパス</param>
        private int CompressZip(string xmlpath, string inipath, string cfgpath)
        {
            int error = MainForm.ProcessNormal;
            try
            {
                using (ZipFile zip = new ZipFile(Encoding.GetEncoding(CFG_ENCODEING)))
                {
                    // 圧縮レベルを設定
                    zip.CompressionLevel = CompressionLevel.BestCompression;
                    // ファイルを追加
                    if (System.IO.File.Exists(xmlpath) && (System.IO.File.Exists(inipath)))
                    {
                        zip.AddFile(xmlpath, "");
                        zip.AddFile(inipath, "");
                        // ZIPファイルを保存
                        zip.Save(cfgpath);
                    }
                    else
                    {
                        //エラーコードを返す
                        error = MainForm.ProcessAbnormal;
                    }
                }
                return error;
            }
            catch (Exception)
            {
                return MainForm.ProcessAbnormal;
            }
        }

        /// <summary>
        /// Zipファイルを解凍する
        /// </summary>
        /// <param name="path1"></param>
        /// <param name="path2"></param>
        /// <param name="path3"></param>
        private int DecompressionZip(string path1, string path2)
        {
            try
            {
                if (System.IO.File.Exists(path1))
                {
                    //(1)ZIPファイルを読み込み
                    using (ZipFile zip = ZipFile.Read(path1))
                    {
                        //(2)解凍時に既にファイルがあったら上書きする設定
                        zip.ExtractExistingFile = ExtractExistingFileAction.OverwriteSilently;
                        //(3)全て解凍する
                        zip.ExtractAll(path2);
                    }
                }
                else
                {
                    return MainForm.ProcessAbnormal;
                }
            }
            catch (Exception)
            {
                return MainForm.ProcessAbnormal;
            }
            return MainForm.ProcessNormal;
        }

        /// <summary>
        /// レイアウトファイルの確認メソッド
        /// </summary>
        /// <param name="dashboardNo">1 または 2</param>
        /// <param name="folderpath"></param>
        /// <returns></returns>
        private int LayoutIniCheck(int dashboardNo, string inifilepath)
        {
            int errorcode = MainForm.ProcessNormal;

            try
            {
                List<LayoutData> iniList = new List<LayoutData>();

                //アイテム一覧を取得
                foreach (DataGridViewRow row in this.appItemGridView.Rows)
                {
                    LayoutData ldata = (LayoutData)row.Cells[0].Value;
                    iniList.Add(ldata);
                }

                //レイアウト情報読み込み
                IniManager inimanager = new IniManager();
                iniList = inimanager.DashboardIniRead(dashboardNo, inifilepath, iniList);

                foreach (LayoutData Data in iniList)
                {
                    switch (Data.disp.ToString())
                    {
                        case LAYOUT_DISPLAY:
                            // カンマ区切りで分割して配列に格納する
                            string[] stArrayData = Data.top.ToString().Split('/');
                            if ((double.Parse(stArrayData[0]) / double.Parse(stArrayData[1])) > 1)
                            {
                                //メッセージ表示
                                throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0042);

                            }
                            //レフト座標の値チェック
                            stArrayData = Data.left.ToString().Split('/');
                            if ((double.Parse(stArrayData[0]) / double.Parse(stArrayData[1])) > 1)
                            {
                                throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0042);

                            }
                            //ボトム座標の値チェック
                            stArrayData = Data.bottom.ToString().Split('/');
                            if ((double.Parse(stArrayData[0]) / double.Parse(stArrayData[1])) > 1)
                            {
                                //メッセージ表示
                                throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0042);

                            }
                            //ライト座標の値チェック
                            stArrayData = Data.right.ToString().Split('/');
                            if ((double.Parse(stArrayData[0]) / double.Parse(stArrayData[1])) > 1)
                            {
                                //メッセージ表示
                                throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0042);

                            }
                            break;
                        case LAYOUT_NOTDISPLAY:

                            break;
                        default:
                            //ＤＩＳＰの値が0、1以外の場合
                            throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0042);
                    }
                }
            }
            catch (Exception e)
            {
                //エラーメッセージ表示、トレースログ書き込み
                errormsessagr.CreateErrorMessage(e, true, this);
                ProcessLogger.ExceptionOccurred(e);
                //エラーコードを戻す
                return MainForm.ProcessAbnormal;
            }
            return errorcode;
        }

        /// <summary>
        /// XMLレイアウトファイルの整合性チェック
        /// </summary>
        /// <param name="filepath">XMLフルパス</param>
        /// <returns>0:正常　1:不正</returns>
        private int LayouyXmlCheck(string filepath)
        {
            FileStream fStream = new FileStream(filepath, FileMode.Open, FileAccess.Read);

            XmlTextReader reader = null;
            try
            {
                //XML形式で読み込めるか確認
                if (File.Exists(filepath))
                {
                    reader = new XmlTextReader(fStream);
                    bool str = reader.Read();
                    //読み取り後開放処理
                    return MainForm.ProcessNormal;
                }
                else
                {
                    return MainForm.ProcessAbnormal;
                }
            }
            catch (Exception e)
            {
                ProcessLogger.ExceptionOccurred(e);
                return MainForm.ProcessAbnormal;
            }
            finally
            {
                reader.Close();
                fStream.Close();
            }
        }

        /// <summary>
        /// アプリケーション設定ファイルとレイアウト情報ファイルの整合性チェック 
        /// </summary>
        /// <param name="iniList"></param>
        /// <returns></returns>
        private int checkItemAppLayout()
        {
            int dashboardNo = 1;
            string layoutIniFullPath = appSettings.DashboardBackupIniFilePath;// Dashboard.ini
            if (System.IO.File.Exists(appSettings.Dashboard2BackupIniFilePath))
            {
                dashboardNo = 2;
                layoutIniFullPath = appSettings.Dashboard2BackupIniFilePath;// Dashboard2.ini
            }

            //アプリケーション設定ファイルとレイアウト情報ファイルの整合性チェック
            IniFile2 ini = new IniFile2(layoutIniFullPath);
            string[] stArrayData = ini.GetSection();
            try
            {
                int i = 0;
                int errorcode =MainForm.ProcessNormal;
                while (stArrayData.Length - 1  > i)
                {
                    for (int j = 0; j < this.frameList.Count ; j++)
                    {
                        DashboardCustomizeTool.LayoutData Data = this.frameList[j];//iniList[i];

                        string sectionName;
                        switch (dashboardNo)
                        {
                            case 2:
                                sectionName = Data.plugin;
                                break;
                            default:
                                sectionName = Data.name;
                                break;
                        }

                        //アイテム名とセッション名があっているかチェック
                        if (string.Equals(stArrayData[i].ToString(), sectionName))
                        {
                            //正常なら0を返して終了
                            errorcode = MainForm.ProcessNormal;
                            break;
                        }
                        //セッション項目に"System"がある場合はスルー
                        else if (string.Equals(stArrayData[i].ToString(), MainForm.LAYOUT_INI_SECTIONL_SUSTEM))
                        {
                            //正常なら0を返して終了
                            errorcode = MainForm.ProcessNormal;
                            break;
                        }
                        //セッション項目に"Socket"がある場合はスルー
                        else if (string.Equals(stArrayData[i].ToString(), MainForm.LAYOUT_INI_SECTIONL_SOCKET))
                        {
                            //正常なら0を返して終了
                            errorcode = MainForm.ProcessNormal;
                            break;
                        }
                        else
                        {
                            //それ以外にあればレイアウト情報ファイルの項目がアプリケーション設定と違う場合
                            errorcode = MainForm.ProcessAbnormal;
                        }
                    }
                    //エラーチェック
                    if (errorcode == MainForm.ProcessNormal)
                    {
                        //エラーがないなら次へ
                        i++;
                    }
                    else
                    {
                        //エラーがあればトレースとメッセージ表示
                        throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.MSGCODE_8041);
                    }
                }
            }
            catch (Exception e)
            {
                //トレースとメッセージ表示
                errormsessagr.CreateErrorMessage(e, false, this);
                ProcessLogger.ExceptionOccurred(e);
                //ワーニングメッセージ表示後は0でもどす。
                return MainForm.ProcessNormal;
            }
            //正常に処理終了の場合0をかえす
            return MainForm.ProcessNormal;
        }

        /// <summary>
        /// 十字キー停止
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void appItemGridView_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Up || e.KeyData == Keys.Down)
            {
                e.Handled = true;
            }
        }

        /// <summary>
        /// ショートカットキーイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form_KeyDown(object sender, KeyEventArgs e)
        {
            //ファイル
            if (e.KeyData == (Keys.Alt | Keys.F))
            {
               
                this.FileFToolStripMenuItem.ShowDropDown();
                // Ctrl + A を入力したとき
            }
            //編集
            if (e.KeyData == (Keys.Alt | Keys.E))
                this.EditEToolStripMenuItem.ShowDropDown();

            if (e.KeyData == (Keys.S))
            {
                this.EditEToolStripMenuItem.ShowDropDown();
            }
        }


        /// <summary>
        /// アプリケーションの終了メソッド
        /// </summary>
        public void ExitApp()
        {
            //クローズ時のメッセージはForm1_FormClosingイベントで表示
            this.Close();
        }

        /// <summary>
        /// マウスのドラッグ＆ドロップ操作
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void appItemGridView_CellMouseDown(object sender, DataGridViewCellMouseEventArgs e)
        {
            //左クリックか確認
            if (e.Button == MouseButtons.Left)
            {
                
                DataGridView lbx = (DataGridView)sender;
                //選択先の情報取得
                DataGridView.HitTestInfo itemIndex = lbx.HitTest(e.X, e.Y);
                if (e.ColumnIndex >= 0)
                {
                    this.appItemGridView[e.ColumnIndex, e.RowIndex].Selected = true;
                    //アイテムの表示が非表示なら追加可能
                    if (!(Convert.ToBoolean(this.appItemGridView[e.ColumnIndex, e.RowIndex].Tag)))
                    {
                        //ドロップ＆ドロップイベント開始
                        LayoutData dat = (LayoutData)this.appItemGridView[e.ColumnIndex, e.RowIndex].Value;
                        string itemText = dat.ToString();
                        DragDropEffects dde = lbx.DoDragDrop(itemText, DragDropEffects.All);
                    }
                }
            }
        }

       /// <summary>
       /// レイアウト表示部側ドラッグ＆ドロップイベント
       /// </summary>
       /// <param name="sender"></param>
       /// <param name="cla"></param>
        private void manageLayoutInfo1_PaneDragDrop(object sender, Dockingcontrollayout.ManageLayoutInfo.PaneHidden cla)
        {
            //アイテム追加処理呼び出し
            AddLayoutItem();
        }

        /// <summary>
        /// ドラッグデータのチックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void manageLayoutInfo1_DragEnter(object sender, DragEventArgs e)
        {
            //ドラッグされているデータがstring型か調べ、
            //そうであればドロップ効果をMoveにする
            if (e.Data.GetDataPresent(typeof(string)))
                e.Effect = DragDropEffects.Move;
            else
                //string型でなければ受け入れない
                e.Effect = DragDropEffects.None;
        }

        /// <summary>
        /// ドロップイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void manageLayoutInfo1_DragDrop(object sender, DragEventArgs e)
        {
            //ドロップされたデータがstring型か調べる
            if (e.Data.GetDataPresent(typeof(string)))
            {
                //アイテム追加
                AddLayoutItem();
            }
        }

        /// <summary>
        /// 多言語設定で取得したコントロール名を設定
        /// </summary>
        private void ControlNameSettings()
        {
            //ManageLayoutInfoで使う言語設定を設定
            this.manageLayoutInfo.SetLanguage(MainForm.Language);
            //コントロールのText項目を設定
            this.Text = languageSettings.AppTitleText;
            this.FileFToolStripMenuItem.Text = languageSettings.FileFToolStripMenuItemText;
            this.OpenToolStripMenuItem.Text = languageSettings.OpenToolStripMenuItemText;
            this.SaveToolStripMenuItem.Text = languageSettings.SaveToolStripMenuItemText;
            this.SaveAsToolStripMenuItem.Text = languageSettings.SaveAsToolStripMenuItemText;
            this.AddToolStripItem.Text = languageSettings.AddToolStripItemText;
            this.ImportToolStripMenuItem.Text = languageSettings.ImportToolStripMenuItemText;
            this.ExportToolStripMenuItem.Text = languageSettings.ExportToolStripMenuItemText;
            this.ConfigToolStripMenuItem.Text = languageSettings.ConfigToolStripMenuItemText;
            this.EndDToolStripMenuItem.Text = languageSettings.EndDToolStripMenuItemText;
            this.EditEToolStripMenuItem.Text = languageSettings.EditEToolStripMenuItemText;
            this.AddToolStripMenuItem.Text = languageSettings.AddToolStripMenuItemText;
            this.DeleteToolStripMenuItem.Text = languageSettings.DeleteToolStripMenuItemText;
            this.AddToolStripItem.Text = languageSettings.AddToolStripItemText;
            this.DeleteToolStripItem.Text = languageSettings.DeleteToolStripItemText;
        }

        /// <summary>
        /// レイアウト表示情報を取得する。
        /// </summary>
        private List<LayoutData> LayoutInfoDataSet()
        {
            List<LayoutData> listdata = new List<LayoutData>();

            foreach (DataGridViewRow rows in appItemGridView.Rows)
            {
                if (rows.Cells == null || rows.Cells.Count == 0)
                {
                    continue;
                }

                //レイアウト情報集積用に構造体呼び出し
                LayoutData savedata = new LayoutData();
                //座標情報はリストで取得0:TOP、1:LEFT、2:BOTTOM、3:RIGHT
                List<string> infolist = new List<string>();
                //セッション名追加
                LayoutData dat = (LayoutData)rows.Cells[0].Value;
                savedata.name = dat.name;
                savedata.name_en = dat.name_en;
                savedata.plugin = dat.plugin;
                savedata.color = dat.color;
                //表示情報があるかチェック
                if (Convert.ToBoolean(rows.Cells[0].Tag))
                {
                    //表示情報を追加
                    savedata.disp = LAYOUT_DISPLAY;
                    //表示しているなら座標情報を取得する。
                    infolist = this.manageLayoutInfo.CheckPanelItem(dat.plugin);
                    if (infolist == null || infolist.Count < 4)
                    {
                        continue;
                    }
                    savedata.top = infolist[0];
                    savedata.left = infolist[1];
                    savedata.bottom = infolist[2];
                    savedata.right = infolist[3];
                }
                else
                {
                    //非表示ならDisp＝0で返す。
                    savedata.disp = LAYOUT_NOTDISPLAY;
                }
                //作成したセッション内の情報をリストに追加
                listdata.Add(savedata);
            }
            return listdata;
        }

        /// <summary>
        /// レイアウト再描画イベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void manageLayoutInfo_Paint(object sender, PaintEventArgs e)
        {
            //レイアウトフラグはあるならば変更後の座標取得を行う
            if (this.isChangeflag)
            {
                //レイアウト座標取得
                befLayoutList = LayoutInfoDataSet();
                this.isChangeflag = false;
            }
        }

        /// <summary>
        /// レイアウトベースパネル SizeChanged イベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void layoutBasePanel_SizeChanged(object sender, EventArgs e)
        {
            // レイアウト領域のサイズ設定
            this.layoutPanelResize();
        }

        /// <summary>
        /// レイアウトベースパネルに合わせて、レイアウト領域のサイズを変更する。
        /// </summary>
        private void layoutPanelResize()
        {
            double panelWidth = (double)this.layoutBasePanel.Width;
            double panelHeight = (double)this.layoutBasePanel.Height;

            double monWidth = (double)configIni.GetConfigData.MonitorWidth;
            double monHeight = (double)configIni.GetConfigData.MonitorHeight;

            double w = panelWidth / monWidth;
            double h = panelHeight / monHeight;
            double mag = w;
            if (w > h)
            {
                mag = h;
            }

            double wid = monWidth * mag;
            double hei = monHeight * mag;

            this.manageLayoutInfo.Size = new Size((int)wid, (int)hei);

            this.lblMonitorSizeValue.Text = string.Format("{0} x {1}", configIni.GetConfigData.MonitorWidth, configIni.GetConfigData.MonitorHeight);
        }

        /// <summary>
        /// XMLNode の DockableControlPane の情報を表示言語に合うよう更新する
        /// </summary>
        /// <param name="node">XmlNode</param>
        private void updateDockableControlPanes(ref XmlNode node)
        {
            if (node.Name.Equals("a1:DockableControlPane"))
            {
                updateDockableControlPane(ref node);
                return;
            }

            if (node.HasChildNodes)
            {
                for (int i = 0; i < node.ChildNodes.Count; i++)
                {
                    XmlNode childNode = node.ChildNodes.Item(i);
                    this.updateDockableControlPanes(ref childNode);
                }
            }
        }

        /// <summary>
        /// XmlNode の子ノードの情報を表示言語に合うよう更新する (Text, Tag/objectValue, ControlName)
        /// </summary>
        /// <param name="node">XmlNode</param>
        private void updateDockableControlPane(ref XmlNode node)
        {
            if (node.HasChildNodes)
            {
                XmlNode textNode = node.SelectSingleNode("Text");
                if (textNode == null)
                {
                    return;
                }

                // ノードのテキストに該当するフレーム名のレイアウト情報を取得する。
                // (日本語名で検索して、ない場合は英語名で検索)
                LayoutData layoutData = getLayoutDataFromName(textNode.InnerText);
                if (string.IsNullOrEmpty(layoutData.plugin))
                {
                    layoutData = getLayoutDataFromNameEn(textNode.InnerText);
                    if (string.IsNullOrEmpty(layoutData.plugin))
                    {
                        return;
                    }
                }

                // 表示言語に合ったテキストに更新する。
                textNode.InnerText = layoutData.ToString();

                // Tag の objectValue をフレーム名に更新する。
                XmlNode tagNode = node.SelectSingleNode("Tag");
                if (tagNode != null)
                {
                    this.upadteObjectValue(ref tagNode, layoutData.plugin);
                }

                // ControlName を表示言語に合ったテキストに更新する。
                XmlNode controlNameNode = node.SelectSingleNode("ControlName");
                if (controlNameNode != null)
                {
                    controlNameNode.InnerText = layoutData.ToString();
                }
            }
        }

        /// <summary>
        /// 指定したフレーム名と同じフレーム名(日本語)のレイアウト情報を取得する。
        /// </summary>
        /// <param name="name">フレーム名</param>
        /// <returns>レイアウト情報 (存在しない場合は空のLayoutData)</returns>
        private LayoutData getLayoutDataFromName(string name)
        {
            try
            {
                return this.frameList.Find(p => p.name.Equals(name));
            }
            catch
            {
                return new LayoutData();
            }
        }

        /// <summary>
        /// 指定したフレーム名と同じフレーム名(英語)のレイアウト情報を取得する。
        /// </summary>
        /// <param name="name">フレーム名</param>
        /// <returns>レイアウト情報 (存在しない場合は空のLayoutData)</returns>
        private LayoutData getLayoutDataFromNameEn(string name)
        {
            try
            {
                return this.frameList.Find(p => p.name_en.Equals(name));
            }
            catch
            {
                return new LayoutData();
            }
        }

        /// <summary>
        /// XmlNode の子ノードの objectValue を指定した値に更新する
        /// </summary>
        /// <param name="node"></param>
        /// <param name="value"></param>
        private void upadteObjectValue(ref XmlNode node, string value)
        {
            if (node.HasChildNodes)
            {
                XmlNode targetNode = node.SelectSingleNode("objectValue");
                if (targetNode != null)
                {
                    targetNode.InnerText = value;
                    if (!this.tagList.Contains(value))
                    {
                        this.tagList.Add(value);
                    }
                }
            }
        }

        /// <summary>
        /// 設定ファイルは上書きせずにレイアウトを読み込む。
        /// </summary>
        /// <returns>結果</returns>
        private FuncResult openLayoutInfo()
        {
            FuncResult result = FuncResult.Failure;
            try
            {
                // 読込ダイアログ
                OpenFileDialog ofd = new OpenFileDialog();

                ofd.Title = languageSettings.OpenDialogTitle;// タイトル
                ofd.FileName = MainForm.CFG_DEFAULT_NAME;// デフォルトのファイル名
                ofd.Filter = languageSettings.FileReadDlgFilter;// ファイルの種類
                ofd.FilterIndex = 0;
                ofd.CheckFileExists = true;// 存在しないファイルは警告
                ofd.CheckPathExists = true;// 存在しないパスは警告
                ofd.RestoreDirectory = true;// 以前選択されていたディレクトリに復元

                // デフォルトパス
                if (Directory.Exists(this.configIni.GetConfigData.InitialDirectory))
                {
                    ofd.InitialDirectory = this.configIni.GetConfigData.InitialDirectory;
                }
                else
                {
                    ofd.InitialDirectory = this.appSettings.DefaultSaveFolderPath;
                }

                DialogResult dlgRet = ofd.ShowDialog();
                if (DialogResult.Cancel.Equals(dlgRet))
                {
                    // キャンセル
                    result = FuncResult.Cancel;
                    return result;
                }

                // 選択したフォルダパスを設定ファイルに保存
                this.configIni.GetConfigData.InitialDirectory = Path.GetDirectoryName(ofd.FileName);
                this.configIni.SaveFile();

                // レイアウトツール 設定ファイルをテンポラリに読み込む。
                result = this.loadLayoutCfg(ofd.FileName);

                // 開いているファイル
                this.setLayoutCfgPath(ofd.FileName);

                // 変更前のレイアウト情報を更新
                this.isChangeflag = true;
                this.manageLayoutInfo.Invalidate();
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            return result;
        }

        /// <summary>
        /// レイアウトを読み込み、設定ファイルを上書きする。
        /// </summary>
        /// <returns>結果</returns>
        private FuncResult importLayoutInfo()
        {
            FuncResult result = FuncResult.Failure;
            try
            {
                // 読込ダイアログ
                OpenFileDialog ofd = new OpenFileDialog();

                ofd.Title = languageSettings.FileReadDlgTitle;// タイトル
                ofd.FileName = MainForm.CFG_DEFAULT_NAME;// デフォルトのファイル名
                ofd.Filter = languageSettings.FileReadDlgFilter;// ファイルの種類
                ofd.FilterIndex = 0;
                ofd.CheckFileExists = true;// 存在しないファイルは警告
                ofd.CheckPathExists = true;// 存在しないパスは警告
                ofd.RestoreDirectory = true;// 以前選択されていたディレクトリに復元

                // デフォルトパス
                if (Directory.Exists(this.configIni.GetConfigData.InitialDirectory))
                {
                    ofd.InitialDirectory = this.configIni.GetConfigData.InitialDirectory;
                }
                else
                {
                    ofd.InitialDirectory = this.appSettings.DefaultSaveFolderPath;
                }

                DialogResult dlgRet = ofd.ShowDialog();
                if (DialogResult.Cancel.Equals(dlgRet))
                {
                    // キャンセル
                    result = FuncResult.Cancel;
                    return result;
                }

                // 警告ダイアログ
                mg = new ShowMsgBoxAPI();
                dlgRet = mg.ShowMsgBox(this, this.Text, languageSettings.MessageImportLayout, MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                if (DialogResult.No.Equals(dlgRet))
                {
                    // いいえ
                    result = FuncResult.Cancel;
                    return result;
                }

                // 選択したフォルダパスを設定ファイルに保存
                this.configIni.GetConfigData.InitialDirectory = Path.GetDirectoryName(ofd.FileName);
                this.configIni.SaveFile();

                // レイアウトツール 設定ファイルをテンポラリに読み込む。
                FuncResult loadRet = this.loadLayoutCfg(ofd.FileName);
                if (!FuncResult.Success.Equals(loadRet))
                {
                    result = loadRet;// 内部でエラー表示して、戻り値がキャンセル扱いの場合があるので、そのまま渡す。
                    return result;
                }

                System.Windows.Forms.Application.DoEvents();

                // 読み込んだ設定を保存する。
                result = this.saveLayoutInfo();

                // 変更前のレイアウト情報を更新
                this.isChangeflag = true;
                this.manageLayoutInfo.Invalidate();
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            return result;
        }

        /// <summary>
        /// レイアウトを読み込む。
        /// </summary>
        /// <param name="layoutCfgPath">レイアウトツール 設定ファイル</param>
        /// <returns>結果</returns>
        private FuncResult loadLayoutCfg(string layoutCfgPath)
        {
            FuncResult result = FuncResult.Failure;
            string tempDashboardIniPath = Path.Combine(appSettings.AdFactoryTemp, Settings.DASHBOARD_BACKUP_INI);
            string tempDashboard2IniPath = Path.Combine(appSettings.AdFactoryTemp, Settings.DASHBOARD2_BACKUP_INI);
            string tempLayoutXmlPath = Path.Combine(appSettings.AdFactoryTemp, Settings.LAYOUT_XML_FILE);
            try
            {
                // 対象ファイルをテンポラリに展開する。
                bool isDec = this.decompLayoutInfoCfg(layoutCfgPath, appSettings.AdFactoryTemp);
                if (!isDec)
                {
                    result = FuncResult.Cancel;// 内部でエラー表示するのでキャンセル扱いにする。
                    return result;
                }

                // レイアウト表示部の初期化
                this.tagList.Clear();
                this.manageLayoutInfo.LayoutDeleteAll();
                this.SetStyle(ControlStyles.ResizeRedraw, true);

                // レイアウト情報の表示処理
                List<LayoutData> iniList = new List<LayoutData>();
                int dashboardNo = 1;
                string iniFilePath = tempDashboardIniPath;
                if (File.Exists(tempDashboard2IniPath))
                {
                    dashboardNo = 2;
                    iniFilePath = tempDashboard2IniPath;
                }

                // レイアウト情報読み込み
                IniManager iniManager = new IniManager();
                iniList = iniManager.DashboardIniRead(dashboardNo, iniFilePath, this.frameList);

                // 表示されていないレイアウトの描画
                this.autoAddLayoutItem(tempLayoutXmlPath);

                // XMLファイルを読み込みレイアウトの表示座標を変更
                this.manageLayoutInfo.LoadLauoutXML(tempLayoutXmlPath);

                // レイアウトの初期選択を設定
                this.LayoutActive();

                result = FuncResult.Success;
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            finally
            {
                // テンポラリフォルダのファイルを削除する。
                FileUtils.deleteFile(tempDashboardIniPath);
                FileUtils.deleteFile(tempDashboard2IniPath);
                FileUtils.deleteFile(tempLayoutXmlPath);
            }
            return result;
        }

        /// <summary>
        /// レイアウトを保存する。
        /// </summary>
        /// <returns>結果</returns>
        private FuncResult saveLayoutInfo()
        {
            FuncResult result = FuncResult.Failure;
            string tempDashboard2IniPath = Path.Combine(appSettings.AdFactoryTemp, Settings.DASHBOARD2_BACKUP_INI);
            string tempLayoutXmlPath = Path.Combine(appSettings.AdFactoryTemp, Settings.LAYOUT_XML_FILE);
            try
            {
                // adMonitor レイアウト設定ファイルを、テンポラリフォルダに保存する。(Dashboard2backup.ini)
                bool isSaveIni = this.saveDashboardIni(tempDashboard2IniPath);
                if (!isSaveIni)
                {
                    return result;
                }

                // レイアウトXMLをテンポラリフォルダに保存する。(adAndonCustomizeToolLayoutInfo.xml)
                bool isSaveXml = this.saveLayoutXml(tempLayoutXmlPath);
                if (!isSaveXml)
                {
                    return result;
                }

                // adMonitor レイアウト設定ファイル
                string fileName = Path.GetFileName(this.cfgFilePath);
                if (fileName.Equals(CFG_DEFAULT_NAME))
                {
                    // 標準ファイルの場合は「Dashboard2.ini」にコピーする。
                    FileUtils.overwriteCopyFile(tempDashboard2IniPath, this.appSettings.Dashboard2IniFilePath);
                }
                else
                {
                    // 設備識別子名付きファイルの場合は「Dashboard2_<設備識別子名>.ini」にコピーする。
                    Match match = Regex.Match(fileName, CFG_IDENTNAME_REGEX, RegexOptions.IgnoreCase);
                    if (match.Success)
                    {
                        string identName = match.Groups[CFG_IDENTNAME_GROUP].Value;

                        // adMonitor レイアウト設定ファイルパス
                        StringBuilder sb1 = new StringBuilder();
                        sb1.Append(Path.GetFileNameWithoutExtension(this.appSettings.Dashboard2IniFilePath));
                        sb1.Append("_");
                        sb1.Append(identName);
                        sb1.Append(Path.GetExtension(this.appSettings.Dashboard2IniFilePath));
                        string dashboardPath = Path.Combine(Path.GetDirectoryName(this.appSettings.Dashboard2IniFilePath), sb1.ToString());

                        FileUtils.overwriteCopyFile(tempDashboard2IniPath, dashboardPath);
                    }
                }

                // レイアウトツール 設定ファイルを、読み込んだcfgファイルに保存する。
                bool isSaveCfg = this.saveLayoutCfg(tempLayoutXmlPath, tempDashboard2IniPath, this.cfgFilePath);
                if (!isSaveCfg)
                {
                    return result;
                }

                this.layoutRefresh();

                result = FuncResult.Success;
            }
            catch (Exception e)
            {
                ProcessLogger.ExceptionOccurred(e);
            }
            finally
            {
                // テンポラリフォルダのファイルを削除する。
                FileUtils.deleteFile(tempDashboard2IniPath);
                FileUtils.deleteFile(tempLayoutXmlPath);
            }
            return result;
        }

        /// <summary>
        /// 設備識別子名で保存
        /// </summary>
        /// <returns>結果</returns>
        private FuncResult saveAsLayoutInfo()
        {
            FuncResult result = FuncResult.Failure;
            string tempDashboard2IniPath = Path.Combine(appSettings.AdFactoryTemp, Settings.DASHBOARD2_BACKUP_INI);
            string tempLayoutXmlPath = Path.Combine(appSettings.AdFactoryTemp, Settings.LAYOUT_XML_FILE);
            try
            {
                string equipmentIdentName;
                string dashboardPath;
                string layoutInfoPath;

                using (SaveAsDialog dialog = new SaveAsDialog())
                {
                    dialog.StartPosition = FormStartPosition.CenterParent;
                    dialog.EquipmentIdentName = string.Empty;

                    // 設定ダイアログを表示
                    DialogResult dlgRet = dialog.ShowDialog(this);
                    if (DialogResult.Cancel.Equals(dlgRet))
                    {
                        // キャンセル
                        result = FuncResult.Cancel;
                        return result;
                    }

                    equipmentIdentName = dialog.EquipmentIdentName;
                    dashboardPath = dialog.DashboardFilePath;
                    layoutInfoPath = dialog.LayoutInfoFilePath;
                }

                // adMonitor レイアウト設定ファイルを、テンポラリフォルダに保存する。(Dashboard2backup.ini)
                bool isSaveIni = this.saveDashboardIni(tempDashboard2IniPath);
                if (!isSaveIni)
                {
                    return result;
                }

                // adMonitor レイアウト設定ファイルを「Dashboard2_<設備識別子名>.ini」にコピーする。
                FileUtils.overwriteCopyFile(tempDashboard2IniPath, dashboardPath);

                // レイアウトXMLをテンポラリフォルダに保存する。(adAndonCustomizeToolLayoutInfo.xml)
                bool isSaveXml = this.saveLayoutXml(tempLayoutXmlPath);
                if (!isSaveXml)
                {
                    return result;
                }

                // レイアウトツール 設定ファイルを、ファイル名「adAndonCustomizeToolLayoutInfo_<設備識別子名>.cfg」で保存する。
                bool isSaveCfg = this.saveLayoutCfg(tempLayoutXmlPath, tempDashboard2IniPath, layoutInfoPath);
                if (!isSaveCfg)
                {
                    return result;
                }

                // 開いているファイル
                this.setLayoutCfgPath(layoutInfoPath);

                this.layoutRefresh();

                result = FuncResult.Success;
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            finally
            {
                // テンポラリフォルダのファイルを削除する。
                FileUtils.deleteFile(tempDashboard2IniPath);
                FileUtils.deleteFile(tempLayoutXmlPath);
            }
            return result;
        }

        /// <summary>
        /// エクスポート
        /// </summary>
        /// <returns>結果</returns>
        private FuncResult exportLayoutInfo()
        {
            FuncResult result = FuncResult.Failure;
            try
            {
                DialogResult dlgRet;
                mg = new ShowMsgBoxAPI();
                bool isChange = this.layoutChangeCheck();
                if (isChange)
                {
                    dlgRet = mg.ShowMsgBox(this, this.Text, languageSettings.MessageSaveLayout, MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                    switch (dlgRet)
                    {
                        case DialogResult.Yes:
                            // レイアウトを保存してエクスポート
                            FuncResult saveResult = this.saveLayoutInfo();
                            if (FuncResult.Failure.Equals(saveResult))
                            {
                                mg.ShowMsgBox(this, this.Text, languageSettings.ErrorMessageSaveLayoutFile, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                                result = FuncResult.Cancel;
                                return result;
                            }
                            break;
                        case DialogResult.No:
                            // レイアウトを保存せずエクスポート (変更前のレイアウト情報がエクスポートされる)
                            break;
                        default:
                            // キャンセル
                            result = FuncResult.Cancel;
                            return result;
                    }
                }

                // 保存ダイアログ
                SaveFileDialog sfd = new SaveFileDialog();

                sfd.Title = languageSettings.FileSaveDlgTitle;// タイトル
                sfd.FileName = MainForm.CFG_DEFAULT_NAME;// デフォルトのファイル名
                sfd.Filter = languageSettings.FileReadDlgFilter;// ファイルの種類
                sfd.FilterIndex = 0;
                sfd.OverwritePrompt = true;// 既に存在するファイル名は警告
                sfd.CheckPathExists = true;// 存在しないパスは警告

                // デフォルトパス
                if (Directory.Exists(this.configIni.GetConfigData.InitialDirectory))
                {
                    sfd.InitialDirectory = this.configIni.GetConfigData.InitialDirectory;
                }
                else
                {
                    sfd.InitialDirectory = this.appSettings.DefaultSaveFolderPath;
                }

                dlgRet = sfd.ShowDialog();
                if (DialogResult.Cancel.Equals(dlgRet))
                {
                    // キャンセル
                    result = FuncResult.Cancel;
                    return result;
                }

                // 選択したフォルダパスを設定ファイルに保存
                this.configIni.GetConfigData.InitialDirectory = Path.GetDirectoryName(sfd.FileName);
                this.configIni.SaveFile();

                // レイアウトツール 設定ファイルを、指定されたファイルにコピーする。
                FileUtils.overwriteCopyFile(this.cfgFilePath, sfd.FileName);

                result = FuncResult.Success;
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            return result;
        }

        /// <summary>
        /// adMonitor レイアウト設定ファイルを保存する。
        /// </summary>
        /// <param name="dashboardIniPath">ファイルパス</param>
        /// <returns>結果</returns>
        private bool saveDashboardIni(string dashboardIniPath)
        {
            bool result = false;
            try
            {
                // レイアウト情報保存リスト
                List<LayoutData> layoutDataList = new List<LayoutData>();

                // レイアウトのチェックを行う
                foreach (DataGridViewRow rows in appItemGridView.Rows)
                {
                    // レイアウト情報集積用に構造体呼び出し
                    // 座標情報はリストで取得0:TOP、1:LEFT、2:BOTTOM、3:RIGHT
                    List<string> infoList = new List<string>();
                    // セッション名追加
                    LayoutData dat = (LayoutData)rows.Cells[0].Value;
                    // 表示情報があるかチェック
                    if (Convert.ToBoolean(rows.Cells[0].Tag))
                    {
                        // 表示情報を追加
                        dat.disp = LAYOUT_DISPLAY;
                        // 表示しているなら座標情報を取得する。
                        infoList = this.manageLayoutInfo.GetLayoutSize(dat.plugin);
                        if (infoList == null || infoList.Count < 4)
                        {
                            continue;
                        }
                        dat.top = infoList[0];
                        dat.left = infoList[1];
                        dat.bottom = infoList[2];
                        dat.right = infoList[3];
                    }
                    else
                    {
                        // 非表示ならDisp＝0で返す。
                        dat.disp = LAYOUT_NOTDISPLAY;
                    }
                    // 作成したセッション内の情報をリストに追加する。
                    layoutDataList.Add(dat);
                }

                // 設定ファイルが存在しない場合は新規作成する。
                if (!File.Exists(dashboardIniPath))
                {
                    if (!Directory.Exists(Path.GetDirectoryName(dashboardIniPath)))
                    {
                        Directory.CreateDirectory(Path.GetDirectoryName(dashboardIniPath));
                    }

                    FileStream fs = File.Create(dashboardIniPath);
                    fs.Close();
                }

                // 設定ファイルの読み取り専用を解除する。
                this.FileCheck(dashboardIniPath, false);

                // 設定を更新する。
                IniManager iniManager = new IniManager();
                foreach (LayoutData layoutData in layoutDataList)
                {
                    iniManager.IniWriteLayout(layoutData, dashboardIniPath);
                }

                result = true;
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            return result;
        }

        /// <summary>
        /// レイアウトXMLファイルを保存する。
        /// </summary>
        /// <param name="layoutXmlPath">ファイルパス</param>
        /// <returns>結果</returns>
        private bool saveLayoutXml(string layoutXmlPath)
        {
            bool result = false;
            try
            {
                this.appItemGridView.Select();
                this.manageLayoutInfo.SaveLayoutXML(layoutXmlPath);

                result = true;
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            return result;
        }

        /// <summary>
        /// レイアウトツール 設定ファイルを保存する。
        /// </summary>
        /// <param name="layoutXmlPath">レイアウトXMLファイルパス</param>
        /// <param name="dashboardIniPath">adMonitor レイアウト設定ファイルパス</param>
        /// <param name="layoutCfgPath">レイアウトツール 設定ファイルパス</param>
        /// <returns>結果</returns>
        private bool saveLayoutCfg(string layoutXmlPath, string dashboardIniPath, string layoutCfgPath)
        {
            bool result = false;
            try
            {
                // ファイルを圧縮する。
                int zipRet = this.CompressZip(layoutXmlPath, dashboardIniPath, layoutCfgPath);
                if (zipRet == 0)
                {
                    result = true;
                }
            }
            catch (Exception ex)
            {
                ProcessLogger.ExceptionOccurred(ex);
            }
            return result;
        }

        /// <summary>
        /// 
        /// </summary>
        private void layoutRefresh()
        {
            DataGridViewCell CellName = appItemGridView.SelectedCells[0];

            // セルに表示されているアイテム名
            LayoutData cellDat = (LayoutData)CellName.Value;
            string cellItemName = cellDat.plugin;

            // 全てのセルをアクティブ化
            this.manageLayoutInfo.SetPaneActiveBorder(cellItemName);

            // 変更前のレイアウト情報を更新
            this.isChangeflag = true;
            this.manageLayoutInfo.Invalidate();

            // レイアウトのアクティブ化後一度データグリッドをアクティブ化する（レイアウト非選択時用）
            this.appItemGridView.Select();

            // ペインを格納しているコントロールのコレクションないを検索
            foreach (DataGridViewRow row in this.appItemGridView.Rows)
            {
                LayoutData dat = (LayoutData)row.Cells[0].Value;
                if (cellItemName.Equals(dat.plugin))
                {
                    // アクティブになったレイアウトに連動して選択されているセルが変更される。
                    row.Selected = true;
                }
            }

            // 本来のセルをアクティブにする
            this.manageLayoutInfo.PaneActiveBorder(cellItemName);
            System.Windows.Forms.Application.DoEvents();
        }

        /// <summary>
        /// レイアウトツール 設定ファイルを展開する。
        /// </summary>
        /// <param name="layoutCfgPath">レイアウトツール 設定ファイルパス</param>
        /// <param name="dstFolder">展開先フォルダパス</param>
        /// <returns>結果</returns>
        private bool decompLayoutInfoCfg(string layoutCfgPath, string dstFolder)
        {
            bool result = false;
            try
            {
                string dashboardIniPath = Path.Combine(dstFolder, Settings.DASHBOARD_BACKUP_INI);
                string dashboard2IniPath = Path.Combine(dstFolder, Settings.DASHBOARD2_BACKUP_INI);
                string layoutXmlPath = Path.Combine(dstFolder, Settings.LAYOUT_XML_FILE);

                FileUtils.deleteFile(dashboardIniPath);
                FileUtils.deleteFile(dashboard2IniPath);
                FileUtils.deleteFile(layoutXmlPath);

                // レイアウト情報cfg解凍
                int ret = DecompressionZip(layoutCfgPath, dstFolder);
                if (ret != MainForm.ProcessNormal)
                {
                    // ZIPファイルの展開に失敗
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0032);
                }

                int dashboardNo = 2;
                string dashboardPath = dashboard2IniPath;
                if (!File.Exists(dashboardPath))
                {
                    dashboardNo = 1;
                    dashboardPath = dashboardIniPath;
                }

                if (!File.Exists(layoutXmlPath) || !File.Exists(dashboardPath))
                {
                    // 設定ファイルがない
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0032);
                }

                // XMLファイルチェック
                if (LayouyXmlCheck(layoutXmlPath) != MainForm.ProcessNormal)
                {
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0051);
                }

                // iniファイルチェック
                ret = LayoutIniCheck(dashboardNo, dashboardPath);
                if (ret == MainForm.ProcessNormal)
                {
                    result = true;
                }
            }
            catch (Exception ex)
            {
                errormsessagr.CreateErrorMessage(ex, true, this);
            }
            return result;
        }

        /// <summary>
        /// cfgファイルのパスを設定する。
        /// </summary>
        /// <param name="layoutCfgPath"></param>
        private void setLayoutCfgPath(string path)
        {
            this.cfgFilePath = path;
            this.lblCfgFilePathValue.Text = new StringBuilder()
                    .Append(" [")
                    .Append(path)
                    .Append("]")
                    .ToString();
        }
    }
}
