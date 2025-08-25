package jp.adtekfuji.adinterfaceservice.websocket.form.output;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.FormFacade;
import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.form.FormInfoEntity;
import jp.adtekfuji.adFactory.entity.form.FormTagEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.excelreplacer.ExcelReplacer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.*;

/**
 * エクセルテンプレートの帳票の出力
 */
public class ExcelTemplateOutput {

    static private final Logger logger = LogManager.getLogger();
    static private final String TAG_WORD = "TAG_";
    static private final Pattern KANBAN_NO_PATTERN = Pattern.compile("^\\$[1-9][0-9]*\\.");

    /**
     * セル情報置換IF
     */
    interface Replacer {
        Pattern p1 = Pattern.compile("\\(.*\\)");

        Replacer apply(Cell cell);
    }

    /**
     * テキストセル情報の置き換え
     */
    static class TextReplacer implements  Replacer {
        final String text;
        TextReplacer(String text)
        {
            this.text = text;
        }
        @Override
        public Replacer apply(Cell cell) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(text);
            return this;
        }
    }

    /**
     * 日付情報の置き換え
     */
    static class DateReplacer implements Replacer {
        final Date date;
        DateReplacer(Date date) {
            this.date = date;
        }

        @Override
        public Replacer apply(Cell cell)
        {
            Matcher m1 = p1.matcher(cell.getStringCellValue());
            SimpleDateFormat sdf;
            if (m1.find()) {
                String format = m1.group();
                sdf = new SimpleDateFormat(format.substring(1, format.length() - 1));
            } else {
                sdf = new SimpleDateFormat();
            }

            Replacer newReplacer = new TextReplacer(sdf.format(date));
            newReplacer.apply(cell);
            return newReplacer;
        }
    }

    /**
     * 追加情報の置き換え
     */
    static class ActualAddtionReplacer implements Replacer {
        final Long actualAddtionId;
        final static ActualResultInfoFacade facade = new ActualResultInfoFacade();

        ActualAddtionReplacer(Long actualAddtionId) {
            this.actualAddtionId = actualAddtionId;
        }

        static String getImageType(byte[] data) {
            // 画像ファイル種別判定
            byte[] picHeader = new byte[8];
            System.arraycopy(data, 0, picHeader, 0, picHeader.length);
            String fileHeader = encodeHexString(picHeader);
            fileHeader = fileHeader.toUpperCase();

            if (fileHeader.equals("89504E470D0A1A0A")) {
                return "png";
            } else if (fileHeader.matches("^FFD8.*")) {
                return "jpg";
            } else if (fileHeader.matches("^474946383961.*") || fileHeader.matches("^474946383761.*")) {
                return "gif";
            } else if (fileHeader.matches("^424D.*")) {
                return "bmp";
            }
            return null;
        }

        @Override
        public Replacer apply(Cell cell) {
            byte [] data;
            try {
                data = facade.downloadFileData(actualAddtionId);
            } catch (Exception ex) {
                Replacer replacer = new DeleteTagReplacer();
                replacer.apply(cell);
                return replacer;
            }

            Sheet sheet = cell.getSheet();
            Workbook wb = sheet.getWorkbook();
            String imageType = getImageType(data);
            if (StringUtils.isEmpty(imageType)) {
                return null;
            }

            try {
                InputStream picImage = new ByteArrayInputStream(data);
                BufferedImage buffImage = ImageIO.read(picImage);
                ExcelReplacer.setPictureResize(wb, sheet, cell, buffImage, imageType, false, false);
                return this;
            } catch (IOException ex) {
                logger.fatal(ex, ex);
                return null;
            }
        }
    }

    /**
     * タグ削除
     */
    static class DeleteTagReplacer implements Replacer {
        @Override
        public Replacer apply(Cell cell) {
            cell.setCellValue("");
            return this;
        }
    }

    /**
     * エラータグ置換
     */
    static class ErrorTagReplacer implements Replacer {
        @Override
        public Replacer apply(Cell cell) {
            final String tagName = cell.getStringCellValue();
            String some = tagName.split("\\(")[0];

            // セルの値が通常タグかどうかチェックする。
            boolean isStandardTag = some.startsWith(TAG_WORD);

            // セルの値が通常タグでない場合、カンバンタグかどうかチェックする。
            if (!isStandardTag && KANBAN_NO_PATTERN.matcher(some).find()) {
                String kanbanTag = KANBAN_NO_PATTERN.matcher(some).replaceFirst("");
                isStandardTag = kanbanTag.startsWith(TAG_WORD);
            }

            // セルの値が標準タグの形式でない場合、スタイルは変更しない
            if (!isStandardTag) {
                return this;
            }

            // 置換できなかったタグの文字色・セルを赤く設定する
            Sheet sheet = cell.getSheet();
            Workbook wb = sheet.getWorkbook();

            // セルスタイルのコピー
            CellStyle style = wb.createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());

            // 背景色を薄い赤に設定する
            style.setFillForegroundColor(IndexedColors.ROSE.getIndex()); // 背景色：薄い赤
            style.setFillPattern(CellStyle.SOLID_FOREGROUND); // 塗り方：塗りつぶし

            // 文字色を赤に設定する
            Font font = wb.createFont();
            font.setFontName(wb.getFontAt(style.getFontIndex()).getFontName());
            font.setColor(IndexedColors.DARK_RED.getIndex()); // 文字色：暗い赤
            style.setFont(font);

            // 設定値を更新する
            cell.setCellStyle(style);
            return this;
        }
    }



    /**
     * 置換選択
     * @param tagData タグ情報
     * @return 置換オブジェクト
     */
    Replacer getReplacer(FormTagEntity.TagData tagData)
    {
        switch (tagData.getTagType()) {
            case TEXT:
                return Objects.isNull(tagData.getText()) ? new ErrorTagReplacer() : new TextReplacer(tagData.getText());
            case DATE:
                return Objects.isNull(tagData.getDate()) ? new ErrorTagReplacer() : new DateReplacer(tagData.getDate());
            case ADDITION:
                return Objects.isNull(tagData.getId()) ? new ErrorTagReplacer() : new ActualAddtionReplacer(tagData.getId());
            default:
                return new ErrorTagReplacer();
        }
    }


    final static FormFacade formFacade = new FormFacade();
    /**
     * 置換開始
     * @param formInfoEntity 置換情報
     * @return
     */
    public ResponseEntity execute(FormInfoEntity formInfoEntity)
    {
        final FormTagEntity formTagEntity = formFacade.getFormTag(formInfoEntity);
        if (Objects.isNull(formTagEntity)) {
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }

        final Map<String, Replacer> tagMap
                = formTagEntity
                .getTagMap()
                .entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> getReplacer(entry.getValue()),
                        (a, b) -> a, () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));


        File inputFile = new File(formInfoEntity.getFormPath());
        try (FileInputStream fis = new FileInputStream(inputFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)
        ) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                                || Objects.isNull(cell.getStringCellValue())) {
                            continue;
                        }

                        final String tagName = cell.getStringCellValue();
                        Replacer replacer
                                = tagMap.computeIfAbsent(
                                tagName,
                                key -> tagMap.getOrDefault(tagName.split("\\(")[0], new ErrorTagReplacer()));

                        Replacer newReplacer = replacer.apply(cell);
                        if (Objects.nonNull(newReplacer)) {
                            tagMap.put(tagName, newReplacer);
                        } else {
                            tagMap.put(tagName, new ErrorTagReplacer());
                        }
                    }
                }
            }

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            File outputFile = new File(System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + sf.format(new Date()));
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
                fos.flush();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
            }

            return formFacade.uploadForm(outputFile.getAbsolutePath());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }
}
