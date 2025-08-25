using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Reflection;
using System.Windows.Forms;
using NLog;
using System.ComponentModel;
using System.Diagnostics;

namespace ExcelImport
{
    public static class LogUtils
    {
        /// <summary>
        /// コントロールに対してロギング処理をアタッチ
        /// </summary>
        /// <param name="control">コントロール</param>
        /// <param name="logger">ロガー</param>
        public static void AttachLoggingToControl(Control control, Logger logger = null)
        {
            // 親クラスごとのロガーインスタンスを取得
            if (logger == null)
            {
                logger = GetContainerLogger(control);
            }

            // コントロールの種類別にロギング処理をアタッチ
            if (control is Form)
            {
                AttachLoggingToForm((Form)control, logger);
            }
            if (control is UserControl)
            {
                AttachLoggingToUserControl((UserControl)control, logger);
            }
            if (control is Button)
            {
                AttachLoggingToButton((Button)control, logger);
            }
            else if (control is ComboBox)
            {
                AttachLoggingToComboBox((ComboBox)control, logger);
            }
            else if (control is ListBox)
            {
                AttachLoggingToListBox((ListBox)control, logger);
            }
            else if (control is CheckBox)
            {
                AttachLoggingToCheckBox((CheckBox)control, logger);
            }

            // 再帰的にすべての子コントロールを処理
            foreach (Control child in control.Controls)
            {
                AttachLoggingToControl(child, logger);
            }
        }

        /// <summary>
        /// フォームにロギングをアタッチ
        /// </summary>
        /// <param name="form">フォーム</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToForm(Form form, Logger logger = null)
        {
            if (logger == null)
            {
                logger = LogManager.GetLogger(form.GetType().FullName);
            }
            AttachLoggingToEvent(form, "Load", logger);
            AttachLoggingToEvent(form, "Shown", logger);
            AttachLoggingToEvent(form, "Closed", logger);
        }

        /// <summary>
        /// ユーザーコントロール（タスクペイン／作業ウインドウ）にロギングをアタッチ
        /// </summary>
        /// <param name="userControl">ユーザーコントロール</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToUserControl(UserControl userControl, Logger logger = null)
        {
            if (logger == null)
            {
                logger = LogManager.GetLogger(userControl.GetType().FullName);
            }
            AttachLoggingToEvent(userControl, "Load", logger);
        }

        /// <summary>
        /// ボタンにロギングをアタッチ
        /// </summary>
        /// <param name="button">ボタン</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToButton(Button button, Logger logger = null)
        {
            if (logger == null)
            {
                logger = GetContainerLogger(button);
            }
            AttachLoggingToEvent(button, "Click", logger);
        }

        /// <summary>
        /// コンボボックスにロギングをアタッチ
        /// </summary>
        /// <param name="comboBox">コンボボックス</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToComboBox(ComboBox comboBox, Logger logger = null)
        {
            if (logger == null)
            {
                logger = GetContainerLogger(comboBox);
            }
            AttachLoggingToEvent(comboBox, "SelectedIndexChanged", logger);
        }

        /// <summary>
        /// リストボックスにロギングをアタッチ
        /// </summary>
        /// <param name="listBox">リストボックス</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToListBox(ListBox listBox, Logger logger = null)
        {
            if (logger == null)
            {
                logger = GetContainerLogger(listBox);
            }
            AttachLoggingToEvent(listBox, "SelectedIndexChanged", logger);
        }

        /// <summary>
        /// チェックボックスにロギングをアタッチ
        /// </summary>
        /// <param name="checkBox">チェックボックス</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToCheckBox(CheckBox checkBox, Logger logger = null)
        {
            if (logger == null)
            {
                logger = GetContainerLogger(checkBox);
            }
            AttachLoggingToEvent(checkBox, "CheckedChanged", logger);
        }

        /// <summary>
        /// 最上位のコンテナコントロール（Form、UserControlなど）のロガーを取得
        /// </summary>
        /// <param name="control">コントロール</param>
        /// <returns>最上位のコンテナコントロール</returns>
        private static Logger GetContainerLogger(Control control)
        {
            Control current = control;
            while (current.GetContainerControl() is Control parentControl && parentControl != current)
            {
                current = parentControl;
            }
            return LogManager.GetLogger(current.GetType().FullName);
        }


