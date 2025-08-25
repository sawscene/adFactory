/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

import adtekfuji.admanagerapp.workreportplugin.enumerate.WorkReportWorkTypeEnum;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.directwork.ActualAddInfoEntity;
import jp.adtekfuji.adFactory.entity.directwork.WorkReportWorkNumEntity;
import jp.adtekfuji.adFactory.entity.view.WorkReportInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 作業日報の行情報
 *
 * @author nar-nakamura
 */
public class WorkReportRowInfo {

    private static final DateTimeFormatter localdateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final String SYS_WORK_REPORT_TIME_FORMAT_SEC = "ss";
    public static final String SYS_WORK_REPORT_TIME_FORMAT_MIN = "mm";
    public static final String SYS_WORK_REPORT_TIME_FORMAT_HOUR = "hh";
    public static final String SYS_WORK_REPORT_TIME_UNIT_SEC = "sec";
    public static final String SYS_WORK_REPORT_TIME_UNIT_MIN = "min";
    public static final String SYS_WORK_REPORT_TIME_UNIT_HOUR = "hour";

    private static final String PICK_LITE_WORK_NAME_REGEX = AdProperty.getProperties().getProperty("PickLiteWorkNameRegex");

    private final BooleanProperty isNew = new SimpleBooleanProperty();
    private final BooleanProperty isEdit = new SimpleBooleanProperty();

    private WorkReportInfoEntity workReport;
    private String workReportTimeFormat;
    private String workReportTimeUnit;

    private IntegerProperty actualNumProperty;  // 実績数プロパティ
    private IntegerProperty workNumProperty;    // 作業数プロパティ
    private StringProperty controlNoProperty;   // 製番プロパティ
    private StringProperty resourcesProperty;   // 装置番号プロパティ
    private StringProperty finalNumProperty;    // 完成数プロパティ
    private StringProperty defectNumProperty;   // 不良数プロパティ
    private StringProperty stopTimeProperty;    // 装置停止プロパティ
    private StringProperty remarks1Property;    // 備考1プロパティ
    private StringProperty remarks2Property;    // 備考2プロパティ
    private StringProperty unitTimeProperty = new SimpleStringProperty("");
    private StringProperty serialNumbersProperty = new SimpleStringProperty("");
    
    private StringProperty orderNumberProperty; // 注文番号プロパティ

    private WorkReportWorkNumEntity workReportWorkNum;// 作業数情報
    private List<String> initialControlNos;     // 製番一覧(初期値)
    private List<String> selectedControlNos;    // 製番一覧(選択値)
    private List<String> serialNumbers;

    /**
     * コンストラクタ
     */
    public WorkReportRowInfo() {
        this.workReport = new WorkReportInfoEntity();
    }

