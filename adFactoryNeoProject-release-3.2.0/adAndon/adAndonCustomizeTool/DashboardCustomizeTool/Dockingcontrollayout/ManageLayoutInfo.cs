using Infragistics.Win.UltraWinDock;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using System.IO;

namespace Dockingcontrollayout
{
    public delegate void ChangeEventHandler(object sender, EventArgs e);

    public partial class ManageLayoutInfo : UserControl
    {
        public delegate void CloseClickHandler(object sender, PaneHidden cla);
        public delegate void PaneClickHandler(object sender, PaneHidden cla);
        public delegate void PaneDragDropHandler(object sender, PaneHidden cla);

        //外部イベント用
        public event CloseClickHandler CloseClick;
        public event PaneClickHandler PaneClick;
        public event PaneDragDropHandler PaneDragDrop;

        //メンバー変数
        private const string COORDINATE_FORMAT = "{0}/{1}";
        private const string DIGIT_FORMAT = "0.0";

        //赤枠線用
        private const int PANE_PADDING =2;
        private const int PANE_PADDING_DEFAULT = 0;

        /// <summary>
        ///   エラーコード正常値
        /// </summary>
        private const int PROCESS_NORMAL = 0;

        /// <summary>
        /// エラーコード不正値
        /// </summary>
        private const int PROCESS_ABNORMAL = 1;

        /// <summary>
        /// 例外
        /// </summary>
        private const int PROCESSEXCEPTION = -1;

        /// <summary>
        /// 非表示ペイン名
        /// </summary>
        private const string PANE_SPACE = "PaneSpace_1";

        /// <summary>
        /// 言語(デフォルト英語)
        /// </summary>
        static public String LANGUAGE = LANGUAGE_SELECT_US;

        private const string LANGUAGE_SELECT_JP = "JP";  //日本語
        private const string LANGUAGE_SELECT_US = "US";  //英語
        private const string LANGUAGE_SELECT_SC = "SC";　//簡体字
        private const string LANGUAGE_SELECT_TC = "TC";  //繁体字
        //フォント
        private const string DEFAULT_FONT = "Microsoft Sans Serif";
        private const int DEFAULT_FONT_SIZE = 10;

        private const int SPLIT_HALF = 2;

        // モニタサイズ (Dashboard2.iniへの保存時に値の分母になる値)
        private double monWidth = 1920.0;
        private double monHeight = 1080.0;

        /// <summary>
        /// 閉じるボタン押下時のイベント用
        /// </summary>
        public class PaneHidden : System.EventArgs
        {
            private string _panename;

            public PaneHidden(EventArgs ea, string panename)
            {
                this._panename = panename;
            }

