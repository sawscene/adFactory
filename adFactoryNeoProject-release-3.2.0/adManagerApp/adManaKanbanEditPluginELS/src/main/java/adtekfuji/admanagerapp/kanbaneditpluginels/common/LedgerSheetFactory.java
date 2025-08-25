/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import adtekfuji.locale.LocaleUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.excelreplacer.ExcelReplacer;

/**
 *
 * @author e-mori
 */
public class LedgerSheetFactory {

    public static enum LedgerResultType {

        SUCCESS(0),
        FAILD_NOTPASS(1),
        FAILD_OTHER(9);

        private final Integer type;

        private LedgerResultType(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final KanbanLedgerPermanenceData ledgerPermanenceData;
    private String defaultFileName = "Ledger" + new Date();
    private File outFile = null;
    private Map<String, Object> replaceData = null;

    public LedgerSheetFactory(KanbanLedgerPermanenceData ledgerPermanenceData, String defaultFileName) {
        this.ledgerPermanenceData = ledgerPermanenceData;
        this.defaultFileName = defaultFileName;
    }

    private File showOutputDialog(ActionEvent event) {
        Node node = (Node) event.getSource();

        File desktopDir = new File(System.getProperty("user.home"), "Desktop");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(desktopDir);
        fileChooser.setInitialFileName(defaultFileName);
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("Excel 97-2003 files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);

        return fileChooser.showSaveDialog(node.getScene().getWindow());
    }

    public LedgerResultType writeLedgerSheet(ActionEvent event) throws IOException {
        File inFile = new File(ledgerPermanenceData.getLedgerFilePass());
        if (!inFile.exists()) {
            return LedgerResultType.FAILD_NOTPASS;
        }
        outFile = showOutputDialog(event);
        if (Objects.isNull(outFile)) {
            return LedgerResultType.FAILD_OTHER;
        }

        replaceData = createReplaceData();
        ExcelReplacer.replace(inFile, outFile, replaceData, ExcelReplacer::toUpperString);

        return LedgerResultType.SUCCESS;
    }

    public List<String> isReplaceCheck() throws IOException {
        List<String> faildReplaceTags = ExcelReplacer.isRepraceCheck(outFile, createCheckWords());
        if (!faildReplaceTags.isEmpty()) {
            return faildReplaceTags;
        }
        return new ArrayList<>();
    }

    private Map<String, Object> createReplaceData() {
        Map<String, Object> map = new HashMap<>();

        //カンバン
        map.putAll(createReplaceDataKanban(ledgerPermanenceData.getKanbanInfoEntity(), ledgerPermanenceData.getActualResultInfoEntitys()));
        //工程順カンバン
        map.putAll(createReplaceDataWorkKanban(ledgerPermanenceData.getWorkKanbanInfoEntitys(), ledgerPermanenceData.getActualResultInfoEntitys()));
        //追加工程
        map.putAll(createReplaceDataSeparateWorkKanban(ledgerPermanenceData.getSeparateworkWorkKanbanInfoEntitys(), ledgerPermanenceData.getActualResultInfoEntitys()));

        //実績
        return map;
    }

    /**
     * カンバン用帳票リプレースデータ作成
     *
     * @param kanbanEntity
     * @return
     */
    private Map<String, Object> createReplaceDataKanban(KanbanInfoEntity kanbanEntity, List<ActualResultEntity> actualResultInfoEntitys) {
        Map<String, Object> map = new HashMap<>();

        map.put("TAG_KANBAN_NAME", kanbanEntity.getKanbanName());
        map.put("TAG_KANBAN_STATUS", LocaleUtils.getString(kanbanEntity.getKanbanStatus().getResourceKey()));
        map.put("TAG_KANBAN_PLAN_START", kanbanEntity.getStartDatetime());
        map.put("TAG_KANBAN_PLAN_END", kanbanEntity.getCompDatetime());
        Optional<ActualResultEntity> start = actualResultInfoEntitys.stream().min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put("TAG_KANBAN_ACTUAL_START", start.get().getImplementDatetime());
        }
        Optional<ActualResultEntity> compDate = actualResultInfoEntitys.stream().max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put("TAG_KANBAN_ACTUAL_END", compDate.get().getImplementDatetime());
        }
        kanbanEntity.getPropertyCollection().stream().forEach((_item) -> {
            map.put(String.format("TAG_KANBAN_PROPERTY(%s)", _item.getKanbanPropertyName()), _item.getKanbanPropertyValue());
        });
        return map;
    }

