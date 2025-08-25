/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

import javafx.scene.control.TextField;

/**
 *
 * @author ke.yokoi
 */
public abstract class ValidationListener {

    /**
     * 問題なし
     *
     * @param field
     */
    public abstract void noProblem(TextField field);

    /**
     * 問題あり
     *
     * @param field
     * @param exception
     */
    public abstract void thereProblem(TextField field, Exception exception);

}
