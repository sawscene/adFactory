///////////////////////////////////////////////////////////////////////////
///  FileName : Logging.cs
///  Function : ログ出力 クラス
///  History 
///    1.0.0.0  : 2015.08.25 新規作成
///
///  All Rights Reserved. Copyright (C) 2015 ADTEK FUJI CO.,LTD.
///////////////////////////////////////////////////////////////////////////

using System;
using System.Diagnostics;

namespace WorkSupportTool.Common
{
    /// <summary>
    /// ログ出力用にlog4netのLoggerを宣言する
    /// </summary>
    public class Logging
    {
        private static string StartSuffix = "Start: ";
        private static string EndSuffix = "End: ";
 
        public static readonly log4net.ILog Logger = log4net.LogManager.GetLogger(
            System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        /// <summary>
        /// メソッド開始時のログを出力する
        /// 出力書式: StartSufix ClassName.MethodName
        /// </summary>
        /// <param name="classType">呼び出し元の型: typeof(Class)で指定すれば高速</param>
        /// <param name="methodName">メソッド名</param>
        public static void StartMethod(Type classType, string methodName = "")
        {
            if (Logger != null)
            {
                Logger.DebugFormat("{0}{1}.{2}", Logging.StartSuffix, classType.Name, methodName);
            }
        }

        /// <summary>
        /// メソッド終了時のログを出力する
        /// 出力書式: EndSufix ClassName.MethodName
        /// </summary>
        /// <param name="classType">呼び出し元の型: typeof(Class) で指定すれば高速</param>
        /// <param name="methodName">メソッド名</param>
        public static void EndMethod(Type classType, string methodName = "")
        {
            if (Logger != null)
            {
                Logger.DebugFormat("{0}{1}.{2}", EndSuffix, classType.Name, methodName);
            }
        }

        /// <summary>
        /// 例外発生時のログを出力する
        /// </summary>
        /// <param name="ex">例外</param>
        public static void ExceptionOccurred(Exception ex)
        {
            var stack = new StackTrace(ex, true);
            if (stack.FrameCount > 0)
            {
                var frame = stack.GetFrame(stack.FrameCount - 1);
                Debug.WriteLine("Exception: {0}({1}) {2}", frame.GetFileName(), frame.GetFileLineNumber(), ex.Message);
            }

            if (Logger != null)
            {
                Logger.Error(ex.Message, ex);
            }
        }
    }
}