            public string PaneName
            {
                get
                {
                    return _panename;
                }
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public ManageLayoutInfo()
        {
            InitializeComponent();
        }

        /// <summary>
        /// モニタサイズ (Dashboard2.iniへの保存時に値の分母になる値) を設定する
        /// </summary>
        /// <param name="width"></param>
        /// <param name="height"></param>
        public void setMonitorSize(int width, int height)
        {
            this.monWidth = width;
            this.monHeight = height;
        }

        /// <summary>
        /// レイアウトのロード処理
        /// </summary>
        /// <param name="fullpath"></param>
        public int LoadLauoutXML(string fullpath)
        {
            ProcessLogger.StartMethod();

            try
            {
                //XMLファイルの有無を確認
                if (System.IO.File.Exists(fullpath))
                {
                    this.ultraDockManager.LoadFromXML(fullpath);
                    //デフォルトの設定がXML読み込みでおかしくなるので、ここで初期設定を行う
                    this.ultraDockManager.DefaultPaneSettings.AllowDockAsTab = Infragistics.Win.DefaultableBoolean.False;
                    this.ultraDockManager.UseDefaultContextMenus = false;

                    this.ultraDockManager.SplitterBarWidth = SPLIT_HALF * 2;
                    this.Padding = new Padding(SPLIT_HALF, SPLIT_HALF, SPLIT_HALF, SPLIT_HALF);

                    //読み込み時に表示していたペインを非表示化する。
                    this.RemoveLayoutItem(PANE_SPACE);
                    this.ultraDockManager.ResumeLayout();
                    //レイアウト側のコントロールをすべて取得
                    ArrayList buf = new ArrayList();
                    foreach (Control c in this.Controls)
                    {
                        buf.Add(c);
                    }
                    int count = buf.Count;
                    //XMLに記載されていないbuttonコントロールをすべて削除
                    for (int i = count - 1; i >= 0; i--)
                    {
                        //ボタンコントロール化どうか判断（追加するコントロールが変更された場合は変更すること）
                        if (buf[i].GetType().Equals(typeof(Button)))
                        {
                            //indexを指定して削除
                            this.Controls.RemoveAt(i);
                        }
                    }
                }
                else
                {
                    ProcessLogger.Logger.Error("XMLファイルが無い：" + fullpath);
                    return ManageLayoutInfo.PROCESS_ABNORMAL;
                }
            }
            catch (Exception e)
            {
                ProcessLogger.Logger.Fatal(e);
                return ManageLayoutInfo.PROCESS_ABNORMAL;
            }

            ProcessLogger.StartMethod();

            return ManageLayoutInfo.PROCESS_NORMAL;
        }

        /// <summary>
        /// レイアウト情報の保存処理
        /// </summary>
        public void SaveLayoutXML(string fullpath)
        {
            // ファイルが存在する場合は一旦削除する。
            if (File.Exists(fullpath))
            {
                File.Delete(fullpath);
            }

            if (!Directory.Exists(Path.GetDirectoryName(fullpath)))
            {
                Directory.CreateDirectory(Path.GetDirectoryName(fullpath));
            }

            this.ultraDockManager.SaveAsXML(fullpath);

            // 保存時に表示していたペインを非表示にする。
            this.RemoveLayoutItem(PANE_SPACE);
        }

        /// <summary>
        /// レイアウト画面にペインを追加する処理
        /// </summary>
        /// <param name="name">タグ名</param>
        /// <param name="displayName">テキスト表示名</param>
        /// <param name="colorcode">カラー情報(16進数)</param>
        public void AddLayoutItem(string name, string displayName, Color colorcode)
        {
            //仮置きのアイテムを指定
            Button but = new Button();
            //コントロールに表示情報などの初期情報を代入
            but.Name = displayName;
            //XMLロード時にUltraDockManager がドックされたコントロールを適切に見つける為にユニークでペイン名を設定
            //コントロール削除時に使用するコントロールとセルの紐付け情報
            but.Tag = name.ToString();
            //ドラッグ＆ドロップ用のイベント追加
            but.DragEnter += Button_DragEnter;
            but.DragDrop += Button_DragDrop;
            but.MouseDown += ManageLayoutInfo_MouseDown;
            but.MinimumSize = new Size(20, 22);
            but.AllowDrop = true;
            //背景色の設定
            but.BackColor = colorcode;
            //作成したコントロール情報を追加メソッドに引き渡す
            this.Addpane(but);
        }

        //-------------------------------------------------------------------------------------------------------------
        //Addpaneの注意書き
        //元ソース：インフラジスティックスから提供してもらったサンプルソース「AddDockPaneCTRL20120409」
        //
        //初期描画段階でペインが垂直分割のみの状態だと、ペイン追加時に不具合が発生する。
        //対応策として、初期描画時にペインを追加し、非表示化する、水平状態のペインを１つ常にある状態に
        //する事で回避可能である。
        //iniファイル上は存在しないがＸＭＬ上では記載される
        //-------------------------------------------------------------------------------------------------------------

        /// <summary>
        /// ペインを追加するメソッド
        /// </summary>
        /// <param name="pb">追加するペインのコントロール情報</param>
        private void Addpane(Button but)
        {
            //サイズ変更を禁止
            this.ultraDockManager.SuspendLayout();
            //初期化
            DockAreaPane bottomDockArea = null;
            //BottomのDockAreaを検索
            foreach (DockAreaPane dockArea in ultraDockManager.DockAreas)
            {
                if (dockArea.DockedLocation == DockedLocation.DockedBottom)
                {
                    //ペイン情報取得
                    bottomDockArea = dockArea;
                    break;
                }
            }
            //ペイン生成
            DockableControlPane con = new DockableControlPane(but);
            //ペイン名指定
            con.Tag = but.Tag;
            con.Text = but.Name;

            //コントロールのフォントを設定
            con.Control.Font = new Font(DEFAULT_FONT, DEFAULT_FONT_SIZE, FontStyle.Regular);
         
            if (bottomDockArea == null)
            {
                bottomDockArea = new DockAreaPane(DockedLocation.DockedBottom);
                ultraDockManager.DockAreas.Insert(bottomDockArea, 0);
                bottomDockArea.Panes.Insert(con, bottomDockArea.Panes.Count);
            }
            else
            {
                //水平分割かチェック
                if (bottomDockArea.ChildPaneStyle != ChildPaneStyle.HorizontalSplit)
                {

                    DockableGroupPane newGroupPane = new DockableGroupPane();
                    newGroupPane.ChildPaneStyle = bottomDockArea.ChildPaneStyle;
                    for (int i = 0; i < bottomDockArea.Panes.Count; i++)
                    {
                        DockablePaneBase pane = bottomDockArea.Panes[0];
                        bottomDockArea.Panes.Remove(pane);
                        newGroupPane.Panes.Add(pane);
                    }
                    newGroupPane.Panes.Add(con);
                    bottomDockArea.Panes.Add(newGroupPane);
                    //最後に水平化
                    bottomDockArea.ChildPaneStyle = ChildPaneStyle.HorizontalSplit;
                }
                else
                    //ペインを追加する
                    bottomDockArea.Panes.Add(con);
            }
            //再計算して再描画
            this.ultraDockManager.ResumeLayout();
        }

        /// <summary>
        /// レイアウトの削除処理
        /// </summary>
        /// <param name="controlTag">削除するペイン名</param>
        public void RemoveLayoutItem(string controlTag)
        {
            // 該当するペインを検索して、ペインを閉じる。
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                if (string.Equals(controlTag, pane.Control.Tag.ToString()))
                {
                    pane.Close();
                    pane.Control.Dispose();
                }
            }
        }