    /**
     * 工程カンバン用帳票リプレースデータ作成
     *
     * @param workKanbanEntitys
     * @return
     */
    private Map<String, Object> createReplaceDataWorkKanban(List<WorkKanbanInfoEntity> workKanbanEntitys, List<ActualResultEntity> actualResultInfoEntitys) {
        Map<String, Object> map = new HashMap<>();
        String workflowName = null;
        String workRevison = null;
        workKanbanEntitys.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));
        for (int index = 1; workKanbanEntitys.size() >= index; index++) {
            WorkKanbanInfoEntity work = workKanbanEntitys.get(index - 1);
            List<ActualResultEntity> workingList = new ArrayList<>();
            List<ActualResultEntity> compList = new ArrayList<>();
            List<String> interruptList = new ArrayList<>();
            List<String> delayList = new ArrayList<>();
            List<String> parentOrganizations = new ArrayList<>();
            List<String> parentEquipments = new ArrayList<>();
            List<String> organizations = new ArrayList<>();
            List<String> equipments = new ArrayList<>();

            for (ActualResultEntity actual : actualResultInfoEntitys) {
                if (actual.getFkWorkKanbanId().equals(work.getWorkKanbanId())) {
                    if (Objects.isNull(workflowName) && Objects.isNull(workRevison)) {
                        workflowName = actual.getWorkflowName();
                        workRevison = actual.getWorkflowRevision();
                    }
                    setParentOrganizations(parentOrganizations, actual);
                    setOrganizations(organizations, actual);
                    setParentEquipments(parentEquipments, actual);
                    setEquipments(equipments, actual);
                    switch (actual.getActualStatus()) {
                        case WORKING:
                            workingList.add(actual);
                            break;
                        case COMPLETION:
                            compList.add(actual);
                            setDelay(delayList, actual);
                            break;
                        case SUSPEND:
                            setIntterupt(interruptList, actual);
                            break;
                        default:
                            break;
                    }
                }
            }

            map.put(String.format("TAG_WORK_NAME%s", index), work.getWorkName());
            map.put(String.format("TAG_WORK_STATUS%s", index), LocaleUtils.getString(work.getWorkStatus().getResourceKey()));
//            時間の単位で再計算する処理を追加すること
            map.put(String.format("TAG_WORK_TAKTTIME%s", index), work.getTaktTime());
            map.put(String.format("TAG_WORK_WORKTIME%s", index), work.getSumTimes());
            map.put(String.format("TAG_WORK_PLAN_START%s", index), work.getStartDatetime());
            map.put(String.format("TAG_WORK_PLAN_END%s", index), work.getCompDatetime());
            StringBuilder interrupts = new StringBuilder();
            for (String interrupt : interruptList) {
                interrupts.append(interrupt);
                interrupts.append(",");
            }
            deleteLastTrailingCharacter(interrupts);
            map.put(String.format("TAG_WORK_INTERRUPT%s", index), interrupts.toString());
            StringBuilder delays = new StringBuilder();
            for (String delay : delayList) {
                delays.append(delay);
                delays.append(",");
            }
            deleteLastTrailingCharacter(delays);
            map.put(String.format("TAG_WORK_DELAY%s", index), delays.toString());
            for (WorkKanbanPropertyInfoEntity pro : work.getPropertyCollection()) {
                map.put(String.format("TAG_WORK_PROPERTY%s(%s)", index, pro.getWorkKanbanPropName()), pro.getWorkKanbanPropValue());
            }
            createReplaceDataWorkKanbanActual(map, index, workingList, compList, parentOrganizations, organizations, parentEquipments, equipments);
            
            this.createReplaceDataTraceability(map, index, compList);
        }

