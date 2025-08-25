/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.facade;

/**
 *
 * @author ke.yokoi
 */
public class LineTimerProperty {

    private Long startCountTime;
    private Long taktTime;

    public LineTimerProperty(Long startCountTime, Long taktTime) {
        this.startCountTime = startCountTime;
        this.taktTime = taktTime;
    }

    public Long getStartCountTime() {
        return startCountTime;
    }

    public void setStartCountTime(Long startCountTime) {
        this.startCountTime = startCountTime;
    }

    public Long getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Long taktTime) {
        this.taktTime = taktTime;
    }

}
