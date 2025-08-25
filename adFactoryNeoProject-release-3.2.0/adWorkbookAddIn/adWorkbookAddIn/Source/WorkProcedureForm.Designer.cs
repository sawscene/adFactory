using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    partial class WorkProcedureForm
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
            this.workProcedureLabel = new System.Windows.Forms.Label();
            this.workProcedureComboBox = new System.Windows.Forms.ComboBox();
            this.workProcedureListBox = new System.Windows.Forms.ListBox();
            this.addWorkProcedureButton = new System.Windows.Forms.Button();
            this.deleteWorkProcedureButton = new System.Windows.Forms.Button();
            this.downWorkProcedureButton = new System.Windows.Forms.Button();
            this.upWorkProcedureButton = new System.Windows.Forms.Button();
            this.okButton = new System.Windows.Forms.Button();
            this.cancelButton = new System.Windows.Forms.Button();
            this.workLabel = new System.Windows.Forms.Label();
            this.workComboBox = new System.Windows.Forms.ComboBox();
            this.explanationLabel = new System.Windows.Forms.Label();
            this.sourceGroupBox = new System.Windows.Forms.GroupBox();
            this.currentWorkLabel = new System.Windows.Forms.Label();
            this.sourceGroupBox.SuspendLayout();
            this.SuspendLayout();
            // 
            // workProcedureLabel
            // 
            this.workProcedureLabel.AutoSize = true;
            this.workProcedureLabel.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workProcedureLabel.ForeColor = System.Drawing.SystemColors.ControlText;
            this.workProcedureLabel.Location = new System.Drawing.Point(10, 78);
            this.workProcedureLabel.Name = "workProcedureLabel";
            this.workProcedureLabel.Size = new System.Drawing.Size(59, 12);
            this.workProcedureLabel.TabIndex = 4;
            this.workProcedureLabel.Tag = "";
            this.workProcedureLabel.Text = "作業手順：";
            // 
            // workProcedureComboBox
            // 
            this.workProcedureComboBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.workProcedureComboBox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.workProcedureComboBox.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workProcedureComboBox.FormattingEnabled = true;
            this.workProcedureComboBox.Location = new System.Drawing.Point(10, 95);
            this.workProcedureComboBox.Name = "workProcedureComboBox";
            this.workProcedureComboBox.Size = new System.Drawing.Size(396, 20);
            this.workProcedureComboBox.TabIndex = 5;
            // 
            // workProcedureListBox
            // 
            this.workProcedureListBox.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.workProcedureListBox.FormattingEnabled = true;
            this.workProcedureListBox.ItemHeight = 12;
            this.workProcedureListBox.Location = new System.Drawing.Point(20, 238);
            this.workProcedureListBox.Name = "workProcedureListBox";
            this.workProcedureListBox.Size = new System.Drawing.Size(416, 196);
            this.workProcedureListBox.TabIndex = 11;
            this.workProcedureListBox.SelectedIndexChanged += new System.EventHandler(this.WorkProcedureListBox_SelectedIndexChanged);
            // 
            // addWorkProcedureButton
            // 
            this.addWorkProcedureButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.addWorkProcedureButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.addWorkProcedureButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.addWorkProcedureButton.Location = new System.Drawing.Point(285, 209);
            this.addWorkProcedureButton.Name = "addWorkProcedureButton";
            this.addWorkProcedureButton.Size = new System.Drawing.Size(50, 23);
            this.addWorkProcedureButton.TabIndex = 7;
            this.addWorkProcedureButton.Tag = "";
            this.addWorkProcedureButton.Text = "作成";
            this.addWorkProcedureButton.UseVisualStyleBackColor = true;
            this.addWorkProcedureButton.Click += new System.EventHandler(this.AddWorkProcedureButton_Click);
            // 
            // deleteWorkProcedureButton
            // 
            this.deleteWorkProcedureButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.deleteWorkProcedureButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.deleteWorkProcedureButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.deleteWorkProcedureButton.Location = new System.Drawing.Point(337, 209);
            this.deleteWorkProcedureButton.Name = "deleteWorkProcedureButton";
            this.deleteWorkProcedureButton.Size = new System.Drawing.Size(50, 23);
            this.deleteWorkProcedureButton.TabIndex = 8;
            this.deleteWorkProcedureButton.Tag = "";
            this.deleteWorkProcedureButton.Text = "削除";
            this.deleteWorkProcedureButton.UseVisualStyleBackColor = true;
            this.deleteWorkProcedureButton.Click += new System.EventHandler(this.DeleteWorkProcedureButton_Click);
            // 
            // downWorkProcedureButton
            // 
            this.downWorkProcedureButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.downWorkProcedureButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.downWorkProcedureButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.downWorkProcedureButton.Location = new System.Drawing.Point(389, 209);
            this.downWorkProcedureButton.Margin = new System.Windows.Forms.Padding(0);
            this.downWorkProcedureButton.Name = "downWorkProcedureButton";
            this.downWorkProcedureButton.Size = new System.Drawing.Size(23, 23);
            this.downWorkProcedureButton.TabIndex = 9;
            this.downWorkProcedureButton.Tag = "";
            this.downWorkProcedureButton.Text = "↓";
            this.downWorkProcedureButton.UseVisualStyleBackColor = true;
            this.downWorkProcedureButton.Click += new System.EventHandler(this.DownWorkProcedureButton_Click);
            // 
            // upWorkProcedureButton
            // 
            this.upWorkProcedureButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.upWorkProcedureButton.BackColor = System.Drawing.SystemColors.Control;
            this.upWorkProcedureButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.upWorkProcedureButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.upWorkProcedureButton.Location = new System.Drawing.Point(414, 209);
            this.upWorkProcedureButton.Margin = new System.Windows.Forms.Padding(0);
            this.upWorkProcedureButton.Name = "upWorkProcedureButton";
            this.upWorkProcedureButton.Size = new System.Drawing.Size(23, 23);
            this.upWorkProcedureButton.TabIndex = 10;
            this.upWorkProcedureButton.Tag = "";
            this.upWorkProcedureButton.Text = "↑";
            this.upWorkProcedureButton.UseVisualStyleBackColor = false;
            this.upWorkProcedureButton.Click += new System.EventHandler(this.UpWorkProcedureButton_Click);
            // 
            // okButton
            // 
            this.okButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.okButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.okButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.okButton.Location = new System.Drawing.Point(281, 451);
            this.okButton.Name = "okButton";
            this.okButton.Size = new System.Drawing.Size(75, 23);
            this.okButton.TabIndex = 12;
            this.okButton.Tag = "";
            this.okButton.Text = "OK";
            this.okButton.UseVisualStyleBackColor = true;
            this.okButton.Click += new System.EventHandler(this.OkButton_Click);
            // 
            // cancelButton
            // 
            this.cancelButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.cancelButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.cancelButton.ForeColor = System.Drawing.SystemColors.ControlText;
            this.cancelButton.Location = new System.Drawing.Point(362, 451);
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.Size = new System.Drawing.Size(75, 23);
            this.cancelButton.TabIndex = 13;
            this.cancelButton.Tag = "";
            this.cancelButton.Text = "キャンセル";
            this.cancelButton.UseVisualStyleBackColor = true;
            this.cancelButton.Click += new System.EventHandler(this.CancelButton_Click);
            // 
            // workLabel
            // 
            this.workLabel.AutoSize = true;
            this.workLabel.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workLabel.ForeColor = System.Drawing.SystemColors.ControlText;
            this.workLabel.Location = new System.Drawing.Point(10, 26);
            this.workLabel.Name = "workLabel";
            this.workLabel.Size = new System.Drawing.Size(35, 12);
            this.workLabel.TabIndex = 2;
            this.workLabel.Tag = "";
            this.workLabel.Text = "工程：";
            // 
            // workComboBox
            // 
            this.workComboBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.workComboBox.BackColor = System.Drawing.SystemColors.Window;
            this.workComboBox.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.workComboBox.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.workComboBox.FormattingEnabled = true;
            this.workComboBox.Location = new System.Drawing.Point(10, 43);
            this.workComboBox.Name = "workComboBox";
            this.workComboBox.Size = new System.Drawing.Size(396, 20);
            this.workComboBox.TabIndex = 3;
            this.workComboBox.SelectedIndexChanged += new System.EventHandler(this.WorkComboBox_SelectedIndexChanged);
            // 
            // explanationLabel
            // 
            this.explanationLabel.AutoSize = true;
            this.explanationLabel.ForeColor = System.Drawing.SystemColors.ControlText;
            this.explanationLabel.Location = new System.Drawing.Point(18, 20);
            this.explanationLabel.Name = "explanationLabel";
            this.explanationLabel.Size = new System.Drawing.Size(418, 24);
            this.explanationLabel.TabIndex = 0;
            this.explanationLabel.Tag = "";
            this.explanationLabel.Text = "作業手順の作成や削除、順序変更が出来ます。\r\n既存の作業手順から新しい作業手順を作成したい場合は、コピー元を指定してください。";
            // 
            // sourceGroupBox
            // 
            this.sourceGroupBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.sourceGroupBox.Controls.Add(this.workComboBox);
            this.sourceGroupBox.Controls.Add(this.workProcedureLabel);
            this.sourceGroupBox.Controls.Add(this.workProcedureComboBox);
            this.sourceGroupBox.Controls.Add(this.workLabel);
            this.sourceGroupBox.Font = new System.Drawing.Font("MS UI Gothic", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.sourceGroupBox.ForeColor = System.Drawing.Color.CornflowerBlue;
            this.sourceGroupBox.Location = new System.Drawing.Point(20, 62);
            this.sourceGroupBox.Name = "sourceGroupBox";
            this.sourceGroupBox.Size = new System.Drawing.Size(416, 135);
            this.sourceGroupBox.TabIndex = 1;
            this.sourceGroupBox.TabStop = false;
            this.sourceGroupBox.Tag = "";
            this.sourceGroupBox.Text = "コピー元";
            // 
            // currentWorkLabel
            // 
            this.currentWorkLabel.AutoEllipsis = true;
            this.currentWorkLabel.ForeColor = System.Drawing.SystemColors.ControlText;
            this.currentWorkLabel.Location = new System.Drawing.Point(20, 220);
            this.currentWorkLabel.Name = "currentWorkLabel";
            this.currentWorkLabel.Size = new System.Drawing.Size(265, 12);
            this.currentWorkLabel.TabIndex = 6;
            this.currentWorkLabel.Tag = "";
            this.currentWorkLabel.Text = "現在の工程：";
            // 
            // WorkProcedureForm
            // 
            this.AcceptButton = this.okButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.cancelButton;
            this.ClientSize = new System.Drawing.Size(459, 486);
            this.Controls.Add(this.currentWorkLabel);
            this.Controls.Add(this.sourceGroupBox);
            this.Controls.Add(this.explanationLabel);
            this.Controls.Add(this.cancelButton);
            this.Controls.Add(this.okButton);
            this.Controls.Add(this.upWorkProcedureButton);
            this.Controls.Add(this.downWorkProcedureButton);
            this.Controls.Add(this.deleteWorkProcedureButton);
            this.Controls.Add(this.addWorkProcedureButton);
            this.Controls.Add(this.workProcedureListBox);
            this.Cursor = System.Windows.Forms.Cursors.Default;
            this.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(93)))), ((int)(((byte)(136)))), ((int)(((byte)(224)))));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.MinimumSize = new System.Drawing.Size(475, 525);
            this.Name = "WorkProcedureForm";
            this.ShowIcon = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
            this.Tag = "";
            this.Text = "作業手順の編集";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.WorkProcedureForm_FormClosing);
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.WorkProcedureForm_FormClosed);
            this.Load += new System.EventHandler(this.WorkProcedureForm_Load);
            this.Resize += new System.EventHandler(this.WorkProcedureForm_Resize);
            this.sourceGroupBox.ResumeLayout(false);
            this.sourceGroupBox.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label workLabel;
        private System.Windows.Forms.Label workProcedureLabel;
        private System.Windows.Forms.ComboBox workComboBox;
        private System.Windows.Forms.ComboBox workProcedureComboBox;
        private System.Windows.Forms.ListBox workProcedureListBox;
        private System.Windows.Forms.Button addWorkProcedureButton;
        private System.Windows.Forms.Button deleteWorkProcedureButton;
        private System.Windows.Forms.Button downWorkProcedureButton;
        private System.Windows.Forms.Button upWorkProcedureButton;
        private System.Windows.Forms.Button okButton;
        private System.Windows.Forms.Button cancelButton;
        private Label explanationLabel;
        private GroupBox sourceGroupBox;
        private Label currentWorkLabel;
    }
}