using System;
using System.Collections.Generic;
using System.Runtime.Serialization;

namespace ExcelImport
{
	public class CellValue<Type>
	{
		public string sheetName;
		public ExcelCell cell;
		public Type value;
	}

	public class Model
	{
		public Workflow workflow;
		public List<Work> works = new List<Work>();
	}

	public enum WorkOrder
	{
		None,
		Parallel,
		Serial
	}

	public class Workflow
	{
		// 工程順ID
		public long? workflowId;
		// 工程順名
		public CellValue<string> workflowName;
		// Rev
		public CellValue<int> rev;
		// 作業番号
		public CellValue<string> workNo;
		// 作業順序
		public CellValue<WorkOrder> workOrder;
		// 承認者1
		public CellValue<string> approverId1;
		// 承認者2
		public CellValue<string> approverId2;
		// 更新者
		public CellValue<string> updatePersonId;
		// 工程順階層
		public CellValue<string> workflowHierarchyName;
		public List<WorkParameter> workParameters = new List<WorkParameter>();
	}

	public enum CheckItemVisibility
	{
		None,
		Visible,
		Hidden
	}

	public class WorkParameter
	{
		// 品番
		public CellValue<string> itemNumber;
		// 工程番号
		public CellValue<int?> workNo;
		// タクトタイム
		public CellValue<string> taktTime;
		public CellValue<int?> workProcedureNo;
		// 画像
		public CellValue<string> fileName;
		public CellValue<int?> checkItemNo;
		// 項目名
		public CellValue<string> key;
		// 現在値
		public CellValue<string> value;
		// 基準値(最小)
		public CellValue<double?> minValue;
		// 基準値(最大)
		public CellValue<double?> maxValue;
		// 表示性
		public CellValue<CheckItemVisibility> visibility;
	}


	public class Work
	{
		public long? workId;
		public string sheetName;
		// 工程名
		public CellValue<string> workName;
		// Rev
		public CellValue<int> rev;
		// 作業番号
		public CellValue<string> workNo;
		// タクトタイム
		public CellValue<string> taktTime;
		public CellValue<string> workHierarchyName;
		public CellValue<WorkOption> option;
		public SortedDictionary<int, WorkProcedure> workProcedureNoMap = new SortedDictionary<int, WorkProcedure>();
	}

	public enum AdditionalOptionType
	{
		NONE = 0,
		TYPE_STRING,
		TYPE_BOOLEAN,
		TYPE_INTEGER,
		TYPE_NUMERIC,
		TYPE_DATE,
		TYPE_IP4_ADDRESS,
		TYPE_MAC_ADDRESS,
		TYPE_PLUGIN,
		TYPE_TRACE,
		TYPE_DEFECT
	}

	public enum DisplayItemTarget
	{
		KANBAN,
		WORKKANBAN,
		WORK
	}

	public class DisplayItem
	{
		// 対象
		public DisplayItemTarget target;
		// 項目名
		public string name;
	}

	public class AdditionalOption
	{
		// 項目名
		public string name;
		// タイプ
		public AdditionalOptionType type;
		// 現在値
		public string value;
	}

	public class Equipment
	{
		// 設備名
		public string name;
		// 設備識別名
		public string identify;
	}

	public class Organization
	{
		// 組織名
		public string name;
		// 組織識別名
		public string identify;
	}

	public class WorkOption
	{
		public List<Equipment> equipments = new List<Equipment>();
		public List<Organization> organizations = new List<Organization>();
		public List<DisplayItem> displayItems = new List<DisplayItem>();
		public List<AdditionalOption> additionalOptions = new List<AdditionalOption>();
		public string backgroundColor = "#FFFFFF";
		public string fontColor = "#000000";
	}

	public class WorkProcedureImage
	{
		public long workId;
		public DateTime timestamp;
		public string clientName;
		public string serverName;
	}

	public class WorkProcedure
	{
		public CellValue<int> workProcedureNo;
		public CellValue<string> workProcedureName;
		public CellValue<WorkProcedureImage> image;
		public CellValue<string> workMethod;
		public CellValue<string> workPoint;
		public SortedDictionary<int, CheckItem> checkItemNoMap = new SortedDictionary<int, CheckItem>();

