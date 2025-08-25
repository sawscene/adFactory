/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 帳票出力結果
 *
 * @author nar-nakamura
 */
public enum OutputReportResultEnum {

    SUCCESS,// 成功
    FATAL,// 失敗
    TEMPLATE_NOT_FOUND,// テンプレートファイルがない
    TEMPLATE_LOAD_FAILED,// テンプレートファイル読み込み失敗
    WORKBOOK_SAVE_FAILED,// ワークブックの保存失敗
}
