/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.utility;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.apache.logging.log4j.LogManager;

/**
 * メソッド実行時間計測クラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 * @see ExecutionTimeLogging
 */
@ExecutionTimeLogging
@Interceptor
public class LogInterceptor {

    @AroundInvoke
    public Object log(InvocationContext ic) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(ic.getMethod().getDeclaringClass().getName());
        sb.append(".");
        sb.append(ic.getMethod().getName());

        //ここでメソッドを実行
        Long startTime = System.currentTimeMillis();
        Object returnObject = ic.proceed();
        Long stopTime = System.currentTimeMillis();

        //処理時間を記録
        LogManager.getLogger(sb.toString()).info("processing time {}[ms] ", (stopTime - startTime));

        return returnObject;
    }

}
