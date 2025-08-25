///*********************************************************************
///  FileName    : ProcessLogger.cs
///  Function    : 1 プロセスで共有して使用するログクラス
///  History 
///    1.0.0.0   : 2015.05.20 新規作成
///
///    All Rights Reserved. Copyright (C) 2015 ADTEK FUJI CO., LTD.
///*********************************************************************
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Text;
using log4net;
using System.Reflection;

namespace DashboardCustomizeTool.CommonClass
{
    /// <summary>
    /// 1 プロセスで共有して使用するログクラスを提供する
    /// </summary>
    public static class ProcessLogger
    {
        /// <summary>
        /// ProcessLogger クラスの初期値を設定する
        /// </summary>
        static ProcessLogger()
        {
            StartSuffix = "Start: ";
            EndSuffix = "End: ";
        }

        /// <summary>
        /// log4net.ILog インタフェースを実装したロギングクラスを設定または取得する
        /// </summary>
        public static ILog Logger { get; set; }

        /// <summary>
        /// ログ出力開始の末尾語を設定または取得する
        /// </summary>
        public static string StartSuffix { get; set; }

        /// <summary>
        /// ログ出力終了の末尾語を設定または取得する
        /// </summary>
        public static string EndSuffix { get; set; }

        /// <summary>
        /// アプリケーション開始時にアプリケーション名とバージョンを出力する
        /// </summary>
        public static void StartApplication()
        {
            if (Logger != null)
            {
                var assembly = Assembly.GetExecutingAssembly();
                var assemblyName = assembly.GetName();
                var name = assembly.Location;
                var version = assemblyName.Version.ToString();

                Logger.InfoFormat("{0}({1}) Start.", name, version);
            }
        }

        /// <summary>
        /// アプリケーション終了時に出力される
        /// </summary>
        public static void EndApplication()
        {
            if (Logger != null)
            {
                var assembly = Assembly.GetExecutingAssembly();
                var assemblyName = assembly.GetName();

                Logger.InfoFormat("{0} End.", assemblyName.Name);
            }
        }

        /// <summary>
        /// メソッド開始時のログを出力する(リフレクションを使用するため、Type を指定するものと比較すると低速)
        /// 出力書式: StartSufix ClassName.MethodName
        /// </summary>
        /// <param name="methodName">メソッド名</param>
        public static void StartMethod([CallerMemberName] string methodName = "")
        {
            var stack = new StackFrame(1);
            StartMethod(stack.GetMethod().DeclaringType, methodName);
        }

        /// <summary>
        /// メソッド開始時のログを出力する(typeof(Class) で指定すれば高速)
        /// 出力書式: StartSufix ClassName.MethodName
        /// </summary>
        /// <param name="classType">呼び出し元の型</param>
        /// <param name="methodName">メソッド名</param>
        public static void StartMethod(Type classType, [CallerMemberName] string methodName = "")
        {
            if (Logger != null)
            {
                Logger.DebugFormat("{0}{1}.{2}", StartSuffix, classType.Name, methodName);
            }
        }

        /// <summary>
        /// メソッド開始時のログを引数指定して出力する
        /// 出力書式: StartSufix ClassName.MethodName(args)
        /// </summary>
        /// <param name="args">引数の列挙子</param>
        /// <param name="methodName">メソッド名</param>
        public static void StartMethodWithArgs(IEnumerable<string> args, [CallerMemberName] string methodName = "")
        {
            var stack = new StackFrame(1);
            StartMethodWithArgs(stack.GetMethod().DeclaringType, args, methodName);
        }

        /// <summary>
        /// メソッド開始時のログを引数指定して出力する
        /// 出力書式: StartSufix ClassName.MethodName(args)
        /// </summary>
        /// <param name="classType">呼び出し元の型</param>
        /// <param name="args">引数の列挙子</param>
        /// <param name="methodName">メソッド名</param>
        public static void StartMethodWithArgs(Type classType, IEnumerable<string> args, [CallerMemberName] string methodName = "")
        {
            if (Logger != null)
            {
                var stack = new StackFrame(1);
                var buffer = new StringBuilder(512);
                foreach (var item in args)
                {
                    buffer.AppendFormat("{0}, ", item);
                }

                Logger.DebugFormat("{0}{1}.{2}({3})", StartSuffix, classType.Name, methodName, buffer.Length > 1 ? buffer.ToString(0, buffer.Length - 2) : string.Empty);
            }
        }

        /// <summary>
        /// メソッド終了時のログを出力する(リフレクションを使用するため、Type を指定するものと比較すると低速)
        /// 出力書式: EndSufix ClassName.MethodName
        /// </summary>
        /// <param name="methodName">メソッド名</param>
        public static void EndMethod([CallerMemberName] string methodName = "")
        {
            var stack = new StackFrame(1);
            EndMethod(stack.GetMethod().DeclaringType, methodName);
        }

        /// <summary>
        /// メソッド終了時のログを出力する(typeof(Class) で指定すれば高速)
        /// 出力書式: EndSufix ClassName.MethodName
        /// </summary>
        /// <param name="classType">呼び出し元の型</param>
        /// <param name="methodName">メソッド名</param>
        public static void EndMethod(Type classType, [CallerMemberName] string methodName = "")
        {
            if (Logger != null)
            {
                Logger.DebugFormat("{0}{1}.{2}", EndSuffix, classType.Name, methodName);
            }
        }

        /// <summary>
        /// 例外発生時のログを出力する(Error(ex.Message, ex))
        /// </summary>
        /// <param name="ex">例外</param>
        public static void ExceptionOccurred(Exception ex)
        {
            ExceptionOccurred(ex.Message, ex);
        }

        /// <summary>
        /// 例外発生時のログを出力する(Error(message, ex))
        /// </summary>
        /// <param name="message">エラーメッセージ</param>
        /// <param name="ex">例外</param>
        public static void ExceptionOccurred(object message, Exception ex)
        {
            if (Logger != null)
            {
                WriteExceptionToDebugWindow(ex);
                Logger.Error(message, ex);
            }
        }

        /// <summary>
        /// 例外をデバッグウィンドウに出力する
        /// </summary>
        /// <param name="ex">発生した例外</param>
        [Conditional("DEBUG")]
        private static void WriteExceptionToDebugWindow(Exception ex)
        {
            var stack = new StackTrace(ex, true);
            if (stack.FrameCount > 0)
            {
                var frame = stack.GetFrame(stack.FrameCount - 1);
                Debug.WriteLine("Exception: {0}({1}) {2}", frame.GetFileName(), frame.GetFileLineNumber(), ex.Message);
            }
        }
    }
}
