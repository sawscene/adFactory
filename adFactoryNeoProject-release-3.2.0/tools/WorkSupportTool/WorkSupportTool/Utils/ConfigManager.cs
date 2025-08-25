using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using WorkSupportTool.Common;

namespace WorkSupportTool.Utils
{
    /// <summary>
    /// 設定管理クラス
    /// </summary>
    public static class ConfigManager
    {
        private static readonly Dictionary<string, string> configValues = new Dictionary<string, string>();

        /// <summary>
        /// コンストラクタ
        /// </summary>
        static ConfigManager()
        {
            try
            {
                String filePath = AppDomain.CurrentDomain.BaseDirectory + Constants.AppName + ".ini";
                FileInfo fileInfo = new FileInfo(filePath);

                foreach (string line in File.ReadLines(fileInfo.ToString()).Where(s => !String.IsNullOrWhiteSpace(s) && !s.StartsWith("#") && s.Contains("=")))
                {
                    string key = line.Substring(0, line.IndexOf('='));
                    string value = line.Substring(line.IndexOf('=') + 1);
                    configValues[key] = value;
                }
            }
            catch (Exception ex)
            {
                Logging.ExceptionOccurred(ex);
            }
        }

        /// <summary>
        /// 設定値を取得する
        /// </summary>
        /// <param name="key"></param>
        /// <param name="defaultVale"></param>
        /// <returns></returns>
        public static string GetValueString(string key, string defaultVale)
        {
            if (configValues.ContainsKey(key))
            {
                return configValues[key] as String;
            }
            return defaultVale;
        }

        /// <summary>
        /// 設定値を取得する
        /// </summary>
        /// <param name="key"></param>
        /// <param name="defaultVale"></param>
        /// <returns></returns>
        public static int GetValueInt(string key, int defaultVale)
        {
            if (configValues.ContainsKey(key))
            {
                return int.Parse(configValues[key]);
            }
            return defaultVale;
        }
    }
}
