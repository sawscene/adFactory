using Microsoft.Office.Tools.Ribbon;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Resources;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    class LocaleUtil
    {
        private static readonly ResourceManager rm = new ResourceManager("adWorkbookAddIn.Resources.Locale", typeof(LocaleUtil).Assembly);

        /// <summary>
        /// 現在のカルチャに基づくローカライズされた文字列を取得する。
        /// </summary>
        /// <param name="key">キー</param>
        /// <param name="args">引数</param>
        /// <returns>ローカライズされた文字列</returns>
        public static string GetString(string key, params object[] args)
        {
            //String localizedString = rm.GetString(key, new CultureInfo("en-US")); // 英語動作確認用 TODO Delete
            String localizedString = rm.GetString(key, CultureInfo.CurrentUICulture);
            if (localizedString == null)
            {
                return null;
            }
            return string.Format(localizedString, args);
        }

        /// <summary>
        /// コントロールにローカライズされた文字列を適用する。
        /// </summary>
        /// <param name="control">コントロール</param>
        public static void ApplyLocaleToControl(Control control)
        {
            string resourceKey;

            if (control.Parent == null)
            {
                // 最上位の親（フォームor作業ウインドウ）であれば、コントロール名をキーとする
                resourceKey = control.Name;
            }
            else
            {
                // 最上位の親の名前とコントロール名をキーとする
                Control current = control;
                while (current.Parent != null)
                {
                    current = current.Parent;
                }
                string topLevelParent = current.Name;
                resourceKey = $"{topLevelParent}.{control.Name}";
            }

            // リソースからローカライズされた文字列を取得できればコントロールに適用
            string localizedText = GetString(resourceKey);
            if (!string.IsNullOrEmpty(localizedText))
            {
                control.Text = localizedText;
            }

            // 全ての子コントロールを再帰的に走査
            foreach (Control child in control.Controls)
            {
                ApplyLocaleToControl(child);
            }
        }

        /// <summary>
        /// リボンにローカライズされた文字列を適用する。
        /// </summary>
        /// <param name="ribbon">リボン</param>
        public static void ApplyLocaleToRibbon(RibbonBase ribbon)
        {
            foreach (RibbonTab tab in ribbon.Tabs)
            {
                SetLocalizedText(tab, $"{ribbon.Name}.{tab.Name}");

                foreach (RibbonGroup group in tab.Groups)
                {
                    SetLocalizedText(group, $"{ribbon.Name}.{group.Name}");

                    foreach (var item in group.Items)
                    {
                        SetLocalizedText(item, $"{ribbon.Name}.{item.Name}");
                    }
                }
            }
        }

        /// <summary>
        /// リボンコントロールにローカライズされた文字列を設定する。
        /// </summary>
        /// <param name="ribbonControl">リボンコントロール</param>
        /// <param name="resourceKey">リソースのキー</param>
        private static void SetLocalizedText(object ribbonControl, string resourceKey)
        {
            string localizedText = GetString(resourceKey);
            if (!string.IsNullOrEmpty(localizedText))
            {
                if (ribbonControl is RibbonGroup group)
                {
                    group.Label = localizedText;
                }
                else if (ribbonControl is RibbonButton button)
                {
                    button.Label = localizedText;

                    // ヒントテキスト
                    button.ScreenTip = localizedText;

                    // 複数行のヒントテキスト
                    string superTip = GetString(resourceKey + "Tip");
                    if (!string.IsNullOrEmpty(superTip))
                    {
                        button.SuperTip = superTip;
                    }

                }
                else if (ribbonControl is RibbonCheckBox checkBox)
                {
                    checkBox.Label = localizedText;
                }
                else if (ribbonControl is RibbonDropDown dropDown)
                {
                    dropDown.Label = localizedText;
                }
                else if (ribbonControl is RibbonComboBox comboBox)
                {
                    comboBox.Label = localizedText;
                }
                else if (ribbonControl is RibbonEditBox editBox)
                {
                    editBox.Label = localizedText;
                }
                else if (ribbonControl is RibbonGallery gallery)
                {
                    gallery.Label = localizedText;
                }
                else if (ribbonControl is RibbonLabel label)
                {
                    label.Label = localizedText;
                }
                else if (ribbonControl is RibbonMenu menu)
                {
                    menu.Label = localizedText;
                }
                else if (ribbonControl is RibbonToggleButton toggleButton)
                {
                    toggleButton.Label = localizedText;
                }
                else
                {
                    throw new Exception($"対応していないリボンコントロール: {ribbonControl.GetType().Name}");
                }
            }
        }
    }
}
