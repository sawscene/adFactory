/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.common;

import adtekfuji.admanagerapp.warehouseplugin.entity.LotTraceCsv;
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
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.warehouse.TrnLotTraceInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class LotTraceCsvFactory {

    private final Logger logger = LogManager.getLogger();

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final List<TrnLotTraceInfo> lotTraceList;

    private File csvFile = null;

    /**
     * コンストラクタ
     *
     * @param lotTraceList ロットトレース情報一覧
     */
    public LotTraceCsvFactory(List<TrnLotTraceInfo> lotTraceList) {
        this.lotTraceList = lotTraceList.stream()
                .sorted(LotTraceComparators.lotTraceFigureNoComparator
                        .thenComparing(LotTraceComparators.lotTraceProductNoComparator)
                        .thenComparing(LotTraceComparators.lotTraceOrderNoComparator)
                        .thenComparing(LotTraceComparators.lotTraceLotNoComparator)
                        .thenComparing(LotTraceComparators.lotTraceDeliveryNoComparator)
                        .thenComparing(LotTraceComparators.lotTraceItemNoComparator)
                        .thenComparing(LotTraceComparators.lotTraceMaterialNoComparator)
                        .thenComparing(LotTraceComparators.lotTraceKanbanNameComparator)
                        .thenComparing(LotTraceComparators.lotTraceWorkNameComparator)
                        .thenComparing(LotTraceComparators.lotTracePersonNoComparator)
                        .thenComparing(LotTraceComparators.lotTracePersonNameComparator)
                        .thenComparing(LotTraceComparators.lotTraceAssemblyDatetimeComparator))
                .collect(Collectors.toList());
    }

    /**
     * CSV出力ファイルを取得する。
     *
     * @return 
     */
    public File getCsvFile() {
        return this.csvFile;
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

            if (Objects.isNull(fileChooser.getInitialDirectory())) {
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
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

        List<LotTraceCsv> list = new ArrayList<>();
        for (TrnLotTraceInfo lotTrace : this.lotTraceList) {
            list.add(new LotTraceCsv(lotTrace));
        }

        String charset = AdProperty.getProperties().getProperty(Constants.EXPORT_CHARSET, Constants.EXPORT_CHARSET_DEF).toUpperCase();
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(charset)) {
            charset = "MS932";
        }

        final Character separator = (FilenameUtils.getExtension(this.csvFile.getPath()).equals("tsv")) ? '\t' : ',';

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile), charset))) {
            StringBuilder title = new StringBuilder();
            title.append("Figure No").append(separator);
            title.append("Product No").append(separator);
            title.append("Product Name").append(separator);
            title.append("Order No").append(separator);
            title.append("Lot No").append(separator);
            title.append("Delivery No").append(separator);
            title.append("Item No").append(separator);
            title.append("Material No").append(separator);
            title.append("Kanban Name").append(separator);
            title.append("Model Name").append(separator);
            title.append("Work Name").append(separator);
            title.append("Person No").append(separator);
            title.append("Person Name").append(separator);
            title.append("Assembly Date").append("\r\n");

            writer.write(title.toString());
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFile, true), charset))) {
            StatefulBeanToCsv<LotTraceCsv> beanToCsv = new StatefulBeanToCsvBuilder<LotTraceCsv>(writer).withSeparator(separator).withLineEnd("\r\n").build();
            beanToCsv.write(list);
        }
    }
}
