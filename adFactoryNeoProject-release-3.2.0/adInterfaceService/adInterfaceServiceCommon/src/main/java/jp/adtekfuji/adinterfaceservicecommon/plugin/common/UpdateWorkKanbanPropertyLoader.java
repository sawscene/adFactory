/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.common;

import java.util.List;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;

/**
 * 工程カンバンプロパティ更新ファイル(update_work_kanban_property.csv) 更新定義
 *
 * @author fu-kato
 */
public interface UpdateWorkKanbanPropertyLoader {

    /**
     * 工程カンバンプロパティ更新ファイルを読み込む
     *
     * @param folder
     * @param tabMode
     * @param importFormatInfo
     * @param ignored
     * @return
     * @throws java.lang.Exception
     */
    List<ImportWorkKanbanPropertyCsv> importFile(String folder, int tabMode, ImportFormatInfo importFormatInfo, boolean ignored) throws Exception;
}
