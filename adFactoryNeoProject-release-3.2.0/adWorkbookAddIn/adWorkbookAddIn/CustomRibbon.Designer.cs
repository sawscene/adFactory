namespace adWorkbookAddIn
{
    partial class CustomRibbon : Microsoft.Office.Tools.Ribbon.RibbonBase
    {
        /// <summary>
        /// 必要なデザイナー変数です。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        public CustomRibbon()
            : base(Globals.Factory.GetRibbonFactory())
        {
            InitializeComponent();
        }

        /// <summary> 
        /// 使用中のリソースをすべてクリーンアップします。
        /// </summary>
        /// <param name="disposing">マネージド リソースを破棄する場合は true を指定し、その他の場合は false を指定します。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region コンポーネント デザイナーで生成されたコード

        /// <summary>
        /// デザイナー サポートに必要なメソッドです。このメソッドの内容を
        /// コード エディターで変更しないでください。
        /// </summary>
        private void InitializeComponent()
        {
            this.CustomTab = this.Factory.CreateRibbonTab();
            this.LinkGroup = this.Factory.CreateRibbonGroup();
            this.loginButton = this.Factory.CreateRibbonButton();
            this.uploadButton = this.Factory.CreateRibbonButton();
            this.SectionGroup = this.Factory.CreateRibbonGroup();
            this.operateWorkProcedure = this.Factory.CreateRibbonButton();
            this.drawingSupportButton = this.Factory.CreateRibbonButton();
            this.dataCheckButton = this.Factory.CreateRibbonButton();
            this.HelpGroup = this.Factory.CreateRibbonGroup();
            this.aboutButton = this.Factory.CreateRibbonButton();
            this.manualButton = this.Factory.CreateRibbonButton();
            this.CustomTab.SuspendLayout();
            this.LinkGroup.SuspendLayout();
            this.SectionGroup.SuspendLayout();
            this.HelpGroup.SuspendLayout();
            this.SuspendLayout();
            // 
            // CustomTab
            // 
            this.CustomTab.ControlId.ControlIdType = Microsoft.Office.Tools.Ribbon.RibbonControlIdType.Office;
            this.CustomTab.Groups.Add(this.LinkGroup);
            this.CustomTab.Groups.Add(this.SectionGroup);
            this.CustomTab.Groups.Add(this.HelpGroup);
            this.CustomTab.Label = "adFactory";
            this.CustomTab.Name = "CustomTab";
            // 
            // LinkGroup
            // 
            this.LinkGroup.Items.Add(this.loginButton);
            this.LinkGroup.Items.Add(this.uploadButton);
            this.LinkGroup.Label = "連携";
            this.LinkGroup.Name = "LinkGroup";
            // 
            // loginButton
            // 
            this.loginButton.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.loginButton.Enabled = false;
            this.loginButton.Image = global::adWorkbookAddIn.Properties.Resources.login_80;
            this.loginButton.Label = "ログイン";
            this.loginButton.Name = "loginButton";
            this.loginButton.ScreenTip = "ログイン";
            this.loginButton.ShowImage = true;
            this.loginButton.SuperTip = "adFactoryのログインIDとパスワードを入力してログインします。";
            this.loginButton.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.LoginButton_Click);
            // 
            // uploadButton
            // 
            this.uploadButton.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.uploadButton.Enabled = false;
            this.uploadButton.Image = global::adWorkbookAddIn.Properties.Resources.upload_80;
            this.uploadButton.Label = "アップロード";
            this.uploadButton.Name = "uploadButton";
            this.uploadButton.ScreenTip = "アップロード";
            this.uploadButton.ShowImage = true;
            this.uploadButton.SuperTip = "作成した作業手順書をadFactoryに登録します。";
            this.uploadButton.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.UploadButton_Click);
            // 
            // SectionGroup
            // 
            this.SectionGroup.Items.Add(this.operateWorkProcedure);
            this.SectionGroup.Items.Add(this.drawingSupportButton);
            this.SectionGroup.Items.Add(this.dataCheckButton);
            this.SectionGroup.Label = "編集";
            this.SectionGroup.Name = "SectionGroup";
            // 
            // operateWorkProcedure
            // 
            this.operateWorkProcedure.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.operateWorkProcedure.Enabled = false;
            this.operateWorkProcedure.Image = global::adWorkbookAddIn.Properties.Resources.work_80;
            this.operateWorkProcedure.Label = "作業手順";
            this.operateWorkProcedure.Name = "operateWorkProcedure";
            this.operateWorkProcedure.ScreenTip = "作業手順";
            this.operateWorkProcedure.ShowImage = true;
            this.operateWorkProcedure.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.OperateWorkProcedure_Click);
            // 
            // drawingSupportButton
            // 
            this.drawingSupportButton.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.drawingSupportButton.Enabled = false;
            this.drawingSupportButton.Image = global::adWorkbookAddIn.Properties.Resources.draw_80;
            this.drawingSupportButton.Label = "作図支援";
            this.drawingSupportButton.Name = "drawingSupportButton";
            this.drawingSupportButton.ScreenTip = "作図支援";
            this.drawingSupportButton.ShowImage = true;
            this.drawingSupportButton.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.DrawingSupportButton_Click);
            // 
            // dataCheckButton
            // 
            this.dataCheckButton.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.dataCheckButton.Enabled = false;
            this.dataCheckButton.Image = global::adWorkbookAddIn.Properties.Resources.detacheck_80;
            this.dataCheckButton.Label = "データチェック";
            this.dataCheckButton.Name = "dataCheckButton";
            this.dataCheckButton.ScreenTip = "データチェック";
            this.dataCheckButton.ShowImage = true;
            this.dataCheckButton.SuperTip = "作成した作業手順書を検証します。";
            this.dataCheckButton.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.DataCheckButton_Click);
            // 
            // HelpGroup
            // 
            this.HelpGroup.Items.Add(this.aboutButton);
            this.HelpGroup.Items.Add(this.manualButton);
            this.HelpGroup.Label = "ヘルプ";
            this.HelpGroup.Name = "HelpGroup";
            // 
            // aboutButton
            // 
            this.aboutButton.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.aboutButton.Enabled = false;
            this.aboutButton.Image = global::adWorkbookAddIn.Properties.Resources.version_80;
            this.aboutButton.Label = "バージョン情報";
            this.aboutButton.Name = "aboutButton";
            this.aboutButton.ScreenTip = "バージョン情報";
            this.aboutButton.ShowImage = true;
            this.aboutButton.SuperTip = "作業手順書作成ツールのバージョンを確認できます。";
            this.aboutButton.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.AboutButton_Click);
            // 
            // manualButton
            // 
            this.manualButton.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.manualButton.Enabled = false;
            this.manualButton.Image = global::adWorkbookAddIn.Properties.Resources.help_80;
            this.manualButton.Label = "操作ガイド";
            this.manualButton.Name = "manualButton";
            this.manualButton.ScreenTip = "操作ガイド";
            this.manualButton.ShowImage = true;
            this.manualButton.SuperTip = "作業手順書の作成方法を学ぶことができます。";
            this.manualButton.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.ManualButton_Click);
            // 
            // CustomRibbon
            // 
            this.Name = "CustomRibbon";
            this.RibbonType = "Microsoft.Excel.Workbook";
            this.Tabs.Add(this.CustomTab);
            this.CustomTab.ResumeLayout(false);
            this.CustomTab.PerformLayout();
            this.LinkGroup.ResumeLayout(false);
            this.LinkGroup.PerformLayout();
            this.SectionGroup.ResumeLayout(false);
            this.SectionGroup.PerformLayout();
            this.HelpGroup.ResumeLayout(false);
            this.HelpGroup.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        internal Microsoft.Office.Tools.Ribbon.RibbonTab CustomTab;
        internal Microsoft.Office.Tools.Ribbon.RibbonGroup SectionGroup;
        internal Microsoft.Office.Tools.Ribbon.RibbonGroup HelpGroup;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton aboutButton;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton manualButton;
        internal Microsoft.Office.Tools.Ribbon.RibbonGroup LinkGroup;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton uploadButton;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton dataCheckButton;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton drawingSupportButton;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton operateWorkProcedure;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton loginButton;
    }

    partial class ThisRibbonCollection
    {
        internal CustomRibbon CustomRibbon
        {
            get { return this.GetRibbon<CustomRibbon>(); }
        }
    }
}