        /// <summary>
        /// ペインの閉じるボタンが押下された時のイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ultraDockManager_PaneHidden(object sender, PaneHiddenEventArgs e)
        {
            e.Pane.DockAreaPane.ChildPaneStyle = ChildPaneStyle.HorizontalSplit;
            //フォームへイベントを送る
            CloseClick(this, new PaneHidden(e, e.Pane.Control.Tag.ToString()));

            //非表示用のペイン以外は全て解放
            if (!(string.Equals(PANE_SPACE, e.Pane.Control.Name.ToString())))
            {
                //リソースの開放を別途行う
                e.Pane.Control.Dispose();
            }
        }

        /// <summary>
        /// 指定したタグ名のペインに選択枠を表示して、他のペインは選択枠を消す。
        /// </summary>
        /// <param name="controlTag">タグ名</param>
        public void PaneActiveBorder(string controlTag)
        {
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                if (string.Equals(controlTag, pane.Control.Tag.ToString()))
                {
                    //枠線を大きくし赤色に塗りつぶす
                    pane.Activate();
                    pane.Settings.PaneAppearance.BackColor = Color.Red;
                    pane.Settings.PaddingBottom = PANE_PADDING;
                    pane.Settings.PaddingLeft = PANE_PADDING;
                    pane.Settings.PaddingRight = PANE_PADDING;
                    pane.Settings.PaddingTop = PANE_PADDING;
                }
                else
                {
                    //違うペインはすべて初期化する。
                    pane.Settings.PaneAppearance.BackColor = Color.Wheat;
                    pane.Settings.PaddingBottom = PANE_PADDING;
                    pane.Settings.PaddingLeft = PANE_PADDING;
                    pane.Settings.PaddingRight = PANE_PADDING;
                    pane.Settings.PaddingTop = PANE_PADDING;
                }
            }
        }

        /// <summary>
        /// ペインがアクティブになった時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ultraDockManager_PaneActivate(object sender, ControlPaneEventArgs e)
        {
            //フォームへイベントを送る
            PaneClick(this, new PaneHidden(e, e.Pane.Control.Tag.ToString())); 
        }

        /// <summary>
        /// レイアウトの座標取得
        /// </summary>
        /// <param name="name">座標取得アイテム名</param>
        /// <returns></returns>
        public List<string> GetLayoutSize(string controlname)
        {
            List<string> layoutRatioList = new List<string>();

            //高さ、幅の最小値
            double minWidth = this.Size.Width;
            double minHeight = this.Size.Height;

            //すべてのコントロールを調べて最小の比率を求める。
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                //非表示ペインを除くペインで計算する。
                if (!(string.Equals(PANE_SPACE,pane.Control.Tag as string))){
                    // コントロールのサイズ
                    double controlheight = pane.ActualSize.Height + (SPLIT_HALF * 2);
                    double controlwidth = pane.ActualSize.Width + (SPLIT_HALF * 2);

                    //最小値を取得
                    if (minWidth > controlwidth)
                    {
                        minWidth = controlwidth;
                    }
                    if (minHeight > controlheight)
                    {
                        minHeight = controlheight;
                    }
                }
            }

            // レイアウト欄の左上座標
            Point layoutOrigin = this.PointToClient(this.Bounds.Location);

