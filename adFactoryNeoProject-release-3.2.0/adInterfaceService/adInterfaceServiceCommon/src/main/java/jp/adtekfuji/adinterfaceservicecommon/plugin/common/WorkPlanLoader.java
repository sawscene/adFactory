/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.common;

import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adinterfaceservicecommon.plugin.entity.WorkPlanInfo;

/**
 * 生産計画を読み込み計画情報を構築させる
 *
 * @author fu-kato
 */
public interface WorkPlanLoader {

    /**
     *
     * @param folder
     * @param tabMode
     * @param importFormatInfo
     * @param ignored
     * @return
     * @throws Exception
     */
    WorkPlanInfo importKanban(String folder, int tabMode, ImportFormatInfo importFormatInfo, boolean ignored) throws Exception;
}
