using ExcelImport;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Web.Script.Serialization;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;
using Excel = Microsoft.Office.Interop.Excel;

namespace adWorkbookAddIn.Source
{
    public partial class CheckItemsControl : UserControl
    {
        private static readonly XDocument doc = XDocument.Parse(Properties.Resources.CheckItemSettings);

        private readonly Excel.Application app;
        private readonly XmlDocument excelFormat;
        private readonly XmlNode checkItemsNode;
        private readonly ToolTip toolTip;

        private const int HEADER_HEIGHT = 25;
        private const int DATA_HEIGHT = 30;
        private const int TEXT_BOX = 0;
        private const int CHECK_BOX = 1;
        private const int COMBO_BOX = 2;
        private const int CUSTOM_FIELDS = 10;

        private CheckItemOption checkItemOption;
        private ExcelCell baseCell;
        private Excel.Worksheet activeSheet;
        private string activeTag;

        public class Item
        {
            public int Id { get; set; }
            public string Value { get; set; }

            public Item(int id, string value)
            {
                this.Id = id;
                this.Value = value ?? "";
            }
        }

        /// <summary>
        /// XMLデータの取得
        /// </summary>
        /// <param name="key">XMLのキー</param>
        /// <returns>XMLデータ</returns>
        private static XElement GetXmlData(string key)
        {
            return doc.Descendants(key).FirstOrDefault();
        }

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public CheckItemsControl()
        {
            InitializeComponent();

            app = Globals.ThisAddIn.Application;
            excelFormat = Globals.ThisAddIn.ExcelFormat;
            checkItemsNode = excelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure/checkItems");
            toolTip = new ToolTip();

            typeComboBox.MouseWheel += new MouseEventHandler(ComboBox_MouseWheel);
            inputTypeComboBox.MouseWheel += new MouseEventHandler(ComboBox_MouseWheel);
            datetimeFormatComboBox.MouseWheel += new MouseEventHandler(ComboBox_MouseWheel);

            Resize += new EventHandler(CheckItemsControl_Resize);

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// 作業ウィンドウの設定
        /// </summary>
        /// <param name="range">選択中のチェック項目の範囲</param>
        public void InitializeTaskPaneData(Excel.Range range)
        {
            Excel.Workbook activeWorkbook = app.ActiveWorkbook;
            activeSheet = app.ActiveSheet;
            baseCell = new ExcelCell(range.Cells[1, 1].Address[false, false]);

            // 基本情報の設定
            int col = int.Parse(checkItemsNode.SelectSingleNode("checkItemName").Attributes.GetNamedItem("column").Value) + 1;
            int row = 1;

            StringBuilder sb = new StringBuilder();
            while (row <= range.Rows.Count)
            {
                string value = (string)range.Cells[row, col].Value;
                if (String.IsNullOrWhiteSpace(value))
                {
                    break;
                }

                sb.Append(value);
                sb.Append("\n");
                row++;
            }

            checkItemName.Text = sb.ToString();
            checkItemNamePanel.Size = new Size(checkItemName.Width, row * 15 + 10);
            minValue.Text = ExcelUtils.GetCellString("minValue", checkItemsNode, activeSheet, baseCell).value;
            maxValue.Text = ExcelUtils.GetCellString("maxValue", checkItemsNode, activeSheet, baseCell).value;
            currentValue.Text = ExcelUtils.GetCellString("currentValue", checkItemsNode, activeSheet, baseCell).value;

            // オプションの設定
            this.activeTag = ExcelUtils.GetCellStringNonBlank("tag", checkItemsNode, activeSheet, this.baseCell).value;
            if (this.activeTag == null)
            {
                checkItemOption = new CheckItemOption();
            }
            else
            {
                checkItemOption = Globals.ThisAddIn.FileRoster.GetCheckItemOption(activeWorkbook, this.activeTag, true);
            }

            // 種別コンボボックスの設定
            Delegate originalHandler = LogUtils.GetEventHandler(typeComboBox, typeComboBox.GetType().GetEvent("SelectedIndexChanged"));
            typeComboBox.SelectedIndexChanged -= (EventHandler)originalHandler;
            if (typeComboBox.DataSource == null)
            {
                ComboBoxDataControl(this.typeComboBox, GetXmlData("TypeComboBox"), 0);
            }

            if (checkItemOption.type == 0)
            {
                if (minValue.Text.Trim() != "" || maxValue.Text.Trim() != "")
                {
                    checkItemOption.type = CheckItemType.MEASURE;
                    checkItemOption.inputType = CheckItemInputType.TEN_KEYBOARD;
                    checkItemOption.isRequired = true;
                }
                else
                {
                    checkItemOption.type = CheckItemType.WORK;
                }
            }
            typeComboBox.SelectedValue = (int)checkItemOption.type;
            typeComboBox.SelectedIndexChanged += (EventHandler)originalHandler;

            // 表示／非表示の切り替え
            PanelVisibleControl(this, (int)checkItemOption.type);

            // オプション情報の設定
            InitOptionData(checkItemOption);
        }

        /// <summary>
        /// チェック項目のオプションデータの設定
        /// </summary>
        /// <param name="option">チェック項目のオプションデータ</param>
        private void InitOptionData(CheckItemOption option)
        {
            Utils.SuspendLayout(this);

            int index = (int)option.type;

            // 入力種コンボボックスの設定
            Delegate originalHandler = LogUtils.GetEventHandler(inputTypeComboBox, inputTypeComboBox.GetType().GetEvent("SelectedIndexChanged"));
            inputTypeComboBox.SelectedIndexChanged -= (EventHandler)originalHandler;
            ComboBoxDataControl(this.inputTypeComboBox, GetXmlData("InputTypeComboBox"), index);
            inputTypeComboBox.SelectedValue = (int)option.inputType;
            inputTypeComboBox.SelectedIndexChanged += (EventHandler)originalHandler;
            formulaPanel.Visible = option.inputType == CheckItemInputType.CALCULATION_RESULTS;

            // 日付形式コンボボックスの設定
            originalHandler = LogUtils.GetEventHandler(datetimeFormatComboBox, datetimeFormatComboBox.GetType().GetEvent("SelectedIndexChanged"));
            datetimeFormatComboBox.SelectedIndexChanged -= (EventHandler)originalHandler;
            ComboBoxDataControl(this.datetimeFormatComboBox, GetXmlData("TimestampFormatComboBox"), index);
            datetimeFormatComboBox.SelectedValue = (int)option.datetimeFormat;
            datetimeFormatComboBox.SelectedIndexChanged += (EventHandler)originalHandler;

            // テキストボックスの設定
            initialValueTextBox.Text = option.initialValue;
            formulaTextBox.Text = option.calculationResult;
            ruleTextBox.Text = option.rule;

            // チェックボックスの設定
            requiredCheckBox.Checked = option.isRequired;
            noDirectInputCheckBox.Checked = option.isNoDirectInput;
            noDuplicationCheckBox.Checked = option.isNoDuplicateInput;
            inputCommentCheckBox.Checked = option.isCommentInput;
            displayCommentCheckBox.Checked = option.isCommentDisplay;

            // 使用設備の設定
            List<string> equipmentNames = option.equipments.Select(v => v.name).ToList();
            equipments.Text = string.Join(", ", equipmentNames);
            if (equipments.Text == "")
            {
                equipments.Text = LocaleUtil.GetString("key.noEquipmentConfigured");
            }

            // 詳細設定の設定
            XElement xmlData = GetXmlData("DetailSettings");
            Dictionary<string, Item> items = xmlData.Descendants("Value")
                .Where(v =>
                {
                    string str = (string)v.Attribute("flag");
                    return str.Substring(index, 1) == "1";
                })
                .ToDictionary(
                    v => LocaleUtil.GetString((string)v),
                    v => new Item((int)v.Attribute("type"), (string)v.Attribute("tag"))
                );

            Utils.ResumeLayout(this, false);

            tableLayoutPanel.SuspendLayout();
            tableLayoutPanel.Controls.Clear();
            tableLayoutPanel.RowStyles.Clear();
            tableLayoutPanel.RowCount = 0;

            tableLayoutPanel.RowCount += 1;
            tableLayoutPanel.RowStyles.Add(new RowStyle(SizeType.Absolute, HEADER_HEIGHT));

            Label headerLabel1 = new Label
            {
                Text = LocaleUtil.GetString($"{this.Name}.{itemValueLabel.Name}"),
                Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right,
                TextAlign = ContentAlignment.MiddleCenter
            };
            tableLayoutPanel.Controls.Add(headerLabel1, 0, tableLayoutPanel.RowCount - 1);

            Label headerLabel2 = new Label
            {
                Text = LocaleUtil.GetString($"{this.Name}.{itemNameLabel.Name}"),
                Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right,
                TextAlign = ContentAlignment.MiddleCenter
            };
            tableLayoutPanel.Controls.Add(headerLabel2, 0, tableLayoutPanel.RowCount - 1);

            int tabIndex = 20;
            foreach (var item in items)
            {
                tableLayoutPanel.RowCount += 1;
                tableLayoutPanel.RowStyles.Add(new RowStyle(SizeType.Absolute, DATA_HEIGHT));

                Label dataLabel = new Label
                {
                    Text = item.Key,
                    Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right,
                    TextAlign = ContentAlignment.MiddleLeft
                };
                tableLayoutPanel.Controls.Add(dataLabel, 0, tableLayoutPanel.RowCount - 1);

                string tag = item.Value.Value;
                FieldInfo field = typeof(DetailSettings).GetField(tag);
                object obj = field?.GetValue(option.detailSettings);
                switch (item.Value.Id)
                {
                    case TEXT_BOX:
                        TextBox textBox = new TextBox
                        {
                            Name = "textBox",
                            Text = obj != null ? obj.ToString() : "",
                            Tag = tag,
                            TabIndex = tabIndex,
                            Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right,
                        };
                        textBox.TextChanged += new EventHandler(DetailSettingsText_TextChanged);
                        tableLayoutPanel.Controls.Add(textBox, 1, tableLayoutPanel.RowCount - 1);
                        LogUtils.AttachLoggingToControl(textBox);
                        break;
                    case CHECK_BOX:
                        CheckBox checkBox = new CheckBox
                        {
                            Name = "checkBox",
                            Checked = obj != null && (bool)field.GetValue(option.detailSettings),
                            Tag = tag,
                            TabIndex = tabIndex,
                            Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right,
                            TextAlign = ContentAlignment.MiddleLeft
                        };
                        checkBox.CheckedChanged += new EventHandler(DetailSettingsCheckBox_CheckedChanged);
                        tableLayoutPanel.Controls.Add(checkBox, 1, tableLayoutPanel.RowCount - 1);
                        LogUtils.AttachLoggingToControl(checkBox);
                        break;
                    case COMBO_BOX:
                        ComboBox comboBox = new ComboBox
                        {
                            Name = "comboBox",
                            Tag = tag,
                            TabIndex = tabIndex,
                            DropDownStyle = ComboBoxStyle.DropDownList,
                            Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right
                        };
                        comboBox.Items.Add("");
                        for (int i = 1; i <= CUSTOM_FIELDS; i++)
                        {
                            comboBox.Items.Add(i.ToString());
                        }
                        comboBox.SelectedIndex = obj != null ? (int)field.GetValue(option.detailSettings) : 0;
                        comboBox.SelectedIndexChanged += new EventHandler(DetailSettingsComboBox_SelectedIndexChanged);
                        tableLayoutPanel.Controls.Add(comboBox, 1, tableLayoutPanel.RowCount - 1);
                        LogUtils.AttachLoggingToControl(comboBox);
                        break;
                    default:
                        break;
                }
                tabIndex++;
            }
            tableLayoutPanel.ResumeLayout(false);
            tableLayoutPanel.PerformLayout();

            ResizeControl();
        }

        /// <summary>
        /// 種別が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void TypeComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            // 種別の設定
            ComboBox comboBox = (ComboBox)sender;
            checkItemOption.Clear();
            checkItemOption.type = (CheckItemType)((int)comboBox.SelectedValue);

            // 表示／非表示の切り替え
            PanelVisibleControl(this, (int)checkItemOption.type);

            // オプション情報の設定
            InitOptionData(checkItemOption);
        }

