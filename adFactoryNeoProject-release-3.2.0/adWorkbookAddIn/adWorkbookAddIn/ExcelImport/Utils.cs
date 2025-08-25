using System;
using System.Security.Cryptography;
using System.Text;
using System.Web.Script.Serialization;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Serialization;

namespace ExcelImport
{
	class Utils
	{
		public static string ToJsonString<Type>(Type obj)
		{
			var serializer = new JavaScriptSerializer();
			return serializer.Serialize(obj);
		}

		public static Type FromJsonString<Type>(string jsonStr)
		{
			var serializer = new JavaScriptSerializer();
			return (Type)serializer.Deserialize(jsonStr, typeof(Type));
		}

		public static string ToXmlString<Type>(Type obj)
		{
			// var serializer = new XmlSerializer(typeofType));

			using (var stream = new System.IO.MemoryStream())
			{
				var settings = new XmlWriterSettings
				{
					Indent = false, // インデントを無効化
					OmitXmlDeclaration = false, // XML宣言を省略
					Encoding = new UTF8Encoding(false) // BOMを排除
				};

				using (var writer = XmlWriter.Create(stream, settings))
				{
					var xns = new XmlSerializerNamespaces();
					xns.Add("", "");
					new XmlSerializer(typeof(Type)).Serialize(writer, obj, xns);
				}

				return Encoding.UTF8.GetString(stream.ToArray());
			}

			//using (var stream = new System.IO.MemoryStream())
			//{
			//	XmlWriterSettings settings = new XmlWriterSettings
			//	{
			//		//Indent = true,
			//		//IndentChars = "   ",
			//		//NewLineChars = "\n",
			//		Indent = false,
			//		OmitXmlDeclaration = true,
			//		//Encoding = new UTF8Encoding(false),
			//	};
			//	XmlWriter streamWriter = XmlWriter.Create(stream, settings);

			//	var xns = new XmlSerializerNamespaces();
			//	xns.Add("", "");
			//	serializer.Serialize(streamWriter, obj, xns);
			//	return Encoding.UTF8.GetString(stream.ToArray());
			//}
		}

		public static string GetHashSha256(string text)
		{
			byte[] bytes = Encoding.UTF8.GetBytes(text);
			SHA256Managed hashstring = new SHA256Managed();
			byte[] hash = hashstring.ComputeHash(bytes);
			string hashString = string.Empty;
			foreach (byte x in hash)
			{
				hashString += String.Format("{0:x2}", x);
			}
			return hashString;
		}

		public static void SuspendLayout(Control control)
		{
			control.SuspendLayout();
			foreach (Control child in control.Controls)
			{
				SuspendLayout(child);
			}
		}

		public static void ResumeLayout(Control control, bool performLayout)
		{
			control.ResumeLayout(performLayout);
			foreach (Control child in control.Controls)
			{
				ResumeLayout(child, performLayout);
			}
		}
	}
}