        map.put("TAG_WORKFLOW_NAME", workflowName);
        map.put("TAG_WORKFLOW_REVISION", workRevison);
        return map;
    }

    /**
     * 実績用帳票リプレースデータ作成
     *
     * @param map
     * @param index
     * @param workingList
     * @param compList
     * @param pO
     * @param o
     * @param pE
     * @param e
     */
    private void createReplaceDataWorkKanbanActual(Map<String, Object> map, int index, List<ActualResultEntity> workingList, List<ActualResultEntity> compList, List<String> pO, List<String> o, List<String> pE, List<String> e) {
        Optional<ActualResultEntity> start = workingList.stream().min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_START%s", index), start.get().getImplementDatetime());
        }
        Optional<ActualResultEntity> compDate = compList.stream().max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_END%s", index), compDate.get().getImplementDatetime());
        }
        StringBuilder parentOrganizations = new StringBuilder();
        for (String parentOrganization : pO) {
            parentOrganizations.append(parentOrganization);
            parentOrganizations.append(",");
        }
        deleteLastTrailingCharacter(parentOrganizations);
        map.put(String.format("TAG_WORK_PARENT_ORGANIZATION%s", index), parentOrganizations.toString());
        StringBuilder organizations = new StringBuilder();
        for (String organization : o) {
            organizations.append(organization);
            organizations.append(",");
        }
        deleteLastTrailingCharacter(organizations);
        map.put(String.format("TAG_WORK_ORGANIZATION%s", index), organizations.toString());
        StringBuilder parentEquipments = new StringBuilder();
        for (String parentEquipmet : pE) {
            parentEquipments.append(parentEquipmet);
            parentEquipments.append(",");
        }
        deleteLastTrailingCharacter(parentEquipments);
        map.put(String.format("TAG_WORK_PARENT_EQUIPMENT%s", index), parentEquipments.toString());
        StringBuilder equipmets = new StringBuilder();
        for (String equipmet : e) {
            equipmets.append(equipmet);
            equipmets.append(",");
        }
        deleteLastTrailingCharacter(equipmets);
        map.put(String.format("TAG_WORK_EQUIPMENT%s", index), equipmets.toString());

    }

    /**
     * 工程カンバン用帳票リプレースデータ作成
     *
     * @param workKanbanEntitys
     * @return
     */
    private Map<String, Object> createReplaceDataSeparateWorkKanban(List<WorkKanbanInfoEntity> workKanbanEntitys, List<ActualResultEntity> actualResultList) {
        Map<String, Object> replaceWords = new HashMap<>();
        workKanbanEntitys.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));

        for (Integer index = 1; workKanbanEntitys.size() >= index; index++) {
            WorkKanbanInfoEntity work = workKanbanEntitys.get(index - 1);
            List<ActualResultEntity> workingList = new ArrayList<>();
            List<ActualResultEntity> compList = new ArrayList<>();
            List<String> interruptList = new ArrayList<>();
            List<String> delayList = new ArrayList<>();
            List<String> parentOrganizations = new ArrayList<>();
            List<String> parentEquipments = new ArrayList<>();
            List<String> organizations = new ArrayList<>();
            List<String> equipments = new ArrayList<>();

            for (ActualResultEntity actual : actualResultList) {
                if (actual.getFkWorkKanbanId().equals(work.getWorkKanbanId())) {
                    setParentOrganizations(parentOrganizations, actual);
                    setOrganizations(organizations, actual);
                    setParentEquipments(parentEquipments, actual);
                    setEquipments(equipments, actual);
                    switch (actual.getActualStatus()) {
                        case WORKING:
                            workingList.add(actual);
                            break;
                        case COMPLETION:
                            compList.add(actual);
                            setDelay(delayList, actual);
                            break;
                        case SUSPEND:
                            setIntterupt(interruptList, actual);
                            break;
                        default:
                            break;
                    }
                }
            }

            replaceWords.put(String.format("TAG_WORK_NAME_S%s", index), work.getWorkName());
            replaceWords.put(String.format("TAG_WORK_STATUS_S%s", index), LocaleUtils.getString(work.getWorkStatus().getResourceKey()));
//            時間の単位で再計算する処理を追加すること
            replaceWords.put(String.format("TAG_WORK_TAKTTIME_S%s", index), work.getTaktTime());
            replaceWords.put(String.format("TAG_WORK_WORKTIME_S%s", index), work.getSumTimes());
            replaceWords.put(String.format("TAG_WORK_PLAN_START_S%s", index), work.getStartDatetime());
            replaceWords.put(String.format("TAG_WORK_PLAN_END_S%s", index), work.getCompDatetime());
            StringBuilder interrupts = new StringBuilder();
            for (String interrupt : interruptList) {
                interrupts.append(interrupt);
                interrupts.append(",");
            }
            deleteLastTrailingCharacter(interrupts);
            replaceWords.put(String.format("TAG_WORK_INTERRUPT_S%s", index), interrupts.toString());
            StringBuilder delays = new StringBuilder();
            for (String delay : delayList) {
                delays.append(delay);
                delays.append(",");
            }
            deleteLastTrailingCharacter(delays);
            replaceWords.put(String.format("TAG_WORK_DELAY_S%s", index), delays.toString());
            for (WorkKanbanPropertyInfoEntity pro : work.getPropertyCollection()) {
                replaceWords.put(String.format("TAG_WORK_PROPERTY_S%s(%s)", index, pro.getWorkKanbanPropName()), pro.getWorkKanbanPropValue());
            }
            createReplaceDataSeparateWorkKanbanActual(replaceWords, index, workingList, compList, parentOrganizations, organizations, parentEquipments, equipments);
        }

        return replaceWords;
    }

    /**
     * 実績用帳票リプレースデータ作成
     *
     * @param map
     * @param index
     * @param workingList
     * @param compList
     * @param pO
     * @param o
     * @param pE
     * @param e
     */
    private void createReplaceDataSeparateWorkKanbanActual(Map<String, Object> map, int index, List<ActualResultEntity> workingList, List<ActualResultEntity> compList, List<String> pO, List<String> o, List<String> pE, List<String> e) {
        Optional<ActualResultEntity> start = workingList.stream().min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_START_S%s", index), start.get().getImplementDatetime());
        }
        Optional<ActualResultEntity> compDate = compList.stream().max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_END_S%s", index), compDate.get().getImplementDatetime());
        }
        StringBuilder parentOrganizations = new StringBuilder();
        for (String parentOrganization : pO) {
            parentOrganizations.append(parentOrganization);
            parentOrganizations.append(",");
        }
        deleteLastTrailingCharacter(parentOrganizations);
        map.put(String.format("TAG_WORK_PARENT_ORGANIZATION_S%s", index), parentOrganizations.toString());
        StringBuilder organizations = new StringBuilder();
        for (String organization : o) {
            organizations.append(organization);
            organizations.append(",");
        }
        deleteLastTrailingCharacter(organizations);
        map.put(String.format("TAG_WORK_ORGANIZATION_S%s", index), organizations.toString());
        StringBuilder parentEquipments = new StringBuilder();
        for (String parentEquipmet : pE) {
            parentEquipments.append(parentEquipmet);
            parentEquipments.append(",");
        }
        deleteLastTrailingCharacter(parentEquipments);
        map.put(String.format("TAG_WORK_PARENT_EQUIPMENT_S%s", index), parentEquipments.toString());
        StringBuilder equipmets = new StringBuilder();
        for (String equipmet : e) {
            equipmets.append(equipmet);
            equipmets.append(",");
        }
        deleteLastTrailingCharacter(equipmets);
        map.put(String.format("TAG_WORK_EQUIPMENT_S%s", index), equipmets.toString());

    }

    private void setParentOrganizations(List<String> parentOrganizations, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getOrganizationParentName())) {
            if (!parentOrganizations.contains(actual.getOrganizationParentName())) {
                parentOrganizations.add(actual.getOrganizationParentName());
            }
        }
    }

    private void setOrganizations(List<String> organizations, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getOrganizationName())) {
            if (!organizations.contains(actual.getOrganizationName())) {
                organizations.add(actual.getOrganizationName());
            }
        }
    }

    private void setParentEquipments(List<String> parentEquipments, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getEquipmentParentName())) {
            if (!parentEquipments.contains(actual.getEquipmentParentName())) {
                parentEquipments.add(actual.getEquipmentParentName());
            }
        }
    }

    private void setEquipments(List<String> equipments, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getEquipmentName())) {
            if (!equipments.contains(actual.getEquipmentName())) {
                equipments.add(actual.getEquipmentName());
            }
        }
    }

    private void setIntterupt(List<String> interruptList, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getInterruptReason())) {
            if (!interruptList.contains(actual.getInterruptReason())) {
                interruptList.add(actual.getInterruptReason());
            }
        }
    }

    private void setDelay(List<String> delayList, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getDelayReason())) {
            if (!delayList.contains(actual.getDelayReason())) {
                delayList.add(actual.getDelayReason());
            }
        }
    }

    private List<String> createCheckWords() {
        List<String> checkWords = new ArrayList<>();
        checkWords.add("TAG_");

        return checkWords;
    }

    private void deleteLastTrailingCharacter(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private boolean isLedgerTemplatePass() {
        return Boolean.FALSE;
    }

    /**
     * 置換データを生成する トレーサビリティ情報
     *
     * @param map
     * @param index
     * @param actualResultList
     */
    private void createReplaceDataTraceability(Map<String, Object> map, int index, List<ActualResultEntity> actualResultList) {
        for (ActualResultEntity actualResult: actualResultList) {
            for (ActualPropertyEntity actualProperty : actualResult.getPropertyCollection()) {
                if (!CustomPropertyTypeEnum.TYPE_TRACE.equals(actualProperty.getActualPropType())) {
                    continue;
                }
                map.put(actualProperty.getActualPropName(), actualProperty.getActualPropValue());
            }
        }
    }

}