		public int pageNo;
	}

	public class CheckItem
	{
		// チェック項目番号
		public CellValue<int> checkItemNo;
		// チェック項目 (作業内容)
		public CellValue<string> checkItemName;
		// 現在値
		public CellValue<string> value;
		// 基準値(最小)
		public CellValue<double?> minValue;
		// 基準値(最大)
		public CellValue<double?> maxValue;
		public CellValue<string> tag;
		public CellValue<CheckItemOption> option;

		public int disp;
	}

	public enum CheckItemType
	{
		NONE = 0,
		PARTS,
		WORK,
		INSPECTION,
		MEASURE,
		CUSTOM,
		TIMESTAMP,
		TIMER,
		JUDG,
		PRODUCT
	}

	public enum CheckItemInputType
	{
		NONE = 0,
		TEXT_KEYBOARD,
		TEN_KEYBOARD,
		CALENDAR_DATETIME,
		TEXT_BARCODE_INPUT,
		LIST_INPUT,
		ATTACH_FILE,
		CALCULATION_RESULTS,
		HANDWRITTEN_TEXT_INPUT,
		VOICE_TEXT_INPUT
	}

	public enum CheckItemDatetimeFormat
	{
		DATETIME_INPUT_OPTION = 1,
		DATE_INPUT_OPTION,
		TIME_INPUT_OPTION
	}

	public class ListWithColor
	{
		// 値
		public string value;
		// 文字色
		public string fontColor;
		// 背景色
		public string backgroundColor;
	}

	public class CustomValue
	{
		// 項目名
		public string name;
		// 現在値
		public string value;
	}

	public class DetailSettings
	{
		// 製品単位に入力する
		public bool bulkInput;
		// 工程名
		public string work;
		// タグ
		public string property;
		// 整数有効桁数
		public int? integerDigits;
		// 小数有効桁数
		public int? decimalDigits;
		// 基準値を±表示
		public bool absoluteDisplay;
		// プラグイン
		public string plugin;
		// カスタム入力フィールド
		public int? customFields;
		// 入力データの区切り文字
		public string delimiter;
		// 前回の入力情報を保持する
		public bool holdPrevData;
		// 製造番号を登録する
		public bool inputProductNum;
		// カメラでQRコードを読み取る
		public bool qrRead;
	}

	public class CheckItemOption
	{
		// 種別
		public CheckItemType type;
		// 入力種
		public CheckItemInputType inputType;
		// 初期値
		public string initialValue;
		// 色付き入力値リスト
		public List<ListWithColor> listWithColors = new List<ListWithColor>();
		// 計算結果
		public string calculationResult;
		// 日付形式
		public CheckItemDatetimeFormat datetimeFormat;
		// 必須入力
		public bool isRequired;
		// 直接入力禁止
		public bool isNoDirectInput;
		// 重複入力禁止
		public bool isNoDuplicateInput;
		// 入力規則
		public string rule;
		// コメント入力
		public bool isCommentInput;
		// コメント表示
		public bool isCommentDisplay;
		// 設備
		public List<Equipment> equipments = new List<Equipment>();
		// 詳細設定
		public DetailSettings detailSettings = new DetailSettings();
		// カスタム設定値リスト
		public List<CustomValue> customValues = new List<CustomValue>();

		public void Clear()
		{
			type = CheckItemType.WORK;
			inputType = CheckItemInputType.NONE;
			initialValue = null;
			listWithColors = new List<ListWithColor>();
			calculationResult = null;
			datetimeFormat = CheckItemDatetimeFormat.DATETIME_INPUT_OPTION;
			isRequired = false;
			isNoDirectInput = false;
			isNoDuplicateInput = false;
			rule = null;
			isCommentInput = false;
			isCommentDisplay = false;
			equipments = new List<Equipment>();
			detailSettings = new DetailSettings();
			customValues = new List<CustomValue>();
		}
	}

	public class FileData
	{
		public Dictionary<string, WorkOption> workOptions = new Dictionary<string, WorkOption>();
		public Dictionary<string, CheckItemOption> checkItemOptions = new Dictionary<string, CheckItemOption>();
	}
}
