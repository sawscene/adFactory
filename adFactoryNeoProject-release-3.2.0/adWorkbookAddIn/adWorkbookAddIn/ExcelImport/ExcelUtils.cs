using adWorkbookAddIn.Source;
using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using System.Xml;
using Excel = Microsoft.Office.Interop.Excel;
using NLog;

namespace ExcelImport
{
    class ExcelUtils
	{
		private static readonly Logger logger = LogManager.GetCurrentClassLogger();

		public static ExcelCell GetCell(string fieldName, XmlNode baseNode, ExcelCell baseCell)
		{
			XmlNode node = baseNode.SelectSingleNode(fieldName);
			CellIterator cells = new CellIterator(baseCell, node);
			ExcelCell cell = cells.GetCell();
			return cell;
		}

		public static string GetCellString(Excel.Worksheet sheet, ExcelCell cell)
		{
			Excel.Range range = sheet.Cells[cell.row, cell.column];
			try
			{
				bool isMerged = range.MergeCells;
				if (!isMerged)
				{
					return range.Text;
				}
				else
				{
					Excel.Range range2 = range.MergeArea;
					Excel.Range range3 = range2.Cells[1, 1];
					try
					{
						return range3.Text;
					}
					finally
					{
						Marshal.ReleaseComObject(range3);
						Marshal.ReleaseComObject(range2);
					}
				}
			}
			catch (Exception)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.GetCellFailed"), sheet.Name, cell);
			}
			finally
			{
				Marshal.ReleaseComObject(range);
			}
		}

		public static CellValue<string> GetCellString(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			ExcelCell cell = GetCell(fieldName, baseNode, baseCell);
			return new CellValue<string> { sheetName = sheet.Name, cell = cell, value = GetCellString(sheet, cell) };
		}

		public static CellValue<string> GetCellStringNonBlank(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			CellValue<string> str = GetCellString(fieldName, baseNode, sheet, baseCell);
			if (string.IsNullOrWhiteSpace(str.value))
			{
				str.value = null;
			}
			return str;
		}

		public static CellValue<int?> GetCellInt(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			CellValue<string> str = GetCellString(fieldName, baseNode, sheet, baseCell);
			if (string.IsNullOrWhiteSpace(str.value))
			{
				return new CellValue<int?> { sheetName = str.sheetName, cell = str.cell };
			}

			if (!int.TryParse(str.value, out int value))
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.Integer"), str.sheetName, str.cell);
			}

			return new CellValue<int?> { sheetName = str.sheetName, cell = str.cell, value = value };
		}

		public static CellValue<double?> GetCellDouble(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			CellValue<string> str = GetCellString(fieldName, baseNode, sheet, baseCell);
			if (string.IsNullOrWhiteSpace(str.value))
			{
				return new CellValue<double?> { sheetName = str.sheetName, cell = str.cell };
			}

			if (!double.TryParse(str.value, out double value))
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.Number"), str.sheetName, str.cell);
			}

			return new CellValue<double?> { sheetName = str.sheetName, cell = str.cell, value = value };
		}

		public static CellValue<Type> GetCellEnum<Type>(string fieldName, Dictionary<string, Type> options, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			CellValue<string> str = GetCellStringNonBlank(fieldName, baseNode, sheet, baseCell);
			if (str.value == null)
			{
				return new CellValue<Type> { sheetName = str.sheetName, cell = str.cell, value = default };
			}

			if (!options.ContainsKey(str.value))
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.Enum", string.Join(", ", options.Keys)), str.sheetName, str.cell);
			}
			return new CellValue<Type> { sheetName = str.sheetName, cell = str.cell, value = options[str.value] };
		}

		public static string ColorToString(int value)
		{
			value = ((value % 0x100) << 16) + (((value >> 8) % 0x100) << 8) + ((value >> 16) % 0x100);
			return "#" + value.ToString("X6");
		}

		public static CellValue<string> GetCellColor(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			ExcelCell cell = GetCell(fieldName, baseNode, baseCell);
			Excel.Range range = sheet.Cells[cell.row, cell.column];
			try
			{
				Excel.Interior interior = range.Interior;
				try
				{
					int value = (int)interior.Color;
					return new CellValue<string> { sheetName = sheet.Name, cell = cell, value = ColorToString(value) };
				}
				finally
				{
					Marshal.ReleaseComObject(interior);
				}
			}
			catch (Exception)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.GetCellFailed"), sheet.Name, cell);
			}
			finally
			{
				Marshal.ReleaseComObject(range);
			}
		}

		public static CellValue<string> GetTextColor(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			ExcelCell cell = GetCell(fieldName, baseNode, baseCell);
			Excel.Range range = sheet.Cells[cell.row, cell.column];
			try
			{
				Microsoft.Office.Interop.Excel.Font font = range.Font;
				try
				{
					int value = (int)font.Color;
					return new CellValue<string> { sheetName = sheet.Name, cell = cell, value = ColorToString(value) };
				}
				finally
				{
					Marshal.ReleaseComObject(font);
				}
			}
			catch (Exception)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.GetCellFailed"), sheet.Name, cell);
			}
			finally
			{
				Marshal.ReleaseComObject(range);
			}
		}

		public static CellValue<Type> GetCellJson<Type>(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell)
		{
			CellValue<string> str = ExcelUtils.GetCellString(fieldName, baseNode, sheet, baseCell);
			if (string.IsNullOrWhiteSpace(str.value))
			{
				return new CellValue<Type> { sheetName = str.sheetName, cell = str.cell, value = default };
			}
			return new CellValue<Type> { sheetName = str.sheetName, cell = str.cell, value = Utils.FromJsonString<Type>(str.value) };
		}

		// ローカルパスを取得する。
		public static string GetLocalPath(string rawPath)
		{
			try
			{
				if (Directory.Exists(rawPath) || File.Exists(rawPath))
					return rawPath;

				var workbookUri = new Uri(rawPath);
				if (workbookUri.IsFile)
					return rawPath;

				// OneDrive と同期されたファイルの場合、URL[https://...] からファイルパスへ変換する
				string localPath = string.Empty;
				List<string> keyNames = new List<string>() { "OneDriveCommercial", "OneDrive", "onedrive" };
				foreach (var keyName in keyNames)
				{
					// システム環境変数から OneDrive のルートディレクトリを取得
					using (var key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("Environment", false))
					{
						var rootDirectory = key.GetValue(keyName).ToString();
						if (string.IsNullOrEmpty(rootDirectory) == false && Directory.Exists(rootDirectory))
						{
							var pathParts = new Queue<string>(workbookUri.LocalPath.Split('/'));

							do
							{
								string subPath = string.Join("\\", pathParts);
								localPath = string.Format("{0}\\{1}", rootDirectory, subPath);

								if (Directory.Exists(localPath) || File.Exists(rawPath))
								{
									logger.Info("GetLocalPath: {0}", localPath);
									return localPath;
								}

								pathParts.Dequeue();
							}
							while (pathParts?.Count > 0);

						}
					}
				}

				logger.Info("GetLocalPath: {0}", localPath);
				return localPath;
			}
			catch (Exception ex)
			{
				logger.Error(ex);
				return rawPath;
			}
		}
	}
}
