using ExcelImport;
using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.Xml;
using Excel = Microsoft.Office.Interop.Excel;

namespace adWorkbookAddIn.Source
{
    class WorkProcedureService
    {
        private readonly Excel.Application app;
        private readonly XmlDocument excelFormat;
        private readonly XmlNode workProcedureNode;
        private readonly int next;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public WorkProcedureService()
        {
            app = Globals.ThisAddIn.Application;
            excelFormat = Globals.ThisAddIn.ExcelFormat;

            workProcedureNode = excelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure");
            next = int.Parse(workProcedureNode.Attributes.GetNamedItem("next").Value);
        }

        /// <summary>
        /// 作業手順名取得
        /// </summary>
        /// <param name="activeSheet">アクティブなシート</param>
        /// <returns>作業手順名のリスト</returns>
        public List<string> GetWorkProcedureNames(Excel.Worksheet activeSheet)
        {
            CellIterator workProcedureCells = new CellIterator(null, workProcedureNode);
            List<string> workProcedureNames = new List<string>();

            // 作業手順名を取得
            while (workProcedureCells.HasNext(activeSheet))
            {
                workProcedureNames.Add(ExcelUtils.GetCellString("workProcedureName", workProcedureNode, activeSheet, workProcedureCells.GetCell()).value);
                workProcedureCells.Next(activeSheet);
            }
            return workProcedureNames;
        }

        /// <summary>
        /// 作業手順No取得
        /// </summary>
        /// <param name="activeSheet">アクティブなシート</param>
        /// <returns>作業手順Noのリスト</returns>
        public List<int?> GetWorkProcedureNo(Excel.Worksheet activeSheet)
        {
            CellIterator workProcedureCells = new CellIterator(null, workProcedureNode);
            List<int?> workProcedureNo = new List<int?>();

            // 作業手順名を取得
            while (workProcedureCells.HasNext(activeSheet))
            {
                try
                {
                    int no = int.Parse(ExcelUtils.GetCellString("workProcedureNo", workProcedureNode, activeSheet, workProcedureCells.GetCell()).value);
                    workProcedureNo.Add(no);
                }
                catch (Exception)
                {
                    workProcedureNo.Add(null);
                }
                workProcedureCells.Next(activeSheet);
            }
            return workProcedureNo;
        }

        /// <summary>
        /// 作業手順追加
        /// </summary>
        /// <param name="destSheet">追加先シート</param>
        /// <param name="destIndex">追加先インデックス</param>
        /// <param name="procedureName">作業手順名</param>
        public void AddWorkProcedure(Excel.Worksheet destSheet, int destIndex, string procedureName)
        {
            Excel.Workbook workbook = app.ActiveWorkbook;
            CellIterator cells = new CellIterator(null, workProcedureNode);
            XmlNode templateNode = excelFormat.DocumentElement.SelectSingleNode("/root/template");

            // テンプレートシートを取得
            string tempSheetName = templateNode.Attributes.GetNamedItem("sheet").Value;
            Excel.Worksheet tempSheet;
            try
            {
                tempSheet = workbook.Sheets[tempSheetName] as Excel.Worksheet;
            }
            catch (COMException ex)
            {
                MessageBox.Show(
                    LocaleUtil.GetString("WorkProcedureForm.notFoundTemplate", tempSheetName),
                    LocaleUtil.GetString("key.error"),
                    MessageBoxButtons.OK, MessageBoxIcon.Warning);
                throw ex;
            }

            // テンプレートシートから空の作業手順をコピー
            int startRow = tempSheet.Range[cells.GetCell().ToString()].Row;
            Excel.Range srcRange = tempSheet.Range[$"{startRow}:{startRow + next - 1}"];
            srcRange.Copy();

            // 指定された位置に挿入
            MoveBaseCell(destSheet, cells, destIndex);
            int insertRow = destSheet.Range[cells.GetCell().ToString()].Row;
            destSheet.Rows[insertRow].Insert(Excel.XlInsertShiftDirection.xlShiftDown);

            // 作業手順名の設定
            string procedureNameCell = ExcelUtils.GetCell("workProcedureName", workProcedureNode, cells.GetCell()).ToString();
            destSheet.Range[procedureNameCell].Value = procedureName;

            // クリップボードの内容を削除
            workbook.Application.CutCopyMode = Excel.XlCutCopyMode.xlCopy;

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(workbook);
            Marshal.ReleaseComObject(tempSheet);
            Marshal.ReleaseComObject(srcRange);
        }

