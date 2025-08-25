/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

import adtekfuji.plugin.PluginLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author e-mori
 */
public class KanbanRegistPreprocessContainer {

    private static KanbanRegistPreprocessContainer instance = null;
    private static final Logger logger = LogManager.getLogger();
    private final List<AdManagerAppKanbankanbanRegistPreprocessInterface> plugins = new ArrayList<>();

    /**
     * インスタンス生成
     *
     */
    private KanbanRegistPreprocessContainer() {
        try {
            //Pluginを読み込む,
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            plugins.clear();
            plugins.addAll(PluginLoader.load(AdManagerAppKanbankanbanRegistPreprocessInterface.class));
            logger.info("plugin:{}", plugins);
            plugins.stream().forEach((plugin) -> {
                plugin.pluginInitialize();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public static void createInstance() {
        if (Objects.isNull(instance)) {
            instance = new KanbanRegistPreprocessContainer();
        }
    }

    public static KanbanRegistPreprocessContainer getInstance() {
        if (Objects.isNull(instance)) {
            logger.fatal("not create instance");
        }
        return instance;
    }

    public void pluginDestructor() {
        plugins.stream().forEach((plugin) -> {
            plugin.pluginDestructor();
        });
    }

    public KanbanRegistPreprocessResultEntity kanbanRegistPreprocess(KanbanInfoEntity kanbanInfoEntity) {
        for (AdManagerAppKanbankanbanRegistPreprocessInterface plugin : plugins) {
            return plugin.kanbanRegistPreprocess(kanbanInfoEntity);
        }
        return new KanbanRegistPreprocessResultEntity(Boolean.TRUE, null);
    }

}
