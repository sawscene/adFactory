using ExcelImport;
using NLog;
using System;
using System.Net.Http;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class LoginForm : Form
    {
        private static readonly Logger logger = LogManager.GetCurrentClassLogger();

        private const string OPTION_NAME = "adWorkbookAddIn";

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public LoginForm()
        {
            InitializeComponent();

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void LoginForm_Load(object sender, EventArgs e)
        {
            serverTextBox.Text = Properties.Settings.Default.SERVER_HOSTNAME;
            loginIdTextBox.Text = Properties.Settings.Default.LOGIN_ID;
            saveLoginIdCheckBox.Checked = !string.IsNullOrEmpty(Properties.Settings.Default.LOGIN_ID);
        }

        /// <summary>
        /// ログインボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void LoginButton_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.None;

            using (HttpClient client = AdFactoryClient.NewHttpClient())
            {
                string loginId = loginIdTextBox.Text;
                bool isLicenseValid;
                OrganizationLoginResult reply;

                AdProperties.LoginID = "";

                string oldHostname = Properties.Settings.Default.SERVER_HOSTNAME;
                Properties.Settings.Default.SERVER_HOSTNAME = serverTextBox.Text;

                try
                {
                    try
                    {
                        isLicenseValid = AdFactoryClient.CheckLicense(client, OPTION_NAME);
                    }
                    catch (Exception ex)
                    {
                        logger.Error(ex);

                        MessageBox.Show(
                            LocaleUtil.GetString("key.connectionFailed"),
                            LocaleUtil.GetString("LoginForm.Title"),
                            MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }

                    if (!isLicenseValid)
                    {
                        MessageBox.Show(
                            LocaleUtil.GetString("key.licenseInvalid"),
                            LocaleUtil.GetString("LoginForm.Title"),
                            MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }

                    try
                    {
                        reply = AdFactoryClient.Login(client, loginId, passwordTextBox.Text);
                    }
                    catch (Exception ex)
                    {
                        logger.Error(ex);

                        MessageBox.Show(
                            LocaleUtil.GetString("key.connectionFailed"),
                            LocaleUtil.GetString("LoginForm.Title"),
                            MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }
                }
                finally
                {
                    Properties.Settings.Default.SERVER_HOSTNAME = oldHostname;
                }


                if (!reply.isSuccess)
                {
                    // ログインID または パスワードが正しくありません。
                    MessageBox.Show(
                        LocaleUtil.GetString("LoginForm.loginFailed"),
                        LocaleUtil.GetString("LoginForm.Title"),
                        MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                if (loginId.ToLower() != "admin" && !reply.roleAuthorities.Contains("EDITED_WORKFLOW"))
                {
                    // このアカウントにはリソースを編集する権限がありません。管理者にお問い合わせください。
                    MessageBox.Show(
                        LocaleUtil.GetString("LoginForm.notPermission"),
                        LocaleUtil.GetString("LoginForm.Title"),
                        MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                AdProperties.LoginID = loginIdTextBox.Text;

                Properties.Settings.Default.SERVER_HOSTNAME = serverTextBox.Text;
                if (saveLoginIdCheckBox.Checked)
                {
                    Properties.Settings.Default.LOGIN_ID = loginIdTextBox.Text;
                }
                else
                {
                    Properties.Settings.Default.LOGIN_ID = "";
                }
                Properties.Settings.Default.Save();

                Globals.ThisAddIn.LoginOrganizationId = reply.organizationId;
                Globals.ThisAddIn.LoginOrganizationLoginId = loginId;

                // ログインに成功した場合
                //MessageBox.Show(
                //    LocaleUtil.GetString("LoginForm.loginSucceed"),
                //    LocaleUtil.GetString("LoginForm.Title"),
                //    MessageBoxButtons.OK, MessageBoxIcon.Information);

                DialogResult = DialogResult.OK;
            }
        }
    }
}
