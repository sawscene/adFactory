using ExcelImport;
using NLog;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using System.Xml;
using Excel = Microsoft.Office.Interop.Excel;

namespace adWorkbookAddIn.Source
{
    public partial class DrawingSupportControl : UserControl
    {
        private static readonly Logger logger = LogManager.GetCurrentClassLogger();

        private static Excel.Workbook imagesWorkbook;
        private const string fontFamily = "Meiryo UI";
        private const float fontSize = 9;
        private const int GROUP_BOX_TOP_MARGIN = 10;
        private const int BUTTON_HEIGHT = 30;
        private const int BUTTON_SPACING = 10;
        private const int GROUP_BOX_SPACING = 20;
        private const int MARGIN = 20; // 左右の余白
        private const int BUTTON_LEFT_MARGIN = 10;
        private const int BUTTON_TOP_MARGIN = 20; // グループボックス内の余白
        private int groupBoxTop;
        private Dictionary<string, GroupBox> groupBoxes;

        private readonly int copyRetryCount;
        private readonly int pasteRetryCount;

        public DrawingSupportControl()
        {
            InitializeComponent();
            try
            {
                XmlDocument excelFormat = Globals.ThisAddIn.ExcelFormat;
                XmlNode copyRetryCountNode = excelFormat.DocumentElement.SelectSingleNode("/root/drawingSupport/copyRetryCount");
                XmlNode pasteRetryCountNode = excelFormat.DocumentElement.SelectSingleNode("/root/drawingSupport/pasteRetryCount");
                copyRetryCount = int.Parse(copyRetryCountNode.Attributes.GetNamedItem("count").Value);
                pasteRetryCount = int.Parse(pasteRetryCountNode.Attributes.GetNamedItem("count").Value);
            }
            catch (Exception ex)
            {
                // 読み取れなかった場合は値を'3'に設定（デフォルト値）
                copyRetryCount = 3;
                pasteRetryCount = 3;
                logger.Error(ex);
                return;
            }
        }

        /// <summary>
        /// フォームが閉じるときの処理
        /// </summary>
        public static void DesignSupportControl_ControlClosing()
        {
            // フォームが閉じられるときに、非表示のブックを閉じる
            if (imagesWorkbook != null)
            {
                imagesWorkbook.Close(false);
                imagesWorkbook = null;
            }
        }

        /// <summary>
        /// 作業ウィンドウ読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DesignSupportControl_Load(object sender, EventArgs e)
        {
            InitializeExcelData();
        }

