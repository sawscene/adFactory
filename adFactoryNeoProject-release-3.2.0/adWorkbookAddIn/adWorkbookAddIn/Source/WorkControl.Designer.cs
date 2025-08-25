
namespace adWorkbookAddIn.Source
{
    partial class WorkControl
    {
        /// <summary> 
        /// 必要なデザイナー変数です。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

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
            this.workNameLabel = new System.Windows.Forms.Label();
            this.fontColor = new System.Windows.Forms.Panel();
            this.backgroundColor = new System.Windows.Forms.Panel();
            this.fontColorButton = new System.Windows.Forms.Button();
            this.backgroundColorButton = new System.Windows.Forms.Button();
            this.optionButton = new System.Windows.Forms.Button();
            this.optionLabel = new System.Windows.Forms.Label();
            this.displayItemsButton = new System.Windows.Forms.Button();
            this.workNoLabel = new System.Windows.Forms.Label();
            this.displayItemsLabel = new System.Windows.Forms.Label();
            this.fontColorLabel = new System.Windows.Forms.Label();
            this.backgroundColorLabel = new System.Windows.Forms.Label();
            this.organizationsButton = new System.Windows.Forms.Button();
            this.organizations = new System.Windows.Forms.Label();
            this.organizationsLabel = new System.Windows.Forms.Label();
            this.equipmentsButton = new System.Windows.Forms.Button();
            this.equipments = new System.Windows.Forms.Label();
            this.equipmentsLabel = new System.Windows.Forms.Label();
            this.taktTimeLabel = new System.Windows.Forms.Label();
            this.revLabel = new System.Windows.Forms.Label();
            this.backgroundColorDialog = new System.Windows.Forms.ColorDialog();
            this.fontColorDialog = new System.Windows.Forms.ColorDialog();
            this.flowLayoutPanel = new System.Windows.Forms.FlowLayoutPanel();
            this.baseInfoLabel = new System.Windows.Forms.Label();
            this.workNamePanel = new System.Windows.Forms.Panel();
            this.workName = new System.Windows.Forms.Label();
            this.revPanel = new System.Windows.Forms.Panel();
            this.rev = new System.Windows.Forms.Label();
            this.workNoPanel = new System.Windows.Forms.Panel();
            this.workNo = new System.Windows.Forms.Label();
            this.taktTimePanel = new System.Windows.Forms.Panel();
            this.taktTime = new System.Windows.Forms.Label();
            this.organizationInfoLabel = new System.Windows.Forms.Label();
            this.equipmentsPanel = new System.Windows.Forms.Panel();
            this.organizationsPanel = new System.Windows.Forms.Panel();
            this.displayInfoLabel = new System.Windows.Forms.Label();
            this.backgroundColorPanel = new System.Windows.Forms.Panel();
            this.fontColorPanel = new System.Windows.Forms.Panel();
            this.displayItemPanel = new System.Windows.Forms.Panel();
            this.additionalInfoLabel = new System.Windows.Forms.Label();
            this.optionPanel = new System.Windows.Forms.Panel();
            this.flowLayoutPanel.SuspendLayout();
            this.workNamePanel.SuspendLayout();
            this.revPanel.SuspendLayout();
            this.workNoPanel.SuspendLayout();
            this.taktTimePanel.SuspendLayout();
            this.equipmentsPanel.SuspendLayout();
            this.organizationsPanel.SuspendLayout();
            this.backgroundColorPanel.SuspendLayout();
            this.fontColorPanel.SuspendLayout();
            this.displayItemPanel.SuspendLayout();
            this.optionPanel.SuspendLayout();
            this.SuspendLayout();
            // 
            // workNameLabel
            // 
            this.workNameLabel.AutoSize = true;
            this.workNameLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workNameLabel.Location = new System.Drawing.Point(3, 3);
            this.workNameLabel.MinimumSize = new System.Drawing.Size(75, 0);
            this.workNameLabel.Name = "workNameLabel";
            this.workNameLabel.Size = new System.Drawing.Size(75, 15);
            this.workNameLabel.TabIndex = 0;
            this.workNameLabel.Text = "工程名：";
            // 
            // fontColor
            // 
            this.fontColor.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.fontColor.BackColor = System.Drawing.SystemColors.Control;
            this.fontColor.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.fontColor.Cursor = System.Windows.Forms.Cursors.Default;
            this.fontColor.Location = new System.Drawing.Point(196, 3);
            this.fontColor.Name = "fontColor";
            this.fontColor.Size = new System.Drawing.Size(18, 18);
            this.fontColor.TabIndex = 14;
            // 
            // backgroundColor
            // 
            this.backgroundColor.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.backgroundColor.BackColor = System.Drawing.SystemColors.Control;
            this.backgroundColor.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.backgroundColor.Cursor = System.Windows.Forms.Cursors.Default;
            this.backgroundColor.Location = new System.Drawing.Point(196, 3);
            this.backgroundColor.Name = "backgroundColor";
            this.backgroundColor.Size = new System.Drawing.Size(18, 18);
            this.backgroundColor.TabIndex = 13;
            // 
            // fontColorButton
            // 
            this.fontColorButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.fontColorButton.BackColor = System.Drawing.SystemColors.ControlDark;
            this.fontColorButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.fontColorButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.fontColorButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.fontColorButton.Location = new System.Drawing.Point(225, -1);
            this.fontColorButton.MinimumSize = new System.Drawing.Size(0, 25);
            this.fontColorButton.Name = "fontColorButton";
            this.fontColorButton.Size = new System.Drawing.Size(75, 25);
            this.fontColorButton.TabIndex = 12;
            this.fontColorButton.Text = "設定";
            this.fontColorButton.UseVisualStyleBackColor = false;
            this.fontColorButton.Click += new System.EventHandler(this.FontColorButton_Click);
            // 
            // backgroundColorButton
            // 
            this.backgroundColorButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.backgroundColorButton.BackColor = System.Drawing.SystemColors.ControlDark;
            this.backgroundColorButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.backgroundColorButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.backgroundColorButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.backgroundColorButton.Location = new System.Drawing.Point(225, -1);
            this.backgroundColorButton.MinimumSize = new System.Drawing.Size(0, 25);
            this.backgroundColorButton.Name = "backgroundColorButton";
            this.backgroundColorButton.Size = new System.Drawing.Size(75, 25);
            this.backgroundColorButton.TabIndex = 12;
            this.backgroundColorButton.Text = "設定";
            this.backgroundColorButton.UseVisualStyleBackColor = false;
            this.backgroundColorButton.Click += new System.EventHandler(this.BackgroundColorButton_Click);
            // 
            // optionButton
            // 
            this.optionButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.optionButton.BackColor = System.Drawing.SystemColors.ControlDark;
            this.optionButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.optionButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.optionButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.optionButton.Location = new System.Drawing.Point(225, -1);
            this.optionButton.MinimumSize = new System.Drawing.Size(0, 25);
            this.optionButton.Name = "optionButton";
            this.optionButton.Size = new System.Drawing.Size(75, 25);
            this.optionButton.TabIndex = 11;
            this.optionButton.Text = "設定";
            this.optionButton.UseVisualStyleBackColor = false;
            this.optionButton.Click += new System.EventHandler(this.OptionButton_Click);
            // 
            // optionLabel
            // 
            this.optionLabel.AutoSize = true;
            this.optionLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.optionLabel.Location = new System.Drawing.Point(3, 3);
            this.optionLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.optionLabel.Name = "optionLabel";
            this.optionLabel.Size = new System.Drawing.Size(100, 15);
            this.optionLabel.TabIndex = 0;
            this.optionLabel.Text = "追加情報";
            // 
            // displayItemsButton
            // 
            this.displayItemsButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.displayItemsButton.BackColor = System.Drawing.SystemColors.ControlDark;
            this.displayItemsButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.displayItemsButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.displayItemsButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.displayItemsButton.Location = new System.Drawing.Point(225, -1);
            this.displayItemsButton.MinimumSize = new System.Drawing.Size(0, 25);
            this.displayItemsButton.Name = "displayItemsButton";
            this.displayItemsButton.Size = new System.Drawing.Size(75, 25);
            this.displayItemsButton.TabIndex = 10;
            this.displayItemsButton.Text = "設定";
            this.displayItemsButton.UseVisualStyleBackColor = false;
            this.displayItemsButton.Click += new System.EventHandler(this.DisplayItemsButton_Click);
            // 
            // workNoLabel
            // 
            this.workNoLabel.AutoSize = true;
            this.workNoLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workNoLabel.Location = new System.Drawing.Point(3, 3);
            this.workNoLabel.MinimumSize = new System.Drawing.Size(75, 0);
            this.workNoLabel.Name = "workNoLabel";
            this.workNoLabel.Size = new System.Drawing.Size(75, 15);
            this.workNoLabel.TabIndex = 0;
            this.workNoLabel.Text = "作業番号：";
            // 
            // displayItemsLabel
            // 
            this.displayItemsLabel.AutoSize = true;
            this.displayItemsLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.displayItemsLabel.Location = new System.Drawing.Point(3, 3);
            this.displayItemsLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.displayItemsLabel.Name = "displayItemsLabel";
            this.displayItemsLabel.Size = new System.Drawing.Size(100, 15);
            this.displayItemsLabel.TabIndex = 0;
            this.displayItemsLabel.Text = "表示項目";
            // 
            // fontColorLabel
            // 
            this.fontColorLabel.AutoSize = true;
            this.fontColorLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.fontColorLabel.Location = new System.Drawing.Point(3, 3);
            this.fontColorLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.fontColorLabel.Name = "fontColorLabel";
            this.fontColorLabel.Size = new System.Drawing.Size(100, 15);
            this.fontColorLabel.TabIndex = 0;
            this.fontColorLabel.Text = "文字色";
            // 
            // backgroundColorLabel
            // 
            this.backgroundColorLabel.AutoSize = true;
            this.backgroundColorLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.backgroundColorLabel.Location = new System.Drawing.Point(3, 3);
            this.backgroundColorLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.backgroundColorLabel.Name = "backgroundColorLabel";
            this.backgroundColorLabel.Size = new System.Drawing.Size(100, 15);
            this.backgroundColorLabel.TabIndex = 0;
            this.backgroundColorLabel.Text = "背景色";
            // 
            // organizationsButton
            // 
            this.organizationsButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.organizationsButton.BackColor = System.Drawing.SystemColors.ControlDark;
            this.organizationsButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.organizationsButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.organizationsButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.organizationsButton.Location = new System.Drawing.Point(225, -1);
            this.organizationsButton.MinimumSize = new System.Drawing.Size(0, 25);
            this.organizationsButton.Name = "organizationsButton";
            this.organizationsButton.Size = new System.Drawing.Size(75, 25);
            this.organizationsButton.TabIndex = 7;
            this.organizationsButton.Text = "設定";
            this.organizationsButton.UseVisualStyleBackColor = false;
            this.organizationsButton.Click += new System.EventHandler(this.OrganizationsButton_Click);
            // 
            // organizations
            // 
            this.organizations.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.organizations.AutoEllipsis = true;
            this.organizations.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.organizations.Location = new System.Drawing.Point(24, 25);
            this.organizations.Margin = new System.Windows.Forms.Padding(3, 0, 3, 10);
            this.organizations.MinimumSize = new System.Drawing.Size(270, 0);
            this.organizations.Name = "organizations";
            this.organizations.Size = new System.Drawing.Size(270, 20);
            this.organizations.TabIndex = 0;
            this.organizations.Text = "（作業者は設定されていません）";
            // 
            // organizationsLabel
            // 
            this.organizationsLabel.AutoSize = true;
            this.organizationsLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.organizationsLabel.Location = new System.Drawing.Point(3, 3);
            this.organizationsLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.organizationsLabel.Name = "organizationsLabel";
            this.organizationsLabel.Size = new System.Drawing.Size(100, 15);
            this.organizationsLabel.TabIndex = 0;
            this.organizationsLabel.Text = "作業者";
            // 
            // equipmentsButton
            // 
            this.equipmentsButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.equipmentsButton.BackColor = System.Drawing.SystemColors.ControlDark;
            this.equipmentsButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.equipmentsButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.equipmentsButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.equipmentsButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.equipmentsButton.Location = new System.Drawing.Point(225, -1);
            this.equipmentsButton.MinimumSize = new System.Drawing.Size(0, 25);
            this.equipmentsButton.Name = "equipmentsButton";
            this.equipmentsButton.Size = new System.Drawing.Size(75, 25);
            this.equipmentsButton.TabIndex = 6;
            this.equipmentsButton.Text = "設定";
            this.equipmentsButton.UseVisualStyleBackColor = false;
            this.equipmentsButton.Click += new System.EventHandler(this.EquipmentsButton_Click);
            // 
            // equipments
            // 
            this.equipments.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.equipments.AutoEllipsis = true;
            this.equipments.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.equipments.Location = new System.Drawing.Point(24, 25);
            this.equipments.Margin = new System.Windows.Forms.Padding(3, 0, 3, 10);
            this.equipments.MinimumSize = new System.Drawing.Size(270, 0);
            this.equipments.Name = "equipments";
            this.equipments.Size = new System.Drawing.Size(270, 20);
            this.equipments.TabIndex = 0;
            this.equipments.Text = "（作業者端末は設定されていません）";
            // 
            // equipmentsLabel
            // 
            this.equipmentsLabel.AutoSize = true;
            this.equipmentsLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.equipmentsLabel.Location = new System.Drawing.Point(3, 3);
            this.equipmentsLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.equipmentsLabel.Name = "equipmentsLabel";
            this.equipmentsLabel.Size = new System.Drawing.Size(100, 15);
            this.equipmentsLabel.TabIndex = 0;
            this.equipmentsLabel.Text = "作業者端末";
            // 
            // taktTimeLabel
            // 
            this.taktTimeLabel.AutoSize = true;
            this.taktTimeLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.taktTimeLabel.Location = new System.Drawing.Point(3, 3);
            this.taktTimeLabel.MinimumSize = new System.Drawing.Size(75, 0);
            this.taktTimeLabel.Name = "taktTimeLabel";
            this.taktTimeLabel.Size = new System.Drawing.Size(75, 15);
            this.taktTimeLabel.TabIndex = 0;
            this.taktTimeLabel.Text = "タクトタイム：";
            // 
            // revLabel
            // 
            this.revLabel.AutoSize = true;
            this.revLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.revLabel.Location = new System.Drawing.Point(3, 3);
            this.revLabel.MinimumSize = new System.Drawing.Size(75, 0);
            this.revLabel.Name = "revLabel";
            this.revLabel.Size = new System.Drawing.Size(75, 15);
            this.revLabel.TabIndex = 0;
            this.revLabel.Text = "Rev：";
            // 
            // backgroundColorDialog
            // 
            this.backgroundColorDialog.FullOpen = true;
            // 
            // fontColorDialog
            // 
            this.fontColorDialog.FullOpen = true;
            // 
            // flowLayoutPanel
            // 
            this.flowLayoutPanel.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.flowLayoutPanel.BackColor = System.Drawing.Color.Transparent;
            this.flowLayoutPanel.Controls.Add(this.baseInfoLabel);
            this.flowLayoutPanel.Controls.Add(this.workNamePanel);
            this.flowLayoutPanel.Controls.Add(this.revPanel);
            this.flowLayoutPanel.Controls.Add(this.workNoPanel);
            this.flowLayoutPanel.Controls.Add(this.taktTimePanel);
            this.flowLayoutPanel.Controls.Add(this.organizationInfoLabel);
            this.flowLayoutPanel.Controls.Add(this.equipmentsPanel);
            this.flowLayoutPanel.Controls.Add(this.organizationsPanel);
            this.flowLayoutPanel.Controls.Add(this.displayInfoLabel);
            this.flowLayoutPanel.Controls.Add(this.backgroundColorPanel);
            this.flowLayoutPanel.Controls.Add(this.fontColorPanel);
            this.flowLayoutPanel.Controls.Add(this.displayItemPanel);
            this.flowLayoutPanel.Controls.Add(this.additionalInfoLabel);
            this.flowLayoutPanel.Controls.Add(this.optionPanel);
            this.flowLayoutPanel.FlowDirection = System.Windows.Forms.FlowDirection.TopDown;
            this.flowLayoutPanel.Location = new System.Drawing.Point(0, 0);
            this.flowLayoutPanel.Margin = new System.Windows.Forms.Padding(0, 0, 30, 0);
            this.flowLayoutPanel.Name = "flowLayoutPanel";
            this.flowLayoutPanel.Padding = new System.Windows.Forms.Padding(0, 0, 10, 0);
            this.flowLayoutPanel.Size = new System.Drawing.Size(336, 500);
            this.flowLayoutPanel.TabIndex = 2;
            this.flowLayoutPanel.WrapContents = false;
            // 
            // baseInfoLabel
            // 
            this.baseInfoLabel.AutoSize = true;
            this.baseInfoLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.baseInfoLabel.Location = new System.Drawing.Point(10, 18);
            this.baseInfoLabel.Margin = new System.Windows.Forms.Padding(10, 18, 3, 3);
            this.baseInfoLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.baseInfoLabel.Name = "baseInfoLabel";
            this.baseInfoLabel.Size = new System.Drawing.Size(100, 15);
            this.baseInfoLabel.TabIndex = 3;
            this.baseInfoLabel.Text = "基本情報";
            // 
            // workNamePanel
            // 
            this.workNamePanel.Controls.Add(this.workName);
            this.workNamePanel.Controls.Add(this.workNameLabel);
            this.workNamePanel.Location = new System.Drawing.Point(20, 39);
            this.workNamePanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.workNamePanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.workNamePanel.Name = "workNamePanel";
            this.workNamePanel.Size = new System.Drawing.Size(300, 25);
            this.workNamePanel.TabIndex = 3;
            // 
            // workName
            // 
            this.workName.AutoSize = true;
            this.workName.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workName.Location = new System.Drawing.Point(84, 4);
            this.workName.MinimumSize = new System.Drawing.Size(210, 0);
            this.workName.Name = "workName";
            this.workName.Size = new System.Drawing.Size(210, 15);
            this.workName.TabIndex = 1;
            this.workName.Text = "工程名";
            // 
            // revPanel
            // 
            this.revPanel.Controls.Add(this.rev);
            this.revPanel.Controls.Add(this.revLabel);
            this.revPanel.Location = new System.Drawing.Point(20, 70);
            this.revPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.revPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.revPanel.Name = "revPanel";
            this.revPanel.Size = new System.Drawing.Size(300, 25);
            this.revPanel.TabIndex = 3;
            // 
            // rev
            // 
            this.rev.AutoSize = true;
            this.rev.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.rev.Location = new System.Drawing.Point(84, 4);
            this.rev.MinimumSize = new System.Drawing.Size(210, 0);
            this.rev.Name = "rev";
            this.rev.Size = new System.Drawing.Size(210, 15);
            this.rev.TabIndex = 1;
            this.rev.Text = "Rev";
            // 
            // workNoPanel
            // 
            this.workNoPanel.Controls.Add(this.workNo);
            this.workNoPanel.Controls.Add(this.workNoLabel);
            this.workNoPanel.Location = new System.Drawing.Point(20, 101);
            this.workNoPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.workNoPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.workNoPanel.Name = "workNoPanel";
            this.workNoPanel.Size = new System.Drawing.Size(300, 25);
            this.workNoPanel.TabIndex = 3;
            // 
            // workNo
            // 
            this.workNo.AutoSize = true;
            this.workNo.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workNo.Location = new System.Drawing.Point(84, 4);
            this.workNo.MinimumSize = new System.Drawing.Size(210, 0);
            this.workNo.Name = "workNo";
            this.workNo.Size = new System.Drawing.Size(210, 15);
            this.workNo.TabIndex = 1;
            this.workNo.Text = "作業番号";
            // 
            // taktTimePanel
            // 
            this.taktTimePanel.Controls.Add(this.taktTime);
            this.taktTimePanel.Controls.Add(this.taktTimeLabel);
            this.taktTimePanel.Location = new System.Drawing.Point(20, 132);
            this.taktTimePanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.taktTimePanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.taktTimePanel.Name = "taktTimePanel";
            this.taktTimePanel.Size = new System.Drawing.Size(300, 25);
            this.taktTimePanel.TabIndex = 3;
            // 
            // taktTime
            // 
            this.taktTime.AutoSize = true;
            this.taktTime.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.taktTime.Location = new System.Drawing.Point(84, 4);
            this.taktTime.MinimumSize = new System.Drawing.Size(210, 0);
            this.taktTime.Name = "taktTime";
            this.taktTime.Size = new System.Drawing.Size(210, 15);
            this.taktTime.TabIndex = 1;
            this.taktTime.Text = "タクトタイム";
            // 
            // organizationInfoLabel
            // 
            this.organizationInfoLabel.AutoSize = true;
            this.organizationInfoLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.organizationInfoLabel.Location = new System.Drawing.Point(10, 170);
            this.organizationInfoLabel.Margin = new System.Windows.Forms.Padding(10, 10, 3, 3);
            this.organizationInfoLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.organizationInfoLabel.Name = "organizationInfoLabel";
            this.organizationInfoLabel.Size = new System.Drawing.Size(100, 15);
            this.organizationInfoLabel.TabIndex = 3;
            this.organizationInfoLabel.Text = "作業者情報";
            // 
            // equipmentsPanel
            // 
            this.equipmentsPanel.Controls.Add(this.equipmentsLabel);
            this.equipmentsPanel.Controls.Add(this.equipmentsButton);
            this.equipmentsPanel.Controls.Add(this.equipments);
            this.equipmentsPanel.Location = new System.Drawing.Point(20, 191);
            this.equipmentsPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.equipmentsPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.equipmentsPanel.Name = "equipmentsPanel";
            this.equipmentsPanel.Size = new System.Drawing.Size(300, 50);
            this.equipmentsPanel.TabIndex = 3;
            // 
            // organizationsPanel
            // 
            this.organizationsPanel.Controls.Add(this.organizationsLabel);
            this.organizationsPanel.Controls.Add(this.organizationsButton);
            this.organizationsPanel.Controls.Add(this.organizations);
            this.organizationsPanel.Location = new System.Drawing.Point(20, 247);
            this.organizationsPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.organizationsPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.organizationsPanel.Name = "organizationsPanel";
            this.organizationsPanel.Size = new System.Drawing.Size(300, 50);
            this.organizationsPanel.TabIndex = 3;
            // 
            // displayInfoLabel
            // 
            this.displayInfoLabel.AutoSize = true;
            this.displayInfoLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.displayInfoLabel.Location = new System.Drawing.Point(10, 310);
            this.displayInfoLabel.Margin = new System.Windows.Forms.Padding(10, 10, 3, 3);
            this.displayInfoLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.displayInfoLabel.Name = "displayInfoLabel";
            this.displayInfoLabel.Size = new System.Drawing.Size(100, 15);
            this.displayInfoLabel.TabIndex = 3;
            this.displayInfoLabel.Text = "表示情報";
            // 
            // backgroundColorPanel
            // 
            this.backgroundColorPanel.Controls.Add(this.backgroundColorLabel);
            this.backgroundColorPanel.Controls.Add(this.backgroundColorButton);
            this.backgroundColorPanel.Controls.Add(this.backgroundColor);
            this.backgroundColorPanel.Location = new System.Drawing.Point(20, 331);
            this.backgroundColorPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.backgroundColorPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.backgroundColorPanel.Name = "backgroundColorPanel";
            this.backgroundColorPanel.Size = new System.Drawing.Size(300, 25);
            this.backgroundColorPanel.TabIndex = 3;
            // 
            // fontColorPanel
            // 
            this.fontColorPanel.Controls.Add(this.fontColor);
            this.fontColorPanel.Controls.Add(this.fontColorLabel);
            this.fontColorPanel.Controls.Add(this.fontColorButton);
            this.fontColorPanel.Location = new System.Drawing.Point(20, 362);
            this.fontColorPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.fontColorPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.fontColorPanel.Name = "fontColorPanel";
            this.fontColorPanel.Size = new System.Drawing.Size(300, 25);
            this.fontColorPanel.TabIndex = 3;
            // 
            // displayItemPanel
            // 
            this.displayItemPanel.Controls.Add(this.displayItemsLabel);
            this.displayItemPanel.Controls.Add(this.displayItemsButton);
            this.displayItemPanel.Location = new System.Drawing.Point(20, 393);
            this.displayItemPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 3);
            this.displayItemPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.displayItemPanel.Name = "displayItemPanel";
            this.displayItemPanel.Size = new System.Drawing.Size(300, 25);
            this.displayItemPanel.TabIndex = 3;
            // 
            // additionalInfoLabel
            // 
            this.additionalInfoLabel.AutoSize = true;
            this.additionalInfoLabel.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.additionalInfoLabel.Location = new System.Drawing.Point(10, 431);
            this.additionalInfoLabel.Margin = new System.Windows.Forms.Padding(10, 10, 3, 3);
            this.additionalInfoLabel.MinimumSize = new System.Drawing.Size(100, 0);
            this.additionalInfoLabel.Name = "additionalInfoLabel";
            this.additionalInfoLabel.Size = new System.Drawing.Size(100, 15);
            this.additionalInfoLabel.TabIndex = 3;
            this.additionalInfoLabel.Text = "追加情報";
            // 
            // optionPanel
            // 
            this.optionPanel.Controls.Add(this.optionButton);
            this.optionPanel.Controls.Add(this.optionLabel);
            this.optionPanel.Location = new System.Drawing.Point(20, 452);
            this.optionPanel.Margin = new System.Windows.Forms.Padding(20, 3, 10, 25);
            this.optionPanel.MinimumSize = new System.Drawing.Size(300, 0);
            this.optionPanel.Name = "optionPanel";
            this.optionPanel.Size = new System.Drawing.Size(300, 25);
            this.optionPanel.TabIndex = 3;
            // 
            // WorkControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.AutoSize = true;
            this.Controls.Add(this.flowLayoutPanel);
            this.Name = "WorkControl";
            this.Size = new System.Drawing.Size(360, 500);
            this.Resize += new System.EventHandler(this.WorkControl_Resize);
            this.flowLayoutPanel.ResumeLayout(false);
            this.flowLayoutPanel.PerformLayout();
            this.workNamePanel.ResumeLayout(false);
            this.workNamePanel.PerformLayout();
            this.revPanel.ResumeLayout(false);
            this.revPanel.PerformLayout();
            this.workNoPanel.ResumeLayout(false);
            this.workNoPanel.PerformLayout();
            this.taktTimePanel.ResumeLayout(false);
            this.taktTimePanel.PerformLayout();
            this.equipmentsPanel.ResumeLayout(false);
            this.equipmentsPanel.PerformLayout();
            this.organizationsPanel.ResumeLayout(false);
            this.organizationsPanel.PerformLayout();
            this.backgroundColorPanel.ResumeLayout(false);
            this.backgroundColorPanel.PerformLayout();
            this.fontColorPanel.ResumeLayout(false);
            this.fontColorPanel.PerformLayout();
            this.displayItemPanel.ResumeLayout(false);
            this.displayItemPanel.PerformLayout();
            this.optionPanel.ResumeLayout(false);
            this.optionPanel.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion
        private System.Windows.Forms.Label workNameLabel;
        private System.Windows.Forms.Label revLabel;
        private System.Windows.Forms.Label taktTimeLabel;
        private System.Windows.Forms.Button equipmentsButton;
        private System.Windows.Forms.Label equipments;
        private System.Windows.Forms.Label equipmentsLabel;
        private System.Windows.Forms.Label organizations;
        private System.Windows.Forms.Label organizationsLabel;
        private System.Windows.Forms.Button organizationsButton;
        private System.Windows.Forms.Label displayItemsLabel;
        private System.Windows.Forms.Label fontColorLabel;
        private System.Windows.Forms.Label backgroundColorLabel;
        private System.Windows.Forms.Button displayItemsButton;
        private System.Windows.Forms.Label workNoLabel;
        private System.Windows.Forms.Button optionButton;
        private System.Windows.Forms.Label optionLabel;
        private System.Windows.Forms.Button backgroundColorButton;
        private System.Windows.Forms.ColorDialog backgroundColorDialog;
        private System.Windows.Forms.Button fontColorButton;
        private System.Windows.Forms.ColorDialog fontColorDialog;
        private System.Windows.Forms.Panel backgroundColor;
        private System.Windows.Forms.Panel fontColor;
        private System.Windows.Forms.FlowLayoutPanel flowLayoutPanel;
        private System.Windows.Forms.Label baseInfoLabel;
        private System.Windows.Forms.Panel workNamePanel;
        private System.Windows.Forms.Panel revPanel;
        private System.Windows.Forms.Panel workNoPanel;
        private System.Windows.Forms.Panel taktTimePanel;
        private System.Windows.Forms.Label organizationInfoLabel;
        private System.Windows.Forms.Panel equipmentsPanel;
        private System.Windows.Forms.Panel organizationsPanel;
        private System.Windows.Forms.Label displayInfoLabel;
        private System.Windows.Forms.Panel backgroundColorPanel;
        private System.Windows.Forms.Panel fontColorPanel;
        private System.Windows.Forms.Panel displayItemPanel;
        private System.Windows.Forms.Label additionalInfoLabel;
        private System.Windows.Forms.Panel optionPanel;
        private System.Windows.Forms.Label workName;
        private System.Windows.Forms.Label rev;
        private System.Windows.Forms.Label workNo;
        private System.Windows.Forms.Label taktTime;
    }
}
