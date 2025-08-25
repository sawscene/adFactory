using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Infragistics.Win.UltraWinDock;
using Ionic.Zip;
using Ionic.Zlib;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Globalization;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Xml;
using DashboardCustomizeTool.CommonClass;
using log4net;

namespace DashboardCustomizeTool
{
    /// <summary>
    /// レイアウト情報構造体
    /// </summary>
    struct LayoutData
    {
        public string name; //アイテム名
        public string color; //マーカカラー
        public string disp; //表示　0:非表示、1:表示
        public string top; // アイテムのTOP座標
        public string left; //アイテムのLeft座標 
        public string bottom; // アイテムのBottom座標
        public string right; // アイテムのRight座標
    }

    /// <summary>
    /// メインフォーム
    /// </summary>
    public partial class MainForm : Form
    {
        /// <summary>
        /// レイアウト情報
        /// </summary>
        List<LayoutData> ListdataInfo = new List<LayoutData>();
        //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
        //2012/10/19 ADTEK 池松 START
        List<LayoutData> LayoutListdataInfo = new List<LayoutData>();
        private bool isChangeflag = false;
        //#8111 END
        //メンバー変数
        //セルのデフォルト色
        private Color DEFAULT_CELL_COLOR = ColorTranslator.FromHtml("#FFFFFF");
        //アプリケーション設定ファイル用定数
        private const string APPITEM_INI_SECION_ITEMLIST =   "ItemList";
        private const string APPITEM_INI_KEYCOUNT =   "ItemCount";
        private const string APPITEM_INI_ITEM_NAME = "Item{0:000}";
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
        private const string PANE_SPACE = "PaneSpace_1";//非表示用ペイン名
        //クラス
        private ShowMsgBoxAPI mg = new ShowMsgBoxAPI();
        private IniManager iniManager = new IniManager();
        public Settings appsettings = new Settings();
        private DashboardCustomizeTool.CommonClass.ErrorMessage errormsessagr = new DashboardCustomizeTool.CommonClass.ErrorMessage();
        //表示言語クラスの呼び出し
        private DashboardCustomizeTool.CommonClass.MultipleLanguages languageSettings = new DashboardCustomizeTool.CommonClass.MultipleLanguages();

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
        static public String Language = "JP";
 
        public MainForm()
        {
            InitializeComponent();
        }

        /// <summary>
        /// フォーム表示前の処
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form_Load(object sender, System.EventArgs e)
        {
            ProcessLogger.StartMethod();

            ControlNameSettings();
            //ここでメインフォームを前表示に切り替える（メッセージ表示用）
            this.TopMost = true;
            this.TopMost = false;
            List<LayoutData> listdata = new List<LayoutData>();
            //アイテム一覧表示処理
            this.errorCode = this.ShowAppItem();
            //アイテム一覧のエラーを確認
            if(!(this.errorCode ==MainForm.ProcessNormal))
            {
                //エラーの場合処理終了
                return;
            }

            //アイテム一覧表示処理でエラーがなければレイアウトの表示へ
            try
            {
                //コントロールの再描画許可
                this.SetStyle(ControlStyles.ResizeRedraw, true);
                //レイアウト情報の表示処理
                List<List<string>> iniList = new List<List<string>>();
                //レイアウト情報cfgファイルのチェック
                if (System.IO.File.Exists(appsettings.DefaultLayoutCfgFilrFullPath))
                {
                    //レイアウト情報CFGの前回情報確認
                    if (System.IO.File.Exists(appsettings.LayoutCfgFilrFullPath))
                    {
                        //cfgファイルがあるならレイアウトの読み込みを行う
                        this.errorCode = this.LayoutCfgDecompression(appsettings.LayoutCfgFilrFullPath, appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath);
                    }
                    else
                    {
                        //デフォルトのcfgファイルがある場合
                        this.errorCode = this.LayoutCfgDecompression(appsettings.DefaultLayoutCfgFilrFullPath, appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath);
                    }
                }
                else
                {
                    //デフォルトコンフィグもない場合　エラーメッセージを表示して処理終了
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0031);
                }
                //エラーの場合処理終了
                if (!(this.errorCode == MainForm.ProcessNormal))
                {
                    return;
                }
                //アイテム一覧を取得
                foreach (DataGridViewRow row in this.appItemGridView.Rows)
                {
                    //アイテム情報を構造体に代入
                    LayoutData ldata = new LayoutData();
                    //アイテム名
                    ldata.name = row.Cells[0].Value.ToString();
                    //マーカ情報
                    ldata.color = row.HeaderCell.Tag.ToString();
                    //アイテム情報を格納したリストを生成
                    listdata.Add(ldata);
                }
                //レイアウト情報iniファイルの読み込み処理
                IniManager inimanager = new IniManager();
                this.ListdataInfo = inimanager.DashboardIniRead(appsettings.LayoutIniFilrFullPath, listdata);
                //表示されていないレイアウトの描画
                this.AutoAddLayoutItem();
                //XMLファイルを読み込みレイアウトの表示座標を変更
                this.errorCode = this.manageLayoutInfo.LoadLauoutXML(appsettings.LayoutXmlFilrFullPath);
                if (this.errorCode == MainForm.ProcessNormal)
                {
                    //レイアウトの初期選択を設定
                    this.LayoutActive();
                    //レイアウトチェック
                    this.errorCode = this.CheCkitemAppLayout();
                }
                else
                {
                    //XMLがもない場合　エラーメッセージを表示して処理終了
                    throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0051);
                }
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
                //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
                //2012/10/19 ADTEK 池松 START
                //起動時に現在のレイアウトの座標情報を取得する。
                this.isChangeflag = true;
                //#8111 END
           
            }