    /**
     * コンストラクタ
     *
     * @param workReport 作業日報情報
     * @param workReportTimeFormat 工数の表示形式
     * @param workReportTimeUnit 工数の単位
     */
    public WorkReportRowInfo(WorkReportInfoEntity workReport, String workReportTimeFormat, String workReportTimeUnit) {
        this.workReport = workReport;

        this.workReportTimeFormat = workReportTimeFormat;
        this.workReportTimeUnit = workReportTimeUnit;

        // 工程名を抽出
        try {
            Matcher m = Pattern.compile(PICK_LITE_WORK_NAME_REGEX).matcher(this.workReport.getWorkName());
            if (m.find()) {
                this.workReport.setWorkName(m.group(1));
            }
        } catch (Exception ex) {
        }

        if (!StringUtils.isEmpty(this.workReport.getWorkReprotAddInfo())) {
            ActualAddInfoEntity addInfo = JsonUtils.jsonToObject(this.workReport.getWorkReprotAddInfo(), ActualAddInfoEntity.class);
            if (Objects.nonNull(addInfo)) {
                this.workReportWorkNum = addInfo.getWorkReportWorkNum();
            }
        }

        // シリアル番号と作業数を取得
        if (this.workReport.getWorkType() == WorkReportWorkTypeEnum.DIRECT_WORK.getValue()
                && !StringUtils.isEmpty(this.workReport.getSerialNumbers())) {
            String[] values = this.workReport.getSerialNumbers().split("\\|");
            Set<String> set = new HashSet<>(Arrays.asList(values));
            if (!set.isEmpty()) {
                this.serialNumbers = new ArrayList<>(set);
                this.serialNumbers.sort(Comparator.comparing(o -> o));

                this.serialNumbersProperty.set(String.join("|", this.serialNumbers));

                this.workReport.setActualNum(this.serialNumbers.size());

                // 工数/台
                BigDecimal value = null;
                switch (this.workReportTimeUnit) {
                    default:
                    case SYS_WORK_REPORT_TIME_UNIT_SEC:
                        value = new BigDecimal((this.workReport.getWorkTime() / 1000.0) / this.workReport.getActualNum());
                        this.unitTimeProperty.set(value.setScale(0, BigDecimal.ROUND_DOWN).toString());
                        break;
                    case SYS_WORK_REPORT_TIME_UNIT_MIN:
                        value = new BigDecimal((this.workReport.getWorkTime() / 60000.0) / this.workReport.getActualNum());
                        this.unitTimeProperty.set(value.setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                        break;
                    case SYS_WORK_REPORT_TIME_UNIT_HOUR:
                        value = new BigDecimal((this.workReport.getWorkTime() / 3600000.0) / this.workReport.getActualNum());
                        this.unitTimeProperty.set(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        break;
                }
            }
        }
    }

    /**
     * 作業日報情報を取得する。
     *
     * @return 作業日報情報
     */
    public WorkReportInfoEntity getWorkReport() {
        return this.workReport;
    }

    /**
     * 作業日報情報を設定する。
     *
     * @param workReport 作業日報情報
     */
    public void setWorkReport(WorkReportInfoEntity workReport) {
        this.workReport = workReport;
    }

    /**
     * 編集可能フラグプロパティを取得する。
     *
     * @return 編集可能フラグ (true:編集可, false:編集不可)
     */
    public BooleanProperty editableProperty() {
        boolean editable = false;
        if (this.workReport.getWorkTime() == 1) {
            editable = true;
        }
        return new SimpleBooleanProperty(editable);
    }

    /**
     * 新規追加フラグプロパティを取得する。
     *
     * @return 新規追加フラグ (true:新規追加, false:新規追加ではない)
     */
    public BooleanProperty isNewProperty() {
        return this.isNew;
    }

    /**
     * 編集フラグプロパティを取得する。
     *
     * @return 編集フラグ (true:編集した, false:編集していない)
     */
    public BooleanProperty isEditProperty() {
        return this.isEdit;
    }

    /**
     * 工数プロパティを取得する。
     *
     * @return 工数
     */
    public StringProperty workTimeProperty() {
        return new SimpleStringProperty(this.getWorkTime());
    }

    /**
     * 工数の分のみプロパティを取得する。
     *
     * @return 工数の分のみ
     */
    public StringProperty workTimeMinProperty() {
        return new SimpleStringProperty(this.getWorkTimeMin());
    }

    /**
     * 工数の秒のみプロパティを取得する。
     *
     * @return 工数の秒のみ
     */
    public StringProperty workTimeSecProperty() {
        return new SimpleStringProperty(this.getWorkTimeSec());
    }

    /**
     * 工数プロパティを取得する。
     *
     * @return 工数
     */
    public StringProperty unitTimeProperty() {
        return this.unitTimeProperty;
    }

    /**
     * 作業数プロパティを取得する。
     *
     * @return 作業数
     */
    public IntegerProperty actualNumProperty() {
        if (Objects.isNull(this.actualNumProperty)) {
            this.actualNumProperty = new SimpleIntegerProperty(this.getActualNum());
        }
        return this.actualNumProperty;
    }

    /**
     * 作業数プロパティを取得する。
     *
     * @return 作業数
     */
    public IntegerProperty workNumProperty() {
        if (Objects.isNull(this.workNumProperty)) {
            this.workNumProperty = new SimpleIntegerProperty(this.getWorkNum());
        }
        return this.workNumProperty;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public StringProperty serialNumbersProperty() {
        return serialNumbersProperty;
    }

    /**
     * 製番プロパティを取得する。
     *
     * @return 製番
     */
    public StringProperty controlNoProperty() {
        if (Objects.isNull(this.controlNoProperty)) {
            this.controlNoProperty = new SimpleStringProperty(this.getControlNo());
        }
        return this.controlNoProperty;
    }

    /**
     * 工数の時間のみを取得する。
     *
     * @return 工数の時間のみ
     */
    public String getWorkTimeHour() {
        List<String> formatList = Arrays.asList(this.workReportTimeFormat.split(":"));
        if (!formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_HOUR)) {
            return "";
        }

        int workTime = this.workReport.getWorkTime() == null ? 0 : this.workReport.getWorkTime();
        int workTimeHour = workTime / 3600000;
        int workTimeMin = workTime / 60000;
        int workTimeSec = (workTime % 60000) / 1000;
        if (!formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_MIN) && (workTimeMin > 0 || workTimeSec > 0) && workTimeHour != 24) {
            workTimeHour += 1;
        }
        return String.valueOf(workTimeHour);
    }

    /**
     * 工数の分のみを取得する。
     *
     * @return 工数の分のみ
     */
    public String getWorkTimeMin() {
        List<String> formatList = Arrays.asList(this.workReportTimeFormat.split(":"));
        if (!formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_MIN)) {
            return "";
        }
        int workTime = this.workReport.getWorkTime() == null ? 0 : this.workReport.getWorkTime();
        int workTimeMin = workTime / 60000;
        int workTimeSec = (workTime % 60000) / 1000;
        if (!formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_SEC) && workTimeSec > 0) {
            workTimeMin += 1;
        }

