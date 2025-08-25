using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DashboardCustomizeTool.Data
{
    class ConfigData
    {
        /// <summary>
        /// 
        /// </summary>
        private string language;

        /// <summary>
        /// 
        /// </summary>
        private int monitorWidth;

        /// <summary>
        /// 
        /// </summary>
        private int monitorHeight;

        /// <summary>
        /// 
        /// </summary>
        private string initialDirectory;

        /// <summary>
        /// 
        /// </summary>
        public string Language
        {
            get { return this.language; }
            set { this.language = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        public int MonitorWidth
        {
            get { return this.monitorWidth; }
            set { this.monitorWidth = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        public int MonitorHeight
        {
            get { return this.monitorHeight; }
            set { this.monitorHeight = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        public string InitialDirectory
        {
            get { return this.initialDirectory; }
            set { this.initialDirectory = value; }
        }
    }
}
