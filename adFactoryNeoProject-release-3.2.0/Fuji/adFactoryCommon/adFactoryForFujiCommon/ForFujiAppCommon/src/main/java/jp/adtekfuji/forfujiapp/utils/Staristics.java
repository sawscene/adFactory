/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

/**
 * 統計計算処理クラス(TODO:バカよけはどうするの? 20160803)
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.22.Fri
 */
public class Staristics {

    //標準偏差・分散・偏差値・確率密度
    /**
     * 平均を計算する
     *
     * @param values 値
     * @return 平均値
     * @throws java.lang.Exception
     */
    public static double average(double[] values) throws Exception {

        double val = 0.0d;
        try {
            double sum = 0.0d;
            for (int i = 0; i < values.length; i++) {
                sum += values[i];
            }
            val = sum / (double) values.length;
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 累乗を計算する
     *
     * @param base 基数
     * @param exponet 指数
     * @return 累乗
     * @throws java.lang.Exception
     */
    public static double power(double base, double exponet) throws Exception {

        double val = 0.0d;
        try {
            val = Math.pow(base, exponet);
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 平方数を計算する
     *
     * @param value 値
     * @return 平方根
     * @throws java.lang.Exception
     */
    public static double squareNumber(double value) throws Exception {

        double val = 0.0d;
        try {
            val = Math.pow(value, 2.0d);
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 平方根を計算する
     *
     * @param value 値
     * @return 平方根
     * @throws java.lang.Exception
     */
    public static double squareRoot(double value) throws Exception {

        double val = 0.0d;
        try {
            val = Math.sqrt(value);
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 最大数を計算する
     *
     * @param values 配列
     * @return 配列内の最大値を出力
     * @throws java.lang.Exception
     */
    public static double maximum(double[] values) throws Exception {

        double val = 0.0d;
        try {
            for (int i = 0; i < values.length; i++) {
                val = Math.max(val, values[i]);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 最小値を計算する
     *
     * @param values 配列
     * @return 配列内の最小値を出力
     * @throws java.lang.Exception
     */
    public static double minimum(double[] values) throws Exception {

        double val = 0.0d;
        try {
            val = values[0];
            for (int i = 0; i < values.length; i++) {
                val = Math.min(val, values[i]);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 中央値を計算する
     *
     * @param values 配列
     * @return 配列内の中央値を出力
     * @throws java.lang.Exception
     */
    public static double medium(double[] values) throws Exception {

        double val = 0.0d;
        try {
            for (int i = 0; i < values.length - 1; i++) {
                for (int j = i + 1; j < values.length; j++) {
                    if (values[j] > values[i]) {
                        double temp = values[i];
                        values[i] = values[j];
                        values[j] = temp;
                    }
                }
            }

            if (values.length % 2 == 1) {
                // 要素数が奇数の場合は真ん中の値を返す
                val = values[values.length / 2];
            } else {
                // 要素数が偶数の場合は真ん中2つの値の平均値を返す
                val = (values[values.length / 2] + values[values.length / 2 - 1]) / 2.0;
            }
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 分散を計算する
     *
     * @param values
     * @return 分散
     * @throws java.lang.Exception
     */
    public static double variance(double[] values) throws Exception {
        double val = 0.0d;
        try {
            double sum = 0.0d;
            double ave = average(values);
            for (int i = 0; i < values.length; i++) {
                //平均との差を求める
                double diff = ave - values[i];
                sum += (squareNumber(diff));
            }
            val = sum / (double) values.length;
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 標準偏差を計算する
     *
     * @param values 値
     * @return 標準偏差
     * @throws java.lang.Exception
     */
    public static double standardDeviation(double[] values) throws Exception {

        double val = 0.0d;
        try {
            val = squareRoot(variance(values));
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 標準偏差を計算する
     *
     * @param variance 分散
     * @return 標準偏差
     * @throws java.lang.Exception
     */
    public static double standardDeviation(double variance) throws Exception {

        double val = 0.0d;
        try {
            val = squareRoot(variance);
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 偏差値を計算する
     *
     * @param target 偏差値を求めたい対象のデータ
     * @param values 偏差値を求めるためのデータ
     * @return 偏差値
     * @throws java.lang.Exception 計算エラー
     */
    public static double deviationValue(double target, double[] values) throws Exception {
        return deviationValue(target, average(values), standardDeviation(values));
    }

    /**
     * 偏差値を計算する
     *
     * @param target 偏差値を求めたい対象のデータ
     * @param average 平均
     * @param standerdDeviation 標準偏差
     * @return 偏差値
     * @throws java.lang.Exception 計算エラー
     */
    public static double deviationValue(double target, double average, double standerdDeviation) throws Exception {
        double val = 0;

        try {
            if (target == average) {
                val = 50.0;
            } else {
                val = (target - average) / standerdDeviation * 10.0d + 50.0d;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return val;
    }

    /**
     * 確率密度を計算する
     *
     * @param target 確率密度を求めたい対象のデータ
     * @param values 確率密度を求めるためのデータ
     * @return 確率密度
     * @throws java.lang.Exception
     */
    public static double probabilityDestiny(double target, double[] values) throws Exception {
        return probabilityDestiny(target, average(values), variance(values));
    }

    /**
     * 確率密度を計算する
     *
     * @param target 確率密度を求めたい対象のデータ
     * @param average 平均
     * @param variane 分散
     * @return 確率密度
     * @throws java.lang.Exception
     */
    public static double probabilityDestiny(double target, double average, double variane) throws Exception {

        double val = 0.0d;
        try {
            if (variane == 0.0d) {
                val = average == target ? 1.0d : 0.0d;
            } else {
                val = Math.exp(-0.5d * power(target - average, 2.0d) / variane) / squareRoot(2.0d * Math.PI * variane);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

    /**
     * 四捨五入する
     *
     * @param value 四捨五入する値
     * @return 四捨五入した値
     * @throws java.lang.Exception
     */
    public static double round(double value) throws Exception {

        double val = 0.0d;
        try {
            val = Math.round(value);
        } catch (Exception ex) {
            throw ex;
        }

        return val;
    }

}
