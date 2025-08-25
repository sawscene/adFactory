using ExcelImport;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ExcelImport
{
	public class Rectangle
	{
		public float left;
		public float top;
		public float right;
		public float bottom;

		public Rectangle(float left, float top, float right, float bottom)
		{
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public float Width
		{
			get { return right - left; }
		}

		public float Height
		{
			get { return bottom - top; }
		}

		public bool Intersects(Rectangle other)
		{
			return Math.Max(left, other.left) < Math.Min(right, other.right) && Math.Max(top, other.top) < Math.Min(bottom, other.bottom);
		}
	}

	public class RectangleGroup
	{
		public Rectangle bounds = new Rectangle(float.PositiveInfinity, float.PositiveInfinity, -float.PositiveInfinity, -float.PositiveInfinity);

		public List<Rectangle> rectangles = new List<Rectangle>();

		public bool Intersects(Rectangle rect)
		{
			foreach (var it in rectangles)
			{
				if (rect.Intersects(it))
				{
					return true;
				}
			}
			return false;
		}

		public void Add(Rectangle rect)
		{
			rectangles.Add(rect);
			bounds.left = Math.Min(bounds.left, rect.left);
			bounds.top = Math.Min(bounds.top, rect.top);
			bounds.right = Math.Max(bounds.right, rect.right);
			bounds.bottom = Math.Max(bounds.bottom, rect.bottom);
		}
	}

	public class RectangleGroups
	{
		public List<RectangleGroup> groups = new List<RectangleGroup>();

		public void Add(Rectangle rect)
		{
			foreach (var group in groups)
			{
				if (group.Intersects(rect))
				{
					group.Add(rect);
					return;
				}
			}

			var newGroup = new RectangleGroup();
			newGroup.Add(rect);
			groups.Add(newGroup);
		}

		public RectangleGroup FindNearestLeftTop(float left, float top, float maxError)
		{
			float r = float.PositiveInfinity;
			RectangleGroup resGroup = null;
			foreach (var group in groups)
			{
				float dx = left - group.bounds.left;
				float dy = top - group.bounds.top;
				float curR = dx * dx + dy * dy;
				if (curR < r)
				{
					r = curR;
					resGroup = group;
				}
			}
			if (r > maxError * maxError)
			{
				return null;
			}
			return resGroup;
		}
	}

}
