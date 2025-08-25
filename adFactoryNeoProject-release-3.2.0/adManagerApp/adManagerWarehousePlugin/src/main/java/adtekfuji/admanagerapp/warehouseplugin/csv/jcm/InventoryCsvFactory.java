/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.csv.jcm;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author s-heya
 */
public class InventoryCsvFactory {
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter FORMAT_KEY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private File csvFile = null;
    private List<TrnMaterialInfo> materials;

    /**
     * コンストラクタ
     * 
     * @param materials
     */
    public InventoryCsvFactory(List<TrnMaterialInfo> materials) {
        this.materials = materials;
    }

    /**
     * CSVファイルを取得する。
     *
     * @return CSV出力ファイル
     */
    public File getCsvFile() {
        return this.csvFile;
    }

    /**
     * 資材情報一覧を設定する。
     * 
     * @param materials 資材情報一覧
     */
    public void setMaterials(List<TrnMaterialInfo> materials) {
        this.materials = materials;
    }

    /**
     * ファイルを選択する。
     *
     * @return ファイルを選択したか (true:選択した, false:キャンセルした)
     */
    public boolean choiceFile() {
        boolean result = false;
        try {
            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            String path = properties.getProperty(Constants.EXPORT_DIR);

            FileChooser fileChooser = new FileChooser();

            if (!org.apache.commons.lang3.StringUtils.isEmpty(path)) {
                File dir = new File(path);
                if (dir.exists()) {
                    fileChooser.setInitialDirectory(dir);
                }
            }

            fileChooser.setTitle(LocaleUtils.getString("key.OutReportTitle"));
            FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");
            fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);

            this.csvFile = fileChooser.showSaveDialog(SceneContiner.getInstance().getWindow());
            if (Objects.nonNull(this.csvFile)) {
                result = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * CSVファイルを出力する。
     * 
     * @throws Exception 
     */
    public void outputCsv() throws Exception {
        Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
        properties.setProperty(Constants.EXPORT_DIR, this.csvFile.getParent());

        String charset = AdProperty.getProperties().getProperty(Constants.EXPORT_CHARSET, Constants.EXPORT_CHARSET_DEF).toUpperCase();
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(charset)) {
            charset = "MS932";
        }

        Character separator = (FilenameUtils.getExtension(this.csvFile.getPath()).equals("tsv")) ? '\t' : ',';

        this.outputInventoryCsv(charset, separator);
    }
 
    /**
     * 在庫リストを出力する。
     * 
     * @param charset
     * @param separator
     * @throws Exception 
     */
    private void outputInventoryCsv(String charset, Character separator) throws Exception {
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            writer.write("\"Location\",\"Item Number\",\"Qty OH\",\"Lot\"\r\n");
        }

        if (Objects.nonNull(this.materials)) {
       
            List<InventoryCsv> sortrdlist = this.materials.stream()
                    .map(o -> new InventoryCsv(o))
                    .sorted(InventoryCsv.locationNoComparator
                            .thenComparing(InventoryCsv.partsNoComparator))
                    .collect(Collectors.toList());

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
                StatefulBeanToCsv<InventoryCsv> beanToCsv = new StatefulBeanToCsvBuilder<InventoryCsv>(writer)
                        .withSeparator(separator).withLineEnd("\r\n").build();
                beanToCsv.write(sortrdlist);
            }
        }
    }
}
