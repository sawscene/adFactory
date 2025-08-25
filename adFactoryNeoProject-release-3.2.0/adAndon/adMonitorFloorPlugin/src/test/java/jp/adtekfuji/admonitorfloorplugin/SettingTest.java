/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorfloorplugin;

import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author fu-kato
 */
public class SettingTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
    }

    /**
     * 進捗モニター設定ファイルにfontSizeMが存在しない場合64_0をフォントサイズとしてFontが作成されること
     * 
     * @throws IOException 
     */
    @Test
    public void test01() throws IOException {
        String filename = "adAndonApp_empty.properties";
        File propFile = new File(getClass().getClassLoader().getResource(filename).getFile());
        AdProperty.rebasePath(propFile.getParent());
        AdProperty.load(filename);

        Font font = Setting.getFont(100.0, 100.0, "a");

        assertThat(font.getSize(), is(64.0));
    }

    /**
     * 進捗モニター設定ファイルにfontSizeMが存在する場合その値をフォントサイズとしてFontが作成されること
     * 
     * @throws IOException 
     */
    @Test
    public void test02() throws IOException {
        String filename = "adAndonApp.properties";
        File propFile = new File(getClass().getClassLoader().getResource(filename).getFile());
        AdProperty.rebasePath(propFile.getParent());
        AdProperty.load(filename);

        Font font = Setting.getFont(100.0, 100.0, "a");

        assertThat(font.getSize(), is(32.0));
    }
}
