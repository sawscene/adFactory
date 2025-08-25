using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.IO;
using System.Text.RegularExpressions;

namespace DashboardCustomizeTool.CommonClass
{
    class IniFile2
    {
        /// <summary>
        /// 
        /// </summary>
        private string path;

        private Dictionary<string, string> dic;
        private List<string> sections;
        private Encoding enc;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="path"></param>
        public IniFile2(string path)
        {
            this.Init(path, Encoding.GetEncoding("shift-jis"));
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="path"></param>
        public IniFile2(string path, Encoding enc)
        {
            this.Init(path, enc);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="filePath"></param>
        /// <param name="encode"></param>
        private void Init(string filePath, Encoding enc)
        {
            this.path = filePath;
            this.enc = enc;

            this.dic = new Dictionary<string, string>();
            this.sections = new List<string>();

            this.Read();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="section"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        public string this[string section, string key]
        {
            set
            {
                string dicKey = section + "#" + key;
                if (this.dic.ContainsKey(dicKey))
                {
                    this.dic[dicKey] = value;
                }
                else
                {
                    this.dic.Add(dicKey, value);
                }
            }
            get
            {
                string dicKey = section + "#" + key;
                if (this.dic.ContainsKey(dicKey))
                {
                    return this.dic[dicKey];
                }
                else
                {
                    return string.Empty;
                }
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public string[] GetSection()
        {
            return this.sections.ToArray();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public bool Read()
        {
            bool result = false;
            try
            {
                string[] rows = File.ReadAllLines(this.path, this.enc);

                this.sections.Clear();

                string section = "";
                for (int i = 0; i < rows.Count(); i++)
                {
                    string row = rows[i];
                    if (string.IsNullOrEmpty(row))
                    {
                        continue;
                    }

                    if (Regex.IsMatch(row, @"^\[(.+)\]$"))
                    {
                        // セクション
                        section = Regex.Replace(row, @"^\[?|\]$", "");

                        if (!this.sections.Contains(section))
                        {
                            this.sections.Add(section);
                        }
                    }
                    else if (Regex.IsMatch(row, @"^(.+)=(.*)$"))
                    {
                        // キー
                        string key = Regex.Replace(row, @"(\s*)=(.*)", "");
                        // 値
                        string value = Regex.Replace(row, @"(.*)=(\s*)", "");

                        this.dic.Add(section + "#" + key, value);
                    }
                }
                result = true;
            }
            catch
            {
                return false;
            }
            return result;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public bool Write()
        {
            bool result = false;
            try
            {
                var items = this.dic.OrderBy((x) => x.Key);

                StringBuilder sb = new StringBuilder();

                string prevSection = "";
                foreach (var item in items)
                {
                    string section = Regex.Replace(item.Key, @"(\s*)#(.*)", "");
                    string key = Regex.Replace(item.Key, @"(.*)#(\s*)", "");

                    if (!section.Equals(prevSection))
                    {
                        prevSection = section;
                        sb.AppendLine(string.Format("[{0}]", section));
                    }

                    sb.AppendLine(string.Format("{0}={1}", key, item.Value));
                }

                // フォルダが存在しない場合作成する。
                string dir = Path.GetDirectoryName(this.path);
                if (!Directory.Exists(dir))
                {
                    Directory.CreateDirectory(dir);
                }

                File.WriteAllText(this.path, sb.ToString(), this.enc);

                result = true;
            }
            catch
            {
                return false;
            }
            return result;
        }
    }
}
