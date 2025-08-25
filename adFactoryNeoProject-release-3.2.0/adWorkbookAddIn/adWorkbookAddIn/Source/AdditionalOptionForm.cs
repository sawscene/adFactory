using ExcelImport;
using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using System.Xml.Linq;

namespace adWorkbookAddIn.Source
{
    public partial class AdditionalOptionForm : Form
    {
        private static readonly XDocument doc = XDocument.Parse(Properties.Resources.WorkSettings);

        private const int DATA_HEIGHT = 30;
        private const int ITEM_NAME_COLUMN = 0;
        private const int TYPE_COLUMN = 1;
        private const int VALUE_COLUMN = 2;
        private const int DELETE_COLUMN = 3;

        private int tabIndex = 1;

        public List<AdditionalOption> AdditionalOptions { get; private set; }

        /// <summary>
        /// XMLデータの取得
        /// </summary>
        /// <param name="key">XMLデータのキー</param>
        /// <returns>XMLデータ</returns>
        private static XElement GetXmlData(string key)
        {
            return doc.Descendants(key).FirstOrDefault();
        }

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="additionalOptions">追加情報のリスト</param>
        public AdditionalOptionForm(List<AdditionalOption> additionalOptions)
        {
            InitializeComponent();
            AdditionalOptions = additionalOptions;

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">オブジェクトのコントロール</param>
        /// <param name="e">イベント引数</param>
        private void AdditionalOptionForm_Load(object sender, EventArgs e)
        {
            Button addButton = new Button
            {
                Name = "addButton",
                Text = "+",
                TabIndex = 1000,
                Size = new Size(25, 25),
                Padding = new Padding(0, 0, 1, 1),
                Cursor = Cursors.Hand
            };
            addButton.Click += AddButton_Click;
            dataPanel.Controls.Add(addButton, 0, 0);
            LogUtils.AttachLoggingToControl(addButton);

            foreach (AdditionalOption additionalOption in AdditionalOptions)
            {
                CreateList(additionalOption);
            }
        }

        /// <summary>
        /// リストデータの作成
        /// </summary>
        /// <param name="additionalOption">追加情報</param>
        /// <returns>true:成功、false:失敗</returns>
        private void CreateList(AdditionalOption additionalOption)
        {
            try
            {
                int rowCount = dataPanel.RowCount;
                int insertRow = dataPanel.RowCount - 1;
                Padding margin = new Padding(3, 5, 3, 3);

                TextBox itemName = new TextBox
                {
                    Name = "itemName",
                    Text = additionalOption.name,
                    TabIndex = tabIndex++,
                    Dock = DockStyle.Fill,
                    Margin = margin
                };
                itemName.Focus();

                ComboBox type = new ComboBox
                {
                    Name = "type",
                    TabIndex = tabIndex++,
                    Dock = DockStyle.Fill,
                    Margin = margin,
                    DropDownStyle = ComboBoxStyle.DropDownList
                };

                XElement xmlData = GetXmlData("AdditionalInfoType");
                if (xmlData == null)
                {
                    throw new Exception(LocaleUtil.GetString("key.loadXmlDataFailed"));
                }
                List<string> typeData = xmlData.Descendants("Value").Select(v => LocaleUtil.GetString((string)v) ?? "").ToList();
                type.Items.AddRange(typeData.ToArray());
                type.SelectedIndex = (int)additionalOption.type;

                TextBox value = new TextBox
                {
                    Name = "value",
                    Text = additionalOption.value,
                    TabIndex = tabIndex++,
                    Dock = DockStyle.Fill,
                    Margin = margin
                };

                Button deleteButton = new Button
                {
                    Name = "deleteButton",
                    Text = "-",
                    Size = new Size(25, 25),
                    Margin = new Padding(0, 4, 0, 0),
                    Padding = new Padding(0, 0, 1, 1),
                    Cursor = Cursors.Hand,
                    TabIndex = tabIndex++
                };
                deleteButton.Click += new EventHandler(DeleteButton_Click);

                dataPanel.SuspendLayout();
                dataPanel.RowCount = rowCount + 1;
                dataPanel.RowStyles.Add(new RowStyle(SizeType.Absolute, DATA_HEIGHT));

                Control control = dataPanel.GetControlFromPosition(0, insertRow);
                if (control != null)
                {
                    dataPanel.Controls.Remove(control);
                    dataPanel.Controls.Add(control, 0, rowCount);
                }

                dataPanel.Controls.Add(itemName, ITEM_NAME_COLUMN, insertRow);
                dataPanel.Controls.Add(type, TYPE_COLUMN, insertRow);
                dataPanel.Controls.Add(value, VALUE_COLUMN, insertRow);
                dataPanel.Controls.Add(deleteButton, DELETE_COLUMN, insertRow);
                LogUtils.AttachLoggingToControl(itemName);
                LogUtils.AttachLoggingToControl(type);
                LogUtils.AttachLoggingToControl(value);
                LogUtils.AttachLoggingToControl(deleteButton);

                dataPanel.ResumeLayout(false);
                dataPanel.PerformLayout();
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

        /// <summary>
        /// 追加ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void AddButton_Click(object sender, EventArgs e)
        {
            AdditionalOption additionalOption = new AdditionalOption
            {
                name = "",
                type = 0,
                value = ""
            };
            CreateList(additionalOption);
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
            AdditionalOptions = new List<AdditionalOption>();
            for (int row = 0; row < dataPanel.RowCount - 1; row++)
            {
                TextBox itemNameTextBox = (TextBox)dataPanel.GetControlFromPosition(ITEM_NAME_COLUMN, row);
                if (itemNameTextBox.Text == "")
                {
                    MessageBox.Show(LocaleUtil.GetString("key.inputRequired"));
                    itemNameTextBox.Focus();
                    return;
                }
                ComboBox typeComboBox = (ComboBox)dataPanel.GetControlFromPosition(TYPE_COLUMN, row);
                if (typeComboBox.SelectedIndex == 0)
                {
                    MessageBox.Show(LocaleUtil.GetString("key.inputRequired"));
                    typeComboBox.Focus();
                    return;
                }

                AdditionalOption additionalOption = new AdditionalOption
                {
                    name = itemNameTextBox.Text,
                    type = (AdditionalOptionType)typeComboBox.SelectedIndex,
                    value = dataPanel.GetControlFromPosition(VALUE_COLUMN, row).Text
                };
                AdditionalOptions.Add(additionalOption);
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
