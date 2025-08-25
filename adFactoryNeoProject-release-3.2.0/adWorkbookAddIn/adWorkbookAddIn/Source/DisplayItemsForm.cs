using ExcelImport;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class DisplayItemsForm : Form
    {
        private const int DATA_HEIGHT = 30;
        private const int TARGET_COLUMN = 0;
        private const int ITEM_NAME_COLUMN = 1;
        private const int DELETE_COLUMN = 2;
        private const int UP_COLUMN = 3;
        private const int DOWN_COLUMN = 4;

        private int tabIndex = 10;
        private string saveText = "";

        private Dictionary<string, DisplayItemTarget> ReverseMap = new Dictionary<string, DisplayItemTarget>();

        public List<DisplayItem> DisplayItems { get; private set; }

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="displayItems">表示項目のリスト</param>
        public DisplayItemsForm(List<DisplayItem> displayItems)
        {
            InitializeComponent();
            DisplayItems = displayItems;

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DisplayItemsForm_Load(object sender, EventArgs e)
        {
            ReverseMap[LocaleUtil.GetString("DisplayItemsForm.kanban")] = DisplayItemTarget.KANBAN;
            ReverseMap[LocaleUtil.GetString("DisplayItemsForm.workKanban")] = DisplayItemTarget.WORKKANBAN;
            ReverseMap[LocaleUtil.GetString("DisplayItemsForm.work")] = DisplayItemTarget.WORK;

            targetComboBox.Items.Add(LocaleUtil.GetString("DisplayItemsForm.kanban"));
            targetComboBox.Items.Add(LocaleUtil.GetString("DisplayItemsForm.workKanban"));
            targetComboBox.Items.Add(LocaleUtil.GetString("DisplayItemsForm.work"));
            targetComboBox.SelectedIndex = 0;

            dataPanel.RowCount = 0;
            foreach (DisplayItem displayItem in DisplayItems)
            {
                CreateList(displayItem);
            }
        }

        /// <summary>
        /// リストデータの作成
        /// </summary>
        /// <param name="displayItem">表示項目</param>
        private void CreateList(DisplayItem displayItem)
        {
            int rowCount = dataPanel.RowCount;

            Label label = new Label
            {
                Text = ReverseMap.FirstOrDefault(v => v.Value == displayItem.target).Key,
                TabIndex = tabIndex++,
                Margin = new Padding(3, 8, 3, 3)
            };

            TextBox textBox = new TextBox
            {
                Text = displayItem.name,
                TabIndex = tabIndex++,
                Margin = new Padding(3, 5, 3, 3),
                Dock = DockStyle.Fill
            };
            textBox.Enter += TextBox_Enter;
            textBox.Leave += TextBox_Leave;

            Button deleteButton = new Button
            {
                Name = "deleteButton",
                Text = "-",
                TabIndex = tabIndex++,
                Size = new Size(25, 25),
                Margin = new Padding(0, 3, 0, 0),
                Padding = new Padding(0, 0, 1, 1),
                Cursor = Cursors.Hand,
                Dock = DockStyle.Fill
            };
            deleteButton.Click += DeleteButton_Click;

            Button upButton = new Button
            {
                Name = "upButton",
                Text = "↑",
                TabIndex = tabIndex++,
                Size = new Size(25, 25),
                Margin = new Padding(0, 3, 0, 0),
                Cursor = Cursors.Hand,
                Dock = DockStyle.Fill
            };
            upButton.Click += UpButton_Click;

            Button downButton = new Button
            {
                Name = "downButton",
                Text = "↓",
                TabIndex = tabIndex++,
                Size = new Size(25, 25),
                Margin = new Padding(0, 3, 0, 0),
                Cursor = Cursors.Hand,
                Dock = DockStyle.Fill
            };
            downButton.Click += DownButton_Click;

            dataPanel.SuspendLayout();
            dataPanel.RowCount = rowCount;
            dataPanel.RowStyles.Add(new RowStyle(SizeType.Absolute, DATA_HEIGHT));

            dataPanel.Controls.Add(label, TARGET_COLUMN, rowCount);
            dataPanel.Controls.Add(textBox, ITEM_NAME_COLUMN, rowCount);
            dataPanel.Controls.Add(deleteButton, DELETE_COLUMN, rowCount);
            dataPanel.Controls.Add(upButton, UP_COLUMN, rowCount);
            dataPanel.Controls.Add(downButton, DOWN_COLUMN, rowCount);
            LogUtils.AttachLoggingToControl(deleteButton);
            LogUtils.AttachLoggingToControl(upButton);
            LogUtils.AttachLoggingToControl(downButton);

            dataPanel.RowCount = rowCount + 1;
            dataPanel.ResumeLayout(false);
            dataPanel.PerformLayout();
        }

        /// <summary>
        /// テキストボックスがフォーカスされた時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void TextBox_Enter(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            saveText = textBox.Text;
        }

        /// <summary>
        /// テキストボックスからフォーカスが外れた時の処理
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
            Label focusedLabel = (Label)dataPanel.GetControlFromPosition(TARGET_COLUMN, position.Row);

            int rowCount = dataPanel.RowCount;
            for (int row = 0; row < rowCount; row++)
            {
                if (row == position.Row)
                {
                    continue;
                }

                Label label = (Label)dataPanel.GetControlFromPosition(TARGET_COLUMN, row);
                TextBox textBox = (TextBox)dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row);
                if (label.Text == focusedLabel.Text && textBox.Text == focusedTextBox.Text)
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

            int rowCount = dataPanel.RowCount;
            for (int row = 0; row < rowCount; row++)
            {
                Label label = (Label)dataPanel.GetControlFromPosition(TARGET_COLUMN, row);
                TextBox textBox = (TextBox)dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row);
                if (label.Text == (string)targetComboBox.SelectedItem && textBox.Text == itemNameTextBox.Text)
                {
                    MessageBox.Show(
                        LocaleUtil.GetString("key.alreadyAdded", textBox.Text),
                        LocaleUtil.GetString("key.error"));
                    return;
                }
            }

            DisplayItem displayItem = new DisplayItem
            {
                target = (DisplayItemTarget)targetComboBox.SelectedIndex,
                name = itemNameTextBox.Text
            };
            CreateList(displayItem);

            itemNameTextBox.Focus();
            itemNameTextBox.Text = "";
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
        /// 上ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void UpButton_Click(object sender, EventArgs e)
        {
            Button button = (Button)sender;
            int row = dataPanel.GetPositionFromControl(button).Row;
            if (row == 0)
            {
                return;
            }
            SwapRows(row, row - 1);
        }

        /// <summary>
        /// 下ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DownButton_Click(object sender, EventArgs e)
        {
            Button button = (Button)sender;
            int row = dataPanel.GetPositionFromControl(button).Row;
            if (row == dataPanel.RowCount)
            {
                return;
            }
            SwapRows(row, row + 1);
        }

        /// <summary>
        /// 行1と行2の入れ替え
        /// </summary>
        /// <param name="row1">行1</param>
        /// <param name="row2">行2</param>
        private void SwapRows(int row1, int row2)
        {
            int columnCount = dataPanel.ColumnCount;

            Control[] row1Controls = new Control[columnCount];
            Control[] row2Controls = new Control[columnCount];

            for (int col = 0; col < columnCount; col++)
            {
                row1Controls[col] = dataPanel.GetControlFromPosition(col, row1);
                row2Controls[col] = dataPanel.GetControlFromPosition(col, row2);
            }

            for (int col = 0; col < columnCount; col++)
            {
                if (row1Controls[col] is TextBox textBox1 && row2Controls[col] is TextBox textBox2)
                {
                    string temp = textBox1.Text;
                    textBox1.Text = textBox2.Text;
                    textBox2.Text = temp;
                }
                if (row1Controls[col] is Label label1 && row2Controls[col] is Label label2)
                {
                    string temp = label1.Text;
                    label1.Text = label2.Text;
                    label2.Text = temp;
                }
            }
        }

        /// <summary>
        /// OKボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OkButton_Click(object sender, EventArgs e)
        {
            DisplayItems = new List<DisplayItem>();
            for (int row = 0; row < dataPanel.RowCount; row++)
            {
                DisplayItem displayItem = new DisplayItem
                {
                    target = ReverseMap[dataPanel.GetControlFromPosition(TARGET_COLUMN, row).Text],
                    name = dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row).Text
                };
                DisplayItems.Add(displayItem);
            }
            DialogResult = DialogResult.OK;
            Close();
        }

        /// <summary>
        /// キャンセルボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CancelButton_Click(object sender, EventArgs e)
        {
            Close();
        }
    }
}
