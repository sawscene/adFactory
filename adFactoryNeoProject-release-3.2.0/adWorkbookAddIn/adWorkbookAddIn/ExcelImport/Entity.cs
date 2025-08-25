using System;
using System.Collections.Generic;
using System.Xml.Serialization;

namespace ExcelImport
{
	[XmlType("response")]
	public class ResponseEntity
	{
		public bool isSuccess;
		public string uri;
		public string errorType;
		public long? errorCode;

	}

	[XmlRoot("organizations")]
	public class Organizations
	{
		[XmlElement("organization")]
		public List<OrganizationEntity> organizations { get; set; }
	}

	[XmlType("organization")]
	public class OrganizationEntity
	{
		public long? organizationId;
		public string organizationName;
		public string organizationIdentify;
		public string authorityType;
		public string mailAddress;
		public long? updatePersonId;
		public DateTime updateDatetime;
		public bool? removeFlag;
		public long? parentId;
		public int? verInfo;
		public int? childCount;
	}

	[XmlType("workHierarchy")]
	public class WorkHierarchyEntity
	{
		public long? hierarchyId;
		public int? hierarchyType;
		public string hierarchyName;
		public long? parentId;
		public int? verInfo;
		public int? childCount;
		public long? workHierarchyId;

		public bool ShouldSerializehierarchyId() { return hierarchyId.HasValue; }
		public bool ShouldSerializehierarchyType() { return hierarchyType.HasValue; }
		public bool ShouldSerializehierarchyName() { return hierarchyName != null; }
		public bool ShouldSerializeparentId() { return parentId.HasValue; }
		public bool ShouldSerializeverInfo() { return verInfo.HasValue; }
		public bool ShouldSerializechildCount() { return childCount.HasValue; }
		public bool ShouldSerializeworkHierarchyId() { return workHierarchyId.HasValue; }
	}

	[XmlType("workflowHierarchy")]
	public class WorkflowHierarchyEntity
	{
		public long? hierarchyId;
		public int? hierarchyType;
		public string hierarchyName;
		public long? parentId;
		public int? verInfo;
		public int? childCount;
		public long? workflowHierarchyId;

		public bool ShouldSerializehierarchyId() { return hierarchyId.HasValue; }
		public bool ShouldSerializehierarchyType() { return hierarchyType.HasValue; }
		public bool ShouldSerializehierarchyName() { return hierarchyName != null; }
		public bool ShouldSerializeparentId() { return parentId.HasValue; }
		public bool ShouldSerializeverInfo() { return verInfo.HasValue; }
		public bool ShouldSerializechildCount() { return childCount.HasValue; }
		public bool ShouldSerializeworkflowHierarchyId() { return workflowHierarchyId.HasValue; }
	}

	[XmlType("work")]
	public class WorkEntity
	{
		public long parentId;
		public long? workId;
		public string workName;
		public int? workRev;
		public int taktTime;
		public string contentType;
		public long updatePersonId;
		public DateTime updateDatetime;
		public string fontColor;
		public string backColor;
		public string workNumber;
		public string workCheckInfo; // List<CheckInfoEntity> JSON
		public string workAddInfo; // List<WorkAddInfoEntity> JSON
		public string displayItems; // List<WorkDisplayItemEntity> JSON
		public List<WorkSectionEntity> workSections;
	}

	[XmlType("workSection")]
	public class WorkSectionEntity
	{
		public long? workSectionId;
		public long? fkWorkId;
		public string documentTitle;
		public int? pageNum;
		public string fileName;
		public DateTime fileUpdated;
		public int workSectionOrder;
		public string physicalName;

		public bool ShouldSerializeworkSectionId() { return workSectionId.HasValue; }
		public bool ShouldSerializefkWorkId() { return fkWorkId.HasValue; }
		public bool ShouldSerializepageNum() { return pageNum.HasValue; }
		public bool ShouldSerializefileUpdated() { return fileUpdated != null; }
	}

	public class CheckInfoEntity
	{
		public string key;   // プロパティ名
		public string type;  // 型
		public string val;   // 値
		public int disp;     // 表示順
		public string cat;   // プロパティ種別
		public string opt;   // 付加情報 (TraceSettingEntity XML)
		public double? min;  // 基準値下限
		public double? max;  // 基準値上限
		public string tag;   // タグ
		public string rules; // 入力規則
		public int page;     // 工程セクション表示順
		public int cp;       // 進捗チェックポイント
	}

	public class WorkAddInfoEntity
	{
		public string key;
		public string type;
		public string val;
		public int disp;
		public int? accessoryId;
	}

	public class WorkDisplayItemEntity
	{
		public int order;
		public string target;
		public string name;
	}

	[XmlType("traceSetting")]
	public class TraceSettingEntity
	{
		public List<TraceOptionEntity> traceOptions = new List<TraceOptionEntity>();
		public List<TraceCustomEntity> traceCustoms = new List<TraceCustomEntity>();
	}

	public enum WorkPropertyCategoryEnum
	{
		INFO,
		PARTS,      // 部品
		WORK,       // 作業
		INSPECTION, // 検査
		MEASURE,    // 測定
		TIMESTAMP,  // 日時
		CUSTOM,     // カスタム
		TIMER,      // タイマー
		LIST,       // リスト
		JUDG,       // 完了判定
		PRODUCT     // 完成品
	}

