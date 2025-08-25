/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * バッキングビーン
 * 
 * @author s-heya
 */
public class BackingBeen implements Serializable {

    private final Logger logger = LogManager.getLogger();

    /**
     * ダイアログを開く。
     * 
     * @param xhtml XHTMLのパス
     * @param title タイトルのリソースキー
     * @param msg 表示メッセージのリソースキー
     */
    public void openDialog(String xhtml, String title, String msg) {
        openDialog(xhtml, title, msg, null);
    }
    
    /**
     * ダイアログを開く。
     * 
     * @param xhtml XHTMLのパス
     * @param title タイトルのリソースキー
     * @param msg 表示メッセージのリソースキー
     * @param detail 詳細メッセージ
     */
    public void openDialog(String xhtml, String title, String msg, String detail) {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("resizable", false);
        options.put("closable", false);
        options.put("width", 700);
        
        Map<String, List<String>> params = new HashMap<>();
        params.put("title", Collections.singletonList(title));
        params.put("id", Collections.singletonList(msg));
        if (!StringUtils.isEmpty(detail)) {
            params.put("detail", Collections.singletonList(detail));
        }
        
        //RequestContext.getCurrentInstance().openDialog(xhtml, options, params);
        PrimeFaces.current().dialog().openDynamic(xhtml, options, params);
     }
    
    /**
     * ダイアログを閉じる。
     */
    public void closeDialog() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        String action = params.get("action");

        //RequestContext.getCurrentInstance().closeDialog(action);
        PrimeFaces.current().dialog().closeDynamic(action);

        logger.info("closeDialog: {} {}", id, action);
    }
}
