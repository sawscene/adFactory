using ExcelImport;
using System;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using System.Xml;
using Excel = Microsoft.Office.Interop.Excel;

namespace adWorkbookAddIn.Source
{
    public partial class WorkControl : UserControl
    {
        private readonly Excel.Application app = Globals.ThisAddIn.Application;
        private readonly XmlDocument excelFormat = Globals.ThisAddIn.ExcelFormat;
        private readonly XmlNode workNode;

        private WorkOption workOption;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public WorkControl()
        {
            InitializeComponent();

            workNode = excelFormat.DocumentElement.SelectSingleNode("/root/work");

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// 作業ウィンドウの初期化
        /// </summary>
        public void InitializeTaskPaneData()
        {
            try
            {
                Utils.SuspendLayout(this);

                Excel.Workbook activeBook = app.ActiveWorkbook;
                Excel.Worksheet activeSheet = app.ActiveSheet;

                workName.Text = ExcelUtils.GetCellString("workName", workNode, activeSheet, null).value;
                rev.Text = ExcelUtils.GetCellString("rev", workNode, activeSheet, null).value;
                workNo.Text = ExcelUtils.GetCellString("workNo", workNode, activeSheet, null).value;
                taktTime.Text = ExcelUtils.GetCellString("taktTime", workNode, activeSheet, null).value;

                workOption = Globals.ThisAddIn.FileRoster.GetWorkOption(activeBook, activeSheet, true);

                equipments.Text = string.Join(", ", workOption.equipments.Select(v => v.name).ToList());
                if (equipments.Text == "")
                {
                    equipments.Text = LocaleUtil.GetString("key.noOrganizationTerminalConfigured");
                }

                organizations.Text = string.Join(", ", workOption.organizations.Select(v => v.name).ToList());
                if (organizations.Text == "")
                {
                    organizations.Text = LocaleUtil.GetString("key.noOrganizationConfigured");
                }

                backgroundColor.BackColor = ColorTranslator.FromHtml(workOption.backgroundColor);
                fontColor.BackColor = ColorTranslator.FromHtml(workOption.fontColor);

                Utils.ResumeLayout(this, false);
            }
            catch (Exception ex)
            {
                MessageBox.Show(
                   LocaleUtil.GetString("AdditionalOptionForm.createDataFailed"),
                   LocaleUtil.GetString("key.error"));
                throw ex;
            }
        }

        /// <summary>
        /// 背景色ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void BackgroundColorButton_Click(object sender, EventArgs e)
        {
            backgroundColorDialog.Color = ColorTranslator.FromHtml(workOption.backgroundColor);
            if (backgroundColorDialog.ShowDialog() == DialogResult.OK)
            {
                Color selectedColor = backgroundColorDialog.Color;
                backgroundColor.BackColor = selectedColor;
                workOption.backgroundColor = $"#{selectedColor.R:X2}{selectedColor.G:X2}{selectedColor.B:X2}";
            }
        }

        /// <summary>
        /// 文字色ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void FontColorButton_Click(object sender, EventArgs e)
        {
            fontColorDialog.Color = ColorTranslator.FromHtml(workOption.fontColor);
            if (fontColorDialog.ShowDialog() == DialogResult.OK)
            {
                Color selectedColor = fontColorDialog.Color;
                fontColor.BackColor = selectedColor;
                workOption.fontColor = $"#{selectedColor.R:X2}{selectedColor.G:X2}{selectedColor.B:X2}";
            }
        }

        /// <summary>
        /// 作業者端末ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void EquipmentsButton_Click(object sender, EventArgs e)
        {
            EquipmentsForm form = new EquipmentsForm(workOption.equipments);
            if (form.ShowDialog() == DialogResult.OK)
            {
                workOption.equipments = form.Equipments;
                equipments.Text = string.Join(", ", form.Equipments.Select(v => v.name).ToList());
                if (equipments.Text == "")
                {
                    equipments.Text = LocaleUtil.GetString("key.noOrganizationTerminalConfigured");
                }
            }
        }

        /// <summary>
        /// 作業者ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OrganizationsButton_Click(object sender, EventArgs e)
        {
            OrganizationsForm form = new OrganizationsForm(workOption.organizations);
            if (form.ShowDialog() == DialogResult.OK)
            {
                workOption.organizations = form.Organizations;
                organizations.Text = string.Join(", ", form.Organizations.Select(v => v.name).ToList());
                if (organizations.Text == "")
                {
                    organizations.Text = LocaleUtil.GetString("key.noOrganizationConfigured");
                }
            }
        }

        /// <summary>
        /// 表示項目ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DisplayItemsButton_Click(object sender, EventArgs e)
        {
            DisplayItemsForm form = new DisplayItemsForm(workOption.displayItems);
            if (form.ShowDialog() == DialogResult.OK)
            {
                workOption.displayItems = form.DisplayItems;
            }
        }

        /// <summary>
        /// 追加情報ボタン押下時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OptionButton_Click(object sender, EventArgs e)
        {
            AdditionalOptionForm form = new AdditionalOptionForm(workOption.additionalOptions);
            if (form.ShowDialog() == DialogResult.OK)
            {
                workOption.additionalOptions = form.AdditionalOptions;
            }
        }

        /// <summary>
        /// 作業ウィンドウのサイズ変更時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void WorkControl_Resize(object sender, EventArgs e)
        {
            ResizeControl();
        }

        /// <summary>
        /// 作業ウィンドウのサイズ変更処理
        /// </summary>
        private void ResizeControl()
        {
            int totalHeight = 0;
            foreach (Control control in flowLayoutPanel.Controls)
            {
                totalHeight += control.Height + control.Margin.Top + control.Margin.Bottom;
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
    }
}