        /// <summary>
        /// コントロールのイベントに対してロギング処理を追加
        /// </summary>
        /// <param name="control">コントロール</param>
        /// <param name="eventName">イベント名</param>
        /// <param name="logger">ロガー</param>
        private static void AttachLoggingToEvent(Control control, string eventName, Logger logger)
        {
            // 元のイベント情報を取得
            EventInfo eventInfo = control.GetType().GetEvent(eventName);
            if (eventInfo == null)
            {
                logger.Warn($"Event {eventName} not found on {control.GetType().FullName}");
                return;
            }

            // 元のハンドラを取得
            Delegate originalHandler = GetEventHandler(control, eventInfo);

            // 新しいハンドラを作成
            EventHandler newHandler = (sender, e) =>
            {
                string additionalInfo = GetEventAdditionalInfo((Control)sender);
                logger.Info($"{control.Name} {additionalInfo} {eventName} start.");
                try
                {
                    // 元のハンドラがあれば呼び出す
                    originalHandler?.DynamicInvoke(sender, e);
                }
                catch (Exception ex)
                {
                    logger.Error(ex, $"{control.Name} {additionalInfo} {eventName} error.");
                    throw;
                }
                finally
                {
                    logger.Info($"{control.Name} {additionalInfo} {eventName} end.");
                }
            };

            // 元のハンドラを削除して新しいハンドラを追加
            MethodInfo addMethod = eventInfo.GetAddMethod(true);
            MethodInfo removeMethod = eventInfo.GetRemoveMethod(true);
            removeMethod.Invoke(control, new object[] { originalHandler });
            addMethod.Invoke(control, new object[] { newHandler });
        }

        /// <summary>
        /// コントロールの追加情報を取得
        /// </summary>
        /// <param name="sender">コントロール</param>
        /// <returns>追加情報</returns>
        private static string GetEventAdditionalInfo(Control control)
        {
            if (control is ComboBox)
            {
                ComboBox comboBox = (ComboBox)control;
                return $"\"{comboBox.Text} [{comboBox.SelectedIndex}]\"";
            }
            else if (control is ListBox)
            {
                ListBox listBox = (ListBox)control;
                return $"\"{listBox.Text} [{listBox.SelectedIndex}]\"";
            }
            else if (control is CheckBox)
            {
                CheckBox checkBox = (CheckBox)control;
                return $"\"{checkBox.Text} [{checkBox.Checked}]\"";
            }
            return $"\"{control.Text}\"";
        }

        /// <summary>
        /// イベントハンドラを取得
        /// </summary>
        /// <param name="control">コントロール</param>
        /// <param name="eventInfo">イベント情報</param>
        /// <returns></returns>

        public static Delegate GetEventHandler(Control control, EventInfo eventInfo)
        {
            // リフレクションを使用してコントロールのイベントハンドラリストを取得
            PropertyInfo eventsProperty = control.GetType().GetProperty("Events", BindingFlags.NonPublic | BindingFlags.Instance);
            if (eventsProperty == null)
            {
                Debug.WriteLine($"Events property not found on {control.GetType().FullName}");
                return null;
            }
            EventHandlerList eventHandlerList = (EventHandlerList)eventsProperty.GetValue(control);

            // 親クラスも含めてイベントキーを探す
            FieldInfo eventKeyField = null;
            Type currentType = control.GetType();
            while (eventKeyField == null && currentType != null)
            {
                eventKeyField = currentType.GetField($"EVENT_{eventInfo.Name.ToUpper()}", BindingFlags.NonPublic | BindingFlags.Static)
                             ?? currentType.GetField($"Event{eventInfo.Name}", BindingFlags.NonPublic | BindingFlags.Static)
                             ?? currentType.GetField(eventInfo.Name, BindingFlags.NonPublic | BindingFlags.Static);
                currentType = currentType.BaseType;
            }
            if (eventKeyField == null)
            {
                Debug.WriteLine($"Event key for {eventInfo.Name} not found on {control.GetType().FullName} or its base types");
                return null;
            }
            object eventKey = eventKeyField.GetValue(null);
            
            // イベントキーからイベントハンドラを取得して返す
            Delegate originalHandler = eventHandlerList[eventKey];
            if (originalHandler == null)
            {
                Debug.WriteLine($"No handler found for {eventInfo.Name} on {control.GetType().FullName}");
            }
            return originalHandler;
        }
    }
}