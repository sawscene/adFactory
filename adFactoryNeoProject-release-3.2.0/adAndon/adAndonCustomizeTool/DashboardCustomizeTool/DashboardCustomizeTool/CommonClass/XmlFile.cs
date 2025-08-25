using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace DashboardCustomizeTool.CommonClass
{
    class XmlFile
    {
        /// <summary>
        /// XMLファイルの内容をXmlNodeに読み込む。
        ///
        /// </summary>
        /// <param name="path">XMLファイルパス</param>
        /// <returns>XmlNode</returns>
        public static XmlNode LoadFile(string path)
        {
            XmlDocument doc = new XmlDocument();
            doc.Load(path);
            return doc.DocumentElement;
        }

        /// <summary>
        /// XmlNodeの内容をXMLファイルに保存する。
        ///
        /// </summary>
        /// <param name="path">XMLファイルパス</param>
        /// <param name="node">XmlNode</param>
        public static void SaveFile(string path, XmlNode node)
        {
            if (node == null)
            {
                return;
            }

            StringBuilder sb = appendNodeString(new StringBuilder(), node, true);
            sb.AppendLine();

            // フォルダが存在しない場合作成する。
            string dir = Path.GetDirectoryName(path);
            if (!Directory.Exists(dir))
            {
                Directory.CreateDirectory(dir);
            }

            File.WriteAllText(path, sb.ToString(), new System.Text.UTF8Encoding(false));
        }

        /// <summary>
        /// XmlNode の内容を StringBuilder に追加する。
        /// </summary>
        /// <param name="sb">XmlNode の内容を追加する StringBuilder</param>
        /// <param name="node">追加する XmlNode</param>
        /// <param name="isRoot">ルートノードか</param>
        /// <returns>XmlNode の内容が追加された StringBuilder</returns>
        private static StringBuilder appendNodeString(StringBuilder sb, XmlNode node, bool isRoot)
        {
            if (!isRoot)
            {
                sb.AppendLine();
            }

            string nodeName = node.Name;
            sb.Append("<" + nodeName);
            if (XmlNodeType.Element.Equals(node.NodeType))
            {
                XmlNamedNodeMap attrNodes = node.Attributes;
                foreach (XmlNode attrNode in attrNodes)
                {
                    sb.Append(" " + attrNode.Name + "=\"" + attrNode.Value + "\"");
                }
            }

            if (node.HasChildNodes)
            {
                sb.Append(">");
                string value = null;
                foreach (XmlNode childNode in node.ChildNodes)
                {
                    if (XmlNodeType.Text.Equals(childNode.NodeType))
                    {
                        value = childNode.Value;
                    }
                    else
                    {
                        appendNodeString(sb, childNode, false);
                    }
                }

                if (!string.IsNullOrEmpty(value))
                {
                    sb.Append(value);
                }
                else
                {
                    sb.AppendLine();
                }
                sb.Append("</" + nodeName + ">");
            }
            else
            {
                if ("SOAP-ENC:Array".Equals(nodeName))
                {
                    sb.AppendLine(">");
                    sb.Append("</" + nodeName + ">");
                }
                else
                {
                    sb.Append("/>");
                }
            }

            return sb;
        }
    }
}
