/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * ライン制御用ステータス
 *
 * @author ke.yokoi
 */
public enum LineManagedStateEnum {

    START_WAIT,
    STARTCOUNT,
    STARTCOUNT_PAUSE,
    TAKTCOUNT,
    TAKTCOUNT_PAUSE,
    STOP;

}
