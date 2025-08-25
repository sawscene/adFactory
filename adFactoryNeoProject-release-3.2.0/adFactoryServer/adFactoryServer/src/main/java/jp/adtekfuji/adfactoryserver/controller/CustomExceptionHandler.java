/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.util.Iterator;
import java.util.Map;
import jakarta.faces.FacesException;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;

/**
 * ajax通信時、セッションタイムアウトのエラーハンドリング
 * 
 * @author shizuka.hirano
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {
    
    /**
     * 例外ハンドラ
     */
    private ExceptionHandler wrapped;

    /**
     * コンストラクタ
     * 
     * @param exception 例外ハンドラ
     */
    CustomExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    /**
     * 例外ハンドラを取得する
     * 
     * @return 例外ハンドラ
     */
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    /**
     * ログアウト画面遷移
     * 
     * @throws FacesException 
     */
    @Override
    public void handle() throws FacesException {
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context
                    = (ExceptionQueuedEventContext) event.getSource();

            Throwable t = context.getException();

            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();

            try {

                if (t instanceof ViewExpiredException) {
                    requestMap.put("jakarta.servlet.error.message", "Session expired, try again!");
                    String errorPageLocation = "/logout.xhtml";
                    fc.setViewRoot(fc.getApplication().getViewHandler().createView(fc, errorPageLocation));
                    fc.getPartialViewContext().setRenderAll(true);
                    fc.renderResponse();
                } else {
                    requestMap.put("jakarta.servlet.error.message", t.getMessage());
                    nav.handleNavigation(fc, null, "/logout.xhtml");
                }

                fc.renderResponse();
            } finally {
                i.remove();
            }
        }
        getWrapped().handle();
    }
}