        /// <summary>
        /// 入力種が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void InputTypeComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            ComboBox comboBox = (ComboBox)sender;

            int selectedId = (int)comboBox.SelectedValue;
            checkItemOption.inputType = (CheckItemInputType)selectedId;
            formulaPanel.Visible = checkItemOption.inputType == CheckItemInputType.CALCULATION_RESULTS;
        }

        /// <summary>
        /// 初期値が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void InitialValueTextBox_TextChanged(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            checkItemOption.initialValue = textBox.Text != "" ? textBox.Text : null;
        }

        /// <summary>
        /// リストボタンが押下されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ListButton_Click(object sender, EventArgs e)
        {
            ListWithColorForm form = new ListWithColorForm(checkItemOption.listWithColors);
            if (form.ShowDialog() == DialogResult.OK)
            {
                checkItemOption.listWithColors = form.listWithColors;
            }
        }

        /// <summary>
        /// 計算式が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void FormulaTextBox_TextChanged(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            checkItemOption.calculationResult = textBox.Text != "" ? textBox.Text : null;
        }

        /// <summary>
        /// 日時形式が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DateFormatComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            ComboBox comboBox = (ComboBox)sender;

            int selectedId = (int)comboBox.SelectedValue;
            checkItemOption.datetimeFormat = (CheckItemDatetimeFormat)selectedId;
        }

        /// <summary>
        /// 必須入力が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void RequiredCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            CheckBox checkBox = (CheckBox)sender;
            checkItemOption.isRequired = checkBox.Checked;
        }

        /// <summary>
        /// 直接入力禁止が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void NoDirectInputCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            CheckBox checkBox = (CheckBox)sender;
            checkItemOption.isNoDirectInput = checkBox.Checked;
        }

        /// <summary>
        /// 重複データ入力禁止が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void NoDuplicationCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            CheckBox checkBox = (CheckBox)sender;
            checkItemOption.isNoDuplicateInput = checkBox.Checked;
        }

        /// <summary>
        /// 入力規則が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void RuleTextBox_TextChanged(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            checkItemOption.rule = textBox.Text != "" ? textBox.Text : null;
        }

        /// <summary>
        /// コメント入力が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void InputCommentCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            CheckBox checkBox = (CheckBox)sender;
            checkItemOption.isCommentInput = checkBox.Checked;
        }

        /// <summary>
        /// 後工程コメント表示が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DisplayCommentCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            CheckBox checkBox = (CheckBox)sender;
            checkItemOption.isCommentDisplay = checkBox.Checked;
        }

        /// <summary>
        /// 設備ボタンが押下されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void EquipmentButton_Click(object sender, EventArgs e)
        {
            EquipmentsForm form = new EquipmentsForm(checkItemOption.equipments);
            if (form.ShowDialog() == DialogResult.OK)
            {
                checkItemOption.equipments = form.Equipments;
                equipments.Text = string.Join(", ", form.Equipments.Select(v => v.name).ToList());
                if (equipments.Text == "")
                {
                    equipments.Text = LocaleUtil.GetString("key.noEquipmentConfigured");
                }
            }
        }

        /// <summary>
        /// 詳細設定（テキスト）が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DetailSettingsText_TextChanged(object sender, EventArgs e)
        {
            TextBox textBox = (TextBox)sender;
            FieldInfo field = typeof(DetailSettings).GetField((string)textBox.Tag);
            if (field != null)
            {
                if (field.FieldType == typeof(int?))
                {
                    try
                    {
                        field.SetValue(checkItemOption.detailSettings, int.Parse(textBox.Text));
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine(ex.Message);
                        field.SetValue(checkItemOption.detailSettings, null);
                    }
                }
                else
                {
                    field.SetValue(checkItemOption.detailSettings, textBox.Text ?? null);
                }
            }
        }

        /// <summary>
        /// 詳細設定（チェックボックス）が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DetailSettingsCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            CheckBox checkBox = (CheckBox)sender;
            FieldInfo field = typeof(DetailSettings).GetField((string)checkBox.Tag);
            if (field != null)
            {
                field.SetValue(checkItemOption.detailSettings, checkBox.Checked);
            }
        }

        /// <summary>
        /// 詳細設定（コンボボックス）が変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DetailSettingsComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            ComboBox comboBox = (ComboBox)sender;
            FieldInfo field = typeof(DetailSettings).GetField((string)comboBox.Tag);
            if (field != null)
            {
                int? index = comboBox.SelectedIndex;
                field.SetValue(checkItemOption.detailSettings, index != 0 ? index : null);
            }
        }

        /// <summary>
        /// カスタム設定ボタンが押下されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CustomSettingsButton_Click(object sender, EventArgs e)
        {
            CustomListForm form = new CustomListForm(checkItemOption.customValues);
            if (form.ShowDialog() == DialogResult.OK)
            {
                checkItemOption.customValues = form.customValues;
            }
        }

        /// <summary>
        /// コンボボックス選択時にマウスホイール操作禁止
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ComboBox_MouseWheel(object sender, MouseEventArgs e)
        {
            ((HandledMouseEventArgs)e).Handled = true;
        }

        /// <summary>
        /// パネルの表示／非表示の切り替え
        /// </summary>
        /// <param name="control">コントロール</param>
        /// <param name="index">種別のインデックス</param>
        private void PanelVisibleControl(Control control, int index)
        {
            XElement xmlData = GetXmlData("DisplayControl");
            XElement elem = xmlData.Descendants("Value").FirstOrDefault(e => (string)e.Attribute("key") == control.Name);
            if (index >= 0 && elem != null)
            {
                control.Visible = ((string)elem.Attribute("flag")).Substring(index, 1) == "1";
            }

            foreach (Control child in control.Controls)
            {
                PanelVisibleControl(child, index);
            }
        }

        /// <summary>
        /// コンボボックスデータの作成
        /// </summary>
        /// <param name="control">コントロール</param>
        /// <param name="xmlData">XMLデータ</param>
        /// <param name="index">種別のインデックス</param>
        private void ComboBoxDataControl(Control control, XElement xmlData, int index)
        {
            if (control is ComboBox comboBox)
            {
                comboBox.DataSource = null;

                List<Item> items = xmlData.Descendants("Value")
                    .Where(v =>
                    {
                        string str = (string)v.Attribute("flag");
                        return str == null || str.Substring(index, 1) == "1";
                    })
                    .Select(v => new Item((int)v.Attribute("id"), LocaleUtil.GetString((string)v))).ToList();
                comboBox.DataSource = items;
                comboBox.DisplayMember = "value";
                comboBox.ValueMember = "id";
            }
        }

        /// <summary>
        /// サイズ変更時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CheckItemsControl_Resize(object sender, EventArgs e)
        {
            ResizeControl();
        }

        /// <summary>
        /// 作業ウィンドウ内の要素のサイズ変更
        /// </summary>
        private void ResizeControl()
        {
            int totalHeight = 0;
            foreach (Control control in flowLayoutPanel.Controls)
            {
                if (control.Visible)
                {
                    totalHeight += control.Height + control.Margin.Top + control.Margin.Bottom;
                }
            }
            flowLayoutPanel.Size = new Size(Width - flowLayoutPanel.Margin.Right, totalHeight);

            foreach (Control control in flowLayoutPanel.Controls)
            {
                if (control is Panel panel)
                {
                    panel.Width = flowLayoutPanel.ClientSize.Width - panel.Margin.Left - panel.Margin.Right;
                }
            }
        }

        /// <summary>
        /// タグを変更する。
        /// </summary>
        public void ChangeTag()
        {
            if (this.checkItemsNode == null || this.baseCell == null)
            {
                // 未初期化の場合
                return;
            }

            string tag = ExcelUtils.GetCellStringNonBlank("tag", this.checkItemsNode, activeSheet, this.baseCell).value;
            if (!String.IsNullOrEmpty(tag) &&  this.activeTag != tag) 
            {
                // タグが変更された場合
                Globals.ThisAddIn.FileRoster.ChangeTag(app.ActiveWorkbook, activeTag, tag, this.checkItemOption);
            }
        }
    }
}
