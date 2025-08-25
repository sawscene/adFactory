/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.List;

/**
 * 帳票出力条件
 *
 * @author nar-nakamura
 */
public class ReportOutputCondition {

    private String outputFolder; // 出力先
    private boolean leaveTags; // 置換されなかったタグを残す
    private boolean exportAsPdf; // pdfにて出力するか?
    private List<String> templateNames; // 帳票出力テンプレートファイル名

    /**
     * コンストラクタ
     */
    public ReportOutputCondition() {
    }

    /**
     * 出力先を取得する。
     *
     * @return 出力先
     */
    public String getOutputFolder() {
        return this.outputFolder;
    }

    /**
     * 出力先を設定する。
     *
     * @param outputFolder 出力先
     */
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * 置換されなかったタグを残すかどうかを取得する。
     *
     * @return 置換されなかったタグを残す(true:残す, false:消去する)
     */
    public boolean isLeaveTags() {
        return this.leaveTags;
    }

    /**
     * PDF形式で出力するかどうかを取得します。
     *
     * @return PDF形式で出力する場合はtrue、それ以外の場合はfalse
     */
    public boolean isExportAsPdf() {
        return exportAsPdf;
    }

    /**
     * PDF形式で出力するかどうかを設定します。
     *
     * @param exportAsPdf PDF形式で出力する場合はtrue、それ以外の場合はfalse
     */
    public void setExportAsPdf(boolean exportAsPdf) {
        this.exportAsPdf = exportAsPdf;
    }

    /**
     * 置換されなかったタグを残すかどうかを設定する。
     *
     * @param leaveTags 置換されなかったタグを残す(true:残す, false:消去する)
     */
    public void setLeaveTags(boolean leaveTags) {
        this.leaveTags = leaveTags;
    }

     /**
     * 帳票出力テンプレートファイルパスを取得する。
     *
     * @return 帳票出力テンプレートファイル名
     */
    public List<String> getTemplateNames() {
        return this.templateNames;
    }

    /**
     * 帳票出力テンプレートファイルパスを設定する。
     *
     * @param templateNames 帳票出力テンプレートファイル名
     */
    public void setTemplateNames(List<String> templateNames) {
        this.templateNames = templateNames;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReportOutputCondition{")
                .append("outputFolder=").append(this.outputFolder)
                .append(", leaveTags=").append(this.leaveTags)
                .append(", exportAsPdf=").append(this.exportAsPdf)
                .append(", templateNames=").append(this.templateNames)
                .append("}")
                .toString();
    }
}
