using System.Drawing;
using System.Linq;
using System.Windows.Forms;

namespace adWorkbookAddIn
{
    /// <summary>
    /// カラー選択コンボボックス
    /// </summary>
    class ColorComboBox : System.Windows.Forms.ComboBox
    {
        /// <summary>
        /// コンストラクタ
        /// </summary>
        public ColorComboBox()
        {
            DataSource = typeof(Color).GetProperties()
                .Where(x => x.PropertyType == typeof(Color))
                .Select(x => x.GetValue(null)).ToList();
            this.MaxDropDownItems = 10;
            this.IntegralHeight = false;
            this.DrawMode = DrawMode.OwnerDrawFixed;
            this.DropDownStyle = ComboBoxStyle.DropDownList;
            this.DrawItem += CustomDrawItem;
        }

        /// <summary>
        /// アイテム描画
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void CustomDrawItem(object sender, DrawItemEventArgs e)
        {
            e.DrawBackground();
            if (e.Index >= 0)
            {
                var text = this.GetItemText(this.Items[e.Index]);
                Color color = (Color)this.Items[e.Index];
                Rectangle r1 = new Rectangle(e.Bounds.Left + 1, e.Bounds.Top + 1, 2 * (e.Bounds.Height - 2), e.Bounds.Height - 2);
                Rectangle r2 = Rectangle.FromLTRB(r1.Right + 2, e.Bounds.Top, e.Bounds.Right, e.Bounds.Bottom);
                using (var b = new SolidBrush(color))
                    e.Graphics.FillRectangle(b, r1);
                e.Graphics.DrawRectangle(Pens.Black, r1);
                TextRenderer.DrawText(e.Graphics, color.Name, this.Font, r2, this.ForeColor, TextFormatFlags.Left | TextFormatFlags.VerticalCenter);
            }
        }
    }
}
