using System.IO;

namespace DashboardCustomizeTool.CommonClass
{
    class FileUtils
    {
        /// <summary>
        /// ファイルを上書きコピーする。
        /// </summary>
        /// <param name="srcFilePath"></param>
        /// <param name="dstFilepath"></param>
        public static void overwriteCopyFile(string srcFilePath, string dstFilepath)
        {
            // コピー先ファイルを削除する。
            deleteFile(dstFilepath);
            // ファイルをコピーする。
            File.Copy(srcFilePath, dstFilepath);
        }

        /// <summary>
        /// ファイルを削除する。
        /// </summary>
        /// <param name="filePath"></param>
        public static void deleteFile(string filePath)
        {
            if (File.Exists(filePath))
            {
                File.Delete(filePath);
            }
        }
    }
}
