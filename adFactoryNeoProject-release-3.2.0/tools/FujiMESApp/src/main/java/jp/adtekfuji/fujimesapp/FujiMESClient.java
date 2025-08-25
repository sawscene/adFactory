/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.fujimesapp;

import java.io.File;
import java.io.IOException;
import jp.adtekfuji.fujimesapp.utils.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FUJI-MES クライアント
 * 
 * @author s-heya
 */
public class FujiMESClient {
    private static final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public FujiMESClient() {
    }

    /**
     * 検査データを送信する。
     */
    public void postInspectionData() {
        try {
            String iniFilePath = System.getProperty("user.dir") + "\\FujiMESApp.ini";
            File file = new File(iniFilePath);
            if (file.exists()) {
                IniFile iniFile = new IniFile(iniFilePath);
            }

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
}