            //表示されているコントロールの情報を取得
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                if (string.Equals(controlname, pane.Control.Tag))
                {
                    // コントロールの左上座標
                    Point controlOrigin = this.PointToClient(pane.Control.Parent.Bounds.Location);

                    // コントロールの座標
                    double controlLeft = controlOrigin.X - layoutOrigin.X;
                    double controlTop = controlOrigin.Y - layoutOrigin.Y;
                    double controlBottom = pane.ActualSize.Height + (SPLIT_HALF * 2);
                    double controlRight = pane.ActualSize.Width + (SPLIT_HALF * 2);

                    double magWid = this.monWidth / (double)this.Width;
                    double magHei = this.monHeight / (double)this.Height;

                    // フレームの座標
                    double frameTop = Math.Round(magHei * controlTop, MidpointRounding.AwayFromZero);
                    double frameLeft = Math.Round(magWid * controlLeft, MidpointRounding.AwayFromZero);
                    double frameBottom = Math.Round(magHei * (controlTop + controlBottom), MidpointRounding.AwayFromZero);
                    double frameRight = Math.Round(magWid * (controlLeft + controlRight), MidpointRounding.AwayFromZero);

                    //描画領域をオーバーした場合の最大値にする
                    if (frameTop > this.monHeight)
                    {
                        frameTop = this.monHeight;
                    }

                    if (frameBottom > this.monHeight)
                    {
                        frameBottom = this.monHeight;
                    }

                    if (frameLeft > this.monWidth)
                    {
                        frameLeft = this.monWidth;
                    }

                    if (frameRight > this.monWidth)
                    {
                        frameRight = this.monWidth;
                    }

                    string topValue = String.Format(COORDINATE_FORMAT, frameTop.ToString(DIGIT_FORMAT), this.monHeight.ToString(DIGIT_FORMAT));
                    string leftValue = String.Format(COORDINATE_FORMAT, frameLeft.ToString(DIGIT_FORMAT), this.monWidth.ToString(DIGIT_FORMAT));
                    string bottomValue = String.Format(COORDINATE_FORMAT, frameBottom.ToString(DIGIT_FORMAT), this.monHeight.ToString(DIGIT_FORMAT));
                    string rightValue = String.Format(COORDINATE_FORMAT, frameRight.ToString(DIGIT_FORMAT), this.monWidth.ToString(DIGIT_FORMAT));

                    //取得した比率をリストに格納
                    layoutRatioList.Add(topValue);
                    layoutRatioList.Add(leftValue);
                    layoutRatioList.Add(bottomValue);
                    layoutRatioList.Add(rightValue);
                }
            }
            //取得した比率リストを返す
            return layoutRatioList;
        }

        /// <summary>
        /// ペインのレイアウトを初期化する
        /// </summary>
        public void LayoutDeleteAll()
        {
            List<string> tagList = new List<string>();
           
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                tagList.Add(pane.Control.Tag.ToString());
            }
            foreach (string tag in tagList)
            {
                this.RemoveLayoutItem(tag);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Button_DragEnter(object sender, DragEventArgs e)
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
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Button_DragDrop(object sender, DragEventArgs e)
        {
            //ドロップされたデータがstring型か調べる
            if (e.Data.GetDataPresent(typeof(string)))
            {
               string itemText =
                    (string)e.Data.GetData(typeof(string));
                //ドロップされたデータを取得
                string EventName = ((Button)sender).Text;
                //イベントを発生　
                PaneDragDrop(this, new PaneHidden(e, itemText));
            }
        }

        /// <summary>
        /// コンテキストメニューアイテムを削除をクリック時
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DeleteToolStripItem_Click(object sender, EventArgs e)
        {
            //選択しているコントロールを閉じる。
            string controlitemname = this.ActiveControl.Tag as string;
            RemoveLayoutItem(controlitemname);
        }

        /// <summary>
        /// レイアウト表示部でマウスを右クリックした時のイベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ManageLayoutInfo_MouseDown(object sender, MouseEventArgs e)
        {
            // 右ボタンのクリックか？
            if (e.Button == MouseButtons.Right)
            {
               //コントロールが選択されているか確認
                if (!(this.ActiveControl == null))
                {
                    //アクティブなコントロールとマウス上のコントロールが同じかチェック
                    Button chk = (Button)sender;
                    if(string.Equals(this.ActiveControl.Name.ToString(),chk.Name.ToString()))
                    {
                        this.SetContext();
                        //コンテキストメニューを表示
                         this.contextMenuStrip.Show(System.Windows.Forms.Cursor.Position.X, System.Windows.Forms.Cursor.Position.Y);
                    }
                }
            }
        }

        /// <summary>
        /// 表示言語設定
        /// </summary>
        /// <param name="language">言語設定</param>
        public void SetLanguage(string language)
        {
            ManageLayoutInfo.LANGUAGE = language;
        }

        /// <summary>
        ///コンテキストメニュー表示言語切り替え用
        /// </summary>
        private void SetContext()
        {
            //多言語対応用
            switch (ManageLayoutInfo.LANGUAGE)
            {
                case LANGUAGE_SELECT_JP:
                    this.DeleteToolStripItem.Text = global::Dockingcontrollayout.Properties.Resources_jp.DeleteToolStripItem;
                    break;
                case LANGUAGE_SELECT_SC:
                    this.DeleteToolStripItem.Text = global::Dockingcontrollayout.Properties.Resources_zh_CHS.DeleteToolStripItem;
                    break;
                case LANGUAGE_SELECT_TC:
                    this.DeleteToolStripItem.Text = global::Dockingcontrollayout.Properties.Resources_zh_CHT.DeleteToolStripItem;
                    break;
                default:
                    this.DeleteToolStripItem.Text = global::Dockingcontrollayout.Properties.Resources.DeleteToolStripItem;
                    break;
            }
        }

        /// <summary>
        /// 各レイアウトの座標情報の取得用
        /// </summary>
        /// <param name="controlname"></param>
        /// <returns></returns>
        public List<string> CheckPanelItem(string controlname)
        {
            List<string> layoutRatioList = new List<string>();

            // レイアウト欄の左上座標
            Point layoutOrigin = this.PointToClient(this.Bounds.Location);

            //表示されているコントロールの情報を取得
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                if (string.Equals(controlname, pane.Control.Tag))
                {
                    // コントロールの左上座標
                    Point controlOrigin = this.PointToClient(pane.Control.Parent.Bounds.Location);

                    // コントロールの座標
                    double controlLeft = controlOrigin.X - layoutOrigin.X;
                    double controlTop = controlOrigin.Y - layoutOrigin.Y;
                    double controlBottom = pane.ActualSize.Height + (SPLIT_HALF * 2);
                    double controlRight = pane.ActualSize.Width + (SPLIT_HALF * 2);

                    double magWid = this.monWidth / (double)this.Width;
                    double magHei = this.monHeight / (double)this.Height;

                    // フレームの座標
                    double top = Math.Round(magHei * controlTop, MidpointRounding.AwayFromZero);
                    double left = Math.Round(magWid * controlLeft, MidpointRounding.AwayFromZero);
                    double bottom = Math.Round(magHei * (controlTop + controlBottom), MidpointRounding.AwayFromZero);
                    double right = Math.Round(magWid * (controlLeft + controlRight), MidpointRounding.AwayFromZero);

                    //描画領域をオーバーした場合の最大値にする
                    if (top > this.monHeight)
                    {
                        top = this.monHeight;
                    }

                    if (bottom > this.monHeight)
                    {
                        bottom = this.monHeight;
                    }

                    if (left > this.monWidth)
                    {
                        left = this.monWidth;
                    }

                    if (right > this.monWidth)
                    {
                        right = this.monWidth;
                    }

                    string topValue = String.Format(COORDINATE_FORMAT, top.ToString(DIGIT_FORMAT), this.monHeight.ToString(DIGIT_FORMAT));
                    string leftValue = String.Format(COORDINATE_FORMAT, left.ToString(DIGIT_FORMAT), this.monWidth.ToString(DIGIT_FORMAT));
                    string bottomValue = String.Format(COORDINATE_FORMAT, bottom.ToString(DIGIT_FORMAT), this.monHeight.ToString(DIGIT_FORMAT));
                    string rightValue = String.Format(COORDINATE_FORMAT, right.ToString(DIGIT_FORMAT), this.monWidth.ToString(DIGIT_FORMAT));

                    //取得した比率をリストに格納
                    layoutRatioList.Add(topValue);
                    layoutRatioList.Add(leftValue);
                    layoutRatioList.Add(bottomValue);
                    layoutRatioList.Add(rightValue);
                }
            }
            //取得した比率リストを返す
            return layoutRatioList;
        }

        /// <summary>
        /// 全てのレイアウトの座標再計算をさせる処理
        /// </summary>
        /// <param name="cellname">セルのアイテム名</param>
        public void SetPaneActiveBorder(string controlname)
        {
            //存在するペインをループ
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                //ペインをアクティブに
                pane.Activate();
            }

            Application.DoEvents();
        }
    }
}