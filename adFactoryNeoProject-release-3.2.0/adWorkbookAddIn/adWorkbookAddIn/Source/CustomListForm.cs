using ExcelImport;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class CustomListForm : Form
    {
        private const int DATA_HEIGHT = 30;
        private const int ITEM_NAME_COLUMN = 0;
        private const int VALUE_COLUMN = 1;
        private const int DELETE_COLUMN = 2;

        private string saveText = "";
        private int tabIndex = 10;

        public List<CustomValue> customValues { get; private set; }

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="customValues">カスタム設定値リスト</param>
        public CustomListForm(List<CustomValue> customValues)
        {
            InitializeComponent();
            this.customValues = customValues;

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// 追加ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void AddButton_Click(object sender, EventArgs e)
        {
            if (itemNameTextBox.Text.Trim() == "")
            {
                itemNameTextBox.Focus();
                return;
            }
            if (valueTextBox.Text.Trim() == "")
            {
                valueTextBox.Focus();
                return;
            }

            int rowCount = dataPanel.RowCount;
            for (int row = 0; row < rowCount; row++)
            {
                Control control1 = dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row);
                if (control1 is TextBox textBox1)
                {
                    if (itemNameTextBox.Text == textBox1.Text)
                    {
                        MessageBox.Show(
                            LocaleUtil.GetString("key.alreadyAdded", itemNameTextBox.Text),
                            LocaleUtil.GetString("key.error"));
                        itemNameTextBox.Focus();
                        return;
                    }
                }
            }

            CustomValue customValue = new CustomValue();
            customValue.name = itemNameTextBox.Text;
            customValue.value = valueTextBox.Text;
            CreateList(customValue);

            itemNameTextBox.Focus();
            itemNameTextBox.Text = "";
            valueTextBox.Text = "";
        }

        /// <summary>
        /// リストの作成
        /// </summary>
        /// <param name="customValue">カスタム設定値</param>
        private void CreateList(CustomValue customValue)
        {
            int rowCount = dataPanel.RowCount;
            Padding margin = new Padding(3, 5, 3, 3);

            TextBox itemName = new TextBox();
            itemName.Text = customValue.name;
            itemName.Dock = DockStyle.Fill;
            itemName.Margin = margin;
            itemName.TabIndex = tabIndex++;
            itemName.Enter += new EventHandler(TextBox_Enter);
            itemName.Leave += new EventHandler(TextBox_Leave);

            TextBox value = new TextBox();
            value.Text = customValue.value;
            value.Dock = DockStyle.Fill;
            value.Margin = margin;
            value.TabIndex = tabIndex++;
            value.Enter += new EventHandler(TextBox_Enter);
            value.Leave += new EventHandler(TextBox_Leave);

            Button deleteButton = new Button();
            deleteButton.Name = "deleteButton";
            deleteButton.Text = "-";
            deleteButton.Size = new Size(25, 25);
            deleteButton.Margin = new Padding(0, 4, 0, 0);
            deleteButton.Padding = new Padding(0, 0, 1, 1);
            deleteButton.Cursor = Cursors.Hand;
            deleteButton.TabIndex = tabIndex++;
            deleteButton.Click += new EventHandler(DeleteButton_Click);

            dataPanel.SuspendLayout();
            dataPanel.RowCount = rowCount;
            dataPanel.RowStyles.Add(new RowStyle(SizeType.Absolute, DATA_HEIGHT));

            dataPanel.Controls.Add(itemName, ITEM_NAME_COLUMN, rowCount);
            dataPanel.Controls.Add(value, VALUE_COLUMN, rowCount);
            dataPanel.Controls.Add(deleteButton, DELETE_COLUMN, rowCount);
            LogUtils.AttachLoggingToControl(deleteButton);

            dataPanel.RowCount = rowCount + 1;
            dataPanel.ResumeLayout(false);
            dataPanel.PerformLayout();
        }

        /// <summary>
        /// フォーカスされたときの処理
        /// </summary>
        /// <param name="sender">コントールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void TextBox_Enter(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            saveText = textBox.Text;
        }

        /// <summary>
        /// フォーカスが外れたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void TextBox_Leave(object sender, EventArgs e)
        {
            TextBox focusedTextBox = (TextBox)sender;
            if (focusedTextBox.Text == "")
            {
                focusedTextBox.Text = saveText;
                focusedTextBox.Focus();
                return;
            }

            TableLayoutPanelCellPosition position = dataPanel.GetPositionFromControl(focusedTextBox);
            int rowCount = dataPanel.RowCount;

            for (int row = 0; row < rowCount; row++)
            {
                if (row == position.Row)
                {
                    continue;
                }

                TextBox textBox = (TextBox)dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row);
                if (textBox.Text == focusedTextBox.Text)
                {
                    MessageBox.Show(
                        LocaleUtil.GetString("key.alreadyAdded", focusedTextBox.Text),
                        LocaleUtil.GetString("key.error"));
                    focusedTextBox.Text = saveText;
                    focusedTextBox.Focus();
                    return;
                }
            }
        }

        /// <summary>
        /// 削除ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DeleteButton_Click(object sender, EventArgs e)
        {
            Button button = (Button)sender;
            int row = dataPanel.GetPositionFromControl(button).Row;

            for (int col = 0; col < dataPanel.ColumnCount; col++)
            {
                Control control = dataPanel.GetControlFromPosition(col, row);
                if (control != null)
                {
                    dataPanel.Controls.Remove(control);
                }
            }
            dataPanel.RowStyles.RemoveAt(row);

            for (int i = row + 1; i < dataPanel.RowCount; i++)
            {
                for (int col = 0; col < dataPanel.ColumnCount; col++)
                {
                    Control control = dataPanel.GetControlFromPosition(col, i);
                    if (control != null)
                    {
                        dataPanel.Controls.Remove(control);
                        dataPanel.Controls.Add(control, col, i - 1);
                    }
                }
            }
            dataPanel.RowCount--;
        }

        /// <summary>
        /// OKボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OkButton_Click(object sender, EventArgs e)
        {
            customValues = new List<CustomValue>();
            for (int row = 0; row < dataPanel.RowCount; row++)
            {
                CustomValue customValue = new CustomValue();
                customValue.name = dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row).Text;
                customValue.value = dataPanel.GetControlFromPosition(VALUE_COLUMN, row).Text;
                customValues.Add(customValue);
            }
            this.DialogResult = DialogResult.OK;
            this.Close();
        }

        /// <summary>
        /// キャンセルボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CancelButton_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CustomListForm_Load(object sender, EventArgs e)
        {
            dataPanel.RowCount = 0;
            foreach (CustomValue customValue in customValues)
            {
                CreateList(customValue);
            }
        }
    }
}
