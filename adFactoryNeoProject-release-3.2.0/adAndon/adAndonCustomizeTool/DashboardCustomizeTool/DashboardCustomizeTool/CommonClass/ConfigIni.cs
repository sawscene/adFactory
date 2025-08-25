using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using DashboardCustomizeTool.Data;

namespace DashboardCustomizeTool.CommonClass
{
    class ConfigIni
    {
        private const string CONFIG_SECTION = "Config";

        private const string LANGUAGE_KEY = "language";
        private const string MONITOR_WIDTH_KEY = "monitorWidth";
        private const string MONITOR_HEIGHT_KEY = "monitorHeight";
        private const string INITIAL_DIRECTORY_KEY = "initialDirectory";

        private Encoding enc = new System.Text.UTF8Encoding(false);

        private string iniFilePath;

        private static ConfigIni instance = null;
        public static ConfigIni GetInstance()
        {
            if (instance == null)
            {
                instance = new ConfigIni();
            }
            return instance;
        }

        private ConfigData configData = new ConfigData();
        public ConfigData GetConfigData
        {
            get { return configData; }
            set { configData = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        public ConfigIni()
        {
            Settings settings = Settings.GetInstance();
            this.iniFilePath = settings.AppIniFilePath;
        }

        /// <summary>
        /// 設定ファイルを読み込む。
        /// </summary>
        public void LoadFile()
        {
            IniFile2 ini = new IniFile2(this.iniFilePath, enc);

            // [Config] language
            string language = ini[CONFIG_SECTION, LANGUAGE_KEY];
            configData.Language = language;

            // [Config] monitorWidth
            string monitorWidth = ini[CONFIG_SECTION, MONITOR_WIDTH_KEY];
            int monitorWidthNum;
            if (string.IsNullOrEmpty(monitorWidth) || !int.TryParse(monitorWidth, out monitorWidthNum))
            {
                monitorWidthNum = 1920;
            }
            configData.MonitorWidth = monitorWidthNum;

            // [Config] monitorHeight
            string monitorHeight = ini[CONFIG_SECTION, MONITOR_HEIGHT_KEY];
            int monitorHeightNum;
            if (string.IsNullOrEmpty(monitorHeight) || !int.TryParse(monitorHeight, out monitorHeightNum))
            {
                monitorHeightNum = 1080;
            }
            configData.MonitorHeight = monitorHeightNum;

            // [Config] initialDirectory
            string initialDirectory = ini[CONFIG_SECTION, INITIAL_DIRECTORY_KEY];
            configData.InitialDirectory = initialDirectory;
        }

        /// <summary>
        /// 設定ファイルを保存する。
        /// </summary>
        public void SaveFile()
        {
            IniFile2 ini = new IniFile2(this.iniFilePath, enc);

            // [Config] language
            ini[CONFIG_SECTION, LANGUAGE_KEY] = this.GetConfigData.Language;
            // [Config] monitorWidth
            ini[CONFIG_SECTION, MONITOR_WIDTH_KEY] = this.GetConfigData.MonitorWidth.ToString();
            // [Config] monitorHeight
            ini[CONFIG_SECTION, MONITOR_HEIGHT_KEY] = this.GetConfigData.MonitorHeight.ToString();
            // [Config] initialDirectory
            ini[CONFIG_SECTION, INITIAL_DIRECTORY_KEY] = this.GetConfigData.InitialDirectory;

            ini.Write();
        }
    }
}
