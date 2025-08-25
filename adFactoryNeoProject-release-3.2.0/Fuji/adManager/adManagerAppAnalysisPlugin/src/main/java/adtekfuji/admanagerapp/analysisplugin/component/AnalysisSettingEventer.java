/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.component;

import java.util.Objects;

/**
 * 設定変更時イベントクラス(コンポーネント間の呼び出しイベントがないためこれで代用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2017.02.14.Tsu
 */
public class AnalysisSettingEventer {

    // シーン間のインスタンスが保持できないためシングルトンにした
    private static AnalysisSettingEventer instance = null;

    private NormalDistributionCompoFxController distributionCompoFxController = null;
    private AnalysisSubMenuCompoController analysisSubMenuCompoController = null;

    public AnalysisSettingEventer() {
    }

    public static AnalysisSettingEventer getInstance() {
        return createInstance();
    }

    private static AnalysisSettingEventer createInstance() {
        if (Objects.isNull(instance)) {
            instance = new AnalysisSettingEventer();
        }
        return instance;
    }

    public void setNormalDistribution(NormalDistributionCompoFxController distributionCompoFxController) {
        this.distributionCompoFxController = distributionCompoFxController;
    }

    public void setAnalysisSubMenu(AnalysisSubMenuCompoController analysisSubMenuCompoController) {
        this.analysisSubMenuCompoController = analysisSubMenuCompoController;
    }
    
    public void updateDistribution(){
        this.distributionCompoFxController.update();
    }
}