            ProcessLogger.EndMethod();

        }

        /// <summary>
        /// 保存ボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //保存処理
            this.saveLayoutInfo();
        }
        /// <summary>
        /// インポートボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ImportToolStripMenuItem_Click(object sender, EventArgs e)
        {
            string filefullname = string.Empty;
            int errorcode = MainForm.ProcessNormal;

            //インポート前の状態を保存
            //ダイアログ表示
            filefullname =   this.FileReadDlg();
            if (!(string.IsNullOrEmpty(filefullname)))
            {
                //インポート処理
                errorcode = this.ImportLayoutInfoFile(filefullname);
                if (errorcode == MainForm.ProcessAbnormal)
                {
                    //インポートしたファイルを元に戻す
                    this.LayoutCfgDecompression(appsettings.LayoutCfgFilrFullPath, appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath);

                    return;
                }
                else if (errorcode == MainForm.ProcessException)
                {
                    return;
                }

                //レイアウト表示部の初期化
                this.manageLayoutInfo.LayoutDeleteAll();
                this.SetStyle(ControlStyles.ResizeRedraw, true);
                //レイアウト情報の表示処理
                List<List<string>> iniList = new List<List<string>>();
                //レイアウト情報読み込み
                IniManager inimanager = new IniManager();
                inimanager.DashboardIniRead(appsettings.LayoutIniFilrFullPath, this.ListdataInfo);
                //表示されていないレイアウトの描画
                this.AutoAddLayoutItem();
                //XMLファイルを読み込みレイアウトの表示座標を変更
                this.manageLayoutInfo.LoadLauoutXML(appsettings.LayoutXmlFilrFullPath);
                //レイアウトの初期選択を設定
                this.LayoutActive();
                //インポートしたini,xmlファイルをZIPファイルに保存する。
                CompressZip(appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath, appsettings.LayoutCfgFilrFullPath);      
                //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
                //2012/10/19 ADTEK 池松 START
                //レイアウト変更フラグを立てる
                this.isChangeflag = true;
                //再描画イベント発生の為に差描画処理を行う。
                this.manageLayoutInfo.Invalidate();
                //#8111 END

            }
        }
        
        /// <summary>
        /// エクスポートボタンクリックイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ExportToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //メッセージを表示する。
            mg = new ShowMsgBoxAPI();
            bool blayout = false;
            blayout = this.LayoutChangeCheck();
            if (blayout)
            {
                string maintitel = languageSettings.MessageSaveLayout;
                //iniファイル、XMLファイルの保存処理
                DialogResult sele = mg.ShowMsgBox(this, this.Text, maintitel, MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                switch (sele)
                {
                    case DialogResult.Yes:
                        //レイアウトの保存
                        this.saveLayoutInfo();
                        this.FileSaveDlg();
                        break;
                    case DialogResult.No:
                        //ZIPファイル生成
                        this.FileSaveDlg();
                        break;
                    case DialogResult.Cancel:
                        //Cancel 
                        break;
                    default:
                        break;
                }
            }
            else
            {
                //未変更の場合そのままダイアログ表示
                this.FileSaveDlg();
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
                blayout = this.LayoutChangeCheck();
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
                        this.ConfigurationFileDelete();
                        ProcessLogger.Logger.Info("Application Ending");
                        break;
                    case DialogResult.No:
                        this.ConfigurationFileDelete();
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
                this.ConfigurationFileDelete();
                //トレース処理終了
                ProcessLogger.Logger.Info("Trace Process End");
            }
        }

        //追加ボタンクリックイベント（メニュー）
        private void AddAToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //レイアウトアイテム追加処理呼び出し
            this.AddLayoutItem();

        }
        //削除ボタンクリックイベント（メニュー）
        private void DeleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //選択されたレイアウトのアイテムを削除する処理の呼び出し
            this.RemoveLayoutItem();
        }
        //アイテム追加ボタンクリックイベント（セル上）
        private void AddToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            //レイアウトにアイテム追加処理呼び出し
            this.AddLayoutItem();
        }
        //アイテム削除ボタンクリックイベント（セル上）
        private void DeleteToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            //選択されたレイアウトのアイテムを削除する処理の呼び出し
            this.RemoveLayoutItem();
        }
        //セルを選択したときにアクティブのレイアウトの表示を切り替える
        private void appItemGridView_CellClick(object sender, DataGridViewCellEventArgs e)
        {
         //   this.SelectLayout();
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
                if (e.Pane.Text.ToString() == this.appItemGridView.Rows[i].Cells[0].Value.ToString())
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
        /// 起動が行われたら、ShowAppItemの呼び出しを行いアプリケーションアイテムの表示を行う
        /// </summary>
        public int ShowAppItem()
        {
            try
            {
                //アプリケーション設定ファイルのフルパス
                string fullpaht = appsettings.ItemListFileFullPath;
                //アプリケーション設定ファイルの存在を確認
                if (!(System.IO.File.Exists(fullpaht)))
                {
                     throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0021);
      
                }

                //ファイルが存在するならアプリケーション設定情報を取得
                iniManager = new IniManager();
                //iniファイルの読み込み
                IniManager inimanager = new IniManager();
                IniFile ini = new IniFile(appsettings.ItemListFileFullPath);
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
                    string sItemColor = string.Format(MainForm.APPITEM_INI_ITEM_COLOR, i);
                    //取得するキー名を設定
                    string siniItemName = ini[sSectionName, sItemName];
                    string sIniItemColor = ini[sSectionName, sItemColor];
                    //iniファイルを正常に取得できたか確認
                    //キー項目に値があるかチェック

                    if (((!string.IsNullOrEmpty(siniItemName)) && (!(string.IsNullOrEmpty(sIniItemColor))) && siniItemName.Length  <= 128 && sIniItemColor.Length == 7) == true)
                    {
                    　   Color backcolor = ColorTranslator.FromHtml(sIniItemColor.ToString());
                    　   //値があればアイテムを追加する
                    　   this.appItemGridView.Rows.Add(siniItemName);
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
                
            }
            catch (Exception e)
            {
                errormsessagr.CreateErrorMessage(e, true, this);
                ProcessLogger.ExceptionOccurred(e);
                return MainForm.ProcessAbnormal;

            }
            return MainForm.ProcessNormal;
        }

        //レイアウトの削除処理
        private void RemoveLayoutItem()
        {
            //セルの色表示を初期化する。
            DataGridViewCell CellColor = appItemGridView.SelectedCells[0];
            //選択時の背景色を初期化
            appItemGridView.SelectedCells[0].Tag = false;
            //選択しているセルの情報取得
            DataGridViewCell CellName = appItemGridView.SelectedCells[0];
            this.manageLayoutInfo.RemoveLayoutItem(CellName.Value.ToString());
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
                string cellitemname = CellName.Value.ToString();
                this.manageLayoutInfo.PaneActiveBorder(cellitemname);
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
            this.manageLayoutInfo.AddLayoutItem(appItemGridView.SelectedCells[0].Value.ToString(),backcolor);
            //画面の再描画
            this.appItemGridView.Refresh();
            //選択表示処理
            SelectLayout();
        }

        /// <summary>
        /// 表示されていないレイアウトを追加するメソッド
        /// </summary>
        /// <param name="iniList"></param>
        private int AutoAddLayoutItem()
        {

            FileStream fStream = null;
            XmlTextReader reader = null;
            try
            {
                List<LayoutData> listdata = new List<LayoutData>();
                listdata = this.ListdataInfo;
                List<string> xmllist = new List<string>();
                //XMLファイルを開く
                //XMLファイルの有無を確認
                if (System.IO.File.Exists(appsettings.LayoutXmlFilrFullPath))
                {
                    fStream = new FileStream(appsettings.LayoutXmlFilrFullPath, FileMode.Open, FileAccess.Read);
                    reader = new XmlTextReader(fStream);
                    //XMLファイルを1ノードずつ読み込む
                    while (reader.Read())
                    {
                        reader.MoveToContent();
                        //ノード要素にデータがある場合
                        if (reader.NodeType == XmlNodeType.Element)
                        {
                            //ノード要素がある場合
                            if (reader.LocalName.Equals(XML_ID))
                            {
                                //ノードデータを取得
                                xmllist.Add(reader.ReadString().ToString());
                            }
                        }
                    }
                    //読み取り後開放処理
                    reader.Close();
                    fStream.Close();

                    //一番最初に空のペインを追加する(ペインの下部追加対策)
                    this.manageLayoutInfo.AddLayoutItem(PANE_SPACE, this.DEFAULT_CELL_COLOR);
                    //非表示状態にする。
                    this.manageLayoutInfo.RemoveLayoutItem(PANE_SPACE);

                    //表示しているアイテム件数を取得
                    for (int i = xmllist.Count - 1; i >= 0; i--)
                    //  foreach (String a in xmllist)
                    {
                        //iniファイルのアイテム情報取得
                        foreach (LayoutData data in listdata)
                        {
                            //XMLとiniで一致するアイテム名だけ追加する。
                            if (string.Equals(data.name.ToString(), xmllist[i].ToString()))
                            {
                                //ＸＭＬと一致するアイテムを追加
                                //レイアウトに送る、背景色情報取得
                                Color backcolor;
                                backcolor = ColorTranslator.FromHtml(data.color.ToString());
                                this.manageLayoutInfo.AddLayoutItem(data.name.ToString(), backcolor);
                                this.CelldisplaycolorCheck(data.name.ToString(), appItemGridView);
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
            finally
            {
                //エラー時ＸＭＬ開放を行う
                reader.Close();
                fStream.Close();
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
                    this.manageLayoutInfo.PaneActiveBorder(row.Cells[0].Value.ToString());
                }
            }
        }

        /// <summary>
        /// セルの色表示のチェック
        /// </summary>
        public void CelldisplaycolorCheck(string panename, DataGridView view)
        {
            //表示されているペインの項目のセルの色が表示されているかチェック
            foreach (DataGridViewRow row in this.appItemGridView.Rows)
            {
                //セルとペインをの表示名とValueの値を比べる
                if (string.Equals(panename.ToString(), row.Cells[0].Value.ToString()))
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
                if (string.Equals(row.Cells[0].Value.ToString(), pn.PaneName.ToString()))
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
                    this.manageLayoutInfo.PaneActiveBorder(pn.PaneName.ToString());
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
                if (string.Equals(row.Cells[0].Value.ToString(), cla.PaneName.ToString()))
                {
                    //アクティブになったペインにあわせてセルに枠線表示
                    row.Selected = true;
                    //赤枠を表示させる
                    this.manageLayoutInfo.PaneActiveBorder(cla.PaneName.ToString());
                }
            }
        }

        /// <summary>
        /// レイアウト情報の保存メソッド
        /// </summary>
        private int saveLayoutInfo()
        {
            int errorcode = MainForm.ProcessNormal;
            try
            {
                //レイアウト情報保存リスト作成
                List<LayoutData> savedataInfolist = new List<LayoutData>();
                //レイアウトのチェックを行う
                foreach (DataGridViewRow rows in appItemGridView.Rows)
                {
                    //レイアウト情報集積用に構造体呼び出し
                    LayoutData savedata = new LayoutData();
                    //座標情報はリストで取得0:TOP、1:LEFT、2:BOTTOM、3:RIGHT
                    List<string> infolist = new List<string>();
                    //セッション名追加
                    savedata.name = rows.Cells[0].Value.ToString();
                    //表示情報があるかチェック
                    if (Convert.ToBoolean(rows.Cells[0].Tag))
                    {
                        //表示情報を追加
                        savedata.disp = LAYOUT_DISPLAY;
                        //表示しているなら座標情報を取得する。
                        infolist =this.manageLayoutInfo.GetLayoutSize(rows.Cells[0].Value.ToString());
                        savedata.top = infolist[0].ToString();
                        savedata.left =infolist[1].ToString();
                        savedata.bottom =infolist[2].ToString();
                        savedata.right = infolist[3].ToString();
                    }
                    else
                    {
                        //非表示ならDisp＝0で返す。
                        savedata.disp = LAYOUT_NOTDISPLAY;
                    }
                    //作成したセッション内の情報をリストに追加
                    savedataInfolist.Add(savedata);
                }
                //生成したレイアウト情報をiniファイルに保存(上書き保存）する。
                if (!(System.IO.File.Exists(appsettings.LayoutIniFilrFullPath)))
                {
                    //フォルダの有無を確認
                    if (System.IO.Directory.Exists(System.IO.Path.GetDirectoryName(appsettings.LayoutIniFilrFullPath)))
                    {
                        //ファイルをiniファイルの保存場所に移動
                        FileStream fs = File.Create(appsettings.LayoutIniFilrFullPath);
                        //// ファイル属性から読み取り専用を削除
                        fs.Close();
                        //iniファイルの読み線解除
                    //    this.FileCheck(appsettings.LayoutIniFilrFullPath, false);
                    }
                    else
                    {
                        //iniフォルダがない場合エラーメッセージ表示
                        throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0041);

                    }
                }
                //Fuji Flexa側のiniファイルの有無を確認
                if (!(System.IO.File.Exists(appsettings.LayoutIniFlexaFileFullPath)))
                {
               
                    //フォルダの有無を確認
                    if (System.IO.Directory.Exists(System.IO.Path.GetDirectoryName(appsettings.LayoutIniFlexaFileFullPath)))
                    {
                        //ファイルをiniファイルの保存場所に移動
                        FileStream fs = File.Create(appsettings.LayoutIniFlexaFileFullPath);
                        fs.Close();
                    }
                    else
                    {
                       throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0041);
                    }
                }
               
                //iniファイルの読み線解除
                this.FileCheck(appsettings.LayoutIniFilrFullPath, false);
                this.FileCheck(appsettings.LayoutIniFlexaFileFullPath, false);
                foreach (LayoutData data in savedataInfolist)
                {
                    //iniファイルに生成した情報を書き込む
                    iniManager.IniWriteLayout(data, appsettings.LayoutIniFilrFullPath);
                    //サーバー側のパスを入れて書きこむ
                    iniManager.IniWriteLayout(data, appsettings.LayoutIniFlexaFileFullPath);
                }

                //XMLファイルを保存する。
                this.appItemGridView.Select();
                this.manageLayoutInfo.SaveLayoutXML(appsettings.LayoutXmlFilrFullPath);
                //ZIPファイルを保存する。
                CompressZip(appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath, appsettings.LayoutCfgFilrFullPath);

                //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
                //2012/10/19 ADTEK 池松 START
                //レイアウト座標取得前に全セルの再計算をさせる（不具合#8111対応）
                DataGridViewCell CellName = appItemGridView.SelectedCells[0];
                //セルに表示されているアイテム名
                string cellitemname = CellName.Value.ToString();
                //全てのセルをアクティブ化
                this.manageLayoutInfo.SetPaneActiveBorder(cellitemname);

                this.isChangeflag = true;
                this.manageLayoutInfo.Refresh();
                //再描画イベント発生の為に差描画処理を行う。
                this.manageLayoutInfo.Invalidate();

                //レイアウトのアクティブ化後一度データグリッドをアクティブ化する（レイアウト非選択時用）
                this.appItemGridView.Select();
                //ペインを格納しているコントロールのコレクションないを検索
                for (int i = 0; i < this.appItemGridView.Rows.Count; i++)
                {
                    if (cellitemname.ToString() == this.appItemGridView.Rows[i].Cells[0].Value.ToString())
                    {
                        //アクティブになったレイアウトに連動して選択されているセルが変更される。
                        this.appItemGridView.Rows[i].Selected = true;
                    }
                }
                //本来のセルをアクティブにする
                this.manageLayoutInfo.PaneActiveBorder(cellitemname);
                
                //#8111 END
            }
            catch(Exception e)
            {
                //エラーメッセージ表示、トレースログ書き込み
                errormsessagr.CreateErrorMessage(e, true, this);
                errorcode = MainForm.ProcessNormal;
                ProcessLogger.ExceptionOccurred(e);
                return errorcode;
            }
            return errorcode;
        }

        /// <summary>
        /// ファイル管理用メソッド　（ファイルチェック、読み取り切り替え、フォルダ生成）
        /// </summary>
        /// <param name="filepath"></param>
        /// <returns></returns>
        private void FileCheck(string filepath,bool onflag)
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
        private bool LayoutChangeCheck()
        {
            //レイアウト変更フラグ
            bool blayout = false;
            List<LayoutData> listdata = new List<LayoutData>();

            //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
            //2012/10/19 ADTEK 池松 START------------------------------------------------------------------------------------------
            //保存時のレイアウト情報取得
            listdata = this.LayoutListdataInfo;
            ////レイアウトの変更内容を確認
            for (int i = 0; i < listdata.Count; i++)
            {
                //保存時のレイアウト情報を取得
                DashboardCustomizeTool.LayoutData Data = listdata[i];

                List<string> Layoutlist = new List<string>();
                Layoutlist = (this.manageLayoutInfo.CheckPanelItem(Data.name.ToString()));
                //非表示のレイアウトならチェックをスルーする
                if (Layoutlist.Count > 0)
                {
                    //初期レイアウトの表示確認、初期レイアウトが表示されていないのなら変更状態
                    if (string.Equals(Data.disp, LAYOUT_NOTDISPLAY))
                    {
                        //変更フラグを立てる
                        blayout = true;
                        break;
                    }
                    //座標が同じか確認
                    if (!(string.Equals(Layoutlist[0].ToString(), Data.top) & string.Equals(Layoutlist[1].ToString(), Data.left.ToString()) && string.Equals(Layoutlist[2].ToString(), Data.bottom.ToString()) && string.Equals(Layoutlist[3].ToString(), Data.right.ToString())))
                    {
                        //変更フラグを立てる
                        blayout = true;
                        break;
                    }
                }
            }

            return blayout;
        }

        /// <summary>
        /// インポート処理
        /// </summary>
        private int ImportLayoutInfoFile(string filefullname)
        {
            int errorcode = MainForm.ProcessNormal;
            //メッセージを表示する。
            mg = new ShowMsgBoxAPI();
            string maintitel = languageSettings.MessageImportLayout;
            //iniファイル、XMLファイルの保存処理
            DialogResult sele = mg.ShowMsgBox(this, this.Text, maintitel, MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            switch (sele)
            {
                case DialogResult.Yes:
                    //一時ファイルを削除する
                    this.ConfigurationFileDelete();

                    //#8112 インポート時に指定したフォルダを保存する
                    //2012/10/18 ADTEK 池松 START
                    //インポート元のフォルダフルパスをiniファイルに書き込む
                    string folder = System.IO.Path.GetDirectoryName(filefullname);
                    string path = appsettings.ItemListFileFullPath;
                    this.appsettings.InitialDirectory = folder;
                    //iniファイル書き込み
                    iniManager.IniWrite(INITIALDIRECTORY, INITIALDIRECTORY, folder, path);
                    //#8112 END

                //レイアウト情報の読み込み
                    errorcode = this.LayoutCfgDecompression(filefullname, appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath);
                    break;
                default:
                    errorcode = MainForm.ProcessException;
                    break;
            }
            return errorcode;
        }

    
        /// <summary>
        /// ファイルを保存する処理
        /// </summary>
        private void FileSaveDlg()
        {
            int errorcode = MainForm.ProcessNormal;
            //SaveFileDialogクラスのインスタンスを作成
            SaveFileDialog sfd = new SaveFileDialog();
            //はじめのファイル名を指定する
            sfd.FileName = MainForm.CFG_DEFAULT_NAME;
            //はじめに表示されるフォルダを指定する
            //指定フォルダの存在を確認
            if (Directory.Exists(this.appsettings.InitialDirectory))
            {
                /* フォルダが有る場合の処理 */
                sfd.InitialDirectory = this.appsettings.InitialDirectory;
            }
            else
            {
                //ない場合はデフォルトのフォルダを選択
                sfd.InitialDirectory = this.appsettings.InitialDirectoryDefault; //@"C:\";
            }
             //[ファイルの種類]に表示される選択肢を指定する
            sfd.Filter = languageSettings.FileReadDlgFilter;
            //[ファイルの種類]ではじめに
            //「すべてのファイル」が選択されているようにする
            sfd.FilterIndex = 0;
            //タイトルを設定する
            sfd.Title = languageSettings.FileSaveDlgTitle;
            //既に存在するファイル名を指定したとき警告する
            //デフォルトでTrueなので指定する必要はない
            sfd.OverwritePrompt = true;
            //存在しないパスが指定されたとき警告を表示する
            //デフォルトでTrueなので指定する必要はない
            sfd.CheckPathExists = true;
            //ダイアログを表示する
            if (sfd.ShowDialog() == DialogResult.OK)
            {
                //OKボタンがクリックされたとき
                //選択されたファイル名を表示する
                Console.WriteLine(sfd.FileName);
                string folder = System.IO.Path.GetDirectoryName(sfd.FileName);
                string path = appsettings.ItemListFileFullPath;
                this.appsettings.InitialDirectory = folder;
                //iniファイル書き込み
                iniManager.IniWrite(INITIALDIRECTORY, INITIALDIRECTORY, folder, path);
                //ZIPファイル生成
                errorcode = this.CompressZip(appsettings.LayoutXmlFilrFullPath, appsettings.LayoutIniFilrFullPath, sfd.FileName.Replace(ZIP_EXTENSION, CFG_EXTENSION));
            }
            if (!(errorcode == MainForm.ProcessNormal))
            {
               //ZIPファイルの生成に失敗した場合
               string maintitel =  languageSettings.MessageLayoutCfg;
               mg.ShowMsgBox(this, this.Text, maintitel, MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
            }
        }

        /// <summary>
        /// ファイルを読み込む
        /// </summary>
        /// <returns></returns>
        private string FileReadDlg()
        {
            string filename = string.Empty;
            //OpenFileDialogクラスのインスタンスを作成
            OpenFileDialog ofd = new OpenFileDialog();
            //はじめのファイル名を指定する
            //はじめに「ファイル名」で表示される文字列を指定する
            ofd.FileName = MainForm.CFG_DEFAULT_NAME;
            //はじめに表示されるフォルダを指定する
            //指定しない（空の文字列）の時は、現在のディレクトリが表示される
                if (Directory.Exists(this.appsettings.InitialDirectory))
            {
                /* フォルダが有る場合の処理 */
                ofd.InitialDirectory = this.appsettings.InitialDirectory;
            }
            else
            {
                //ない場合はデフォルトのフォルダを選択
                ofd.InitialDirectory = this.appsettings.InitialDirectoryDefault; //@"C:\";
            }
            //[ファイルの種類]に表示される選択肢を指定する
            //指定しないとすべてのファイルが表示される
            ofd.Filter = languageSettings.FileReadDlgFilter;
            //[ファイルの種類]ではじめに
            //「すべてのファイル」が選択されているようにする
            ofd.FilterIndex = 0;
            //タイトルを設定する
            ofd.Title = languageSettings.FileReadDlgTitle;
            //ダイアログボックスを閉じる前に現在のディレクトリを復元するようにする
            ofd.RestoreDirectory = true;
            //存在しないファイルの名前が指定されたとき警告を表示する
            //デフォルトでTrueなので指定する必要はない
            ofd.CheckFileExists = true;
            //存在しないパスが指定されたとき警告を表示する
            //デフォルトでTrueなので指定する必要はない
            ofd.CheckPathExists = true;
            //ダイアログを表示する
            if (ofd.ShowDialog() == DialogResult.OK)
            {
                //OKボタンがクリックされたとき
                //選択されたファイル名を表示する
                Console.WriteLine(ofd.FileName);
                filename = ofd.FileName;
            }
            return filename;
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
        private int DecompressionZIp(string path1, string path2)
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
        /// レイアウト情報cfg解凍、情報登録
        /// </summary>
        /// <param name="cfgpath">レイアウト情報cfgパス</param>
        /// <param name="movexmlpaht">XMLファイルの移動先</param>
        /// <param name="moveinipaht">INIファイルの移動先</param>
        /// <returns>正常終了</returns>
        private int LayoutCfgDecompression(string cfgpath,string movexmlpaht ,string moveinipaht)
        {
            int errorcode = MainForm.ProcessNormal;
            try
            {
                //レイアウト情報cfgパス取得
                string apppath = System.IO.Path.GetDirectoryName(appsettings.LayoutIniFilrFullPath);

                //レイアウト情報cfg解凍
                errorcode = DecompressionZIp(cfgpath, apppath);

                //正常に解凍できたかチェック
                if (!(errorcode == MainForm.ProcessNormal))
                {
                   throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0032);
                }
                else
                {
                    //ファイルの存在確認
                    if (File.Exists(appsettings.LayoutXmlFilrFullPath) && (File.Exists(appsettings.LayoutIniFilrFullPath)))
                    {
                        //XMLファイルチェック
                        if (!(LayouyXmlCheck(appsettings.LayoutXmlFilrFullPath) == MainForm.ProcessNormal))
                        {
                            throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0051);
                        }
                        //iniファイルチェック
                        errorcode = LayoutIniCheck(appsettings.LayoutIniFilrFullPath);
                    }
                    else
                    {
                        throw new DashboardCustomizeTool.CommonClass.MyException(DashboardCustomizeTool.CommonClass.ErrorMessage.ERROR_MSGCODE_0032);
                    }
                }
            }catch (Exception e)
            {
                errormsessagr.CreateErrorMessage(e, true, this);
                return MainForm.ProcessAbnormal;
            }

            return errorcode;
        }


        /// <summary>
        /// レイアウトファイルの確認メソッド
        /// </summary>
        /// <param name="folderpath"></param>
        /// <returns></returns>
        private int LayoutIniCheck(string inifilepath)
        {
            int errorcode = MainForm.ProcessNormal;

            try
            {
                List<LayoutData> iniList = new List<LayoutData>();
                string filepath = appsettings.LayoutIniFilrFullPath;

                //アイテム一覧を取得
                foreach (DataGridViewRow row in this.appItemGridView.Rows)
                {
                    //アイテム情報を構造体に代入
                    LayoutData ldata = new LayoutData();
                    ldata.name = row.Cells[0].Value.ToString();
                    ldata.color = row.HeaderCell.Tag.ToString();
                    iniList.Add(ldata);
                }

                //レイアウト情報読み込み
                IniManager inimanager = new IniManager();
                iniList = inimanager.DashboardIniRead(inifilepath, iniList);

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
        private int CheCkitemAppLayout()
        {
            //アプリケーション設定ファイルとレイアウト情報ファイルの整合性チェック
            IniFile ini = new IniFile(appsettings.LayoutIniFilrFullPath);
            string[] stArrayData = ini.GetSection();
            try
            {
                int i = 0;
                int errorcode =MainForm.ProcessNormal;
                while (stArrayData.Length-1  > i)
                {
                    for (int j = 0; j < this.ListdataInfo.Count ; j++)
                    {
                        DashboardCustomizeTool.LayoutData Data = this.ListdataInfo[j];//iniList[i];
                        //アイテム名とセッション名があっているかチェック
                        if (string.Equals(stArrayData[i].ToString(), Data.name.ToString()))
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
                        string itemText = this.appItemGridView[e.ColumnIndex, e.RowIndex].Value.ToString();
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
            this.SaveToolStripMenuItem.Text = languageSettings.SaveToolStripMenuItemText;
            this.AddToolStripItem.Text = languageSettings.AddToolStripItemText;
            this.ImportToolStripMenuItem.Text = languageSettings.ImportToolStripMenuItemText;
            this.ExportToolStripMenuItem.Text = languageSettings.ExportToolStripMenuItemText;
            this.EndDToolStripMenuItem.Text = languageSettings.EndDToolStripMenuItemText;
            this.EditEToolStripMenuItem.Text = languageSettings.EditEToolStripMenuItemText;
            this.AddToolStripMenuItem.Text = languageSettings.AddToolStripMenuItemText;
            this.DeleteToolStripMenuItem.Text = languageSettings.DeleteToolStripMenuItemText;
            this.AddToolStripItem.Text = languageSettings.AddToolStripItemText;
            this.DeleteToolStripItem.Text = languageSettings.DeleteToolStripItemText;
           
        }
        /// <summary>
        /// 一時ファイルの削除
        /// </summary>
        private void ConfigurationFileDelete()
        {
            //ファイルの存在確認
            if( (System.IO.File.Exists(appsettings.LayoutIniFilrFullPath)))
            {

/*
                // 読み取り専用を解除して削除
                this.FileCheck(appsettings.LayoutIniFilrFullPath, false);
                System.IO.File.Delete(appsettings.LayoutIniFilrFullPath);
*/

            }
            //ファイルの存在確認
            if( (System.IO.File.Exists(appsettings.LayoutXmlFilrFullPath)))
            {

/*
                //読み取り専用を解除して削除
                this.FileCheck(appsettings.LayoutXmlFilrFullPath, false);
                System.IO.File.Delete(appsettings.LayoutXmlFilrFullPath);
*/

            }
        }

        //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
        //2012/10/19 ADTEK 池松 START
        /// <summary>
        /// レイアウト表示情報を取得する。
        /// </summary>
        private List<LayoutData> LayoutInfoDataSet()
        {
            List<LayoutData> listdata = new List<LayoutData>();
            //保存時のレイアウト情報取得
            //      listdata = this.LayoutListdataInfo;
            //赤枠を削除する。

            foreach (DataGridViewRow rows in appItemGridView.Rows)
            {
                //レイアウト情報集積用に構造体呼び出し
                LayoutData savedata = new LayoutData();
                //座標情報はリストで取得0:TOP、1:LEFT、2:BOTTOM、3:RIGHT
                List<string> infolist = new List<string>();
                //セッション名追加
                savedata.name = rows.Cells[0].Value.ToString();
                //表示情報があるかチェック
                if (Convert.ToBoolean(rows.Cells[0].Tag))
                {
                    //表示情報を追加
                    savedata.disp = LAYOUT_DISPLAY;
                    //表示しているなら座標情報を取得する。
                    infolist = this.manageLayoutInfo.CheckPanelItem(rows.Cells[0].Value.ToString());
                    savedata.top = infolist[0].ToString();
                    savedata.left = infolist[1].ToString();
                    savedata.bottom = infolist[2].ToString();
                    savedata.right = infolist[3].ToString();
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
                LayoutListdataInfo = LayoutInfoDataSet();
                this.isChangeflag = false;
            }
        }           
        //#8111 END
    
    }
}
