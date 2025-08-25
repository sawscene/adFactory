/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

/**
 * コンポーネントのインターフェイス
 *
 * @author s-heya
 */
public interface ComponentHandler {

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。
     *
     * @return
     */
    boolean destoryComponent();
}