        /// <summary>
        /// 作業手順移動
        /// </summary>
        /// <param name="sheet">アクティブなシート</param>
        /// <param name="srcIndex">移動元インデックス</param>
        /// <param name="destIndex">移動先インデックス</param>
        public void MoveWorkProcedure(Excel.Worksheet sheet, int srcIndex, int destIndex)
        {
            CellIterator srcCells = new CellIterator(null, workProcedureNode);
            CellIterator destCells = new CellIterator(null, workProcedureNode);

            // 移動元の作業手順をコピー
            MoveBaseCell(sheet, srcCells, srcIndex);
            int startRow = sheet.Range[srcCells.GetCell().ToString()].Row;
            Excel.Range srcRange = sheet.Range[$"{startRow}:{startRow + next - 1}"];
            srcRange.Copy();

            // 移動先の作業手順の前に挿入
            MoveBaseCell(sheet, destCells, destIndex);
            int insertRow = sheet.Range[destCells.GetCell().ToString()].Row;
            sheet.Rows[insertRow].Insert(Excel.XlInsertShiftDirection.xlShiftDown);

            if (destIndex <= srcIndex)
            {
                // 参照を1つずらす（前にコピーを挿入したため）
                srcCells.Next(sheet);
                startRow = sheet.Range[srcCells.GetCell().ToString()].Row;
                srcRange = sheet.Range[$"{startRow}:{startRow + next - 1}"];
            }

            // 移動元の作業手順を削除
            DeleteShapesInRange(srcRange);
            srcRange.Delete(Excel.XlDeleteShiftDirection.xlShiftUp);

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(srcRange);
        }

        /// <summary>
        /// 作業手順コピー
        /// </summary>
        /// <param name="srcSheet">コピー元シート</param>
        /// <param name="destSheet">コピー先シート</param>
        /// <param name="srcIndex">コピー元インデクス</param>
        /// <param name="destIndex">コピー先インデクス</param>
        /// <param name="procedureName">作業手順名</param>
        public void CopyWorkProcedure(Excel.Worksheet srcSheet, Excel.Worksheet destSheet, int srcIndex, int destIndex, string procedureName)
        {
            CellIterator srcCells = new CellIterator(null, workProcedureNode);
            CellIterator destCells = new CellIterator(null, workProcedureNode);

            // 選択シートから既存の作業手順をコピー
            MoveBaseCell(srcSheet, srcCells, srcIndex);
            int startRow = srcSheet.Range[srcCells.GetCell().ToString()].Row;
            Excel.Range srcRange = srcSheet.Range[$"{startRow}:{startRow + next - 1}"];

            // 指定された位置に挿入
            MoveBaseCell(destSheet, destCells, destIndex);
            int insertRow = destSheet.Range[destCells.GetCell().ToString()].Row;
            Excel.Range destRange = destSheet.Rows[insertRow];
            for (int i = 1; i <= srcRange.Rows.Count; i++)
            {
                // 空の行を挿入
                destRange.Insert(Excel.XlInsertShiftDirection.xlShiftDown);
            }

            // セルのコピー
            srcRange.Copy();
            destSheet.Rows[insertRow].PasteSpecial();

            // クリップボードの内容を削除
            Excel.Workbook workbook = app.ActiveWorkbook;
            workbook.Application.CutCopyMode = Excel.XlCutCopyMode.xlCopy;

            Excel.Shapes shapes = srcSheet.Shapes;
            foreach (Excel.Shape shape in shapes)
            {
                if (shape.TopLeftCell.Row >= srcRange.Row &&
                    shape.TopLeftCell.Row <= srcRange.Row + srcRange.Rows.Count - 1 &&
                    shape.TopLeftCell.Column >= srcRange.Column &&
                    shape.TopLeftCell.Column <= srcRange.Column + srcRange.Columns.Count - 1)
                {
                    // 画像のコピー
                    shape.Copy();
                    destSheet.Paste();

                    // 位置の設定
                    Excel.Shape pastedShape = destSheet.Shapes.Item(destSheet.Shapes.Count);
                    float relativeTop = (float)(shape.Top - srcRange.Top);
                    pastedShape.Top = (float)destSheet.Rows[insertRow].Top + relativeTop;
                    pastedShape.Left = shape.Left;

                    // COMオブジェクトの解放
                    Marshal.ReleaseComObject(shape);
                    Marshal.ReleaseComObject(pastedShape);
                }
            }

            // 作業手順名の設定
            string procedureNameCell = ExcelUtils.GetCell("workProcedureName", workProcedureNode, destCells.GetCell()).ToString();
            destSheet.Range[procedureNameCell].Value = procedureName;

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(srcRange);
            Marshal.ReleaseComObject(destRange);
            Marshal.ReleaseComObject(workbook);
            Marshal.ReleaseComObject(shapes);
        }

