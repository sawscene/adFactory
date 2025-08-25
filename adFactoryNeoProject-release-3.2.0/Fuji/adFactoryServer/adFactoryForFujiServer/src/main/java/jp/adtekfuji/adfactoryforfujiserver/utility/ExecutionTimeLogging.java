/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.utility;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.interceptor.InterceptorBinding;

/**
 * アノテーション使用対象メソッドの実行時間をログとして 記録します。
 *
 * <p>
 * 使用上の注意 本アノテーションを使用する際は、下記の設定を行ってください。 1. 使用するプロジェクトにbeans.xmlを作成する アノテーションをメソッドに付加した際、警告が表示されます。
 * 指示に従ってbeans.xmlを作成していただければ結構です。 2. 作成したbeans.xmlに注入対象となるクラスを記述する。 本機能が採用している@interseptorはインターセプトするクラスを指定する
 * 必要があります。下記のメンバをbeans.xmlに追加してください。
 *
 * <p>
 * ＜interceptors＞ ＜class＞adtekfuji.utility.LogInterceptor＜/class＞ ＜/interceptors＞
 *
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 * @see LogInterceptor
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface ExecutionTimeLogging {
}
