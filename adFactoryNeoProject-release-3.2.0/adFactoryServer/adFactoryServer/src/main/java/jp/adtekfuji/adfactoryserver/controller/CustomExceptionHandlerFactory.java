/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

/**
 * jsfの例外ハンドラファクトリ
 * 
 * @author shizuka.hirano
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {
    
    /**
     * 例外ハンドラファクトリ
     */
    private ExceptionHandlerFactory parent;

    /**
     * コンストラクタ
     * 
     * @param parent 例外ハンドラファクトリ
     */
    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    /**
     * 例外ハンドラを取得する
     * 
     * @return 例外ハンドラ
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = new CustomExceptionHandler(parent.getExceptionHandler());
        return handler;
    }

}