	public enum TraceOptionTypeEnum
	{
		PLUGIN,
		WORK,
		PROPERTY,
		COUNT,
		FIELDS,
		TEN_KEYBOARD,
		INTEGER_DIGITS,
		DECIMAL_DIGITS,
		ABSOLUTE_DISPLAY,
		VALUE_LIST,
		COLOR_VALUE_LIST,
		REFERENCE_NUMBER,
		FIELD_SIZE,
		DELIMITER,
		HOLD_PREV_DATA,
		CHECK_EMPTY,
		CHECK_UNIQUE,
		INPUT_LIST_ONLY,
		ATTACH_FILE,
		BULK_INPUT,
		CHECK_BARCODE,
		INPUT_PRODUCT_NUM,
		DATETIME_TYPE,
		KEYBOARD_TYPE,
		INPUT_TEXT,
		DISPLAY_TEXT,
		QR_READ
	}

	[XmlType("value")]
	public class InputValueColor
	{
		public string text;
		public string textColor;
		public string textBkColor;
	}

	[XmlType("traceOption")]
	public class TraceOptionEntity {
		public TraceOptionTypeEnum key;
		public string value;
		[XmlArrayItem("value")]
		public List<string> values;
		public List<InputValueColor> valueColors;
	}

	[XmlType("traceCustom")]
	public class TraceCustomEntity
	{
		public string name;
		[XmlElement(IsNullable = false)]
		public string rule;
		[XmlElement(IsNullable = false)]
		public string value;
	}

	[XmlType("workflow")]
	public class WorkflowEntity
	{
		public long parentId;
		public long? workflowId;
		public string workflowName;
		public string workflowRevision;
		public string workflowDiaglam;
		public long fkUpdatePersonId;
		public DateTime updateDatetime;
		public string ledgerPath;
		public string workflowNumber;
		public int? workflowRev;
		public List<ConWorkflowWorkEntity> conWorkflowWorks;
		public List<ConWorkflowWorkEntity> conWorkflowSeparateworks;
		public string modelName;
		public DateTime openTime;
		public DateTime closeTime;
		public string schedulePolicy;
	}

	[XmlType("conWorkflowWork")]
	public class ConWorkflowWorkEntity
	{
		public int workKbn;
		public long fkWorkflowId;
		public long fkWorkId;
		public bool skipFlag;
		public int workflowOrder;
		public DateTime standardStartTime;
		public DateTime standardEndTime;
		public string workName;
		public int workRev;
		[XmlArrayItem("equipment")]
		public List<long> equipments = new List<long>();
		[XmlArrayItem("organization")]
		public List<long> organizations = new List<long>();
	}

	[XmlType("importWorkflow")]
	public class ImportWorkflowEntity
	{
		[XmlElement("workflow", typeof(WorkflowEntity))]
		public WorkflowEntity workflow;
		[XmlElement("workflowHierarchy", typeof(WorkflowHierarchyEntity))]
		public WorkflowHierarchyEntity workflowHierarchy;
		[XmlElement("work", typeof(WorkEntity))]
		public List<WorkEntity> works = new List<WorkEntity>();
		[XmlElement("workHierarchy", typeof(WorkHierarchyEntity))]
		public List<WorkHierarchyEntity> workHierarchies = new List<WorkHierarchyEntity>();
		[XmlElement("workParameters", typeof(WorkParametersEntity))]
		public List<WorkParametersEntity> workParametersList = new List<WorkParametersEntity>();
	}


	[XmlType("workParameters")]
	public class WorkParametersEntity
	{
		public string itemNumber;
		public long workflowId;
		public string workParameter;
	}

	public class WorkParameterEntity
	{
		public List<WorkParameterWorkEntity> work = new List<WorkParameterWorkEntity>();
	}

	public class WorkParameterWorkEntity
	{
		public long workId;
		public int? taktTime;
		public List<WorkParameterWorkSectionEntity> workSection = new List<WorkParameterWorkSectionEntity>();
	}

	public class WorkParameterWorkSectionEntity
	{
		public int order;
		public string documentTitle;
		public string fileName;
		public string physicalFileName;
		public List<WorkParameterWorkCheckInfoEntity> workCheckInfo = new List<WorkParameterWorkCheckInfoEntity>();
	}

	public class WorkParameterWorkCheckInfoEntity
	{
		public int order;
		public bool hidden;
		public string key;
		public double? min;
		public double? max;
		public string val;
	}

	[XmlRoot("equipments")]
	public class Equipments
	{
		[XmlElement("equipment")]
		public List<EquipmentEntity> equipments { get; set; }
	}

	[XmlRoot("equipment")]
	public class EquipmentEntity
	{
		public long? equipmentId;
		public string equipmentName;
		public string equipmentIdentify;
		public long? parentId;
	}

	[XmlRoot("systemOption")]
	public class SystemOptionEntity
	{
		public string optionName;
		public bool enable;
	}

	public enum LoginType
	{
		PASSWORD,
		BARCODE,
		NFC_CARD,
		LDAP
	}

	[XmlRoot("organizationLoginRequest")]
	public class OrganizationLoginRequest
	{
		public LoginType loginType;
		public string loginId;
		public string authData;
	}

	[XmlRoot("organizationLoginResult")]
	public class OrganizationLoginResult
	{
		public bool isSuccess;
		public string errorType;

		public long organizationId;
		// public OrganizationInfoEntity organizationInfo;
		[XmlArrayItem("roleAuthority")]
		public List<string> roleAuthorities = new List<string>();

		public string message;
	}
}
