/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFloorLayoutEditor;

/**
 *
 * @author ke.yokoi
 */
public interface UndoRedoable {
    public void doUndo();
    public void doRedo();
}
