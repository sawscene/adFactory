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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author s-heya
 */
public class OperationCsvFactory {
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter FORMAT_KEY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private WarehouseEvent event = null;
    private File csvFile = null;
    private final List<LogStockInfo> logList;
    private List<TrnMaterialInfo> materials;

    /**
     * コンストラクタ
     * 
     * @param event
     * @param logList
     */
    public OperationCsvFactory(WarehouseEvent event, List<LogStockInfo> logList) {
        this.event = event;
        this.logList = logList;
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

        switch (this.event) {
            case RECIVE:
                this.outputStoreinCsv(charset, separator);
                break;
            case INSPECTION:
                this.outputInspectionCsv(charset, separator);
                break;
            case RECEIPT_PRODUCTION:
                this.outputProductCsv(charset, separator);
                break;
            case SHIPPING:
                this.outputShipmentCsv(charset, separator);
                break;
            default:
                break;
        }
    }
    
    /**
     * 入庫実績を出力する。
     * 
     * @param charset
     * @param separator
     * @throws Exception 
     */
    private void outputStoreinCsv(String charset, Character separator) throws Exception {
        
        List<StoreinCsv> outputlist = new ArrayList<>();
        
        Function<LogStockInfo, String> compositeKey = obj -> {
            StringBuilder sb = new StringBuilder();
            sb.append(obj.getEventDate().format(FORMAT_KEY)).append("-").append(obj.getSupplyNo());
            return sb.toString();
        };

        Map<String, List<LogStockInfo>> map = this.logList.stream().collect(Collectors.groupingBy(compositeKey));
        
        map.values().forEach(list -> {
            int qty = list.stream().collect(Collectors.summingInt(LogStockInfo::getEventNum));
            outputlist.add(new StoreinCsv(list.get(0), qty));
        });
        
        List<StoreinCsv> sortrdlist = outputlist.stream()
                .sorted(StoreinCsv.dateComparator
                        .thenComparing(StoreinCsv.supplyNoComparator))
                .collect(Collectors.toList());
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            writer.write("\"Purchase Order\",\"Po Ln\",\"Item Number\",\"Receipt Quantity\",\"Receipt Date\",\"Lot\"\r\n");
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
            StatefulBeanToCsv<StoreinCsv> beanToCsv = new StatefulBeanToCsvBuilder<StoreinCsv>(writer)
                    .withSeparator(separator).withLineEnd("\r\n").build();
            beanToCsv.write(sortrdlist);
        }
    }

    /**
     * 完成実績を出力する。
     * 
     * @param charset
     * @param separator
     * @throws Exception 
     */
    private void outputProductCsv(String charset, Character separator) throws Exception {
        
        List<ProductCsv> sortrdlist = this.logList.stream()
                .map(o -> new ProductCsv(o))
                .sorted(ProductCsv.dateComparator
                        .thenComparing(ProductCsv.productNoComparator))
                .collect(Collectors.toList());
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            writer.write("\"Item Number\",\"Completion date\",\"Quantity\",\"Serial Number\"\r\n");
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
            StatefulBeanToCsv<ProductCsv> beanToCsv = new StatefulBeanToCsvBuilder<ProductCsv>(writer)
                    .withSeparator(separator).withLineEnd("\r\n").build();
            beanToCsv.write(sortrdlist);
        }
    }
    
    /**
     * 入庫実績を出力する。
     * 
     * @param charset
     * @param separator
     * @throws Exception 
     */
    private void outputShipmentCsv(String charset, Character separator) throws Exception {
        
        List<ShipmentCsv> outputlist = new ArrayList<>();
        
        Function<LogStockInfo, String> compositeKey = obj -> {
            StringBuilder sb = new StringBuilder();
            sb.append(obj.getEventDate().format(FORMAT_KEY)).append("-").append(obj.getProductNo());
            return sb.toString();
        };

        Map<String, List<LogStockInfo>> map = this.logList.stream().collect(Collectors.groupingBy(compositeKey));
        
        map.values().forEach(list -> {
            LogStockInfo log = list.get(0);
            int qty = list.stream().collect(Collectors.summingInt(LogStockInfo::getEventNum));
            outputlist.add(new ShipmentCsv(log.getProductNo(), qty, log.getEventDate()));
        });
        
        List<ShipmentCsv> sortrdlist = outputlist.stream()
                .sorted(ShipmentCsv.dateComparator
                        .thenComparing(ShipmentCsv.productNoComparator))
                .collect(Collectors.toList());
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            writer.write("\"Item Number\",\"Quantity\",\"Pick day\"\r\n");
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
            StatefulBeanToCsv<ShipmentCsv> beanToCsv = new StatefulBeanToCsvBuilder<ShipmentCsv>(writer)
                    .withSeparator(separator).withLineEnd("\r\n").build();
            beanToCsv.write(sortrdlist);
        }
    }

    /**
     * 受入検査実績を出力する。
     * 
     * @param charset
     * @param separator
     * @throws Exception 
     */
    private void outputInspectionCsv(String charset, Character separator) throws Exception {
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            writer.write("\"Purchase Order\",\"Po Ln\",\"Item Number\",\"Receipt Quantity\",\"Receipt Date\",\"Lot\",\"Defective Quantity\"\r\n");
        }

        if (Objects.nonNull(this.materials)) {
       
            List<InspectionCsv> sortrdlist = this.materials.stream()
                    .map(o -> new InspectionCsv(o))
                    .sorted(InspectionCsv.dateComparator
                            .thenComparing(InspectionCsv.supplyNoComparator))
                    .collect(Collectors.toList());

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
                StatefulBeanToCsv<InspectionCsv> beanToCsv = new StatefulBeanToCsvBuilder<InspectionCsv>(writer)
                        .withSeparator(separator).withLineEnd("\r\n").build();
                beanToCsv.write(sortrdlist);
            }
        }
    }
}