        if (formatList.size() > 1 && formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_HOUR)) {
            workTimeMin = workTimeMin % 60;
            return String.format(":%02d", workTimeMin);
        }

        return String.valueOf(workTimeMin);
    }

    /**
     * 工数の分のみを設定する。
     *
     * @param value 工数の分のみ
     */
    public void setWorkTimeMin(String value) {
        int workTimeMin = StringUtils.parseInteger(value);
        int workTime = workTimeMin * 60000;
        this.workReport.setWorkTime(workTime);
    }

    /**
     * 工数の秒のみを取得する。
     *
     * @return 工数の秒のみ
     */
    public String getWorkTimeSec() {
        List<String> formatList = Arrays.asList(this.workReportTimeFormat.split(":"));
        if (!formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_SEC)) {
            return "";
        }

        int workTimeSec = 0;
        int workTime = this.workReport.getWorkTime() == null ? 0 : this.workReport.getWorkTime();
        workTimeSec = workTime / 1000;

        if (formatList.size() == 1) {
            return String.valueOf(workTimeSec);
        }
        return String.format(":%02d", workTimeSec % 60);
    }

    /**
     * 工数文字列(h:mm:ss)を取得する。
     *
     * @return 工数文字列(h:mm:ss)
     */
    public String getWorkTime() {
        return getWorkTimeHour() + getWorkTimeMin() + getWorkTimeSec();
    }

    /**
     * 工数文字列(m:ss)で工数を設定する。
     *
     * @param value 工数文字列(m:ss)
     */
    public void setWorkTime(String value) {
        List<String> formatList = Arrays.asList(this.workReportTimeFormat.split(":"));
        List<String> separated = Arrays.asList(value.split(":"));
        Integer hour = Integer.valueOf("00");
        Integer min = Integer.valueOf("00");
        Integer sec = Integer.valueOf("00");
        switch (separated.size()) {
            case 3: {
                hour = Integer.valueOf(separated.get(0));
                min = Integer.valueOf(separated.get(1));
                sec = Integer.valueOf(separated.get(2));
                break;
            }
            case 2: {
                hour = Integer.valueOf(formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_HOUR) ? separated.get(0) : "00");
                if (formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_HOUR)) {
                    min = Integer.valueOf(formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_MIN) ? separated.get(1) : "00");
                } else {
                    min = Integer.valueOf(formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_MIN) ? separated.get(0) : "00");
                }
                sec = Integer.valueOf(formatList.contains(SYS_WORK_REPORT_TIME_FORMAT_SEC) ? separated.get(1) : "00");
                break;
            }
            case 1: {
                // 時分秒のうち設定ファイルで指定されたもののみ更新
                switch (formatList.get(0)) {
                    case SYS_WORK_REPORT_TIME_FORMAT_HOUR:
                        hour = Integer.valueOf(separated.get(0));
                        break;
                    case SYS_WORK_REPORT_TIME_FORMAT_MIN:
                        min = Integer.valueOf(separated.get(0));
                        break;
                    case SYS_WORK_REPORT_TIME_FORMAT_SEC:
                        sec = Integer.valueOf(separated.get(0));
                        break;
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
        this.workReport.setWorkTime(hour * 3600000 + min * 60000 + sec * 1000);
    }

    /**
     * 作業日を取得する。
     *
     * @return 作業日
     */
    public LocalDate getWorkDate() {
        LocalDate date = null;
        try {
            if (Objects.isNull(this.workReport.getWorkDate()) || this.workReport.getWorkDate().isEmpty()) {
                date = null;
            } else {
                date = LocalDate.parse(this.workReport.getWorkDate(), localdateFormatter);
            }
        } catch (Exception ex) {
        }
        return date;
    }

    /**
     * 作業日を設定する。
     *
     * @param value 作業日
     */
    public void setWorkDate(LocalDate value) {
        if (Objects.isNull(value)) {
            this.workReport.setWorkDate(null);
        } else {
            this.workReport.setWorkDate(localdateFormatter.format(value));
        }
    }

    /**
     * 新規追加フラグを取得する。
     *
     * @return 新規追加フラグ (true:新規追加, false:新規追加ではない)
     */
    public Boolean getIsNew() {
        return isNew.get();
    }

    /**
     * 新規追加フラグを設定する。
     *
     * @param isNew 新規追加フラグ (true:新規追加, false:新規追加ではない)
     */
    public void setIsNew(Boolean isNew) {
        this.isNew.set(isNew);
    }

    /**
     * 編集フラグを取得する。
     *
     * @return 編集フラグ (true:編集した, false:編集していない)
     */
    public Boolean getIsEdit() {
        return isEdit.get();
    }

    /**
     * 編集フラグを設定する。
     *
     * @param isEdit 編集フラグ (true:編集した, false:編集していない)
     */
    public void setIsEdit(Boolean isEdit) {
        this.isEdit.set(isEdit);
    }

    /**
     * 作業種別を取得する。
     *
     * @return 作業種別
     */
    public Integer getWorkType() {
        if (Objects.nonNull(this.workReport) && Objects.nonNull(this.workReport.getWorkType())) {
            return this.workReport.getWorkType();
        } else {
            return -1;
        }
    }

    /**
     * 実績数を取得する。
     *
     * @return 実績数
     */
    public Integer getActualNum() {
        return Objects.nonNull(this.workReport) ? this.workReport.getActualNum() : 0;
    }

    /**
     * 作業数を取得する。
     *
     * @return 作業数
     */
    public Integer getWorkNum() {
        if (this.getSelectedControlNos().isEmpty()) {
            if (Objects.nonNull(this.workReportWorkNum) && Objects.nonNull(this.workReportWorkNum.getOrderNum())) {
                return this.workReportWorkNum.getOrderNum().intValue();
            }
            return 1;
        }
        return this.getSelectedControlNos().size();
    }

    /**
     * 作業数を設定する。
     *
     * @param workNum 作業数
     */
    public void setWorkNum(Integer workNum) {
        if (Objects.nonNull(this.workNumProperty)) {
            this.workNumProperty.set(workNum);
        }
    }

    /**
     * 製番を取得する。
     *
     * @return 製番
     */
    public String getControlNo() {
        List<String> controlNoGroups = getControlNoGroups(this.getSelectedControlNos());
        if (!controlNoGroups.isEmpty()) {
            return String.join(",", controlNoGroups);
        } else {
            return null;
        }
    }

    /**
     * 製番を設定する。
     *
     * @param controlNo 製番
     */
    public void setControlNo(String controlNo) {
        if (Objects.nonNull(this.controlNoProperty)) {
            this.controlNoProperty.set(controlNo);
        }
    }

    /**
     * 製番一覧(初期値)を取得する。
     *
     * @return 製番一覧(初期値)
     */
    public List<String> getInitialControlNos() {
        if (Objects.isNull(this.initialControlNos)) {
            this.initialControlNos = Objects.nonNull(this.workReportWorkNum) ? this.getControlNos(this.workReportWorkNum.getControlNo()) : new ArrayList<>();
        }
        return this.initialControlNos;
    }

    /**
     * 選択済みの製番一覧を取得する。
     *
     * @return 選択済みの製番一覧
     */
    public List<String> getSelectedControlNos() {
        if (Objects.isNull(this.selectedControlNos)) {
            this.selectedControlNos = Objects.nonNull(this.workReportWorkNum) ? this.getControlNos(this.workReportWorkNum.getSelectedControlNo()) : new ArrayList<>();
        }
        return this.selectedControlNos;
    }

    /**
     * 選択済みの製番一覧を設定する。
     *
     * @param selectedControlNos 選択済みの製番一覧
     */
    public void setSelectedControlNos(List<String> selectedControlNos) {
        this.selectedControlNos = selectedControlNos;
    }

    /**
     * 製番一覧を取得する。
     *
     * <pre>
     * 製番文字列グループのフォーマット例
     * 例1) ["1001"]
     * 例2) ["1001-1012"]
     * 例3) ["1001","1012"]
     * 例4) ["1001-1003", "1005-1012"]
     * 例5) ["1001-1003", "1005", "1007-1008", "1010-1012"]
     * </pre>
     *
     * @param controlNoGroups 製番文字列グループ
     * @return 製番一覧
     */
    private List<String> getControlNos(List<String> controlNoGroups) {
        List<String> controlNos = new ArrayList<>();

        if (Objects.isNull(controlNoGroups) || controlNoGroups.isEmpty()) {
            return controlNos;
        }

        for (String controlNoGroup : controlNoGroups) {
            // 先頭と末尾の製番を取得
            String rangeStart;
            String rangeEnd;
            String[] controlNoArray = controlNoGroup.split("-");
            switch (controlNoArray.length) {
                case 1:
                    rangeStart = controlNoArray[0];
                    rangeEnd = rangeStart;
                    break;
                case 2:
                    rangeStart = controlNoArray[0];
                    rangeEnd = controlNoArray[1];
                    break;
                default:
                    continue;
            }

            // 製番リストを生成
            int startNo = Integer.valueOf(rangeStart);
            int endNo = Integer.valueOf(rangeEnd);
            for (int controlNo = startNo; controlNo <= endNo; controlNo++) {
                controlNos.add(String.format(getControlNoFormat(), controlNo));
            }
        }

        return controlNos;
    }

    /**
     * 製番文字列グループを取得する。
     *
     * @param controlNos 製番一覧
     * @return 製番文字列グループ
     */
    private List<String> getControlNoGroups(List<String> controlNos) {
        if (Objects.isNull(controlNos) || controlNos.isEmpty()) {
            return new ArrayList<>();
        } else if (controlNos.size() == 1) {
            return new ArrayList<>(Arrays.asList(controlNos.get(0)));
        }

        // 製番一覧から製番表記を生成
        List<String> ranges = new ArrayList<>();
        Integer rangeStart;
        Integer rangeEnd;
        List<Integer> controlNoNums = controlNos.stream().map(p -> Integer.valueOf(p)).collect(Collectors.toList());

        for (Integer index = 0; index < controlNoNums.size(); index++) {
            rangeStart = controlNoNums.get(index);
            rangeEnd = rangeStart;

            // 連番の末尾の製番を取得
            while (index + 1 < controlNoNums.size() && controlNoNums.get(index + 1) - controlNoNums.get(index) == 1) {
                rangeEnd = controlNoNums.get(index + 1);
                index++;
            }

            String formatBase = getControlNoFormat();
            if (rangeStart.equals(rangeEnd)) {
                ranges.add(String.format(formatBase, rangeStart));
            } else {
                String format = String.format("%s-%s", formatBase, formatBase);
                ranges.add(String.format(format, rangeStart, rangeEnd));
            }
        }

        return ranges;
    }

    /**
     * 製番のフォーマットを取得する。
     *
     * @return 製番のフォーマット
     */
    private String getControlNoFormat() {
        Integer digit = 4;
        if (Objects.nonNull(this.workReportWorkNum) && Objects.nonNull(this.workReportWorkNum.getDigit())) {
            if (this.workReportWorkNum.getDigit() > digit) {
                digit = this.workReportWorkNum.getDigit();
            }
        }
        return "%0" + String.valueOf(digit) + "d";
    }

    /**
     * 作業数と製造番号を適用する。
     *
     * @param isWorkNum
     * @return 作業日報情報
     */
    public WorkReportInfoEntity apply(boolean isWorkNum) {
        if (isWorkNum) {
            if (Objects.isNull(this.workReportWorkNum)) {
                this.workReportWorkNum = new WorkReportWorkNumEntity();
            }
            if (Objects.nonNull(this.controlNoProperty().get())) {
                this.workReportWorkNum.setSelectedControlNo(getControlNoGroups(this.selectedControlNos));
            }
            this.workReportWorkNum.setResources(this.resourcesProperty.getValue());
            this.workReportWorkNum.setFinalNum(Double.valueOf(this.finalNumProperty.get()));
            this.workReportWorkNum.setDefectNum(Double.valueOf(this.defectNumProperty.get()));
            this.workReportWorkNum.setStopTime(convertTimetoNum(this.stopTimeProperty.get()));
            ActualAddInfoEntity addInfo = new ActualAddInfoEntity();
            addInfo.setWorkReportWorkNum(this.workReportWorkNum);
            this.workReport.setWorkReprotAddInfo(JsonUtils.objectToJson(addInfo));
        }
        return this.workReport;
    }

    /**
     * 備考1プロパティを取得する。
     *
     * @return
     */
    public StringProperty remarks1Property() {
        if (Objects.isNull(this.remarks1Property)) {
            this.remarks1Property = new SimpleStringProperty("");
            if (Objects.nonNull(this.workReportWorkNum)) {
                this.remarks1Property.set(this.workReportWorkNum.getRemarks1());
            }
        }
        return this.remarks1Property;
    }

    /**
     * 備考2プロパティを取得する。
     *
     * @return
     */
    public StringProperty remarks2Property() {
        if (Objects.isNull(this.remarks2Property)) {
            this.remarks2Property = new SimpleStringProperty("");
            if (Objects.nonNull(this.workReportWorkNum)) {
                this.remarks2Property.set(this.workReportWorkNum.getRemarks2());
            }
        }
        return this.remarks2Property;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    /**
     * 工数/台を取得する。
     *
     * @return 工数/台
     */
    public String getUnitTime() {
        return this.unitTimeProperty.get();
    }

    /**
     * 工数/台を設定する。
     *
     * @param unitTime 工数/台
     */
    public void setUnitTime(String unitTime) {
        this.unitTimeProperty.set(unitTime);
    }

    /**
     * 装置番号プロパティを取得する。
     *
     * @return
     */
    public StringProperty resourcesProperty() {
        return new SimpleStringProperty(this.getResourcesProp());
    }

    /**
     * 装置番号を取得する。
     *
     * @return
     */
    public String getResourcesProp() {
        if (Objects.isNull(this.resourcesProperty)) {
            if (Objects.nonNull(this.workReportWorkNum) && Objects.nonNull(this.workReportWorkNum.getResources())) {
                this.resourcesProperty = new SimpleStringProperty(this.workReportWorkNum.getResources());
            } else {
                this.resourcesProperty = new SimpleStringProperty("");
            }
        }
        return this.resourcesProperty.get();
    }

    /**
     * 装置番号を設定する。
     *
     * @param resources 装置番号
     */
    public void setResourcesProp(String resources) {
        this.resourcesProperty.set(resources);
    }

    /**
     * 完成数プロパティを取得する。
     *
     * @return
     */
    public StringProperty finalNumProperty() {
        if (Objects.isNull(this.finalNumProperty)) {
            if (Objects.nonNull(this.workReportWorkNum)) {
                if (Objects.nonNull(this.workReportWorkNum.getFinalNum())) {
                    Integer finalInt = this.workReportWorkNum.getFinalNum().intValue();
                    this.finalNumProperty = new SimpleStringProperty(finalInt.toString());
                } else {
                    this.finalNumProperty = new SimpleStringProperty(this.getWorkNum().toString());
                }
            } else {
                this.finalNumProperty = new SimpleStringProperty("0");
            }
        }
        return this.finalNumProperty;
    }

    /**
     * 完成数を設定する。
     *
     * @param finalNum 完成数
     */
    public void setFinalNum(String finalNum) {
        this.finalNumProperty.set(finalNum);
    }

    /**
     * 不良数プロパティを取得する。
     *
     * @return
     */
    public StringProperty defectNumProperty() {
        if (Objects.isNull(this.defectNumProperty)) {
            if (Objects.nonNull(this.workReportWorkNum) && Objects.nonNull(this.workReportWorkNum.getDefectNum())) {
                Integer defectInt = this.workReportWorkNum.getDefectNum().intValue();
                this.defectNumProperty = new SimpleStringProperty(defectInt.toString());
            } else {
                this.defectNumProperty = new SimpleStringProperty("0");
            }
        }
        return this.defectNumProperty;
    }

    /**
     * 不良数を設定する。
     *
     * @param defectNum 不良数
     */
    public void setDefectNum(String defectNum) {
        this.defectNumProperty.set(defectNum);
    }

    /**
     * 装置停止プロパティを取得する。
     *
     * @return
     */
    public StringProperty stopTimeProperty() {
        if (Objects.isNull(this.stopTimeProperty)) {
            if (Objects.nonNull(this.workReportWorkNum) && Objects.nonNull(this.workReportWorkNum.getStopTime())) {
                int time = this.workReportWorkNum.getStopTime();
                if (time != 0) {
                    this.stopTimeProperty = new SimpleStringProperty(getStopTimeHour(time) + getStopTimeMin(time));
                }
            } else {
                this.stopTimeProperty = new SimpleStringProperty("0:00");
            }
        }
        return this.stopTimeProperty;
    }

    /**
     * 装置停止の分のみを取得する。
     *
     * @param time
     * @return
     */
    public String getStopTimeHour(int time) {
        if (time >= 3600000) {
            int stopTimeHour = time / 3600000;
            if (stopTimeHour <= 24) {
                stopTimeHour = (int) Math.floor(stopTimeHour);
                return String.valueOf(stopTimeHour);
            }
            return String.valueOf(24);
        } else {
            return String.valueOf("0");
        }
    }

    /**
     * 装置停止の分のみを取得する。
     *
     * @param time
     * @return
     */
    public String getStopTimeMin(int time) {
        if (time > 60000) {
            int stopTimeMin = time / 60000;
            stopTimeMin = stopTimeMin % 60;
            return String.format(":%02d", stopTimeMin);
        } else {
            return String.valueOf("00");
        }
    }

    /**
     * 装置停止を設定する。
     *
     * @param stopTime 装置停止
     */
    public void setStopTime(String stopTime) {
        this.stopTimeProperty.set(stopTime);
    }

    /**
     *
     * @param stopTimes
     * @return
     */
    public Integer convertTimetoNum(String stopTimes) {

        Long newStopTime = 0L;
        String[] split = stopTimes.split(":");
        // hh:mm
        newStopTime = newStopTime + TimeUnit.HOURS.toMillis(Long.parseLong(split[0]));
        if (split.length == 2) {
            newStopTime = newStopTime + TimeUnit.MINUTES.toMillis(Long.parseLong(split[1]));
        }
        Integer newStopTimeInt = (newStopTime.intValue());
        return newStopTimeInt;
    }
    
    public WorkReportWorkNumEntity getWorkReportWorkNum() {
        return workReportWorkNum;
    }
    
    /**
     * 注文番号プロパティを取得する。
     * 
     * @return 
     */
    public StringProperty orderNumberProperty() {
        return new SimpleStringProperty(this.getOrderNumProp());
    }

    /**
     * 注文番号を取得する。
     * 
     * @return
     */
    public String getOrderNumProp() {
        
        if (this.workReport.getWorkType() == WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()){
            if(Objects.nonNull(workReport.getProductionNumber())){
                return workReport.getProductionNumber();
            }
        }
        if (this.workReport.getWorkType() == WorkReportWorkTypeEnum.DIRECT_WORK.getValue()){
            return workReport.getOrderNumber();
        }
        return null;
    }
}