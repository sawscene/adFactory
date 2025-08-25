namespace Dockingcontrollayout
{
    partial class ManageLayoutInfo
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

        #region コンポーネント デザイナで生成されたコード

        /// <summary>
        /// デザイナ サポートに必要なメソッドです。このメソッドの内容を 
        /// コード エディタで変更しないでください。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.ultraDockManager = new Infragistics.Win.UltraWinDock.UltraDockManager(this.components);
            this._ManageLayoutInfoUnpinnedTabAreaLeft = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._ManageLayoutInfoUnpinnedTabAreaRight = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._ManageLayoutInfoUnpinnedTabAreaTop = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._ManageLayoutInfoUnpinnedTabAreaBottom = new Infragistics.Win.UltraWinDock.UnpinnedTabArea();
            this._ManageLayoutInfoAutoHideControl = new Infragistics.Win.UltraWinDock.AutoHideControl();
            this.DeleteToolStripItem = new System.Windows.Forms.ToolStripMenuItem();
            this.contextMenuStrip = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.AddToolStripItem = new System.Windows.Forms.ToolStripMenuItem();
            ((System.ComponentModel.ISupportInitialize)(this.ultraDockManager)).BeginInit();
            this.contextMenuStrip.SuspendLayout();
            this.SuspendLayout();
            // 
            // ultraDockManager
            // 
            this.ultraDockManager.CaptionStyle = Infragistics.Win.UltraWinDock.CaptionStyle.VisualStudio2005;
            this.ultraDockManager.DefaultPaneSettings.AllowDockBottom = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.AllowDockLeft = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.AllowDockRight = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.AllowDockTop = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.AllowFloating = Infragistics.Win.DefaultableBoolean.False;
            this.ultraDockManager.DefaultPaneSettings.DoubleClickAction = Infragistics.Win.UltraWinDock.PaneDoubleClickAction.None;
            this.ultraDockManager.DefaultPaneSettings.PaddingBottom = 2;
            this.ultraDockManager.DefaultPaneSettings.PaddingLeft = 2;
            this.ultraDockManager.DefaultPaneSettings.PaddingRight = 2;
            this.ultraDockManager.DefaultPaneSettings.PaddingTop = 2;
            this.ultraDockManager.DragWindowStyle = Infragistics.Win.UltraWinDock.DragWindowStyle.LayeredWindow;
            this.ultraDockManager.HostControl = this;
            this.ultraDockManager.LayoutStyle = Infragistics.Win.UltraWinDock.DockAreaLayoutStyle.FillContainer;
            this.ultraDockManager.SaveSettingsFormat = Infragistics.Win.SaveSettingsFormat.Xml;
            this.ultraDockManager.ShowPinButton = false;
            this.ultraDockManager.UnpinnedTabHoverAction = Infragistics.Win.UltraWinDock.UnpinnedTabHoverAction.None;
            this.ultraDockManager.UseDefaultContextMenus = false;
            this.ultraDockManager.WindowStyle = Infragistics.Win.UltraWinDock.WindowStyle.Windows;
            this.ultraDockManager.PaneActivate += new Infragistics.Win.UltraWinDock.ControlPaneEventHandler(this.ultraDockManager_PaneActivate);
            this.ultraDockManager.PaneHidden += new Infragistics.Win.UltraWinDock.PaneHiddenEventHandler(this.ultraDockManager_PaneHidden);
            // 
            // _ManageLayoutInfoUnpinnedTabAreaLeft
            // 
            this._ManageLayoutInfoUnpinnedTabAreaLeft.Dock = System.Windows.Forms.DockStyle.Left;
            this._ManageLayoutInfoUnpinnedTabAreaLeft.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this._ManageLayoutInfoUnpinnedTabAreaLeft.Location = new System.Drawing.Point(0, 0);
            this._ManageLayoutInfoUnpinnedTabAreaLeft.Name = "_ManageLayoutInfoUnpinnedTabAreaLeft";
            this._ManageLayoutInfoUnpinnedTabAreaLeft.Owner = this.ultraDockManager;
            this._ManageLayoutInfoUnpinnedTabAreaLeft.Size = new System.Drawing.Size(0, 435);
            this._ManageLayoutInfoUnpinnedTabAreaLeft.TabIndex = 0;
            // 
            // _ManageLayoutInfoUnpinnedTabAreaRight
            // 
            this._ManageLayoutInfoUnpinnedTabAreaRight.Dock = System.Windows.Forms.DockStyle.Right;
            this._ManageLayoutInfoUnpinnedTabAreaRight.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this._ManageLayoutInfoUnpinnedTabAreaRight.Location = new System.Drawing.Point(698, 0);
            this._ManageLayoutInfoUnpinnedTabAreaRight.Name = "_ManageLayoutInfoUnpinnedTabAreaRight";
            this._ManageLayoutInfoUnpinnedTabAreaRight.Owner = this.ultraDockManager;
            this._ManageLayoutInfoUnpinnedTabAreaRight.Size = new System.Drawing.Size(0, 435);
            this._ManageLayoutInfoUnpinnedTabAreaRight.TabIndex = 1;
            // 
            // _ManageLayoutInfoUnpinnedTabAreaTop
            // 
            this._ManageLayoutInfoUnpinnedTabAreaTop.Dock = System.Windows.Forms.DockStyle.Top;
            this._ManageLayoutInfoUnpinnedTabAreaTop.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this._ManageLayoutInfoUnpinnedTabAreaTop.Location = new System.Drawing.Point(0, 0);
            this._ManageLayoutInfoUnpinnedTabAreaTop.Name = "_ManageLayoutInfoUnpinnedTabAreaTop";
            this._ManageLayoutInfoUnpinnedTabAreaTop.Owner = this.ultraDockManager;
            this._ManageLayoutInfoUnpinnedTabAreaTop.Size = new System.Drawing.Size(698, 0);
            this._ManageLayoutInfoUnpinnedTabAreaTop.TabIndex = 2;
            // 
            // _ManageLayoutInfoUnpinnedTabAreaBottom
            // 
            this._ManageLayoutInfoUnpinnedTabAreaBottom.Dock = System.Windows.Forms.DockStyle.Bottom;
            this._ManageLayoutInfoUnpinnedTabAreaBottom.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this._ManageLayoutInfoUnpinnedTabAreaBottom.Location = new System.Drawing.Point(0, 435);
            this._ManageLayoutInfoUnpinnedTabAreaBottom.Name = "_ManageLayoutInfoUnpinnedTabAreaBottom";
            this._ManageLayoutInfoUnpinnedTabAreaBottom.Owner = this.ultraDockManager;
            this._ManageLayoutInfoUnpinnedTabAreaBottom.Size = new System.Drawing.Size(698, 0);
            this._ManageLayoutInfoUnpinnedTabAreaBottom.TabIndex = 3;
            // 
            // _ManageLayoutInfoAutoHideControl
            // 
            this._ManageLayoutInfoAutoHideControl.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this._ManageLayoutInfoAutoHideControl.Location = new System.Drawing.Point(0, 0);
            this._ManageLayoutInfoAutoHideControl.Name = "_ManageLayoutInfoAutoHideControl";
            this._ManageLayoutInfoAutoHideControl.Owner = this.ultraDockManager;
            this._ManageLayoutInfoAutoHideControl.Size = new System.Drawing.Size(0, 435);
            this._ManageLayoutInfoAutoHideControl.TabIndex = 4;
            // 
            // DeleteToolStripItem
            // 
            this.DeleteToolStripItem.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F);
            this.DeleteToolStripItem.Name = "DeleteToolStripItem";
            this.DeleteToolStripItem.Size = new System.Drawing.Size(145, 22);
            this.DeleteToolStripItem.Text = "アイテムを削除";
            this.DeleteToolStripItem.Click += new System.EventHandler(this.DeleteToolStripItem_Click);
            // 
            // contextMenuStrip
            // 
            this.contextMenuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.DeleteToolStripItem});
            this.contextMenuStrip.Name = "contextMenuStrip";
            this.contextMenuStrip.Size = new System.Drawing.Size(146, 26);
            // 
            // AddToolStripItem
            // 
            this.AddToolStripItem.Name = "AddToolStripItem";
            this.AddToolStripItem.Size = new System.Drawing.Size(160, 22);
            this.AddToolStripItem.Text = "アイテムを追加";
            // 
            // ManageLayoutInfo
            // 
            this.AllowDrop = true;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this._ManageLayoutInfoAutoHideControl);
            this.Controls.Add(this._ManageLayoutInfoUnpinnedTabAreaLeft);
            this.Controls.Add(this._ManageLayoutInfoUnpinnedTabAreaTop);
            this.Controls.Add(this._ManageLayoutInfoUnpinnedTabAreaBottom);
            this.Controls.Add(this._ManageLayoutInfoUnpinnedTabAreaRight);
            this.Name = "ManageLayoutInfo";
            this.Size = new System.Drawing.Size(698, 435);
            this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.ManageLayoutInfo_MouseDown);
            ((System.ComponentModel.ISupportInitialize)(this.ultraDockManager)).EndInit();
            this.contextMenuStrip.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private Infragistics.Win.UltraWinDock.UltraDockManager ultraDockManager;
        private Infragistics.Win.UltraWinDock.AutoHideControl _ManageLayoutInfoAutoHideControl;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _ManageLayoutInfoUnpinnedTabAreaLeft;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _ManageLayoutInfoUnpinnedTabAreaTop;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _ManageLayoutInfoUnpinnedTabAreaBottom;
        private Infragistics.Win.UltraWinDock.UnpinnedTabArea _ManageLayoutInfoUnpinnedTabAreaRight;
        private System.Windows.Forms.ToolStripMenuItem DeleteToolStripItem;
        private System.Windows.Forms.ContextMenuStrip contextMenuStrip;
        private System.Windows.Forms.ToolStripMenuItem AddToolStripItem;

    }
}
