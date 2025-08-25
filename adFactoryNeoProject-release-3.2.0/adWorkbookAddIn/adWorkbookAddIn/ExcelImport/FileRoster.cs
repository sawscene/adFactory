using adWorkbookAddIn;
using System;
using System.Collections.Generic;
using System.IO;
using Excel = Microsoft.Office.Interop.Excel;

namespace ExcelImport
{
	public class FileDataInfo
	{
		public FileDataInfo(string workbookName)
		{
			WorkbookName = workbookName;
		}

		public string WorkbookName { get; set; }

		private readonly Dictionary<Excel.Worksheet, WorkOption> workOptions = new Dictionary<Excel.Worksheet, WorkOption>();
		private readonly Dictionary<string, CheckItemOption> checkItemOptions = new Dictionary<string, CheckItemOption>();

		public void Clear()
		{
			workOptions.Clear();
			checkItemOptions.Clear();
		}

		public string GetFilePath()
		{
			var excelApplication = Globals.ThisAddIn.Application;
			var workbook = excelApplication.Workbooks[WorkbookName];
			string path = ExcelUtils.GetLocalPath(workbook.Path);
			return Path.Combine(path, WorkbookName + ".json");
		}

		private void LoadInt(Excel.Workbook workbook, FileData model)
		{
			Clear();
			if (model == null)
			{
				return;
			}
			foreach (var ent in model.workOptions) {
				Excel.Worksheet worksheet = workbook.Worksheets[ent.Key];
				workOptions[worksheet] = ent.Value;
			}
			foreach (var ent in model.checkItemOptions)
			{
				checkItemOptions[ent.Key] = ent.Value;
			}
		}

		private FileData SaveInt(Excel.Workbook workbook)
		{
			var model = new FileData();

			Excel.Sheets sheets = workbook.Sheets;
			var deletedSheets = new HashSet<Excel.Worksheet>(workOptions.Keys);

			foreach (Excel.Worksheet workSheet in sheets)
			{
				WorkOption workOption;
				if (workOptions.TryGetValue(workSheet, out workOption))
				{
					deletedSheets.Remove(workSheet);
					string sheetName = workSheet.Name;
					model.workOptions[sheetName] = workOption;
				}
			}
			foreach (Excel.Worksheet workSheet in deletedSheets)
			{
				workOptions.Remove(workSheet);
			}

			foreach (var ent in workOptions)
			{
				string sheetName = ent.Key.Name;
				model.workOptions[sheetName] = ent.Value;
			}
			foreach (var ent in checkItemOptions)
			{
				model.checkItemOptions[ent.Key] = ent.Value;
			}
			return model;
		}

		public void Load()
		{
			var excelApplication = Globals.ThisAddIn.Application;
			var workbook = excelApplication.Workbooks[WorkbookName];
			string path = ExcelUtils.GetLocalPath(workbook.Path);
			try
			{
				string fileContents;
				using (FileStream file = File.Open(Path.Combine(path, WorkbookName + ".json"), FileMode.Open, FileAccess.Read))
				{
					using (StreamReader reader = new StreamReader(file))
					{
						fileContents = reader.ReadToEnd();
					}
				}
				try
				{
					LoadInt(workbook, Utils.FromJsonString<FileData>(fileContents));
				}
				catch (Exception ex)
				{
					string timestamp = string.Format("{0:yyyyMMddHHmmss}", DateTime.Now);
					try
					{
						File.Move(Path.Combine(path, WorkbookName + ".json"), Path.Combine(path, WorkbookName + "_" + timestamp + ".json"));
					}
					catch (IOException)
					{
					}
					throw ex;
				}
			}
			catch (FileNotFoundException)
			{
				Clear();
			}
		}

		public void Save()
		{
			var excelApplication = Globals.ThisAddIn.Application;
			var workbook = excelApplication.Workbooks[WorkbookName];
			string path = ExcelUtils.GetLocalPath(workbook.Path);
			using (FileStream file = File.Open(Path.Combine(path, WorkbookName + ".json"), FileMode.Create))
			{
				using (StreamWriter writer = new StreamWriter(file))
				{
					var model = SaveInt(workbook);
					string fileContents = Utils.ToJsonString(model);
					writer.Write(fileContents);
				}
			}
		}

		public WorkOption GetWorkOption(Excel.Worksheet worksheet, bool create)
		{
			WorkOption workOption;
			if (!workOptions.TryGetValue(worksheet, out workOption))
			{
				if (!create)
				{
					return null;
				}
				workOption = new WorkOption();
				workOptions[worksheet] = workOption;
			}
			return workOption;
		}

