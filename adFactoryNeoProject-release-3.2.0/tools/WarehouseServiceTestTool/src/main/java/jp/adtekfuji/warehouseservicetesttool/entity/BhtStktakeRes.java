/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.entity;

/**
 * ハンディ端末 棚卸結果
 *
 * @author nar-nakamura
 */
public class BhtStktakeRes {

    private String control;
    private String labelNo;

    public BhtStktakeRes() {

    }

    /**
     * 棚卸結果
     *
     * @param control 部品識別名 (管理区分＋図番)
     * @param labelNo 連番
     */
    public BhtStktakeRes(
            String control, String labelNo) {
        this.control = control;
        this.labelNo = labelNo;
    }

    /**
     * 部品識別名 (管理区分＋図番)
     *
     * @return
     */
    public String getControl() {
        return this.control;
    }

    /**
     * 部品識別名 (管理区分＋図番)
     *
     * @param control
     */
    public void setControl(String control) {
        this.control = control;
    }

    /**
     * 連番
     *
     * @return
     */
    public String getLabelNo() {
        return this.labelNo;
    }

    /**
     * 連番
     *
     * @param labelNo
     */
    public void setLabelNo(String labelNo) {
        this.labelNo = labelNo;
    }

    @Override
    public String toString() {
        return "BhtStktakeRes{"
                + ", control=" + this.control
                + ", labelNo=" + this.labelNo
                + "}";
    }
}
