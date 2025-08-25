/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template checkfile, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import adtekfuji.utility.IniFile;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * プロパティテンプレート読み込みクラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.09.2.Fri
 */
public class PropertyTemplateLoader {

    private static final Logger logger = LogManager.getLogger();

    private static final String CONF_HOME = System.getenv("ADFACTORY_HOME") + "\\conf";
    private static final String FILE_NAME_WORK_PROPERTY = "\\WorkPropertyTemplate.properties";
    private static final String FILE_NAME_WORKFLOW_PROPERTY = "\\WorkflowPropertyTemplate.properties";

    private static final String SECTION_PROP = "prop";
    private static final String KEY_NAME = "Name";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_VALUE = "Value";
    private static final String KEY_REGEX = "Regex";

    /**
     * テンプレートを読み取り工程のプロパティデータを生成して返す
     *
     * @return テンプレートのプロパティデータ
     */
    public static List<WorkPropertyInfoEntity> getWorkProperties() {
        List<WorkPropertyInfoEntity> properties = new ArrayList<>();
        try {
            // ファイルの存在チェック
            File checkfile = new File(CONF_HOME + FILE_NAME_WORK_PROPERTY);
            if (!checkfile.exists()) {
                // ファイルがない場合新規作成
                createPropertyFile(checkfile);
            }

            IniFile file = new IniFile(CONF_HOME + FILE_NAME_WORK_PROPERTY);
            for (int i = 1;; i++) {
                WorkPropertyInfoEntity property = new WorkPropertyInfoEntity();
                String name = Objects.isNull(file.getString(SECTION_PROP + String.valueOf(i), KEY_NAME, ""))
                        ? "" : file.getString(SECTION_PROP + String.valueOf(i), KEY_NAME, "");
                property.setWorkPropName(name);
                CustomPropertyTypeEnum[] enums = CustomPropertyTypeEnum.values();
                String tempType = file.getString(SECTION_PROP + String.valueOf(i), KEY_TYPE, "");
                for (CustomPropertyTypeEnum enum1 : enums) {
                    if (enum1.toString().equals(tempType)) {
                        property.setWorkPropType(CustomPropertyTypeEnum.valueOf(tempType));
                    }
                }
                property.setWorkPropValue(file.getString(SECTION_PROP + String.valueOf(i), KEY_VALUE, null));
                property.setWorkPropOrder(i);

                if (property.getWorkPropName().equals("") && Objects.isNull(property.getWorkPropType())) {
                    break;
                }
                properties.add(property);
            }
        } catch (IOException ex) {
            //テンプレートファイル読み込みエラー
            logger.fatal(ex, ex);
        }
        return properties;
    }

    /**
     * テンプレートを読み取り工程順のプロパティデータを生成して返す
     *
     * @return テンプレートのプロパティデータ
     */
    public static List<KanbanPropertyTemplateInfoEntity> getWorkflowProperties() {
        List<KanbanPropertyTemplateInfoEntity> properties = new ArrayList<>();
        try {
            // ファイルの存在チェック
            File checkfile = new File(CONF_HOME + FILE_NAME_WORKFLOW_PROPERTY);
            if (!checkfile.exists()) {
                // ファイルがない場合新規作成
                createPropertyFile(checkfile);
            }

            IniFile file = new IniFile(CONF_HOME + FILE_NAME_WORKFLOW_PROPERTY);
            for (int i = 1;; i++) {
                KanbanPropertyTemplateInfoEntity property = new KanbanPropertyTemplateInfoEntity();
                String name = Objects.isNull(file.getString(SECTION_PROP + String.valueOf(i), KEY_NAME, ""))
                        ? "" : file.getString(SECTION_PROP + String.valueOf(i), KEY_NAME, "");
                property.setKanbanPropName(name);
                CustomPropertyTypeEnum[] enums = CustomPropertyTypeEnum.values();
                String tempType = file.getString(SECTION_PROP + String.valueOf(i), KEY_TYPE, "");
                for (CustomPropertyTypeEnum enum1 : enums) {
                    if (enum1.toString().equals(tempType)) {
                        property.setKanbanPropType(CustomPropertyTypeEnum.valueOf(tempType));
                    }
                }
                property.setKanbanPropInitialValue(file.getString(SECTION_PROP + String.valueOf(i), KEY_VALUE, null));
                property.setKanbanPropOrder(i);

                if (property.getKanbanPropName().equals("") && Objects.isNull(property.getKanbanPropType())) {
                    break;
                }
                properties.add(property);
            }
        } catch (IOException ex) {
            //テンプレートファイル読み込みエラー
            logger.fatal(ex, ex);
        }
        return properties;
    }

