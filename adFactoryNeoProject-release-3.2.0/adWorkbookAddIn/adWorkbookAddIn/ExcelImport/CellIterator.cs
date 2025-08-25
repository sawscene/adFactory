using Microsoft.Office.Interop.Excel;
using System;
using System.Runtime.InteropServices;
using System.Xml;

namespace ExcelImport
{
	public class CellIterator
	{
		enum Direction
		{
			None,
			Row,
			Column,
			RowColumn,
			ColumnRow
		}

		ExcelCell cell;
		int? row = null;
		int? column = null;
		int? next = null;
		Direction direction = Direction.None;
		string termination = null;
		int? limit = null;

		public CellIterator(ExcelCell baseCell, XmlNode node)
		{
			string cellField = node.Attributes.GetNamedItem("cell")?.Value;
			string rowField = node.Attributes.GetNamedItem("row")?.Value;
			string columnField = node.Attributes.GetNamedItem("column")?.Value;
			string nextField = node.Attributes.GetNamedItem("next")?.Value;
			string directionField = node.Attributes.GetNamedItem("direction")?.Value;
			termination = node.Attributes.GetNamedItem("termination")?.Value;
			string limitField = node.Attributes.GetNamedItem("limit")?.Value;

			if (cellField != null)
			{
				cell = new ExcelCell(cellField);
			}
			else
			{
				cell = new ExcelCell(baseCell);
			}

			if (rowField != null)
			{
				row = int.Parse(rowField);
			}

			if (columnField != null)
			{
				column = int.Parse(columnField);
			}

			if (nextField != null)
			{
				next = int.Parse(nextField);
			}

			if (directionField != null)
			{
				switch (directionField)
				{
					case "row":
						direction = Direction.Row;
						break;
					case "column":
						direction = Direction.Column;
						break;
					case "row,column":
						direction = Direction.RowColumn;
						break;
					case "column,row":
						direction = Direction.ColumnRow;
						break;
					default:
						throw new ArgumentException("invalid `direction` value");
				}
			}

			if (limitField != null)
            {
				limit = int.Parse(limitField);
			}

			if (row != null)
			{
				cell.row += row.Value;
			}

			if (column != null)
			{
				cell.column += column.Value;
			}
		}

		private ExcelCell NextCell()
		{
			if (next == null)
			{
				return null;
			}

			switch (direction)
			{
				case Direction.Row:
					return new ExcelCell(cell.column, cell.row + next.Value);
				case Direction.Column:
					return new ExcelCell(cell.column + next.Value, cell.row);
			}

			return null;
		}

		public ExcelCell GetCell()
		{
			return new ExcelCell(cell);
		}

		public bool HasNext(Worksheet sheet)
		{
			string value = ExcelUtils.GetCellString(sheet, cell);
			if (value == termination)
			{
				return false;
			}

			if (!limit.HasValue)
			{
				return true;
			}

			return cell.row <= limit;
		}

		public void Next(Worksheet sheet)
		{
			cell = NextCell();
		}
	}
}
