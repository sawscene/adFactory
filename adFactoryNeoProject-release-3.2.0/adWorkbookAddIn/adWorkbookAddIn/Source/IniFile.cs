using NLog;
using System;
using System.IO;
using System.Text;

namespace adWorkbookAddIn.Source
{
    /// <summary>
    /// INIファイル
    /// </summary>
    public class IniFile
    {
        private static readonly Logger logger = LogManager.GetCurrentClassLogger();
        
        /// <summary>
        /// INIファイルの値を読み込み
        /// </summary>
        public static string ReadIniValue(string section, string key, string defaultValue, string filePath)
        {
            try
            {
                // INIファイルを読み込む
                using (StreamReader reader = new StreamReader(filePath, Encoding.ASCII))
                {
                    string line;
                    string currentSection = null;  // 現在処理中のセクション

                    while ((line = reader.ReadLine()) != null)
                    {
                        // 各行を読み込み
                        line = line.Trim();
                        // セクションの始まりを検出
                        if (line.StartsWith("[") && line.EndsWith("]"))
                        {
                            currentSection = line.Substring(1, line.Length - 2).Trim();
                        }
                        else if (currentSection != null && currentSection.Equals(section, StringComparison.OrdinalIgnoreCase))
                        {
                            // 現在処理中のセクションが指定されたセクションと一致する場合
                            if (line.StartsWith($"{key}=", StringComparison.OrdinalIgnoreCase))
                            {
                                // 該当のキーが見つかった場合、値を取得
                                string value = line.Substring(key.Length + 1).Trim();

                                // ダブルクオーテーションで囲まれている場合は取り除く
                                if (!string.IsNullOrEmpty(value) && value.StartsWith("\"") && value.EndsWith("\""))
                                {
                                    value = value.Substring(1, value.Length - 2);
                                }
                                return value;
                            }
                        }
                    }
                }
                // 該当するキーが見つからなかった場合はデフォルト値を返す
                return defaultValue;
            }
            catch (Exception ex)
            {
                logger.Warn(ex);
                return defaultValue;
            }
        }
    }
}
