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
public class MovableNodeModel implements UndoRedoable {
    final private MovableNode node;
    private Boolean visible;
    private double posX;
    private double posY;
    private Boolean pre_enable;
    private double pre_posX;
    private double pre_posY;
    
    public MovableNodeModel(MovableNode  node ) {
        this.node = node;
    }

    public void setPre(Boolean visible, double posX, double posY) {
        this.pre_enable = visible;
        this.pre_posX = posX;
        this.pre_posY = posY;
    }
    
    public void setNow(Boolean visible, double posX, double posY) {
        this.visible = visible;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void doUndo() {
        node.update(pre_enable, pre_posX, pre_posY);
    }

    @Override
    public void doRedo() {
        node.update(visible, posX, posY);
    }
    
}