		public CheckItemOption GetCheckItemOption(string tag, bool create)
		{
			CheckItemOption checkItemOption;
			if (!checkItemOptions.TryGetValue(tag, out checkItemOption))
			{
				if (!create)
				{
					return null;
				}
				checkItemOption = new CheckItemOption();
				checkItemOptions[tag] = checkItemOption;
			}
			return checkItemOption;
		}

		/// <summary>
		/// タグを変更する。
		/// </summary>
		/// <param name="oldTag"></param>
		/// <param name="newTag"></param>
		/// <param name="checkItemOption"></param>
		public void ChangeTag(string oldTag, string newTag, CheckItemOption checkItemOption) 
		{
			checkItemOptions.ContainsKey(newTag);
			checkItemOptions[newTag] = checkItemOption;

			HashSet<string> deletedTags = new HashSet<string>(checkItemOptions.Keys);
			deletedTags.Remove(oldTag);
			checkItemOptions.Remove(oldTag);
		}

		public void DeleteCheckItemOptionUnusedTags(HashSet<string> usedTags)
		{
			HashSet<string> deletedTags = new HashSet<string>(checkItemOptions.Keys);
			foreach (var tag in usedTags)
			{
				deletedTags.Remove(tag);
			}
			foreach (var tag in deletedTags)
			{
				checkItemOptions.Remove(tag);
			}
		}
	}

	public class FileRoster
	{
		private readonly Dictionary<Excel.Workbook, FileDataInfo> fileDatas = new Dictionary<Excel.Workbook, FileDataInfo>();

		private FileDataInfo GetInfo(Excel.Workbook workbook, bool create)
		{
			FileDataInfo fileDataInfo;
			if (!fileDatas.TryGetValue(workbook, out fileDataInfo))
			{
				if (!create)
				{
					return null;
				}
				string workbookName = workbook.Name;
				fileDataInfo = new FileDataInfo(workbookName);
				fileDataInfo.Clear();
				fileDatas[workbook] = fileDataInfo;
			}
			return fileDataInfo;
		}

		public WorkOption GetWorkOption(Excel.Workbook workbook, Excel.Worksheet worksheet, bool create)
		{
			FileDataInfo fileDataInfo = GetInfo(workbook, create);
			if (fileDataInfo == null)
			{
				return null;
			}
			return fileDataInfo.GetWorkOption(worksheet, create);
		}

		public CheckItemOption GetCheckItemOption(Excel.Workbook workbook, string tag, bool create)
		{
			FileDataInfo fileDataInfo = GetInfo(workbook, create);
			if (fileDataInfo == null)
			{
				return null;
			}
			return fileDataInfo.GetCheckItemOption(tag, create);
		}

		/// <summary>
		/// タグを変更する。
		/// </summary>
		/// <param name="workbook"></param>
		/// <param name="oldTag"></param>
		/// <param name="newTag"></param>
		/// <param name="checkItemOption"></param>
		public void ChangeTag(Excel.Workbook workbook, string oldTag, string newTag, CheckItemOption checkItemOption)
		{
			FileDataInfo fileDataInfo = GetInfo(workbook, false);
			if (fileDataInfo == null)
			{
				return;
			}
			fileDataInfo.ChangeTag(oldTag, newTag, checkItemOption);
		}

		public void DeleteCheckItemOptionUnusedTags(Excel.Workbook workbook, HashSet<string> usedTags)
		{
			FileDataInfo fileDataInfo = GetInfo(workbook, false);
			if (fileDataInfo == null)
			{
				return;
			}
			fileDataInfo.DeleteCheckItemOptionUnusedTags(usedTags);
		}

		public void OnOpen(Excel.Workbook workbook)
		{
			string workbookName = workbook.Name;
			var fileDataInfo = new FileDataInfo(workbookName);
			fileDataInfo.Load();
			fileDatas[workbook] = fileDataInfo;
		}

		public void OnClose(Excel.Workbook workbook)
		{
			fileDatas.Remove(workbook);
		}

		public void OnSave(Excel.Workbook workbook)
		{
			if (!fileDatas.TryGetValue(workbook, out FileDataInfo fileDataInfo))
			{
				return;
			}
			string workbookName = workbook.Name;
			fileDataInfo.WorkbookName = workbookName;
			fileDataInfo.Save();
		}
	}
}
