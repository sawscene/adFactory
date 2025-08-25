/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import jp.adtekfuji.javafxcommon.controls.popup.TooltipBehavior;

/**
 * ツールチップビルダークラス
 *
 * @author s-heya
 */
public class TooltipBuilder {

    /**
     * ツールチップを構築する。
     *
     * @param node
     * @return
     * @throws java.lang.Exception
     */
    public static Tooltip build(Node node) throws Exception {
        if (Objects.isNull(node)) {
            throw new Exception("Node is not specified.");
        }

        Tooltip toolTip = new Tooltip();

        final TooltipBehavior behavior = new TooltipBehavior();
        // マウスが乗ってから0.1秒後に表示
        behavior.setOpenDuration(new Duration(100));
        behavior.setHideDuration(Duration.INDEFINITE);
        // マウスが放れてから0.3秒後に非表示
        behavior.setLeftDuration(new Duration(300));
        behavior.install(node, toolTip);

        return toolTip;
    }

}