    /**
     * プロパティテンプレートファイルを作成する
     *
     * @param file 作成するファイル情報
     * @param contents 書き込む内容
     */
    private static void createPropertyFile(File file) throws IOException {
        if (file.createNewFile()) {
            logger.info("Created the file:{}", file);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Shift_JIS"))) {
                bw.write("[" + SECTION_PROP + "1]");
                bw.newLine();
                bw.write(KEY_NAME + "=");
                bw.newLine();
                bw.write(KEY_TYPE + "=");
                bw.newLine();
                bw.write(KEY_VALUE + "=");
                bw.newLine();
            }

        } else {
            logger.info("Failed to create the file:{}", file);
        }
    }

    /**
     * 工程設定 追加情報テンプレートの正規表現を取得
     *
     * @return
     */
    public static Map<String, String> getWorkTemplateRegexTexts() {
        Map<String, String> regexTexts = new HashMap<>();
        try {
            // ファイルの存在チェック
            File checkfile = new File(CONF_HOME + FILE_NAME_WORK_PROPERTY);
            if (!checkfile.exists()) {
                // ファイルがない場合新規作成
                createPropertyFile(checkfile);
            }

            IniFile file = new IniFile(CONF_HOME + FILE_NAME_WORK_PROPERTY);
            for (int i = 1;; i++) {
                String section = SECTION_PROP + String.valueOf(i);

                String name = file.getString(section, KEY_NAME, null);
                String type = file.getString(section, KEY_TYPE, null);
                if (Objects.isNull(name) && Objects.isNull(type)) {
                    break;
                }

                String regex = file.getString(section, KEY_REGEX, null);
                if (Objects.nonNull(name) && !name.isEmpty()
                        && Objects.nonNull(regex) && !regex.isEmpty()) {
                    regexTexts.put(name, regex);
                }
            }
        } catch (IOException ex) {
            //テンプレートファイル読み込みエラー
            logger.fatal(ex, ex);
        }
        return regexTexts;
    }

    /**
     * 工程順設定 追加情報テンプレートの正規表現を取得
     *
     * @return
     */
    public static Map<String, String> getWorkflowTemplateRegexText() {
        Map<String, String> regexTexts = new HashMap<>();
        try {
            // ファイルの存在チェック
            File checkfile = new File(CONF_HOME + FILE_NAME_WORKFLOW_PROPERTY);
            if (!checkfile.exists()) {
                // ファイルがない場合新規作成
                createPropertyFile(checkfile);
            }

            IniFile file = new IniFile(CONF_HOME + FILE_NAME_WORKFLOW_PROPERTY);
            for (int i = 1;; i++) {
                String section = SECTION_PROP + String.valueOf(i);

                String name = file.getString(section, KEY_NAME, null);
                String type = file.getString(section, KEY_TYPE, null);
                if (Objects.isNull(name) && Objects.isNull(type)) {
                    break;
                }

                String regex = file.getString(section, KEY_REGEX, null);
                if (Objects.nonNull(name) && !name.isEmpty()
                        && Objects.nonNull(regex) && !regex.isEmpty()) {
                    regexTexts.put(name, regex);
                }
            }
        } catch (IOException ex) {
            //テンプレートファイル読み込みエラー
            logger.fatal(ex, ex);
        }
        return regexTexts;
    }
}
