using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using WorkSupportTool.Common;
using WorkSupportTool.Utils;

namespace WorkSupportTool.Net
{
    /// <summary>
    /// ファイル情報
    /// </summary>
    public class FtpFileInfo
    {
        public string Flags;
        public string Owner;
        public string Group;
        public bool IsDirectory;
        public string DateTime;
        public long Size; 
        public string Name;
    }
   
    /// <summary>
    /// ファイルリストの形式
    /// </summary>
    public enum FileListStyle
    {
        UnixStyle,
        WindowsStyle,
        Unknown  
    }

    /// <summary>
    /// FTPクライアント
    /// </summary>
    public class FtpClient
    {
        private readonly string destDir;
        private readonly string uploadUri;
        private readonly string userName;
        private readonly string password;
        private readonly int timeout;
        private readonly int maxRetry;
        private readonly string[] filePlefix;

        public String DownloadUri
        {
            get;
            set;
        }


        /// <summary>
        /// コンストラクタ
        /// </summary>
        public FtpClient()
        {
            this.destDir = ConfigManager.GetValueString(Constants.Download, String.Empty);
            this.DownloadUri = ConfigManager.GetValueString(Constants.Out, String.Empty);
            this.uploadUri = ConfigManager.GetValueString(Constants.In, String.Empty);
            this.userName = ConfigManager.GetValueString(Constants.User, String.Empty);
            this.password = CryptoUtils.Decrypt(ConfigManager.GetValueString(Constants.Password, String.Empty));
            this.timeout = ConfigManager.GetValueInt(Constants.Timeout, 3000);
            this.maxRetry = ConfigManager.GetValueInt(Constants.Retry, 3);
            // 接頭辞を取得
            this.filePlefix = Array.FindAll(Regex.Split(ConfigManager.GetValueString(Constants.FilePlefix, ""), @"^\s+|\s*,\s*|\s+$"),str => !String.IsNullOrEmpty(str));
        }

