/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

/**
 *
 * @author e.mori
 */
public class KanbanRegistPreprocessResultEntity {
    
    private Boolean result;
    private String resultMessage;

    public KanbanRegistPreprocessResultEntity(Boolean result, String resultMessage) {
        this.result = result;
        this.resultMessage = resultMessage;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public String toString() {
        return "KanbanRegistPreprocessResultEntity{" + "result = " + getResult() + "resultMessage = " + getResultMessage() + '}';
    }
}
