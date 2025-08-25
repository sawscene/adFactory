package jp.adtekfuji.guitesttool;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.mainapp.LocalePluginInterface;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());
        sp.setAppTitle("adFactory GuiTestTool");
        sp.addCssPath("/styles/colorStyles.css");
        sp.addCssPath("/styles/designStyles.css");
        sp.addCssPath("/styles/fontStyles.css");
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("MainScene");
    }

    public static void main(String[] args) throws IOException {
        //言語ファイルプラグイン読み込み.
        PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
        PluginLoader.load(LocalePluginInterface.class);
        //プロパティファイル読み込み.
        AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
        AdProperty.load("adFactoryGuiTest.properties");
        //アンドンモニタプラグイン読み込み.
        PluginLoader.load(AdAndonComponentInterface.class);
        //起動.
        launch(args);
        //後始末.
        AdProperty.store();
    }

}
