using adWorkbookAddIn;
using adWorkbookAddIn.Source;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Documents;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Xml;
using Excel = Microsoft.Office.Interop.Excel;

namespace ExcelImport
{

    class ExcelImport
	{
		private const string ImagesTempDirName = "temp";
		private const string ImagesDirName = "images";

		// 整数 入力制限あり、少数 入力制限あり
		private const String Pattern1 = "^-$|^-?[0-9]{{1,{0}}}(\\.[0-9]{{0,{1}}})?$";
		private const String Pattern2 = "^-$|^-?0(\\.[0-9]{{0,{0}}})?$";
		// 整数 入力制限あり、少数 入力不可
		private const String Pattern3 = "^-$|^-?[0-9]{{1,{0}}}?$";
		// 整数 入力制限あり、少数 入力制限なし
		private const String Pattern4 = "^-$|^-?[0-9]{{1,{0}}}(\\.[0-9]*)?$";
		// 整数 入力制限なし、少数 入力制限あり
		private const String Pattern5 = "^-$|^-?[0-9]+(\\.[0-9]{{0,{0}}})?$";
		// 整数 入力制限なし、少数 入力不可
		private const String Pattern6 = "^-$|^-?[0-9]+$";

		private static CellValue<int?> ParseTime(CellValue<string> time)
		{
			if (string.IsNullOrWhiteSpace(time.value))
			{
				return new CellValue<int?> { sheetName = time.sheetName, cell = time.cell };
			}
			string[] parts = time.value.Split(':');
			if (parts.Length != 3)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidTaktTime"), time.sheetName, time.cell);
			}
			if (!int.TryParse(parts[0], out int hour) || !int.TryParse(parts[1], out int minute) || !int.TryParse(parts[2], out int second))
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidTaktTime"), time.sheetName, time.cell);
			}
			if (hour < 0 || hour >= 100)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidTaktTime"), time.sheetName, time.cell);
			}
			if (minute < 0 || minute >= 60)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidTaktTime"), time.sheetName, time.cell);
			}
			if (second < 0 || second >= 60)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidTaktTime"), time.sheetName, time.cell);
			}
			int value = ((hour * 60 + minute) * 60 + second) * 1000;
			return new CellValue<int?> { sheetName = time.sheetName, cell = time.cell, value = value };
		}

		/// <summary>
		/// 図のバウンディングボックスを取得する
		/// </summary>
		/// <param name="shape">図</param>
		/// <returns>バウンディングボックス</returns>
		private static Rectangle GetShapeRect(Excel.Shape shape)
		{
			float left = shape.Left;
			float top = shape.Top;
			float width = shape.Width;
			float height = shape.Height;
			float rotation = shape.Rotation;

			if (rotation == 0.0f)
			{
				return new Rectangle(left, top, left + width, top + height);
			}

			// 回転した長方形バウンディングボックスを計算する

			double rotationRadians = rotation * Math.PI / 180.0;

			double dx = Math.Cos(rotationRadians);
			double dy = Math.Sin(rotationRadians);

			Point[] pts = new Point[4];
			pts[0] = new Point(-width / 2.0, -height / 2.0);
			pts[1] = new Point( width / 2.0, -height / 2.0);
			pts[2] = new Point( width / 2.0,  height / 2.0);
			pts[3] = new Point(-width / 2.0,  height / 2.0);

			for (int i = 0; i < pts.Length; i++)
			{
				double x = pts[i].X;
				double y = pts[i].Y;

				pts[i].X = x*dx - y*dy;
				pts[i].Y = x*dy + y*dx;
			}

			for (int i = 0; i < pts.Length; i++)
			{
				pts[i].X += left + width / 2.0;
				pts[i].Y += top + height / 2.0;
			}

			float xMin = float.PositiveInfinity;
			float xMax = float.NegativeInfinity;
			float yMin = float.PositiveInfinity;
			float yMax = float.NegativeInfinity;
			for (int i = 0; i < pts.Length; i++)
			{
				float x = (float)pts[i].X;
				float y = (float)pts[i].Y;

				xMin = Math.Min(xMin, x);
				xMax = Math.Max(xMax, x);
				yMin = Math.Min(yMin, y);
				yMax = Math.Max(yMax, y);
			}

			return new Rectangle(xMin, yMin, xMax, yMax);
		}

		/// <summary>
		/// 長方形のセル範囲を取得する
		/// </summary>
		/// <param name="sheet">Excelシート</param>
		/// <param name="rect">長方形</param>
		/// <returns>セル範囲</returns>
		private static Excel.Range GetRectRange(Excel.Worksheet sheet, Rectangle rect)
		{
			Excel.Shape shape = sheet.Shapes.AddLine(rect.left, rect.top, rect.right, rect.bottom);
			Excel.Range beginRange = shape.TopLeftCell;
			Excel.Range endRange = shape.BottomRightCell;
			Excel.Range range = sheet.Range[beginRange, endRange];

			shape.Delete();
			Marshal.ReleaseComObject(beginRange);
			Marshal.ReleaseComObject(endRange);
			Marshal.ReleaseComObject(shape);

			return range;
		}

		private static MemoryStream GetPicture(Excel.Worksheet sheet, Rectangle rect)
		{
			Excel.Range range = GetRectRange(sheet, rect);

			double alignedLeft = range.Left;
			double alignedTop = range.Top;
			double alignedWidth = range.Width;
			double alignedHeight = range.Height;

			try
			{
				int attempts = 10;
				while (true)
				{
					try
					{
						range.CopyPicture(Excel.XlPictureAppearance.xlScreen, Excel.XlCopyPictureFormat.xlBitmap);
						if (!Clipboard.ContainsData(System.Windows.DataFormats.Bitmap))
						{
							return null;
						}
						BitmapSource bitmapSource = Clipboard.GetData(System.Windows.DataFormats.Bitmap) as BitmapSource;

						int bitmapWidth = bitmapSource.PixelWidth;
						int bitmapHeight = bitmapSource.PixelHeight;

						double hScale = bitmapWidth / alignedWidth;
						double vScale = bitmapHeight / alignedHeight;

						var noAlphaSource = new FormatConvertedBitmap();
						noAlphaSource.BeginInit();
						noAlphaSource.Source = bitmapSource;
						noAlphaSource.DestinationFormat = PixelFormats.Rgb24;
						noAlphaSource.EndInit();

						var cropRect = new Int32Rect(
							(int)((rect.left - alignedLeft) * hScale + 0.5),
							(int)((rect.top - alignedTop) * vScale + 0.5),
							(int)(rect.Width * hScale + 0.5),
							(int)(rect.Height * vScale + 0.5)
						);

						int s = 2;
						cropRect.X += s;
						cropRect.Y += s;
						cropRect.Width -= 2 * s;
						cropRect.Height -= 2 * s;

						if (cropRect.Width <= 0 || cropRect.Height <= 0)
						{
							return null;
						}

						var croppedSource = new CroppedBitmap(noAlphaSource, cropRect);

						var encoder = new PngBitmapEncoder();
						encoder.Frames.Add(BitmapFrame.Create(croppedSource));

						MemoryStream buf = new MemoryStream();
						encoder.Save(buf);
						return buf;
					}
					catch (Exception ex)
					{
						attempts--;
						if (attempts < 0)
						{
							throw ex;
						}
						System.Windows.Forms.Application.DoEvents();
					}
				}
			}
			finally
			{
				Marshal.ReleaseComObject(range);
			}
		}

		private static CellValue<MemoryStream> GetPicture(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell, RectangleGroups rectangleGroups)
		{
			ExcelCell cell = ExcelUtils.GetCell(fieldName, baseNode, baseCell);

			Excel.Range fieldRange = sheet.Cells[cell.row, cell.column];
			try
			{
				double left = fieldRange.Left;
				double top = fieldRange.Top;

				RectangleGroup rectangleGroup = rectangleGroups.FindNearestLeftTop((float)left, (float)top, 16);
				if (rectangleGroup == null)
				{
					return new CellValue<MemoryStream> { sheetName = sheet.Name, cell = cell, value = null };
				}

				return new CellValue<MemoryStream> { sheetName = sheet.Name, cell = cell, value = GetPicture(sheet, rectangleGroup.bounds) };
			}
			catch (Exception)
			{
				// 画像の取得に失敗しました。
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.GetImageFailed"), sheet.Name, cell);
			}
			finally
			{
				Marshal.ReleaseComObject(fieldRange);
			}
		}

		private static string NormalizeFilename(string filename)
		{
			StringBuilder sb = new StringBuilder();
			HashSet<char> invalidChars = new HashSet<char>(Path.GetInvalidFileNameChars());
			foreach (char ch in filename)
			{
				if (invalidChars.Contains(ch)) {
					sb.Append("-");
				}
				else
				{
					sb.Append(ch);
				}
			}
			return sb.ToString();
		}

		private static string GetPictureFileName(Work work, WorkProcedure workProcedure)
		{
			return NormalizeFilename(work.sheetName + "_" + workProcedure.workProcedureName.value + "_" + workProcedure.workProcedureNo.value + ".png");
		}

		private static string GetServerFileName(string fileName, ref int fileSequence)
		{
			string hostName = Environment.MachineName;
			fileSequence++;
			string timestamp = string.Format("{0:yyyyMMddHHmmssfff}", DateTime.Now);
			int dotPos = fileName.LastIndexOf('.');
			string extension = dotPos < 0 ? "" : fileName.Substring(dotPos);
			return $"{hostName}-{fileSequence}-{timestamp}{extension}";
		}

		private static readonly Dictionary<string, WorkOrder> workOrderOptions = new Dictionary<string, WorkOrder> {
			{ "並列", WorkOrder.Parallel },
			{ "直列", WorkOrder.Serial }
		};

		private static readonly Dictionary<string, CheckItemVisibility> visibilityOptions = new Dictionary<string, CheckItemVisibility> {
			{ "表示", CheckItemVisibility.Visible },
			{ "非表示", CheckItemVisibility.Hidden }
		};

		private static Model ImportModelExcel(UploadProgressForm progress, Excel.Workbook workbook, XmlDocument excelFormat, string workbookPath, ref int fileSequence)
		{
			XmlNode workflowNode = excelFormat.DocumentElement.SelectSingleNode("/root/workflow");
			XmlNode workParameterNode = excelFormat.DocumentElement.SelectSingleNode("/root/workflow/workParameter");
			XmlNode workNode = excelFormat.DocumentElement.SelectSingleNode("/root/work");
			XmlNode workProcedureNode = excelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure");
			XmlNode checkItemsNode = excelFormat.DocumentElement.SelectSingleNode("/root/work/workProcedure/checkItems");

			var model = new Model
			{
				workflow = new Workflow()
			};

			Excel.Sheets sheets = workbook.Sheets;

			// アクティブシートを退避
			Excel.Worksheet savedActiveSheet = workbook.ActiveSheet;
			try
			{
				#region 工程順を読込む
				string workflowSheetName = workflowNode.Attributes.GetNamedItem("sheet").Value;
				Excel.Worksheet workflowSheet;
				try
				{
					workflowSheet = sheets[workflowSheetName];
				}
				catch (COMException)
				{
					throw new ImportException(string.Format(LocaleUtil.GetString("ExcelUtils.Validate.MissingMasterSheet"), workflowSheetName));
				}
				var workflow = new Workflow
				{
					workflowName = ErrorUtils.Required(ExcelUtils.GetCellStringNonBlank("workflowName", workflowNode, workflowSheet, null)),
					rev = ErrorUtils.RequiredInt(ExcelUtils.GetCellInt("rev", workflowNode, workflowSheet, null)),
					workNo = ExcelUtils.GetCellStringNonBlank("workNo", workflowNode, workflowSheet, null),
					workOrder = ErrorUtils.Required(ExcelUtils.GetCellEnum("workOrder", workOrderOptions, workflowNode, workflowSheet, null)),
					approverId1 = ExcelUtils.GetCellStringNonBlank("approverId1", workflowNode, workflowSheet, null),
					approverId2 = ExcelUtils.GetCellStringNonBlank("approverId2", workflowNode, workflowSheet, null),
					updatePersonId = ErrorUtils.Required(ExcelUtils.GetCellStringNonBlank("updatePersonId", workflowNode, workflowSheet, null)),
					workflowHierarchyName = ErrorUtils.Required(ExcelUtils.GetCellStringNonBlank("workflowHierarchy", workflowNode, workflowSheet, null))
				};
				model.workflow = workflow;
				#endregion

				#region 作業パラメータを読込む
				CellIterator workParameterCells = new CellIterator(null, workParameterNode);
				for (;; workParameterCells.Next(workflowSheet))
				{
					var workParameter = new WorkParameter
					{
						itemNumber = ExcelUtils.GetCellStringNonBlank("itemNumber", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						workNo = ExcelUtils.GetCellInt("workNo", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						taktTime = ExcelUtils.GetCellStringNonBlank("taktTime", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						workProcedureNo = ExcelUtils.GetCellInt("workProcedureNo", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						fileName = ExcelUtils.GetCellStringNonBlank("fileName", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						checkItemNo = ExcelUtils.GetCellInt("checkItemNo", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						key = ExcelUtils.GetCellStringNonBlank("key", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						value = ExcelUtils.GetCellStringNonBlank("value", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						minValue = ExcelUtils.GetCellDouble("minValue", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						maxValue = ExcelUtils.GetCellDouble("maxValue", workParameterNode, workflowSheet, workParameterCells.GetCell()),
						visibility = ExcelUtils.GetCellEnum("visibility", visibilityOptions, workParameterNode, workflowSheet, workParameterCells.GetCell())
					};

					if (workParameter.itemNumber.value == null &&
						!workParameter.workNo.value.HasValue &&
						workParameter.taktTime.value == null &&
						!workParameter.workProcedureNo.value.HasValue &&
						workParameter.fileName.value == null &&
						!workParameter.checkItemNo.value.HasValue &&
						workParameter.key.value == null &&
						workParameter.value.value == null &&
						!workParameter.minValue.value.HasValue &&
						!workParameter.maxValue.value.HasValue &&
						workParameter.visibility.value == CheckItemVisibility.None
						)
					{
						break;
					}

					if (workParameter.minValue.value.HasValue && workParameter.maxValue.value.HasValue && workParameter.minValue.value.Value > workParameter.maxValue.value.Value)
					{
						throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.MinMaxValue"), workParameter.maxValue.sheetName, workParameter.maxValue.cell);
					}

					workflow.workParameters.Add(workParameter);
				}
				#endregion

				#region 画像一時フォルダを空にする
				string imagesPath = Path.Combine(workbookPath, ImagesTempDirName);
				Directory.CreateDirectory(imagesPath);

				DirectoryInfo di = new DirectoryInfo(imagesPath);
				foreach (FileInfo file in di.GetFiles())
				{
					file.Delete();
				}
				#endregion

				#region 工程を読込む
				string workSheetNamePattern = workNode.Attributes.GetNamedItem("sheet").Value;
				var usedWorkNames = new HashSet<string>();
				var usedTags = new HashSet<string>();

				int workSheetCount = 0;
				int workSheetProgress = 0;
				foreach (Excel.Worksheet workSheet in sheets)
				{
					string sheetName = workSheet.Name;
					if (Regex.Matches(sheetName, workSheetNamePattern).Count <= 0)
					{
						continue;
					}
					workSheetCount++;
				}
				progress.SetCurrentProgressMax(workSheetCount);

				foreach (Excel.Worksheet workSheet in sheets)
				{
					string sheetName = workSheet.Name;
					if (Regex.Matches(sheetName, workSheetNamePattern).Count <= 0)
					{
						continue;
					}

					workSheet.Activate();

					progress.SetCurrentProgressValue(workSheetProgress++, LocaleUtil.GetString("ExcelImport.DoImport.ImportingModel"));
					var rectangleGroups = new RectangleGroups();

					Excel.Shapes shapes = workSheet.Shapes;
					foreach (Excel.Shape shape in shapes)
					{
						Rectangle rect = GetShapeRect(shape);
						rectangleGroups.Add(rect);
					}

					Work work = new Work
					{
						sheetName = sheetName,
						workName = ErrorUtils.Required(ExcelUtils.GetCellStringNonBlank("workName", workNode, workSheet, null)),
						rev = ErrorUtils.RequiredInt(ExcelUtils.GetCellInt("rev", workNode, workSheet, null)),
						workNo = ExcelUtils.GetCellStringNonBlank("workNo", workNode, workSheet, null),
						taktTime = ExcelUtils.GetCellStringNonBlank("taktTime", workNode, workSheet, null),
						workHierarchyName = /*ExcelUtils.GetCellString("workHierarchy", workNode, workSheet, null)*/ workflow.workflowHierarchyName,
						option = new CellValue<WorkOption> { value = Globals.ThisAddIn.FileRoster.GetWorkOption(workbook, workSheet, false) }
					};

					if (!usedWorkNames.Add(work.workName.value))
					{
						string args = LocaleUtil.GetString("ExcelUtils.Args.Workbook");
						throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.DuplicateValue", args), work.workName.sheetName, work.workName.cell);
					}

					model.works.Add(work);

					int nextPage = 1;
					int nextDisp = 1;

					CellIterator workProcedureCells = new CellIterator(null, workProcedureNode);
					ExcelCell workProcedureNextCell = workProcedureCells.GetCell();

					while (true)
					{
						ExcelCell workProcedureCell = workProcedureNextCell;
						workProcedureCells.Next(workSheet);
						workProcedureNextCell = workProcedureCells.GetCell();

						CellValue<int?> workProcedureNo = ExcelUtils.GetCellInt("workProcedureNo", workProcedureNode, workSheet, workProcedureCell);
						CellValue<string> workProcedureName = ExcelUtils.GetCellStringNonBlank("workProcedureName", workProcedureNode, workSheet, workProcedureCell);

						if (!workProcedureNo.value.HasValue && workProcedureName.value == null)
						{
							break;
						}

						WorkProcedure workProcedure = new WorkProcedure
						{
							workProcedureNo = ErrorUtils.RequiredInt(workProcedureNo),
							workProcedureName = ErrorUtils.Required(workProcedureName),
							//workMethod = ExcelUtils.GetCellString("workMethod", workProcedureNode, workSheet, workProcedureCells.GetCell()),
							workPoint = ExcelUtils.GetCellStringNonBlank("workPoint", workProcedureNode, workSheet, workProcedureCell),
							pageNo = nextPage++
						};

						workProcedure.image = SaveImage("image", workProcedureNode, workSheet, workProcedureCell, work, workProcedure, rectangleGroups, imagesPath, ref fileSequence);
						if (work.workProcedureNoMap.ContainsKey(workProcedure.workProcedureNo.value))
						{
							string args = LocaleUtil.GetString("ExcelUtils.Args.Worksheet");
							throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.DuplicateValue", args), workProcedure.workProcedureNo.sheetName, workProcedure.workProcedureNo.cell);
						}

						work.workProcedureNoMap[workProcedure.workProcedureNo.value] = workProcedure;

						CellIterator checkItemsCells = new CellIterator(workProcedureCell, checkItemsNode);
						ExcelCell checkItemsNextCell = checkItemsCells.GetCell();

						while (checkItemsCells.HasNext(workSheet))
						{
							ExcelCell checkItemsCell = checkItemsNextCell;
							checkItemsCells.Next(workSheet);
							checkItemsNextCell = checkItemsCells.GetCell();

							CellValue<string> tag = ExcelUtils.GetCellStringNonBlank("tag", checkItemsNode, workSheet, checkItemsCell);
							CheckItem checkItem = new CheckItem
							{
								checkItemNo = ErrorUtils.RequiredInt(ExcelUtils.GetCellInt("checkItemNo", checkItemsNode, workSheet, checkItemsCell)),
								checkItemName = ExcelUtils.GetCellStringNonBlank("checkItemName", checkItemsNode, workSheet, checkItemsCell),
								value = ExcelUtils.GetCellStringNonBlank("currentValue", checkItemsNode, workSheet, checkItemsCell),
								minValue = ExcelUtils.GetCellDouble("minValue", checkItemsNode, workSheet, checkItemsCell),
								maxValue = ExcelUtils.GetCellDouble("maxValue", checkItemsNode, workSheet, checkItemsCell),
								tag = tag,
								disp = nextDisp
							};

							while (checkItemsNextCell.row < workProcedureNextCell.row - 1)
							{
								int? checkItemNo = ExcelUtils.GetCellInt("checkItemNo", checkItemsNode, workSheet, checkItemsNextCell).value;
								if (checkItemNo.HasValue)
								{
									break;
								}

								string checkItemName = ExcelUtils.GetCellStringNonBlank("checkItemName", checkItemsNode, workSheet, checkItemsNextCell).value;
								checkItem.checkItemName.value += "\n" + checkItemName;

								checkItemsCells.Next(workSheet);
								checkItemsNextCell = checkItemsCells.GetCell();
							}

							if (string.IsNullOrWhiteSpace(checkItem.checkItemName.value) &&
								checkItem.value.value == null &&
								checkItem.minValue.value == null &&
								checkItem.maxValue.value == null &&
								checkItem.tag.value == null)
							{
								continue;
							}

							ErrorUtils.Required(checkItem.checkItemName);
							ErrorUtils.Required(tag);

							if (checkItem.minValue.value.HasValue && checkItem.maxValue.value.HasValue && checkItem.minValue.value.Value > checkItem.maxValue.value.Value)
							{
								throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.MinMaxValue"), checkItem.maxValue.sheetName, checkItem.maxValue.cell);
							}

							checkItem.option = new CellValue<CheckItemOption> { value = Globals.ThisAddIn.FileRoster.GetCheckItemOption(workbook, tag.value, false) };

							if (!usedTags.Add(tag.value))
							{
								string args = LocaleUtil.GetString("ExcelUtils.Args.Workbook");
								throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.DuplicateValue", args), tag.sheetName, tag.cell);
							}

							if (workProcedure.checkItemNoMap.ContainsKey(checkItem.checkItemNo.value))
							{
								string args = LocaleUtil.GetString("ExcelUtils.Args.WorkProcedure");
								throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.DuplicateValue", args), checkItem.checkItemNo.sheetName, checkItem.checkItemNo.cell);
							}

							nextDisp++;
							workProcedure.checkItemNoMap[checkItem.checkItemNo.value] = checkItem;
						}
					}
				}
				#endregion

				Globals.ThisAddIn.FileRoster.DeleteCheckItemOptionUnusedTags(workbook, usedTags);
			}
			finally
			{
				// アクティブシートを復元
				savedActiveSheet.Activate();
			}

			return model;
		}

		private static WorkEntity BuildWorkEntity(Work work, long userId, long parentId)
		{
			var workEntity = new WorkEntity
			{
				parentId = parentId,
				workId = work.workId,
				workName = work.workName.value,
				workRev = work.rev.value,
				taktTime = ErrorUtils.Required(ParseTime(work.taktTime)).value.Value,
				contentType = "STRING", // ?
				updatePersonId = userId,
				updateDatetime = DateTime.Now,
				fontColor = work.option.value == null ? "#FFFFFF" : work.option.value.fontColor,
				backColor = work.option.value == null ? "#000000" : work.option.value.backgroundColor,
				workNumber = work.workNo.value,
				workSections = new List<WorkSectionEntity>()
			};

			var checkInfos = new List<CheckInfoEntity>();
			foreach (var workProcedure in work.workProcedureNoMap.Values)
			{
				var workSectionEntity = new WorkSectionEntity
				{
					documentTitle = workProcedure.workProcedureName.value,
					workSectionOrder = workProcedure.pageNo
				};
				if (workProcedure.image != null)
				{
					workSectionEntity.fileName = workProcedure.image.value.clientName;
					workSectionEntity.physicalName = workProcedure.image.value.serverName;
					workSectionEntity.fileUpdated = workProcedure.image.value.timestamp;
				}
				workEntity.workSections.Add(workSectionEntity);

				foreach (var checkItem in workProcedure.checkItemNoMap.Values)
				{
					var checkInfo = new CheckInfoEntity
					{
						key = checkItem.checkItemName.value,
						type = "TYPE_STRING",
						val = checkItem.value.value,
						disp = checkItem.disp,
						min = checkItem.minValue.value,
						max = checkItem.maxValue.value,
						tag = checkItem.tag.value,
						page = workProcedure.pageNo,
						cp = 0,
					};

					checkInfos.Add(checkInfo);
					BuildCheckItemOption(checkItem.option.value, checkInfo);
				}
			}
			workEntity.workCheckInfo = Utils.ToJsonString(checkInfos);

			if (work.option.value != null)
			{
				var workAddInfoEntities = new List<WorkAddInfoEntity>();
				int disp = 1;
				foreach (var it in work.option.value.additionalOptions)
				{
					var workAddInfoEntity = new WorkAddInfoEntity
					{
						key = it.name,
						type = Enum.GetName(typeof(AdditionalOptionType), it.type),
						val = it.value,
						disp = disp++
					};
					workAddInfoEntities.Add(workAddInfoEntity);
				}
				workEntity.workAddInfo = Utils.ToJsonString(workAddInfoEntities);

				var workDisplayItemEntities = new List<WorkDisplayItemEntity>();
				int order = 1;
				foreach (var it in work.option.value.displayItems)
				{
					var workDisplayItemEntity = new WorkDisplayItemEntity
					{
						order = order++,
						name = it.name
					};
					switch (it.target)
					{
						case DisplayItemTarget.KANBAN:
							workDisplayItemEntity.target = "KANBAN";
							break;
						case DisplayItemTarget.WORKKANBAN:
							workDisplayItemEntity.target = "WORKKANBAN";
							break;
						case DisplayItemTarget.WORK:
							workDisplayItemEntity.target = "WORK";
							break;
					}
					workDisplayItemEntities.Add(workDisplayItemEntity);
				}
				workEntity.displayItems = Utils.ToJsonString(workDisplayItemEntities);
			}

			return workEntity;
		}

		private static void AddTraceOption(TraceSettingEntity traceSettingEntity, TraceOptionTypeEnum key, string value)
		{
			TraceOptionEntity traceOptEntity = new TraceOptionEntity
			{
				key = key,
				value = value
			};
			traceSettingEntity.traceOptions.Add(traceOptEntity);
		}

		/// <summary>
		/// 入力規則を生成する。
		/// </summary>
		/// <param name="option">チェック項目情報</param>
		/// <returns></returns>
		private static String CreateValidationRule(CheckItemOption option)
		{
			if (!option.detailSettings.integerDigits.HasValue && !option.detailSettings.decimalDigits.HasValue)
			{
				return null;
			}

			String validationRule = null;
			int? integerDigits = option.detailSettings.integerDigits;
			int? decimalDigits = option.detailSettings.decimalDigits;

			if (integerDigits.HasValue)
			{
				if (decimalDigits.HasValue)
				{
					if (0 < decimalDigits.Value)
					{
						if (1 < integerDigits.Value)
						{
							// 整数 入力制限あり、少数 入力制限あり
							validationRule = String.Format(Pattern1, integerDigits.Value, decimalDigits.Value);
						}
						else if (1 == integerDigits.Value)
						{
							validationRule = String.Format("^-$|^-?[0-9](\\.[0-9]{{0,{0}}})?$", decimalDigits.Value);
						}
						else
						{
							validationRule = String.Format(Pattern2, decimalDigits.Value);
						}
					}
					else
					{
						if (1 < integerDigits.Value)
						{
							// 整数 入力制限あり、少数 入力不可
							validationRule = String.Format(Pattern3, integerDigits.Value);
						}
						else if (1 == integerDigits.Value)
						{
							// 整数 入力制限あり、少数 入力不可
							validationRule = "^-$|^-?[0-9]?$";
						}
						else
						{
							// 0しか入力出来ない
							validationRule = "^0$";
						}
					}
				}
				else
				{
					// 整数 入力制限あり、少数 入力制限なし
					if (1 < integerDigits.Value)
					{
						validationRule = String.Format(Pattern4, integerDigits.Value);
					}
					else if (1 == integerDigits.Value)
					{
						validationRule = "^-$|^-?[0-9](\\.[0-9]*)?$";
					}
					else
					{
						validationRule = "^-$|^-?0(\\.[0-9]*)?$";
					}
				}
			}
			else if (decimalDigits.HasValue)
			{
				if (0 < decimalDigits.Value)
				{
					validationRule = String.Format(Pattern5, decimalDigits.Value);
				}
				else
				{
					validationRule = Pattern6;
				}
			}

			return validationRule;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="checkItemOption"></param>
		/// <param name="checkInfo"></param>
		private static void BuildCheckItemOption(CheckItemOption checkItemOption, CheckInfoEntity checkInfo)
		{
			if (checkItemOption == null)
			{
				checkItemOption = new CheckItemOption();
				if (checkInfo.min != null || checkInfo.max != null)
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

			var traceSettingEntity = new TraceSettingEntity();

			checkInfo.cat = Enum.GetName(typeof(CheckItemType), checkItemOption.type);
			checkInfo.rules = checkItemOption.rule;

			switch (checkItemOption.type)
			{
				case CheckItemType.PARTS:
					checkInfo.cat = "PARTS";
					break;
				case CheckItemType.WORK:
					checkInfo.cat = "WORK";
					break;
				case CheckItemType.INSPECTION:
					checkInfo.cat = "INSPECTION";
					break;
				case CheckItemType.MEASURE:
					checkInfo.cat = "MEASURE";
					// 入力規則
					checkInfo.rules = CreateValidationRule(checkItemOption);
					break;
				case CheckItemType.CUSTOM:
					checkInfo.cat = "CUSTOM";
					break;
				case CheckItemType.TIMESTAMP:
					checkInfo.cat = "TIMESTAMP";
					break;
				case CheckItemType.TIMER:
					checkInfo.cat = "TIMER";
					break;
				case CheckItemType.JUDG:
					checkInfo.cat = "JUDG";
					break;
				case CheckItemType.PRODUCT:
					checkInfo.cat = "PRODUCT";
					break;
			}

			switch (checkItemOption.inputType)
			{
				case CheckItemInputType.TEXT_KEYBOARD:
				case CheckItemInputType.TEN_KEYBOARD:
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.TEN_KEYBOARD, "true");
					break;
				case CheckItemInputType.ATTACH_FILE:
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.ATTACH_FILE, "true");
					break;
				case CheckItemInputType.CALCULATION_RESULTS:
					// TODO
					break;
			}

			switch (checkItemOption.type)
			{
				case CheckItemType.INSPECTION:
				case CheckItemType.CUSTOM:
					switch (checkItemOption.inputType)
					{
						case CheckItemInputType.TEXT_KEYBOARD:
							AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.KEYBOARD_TYPE, "TOUCH_KEYBOARD");
							break;
						case CheckItemInputType.TEN_KEYBOARD:
							AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.KEYBOARD_TYPE, "TEN_KEYBOARD");
							break;
					}
					break;

				case CheckItemType.MEASURE:
					if (checkItemOption.calculationResult != null)
					{
						// 計算式をカスタム設定に追加
						var traceCustomEntity = new TraceCustomEntity
						{
							name = "Formula",
							value = checkItemOption.calculationResult
						};
						traceSettingEntity.traceCustoms.Add(traceCustomEntity);
					}
					break;

				case CheckItemType.TIMESTAMP:
					switch (checkItemOption.datetimeFormat)
					{
						case CheckItemDatetimeFormat.DATETIME_INPUT_OPTION:
							AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.DATETIME_TYPE, "DATETIME");
							break;
						case CheckItemDatetimeFormat.DATE_INPUT_OPTION:
							AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.DATETIME_TYPE, "DATE");
							break;
						case CheckItemDatetimeFormat.TIME_INPUT_OPTION:
							AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.DATETIME_TYPE, "TIME");
							break;
					}
					break;
			}

			if (checkItemOption.isRequired)
			{
				AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.CHECK_EMPTY, "true");
			}

			if (checkItemOption.isNoDirectInput)
			{
				AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.INPUT_LIST_ONLY, "true");
			}

			if (checkItemOption.isNoDuplicateInput)
			{
				AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.CHECK_UNIQUE, "true");
			}

			if (checkItemOption.isCommentInput)
			{
				// 入力フォームのカラムを拡張できるようにするため数値型
				AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.INPUT_TEXT, "2");
			}

			if (checkItemOption.isCommentDisplay)
			{
				AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.DISPLAY_TEXT, "true");
			}

			if (checkItemOption.listWithColors != null && checkItemOption.listWithColors.Count > 0)
			{
				var traceOptEntity = new TraceOptionEntity
				{
					key = TraceOptionTypeEnum.COLOR_VALUE_LIST,
					valueColors = new List<InputValueColor>()
				};

				foreach (var colorIt in checkItemOption.listWithColors)
				{
					var colorValue = new InputValueColor
					{
						text = colorIt.value,
						textColor = colorIt.fontColor,
						textBkColor = colorIt.backgroundColor
					};
					traceOptEntity.valueColors.Add(colorValue);
				}
				traceSettingEntity.traceOptions.Add(traceOptEntity);
			}

			if (checkItemOption.equipments != null && checkItemOption.equipments.Count > 0)
			{
				var traceOptEntity = new TraceOptionEntity
				{
					key = TraceOptionTypeEnum.REFERENCE_NUMBER,
					values = new List<string>()
				};

				foreach (var equipment in checkItemOption.equipments)
				{
					traceOptEntity.values.Add(equipment.identify);
				}
				traceSettingEntity.traceOptions.Add(traceOptEntity);

				AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.FIELDS, "EQUIPMENT");
			}

			if (checkItemOption.customValues != null)
			{
				foreach (var customValue in checkItemOption.customValues)
				{
					var traceCustomEntity = new TraceCustomEntity
					{
						name = customValue.name,
						value = customValue.value
					};
					traceSettingEntity.traceCustoms.Add(traceCustomEntity);
				}
			}

			if (checkItemOption.detailSettings != null)
			{
				if (checkItemOption.detailSettings.bulkInput)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.BULK_INPUT, "true");
				}
				if (checkItemOption.detailSettings.work != null && checkItemOption.detailSettings.work.Length > 0)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.WORK, checkItemOption.detailSettings.work);
				}
				if (checkItemOption.detailSettings.property != null && checkItemOption.detailSettings.property.Length > 0)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.PROPERTY, checkItemOption.detailSettings.property);
				}
				if (checkItemOption.detailSettings.integerDigits.HasValue)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.INTEGER_DIGITS, checkItemOption.detailSettings.integerDigits.Value.ToString());
				}
				if (checkItemOption.detailSettings.decimalDigits.HasValue)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.DECIMAL_DIGITS, checkItemOption.detailSettings.decimalDigits.Value.ToString());
				}
				if (checkItemOption.detailSettings.absoluteDisplay)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.ABSOLUTE_DISPLAY, "true");
				}
				if (checkItemOption.detailSettings.plugin != null && checkItemOption.detailSettings.plugin.Length > 0)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.PLUGIN, checkItemOption.detailSettings.plugin);
				}
				if (checkItemOption.detailSettings.customFields.HasValue)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.FIELDS, "CUSTOM");
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.FIELD_SIZE, checkItemOption.detailSettings.customFields.ToString());
				}
				if (checkItemOption.detailSettings.delimiter != null && checkItemOption.detailSettings.delimiter.Length > 0)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.DELIMITER, checkItemOption.detailSettings.delimiter);
				}
				if (checkItemOption.detailSettings.holdPrevData)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.HOLD_PREV_DATA, "true");
				}
				if (checkItemOption.detailSettings.inputProductNum)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.INPUT_PRODUCT_NUM, "true");
				}
				if (checkItemOption.detailSettings.qrRead)
				{
					AddTraceOption(traceSettingEntity, TraceOptionTypeEnum.QR_READ, "true");
				}
			}

			checkInfo.opt = Utils.ToXmlString(traceSettingEntity);
		}

		private static List<WorkParametersEntity> BuildWorkParametersEntity(Model model, string workbookPath, ref int fileSequence)
		{
			var workParametersEntities = new List<WorkParametersEntity>();

			WorkParametersEntity workParsEntity = null;
			WorkParameterEntity workParEntity = null;
			WorkParameterWorkEntity workEntity = null;
			WorkParameterWorkSectionEntity workSectEntity = null;

			Work work = null;
			WorkProcedure workProcedure = null;

			// 品番をキーにソートする
			model.workflow.workParameters.Sort((a, b) => 
			{
				return String.Compare(a.itemNumber.value, b.itemNumber.value);
			});

			foreach (var workPar in model.workflow.workParameters)
			{
				// 品番
				if (workPar.itemNumber.value != null && (workParsEntity == null || workParsEntity.itemNumber != workPar.itemNumber.value))
				{
					if (workParsEntity != null)
					{
						workParsEntity.workParameter = Utils.ToJsonString(workParEntity);
					}

					workParsEntity = new WorkParametersEntity
					{
						workflowId = model.workflow.workflowId.Value,
						itemNumber = workPar.itemNumber.value
					};
					workParametersEntities.Add(workParsEntity);

					workParEntity = new WorkParameterEntity();
					workEntity = null;
					workSectEntity = null;
					work = null;
					workProcedure = null;
				}

				// 工程
				if (workParEntity != null && workPar.workNo.value.HasValue) {
					int workIdx = workPar.workNo.value.Value - 1;
					if (workIdx < 0 || workIdx >= model.works.Count)
					{
						throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidReference"), workPar.workNo.sheetName, workPar.workNo.cell);
					}
					work = model.works[workIdx];
					if (workEntity == null || workEntity.workId != work.workId.Value) {
						CellValue<int?> workParTaktTime = ParseTime(workPar.taktTime);

						workEntity = new WorkParameterWorkEntity
						{
							workId = work.workId.Value,
							taktTime = workParTaktTime.value
						};
						workParEntity.work.Add(workEntity);

						workSectEntity = null;
						workProcedure = null;
					}
				}

				// 作業手順
				if (workEntity != null && workPar.workProcedureNo.value.HasValue)
				{
					if (!work.workProcedureNoMap.ContainsKey(workPar.workProcedureNo.value.Value))
					{
						throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidReference"), workPar.workProcedureNo.sheetName, workPar.workProcedureNo.cell);
					}

					workProcedure = work.workProcedureNoMap[workPar.workProcedureNo.value.Value];
					if (workSectEntity == null || workSectEntity.order != workProcedure.pageNo)
					{
						workSectEntity = new WorkParameterWorkSectionEntity
						{
							order = workProcedure.pageNo,
							fileName = workPar.fileName.value
						};

						if (workSectEntity.fileName != null)
						{
							if (!File.Exists(Path.Combine(workbookPath, ImagesDirName, workSectEntity.fileName)))
							{
								throw new ImportException(LocaleUtil.GetString("ExcelImport.WorkParameterImageNotFound", Path.Combine(ImagesDirName, workSectEntity.fileName)), workPar.fileName.sheetName, workPar.fileName.cell);
							}
							workSectEntity.physicalFileName = GetServerFileName(workSectEntity.fileName, ref fileSequence);
						}
						workEntity.workSection.Add(workSectEntity);
					}
					else
					{
						if (!String.IsNullOrWhiteSpace(workPar.fileName.value))
						{
							if (!String.IsNullOrWhiteSpace(workSectEntity.fileName) 
								&& !String.Equals(workSectEntity.fileName, workPar.fileName.value))
							{
								// 「この作業手順には、すでに別の画像ファイルが設定されています。画像ファイルを確認してください。」
								throw new ImportException(LocaleUtil.GetString("ExcelImport.diffImage"), workPar.fileName.sheetName, workPar.fileName.cell);
							}

							workSectEntity.fileName = workPar.fileName.value;
							if (!File.Exists(Path.Combine(workbookPath, ImagesDirName, workSectEntity.fileName)))
							{
								throw new ImportException(LocaleUtil.GetString("ExcelImport.WorkParameterImageNotFound", Path.Combine(ImagesDirName, workSectEntity.fileName)), workPar.fileName.sheetName, workPar.fileName.cell);
							}
							workSectEntity.physicalFileName = GetServerFileName(workSectEntity.fileName, ref fileSequence);
						}
					}
				}

				// チェック項目
				if (workSectEntity != null && workPar.checkItemNo.value.HasValue)
				{
					if (!workProcedure.checkItemNoMap.ContainsKey(workPar.checkItemNo.value.Value))
					{
						throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.InvalidReference"), workPar.checkItemNo.sheetName, workPar.checkItemNo.cell);
					}
					CheckItem checkParam = workProcedure.checkItemNoMap[workPar.checkItemNo.value.Value];

					var workCheckInfoEntity = new WorkParameterWorkCheckInfoEntity
					{
						order = checkParam.disp,
						hidden = workPar.visibility.value == CheckItemVisibility.Hidden,
						key = workPar.key.value,
						val = workPar.value.value,
						min = workPar.minValue.value,
						max = workPar.maxValue.value
					};
					workSectEntity.workCheckInfo.Add(workCheckInfoEntity);
				}
			}

			if (workParsEntity != null)
			{
				workParsEntity.workParameter = Utils.ToJsonString(workParEntity);
			}

			// ワイルドカード(*)を正規表現の ".*" に変換
			workParametersEntities.ForEach(o => o.itemNumber = o.itemNumber.Replace("*", ".*"));

			return workParametersEntities;
		}

		private static WorkflowEntity BuildWorkflowEntity(Model model, long userId, long parentId, HttpClient client)
		{
			bool isParallel;
			switch (model.workflow.workOrder.value)
			{
				case WorkOrder.Parallel:
					isParallel = true;
					break;
				case WorkOrder.Serial:
				default:
					isParallel = false;
					break;
			}

			var workflowEntity = new WorkflowEntity
			{
				parentId = parentId,
				workflowId = model.workflow.workflowId,
				workflowName = model.workflow.workflowName.value,
				workflowRev = model.workflow.rev.value,
				fkUpdatePersonId = userId,
				updateDatetime = DateTime.Now,
				workflowNumber = model.workflow.workNo.value,
				schedulePolicy = isParallel ? "PriorityParallel" : "PrioritySerial"
			};

			if (model.workflow.workflowId.HasValue)
			{
				workflowEntity.workflowDiaglam = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + BuildWorkflowDiagram(model, isParallel).OuterXml;

				Console.WriteLine(workflowEntity.workflowDiaglam);

				workflowEntity.conWorkflowWorks = new List<ConWorkflowWorkEntity>();
				int workflowOrder = 1;
				foreach (var work in model.works)
				{
					var conWorkflowWorkEntity = new ConWorkflowWorkEntity
					{
						workKbn = 0,
						fkWorkflowId = model.workflow.workflowId.Value,
						fkWorkId = work.workId.Value,
						skipFlag = false,
						workflowOrder = isParallel ? 10000 + (workflowOrder++) : (workflowOrder++) * 10000 + 1,
						standardStartTime = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc),
						standardEndTime = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc),
						workName = work.workName.value,
						workRev = work.rev.value
					};
					workflowEntity.conWorkflowWorks.Add(conWorkflowWorkEntity);

					if (work.option.value != null) { 
						foreach (var equipment in work.option.value.equipments)
						{
							long? equipmentId = AdFactoryClient.FindEquipmentIdByName(client, equipment.identify);
							if (equipmentId == null)
                            {
								// 作業者端末 [{0}] が見つかりません。adManager で確認してください。
								throw new ImportException(LocaleUtil.GetString("EquipmentNotFound", equipment.identify));
							}

							conWorkflowWorkEntity.equipments.Add(equipmentId.Value);
						}

						foreach (var organization in work.option.value.organizations)
						{
							long? organizationId = AdFactoryClient.FindUserIdByName(client, organization.identify);
							if (organizationId == null)
							{
								// 作業者 [{0}] が見つかりません。adManager で確認してください。
								throw new ImportException(LocaleUtil.GetString("OrganizationNotFound", organization.name));
							}

							conWorkflowWorkEntity.organizations.Add(organizationId.Value);
						}
					}
				}
			}

			return workflowEntity;
		}

		private static XmlDocument BuildWorkflowDiagram(Model model, bool isParallel)
		{
			XmlDocument doc = new XmlDocument();

			var definitionsNode = doc.CreateElement("definitions");
			doc.AppendChild(definitionsNode);
			definitionsNode.SetAttribute("targetNamespace", "http://www.adtek-fuji.co.jp/adfactory");

			var processNode = doc.CreateElement("process");
			definitionsNode.AppendChild(processNode);
			processNode.SetAttribute("isExecutable", "true");

			var startEventNode = doc.CreateElement("startEvent");
			processNode.AppendChild(startEventNode);
			startEventNode.SetAttribute("id", "start_id");
			startEventNode.SetAttribute("name", "start");

			var endEventNode = doc.CreateElement("endEvent");
			processNode.AppendChild(endEventNode);
			endEventNode.SetAttribute("id", "end_id");
			endEventNode.SetAttribute("name", "end");

			foreach (var work in model.works)
			{
				var taskNode = doc.CreateElement("task");
				processNode.AppendChild(taskNode);
				taskNode.SetAttribute("id", work.workId.ToString());
				taskNode.SetAttribute("name", work.workName.value + " : " + work.rev.value.ToString());
			}

			if (isParallel)
			{
				int seqId = 1;
				{
					var parallelGatewayNode = doc.CreateElement("parallelGateway");
					processNode.AppendChild(parallelGatewayNode);
					parallelGatewayNode.SetAttribute("pair", "parallelId_2");
					parallelGatewayNode.SetAttribute("id", "parallelId_1");
					parallelGatewayNode.SetAttribute("name", "parallelId_1");
				}
				{
					var parallelGatewayNode = doc.CreateElement("parallelGateway");
					processNode.AppendChild(parallelGatewayNode);
					parallelGatewayNode.SetAttribute("pair", "parallelId_1");
					parallelGatewayNode.SetAttribute("id", "parallelId_2");
					parallelGatewayNode.SetAttribute("name", "parallelId_2");
				}
				{
					var sequenceFlowNode = doc.CreateElement("sequenceFlow");
					processNode.AppendChild(sequenceFlowNode);
					sequenceFlowNode.SetAttribute("sourceRef", "start_id");
					sequenceFlowNode.SetAttribute("targetRef", "parallelId_1");
					sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
					sequenceFlowNode.SetAttribute("name", "");
				}
				{
					var sequenceFlowNode = doc.CreateElement("sequenceFlow");
					processNode.AppendChild(sequenceFlowNode);
					sequenceFlowNode.SetAttribute("sourceRef", "parallelId_2");
					sequenceFlowNode.SetAttribute("targetRef", "end_id");
					sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
					sequenceFlowNode.SetAttribute("name", "");
				}
				for (int i = 0; i < model.works.Count; i++)
				{
					{
						var sequenceFlowNode = doc.CreateElement("sequenceFlow");
						processNode.AppendChild(sequenceFlowNode);
						sequenceFlowNode.SetAttribute("sourceRef", "parallelId_1");
						sequenceFlowNode.SetAttribute("targetRef", model.works[i].workId.ToString());
						sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
						sequenceFlowNode.SetAttribute("name", "");
					}
					{
						var sequenceFlowNode = doc.CreateElement("sequenceFlow");
						processNode.AppendChild(sequenceFlowNode);
						sequenceFlowNode.SetAttribute("sourceRef", model.works[i].workId.ToString());
						sequenceFlowNode.SetAttribute("targetRef", "parallelId_2");
						sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
						sequenceFlowNode.SetAttribute("name", "");
					}
				}
			}
			else
			{
				int seqId = 1;
				Work prevWork = null;
				foreach (var work in model.works)
				{
					if (prevWork == null)
					{
						var sequenceFlowNode = doc.CreateElement("sequenceFlow");
						processNode.AppendChild(sequenceFlowNode);
						sequenceFlowNode.SetAttribute("sourceRef", "start_id");
						sequenceFlowNode.SetAttribute("targetRef", work.workId.ToString());
						sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
						sequenceFlowNode.SetAttribute("name", "");
					}
					else
					{
						var sequenceFlowNode = doc.CreateElement("sequenceFlow");
						processNode.AppendChild(sequenceFlowNode);
						sequenceFlowNode.SetAttribute("sourceRef", prevWork.workId.ToString());
						sequenceFlowNode.SetAttribute("targetRef", work.workId.ToString());
						sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
						sequenceFlowNode.SetAttribute("name", "");
					}
					prevWork = work;
				}
				if (prevWork != null)
				{
					var sequenceFlowNode = doc.CreateElement("sequenceFlow");
					processNode.AppendChild(sequenceFlowNode);
					sequenceFlowNode.SetAttribute("sourceRef", prevWork.workId.ToString());
					sequenceFlowNode.SetAttribute("targetRef", "end_id");
					sequenceFlowNode.SetAttribute("id", "seq" + (seqId++));
					sequenceFlowNode.SetAttribute("name", "");
				}
			}

			return doc;
		}

		private static CellValue<WorkProcedureImage> SaveImage(string fieldName, XmlNode baseNode, Excel.Worksheet sheet, ExcelCell baseCell,
			Work work, WorkProcedure workProcedure, RectangleGroups rectangleGroups, string imagesPath, ref int fileSequence)
		{
			string clientName = GetPictureFileName(work, workProcedure);
			var workProcedureImage = new WorkProcedureImage
			{
				timestamp = DateTime.Now,
				clientName = clientName,
				serverName = GetServerFileName(clientName, ref fileSequence)
			};

			CellValue<MemoryStream> buf = GetPicture(fieldName, baseNode, sheet, baseCell, rectangleGroups);
			if (buf.value == null)
			{
				// 画像の取得に失敗しました。青枠内に画像や図形を収めるように配置してください。
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.GetImageFailed"));
			}

			buf.value.Seek(0, SeekOrigin.Begin);
			using (Stream file = File.Create(Path.Combine(imagesPath, workProcedureImage.clientName)))
			{
				buf.value.CopyTo(file);
			}

			return new CellValue<WorkProcedureImage> { sheetName = buf.sheetName, cell = buf.cell, value = workProcedureImage };
		}

		private static void UploadWorkProcedureImages(UploadProgressForm progress, Model model, string workbookPath)
		{
			progress.SetCurrentProgressMax(model.works.Count);
			int workProgress = 0;
			foreach (var work in model.works)
			{
				progress.SetCurrentProgressValue(workProgress++, LocaleUtil.GetString("ExcelImport.DoImport.UploadingImages"));
				foreach (var workProcedure in work.workProcedureNoMap.Values)
				{
					if (workProcedure.image == null)
					{
						// 画像の取得に失敗しました。青枠内に画像や図形を収めるように配置してください。
						throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.GetImageFailed"));
					}

					var image = workProcedure.image.value;
					using (var file = File.OpenRead(Path.Combine(workbookPath, ImagesTempDirName, image.clientName)))
					{
						AdFactoryClient.UploadImage(0, image.serverName, file);
					}
				}
			}
		}

		private static void UploadWorkParameterImages(List<WorkParametersEntity> workParametersList, string workbookPath)
		{
			foreach (var workParameters in workParametersList)
			{
				var workParameterEntity = Utils.FromJsonString<WorkParameterEntity>(workParameters.workParameter);
				UploadWorkParameterImages(workParameterEntity, workbookPath);
			}
		}

		private static void UploadWorkParameterImages(WorkParameterEntity workParameterEntity, string workbookPath)
		{
			foreach (var work in workParameterEntity.work)
			{
				foreach (var workSection in work.workSection)
				{
					if (workSection.fileName == null)
					{
						continue;
					}
					using (var file = File.OpenRead(Path.Combine(workbookPath, ImagesDirName, workSection.fileName)))
					{
						AdFactoryClient.UploadImage(0, workSection.physicalFileName, file);
					}
				}
			}
		}

		public static bool ValidateWorkbook(XmlDocument excelFormat, Excel.Workbook workbook)
		{
			XmlNode workflowNode = excelFormat.DocumentElement.SelectSingleNode("/root/workflow");

			Excel.Sheets sheets = workbook.Sheets;

			string workflowSheetName = workflowNode.Attributes.GetNamedItem("sheet").Value;
			Excel.Worksheet workflowSheet;
			try
			{
				workflowSheet = sheets[workflowSheetName];
			}
			catch (COMException)
			{
				return false;
			}

			ExcelCell cell = ExcelUtils.GetCell("workflowName", workflowNode, null);
			cell.row -= 1;
			string workflowNameHeader = ExcelUtils.GetCellString(workflowSheet, cell);
			if (workflowNameHeader != "工程順名")
			{
				return false;
			}

			return true;
		}

		public static void DoImport(UploadProgressForm progress, XmlDocument excelFormat, Excel.Workbook workbook, bool isDryRun)
		{
			if (isDryRun)
			{
				progress.SetTotalProgressMax(1);
			}
			else
			{
				progress.SetTotalProgressMax(2);
			}

			progress.SetTotalProgressValue(0);

			long nextWorkId = 1;
			long nextWorkflowId = 1;
			long nextWorkHierarchyId = 1;
			long nextWorkflowHierarchyId = 1;

			string workbookPath = ExcelUtils.GetLocalPath(workbook.Path);
			if (string.IsNullOrEmpty(workbookPath) || !Directory.Exists(workbookPath))
			{
				throw new ImportException(LocaleUtil.GetString("ExcelImport.NotSaved"), null, null);
			}

			var res = new ImportWorkflowEntity();

			int fileSequence = 0;
			Model model = ImportModelExcel(progress, workbook, excelFormat, workbookPath, ref fileSequence);

			progress.SetTotalProgressValue(1);
			using (HttpClient client = AdFactoryClient.NewHttpClient())
			{
/*
				long? userIdOpt = AdFactoryClient.FindUserIdByName(client, model.workflow.updatePersonId.value);
				if (!userIdOpt.HasValue)
				{
					throw new ImportException("ユーザー「" + model.workflow.updatePersonId.value + "」が存在しません。",
						model.workflow.updatePersonId.sheetName, model.workflow.updatePersonId.cell);
				}
*/
				long? userIdOpt = Globals.ThisAddIn.LoginOrganizationId;
				if (!userIdOpt.HasValue)
				{
					if (isDryRun)
					{
						userIdOpt = 1;
					}
				}
				long userId = userIdOpt.Value;
				long parentWorkflowId = nextWorkflowHierarchyId++;

				var workflowHierarchyEntity = new WorkflowHierarchyEntity
				{
					hierarchyName = model.workflow.workflowHierarchyName.value,
					workflowHierarchyId = parentWorkflowId
				};
				res.workflowHierarchy = workflowHierarchyEntity;

				long parentWorkId = nextWorkHierarchyId++;
				var workHierarchyEntity = new WorkHierarchyEntity
				{
					hierarchyName = model.workflow.workflowHierarchyName.value,
					workHierarchyId = parentWorkId
				};
				res.workHierarchies.Add(workHierarchyEntity);

				foreach (var work in model.works)
				{
					work.workId = nextWorkId++;

					var workEntity = BuildWorkEntity(work, userId, parentWorkId);
					res.works.Add(workEntity);
				}

				model.workflow.workflowId = nextWorkflowId++;
				WorkflowEntity workflowEntity = BuildWorkflowEntity(model, userId, parentWorkflowId, client);
				res.workflow = workflowEntity;

				res.workParametersList = BuildWorkParametersEntity(model, workbookPath, ref fileSequence);

				if (!isDryRun)
				{
					UploadWorkProcedureImages(progress, model, workbookPath);
					UploadWorkParameterImages(res.workParametersList, workbookPath);
					progress.SetTotalProgressValue(2);

					AdFactoryClient.ImportWorkflow(client, res);
				}
			}
		}

	}
}
