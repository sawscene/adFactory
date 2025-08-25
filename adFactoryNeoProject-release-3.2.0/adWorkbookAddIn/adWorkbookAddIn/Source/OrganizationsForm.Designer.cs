
namespace adWorkbookAddIn.Source
{
    partial class OrganizationsForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.treeView = new System.Windows.Forms.TreeView();
            this.okButton = new System.Windows.Forms.Button();
            this.cancelButton = new System.Windows.Forms.Button();
            this.addButton = new System.Windows.Forms.Button();
            this.deleteButton = new System.Windows.Forms.Button();
            this.mainPanel = new System.Windows.Forms.FlowLayoutPanel();
            this.listBox = new System.Windows.Forms.ListView();
            this.subPanel1 = new System.Windows.Forms.FlowLayoutPanel();
            this.subPanel2 = new System.Windows.Forms.FlowLayoutPanel();
            this.mainPanel.SuspendLayout();
            this.subPanel1.SuspendLayout();
            this.subPanel2.SuspendLayout();
            this.SuspendLayout();
            // 
            // treeView
            // 
            this.treeView.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
            this.treeView.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.treeView.Location = new System.Drawing.Point(21, 20);
            this.treeView.Margin = new System.Windows.Forms.Padding(20, 20, 10, 20);
            this.treeView.Name = "treeView";
            this.treeView.Size = new System.Drawing.Size(179, 244);
            this.treeView.TabIndex = 1;
            // 
            // okButton
            // 
            this.okButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.okButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.okButton.Location = new System.Drawing.Point(3, 3);
            this.okButton.MinimumSize = new System.Drawing.Size(0, 26);
            this.okButton.Name = "okButton";
            this.okButton.Size = new System.Drawing.Size(75, 26);
            this.okButton.TabIndex = 5;
            this.okButton.Text = "OK";
            this.okButton.UseVisualStyleBackColor = true;
            this.okButton.Click += new System.EventHandler(this.OkButton_Click);
            // 
            // cancelButton
            // 
            this.cancelButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.cancelButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.cancelButton.Location = new System.Drawing.Point(84, 3);
            this.cancelButton.MinimumSize = new System.Drawing.Size(0, 26);
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.Size = new System.Drawing.Size(75, 26);
            this.cancelButton.TabIndex = 6;
            this.cancelButton.Text = "キャンセル";
            this.cancelButton.UseVisualStyleBackColor = true;
            this.cancelButton.Click += new System.EventHandler(this.CancelButton_Click);
            // 
            // addButton
            // 
            this.addButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.addButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.addButton.Location = new System.Drawing.Point(3, 3);
            this.addButton.MaximumSize = new System.Drawing.Size(75, 26);
            this.addButton.MinimumSize = new System.Drawing.Size(75, 26);
            this.addButton.Name = "addButton";
            this.addButton.Size = new System.Drawing.Size(75, 26);
            this.addButton.TabIndex = 2;
            this.addButton.Text = "追加";
            this.addButton.UseVisualStyleBackColor = true;
            this.addButton.Click += new System.EventHandler(this.AddButton_Click);
            // 
            // deleteButton
            // 
            this.deleteButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.deleteButton.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.deleteButton.Location = new System.Drawing.Point(3, 35);
            this.deleteButton.MaximumSize = new System.Drawing.Size(75, 26);
            this.deleteButton.MinimumSize = new System.Drawing.Size(75, 26);
            this.deleteButton.Name = "deleteButton";
            this.deleteButton.Size = new System.Drawing.Size(75, 26);
            this.deleteButton.TabIndex = 3;
            this.deleteButton.Text = "削除";
            this.deleteButton.UseVisualStyleBackColor = true;
            this.deleteButton.Click += new System.EventHandler(this.DeleteButton_Click);
            // 
            // mainPanel
            // 
            this.mainPanel.Controls.Add(this.listBox);
            this.mainPanel.Controls.Add(this.subPanel1);
            this.mainPanel.Controls.Add(this.treeView);
            this.mainPanel.Controls.Add(this.subPanel2);
            this.mainPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.mainPanel.FlowDirection = System.Windows.Forms.FlowDirection.RightToLeft;
            this.mainPanel.Location = new System.Drawing.Point(0, 0);
            this.mainPanel.Name = "mainPanel";
            this.mainPanel.Size = new System.Drawing.Size(500, 328);
            this.mainPanel.TabIndex = 0;
            // 
            // listBox
            // 
            this.listBox.Font = new System.Drawing.Font("Meiryo UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.listBox.HideSelection = false;
            this.listBox.Location = new System.Drawing.Point(301, 20);
            this.listBox.Margin = new System.Windows.Forms.Padding(10, 20, 20, 20);
            this.listBox.Name = "listBox";
            this.listBox.Size = new System.Drawing.Size(179, 244);
            this.listBox.TabIndex = 4;
            this.listBox.UseCompatibleStateImageBehavior = false;
            this.listBox.View = System.Windows.Forms.View.List;
            // 
            // subPanel1
            // 
            this.subPanel1.AutoSize = true;
            this.subPanel1.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.subPanel1.Controls.Add(this.addButton);
            this.subPanel1.Controls.Add(this.deleteButton);
            this.subPanel1.FlowDirection = System.Windows.Forms.FlowDirection.TopDown;
            this.subPanel1.Location = new System.Drawing.Point(210, 113);
            this.subPanel1.Margin = new System.Windows.Forms.Padding(0, 113, 0, 0);
            this.subPanel1.Name = "subPanel1";
            this.subPanel1.Size = new System.Drawing.Size(81, 64);
            this.subPanel1.TabIndex = 0;
            // 
            // subPanel2
            // 
            this.subPanel2.AutoSize = true;
            this.subPanel2.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.subPanel2.Controls.Add(this.okButton);
            this.subPanel2.Controls.Add(this.cancelButton);
            this.subPanel2.Location = new System.Drawing.Point(318, 284);
            this.subPanel2.Margin = new System.Windows.Forms.Padding(0, 0, 20, 15);
            this.subPanel2.Name = "subPanel2";
            this.subPanel2.Size = new System.Drawing.Size(162, 32);
            this.subPanel2.TabIndex = 0;
            // 
            // OrganizationsForm
            // 
            this.AcceptButton = this.okButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.cancelButton;
            this.ClientSize = new System.Drawing.Size(500, 328);
            this.Controls.Add(this.mainPanel);
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.MinimumSize = new System.Drawing.Size(516, 367);
            this.Name = "OrganizationsForm";
            this.ShowIcon = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "作業者";
            this.Load += new System.EventHandler(this.OrganizationsForm_Load);
            this.mainPanel.ResumeLayout(false);
            this.mainPanel.PerformLayout();
            this.subPanel1.ResumeLayout(false);
            this.subPanel2.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TreeView treeView;
        private System.Windows.Forms.Button okButton;
        private System.Windows.Forms.Button cancelButton;
        private System.Windows.Forms.Button addButton;
        private System.Windows.Forms.Button deleteButton;
        private System.Windows.Forms.FlowLayoutPanel mainPanel;
        private System.Windows.Forms.FlowLayoutPanel subPanel1;
        private System.Windows.Forms.FlowLayoutPanel subPanel2;
        private System.Windows.Forms.ListView listBox;
    }
}