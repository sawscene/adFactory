namespace DashboardCustomizeTool
{
    partial class MainForm
    {
        /// <summary>
        /// 必要なデザイナ変数です。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 使用中のリソースをすべてクリーンアップします。
        /// </summary>
        /// <param name="disposing">マネージ リソースが破棄される場合 true、破棄されない場合は false です。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows フォーム デザイナで生成されたコード

        /// <summary>
        /// デザイナ サポートに必要なメソッドです。このメソッドの内容を
        /// コード エディタで変更しないでください。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            Infragistics.Win.UltraWinDock.DockAreaPane dockAreaPane1 = new Infragistics.Win.UltraWinDock.DockAreaPane(Infragistics.Win.UltraWinDock.DockedLocation.DockedLeft, new System.Guid("c44cc55e-14ea-46a1-8ea1-3736cb2984aa"));
            Infragistics.Win.UltraWinDock.DockableControlPane dockableControlPane1 = new Infragistics.Win.UltraWinDock.DockableControlPane(new System.Guid("754d58bd-41d4-4799-952d-cdc5b12e4551"), new System.Guid("00000000-0000-0000-0000-000000000000"), -1, new System.Guid("c44cc55e-14ea-46a1-8ea1-3736cb2984aa"), -1);
            Infragistics.Win.UltraWinDock.DockAreaPane dockAreaPane2 = new Infragistics.Win.UltraWinDock.DockAreaPane(Infragistics.Win.UltraWinDock.DockedLocation.DockedRight, new System.Guid("10b66e32-d6f2-4d92-8a81-391f5697d1aa"));
            Infragistics.Win.UltraWinDock.DockableControlPane dockableControlPane2 = new Infragistics.Win.UltraWinDock.DockableControlPane(new System.Guid("edf83dff-3d38-47d5-97f8-d49f0abdd609"), new System.Guid("00000000-0000-0000-0000-000000000000"), -1, new System.Guid("10b66e32-d6f2-4d92-8a81-391f5697d1aa"), -1);
            this.appItemGridView = new System.Windows.Forms.DataGridView();
            this.ItemName = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.manageLayoutInfo = new Dockingcontrollayout.ManageLayoutInfo();
            this.contextMenuStrip = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.AddToolStripItem = new System.Windows.Forms.ToolStripMenuItem();
            this.DeleteToolStripItem = new System.Windows.Forms.ToolStripMenuItem();
            this.ultraDockManager = new Infragistics.Win.UltraWinDock.UltraDockManager(this.components);
            this._Form1AutoHideControl = new Infragistics.Win.UltraWinDock.AutoHideControl();
            this._Form1UnpinnedTabAreaRight = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._Form1UnpinnedTabAreaBottom = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._Form1UnpinnedTabAreaTop = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._Form1UnpinnedTabAreaLeft = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this.windowDockingArea2 = new Infragistics.Win.UltraWinDock.WindowDockingArea();
            this.dockableWindow2 = new Infragistics.Win.UltraWinDock.DockableWindow();
            this.FileFToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.SaveToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.ImportToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.ExportToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.EndDToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.EditEToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.AddToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.DeleteToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.menuStrip = new System.Windows.Forms.MenuStrip();
            this.dockableWindow1 = new Infragistics.Win.UltraWinDock.DockableWindow();
            this.windowDockingArea1 = new Infragistics.Win.UltraWinDock.WindowDockingArea();
            ((System.ComponentModel.ISupportInitialize)(this.appItemGridView)).BeginInit();
            this.contextMenuStrip.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.ultraDockManager)).BeginInit();
            this.windowDockingArea2.SuspendLayout();
            this.dockableWindow2.SuspendLayout();
            this.menuStrip.SuspendLayout();
            this.dockableWindow1.SuspendLayout();
            this.windowDockingArea1.SuspendLayout();
            this.SuspendLayout();
            // 
            // appItemGridView
            // 
            this.appItemGridView.AllowUserToAddRows = false;
            this.appItemGridView.AllowUserToDeleteRows = false;
            this.appItemGridView.AllowUserToResizeColumns = false;
            this.appItemGridView.AllowUserToResizeRows = false;
            this.appItemGridView.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.appItemGridView.BackgroundColor = System.Drawing.Color.White;
            this.appItemGridView.CellBorderStyle = System.Windows.Forms.DataGridViewCellBorderStyle.None;
            this.appItemGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.appItemGridView.ColumnHeadersVisible = false;
            this.appItemGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.ItemName});
            dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleLeft;
            dataGridViewCellStyle1.BackColor = System.Drawing.SystemColors.Window;
            dataGridViewCellStyle1.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F);
            dataGridViewCellStyle1.ForeColor = System.Drawing.SystemColors.ControlText;
            dataGridViewCellStyle1.SelectionBackColor = System.Drawing.Color.White;
            dataGridViewCellStyle1.SelectionForeColor = System.Drawing.SystemColors.ControlText;
            dataGridViewCellStyle1.WrapMode = System.Windows.Forms.DataGridViewTriState.False;
            this.appItemGridView.DefaultCellStyle = dataGridViewCellStyle1;
            resources.ApplyResources(this.appItemGridView, "appItemGridView");
            this.appItemGridView.EnableHeadersVisualStyles = false;
            this.appItemGridView.MultiSelect = false;
            this.appItemGridView.Name = "appItemGridView";
            this.appItemGridView.ReadOnly = true;
            this.appItemGridView.RowHeadersBorderStyle = System.Windows.Forms.DataGridViewHeaderBorderStyle.None;
            this.appItemGridView.RowHeadersWidthSizeMode = System.Windows.Forms.DataGridViewRowHeadersWidthSizeMode.DisableResizing;
            this.appItemGridView.RowTemplate.Height = 21;
            this.appItemGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.appItemGridView.StandardTab = true;
            this.appItemGridView.TabStop = false;
            this.appItemGridView.CellMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.appItemGridView_CellMouseClick);
            this.appItemGridView.CellMouseDown += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.appItemGridView_CellMouseDown);
            this.appItemGridView.CellPainting += new System.Windows.Forms.DataGridViewCellPaintingEventHandler(this.dataGridView1_CellPainting);
            this.appItemGridView.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.appItemGridView_CellClick);
            this.appItemGridView.KeyDown += new System.Windows.Forms.KeyEventHandler(this.appItemGridView_KeyDown);
            this.appItemGridView.SelectionChanged += new System.EventHandler(this.appItemGridView_SelectionChanged);
            // 
            // ItemName
            // 
            this.ItemName.FillWeight = 5.128205F;
            resources.ApplyResources(this.ItemName, "ItemName");
            this.ItemName.Name = "ItemName";
            this.ItemName.ReadOnly = true;
            // 
            // manageLayoutInfo
            // 
            this.manageLayoutInfo.AllowDrop = true;
            resources.ApplyResources(this.manageLayoutInfo, "manageLayoutInfo");
            this.manageLayoutInfo.Name = "manageLayoutInfo";
            this.manageLayoutInfo.Paint += new System.Windows.Forms.PaintEventHandler(this.manageLayoutInfo_Paint);
            this.manageLayoutInfo.PaneDragDrop += new Dockingcontrollayout.ManageLayoutInfo.PaneDragDropHandler(this.manageLayoutInfo1_PaneDragDrop);
            this.manageLayoutInfo.DragDrop += new System.Windows.Forms.DragEventHandler(this.manageLayoutInfo1_DragDrop);
            this.manageLayoutInfo.PaneClick += new Dockingcontrollayout.ManageLayoutInfo.PaneClickHandler(this.manageLayoutInfo1_PaneClick);
            this.manageLayoutInfo.CloseClick += new Dockingcontrollayout.ManageLayoutInfo.CloseClickHandler(this.manageLayoutInfo1_MyClick);
            this.manageLayoutInfo.DragEnter += new System.Windows.Forms.DragEventHandler(this.manageLayoutInfo1_DragEnter);
            this.manageLayoutInfo.KeyDown += new System.Windows.Forms.KeyEventHandler(this.Form_KeyDown);
            // 
            // contextMenuStrip
            // 
            resources.ApplyResources(this.contextMenuStrip, "contextMenuStrip");
            this.contextMenuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.AddToolStripItem,
            this.DeleteToolStripItem});
            this.contextMenuStrip.Name = "contextMenuStrip";
            // 
            // AddToolStripItem
            // 
            resources.ApplyResources(this.AddToolStripItem, "AddToolStripItem");
            this.AddToolStripItem.Name = "AddToolStripItem";
            this.AddToolStripItem.Click += new System.EventHandler(this.AddToolStripMenuItem1_Click);
            // 
            // DeleteToolStripItem
            // 
            resources.ApplyResources(this.DeleteToolStripItem, "DeleteToolStripItem");
            this.DeleteToolStripItem.Name = "DeleteToolStripItem";
            this.DeleteToolStripItem.Click += new System.EventHandler(this.DeleteToolStripMenuItem_Click);
            // 
            // ultraDockManager
            // 
            this.ultraDockManager.DefaultPaneSettings.AllowFloating = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.AllowPin = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.AllowResize = Infragistics.Win.DefaultableBoolean.True;
            this.ultraDockManager.DefaultPaneSettings.DoubleClickAction = Infragistics.Win.UltraWinDock.PaneDoubleClickAction.None;
            dockAreaPane1.DockedBefore = new System.Guid("10b66e32-d6f2-4d92-8a81-391f5697d1aa");
            dockableControlPane1.Control = this.appItemGridView;
            dockableControlPane1.OriginalControlBounds = new System.Drawing.Rectangle(0, 0, 240, 150);
            dockableControlPane1.Settings.AllowClose = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowDockAsTab = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowDockBottom = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowDockLeft = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowDockRight = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowDockTop = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowDragging = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowFloating = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowMaximize = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowMinimize = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.AllowPin = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Settings.ShowCaption = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane1.Size = new System.Drawing.Size(100, 100);
            resources.ApplyResources(dockableControlPane1, "dockableControlPane1");
            dockAreaPane1.Panes.AddRange(new Infragistics.Win.UltraWinDock.DockablePaneBase[] {
            dockableControlPane1});
            dockAreaPane1.Size = new System.Drawing.Size(197, 538);
            dockableControlPane2.Control = this.manageLayoutInfo;
            dockableControlPane2.OriginalControlBounds = new System.Drawing.Rectangle(271, 61, 698, 435);
            dockableControlPane2.Settings.AllowClose = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowDockAsTab = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowDockBottom = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowDockLeft = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowDockRight = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowDockTop = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowDragging = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowFloating = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowMaximize = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowMinimize = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowPin = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.AllowResize = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Settings.ShowCaption = Infragistics.Win.DefaultableBoolean.False;
            dockableControlPane2.Size = new System.Drawing.Size(100, 100);
            resources.ApplyResources(dockableControlPane2, "dockableControlPane2");
            dockAreaPane2.Panes.AddRange(new Infragistics.Win.UltraWinDock.DockablePaneBase[] {
            dockableControlPane2});
            dockAreaPane2.Size = new System.Drawing.Size(582, 538);
            dockAreaPane2.UnfilledSize = new System.Drawing.Size(100, 381);
            this.ultraDockManager.DockAreas.AddRange(new Infragistics.Win.UltraWinDock.DockAreaPane[] {
            dockAreaPane1,
            dockAreaPane2});
            this.ultraDockManager.DragWindowStyle = Infragistics.Win.UltraWinDock.DragWindowStyle.LayeredWindow;
            this.ultraDockManager.HostControl = this;
            this.ultraDockManager.LayoutStyle = Infragistics.Win.UltraWinDock.DockAreaLayoutStyle.FillContainer;
            this.ultraDockManager.ShowDisabledButtons = false;
            this.ultraDockManager.PaneActivate += new Infragistics.Win.UltraWinDock.ControlPaneEventHandler(this.ultraDockManager_PaneActivate);
            // 
            // _Form1AutoHideControl
            // 
            resources.ApplyResources(this._Form1AutoHideControl, "_Form1AutoHideControl");
            this._Form1AutoHideControl.Name = "_Form1AutoHideControl";
            this._Form1AutoHideControl.Owner = this.ultraDockManager;
            // 
            // _Form1UnpinnedTabAreaRight
            // 
            resources.ApplyResources(this._Form1UnpinnedTabAreaRight, "_Form1UnpinnedTabAreaRight");
            this._Form1UnpinnedTabAreaRight.Name = "_Form1UnpinnedTabAreaRight";
            this._Form1UnpinnedTabAreaRight.Owner = this.ultraDockManager;
            // 
            // _Form1UnpinnedTabAreaBottom
            // 
            resources.ApplyResources(this._Form1UnpinnedTabAreaBottom, "_Form1UnpinnedTabAreaBottom");
            this._Form1UnpinnedTabAreaBottom.Name = "_Form1UnpinnedTabAreaBottom";
            this._Form1UnpinnedTabAreaBottom.Owner = this.ultraDockManager;
            // 
            // _Form1UnpinnedTabAreaTop
            // 
            resources.ApplyResources(this._Form1UnpinnedTabAreaTop, "_Form1UnpinnedTabAreaTop");
            this._Form1UnpinnedTabAreaTop.Name = "_Form1UnpinnedTabAreaTop";
            this._Form1UnpinnedTabAreaTop.Owner = this.ultraDockManager;
            // 
            // _Form1UnpinnedTabAreaLeft
            // 
            resources.ApplyResources(this._Form1UnpinnedTabAreaLeft, "_Form1UnpinnedTabAreaLeft");
            this._Form1UnpinnedTabAreaLeft.Name = "_Form1UnpinnedTabAreaLeft";
            this._Form1UnpinnedTabAreaLeft.Owner = this.ultraDockManager;
            // 
            // windowDockingArea2
            // 
            this.windowDockingArea2.Controls.Add(this.dockableWindow2);
            resources.ApplyResources(this.windowDockingArea2, "windowDockingArea2");
            this.windowDockingArea2.Name = "windowDockingArea2";
            this.windowDockingArea2.Owner = this.ultraDockManager;
            // 
            // dockableWindow2
            // 
            this.dockableWindow2.Controls.Add(this.appItemGridView);
            resources.ApplyResources(this.dockableWindow2, "dockableWindow2");
            this.dockableWindow2.Name = "dockableWindow2";
            this.dockableWindow2.Owner = this.ultraDockManager;
            // 
            // FileFToolStripMenuItem
            // 
            this.FileFToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.SaveToolStripMenuItem,
            this.ImportToolStripMenuItem,
            this.ExportToolStripMenuItem,
            this.EndDToolStripMenuItem});
            resources.ApplyResources(this.FileFToolStripMenuItem, "FileFToolStripMenuItem");
            this.FileFToolStripMenuItem.Name = "FileFToolStripMenuItem";
            // 
            // SaveToolStripMenuItem
            // 
            this.SaveToolStripMenuItem.Name = "SaveToolStripMenuItem";
            resources.ApplyResources(this.SaveToolStripMenuItem, "SaveToolStripMenuItem");
            this.SaveToolStripMenuItem.Click += new System.EventHandler(this.SaveToolStripMenuItem_Click);
            // 
            // ImportToolStripMenuItem
            // 
            this.ImportToolStripMenuItem.Name = "ImportToolStripMenuItem";
            resources.ApplyResources(this.ImportToolStripMenuItem, "ImportToolStripMenuItem");
            this.ImportToolStripMenuItem.Click += new System.EventHandler(this.ImportToolStripMenuItem_Click);
            // 
            // ExportToolStripMenuItem
            // 
            this.ExportToolStripMenuItem.Name = "ExportToolStripMenuItem";
            resources.ApplyResources(this.ExportToolStripMenuItem, "ExportToolStripMenuItem");
            this.ExportToolStripMenuItem.Click += new System.EventHandler(this.ExportToolStripMenuItem_Click);
            // 
            // EndDToolStripMenuItem
            // 
            this.EndDToolStripMenuItem.Name = "EndDToolStripMenuItem";
            resources.ApplyResources(this.EndDToolStripMenuItem, "EndDToolStripMenuItem");
            this.EndDToolStripMenuItem.Click += new System.EventHandler(this.EndToolStripMenuItem_Click);
            // 
            // EditEToolStripMenuItem
            // 
            this.EditEToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.AddToolStripMenuItem,
            this.DeleteToolStripMenuItem});
            resources.ApplyResources(this.EditEToolStripMenuItem, "EditEToolStripMenuItem");
            this.EditEToolStripMenuItem.Name = "EditEToolStripMenuItem";
            this.EditEToolStripMenuItem.DropDownOpened += new System.EventHandler(this.EditEToolStripMenuItem_DropDownOpened);
            // 
            // AddToolStripMenuItem
            // 
            this.AddToolStripMenuItem.Name = "AddToolStripMenuItem";
            resources.ApplyResources(this.AddToolStripMenuItem, "AddToolStripMenuItem");
            this.AddToolStripMenuItem.Click += new System.EventHandler(this.AddAToolStripMenuItem_Click);
            // 
            // DeleteToolStripMenuItem
            // 
            this.DeleteToolStripMenuItem.Name = "DeleteToolStripMenuItem";
            resources.ApplyResources(this.DeleteToolStripMenuItem, "DeleteToolStripMenuItem");
            this.DeleteToolStripMenuItem.Click += new System.EventHandler(this.DeleteToolStripMenuItem_Click);
            // 
            // menuStrip
            // 
            resources.ApplyResources(this.menuStrip, "menuStrip");
            this.menuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.FileFToolStripMenuItem,
            this.EditEToolStripMenuItem});
            this.menuStrip.Name = "menuStrip";
            // 
            // dockableWindow1
            // 
            this.dockableWindow1.Controls.Add(this.manageLayoutInfo);
            resources.ApplyResources(this.dockableWindow1, "dockableWindow1");
            this.dockableWindow1.Name = "dockableWindow1";
            this.dockableWindow1.Owner = this.ultraDockManager;
            // 
            // windowDockingArea1
            // 
            this.windowDockingArea1.Controls.Add(this.dockableWindow1);
            resources.ApplyResources(this.windowDockingArea1, "windowDockingArea1");
            this.windowDockingArea1.Name = "windowDockingArea1";
            this.windowDockingArea1.Owner = this.ultraDockManager;
            // 
            // MainForm
            // 
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this._Form1AutoHideControl);
            this.Controls.Add(this.windowDockingArea1);
            this.Controls.Add(this.windowDockingArea2);
            this.Controls.Add(this._Form1UnpinnedTabAreaTop);
            this.Controls.Add(this._Form1UnpinnedTabAreaBottom);
            this.Controls.Add(this._Form1UnpinnedTabAreaLeft);
            this.Controls.Add(this._Form1UnpinnedTabAreaRight);
            this.Controls.Add(this.menuStrip);
            this.KeyPreview = true;
            this.Name = "MainForm";
            this.Load += new System.EventHandler(this.Form_Load);
            this.Shown += new System.EventHandler(this.Form_Shown);
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form_FormClosing);
            this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.Form_KeyDown);
            ((System.ComponentModel.ISupportInitialize)(this.appItemGridView)).EndInit();
            this.contextMenuStrip.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.ultraDockManager)).EndInit();
            this.windowDockingArea2.ResumeLayout(false);
            this.dockableWindow2.ResumeLayout(false);
            this.menuStrip.ResumeLayout(false);
            this.menuStrip.PerformLayout();
            this.dockableWindow1.ResumeLayout(false);
            this.windowDockingArea1.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.DataGridView appItemGridView;
        private System.Windows.Forms.ContextMenuStrip contextMenuStrip;
        private System.Windows.Forms.ToolStripMenuItem AddToolStripItem;
        private System.Windows.Forms.ToolStripMenuItem DeleteToolStripItem;
        private Dockingcontrollayout.ManageLayoutInfo manageLayoutInfo;
        private Infragistics.Win.UltraWinDock.UltraDockManager ultraDockManager;
        private Infragistics.Win.UltraWinDock.AutoHideControl _Form1AutoHideControl;
        private Infragistics.Win.UltraWinDock.WindowDockingArea windowDockingArea1;
        private Infragistics.Win.UltraWinDock.DockableWindow dockableWindow2;
        private Infragistics.Win.UltraWinDock.WindowDockingArea windowDockingArea2;
        private Infragistics.Win.UltraWinDock.DockableWindow dockableWindow1;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _Form1UnpinnedTabAreaTop;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _Form1UnpinnedTabAreaBottom;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _Form1UnpinnedTabAreaLeft;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _Form1UnpinnedTabAreaRight;
        private System.Windows.Forms.MenuStrip menuStrip;
        private System.Windows.Forms.ToolStripMenuItem FileFToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem SaveToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem ImportToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem ExportToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem EndDToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem EditEToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem AddToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem DeleteToolStripMenuItem;
        private System.Windows.Forms.DataGridViewTextBoxColumn ItemName;


    }
}
