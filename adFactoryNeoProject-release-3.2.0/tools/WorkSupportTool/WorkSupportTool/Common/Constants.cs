using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WorkSupportTool.Common
{
    class Constants
    {
        public static String AppName = "WorkSupportTool";
        public static String DisplayName = "Work Support Tool";
        
        // WorkSupportTool.iniのキー名
        public static String Mode = "Mode";
        public static String Download = "Download";
        public static String History = "History";
        public static String Out = "Out";
        public static String In = "In";
        public static String Master = "Master"; 
        public static String User = "User";
        public static String Password = "Password";
        public static String Timeout = "Timeout";
        public static String Retry = "Retry";
        public static String FilePlefix = "WorkCodePrefix";

        public static String TsvExtension = ".tsv";
        public static String StatusOK = "工数連携エラーはありません";
    }
}
