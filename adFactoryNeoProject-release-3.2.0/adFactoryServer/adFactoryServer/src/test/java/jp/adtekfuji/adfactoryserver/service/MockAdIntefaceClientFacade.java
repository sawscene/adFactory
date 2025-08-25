/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.DeviceConnectionServiceCommand;
import jp.adtekfuji.adFactory.adinterface.command.LineTimerNoticeCommand;

/**
 *
 * @author ke.yokoi
 */
public class MockAdIntefaceClientFacade extends AdIntefaceClientFacade {

    public MockAdIntefaceClientFacade() {
    }

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void noticeActual(ActualNoticeCommand command) {
    }

    @Override
    public void noticeCalling(CallingNoticeCommand command) {
    }

    @Override
    public void noticeLineTimer(LineTimerNoticeCommand command) {
    }

    @Override
    public void noticeDeviceConnectionService(DeviceConnectionServiceCommand command) {
    }

}
