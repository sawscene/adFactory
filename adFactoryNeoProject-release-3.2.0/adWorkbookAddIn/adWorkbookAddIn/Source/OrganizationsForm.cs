using ExcelImport;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Net.Http;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class OrganizationsForm : Form
    {
        public List<Organization> Organizations { get; private set; }

        private const long USER_ID_ADMIN = 1;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="organizations">作業者のリスト</param>
        public OrganizationsForm(List<Organization> organizations)
        {
            InitializeComponent();
            Organizations = organizations;
            Resize += ResizeControl;

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">オブジェクトのコントロール</param>
        /// <param name="e">イベント引数</param>
        private void OrganizationsForm_Load(object sender, EventArgs e)
        {
            LocaleUtil.ApplyLocaleToControl(this);
            InitializeFormData();

            treeView.DrawMode = TreeViewDrawMode.OwnerDrawText;
            treeView.DrawNode += TreeView_DrawNode;
        }

        /// <summary>
        /// フォームデータの初期化
        /// </summary>
        private void InitializeFormData()
        {
            List<OrganizationEntity> organizationList = new List<OrganizationEntity>();

            // 設備情報の取得
            HttpClient httpClient = AdFactoryClient.NewHttpClient();
            try
            {
                organizationList = AdFactoryClient.FindOrganizationByUserId(httpClient, USER_ID_ADMIN);
            }
            catch (Exception)
            {
                MessageBox.Show(
                    LocaleUtil.GetString("key.connectionFailed") + "\r\n\r\n" +
                    LocaleUtil.GetString("key.getOrganizationInfoFailed"),
                    LocaleUtil.GetString("key.error"),
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                Close();
            }

            Dictionary<int, TreeNode> organizationDict = organizationList
                .ToDictionary(
                    v => (int)v.organizationId,
                    v => {
                        TreeNode treeNode = new TreeNode(v.organizationName);
                        treeNode.Tag = v.organizationIdentify;
                        return treeNode;
                    });

            // 階層の作成
            TreeNode rootNode = new TreeNode(LocaleUtil.GetString("OrganizationsForm.root"));
            foreach (OrganizationEntity organization in organizationList)
            {
                TreeNode childNode = organizationDict[(int)organization.organizationId];

                if (organization.parentId == null || organization.parentId == 0)
                {
                    rootNode.Nodes.Add(childNode);
                }
                else
                {
                    TreeNode parentNode = organizationDict[(int)organization.parentId];
                    parentNode.Nodes.Add(childNode);
                }
            }
            treeView.Nodes.Add(rootNode);
            treeView.Nodes[0].Expand();

            // リストボックスの設定
            if (Organizations != null)
            {
                foreach (Organization organization in Organizations)
                {
                    if (organizationList.Find(v => v.organizationIdentify == organization.identify) != null)
                    {
                        ListViewItem item = new ListViewItem(organization.name)
                        {
                            Tag = organization.identify
                        };
                        listBox.Items.Add(item);
                    }
                }
            }
        }

        /// <summary>
        /// ノード描画時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void TreeView_DrawNode(object sender, DrawTreeNodeEventArgs e)
        {
            if (e.Node.IsSelected)
            {
                if (treeView.Focused)
                {
                    e.Graphics.FillRectangle(SystemBrushes.Highlight, e.Bounds);
                    TextRenderer.DrawText(e.Graphics, e.Node.Text, treeView.Font, e.Bounds, SystemColors.HighlightText);
                }
                else
                {
                    e.Graphics.FillRectangle(Brushes.LightGray, e.Bounds);
                    TextRenderer.DrawText(e.Graphics, e.Node.Text, treeView.Font, e.Bounds, SystemColors.ControlText);
                }
            }
            else
            {
                e.DrawDefault = true;
            }
        }

        /// <summary>
        /// 追加ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void AddButton_Click(object sender, EventArgs e)
        {
            if (treeView.SelectedNode == null)
            {
                return;
            }

            if (treeView.SelectedNode == treeView.Nodes[0])
            {
                return;
            }

            ListViewItem selectedItem = new ListViewItem(treeView.SelectedNode.Text)
            {
                Tag = (string)treeView.SelectedNode.Tag
            };
            foreach (ListViewItem organizationItem in listBox.Items)
            {
                if ((string)organizationItem.Tag == (string)selectedItem.Tag)
                {
                    return;
                }
            }

            listBox.Items.Add(selectedItem);
        }

        /// <summary>
        /// 削除ボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void DeleteButton_Click(object sender, EventArgs e)
        {
            if (listBox.SelectedItems.Count == 0)
            {
                return;
            }

            ListViewItem selectedItem = listBox.SelectedItems[0];
            int index = selectedItem.Index;
            listBox.Items.Remove(selectedItem);

            if (listBox.Items.Count == 0)
            {
                return;
            }

            int newIndex = index > 0 ? index - 1 : 0;
            listBox.Items[newIndex].Selected = true;
            listBox.Items[newIndex].Focused = true;
        }

        /// <summary>
        /// OKボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void OkButton_Click(object sender, EventArgs e)
        {
            Organizations = new List<Organization>();
            foreach (ListViewItem listBox in listBox.Items)
            {
                Organization organization = new Organization
                {
                    name = listBox.Text,
                    identify = (string)listBox.Tag
                };
                Organizations.Add(organization);
            }
            DialogResult = DialogResult.OK;
            Close();
        }

        /// <summary>
        /// キャンセルボタン押下時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void CancelButton_Click(object sender, EventArgs e)
        {
            Close();
        }

        /// <summary>
        /// サイズ変更時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void ResizeControl(object sender, EventArgs e)
        {
            int width = (int)((mainPanel.Width - subPanel1.Width) * 0.5) - (listBox.Margin.Left + listBox.Margin.Right);
            int height = mainPanel.Height - subPanel2.Height - subPanel2.Margin.Bottom - listBox.Margin.Top - listBox.Margin.Bottom;
            listBox.Size = new Size(width, height);
            treeView.Size = new Size(width, height);

            int top = (int)(treeView.Height * 0.5 - subPanel1.Height * 0.5) + treeView.Margin.Top;
            subPanel1.Margin = new Padding(0, top, 0, 0);
        }
    }
}
