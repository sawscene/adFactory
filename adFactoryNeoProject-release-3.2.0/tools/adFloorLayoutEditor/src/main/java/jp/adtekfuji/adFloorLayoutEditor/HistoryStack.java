/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFloorLayoutEditor;

import java.util.Stack;

/**
 *
 * @author ke.yokoi
 */
public class HistoryStack {
   
    private final Stack<UndoRedoable> undoStack = new Stack<>();
    private final Stack<UndoRedoable> redoStack = new Stack<>();
     
    /**
     * アンドゥ
     * @return
     */
    public UndoRedoable undo(){
 
        UndoRedoable result = null;
        if( !undoStack.empty() ){
            result = undoStack.pop();
            result.doUndo();
            redoStack.push(result);
        }
 
        return result;
    }
 
    /**
     * リドゥ
     * @return
     */
    public UndoRedoable redo(){
 
        UndoRedoable result = null;
        if( !redoStack.empty() ){
            result = redoStack.pop();
            result.doRedo();
            undoStack.push(result);
        }
 
        return result;
    }
 
    /**
     * 履歴の追加
     * @param history
     */
    public void add(UndoRedoable history){
        undoStack.push(history);
        redoStack.clear();
    }
     
    /**
     * アンドゥの列挙
     * @return
     */
    public final Iterable<UndoRedoable> iterateUndo(){
        return undoStack;
    }
}
