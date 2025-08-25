/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryactualdataoutput;

import adtekfuji.clientservice.ActualResultInfoFacade;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adfactoryactualdataoutput.entity.ColumnNameProperty;
import jp.adtekfuji.adfactoryactualdataoutput.entity.OutSettingProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class OutputActual {

    private final static Logger logger = LogManager.getLogger();
    private final static Integer MAX_LOAD_SIZE = 100;
    private final ActualResultInfoFacade actualResultInfoFacade = new ActualResultInfoFacade();

    public OutputActual() {
    }

    public void output(OutSettingProperty property) throws FileNotFoundException, IOException {
        //実績データ取得
        List<ActualResultEntity> actuals = getActualData(property);
        //実績データ書き込み
        String path = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + property.getFilePath();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), property.getFileEncode().getCharset()))) {
            //カラム書き込み.
            if (property.getFirstRowColumnCheck()) {
                writer.append(getColumn(property));
            }
            //データ書き込み.
            writer.append("");
            writer.flush();
        }
        //ローカルパス、共有フォルダ、FTPへの出力.
    }

    private List<ActualResultEntity> getActualData(OutSettingProperty property) {
        OutSettingProperty.TimeSettingEnum timeSetting = property.getTimeSetting();
        Date fromDate;
        Date toDate;
        switch (timeSetting) {
            case PAST_TIME:
                Calendar from = Calendar.getInstance();
                from.add(Calendar.HOUR, -property.getPastTime());
                Calendar to = Calendar.getInstance();
                fromDate = from.getTime();
                toDate = to.getTime();
                break;
            case RANGE_TIME:
                fromDate = property.getFromRangeTime();
                toDate = property.getToRangeTime();
                break;
            default:
                return new ArrayList<>();
        }

        ActualSearchCondition actualSearchCondition = new ActualSearchCondition()
                .fromDate(fromDate).toDate(toDate);//.statusList(selectStatusData);
        List<ActualResultEntity> actuals = new ArrayList<>();
        long actualMax = actualResultInfoFacade.searchCount(actualSearchCondition);
        for (long actualCnt = 0; actualCnt <= actualMax; actualCnt += MAX_LOAD_SIZE) {
            actuals.addAll(actualResultInfoFacade.searchRange(actualSearchCondition, actualCnt, actualCnt + MAX_LOAD_SIZE - 1));
        }
        return actuals;
    }

    private String getColumn(OutSettingProperty property) {
        StringBuilder sb = new StringBuilder();
        for (ColumnNameProperty column : property.getColumnNameList()) {
            if (property.getDoubleMarkCheck()) {
                sb.append('"');
            }
            String name = column.getOutColumnName().replaceAll("^\\(.\\)", "");
            sb.append(name);
            if (property.getDoubleMarkCheck()) {
                sb.append('"');
            }
        }
        return sb.toString();
    }

}