        /// <summary>
        /// 作業手順削除
        /// </summary>
        /// <param name="sheet">アクティブなシート</param>
        /// <param name="deleteIndex">削除対象インデックス</param>
        public void DeleteWorkProcedure(Excel.Worksheet sheet, int deleteIndex)
        {
            CellIterator cells = new CellIterator(null, workProcedureNode);

            // 削除対象のセルを取得
            MoveBaseCell(sheet, cells, deleteIndex);
            int startRow = sheet.Range[cells.GetCell().ToString()].Row;
            Excel.Range deleteRange = sheet.Range[$"{startRow}:{startRow + next - 1}"];

            // 削除を実行
            DeleteShapesInRange(deleteRange);
            deleteRange.Delete(Excel.XlDeleteShiftDirection.xlShiftUp);

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(deleteRange);
        }

        /// <summary>
        /// 作業手順置換（別シート間）
        /// </summary>
        /// <param name="srcSheet">コピー元シート</param>
        /// <param name="destSheet">コピー先シート</param>
        /// <param name="srcIndex">コピー元インデックス</param>
        /// <param name="destIndex">コピー先インデックス</param>
        public void ReplaceWorkProcedure(Excel.Worksheet srcSheet, Excel.Worksheet destSheet, int srcIndex, int destIndex)
        {
            CellIterator srcCells = new CellIterator(null, workProcedureNode);
            CellIterator destCells = new CellIterator(null, workProcedureNode);

            // アクティブシートの作業手順を削除
            int startRow = destSheet.Range[destCells.GetCell().ToString()].Row;
            MoveBaseCell(destSheet, destCells, destIndex);
            int endRow = destSheet.Range[destCells.GetCell().ToString()].Row + next - 1;
            Excel.Range deleteRange = destSheet.Range[$"{startRow}:{endRow}"];
            DeleteShapesInRange(deleteRange);
            deleteRange.Delete(Excel.XlDeleteShiftDirection.xlShiftUp);

            // 操作後の作業手順をコピー
            MoveBaseCell(srcSheet, srcCells, srcIndex);
            endRow = srcSheet.Range[srcCells.GetCell().ToString()].Row + next - 1;
            Excel.Range srcRange = srcSheet.Range[$"{startRow}:{endRow}"];
            Excel.Range destRange = destSheet.Range[$"{startRow}:{endRow}"];
            for (int i = 1; i <= srcRange.Rows.Count; i++)
            {
                // セルの高さの設定
                destRange.Rows[i].RowHeight = srcRange.Rows[i].RowHeight;
            }
            srcRange.Copy();
            destSheet.Rows[startRow].Select();
            destSheet.Paste();

            // クリップボードの内容を削除
            Excel.Workbook workbook = app.ActiveWorkbook;
            workbook.Application.CutCopyMode = Excel.XlCutCopyMode.xlCopy;

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(deleteRange);
            Marshal.ReleaseComObject(srcRange);
            Marshal.ReleaseComObject(workbook);
        }

        /// <summary>
        /// 範囲内の図形削除
        /// </summary>
        /// <param name="range">削除範囲</param>
        private void DeleteShapesInRange(Excel.Range range)
        {
            Excel.Shapes shapes = range.Worksheet.Shapes;

            // リバースループで削除（削除する際にインデックスがずれるのを防ぐため）
            for (int i = shapes.Count; i > 0; i--)
            {
                Excel.Shape shape = shapes.Item(i);

                // 図形の位置が指定の範囲内かどうかをチェック
                if (shape.TopLeftCell.Row >= range.Row &&
                    shape.TopLeftCell.Row <= range.Row + range.Rows.Count - 1 &&
                    shape.TopLeftCell.Column >= range.Column &&
                    shape.TopLeftCell.Column <= range.Column + range.Columns.Count - 1)
                {
                    shape.Delete();
                }
            }

            // COMオブジェクトの解放
            Marshal.ReleaseComObject(shapes);
        }

        /// <summary>
        /// ベースセルの移動
        /// </summary>
        /// <param name="sheet">移動対象シート</param>
        /// <param name="cells">セルイテレータ</param>
        /// <param name="count">移動回数</param>
        private void MoveBaseCell(Excel.Worksheet sheet, CellIterator cells, int count)
        {
            for (int i = 0; i < count; i++)
            {
                cells.Next(sheet);
            }
        }
    }
}
