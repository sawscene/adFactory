using ExcelImport;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class ListWithColorForm : Form
    {
        private const int DATA_HEIGHT = 30;
        private const int VALUE_COLUMN = 0;
        private const int FONT_COLOR_COLUMN = 2;
        private const int BACKGROUND_COLOR_COLUMN = 4;

        private int tabIndex = 10;
        private string saveText = "";

        public List<ListWithColor> listWithColors { get; private set; }

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="listWithColors">色付き設定値のリスト</param>
        public ListWithColorForm(List<ListWithColor> listWithColors)
        {
            InitializeComponent();
            this.listWithColors = listWithColors;

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
            // 空の文字列
            if (valueText.Text.Trim() == "")
            {
                valueText.Focus();
                return;
            }

            ListWithColor listWithColor = new ListWithColor();
            listWithColor.value = valueText.Text;
            listWithColor.fontColor = "#000000";
            listWithColor.backgroundColor = "#FFFFFF";
            CreateList(listWithColor);

            valueText.Focus();
            valueText.Text = "";
        }

        /// <summary>
        /// リストの作成
        /// </summary>
        /// <param name="listWithColor">色付き設定値</param>
        private void CreateList(ListWithColor listWithColor)
        {
            int rowCount = dataPanel.RowCount;

            TextBox textbox = new TextBox();
            textbox.Text = listWithColor.value;
            textbox.Margin = new Padding(3, 5, 10, 3);
            textbox.Dock = DockStyle.Fill;
            textbox.TabIndex = tabIndex++;
            textbox.Enter += new EventHandler(ValueTextBox_Enter);
            textbox.Leave += new EventHandler(ValueTextBox_Leave);

            Panel fontColorPanel = new Panel();
            fontColorPanel.BorderStyle = BorderStyle.Fixed3D;
            fontColorPanel.BackColor = ColorTranslator.FromHtml(listWithColor.fontColor);
            fontColorPanel.Margin = new Padding(5, 7, 0, 0);
            fontColorPanel.Size = new Size(20, 19);
            fontColorPanel.Cursor = Cursors.Hand;
            fontColorPanel.TabIndex = tabIndex++;
            fontColorPanel.Click += new EventHandler(ColorPanel_Click);

            Label fontColorLabel = new Label();
            fontColorLabel.Margin = new Padding(0, 8, 0, 0);
            fontColorLabel.Text = listWithColor.fontColor;
            fontColorPanel.TabIndex = 0;

            Panel backgroundColorPanel = new Panel();
            backgroundColorPanel.BorderStyle = BorderStyle.Fixed3D;
            backgroundColorPanel.BackColor = ColorTranslator.FromHtml(listWithColor.backgroundColor);
            backgroundColorPanel.Margin = new Padding(5, 7, 0, 0);
            backgroundColorPanel.Size = new Size(20, 19);
            backgroundColorPanel.Cursor = Cursors.Hand;
            backgroundColorPanel.TabIndex = tabIndex++;
            backgroundColorPanel.Click += new EventHandler(ColorPanel_Click);

            Label backgroundColorLabel = new Label();
            backgroundColorLabel.Margin = new Padding(0, 8, 0, 0);
            backgroundColorLabel.Text = listWithColor.backgroundColor;
            backgroundColorLabel.TabIndex = 0;

            Button deleteButton = new Button();
            deleteButton.Text = "-";
            deleteButton.Size = new Size(25, 25);
            deleteButton.Margin = new Padding(0, 4, 0, 0);
            deleteButton.Padding = new Padding(0, 0, 1, 1);
            deleteButton.Cursor = Cursors.Hand;
            deleteButton.TabIndex = tabIndex++;
            deleteButton.Click += new EventHandler(DeleteButton_Click);
            LogUtils.AttachLoggingToControl(deleteButton);

            Button upButton = new Button();
            upButton.Text = "↑";
            upButton.Size = new Size(25, 25);
            upButton.Margin = new Padding(0, 4, 0, 0);
            upButton.Cursor = Cursors.Hand;
            upButton.TabIndex = tabIndex++;
            upButton.Click += new EventHandler(UpButton_Click);
            LogUtils.AttachLoggingToControl(upButton);

            Button downButton = new Button();
            downButton.Text = "↓";
            downButton.Size = new Size(25, 25);
            downButton.Margin = new Padding(0, 4, 0, 0);
            downButton.Cursor = Cursors.Hand;
            downButton.TabIndex = tabIndex++;
            downButton.Click += new EventHandler(DownButton_Click);
            LogUtils.AttachLoggingToControl(downButton);

            dataPanel.SuspendLayout();
            dataPanel.RowCount = rowCount;
            dataPanel.RowStyles.Add(new RowStyle(SizeType.Absolute, DATA_HEIGHT));

            dataPanel.Controls.Add(textbox, 0, rowCount);
            dataPanel.Controls.Add(fontColorPanel, 1, rowCount);
            dataPanel.Controls.Add(fontColorLabel, 2, rowCount);
            dataPanel.Controls.Add(backgroundColorPanel, 3, rowCount);
            dataPanel.Controls.Add(backgroundColorLabel, 4, rowCount);
            dataPanel.Controls.Add(deleteButton, 5, rowCount);
            dataPanel.Controls.Add(upButton, 6, rowCount);
            dataPanel.Controls.Add(downButton, 7, rowCount);

            dataPanel.RowCount = rowCount + 1;
            dataPanel.ResumeLayout(false);
            dataPanel.PerformLayout();
        }

        /// <summary>
        /// フォーカスされたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ValueTextBox_Enter(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            saveText = textBox.Text;
        }

        /// <summary>
        /// フォーカスが外れたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ValueTextBox_Leave(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            if (textBox.Text == "")
            {
                textBox.Text = saveText;
            }
        }

        /// <summary>
        /// 色パネル押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ColorPanel_Click(object sender, EventArgs e)
        {
            Panel panel = (Panel)sender;
            colorDialog.Color = panel.BackColor;
            if (colorDialog.ShowDialog() == DialogResult.OK)
            {
                Color selectedColor = colorDialog.Color;
                panel.BackColor = selectedColor;

                TableLayoutPanelCellPosition position = dataPanel.GetPositionFromControl(panel);
                Control control = dataPanel.GetControlFromPosition(position.Column + 1, position.Row);
                if (control is Label label)
                {
                    string color = GetHexFromColor(selectedColor);
                    label.Text = color;
                }
            }
        }

        /// <summary>
        /// 色コードの変換処理
        /// </summary>
        /// <param name="color">色のオブジェクト</param>
        /// <returns>色コード</returns>
        private string GetHexFromColor(Color color)
        {
            return $"#{color.R:X2}{color.G:X2}{color.B:X2}";
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

            // 指定された行のすべてのコントロールを削除
            for (int col = 0; col < dataPanel.ColumnCount; col++)
            {
                Control control = dataPanel.GetControlFromPosition(col, row);
                if (control != null)
                {
                    dataPanel.Controls.Remove(control);
                }
            }
            // 行スタイルを削除
            dataPanel.RowStyles.RemoveAt(row);

            // 行を削除
            for (int i = row + 1; i < dataPanel.RowCount; i++)
            {
                // 下の行を上に移動
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
            // 最後の行を削除
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
        /// 行の入れ替え
        /// </summary>
        /// <param name="row1">行1</param>
        /// <param name="row2">行2</param>
        private void SwapRows(int row1, int row2)
        {
            // 列数を取得
            int columnCount = dataPanel.ColumnCount;

            // 行1と行2のコントロールを一時保存するリストを作成
            Control[] row1Controls = new Control[columnCount];
            Control[] row2Controls = new Control[columnCount];

            // 行のコントロールを取得して保存
            for (int col = 0; col < columnCount; col++)
            {
                row1Controls[col] = dataPanel.GetControlFromPosition(col, row1);
                row2Controls[col] = dataPanel.GetControlFromPosition(col, row2);
            }

            // 行1と行2のテキストを入れ替える
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
                if (row1Controls[col] is Panel panel1 && row2Controls[col] is Panel panel2)
                {
                    Color temp = panel1.BackColor;
                    panel1.BackColor = panel2.BackColor;
                    panel2.BackColor = temp;
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
            listWithColors = new List<ListWithColor>();
            for (int row = 0; row < dataPanel.RowCount; row++)
            {
                ListWithColor listWithColor = new ListWithColor();
                listWithColor.value = dataPanel.GetControlFromPosition(VALUE_COLUMN, row).Text;
                listWithColor.fontColor = dataPanel.GetControlFromPosition(FONT_COLOR_COLUMN, row).Text;
                listWithColor.backgroundColor = dataPanel.GetControlFromPosition(BACKGROUND_COLOR_COLUMN, row).Text;
                listWithColors.Add(listWithColor);
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
        private void ListWithColorForm_Load(object sender, EventArgs e)
        {
            dataPanel.RowCount = 0;
            foreach (ListWithColor listWithColor in listWithColors)
            {
                CreateList(listWithColor);
            }
        }
    }
}
