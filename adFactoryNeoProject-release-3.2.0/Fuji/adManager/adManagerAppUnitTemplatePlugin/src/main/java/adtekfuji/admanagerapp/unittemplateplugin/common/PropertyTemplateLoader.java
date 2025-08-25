/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template checkfile, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import adtekfuji.utility.IniFile;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplatePropertyInfoEntity;
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
    private static final String FILE_NAME_UNITTEMPLATE_PROPERTY = "\\UnitTemplatePropertyTemplate.properties";

    private static final String SECTION_PROP = "prop";
    private static final String KEY_NAME = "Name";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_VALUE = "Value";

    /**
     * テンプレートを読み取り工程のプロパティデータを生成して返す
     *
     * @return テンプレートのプロパティデータ
     */
    public static List<UnitTemplatePropertyInfoEntity> getUnitTemplateProperties() {
        List<UnitTemplatePropertyInfoEntity> properties = new ArrayList<>();
        try {
            // ファイルの存在チェック
            File checkfile = new File(CONF_HOME + FILE_NAME_UNITTEMPLATE_PROPERTY);
            if (!checkfile.exists()) {
                // ファイルがない場合新規作成
                createPropertyFile(checkfile);
            }

            IniFile file = new IniFile(CONF_HOME + FILE_NAME_UNITTEMPLATE_PROPERTY);
            for (int i = 1;; i++) {
                UnitTemplatePropertyInfoEntity property = new UnitTemplatePropertyInfoEntity();
                property.setUnitTemplatePropertyName(file.getString(SECTION_PROP + String.valueOf(i), KEY_NAME, ""));
                CustomPropertyTypeEnum[] enums = CustomPropertyTypeEnum.values();
                String tempType = file.getString(SECTION_PROP + String.valueOf(i), KEY_TYPE, "");
                for (CustomPropertyTypeEnum enum1 : enums) {
                    if (enum1.toString().equals(tempType)) {
                        property.setUnitTemplatePropertyType(CustomPropertyTypeEnum.valueOf(tempType));
                    }
                }
                property.setUnitTemplatePropertyValue(file.getString(SECTION_PROP + String.valueOf(i), KEY_VALUE, null));
                property.setUnitTemplatePropertyOrder(i);

                if (property.getUnitTemplatePropertyName().equals("") && Objects.isNull(property.getUnitTemplatePropertyType())) {
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

}
