/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.common;

import adtekfuji.admanagerapp.warehouseplugin.entity.MaterialCsv;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 資材情報CSVファクトリー
 *
 * @author nar-nakamura
 */
public class MaterialCsvFactory {

    private final Logger logger = LogManager.getLogger();

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final List<TrnMaterialInfo> materialList;

    private File csvFile = null;

    /**
     * コンストラクタ
     *
     * @param materialList 資材情報一覧
     */
    public MaterialCsvFactory(List<TrnMaterialInfo> materialList) {
        this.materialList = materialList;
    }

    /**
     * CSV出力ファイルを取得する。
     *
     * @return CSV出力ファイル
     */
    public File getCsvFile() {
        return this.csvFile;
    }

    /**
     * CSV出力ファイルを設定する。
     *
     * @param csvFile CSV出力ファイル
     */
    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    /**
     * CSV出力するファイルを選択する。
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

            this.csvFile = fileChooser.showSaveDialog(sc.getWindow());
            if (Objects.nonNull(this.csvFile)) {
                result = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * CSVファイル出力する。
     */
    public void outputCsv() throws Exception {
        Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);

        properties.setProperty(Constants.EXPORT_DIR, this.csvFile.getParent());

        String inventoryLarge = LocaleUtils.getString("key.Inventory.Large");
        String inventorySmall = LocaleUtils.getString("key.Inventory.Small");

        List<MaterialCsv> list = new ArrayList<>();
        for (TrnMaterialInfo material : this.materialList) {
            list.add(new MaterialCsv(material, inventoryLarge, inventorySmall));
        }

        String charset = AdProperty.getProperties().getProperty(Constants.EXPORT_CHARSET, Constants.EXPORT_CHARSET_DEF).toUpperCase();
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(charset)) {
            charset = "MS932";
        }

        final Character separator = (FilenameUtils.getExtension(this.csvFile.getPath()).equals("tsv")) ? '\t' : ',';

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            String title = new StringBuilder()
                    .append(LocaleUtils.getString("key.ProductNo")).append(separator) // 品目
                    .append(LocaleUtils.getString("key.ProductName")).append(separator) // 品名
                    .append(LocaleUtils.getString("key.SupplyNo")).append(separator) // 発注番号
                    .append(LocaleUtils.getString("key.OrderNo")).append(separator) // 製造オーダー番号
                    .append(LocaleUtils.getString("key.ProductionNumber")).append(separator) // 製造番号
                    .append(LocaleUtils.getString("key.MaterialNo")).append(separator) // 資材番号
                    .append(LocaleUtils.getString("key.StockQuantity")).append(separator) // 在庫数
                    .append(LocaleUtils.getString("key.AreaName")).append(separator) // 区画名
                    .append(LocaleUtils.getString("key.LocationNo")).append(separator) // 棚番号
                    .append(LocaleUtils.getString("key.ArrivalNum")).append(separator) // 納入予定数
                    .append(LocaleUtils.getString("key.ArrivalDate")).append(separator) // 納入予定日
                    .append(LocaleUtils.getString("key.StockDate")).append(separator) // 最終入庫日
                    .append(LocaleUtils.getString("key.InventoryDiff")).append(separator) // 在庫過不足
                    .append(LocaleUtils.getString("key.InventoryNum")).append(separator) // 棚卸数
                    .append(LocaleUtils.getString("key.Inventory.Location")).append(separator) // 棚番訂正
                    .append(LocaleUtils.getString("key.InventoryDate")).append("\r\n") // 棚卸実施日
                    .toString();

            writer.write(title);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
            StatefulBeanToCsv<MaterialCsv> beanToCsv = new StatefulBeanToCsvBuilder<MaterialCsv>(writer).withSeparator(separator).withLineEnd("\r\n").build();
            beanToCsv.write(list);
        }
    }
}
