/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.entity.unittemplate.ConUnitTemplateAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ワークフロー画面編集用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.01.Tsu
 */
public class UnitTemplatePaneEditor {

    private final static Logger logger = LogManager.getLogger();
    private final static SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 編集しているユニットテンプレートを返す
     *
     * @param unittemplateModel
     * @return ユニットテンプレート
     */
    public static UnitTemplateInfoEntity getUnitTemplateInfoEntity(UnitTemplateModel unittemplateModel) {
        return unittemplateModel.getUnitTemplate();
    }

    /**
     * 工程順を直列に追加
     *
     * @param unittemplateModel
     * @param entity 挿入するユニットテンプレート
     */
    public static void addSerial(UnitTemplateModel unittemplateModel, WorkflowInfoEntity entity) {
        try {
            if (!addCheck(unittemplateModel, entity)) {
                return;
            }

            //開始時間終了時間の設定
            Date startDate = sdf.parse("1970-01-01 00:00:00+00");

            if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof UnitTemplateCell) {
                UnitTemplateCell cell = (UnitTemplateCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase();
                ConUnitTemplateAssociateInfoEntity templateAssociate = cell.getUnitTemplateAssociate();
                // TODO:スキップあるならこっちの計算
//                startDate = templateAssociate.getSkipFlag() ? templateAssociate.getStandardStartTime() : templateAssociate.getStandardEndTime();
                startDate = templateAssociate.getStandardEndTime();
            } else if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof ParallelStartCell) {
                // ParallelCell内の一番大きい時間を取得する
                UnitTemplateCell cell = unittemplateModel.getUnitTemplatePane().getLastBPMNCell((ParallelStartCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase());
                if (Objects.nonNull(cell)) {
                    ConUnitTemplateAssociateInfoEntity templateAssociate = cell.getUnitTemplateAssociate();
                    startDate = templateAssociate.getStandardEndTime();
                }
            }

            Date endDate = new Date(startDate.getTime() + getWorkflowTactTime(entity));

            //工程順工程関連付けデータ作成
            ConUnitTemplateAssociateInfoEntity templateAssociate = new ConUnitTemplateAssociateInfoEntity();
            templateAssociate.setFkParentUnitTemplateId(unittemplateModel.getUnitTemplate().getUnitTemplateId());
            templateAssociate.setFkWorkflowId(entity.getWorkflowId());
            templateAssociate.setStandardStartTime(startDate);
            templateAssociate.setStandardEndTime(endDate);

            //工程セル作成
            UnitTemplateCell work = new UnitTemplateCell(templateAssociate, entity.getWorkflowName(), entity.getWorkflowRev());
            if (unittemplateModel.add(unittemplateModel.getUnitTemplatePane().getSelectedCellBase(), work)) {
                unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                work.setSelected(true);
            }

            unittemplateModel.updateTimetable(work, templateAssociate.getTaktTime(), false, false);
            unittemplateModel.updateWorkflowOrder();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ユニットテンプレートを直列に追加
     *
     * @param unittemplateModel
     * @param entity 挿入するユニットテンプレート
     */
    public static void addSerial(UnitTemplateModel unittemplateModel, UnitTemplateInfoEntity entity) {
        try {
            if (!addCheck(unittemplateModel, entity)) {
                return;
            }

            //開始時間終了時間の設定
            Date startDate = sdf.parse("1970-01-01 00:00:00+00");

            if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof UnitTemplateCell) {
                UnitTemplateCell cell = (UnitTemplateCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase();
                ConUnitTemplateAssociateInfoEntity templateAssociate = cell.getUnitTemplateAssociate();
                // TODO:スキップあるならこっちの計算
//                startDate = templateAssociate.getSkipFlag() ? templateAssociate.getStandardStartTime() : templateAssociate.getStandardEndTime();
                startDate = templateAssociate.getStandardEndTime();
            } else if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof ParallelStartCell) {
                // ParallelCell内の一番大きい時間を取得する
                UnitTemplateCell cell = unittemplateModel.getUnitTemplatePane().getLastBPMNCell((ParallelStartCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase());
                if (Objects.nonNull(cell)) {
                    ConUnitTemplateAssociateInfoEntity templateAssociate = cell.getUnitTemplateAssociate();
                    startDate = templateAssociate.getStandardEndTime();
                }
            }

            Date endDate = new Date(startDate.getTime() + entity.getTactTime());

            //工程順工程関連付けデータ作成
            ConUnitTemplateAssociateInfoEntity templateAssociate = new ConUnitTemplateAssociateInfoEntity();
            templateAssociate.setFkParentUnitTemplateId(unittemplateModel.getUnitTemplate().getUnitTemplateId());
            templateAssociate.setFkUnitTemplateId(entity.getUnitTemplateId());
            templateAssociate.setStandardStartTime(startDate);
            templateAssociate.setStandardEndTime(endDate);

            //工程セル作成
            UnitTemplateCell work = new UnitTemplateCell(templateAssociate, entity.getUnitTemplateName());
            if (unittemplateModel.add(unittemplateModel.getUnitTemplatePane().getSelectedCellBase(), work)) {
                unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                work.setSelected(true);
            }

            unittemplateModel.updateTimetable(work, templateAssociate.getTaktTime(), false, false);
            unittemplateModel.updateWorkflowOrder();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順を並列に追加
     *
     * @param unittemplateModel
     * @param entity 挿入するユニットテンプレート
     * @param isShift 並列工程追加時に時間をシフトするかs
     */
    public static void addParallel(UnitTemplateModel unittemplateModel, WorkflowInfoEntity entity, Boolean isShift) {
        if (!addCheck(unittemplateModel, entity)) {
            return;
        }

        //工程順工程関連付けデータ作成
        ConUnitTemplateAssociateInfoEntity templateAssociate = new ConUnitTemplateAssociateInfoEntity();
        templateAssociate.setFkParentUnitTemplateId(unittemplateModel.getUnitTemplate().getUnitTemplateId());
        templateAssociate.setFkWorkflowId(entity.getWorkflowId());

        //工程セル作成
        UnitTemplateCell work = new UnitTemplateCell(templateAssociate, entity.getWorkflowName(), entity.getWorkflowRev());
        if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof UnitTemplateCell) {
            ParallelStartCell parallelStartCell = new ParallelStartCell();
            ParallelEndCell parallelEndCell = new ParallelEndCell(parallelStartCell);

            UnitTemplateCell selectedCell = (UnitTemplateCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase();
            CellBase previousCell = unittemplateModel.getUnitTemplatePane().getPreviousCell(selectedCell);

            // 開始時間と終了時間の設定
            ConUnitTemplateAssociateInfoEntity selectedTempAssociate = selectedCell.getUnitTemplateAssociate();

            Date startDate = selectedTempAssociate.getStandardStartTime();
            if (isShift) {
                // TODO:スキップあるならこっちの計算
//                startDate = selectedTempAssociate.getSkipFlag() ? startDate : selectedTempAssociate.getStandardEndTime();
                startDate = selectedTempAssociate.getStandardEndTime();
            }
            templateAssociate.setStandardStartTime(startDate);
            // タクトタイムが算出できないので何か方法を考える
            templateAssociate.setStandardEndTime(new Date(startDate.getTime() + getWorkflowTactTime(entity)));

            if (Objects.nonNull(previousCell)
                    && unittemplateModel.remove(selectedCell)
                    && unittemplateModel.addGateway(previousCell, parallelStartCell, parallelEndCell)
                    && unittemplateModel.add(parallelStartCell, selectedCell)
                    && unittemplateModel.add(parallelStartCell, work)) {
                unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                work.setSelected(true);

                unittemplateModel.updateTimetable(work, templateAssociate.getTaktTime(), false, isShift);
                unittemplateModel.updateWorkflowOrder();

            } else {
                // 挿入
                parallelStartCell = unittemplateModel.getParallelStartCell(selectedCell);
                List<CellBase> cells = parallelStartCell.getFirstRow();
                int index = cells.indexOf(selectedCell) + 1;
                if (unittemplateModel.addWithUpdateTimetable(parallelStartCell, index, work)) {
                    unittemplateModel.updateWorkflowOrder();
                    unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                    work.setSelected(true);
                }
            }

        } else if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof ParallelStartCell) {
            ParallelStartCell parallelStartCell = (ParallelStartCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase();

            List<CellBase> cells = parallelStartCell.getFirstRow().stream()
                    .filter(o -> o instanceof UnitTemplateCell)
                    .collect(Collectors.toList());
            UnitTemplateCell beforeCell = (UnitTemplateCell) cells.get(cells.size() - 1);

            Date startDate = beforeCell.getUnitTemplateAssociate().getStandardStartTime();
            if (isShift) {
                startDate = beforeCell.getUnitTemplateAssociate().getStandardEndTime();
            }
            templateAssociate.setStandardStartTime(startDate);
            // タクトタイムが算出できないので何か方法を考える
            templateAssociate.setStandardEndTime(new Date(startDate.getTime() + RestAPI.getWorkflowTactTime(entity.getWorkflowId())));

            if (unittemplateModel.add(parallelStartCell, work)) {
                unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                work.setSelected(true);

                unittemplateModel.updateTimetable(work, templateAssociate.getTaktTime(), false, isShift);
                unittemplateModel.updateWorkflowOrder();
            }
        }
    }

    /**
     * ユニットテンプレートを並列に追加
     *
     * @param unittemplateModel
     * @param entity 挿入するユニットテンプレート
     * @param isShift 並列工程追加時に時間をシフトするか
     */
    public static void addParallel(UnitTemplateModel unittemplateModel, UnitTemplateInfoEntity entity, Boolean isShift) {
        if (!addCheck(unittemplateModel, entity)) {
            return;
        }

        //工程順工程関連付けデータ作成
        ConUnitTemplateAssociateInfoEntity templateAssociate = new ConUnitTemplateAssociateInfoEntity();
        templateAssociate.setFkWorkflowId(unittemplateModel.getUnitTemplate().getUnitTemplateId());
        templateAssociate.setFkUnitTemplateId(entity.getUnitTemplateId());

        //工程セル作成
        UnitTemplateCell work = new UnitTemplateCell(templateAssociate, entity.getUnitTemplateName());
        if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof UnitTemplateCell) {
            ParallelStartCell parallelStartCell = new ParallelStartCell();
            ParallelEndCell parallelEndCell = new ParallelEndCell(parallelStartCell);

            UnitTemplateCell selectedCell = (UnitTemplateCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase();
            CellBase previousCell = unittemplateModel.getUnitTemplatePane().getPreviousCell(selectedCell);

            // 開始時間と終了時間の設定
            ConUnitTemplateAssociateInfoEntity entityflowWork = selectedCell.getUnitTemplateAssociate();

            Date startDate = entityflowWork.getStandardStartTime();
            if (isShift) {
                // TODO:スキップあるならこっちの計算
//                startDate = entityflowWork.getSkipFlag() ? startDate : entityflowWork.getStandardEndTime();
                startDate = entityflowWork.getStandardEndTime();
            }
            templateAssociate.setStandardStartTime(startDate);
            templateAssociate.setStandardEndTime(new Date(startDate.getTime() + RestAPI.getUnitTemplateTactTime(entity.getUnitTemplateId())));

            if (Objects.nonNull(previousCell)
                    && unittemplateModel.remove(selectedCell)
                    && unittemplateModel.addGateway(previousCell, parallelStartCell, parallelEndCell)
                    && unittemplateModel.add(parallelStartCell, selectedCell)
                    && unittemplateModel.add(parallelStartCell, work)) {
                unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                work.setSelected(true);

                unittemplateModel.updateTimetable(work, templateAssociate.getTaktTime(), false, isShift);
                unittemplateModel.updateWorkflowOrder();

            } else {
                // 挿入
                parallelStartCell = unittemplateModel.getParallelStartCell(selectedCell);
                List<CellBase> cells = parallelStartCell.getFirstRow();
                int index = cells.indexOf(selectedCell) + 1;
                if (unittemplateModel.addWithUpdateTimetable(parallelStartCell, index, work)) {
                    unittemplateModel.updateWorkflowOrder();
                    unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                    work.setSelected(true);
                }
            }

        } else if (unittemplateModel.getUnitTemplatePane().getSelectedCellBase() instanceof ParallelStartCell) {
            ParallelStartCell parallelStartCell = (ParallelStartCell) unittemplateModel.getUnitTemplatePane().getSelectedCellBase();

            List<CellBase> cells = parallelStartCell.getFirstRow().stream()
                    .filter(o -> o instanceof UnitTemplateCell)
                    .collect(Collectors.toList());
            UnitTemplateCell beforeCell = (UnitTemplateCell) cells.get(cells.size() - 1);

            Date startDate = beforeCell.getUnitTemplateAssociate().getStandardStartTime();
            if (isShift) {
                startDate = beforeCell.getUnitTemplateAssociate().getStandardEndTime();
            }
            templateAssociate.setStandardStartTime(startDate);
            templateAssociate.setStandardEndTime(new Date(startDate.getTime() + entity.getTactTime()));

            if (unittemplateModel.add(parallelStartCell, work)) {
                unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection().add(templateAssociate);
                work.setSelected(true);

                unittemplateModel.updateTimetable(work, templateAssociate.getTaktTime(), false, isShift);
                unittemplateModel.updateWorkflowOrder();
            }
        }
    }

    /**
     * セルが挿入できるかの確認
     *
     * @param parent 親のユニットテンプレート
     * @param object 追加する情報
     * @return 追加の有無
     */
    private static boolean addCheck(UnitTemplateModel unittemplateModel, Object object) {
        if (object instanceof WorkflowInfoEntity) {
            WorkflowInfoEntity entity = (WorkflowInfoEntity) object;
            if (Objects.isNull(unittemplateModel.getUnitTemplatePane().getSelectedCellBase()) || Objects.isNull(entity)) {
                return false;
            }
            Boolean isAdd = true;
            for (ConUnitTemplateAssociateInfoEntity associate : unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection()) {
                if (Objects.nonNull(associate.getFkWorkflowId())) {
                    if (associate.getFkWorkflowId().equals(entity.getWorkflowId())) {
                        isAdd = false;
                        break;
                    }
                }
            }
            if (isAdd) {
                return true;
            }
        } else if (object instanceof UnitTemplateInfoEntity) {
            UnitTemplateInfoEntity entity = (UnitTemplateInfoEntity) object;
            if (Objects.isNull(unittemplateModel.getUnitTemplatePane().getSelectedCellBase()) || Objects.isNull(entity)) {
                return false;
            }
            Boolean isAdd = true;
            for (ConUnitTemplateAssociateInfoEntity associate : unittemplateModel.getUnitTemplate().getConUnitTemplateAssociateCollection()) {
                if (Objects.nonNull(associate.getFkUnitTemplateId())) {
                    if (associate.getFkUnitTemplateId().equals(entity.getUnitTemplateId())) {
                        isAdd = false;
                        break;
                    }
                }
            }
            if (isAdd) {
                return true;
            }
        }
        return false;
    }

    /**
     * セルの削除
     *
     * @param unittemplateModel
     * @param parent 親のユニットテンプレート
     */
    public static void delete(UnitTemplateModel unittemplateModel, UnitTemplateInfoEntity parent) {
        List<CellBase> checkedCells = unittemplateModel.getUnitTemplatePane().getCheckedCells();
        if (checkedCells.isEmpty()) {
            return;
        }

        String messgage = checkedCells.size() > 1
                ? LocaleUtils.getString("key.DeleteMultipleMessage")
                : LocaleUtils.getString("key.DeleteSingleMessage");
        String content = checkedCells.size() > 1
                ? null
                : checkedCells.get(0) instanceof UnitTemplateCell ? ((UnitTemplateCell) checkedCells.get(0)).getUnitTemplateNameWithRev() : null;

        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
        if (ret.equals(ButtonType.OK)) {
            //チェックされている工程の削除
            for (CellBase cell : checkedCells) {
                if (cell instanceof UnitTemplateCell) {
                    ConUnitTemplateAssociateInfoEntity tempAssociate = ((UnitTemplateCell) cell).getUnitTemplateAssociate();
                    unittemplateModel.removeWithUpdateTimetable(cell);
                    parent.getConUnitTemplateAssociateCollection().remove(tempAssociate);
                }
            }

            // 空となったParallelCell
            List<ParallelStartCell> emptyCells = new ArrayList<>();
            // 直列工程となったParallelCell
            List<ParallelStartCell> changeCells = new ArrayList<>();

            // 不要となったParallelCellを削除
            do {
                emptyCells.clear();
                changeCells.clear();

                for (CellBase cell : unittemplateModel.getUnitTemplatePane().getCellList()) {
                    if (cell instanceof ParallelStartCell) {
                        ParallelStartCell parallelCell = (ParallelStartCell) cell;
                        ObservableList<Node> children = parallelCell.getParallelPane().getChildren();

                        if (children.isEmpty()) {
                            emptyCells.add(parallelCell);
                        }

                        if (children.size() == 1) {
                            changeCells.add(parallelCell);
                        }
                    }
                }

                // 空となったParallelCellを削除
                for (ParallelStartCell emptyCell : emptyCells) {
                    unittemplateModel.remove(emptyCell, emptyCell.getParallelEndCell());
                }

                // 直列工程となったParallelCellを再構築
                for (ParallelStartCell changeCell : changeCells) {
                    List<CellBase> cells = changeCell.getCells();

                    for (ListIterator iterator = cells.listIterator(cells.size()); iterator.hasPrevious();) {
                        CellBase cell = (CellBase) iterator.previous();
                        if (cell instanceof UnitTemplateCell) {
                            unittemplateModel.remove(cell);
                            unittemplateModel.add(changeCell.getParallelEndCell(), (UnitTemplateCell) cell);
                        } else if (cell instanceof ParallelStartCell) {
                            addParallelCell(unittemplateModel, changeCell, (ParallelStartCell) cell);
                        }
                    }
                    unittemplateModel.remove(changeCell, changeCell.getParallelEndCell());
                }

                unittemplateModel.updateWorkflowOrder();

            } while (!emptyCells.isEmpty() || !changeCells.isEmpty()); // 削除対象が無くなるまで繰り返す
        }
    }

    /**
     * 並列工程セルを追加する
     *
     * @param previousCell
     * @param parallelStartCell
     */
    private static void addParallelCell(UnitTemplateModel unittemplateModel, ParallelStartCell previousCell, ParallelStartCell parallelStartCell) {
        List<CellBase> cells = parallelStartCell.getCells();

        for (CellBase cell : cells) {
            unittemplateModel.remove(cell);
        }

        unittemplateModel.remove(parallelStartCell, parallelStartCell.getParallelEndCell());
        unittemplateModel.addGateway(previousCell.getParallelEndCell(), parallelStartCell, parallelStartCell.getParallelEndCell());

        for (CellBase cell : cells) {
            if (cell instanceof UnitTemplateCell) {
                unittemplateModel.add(parallelStartCell, (UnitTemplateCell) cell);
            } else if (cell instanceof ParallelStartCell) {
                addParallelCell(unittemplateModel, parallelStartCell, (ParallelStartCell) cell);
            }
        }
    }

    /**
     * 表示されているすべてのセルを選択する
     *
     * @param unittemplateModel
     */
    public static void allCheck(UnitTemplateModel unittemplateModel) {
        for (CellBase cell : unittemplateModel.getUnitTemplatePane().getCellList()) {
            cell.setChecked(true);
        }
    }

    /**
     * 選択されているセルを全て解除する
     *
     * @param unittemplateModel
     */
    public static void allUncheck(UnitTemplateModel unittemplateModel) {
        for (CellBase cell : unittemplateModel.getUnitTemplatePane().getCellList()) {
            cell.setChecked(false);
        }
    }

    /**
     * @param unittemplateModel* ワークフローの更新
     *
     */
    public static void updateWorkflowOrder(UnitTemplateModel unittemplateModel) {
        unittemplateModel.updateWorkflowOrder();
    }

    private static Long getWorkflowTactTime(WorkflowInfoEntity entity) {
        Long result = 0l;
        if (Objects.nonNull(entity.getConWorkflowWorkInfoCollection()) && !entity.getConWorkflowWorkInfoCollection().isEmpty()) {
            if (entity.getConWorkflowWorkInfoCollection().size() == 1) {
                result = entity.getConWorkflowWorkInfoCollection().get(0).getTaktTime();
            } else if (entity.getConWorkflowWorkInfoCollection().size() > 1) {
                Long min = entity.getConWorkflowWorkInfoCollection().get(0).getStandardStartTime().getTime();
                Long max = entity.getConWorkflowWorkInfoCollection().get(0).getStandardEndTime().getTime();
                for (ConWorkflowWorkInfoEntity con : entity.getConWorkflowWorkInfoCollection()) {
                    if (min > con.getStandardStartTime().getTime()) {
                        min = con.getStandardStartTime().getTime();
                    }
                    if (max < con.getStandardEndTime().getTime()) {
                        max = con.getStandardEndTime().getTime();
                    }
                }
                result = max - min;
            }
        }
        return result;
    }
}
