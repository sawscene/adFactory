using adWorkbookAddIn.Source;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ExcelImport
{
	class ImportException : Exception
	{
		public string SheetName { get; }
		public ExcelCell Cell { get; }

		public ImportException(string message) : base(message)
		{
		}

		public ImportException(string message, string sheetName, ExcelCell cell) : base(message)
		{
			this.SheetName = sheetName;
			this.Cell = cell == null ? null : new ExcelCell(cell);
		}
	}

	class CancelledException : Exception
	{
	}

	class ErrorUtils
	{
		public static CellValue<int> RequiredInt(CellValue<int?> value)
		{
			if (!value.value.HasValue)
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.Required"), value.sheetName, value.cell);
			}
			return new CellValue<int> { sheetName = value.sheetName, cell = value.cell, value = value.value.Value };
		}

		public static CellValue<Type> Required<Type>(CellValue<Type> value)
		{
			if (object.Equals(value.value, default(Type)))
			{
				throw new ImportException(LocaleUtil.GetString("ExcelUtils.Validate.Required"), value.sheetName, value.cell);
			}
			return value;
		}
	}
}
