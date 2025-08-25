using ExcelImport;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Net.Http;
using System.Windows.Forms;

namespace adWorkbookAddIn.Source
{
    public partial class EquipmentsForm : Form
    {
        public List<Equipment> Equipments { get; private set; }

        private const long USER_ID_ADMIN = 1;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="equipments">設備のリスト</param>
        public EquipmentsForm(List<Equipment> equipments)
        {
            InitializeComponent();
            Equipments = equipments;
            Resize += ResizeControl;

            LocaleUtil.ApplyLocaleToControl(this);
            LogUtils.AttachLoggingToControl(this);
        }

        /// <summary>
        /// フォーム読込時の処理
        /// </summary>
        /// <param name="sender">コントロールのオブジェクト</param>
        /// <param name="e">イベント引数</param>
        private void EquipmentsForm_Load(object sender, EventArgs e)
        {
            InitializeFormData();

            treeView.DrawMode = TreeViewDrawMode.OwnerDrawText;
            treeView.DrawNode += TreeView_DrawNode;
        }

        /// <summary>
        /// フォームデータの初期化
        /// </summary>
        private void InitializeFormData()
        {
            List<EquipmentEntity> equipmentList = new List<EquipmentEntity>();

            // 設備情報の取得
            HttpClient httpClient = AdFactoryClient.NewHttpClient();
            try
            {
                equipmentList = AdFactoryClient.FindEquipmentByUserId(httpClient, USER_ID_ADMIN);
            }
            catch (Exception)
            {
                MessageBox.Show(
                    LocaleUtil.GetString("key.connectionFailed") + "\r\n\r\n" + 
                    LocaleUtil.GetString("key.getEquipmentInfoFailed"),
                    LocaleUtil.GetString("key.error"),
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                Close();
            }

            Dictionary<int, TreeNode> equipmentDict = equipmentList
                .ToDictionary(
                    v => (int)v.equipmentId,
                    v => {
                        TreeNode treeNode = new TreeNode(v.equipmentName)
                        {
                            Tag = v.equipmentIdentify
                        };
                        return treeNode;
                    });

            // 階層の作成
            TreeNode rootNode = new TreeNode(LocaleUtil.GetString("EquipmentsForm.root"));
            foreach (EquipmentEntity equipment in equipmentList)
            {
                TreeNode childNode = equipmentDict[(int)equipment.equipmentId];

                if (equipment.parentId == null || equipment.parentId == 0)
                {
                    rootNode.Nodes.Add(childNode);
                }
                else
                {
                    TreeNode parentNode = equipmentDict[(int)equipment.parentId];
                    parentNode.Nodes.Add(childNode);
                }
            }
            treeView.Nodes.Add(rootNode);
            treeView.Nodes[0].Expand();

            // リストボックスの設定
            if (Equipments != null)
            {
                foreach (Equipment equipment in Equipments)
                {
                    if (equipmentList.Find(v => v.equipmentIdentify == equipment.identify) != null)
                    {
                        ListViewItem item = new ListViewItem(equipment.name)
                        {
                            Tag = equipment.identify
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
            foreach (ListViewItem equipmentItem in listBox.Items)
            {
                if ((string)equipmentItem.Tag == (string)selectedItem.Tag)
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
            Equipments = new List<Equipment>();
            foreach (ListViewItem listBox in listBox.Items)
            {
                Equipment equipment = new Equipment
                {
                    name = listBox.Text,
                    identify = (string)listBox.Tag
                };
                Equipments.Add(equipment);
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
