/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkfpinspection;

import java.util.List;

/**
 * 検査コマンド情報
 *
 * @author sh-hirano
 */
public class InspectionCommand {

    public enum FpCommandEnum {
        /**
         * 検査開始
         */
        INSPECTION_START(1),
        /**
         * 検査完了
         */
        INSPECTION_RESULT(2);

        private final int intValue; // int値

        /**
         * コンストラクタ
         *
         * @param intValue int値
         */
        private FpCommandEnum(int intValue) {
            this.intValue = intValue;
        }

        /**
         * int値を取得する。
         *
         * @return int値
         */
        public int getIntValue() {
            return this.intValue;
        }

        /**
         * int値に対応したFPコマンドを取得する。
         *
         * @param intValue int値
         * @return FPコマンド
         */
        public static FpCommandEnum valueOf(int intValue) {
            FpCommandEnum result = null;
            for (FpCommandEnum value : FpCommandEnum.values()) {
                if (value.getIntValue() == intValue) {
                    result = value;
                    break;
                }
            }
            return result;
        }
    }

    /**
     * 検査エラー
     */
    public enum InspectionErrorEnum {
        FILE_WRITE_ERROR,
        TIMEOUT_ERROR;
    }

    private FpCommandEnum command; // コマンド
    private String inspection; // 検査コマンド
    private String result; // 検査結果
    private List<String> datas; // データ

    private InspectionErrorEnum inspectionError = null; // adProduct側の検査エラー

    /**
     * コンストラクタ
     */
    public InspectionCommand() {
    }

    /**
     * コマンドを取得する。
     *
     * @return コマンド
     */
    public FpCommandEnum getCommand() {
        return this.command;
    }

    /**
     * コマンドを設定する。
     *
     * @param command コマンド
     */
    public void setCommand(FpCommandEnum command) {
        this.command = command;
    }

    /**
     * 検査コマンドを取得する。
     *
     * @return 検査コマンド
     */
    public String getInspection() {
        return this.inspection;
    }

    /**
     * 検査コマンドを設定する。
     *
     * @param inspection 検査コマンド
     */
    public void setInspection(String inspection) {
        this.inspection = inspection;
    }

    /**
     * 検査結果を取得する。
     *
     * @return 検査結果
     */
    public String getResult() {
        return this.result;
    }

    /**
     * 検査結果を設定する。
     *
     * @param result 検査結果
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * データを取得する。
     *
     * @return データ
     */
    public List<String> getDatas() {
        return datas;
    }

    /**
     * データを設定する。
     *
     * @param datas データ
     */
    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    /**
     * adProduct側の検査エラーを取得する。
     *
     * @return adProduct側の検査エラー
     */
    public InspectionErrorEnum getInspectionError() {
        return this.inspectionError;
    }

    /**
     * adProduct側の検査エラーを設定する。
     *
     * @param inspectionError adProduct側の検査エラー
     */
    public void setInspectionError(InspectionErrorEnum inspectionError) {
        this.inspectionError = inspectionError;
    }

    @Override
    public String toString() {
        return "InspectionCommand{"
                + "command=" + this.command
                + ", inspection=" + this.inspection
                + ", result=" + this.result
                + ", datas=" + this.datas
                + ", inspectionError=" + this.inspectionError
                + "}";
    }
}
