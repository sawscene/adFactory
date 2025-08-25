using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Infragistics.Win.UltraWinDock;
using System.Diagnostics;
using System.Collections;
using System.IO;
using System.Xml;

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
        static public String LANGUAGE = "en";

        private const string LANGUAGE_SELECT_JP = "JP";  //日本語
        private const string LANGUAGE_SELECT_US = "US";  //英語
        private const string LANGUAGE_SELECT_SC = "SC";　//簡体字
        private const string LANGUAGE_SELECT_TC = "TC";  //繁体字
        //フォント
        private const string DEFAULT_FONT = "Microsoft Sans Serif";
        private const int DEFAULT_FONT_SIZE = 10;



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

        public ManageLayoutInfo()
        {
            InitializeComponent();
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
                    for (int i = 0; i <= count - 1; i++)
                    {
                        //ボタンコントロール化どうか判断（追加するコントロールが変更された場合は変更すること）
                        if (buf[i].GetType().Equals(typeof(Button)))
                        {
                            //indexを指定して削除
                           this.Controls.RemoveAt(i);
                            count = -1;
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
            //XMLを一度クリアして再作成   
            System.IO.File.Delete(fullpath);
            this.ultraDockManager.SaveAsXML(fullpath);
            //保存時に表示していたペインを非表示に
            this.RemoveLayoutItem(PANE_SPACE);
        }

        /// <summary>
        /// レイアウト画面にペインを追加する処理
        /// </summary>
        /// <param name="name">テキスト表示名</param>
        /// <param name="colorcode">カラー情報(16進数)</param>
        public void AddLayoutItem(string name,Color colorcode)
        {
            //仮置きのアイテムを指定
            Button but = new Button();
            //コントロールに表示情報などの初期情報を代入
            but.Name = name.ToString(); 
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
            con.Tag =  but.Name;
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
        /// <param name="controlname">削除するペイン名</param>
        public void RemoveLayoutItem(string controlname)
        {
            //ペインを格納しているコントロールのコレクションないを検索
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                //ペインの表示されているアイテム名
                string controlitemname = pane.Control.Name.ToString();
                if (string.Equals(controlname, controlitemname))
                {
                    //ペインの閉じる
                    pane.Close();
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
        /// レイアウトの全削除処理
        /// </summary>
        /// <param name="controlname">削除するペイン名</param>
        public void RemoveLayoutItemAll(string controlname)
        {
            //ペインを格納しているコントロールのコレクションないを検索
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                //ペインの表示されているアイテム名
                string controlitemname = pane.Control.Tag.ToString();
                if (string.Equals(controlname, controlitemname))
                {
                    //ペインの閉じる
                    pane.Close();
                    //ペインのリソースを開放
                    pane.Control.Dispose();
                }
            }
        }

        /// <summary>
        /// 選択されているセル関連するペインを赤枠表示にするメソッド
        /// </summary>
        /// <param name="cellname">セルのアイテム名</param>
        public void PaneActiveBorder(string controlname)
        {
            //存在するペインをループ
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                //コントロール名とItemNameが同じペインを探す
                if (string.Equals(controlname.ToString(), pane.Control.Tag.ToString()))
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
            double Minimumwidth = this.Size.Width;
            double Minimumheight = this.Size.Height;
            //比率の最大数
            int maxwidthcount = 0;
            int maxheightcount = 0;

            //すべてのコントロールを調べて最小の比率を求める。
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                //非表示ペインを除くペインで計算する。
                if(!(string.Equals(PANE_SPACE,pane.Control.Name.ToString()))){
                    //コントロールの幅、高さ
                    double controlwidth = 0;
                    double controlheight = 0;
                    //コントロールのサイズを取得
                    controlheight = pane.Control.Bounds.Bottom;
                    controlwidth = pane.Control.Bounds.Right;
                    //最小値を取得
                    if(Minimumwidth > controlwidth)
                    {
                        Minimumwidth = controlwidth;
                    }
                    if(Minimumheight > controlheight)
                    {
                        Minimumheight = controlheight;
                    }
                }
            }

            //最小値を元に比率の最大値を取得する
            maxwidthcount =Convert.ToInt16(Math.Floor( this.Size.Width / Minimumwidth ));
            maxheightcount = Convert.ToInt16(Math.Floor( this.Size.Height / Minimumheight ));

            //表示されているコントロールの情報を取得
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                if (string.Equals(controlname, pane.Control.Name))
                {
                    // フォームの左上隅の原点を（クライアント座標で）取得する
                    Point winRectLocation = this.PointToClient(this.Bounds.Location);

                    // コントロールの左上隅の原点を（クライアント座標で）取得する
                    Point winRectLocation11 = pane.Control.PointToClient(pane.Control.Bounds.Location);
                    //スタート位置比率
                    double top =0;
                    double left =0;
                    double Bottom =0;
                    double Right =0;
                    //コントロールの各位置情報
                    string toppoint = string.Empty;
                    string Leftpoint = string.Empty;
                    string Bottompoint = string.Empty;
                    string Rightpoint = string.Empty;

                    //コントロールの幅、高さ
                    double controlwidth = 0;
                    double controlheight = 0;
         
                    //コントロールのサイズを出す演算子
                    double controlleft = 0;
                    double controltop = 0;

                    //コントロールのX,Y座標の取得
                    controlleft = (((winRectLocation11.X - winRectLocation.X) * -1));
                    controltop = (((winRectLocation11.Y - winRectLocation.Y) * -1));
                    //コントロールのサイズを取得
                    controlheight = pane.Control.Bounds.Bottom;
                  
                    controlwidth = pane.Control.Bounds.Right;
                    //最小サイズを基準に比率を求める
                    top = Convert.ToInt16(Math.Floor(controltop / Minimumheight));
                    left = Convert.ToInt16(Math.Floor(controlleft / Minimumwidth));

                    Bottom = Convert.ToInt16(Math.Floor((controltop + controlheight) / Minimumheight));
                    Right = Convert.ToInt16(Math.Floor((controlleft + controlwidth) / Minimumwidth));
                    //描画領域をオーバーした場合の最大値にする
                    if (top > maxheightcount)
                    {
                        top = maxheightcount;
                    }
                    if (Bottom > maxheightcount)
                    {
                        Bottom = maxheightcount;
                    }

                    if (left > maxwidthcount)
                    {
                        left = maxwidthcount;
                    }
                    if (Right > maxwidthcount)
                    {
                        Right = maxwidthcount;
                    }
                    toppoint = String.Format(COORDINATE_FORMAT, top.ToString(DIGIT_FORMAT), maxheightcount.ToString(DIGIT_FORMAT));
                    Leftpoint = String.Format(COORDINATE_FORMAT, left.ToString(DIGIT_FORMAT), maxwidthcount.ToString(DIGIT_FORMAT));
                    Bottompoint = String.Format(COORDINATE_FORMAT, Bottom.ToString(DIGIT_FORMAT), maxheightcount.ToString(DIGIT_FORMAT));
                    Rightpoint = String.Format(COORDINATE_FORMAT, Right.ToString(DIGIT_FORMAT), maxwidthcount.ToString(DIGIT_FORMAT));

                    //取得した比率をリストに格納
                    layoutRatioList.Add(toppoint.ToString());
                    layoutRatioList.Add(Leftpoint.ToString());
                    layoutRatioList.Add(Bottompoint.ToString());
                    layoutRatioList.Add(Rightpoint.ToString());
                }
            }
            //取得した比率リストを返す
            return layoutRatioList;
        }
        //ペインのレイアウトを初期化する
        public void LayoutDeleteAll()
        {
            List<string> list = new List<string>();
           
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                list.Add(pane.Control.Name);
            }
            foreach (string namelist in list)
            {
                this.RemoveLayoutItemAll(namelist.ToString());
            }
        }
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
            RemoveLayoutItem(this.ActiveControl.Name);
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


        //#8111 レイアウト未変更時に終了すると保存確認のメッセージが表示される
        //2012/10/19 ADTEK 池松 START
        /// <summary>
        /// 各レイアウトの座標情報の取得用
        /// </summary>
        /// <param name="controlname"></param>
        /// <returns></returns>
        public List<string> CheckPanelItem(string controlname)
        {
            List<string> layoutRatioList = new List<string>();
            //表示されているコントロールの情報を取得
            foreach (DockableControlPane pane in this.ultraDockManager.ControlPanes)
            {
                if (string.Equals(controlname, pane.Control.Name))
                {
                    // フォームの左上隅の原点を（クライアント座標で）取得する
                    Point winRectLocation = this.PointToClient(this.Bounds.Location);
                    // コントロールの左上隅の原点を（クライアント座標で）取得する
                    Point winRectLocation11 = pane.Control.PointToClient(pane.Control.Bounds.Location);
                    //コントロールの各位置情報
                    string toppoint = string.Empty;
                    string Leftpoint = string.Empty;
                    string Bottompoint = string.Empty;
                    string Rightpoint = string.Empty;

                    //コントロールの幅、高さ
                    double controlwidth = 0;
                    double controlheight = 0;

                    //コントロールのサイズを出す演算子
                    double controlleft = 0;
                    double controltop = 0;

                    //コントロールのX,Y座標の取得
                    controlleft = (((winRectLocation11.X - winRectLocation.X) * -1));
                    controltop = (((winRectLocation11.Y - winRectLocation.Y) * -1));
                    //コントロールのサイズを取得
                    controlheight = pane.Control.Bounds.Bottom;
                    controlwidth = pane.Control.Bounds.Right;

                    toppoint =controltop.ToString();
                    Leftpoint = controlleft.ToString();
                    Bottompoint =controlheight.ToString();
                    Rightpoint = controlwidth.ToString();
                    //取得した比率をリストに格納
                    layoutRatioList.Add(toppoint.ToString());
                    layoutRatioList.Add(Leftpoint.ToString());
                    layoutRatioList.Add(Bottompoint.ToString());
                    layoutRatioList.Add(Rightpoint.ToString());
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
        }
        //#8111 END
    }
}