        /// <summary>
        /// ダウンロード対象ファイルか?
        /// </summary>
        public bool IsDownLoadTargetFileName(String fileName)
        {
            if (this.filePlefix.Length == 0)
            {
                return true;
            }

            foreach (string str in this.filePlefix)
            {
                if (fileName.StartsWith(str))
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// ファイル情報リストを取得する
        /// </summary>
        /// <returns></returns>
        public FtpFileInfo[] GetFileList()
        {
            WebResponse response = null;
            FtpFileInfo[] list = null;

            int retry = 0;
            while (retry <= this.maxRetry)
            {
                try
                {
                    Uri uri = new Uri(this.DownloadUri);
                    Logging.Logger.InfoFormat("ListDirectory({0}): {1}", retry, uri.ToString());

                    FtpWebRequest request = WebRequest.Create(uri) as FtpWebRequest;

                    request.Credentials = new NetworkCredential(this.userName, this.password);
                    request.Method = WebRequestMethods.Ftp.ListDirectoryDetails;
                    request.Timeout = this.timeout;
                    request.KeepAlive = false;
                    request.UsePassive = true;

                    response = request.GetResponse();
                    StreamReader reader = new StreamReader(response.GetResponseStream(), Encoding.UTF8);
                    string data = reader.ReadToEnd();
                    response.Close();

                    list = this.GetList(data);
                    foreach (FtpFileInfo thisstruct in list)
                    {
                        if (thisstruct.IsDirectory)
                        {
                            Logging.Logger.Info("<Dir> " + thisstruct.Name + "," + thisstruct.Owner + "," + thisstruct.Flags + "," + thisstruct.DateTime);
                        }
                        else
                        {
                            Logging.Logger.Info("<File> " + thisstruct.Name + "," + thisstruct.Owner + "," + thisstruct.Flags + "," + thisstruct.DateTime + "," + thisstruct.Size);
                        }
                    }

                    break;
                }
                catch (WebException ex)
                {
                    if (retry == maxRetry)
                    {
                        throw;
                    }
                    Logging.ExceptionOccurred(ex);
                    retry++;
                    Thread.Sleep(1000);
                }
                finally
                {
                    if (response != null)
                    {
                        response.Close();
                        response = null;
                    }
                }
            }

            return list;
        }
        
        /// <summary>
        /// ファイルをダウンロードする
        /// </summary>
        /// <param name="fileName">ダウンロードするファイル名</param>
        /// <param name="destPath">出力先のファイルパス</param>
        /// <param name="isOverride">出力先のファイルを上書きするかどうか</param>
        /// <returns></returns>
        public bool DownloadFile(string fileName, string destPath, bool isOverride)
        {
            Stream stream = null;
            FtpWebResponse response = null;

            int retry = 0;
            while (retry <= this.maxRetry)
            {
                try
                {
                    Uri uri = new Uri(this.DownloadUri+"/"+ fileName);
                    string path = String.IsNullOrEmpty(destPath) ? Path.Combine(this.destDir, fileName) : destPath;

                    if (!isOverride && File.Exists(path))
                    {
                        Logging.Logger.InfoFormat("File exists: {0}", path);
                        break;
                    }

                    Logging.Logger.InfoFormat("Download ({0}): {1} -> {2}", retry, uri.ToString(), path);

                    FtpWebRequest request = WebRequest.Create(uri) as FtpWebRequest;
                    request.Credentials = new NetworkCredential(this.userName, this.password);
                    request.Method = WebRequestMethods.Ftp.DownloadFile;
                    request.Timeout = this.timeout;
                    request.KeepAlive = false;
                    request.UsePassive = true;
                    request.UseBinary = true;

                    response = request.GetResponse() as FtpWebResponse;
                    stream = response.GetResponseStream();

                    Logging.Logger.InfoFormat("FtpStatusCode: {0}, {1}", response.StatusCode, response.StatusDescription);

                    using (FileStream fs = new FileStream(path, FileMode.Create, FileAccess.Write))
                    {
                        byte[] buffer = new byte[1024];
                        while (true)
                        {
                            int size = stream.Read(buffer, 0, buffer.Length);
                            if (size == 0)
                            {
                                break;
                            }
                            fs.Write(buffer, 0, size);
                        }
                        stream.Close();
                    }

                    Logging.Logger.Info("Download Successful!");

                    break;
                }
                catch (WebException ex)
                {
                    if (retry == maxRetry)
                    {
                        throw;
                    }
                    Logging.ExceptionOccurred(ex);
                    retry++;
                    Thread.Sleep(1000);
                }
                finally
                {
                    if (stream != null)
                    {
                        stream.Close();
                        stream = null;
                    }

                    if (response != null)
                    {
                        response.Close();
                        response = null;
                    }
                }
            }

            return true;
        }

        /// <summary>
        /// 作業データをアップロードする
        /// </summary>
        /// <param name="filePath"></param>
        /// <param name="isDelete"></param>
        /// <returns></returns>
        public bool UploadFile(String filePath, bool isDelete)
        {
            Stream stream = null; 
            FtpWebResponse response = null;

            int retry = 0;
            while (retry <= this.maxRetry)
            {
                try
                {
                    string fileName = Path.GetFileName(filePath);
                    Uri uri = new Uri(this.uploadUri + "/" + fileName);
                    Logging.Logger.InfoFormat("Upload ({0}): {1} -> {2}", retry, filePath, uri.ToString());

                    FtpWebRequest request = WebRequest.Create(uri) as FtpWebRequest;
                    request.Credentials = new NetworkCredential(this.userName, this.password);
                    request.Method = WebRequestMethods.Ftp.UploadFile;
                    request.Timeout = this.timeout;
                    request.KeepAlive = false;
                    request.UsePassive = true;
                    request.UseBinary = true;

                    stream = request.GetRequestStream();

                    using (FileStream fs = new FileStream(filePath, FileMode.Open, FileAccess.Read))
                    {
                        byte[] buffer = new byte[1024];
                        while (true)
                        {
                            int readSize = fs.Read(buffer, 0, buffer.Length);
                            if (readSize == 0)
                            {
                                break;
                            }
                            stream.Write(buffer, 0, readSize);
                        }
                        fs.Close();
                        stream.Close();
                    }
                    
                    response = request.GetResponse() as FtpWebResponse;
                    Logging.Logger.InfoFormat("FtpStatusCode: {0}, {1}", response.StatusCode, response.StatusDescription);

                    Logging.Logger.Info("Upload Successful!");

                    if (isDelete)
                    {
                        File.Delete(filePath);
                        Logging.Logger.InfoFormat("Delete: {0}", filePath);
                    }

                    break;
                }
                catch (WebException ex)
                {
                    if (retry == maxRetry)
                    {
                        throw;
                    }
                    Logging.ExceptionOccurred(ex);
                    retry++;
                    Thread.Sleep(1000);
                }
                finally
                {
                    if (stream != null)
                    {
                        stream.Close();
                        stream = null;
                    }

                    if (response != null)
                    {
                        response.Close();
                        response = null;
                    }
                }
            }

            return true;
        }

        /// <summary>
        /// ファイルを削除する
        /// </summary>
        /// <param name="fileName"></param>
        /// <returns></returns>
        public bool DeleteFile(String fileName)
        {
            FtpWebResponse response = null;

            int retry = 0;
            while (retry <= this.maxRetry)
            {
                try
                {
                    Uri uri = new Uri(this.DownloadUri + "/" + fileName);

                    Logging.Logger.InfoFormat("DeleteFile ({0}): {1}", retry, uri.ToString());

                    FtpWebRequest request = WebRequest.Create(uri) as FtpWebRequest;
                    request.Credentials = new NetworkCredential(this.userName, this.password);
                    request.Method = WebRequestMethods.Ftp.DeleteFile;
                    request.Timeout = this.timeout;
                    request.KeepAlive = false;
                    request.UsePassive = true;

                    response = request.GetResponse() as FtpWebResponse;

                    Logging.Logger.InfoFormat("FtpStatusCode: {0}, {1}", response.StatusCode, response.StatusDescription);
                    
                    break;
                }
                catch (WebException ex)
                {
                    if (retry == maxRetry)
                    {
                        throw;
                    }
                    Logging.ExceptionOccurred(ex);
                    retry++;
                    Thread.Sleep(1000);
                }
                finally
                {
                    if (response != null)
                    {
                        response.Close();
                        response = null;
                    }
                }
            }

            return true;
        }

        /// <summary>
        /// ファイルリストからファイル情報リストを取得する
        /// </summary>
        /// <param name="datastring"></param>
        /// <returns></returns>
        private FtpFileInfo[] GetList(string datastring)
        {
            List<FtpFileInfo> myListArray = new List<FtpFileInfo>();
            string[] dataRecords = datastring.Split('\n');
            FileListStyle _directoryListStyle = GuessFileListStyle(dataRecords);
            foreach (string s in dataRecords)
            {
                if (_directoryListStyle != FileListStyle.Unknown && s != "")
                {
                    FtpFileInfo f = new FtpFileInfo();
                    f.Name = "..";

                    switch (_directoryListStyle)
                    {
                        case FileListStyle.UnixStyle:
                            f = ParseFileStructFromUnixStyleRecord(s);
                            break;
                        case FileListStyle.WindowsStyle:
                            f = ParseFileStructFromWindowsStyleRecord(s);
                            break;
                    }

                    if (f != null && !(f.Name == "." || f.Name == ".."))
                    {
                        myListArray.Add(f);
                    }
                }
            }
            return myListArray.ToArray();
        }

        /// <summary>
        /// Windows形式のファイルリストを解析する
        /// </summary>
        /// <param name="Record"></param>
        /// <returns></returns>
        private FtpFileInfo ParseFileStructFromWindowsStyleRecord(string Record)
        {
            FtpFileInfo f = new FtpFileInfo();
            string processstr = Record.Trim();
            string dateStr = processstr.Substring(0, 8);
            processstr = (processstr.Substring(8, processstr.Length - 8)).Trim();
            string timeStr = processstr.Substring(0, 7);
            processstr = (processstr.Substring(7, processstr.Length - 7)).Trim();
            //f.DateTime = DateTime.Parse(dateStr + " " + timeStr);
            f.DateTime = dateStr + " " + timeStr;
            if (processstr.Substring(0, 5) == "<DIR>")
            {
                f.IsDirectory = true;
                processstr = (processstr.Substring(5, processstr.Length - 5)).Trim();
            }
            else
            {
                f.Size = Convert.ToInt64(processstr.Substring(0, processstr.IndexOf(' ')).Trim());
                processstr = processstr.Remove(0, processstr.IndexOf(' ') + 1);
                f.IsDirectory = false;
            }
            f.Name = processstr;
            return f;
        }
        
        /// <summary>
        /// ファイルリストの形式を取得する
        /// </summary>
        /// <param name="recordList"></param>
        /// <returns></returns>
        public FileListStyle GuessFileListStyle(string[] recordList)
        {
            foreach (string s in recordList)
            {
                if (s.Length > 10 && Regex.IsMatch(s.Substring(0, 10), "(-|d)(-|r)(-|w)(-|x)(-|r)(-|w)(-|x)(-|r)(-|w)(-|x)"))
                {
                    return FileListStyle.UnixStyle;
                }
                else if (s.Length > 8 && Regex.IsMatch(s.Substring(0, 8), "[0-9][0-9]-[0-9][0-9]-[0-9][0-9]"))
                {
                    return FileListStyle.WindowsStyle;
                }
            }
            return FileListStyle.Unknown;
        }

        /// <summary>
        /// Unix形式のファイルリストを解析する
        /// </summary>
        /// <param name="Record"></param>
        /// <returns></returns>
        private FtpFileInfo ParseFileStructFromUnixStyleRecord(string Record)
        {
            try
            {
                FtpFileInfo f = new FtpFileInfo();
                string processstr = Record.Trim();
                f.Flags = processstr.Substring(0, 9);
                f.IsDirectory = (f.Flags[0] == 'd');
                processstr = (processstr.Substring(11)).Trim();
                _cutSubstringFromStringWithTrim(ref processstr, ' ', 0); // skip one part
                f.Owner = _cutSubstringFromStringWithTrim(ref processstr, ' ', 0);
                f.Group = _cutSubstringFromStringWithTrim(ref processstr, ' ', 0);
                f.Size = Convert.ToInt64(_cutSubstringFromStringWithTrim(ref processstr, ' ', 0));
                //f.DateTime = DateTime.Parse(_cutSubstringFromStringWithTrim(ref processstr, ' ', 8));
                f.DateTime = _cutSubstringFromStringWithTrim(ref processstr, ' ', 8);
                f.Name = processstr;
                return f;
            }
            catch
            {
                // 例外を無視する (FTPサーバーがWU-FTPDの場合、ファイルリストに余分な情報が含まれているため)
            }

            return null;
        }

        /// <summary>
        /// 文字列処理
        /// </summary>
        /// <param name="s"></param>
        /// <param name="c"></param>
        /// <param name="startIndex"></param>
        /// <returns></returns>
        private string _cutSubstringFromStringWithTrim(ref string s, char c, int startIndex)
        {
            int pos1 = s.IndexOf(c, startIndex);
            string retString = s.Substring(0, pos1);
            s = (s.Substring(pos1)).Trim();
            return retString;
        }
    }
}
