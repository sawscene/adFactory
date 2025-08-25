/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

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
 * @see LogInterceptor
 * @author e-mori
 * @since　2015/03/27
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ExecutionTimeLogging {
}
