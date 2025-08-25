package jp.adtekfuji.addatabaseapp.utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * コマンド定義クラス
 *
 * @author e-mori
 */
public class CMDContents {

    public static final String EXE = "cmd.exe";
    public static final String ARG_C = "/C";
    public static final String ARG_START = "start";
    public static final String ARG_STOP = "stop";
    public static final String ARG_ECHO_ON = "@echo on";
    public static final String ARG_ECHO_OFF = "@echo off";
    public static final String ARG_PAUSE = "pause";
    public static final String ARG_EXIT = "exit";
    public static final String SET_PATH  = "set path=%Path%;";

    // サービス名
    public final static String SERVICE_ADINTERFACE= "adInterfaceService";// adInterface 管理サービス
    public final static String SERVICE_TOMEE = "TomEE";// Apache TomEE
}
