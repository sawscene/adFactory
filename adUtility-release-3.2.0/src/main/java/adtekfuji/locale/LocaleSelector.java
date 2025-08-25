/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.locale;

import adtekfuji.plugin.PluginLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 言語切替、言語プラグイン 利用方法： ・src/main/resources に META-INF.services パッケージを作成し ・adtekfuji.locale.ILocale
 * ファイルを作成 ・ファイル内にプラグインの名前を記述する（例 adtekfuji.locale.LocaleJP）
 *
 * @author ke.yokoi
 */
public class LocaleSelector {

    private static LocaleSelector instance;
    private final List<ILocale> locales = new ArrayList<>();
    private ILocale selectedLocale;

    private LocaleSelector() {
        //既定の言語.
        locales.add(new DefaultLocale());
        //プラグインを読み込む.
        locales.addAll(PluginLoader.load(ILocale.class));
        selectedLocale = locales.get(0);
        Locale.setDefault(selectedLocale.getLocale());
    }

    public static void createInstance() {
        if (Objects.isNull(instance)) {
            instance = new LocaleSelector();
        }
    }

    public static LocaleSelector getInstance() {
        return instance;
    }

    /**
     * 選択できる言語の一覧を取得.
     *
     * @return 言語の一覧
     */
    public List<String> getSelectableNames() {
        List<String> list = new ArrayList<>();
        locales.stream().forEach((locale) -> {
            list.add(locale.getDisplayName());
        });
        return list;
    }

    /**
     * 選択中の言語を取得.
     *
     * @return 選択中の言語
     */
    public String selectedLocale() {
        return selectedLocale.getDisplayName();
    }

    /**
     * 選択した言語を設定.
     *
     * @param name 新しく選択した言語
     */
    public void setLocale(final String name) {
        locales.stream().filter((locale) -> (locale.getDisplayName().equals(name))).forEach((locale) -> {
            Locale.setDefault(locale.getLocale());
            selectedLocale = locale;
        });
    }

}
