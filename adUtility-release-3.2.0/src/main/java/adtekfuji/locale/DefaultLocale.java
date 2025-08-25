/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.locale;

import java.util.Locale;

/**
 * 既定の言語
 *
 * @author ke.yokoi
 */
public class DefaultLocale implements ILocale {

    @Override
    public String getDisplayName() {
        return "English";
    }

    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

}
