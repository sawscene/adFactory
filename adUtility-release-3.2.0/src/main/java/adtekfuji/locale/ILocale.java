/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.locale;

import java.util.Locale;

/**
 * 言語切替プラグインのインターフェース
 *
 * @author ke.yokoi
 */
public interface ILocale {

    public String getDisplayName();

    public Locale getLocale();
}