        /// <summary>
        /// データの初期化
        /// </summary>
        private void InitializeExcelData()
        {
            // Excelアプリケーションの取得
            Excel.Application excelApp = Globals.ThisAddIn.Application;

            try
            {
                if (imagesWorkbook == null)
                {
                    string workbookFolderPath = Path.Combine(Path.GetTempPath(), "adWorkbook");
                    if (!Directory.Exists(workbookFolderPath))
                    {
                        // なければ作成
                        Directory.CreateDirectory(workbookFolderPath);
                    }
                    else
                    {
                        // あればファイルを削除
                        DeleteAllFilesInDirectory(workbookFolderPath);
                    }

                    // 作図支援ファイルのコピーを作成
                    Guid uuid = Guid.NewGuid();
                    string workbookPath = Path.Combine(Globals.ThisAddIn.AdFactoryHome, "adWorkbook", "conf", "作図支援.xlsx");
                    string destFilePath = Path.Combine(workbookFolderPath, "作図支援_" + uuid + ".xlsx");
                    File.Copy(workbookPath, destFilePath, overwrite: true);

                    // コピーしたファイルを非表示で開く
                    imagesWorkbook = excelApp.Workbooks.Open(destFilePath, ReadOnly: true);
                    imagesWorkbook.Windows[1].Visible = false;
                }
            }
            catch (Exception ex)
            {
                logger.Error(ex);
                MessageBox.Show(
                    LocaleUtil.GetString("key.readConfigFileFailed"),
                    LocaleUtil.GetString("key.drawingSupport"),
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            // listシートからデータを読み込み、グループボックスとボタンを作成
            Excel.Worksheet listSheet = imagesWorkbook.Sheets["list"];
            Excel.Range usedRange = listSheet.UsedRange;
            groupBoxes = new Dictionary<string, GroupBox>();

            for (int row = 2; row <= usedRange.Rows.Count; row++)
            {
                string groupName = usedRange.Cells[row, 1].Text;
                string buttonText = usedRange.Cells[row, 2].Text;
                string shapeName = usedRange.Cells[row, 3].Text;
                Excel.Range cell = usedRange.Cells[row, 2];
                int backgroundColor = (int)(double)cell.Interior.Color;
                int fontColor = (int)(double)cell.Font.Color;
                CreateButton(groupName, buttonText, shapeName, backgroundColor, fontColor);
            }

            ArrangeGroupBoxes();

            this.Resize += new EventHandler(this.DesignSupportControl_Resize);
        }

        /// <summary>
        /// 指定のディレクトリ内のファイルを削除する
        /// </summary>
        /// <param name="directoryPath">ディレクトリ</param>
        public static void DeleteAllFilesInDirectory(string directoryPath)
        {
            // フォルダ内のファイルを取得
            string[] files = Directory.GetFiles(directoryPath);

            // 各ファイルを削除
            foreach (var file in files)
            {
                try
                {
                    File.Delete(file);
                }
                catch (Exception ex)
                {
                    logger.Error(ex);
                }
            }
        }

        /// <summary>
        /// ボタン作成
        /// </summary>
        /// <param name="groupName">グループ名</param>
        /// <param name="buttonText">ボタンのラベル</param>
        /// <param name="shapeName">図形名</param>
        /// <param name="backgroundColor">背景色</param>
        /// <param name="fontColor">文字色</param>
        private void CreateButton(string groupName, string buttonText, string shapeName, int backgroundColor, int fontColor)
        {
            // グループボックスが存在しない場合は作成
            if (!groupBoxes.ContainsKey(groupName))
            {
                GroupBox groupBox = new GroupBox();
                groupBox.Font = new Font(new FontFamily(fontFamily), fontSize);
                groupBox.ForeColor = Color.White;
                groupBox.BackColor = Color.Transparent;
                groupBox.Text = groupName;
                groupBox.AutoSize = true;
                groupBox.AutoSizeMode = AutoSizeMode.GrowAndShrink;
                groupBox.Top = groupBoxTop;
                groupBox.Left = MARGIN;
                groupBox.MinimumSize = new Size(CalculateLabelWidth(groupBox.Text, groupBox.Font) + BUTTON_LEFT_MARGIN, 0);
                this.Controls.Add(groupBox);
                groupBoxes[groupName] = groupBox;

                // 次のグループボックスの位置を計算
                groupBoxTop += groupBox.Height + GROUP_BOX_SPACING;
            }

            // ボタンを作成
            Button btn = new Button();
            btn.Font = new Font(new FontFamily(fontFamily), fontSize);
            btn.Name = "btn";
            btn.Text = buttonText;
            btn.Tag = shapeName;
            btn.Height = BUTTON_HEIGHT;
            btn.AutoSize = true;

            // 背景色を設定
            btn.BackColor = ColorTranslator.FromOle(backgroundColor);
            // 文字色を設定
            btn.ForeColor = ColorTranslator.FromOle(fontColor);

            // ボタンの幅を文字の長さに応じて設定
            using (Graphics g = btn.CreateGraphics())
            {
                SizeF size = g.MeasureString(btn.Text, btn.Font);
                btn.Width = (int)size.Width + 20; // 余白分を追加
            }

            btn.Click += Btn_Click;

            // ボタンをグループボックスに追加
            groupBoxes[groupName].Controls.Add(btn);
            LogUtils.AttachLoggingToControl(btn);
        }

        /// <summary>
        /// テキストとフォントからテキストの横幅を取得する
        /// </summary>
        /// <param name="labelText">テキスト</param>
        /// <param name="font">フォント</param>
        /// <returns>テキストの横幅</returns>
        public int CalculateLabelWidth(string labelText, Font font)
        {
            using (Graphics g = Graphics.FromHwnd(IntPtr.Zero))
            {
                SizeF size = g.MeasureString(labelText, font);
                return (int)size.Width;
            }
        }

        /// <summary>
        /// グループボックスの作成
        /// </summary>
        private void ArrangeGroupBoxes()
        {
            // 現在のスクロール位置を保存
            Point scrollPosition = this.AutoScrollPosition;
            scrollPosition = new Point(-scrollPosition.X, -scrollPosition.Y);

            this.SuspendLayout(); // レイアウトの一時停止

            groupBoxTop = GROUP_BOX_TOP_MARGIN;
            int formWidth = this.ClientSize.Width - 2 * MARGIN;

            foreach (var groupBox in groupBoxes.Values)
            {
                int buttonLeft = BUTTON_LEFT_MARGIN;
                int buttonTop = BUTTON_TOP_MARGIN; // グループボックス内の余白

                groupBox.Width = formWidth; // フォームの幅に合わせる

                foreach (Control control in groupBox.Controls)
                {
                    if (control is Button btn)
                    {
                        // ボタンの配置位置を設定
                        if (buttonLeft + btn.Width > groupBox.Width - 20) // グループボックスの右端を超える場合は改行
                        {
                            buttonLeft = BUTTON_LEFT_MARGIN;
                            buttonTop += BUTTON_HEIGHT + BUTTON_SPACING;
                        }

                        btn.Left = buttonLeft;
                        btn.Top = buttonTop;

                        // 次のボタンの配置位置を計算
                        buttonLeft += btn.Width + BUTTON_SPACING;
                    }
                }

                // グループボックスの高さを調整
                groupBox.Height = buttonTop + BUTTON_HEIGHT + 10; // ボトム余白を追加

                groupBox.Top = groupBoxTop;
                groupBoxTop += groupBox.Height + GROUP_BOX_SPACING;
            }

            this.AutoScrollMinSize = new Size(0, groupBoxTop); // フォームのスクロール範囲を設定

            this.ResumeLayout(); // レイアウトの再開

            // 保存したスクロール位置を復元
            this.VerticalScroll.Value = Math.Max(0, scrollPosition.Y);
        }

        /// <summary>
        /// ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void Btn_Click(object sender, EventArgs e)
        {
            Button btn = sender as Button;
            string shapeName = btn.Tag as string;
            bool copySuccessful = false;
            bool pasteSuccessful = false;

            // 図形をコピー
            for (int i = 0; i < copyRetryCount; i++)
            {
                try
                {
                    CopyShape(shapeName);
                    copySuccessful = true;
                    break;
                }
                catch (Exception ex)
                {
                    logger.Fatal(ex);
                }
            }
            if (!copySuccessful)
            {
                MessageBox.Show(
                    LocaleUtil.GetString("key.createDiagramFailed"),
                    LocaleUtil.GetString("key.drawingSupport"),
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            // アクティブなシートにペースト
            for (int i = 0; i < pasteRetryCount; i++)
            {
                try
                {
                    PasteShapeToActiveSheet();
                    pasteSuccessful = true;
                    break;
                }
                catch (Exception ex)
                {
                    logger.Fatal(ex);
                }
            }
            if (!pasteSuccessful)
            {
                MessageBox.Show(
                    LocaleUtil.GetString("key.createDiagramFailed"),
                    LocaleUtil.GetString("key.drawingSupport"),
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
        }

        /// <summary>
        /// 図形のコピーする
        /// </summary>
        /// <param name="shapeName">図形の名前</param>
        private void CopyShape(string shapeName)
        {
            Excel.Worksheet materialsSheet = imagesWorkbook.Sheets["materials"];
            Excel.Shape shape = materialsSheet.Shapes.Item(shapeName);
            Application.DoEvents();
            shape.Copy();
        }

        /// <summary>
        /// アクティブなシートに図形をペーストする
        /// </summary>
        private void PasteShapeToActiveSheet()
        {
            Excel.Application excelApp = Globals.ThisAddIn.Application;
            Excel.Workbook currentWorkbook = excelApp.ActiveWorkbook;
            Excel.Worksheet currentSheet = currentWorkbook.ActiveSheet;
            Excel.Range currentCell = currentWorkbook.Application.ActiveCell;

            Application.DoEvents();
            currentSheet.Paste();

            Excel.Shape newShape = currentSheet.Shapes.Item(currentSheet.Shapes.Count);
            newShape.Left = (float)currentCell.Left;
            newShape.Top = (float)currentCell.Top;
        }

        /// <summary>
        /// 作業ウィンドウのサイズが変更されたときの処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DesignSupportControl_Resize(object sender, EventArgs e)
        {
            ArrangeGroupBoxes();
        }
    }
}
