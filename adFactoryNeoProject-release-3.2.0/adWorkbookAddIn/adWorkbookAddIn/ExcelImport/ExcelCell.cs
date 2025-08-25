using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ExcelImport
{

	public class ExcelCell
	{
		public int column;
		public int row;

		public ExcelCell(ExcelCell src)
		{
			this.column = src.column;
			this.row = src.row;
		}

		public ExcelCell(int column, int row)
		{
			this.column = column;
			this.row = row;
		}

		public ExcelCell(string str)
		{
			int i = 0;
			column = 0;
			while (i < str.Length && str[i] >= 'A' && str[i] <= 'Z')
			{
				int digit = str[i] - 'A' + 1;
				column = column * ('Z' - 'A' + 1) + digit;
				i++;
			}
			row = 0;
			while (i < str.Length && str[i] >= '0' && str[i] <= '9')
			{
				int digit = str[i] - '0';
				row = row * 10 + digit;
				i++;
			}
		}

		public override string ToString()
		{
			string res = "";
			int i = column - 1;
			while (i >= 0)
			{
				res = (char)('A' + (i % 26)) + res;
				i = i / 26 - 1;

			}
			res += row.ToString();
			return res;
		}
	}
}
