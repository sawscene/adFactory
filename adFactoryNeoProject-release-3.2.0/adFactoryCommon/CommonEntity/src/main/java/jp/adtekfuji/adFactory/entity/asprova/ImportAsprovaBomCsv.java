/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.asprova;

import adtekfuji.utility.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Asprova製造BOMファイル インポート用データ
 *
 * @author (HN)y-harada
 */
public class ImportAsprovaBomCsv {
    private final Logger logger = LogManager.getLogger();
    private int rowNo;                 // 行数
    
    // Asprova製造Bomファイル情報　定数
    public final static String workTypeName = "種別";

    public enum WORK_TYPE {
        FD("FD","E"),  // 前段取り
        MW("","P"),    // 主作業
        BD("BD","S");  // 後段取り

        WORK_TYPE(String name, String actualType) {
            this.name =name;
            this.actualType = actualType;
        }
        final public String name;
        final public String actualType;

        public static WORK_TYPE toEnum(String name) {

            return Arrays
                    .stream(WORK_TYPE.values())
                    .filter(l -> StringUtils.equals(name, l.name))
                    .findFirst()
                    .orElse(null);
        }
    };

    private enum BOM_COL {
        ITEM_CODE("itemName",true),          // 品目(工程順)
        WORK_NUMBER("workNumber", true),      // 工程番号
        WORK_CODE("workCode", true),          // 工程コード
        WORK_SELECTOR("workSelector", false),  // 工程セレクタ
        SETUP_WORK_CODE("workCode", false),    // セットアップ工程コード
        WORK_TYPE("workType", false),          // 段取り
        PARALLEL_SETUP("parallelSetup", false),    // 並列段取り
        PARALLEL_WORK("parallelWork", false); // 並列工程

        BOM_COL(String name, boolean required) {
            this.name =name;
            this.required = required;
        }
        final public String name;
        final public boolean required;
    }

    final static public int ColumnNumber = 3;

    List<String> header;
    Map<BOM_COL, String> bomData = new HashMap<>();
    String workflowName = "";
    String workName = "";
    String workCode = "";
    WORK_TYPE workType = WORK_TYPE.MW;

    /**
     * Asprova製造BOMファイルのインポート用データ
     */
    public ImportAsprovaBomCsv() {
    }


    public String setValue(int rowNo, List<String> header, List<String> values) {

        this.rowNo = rowNo;
        this.header = header;

        bomData = Arrays
                .stream(BOM_COL.values())
                .filter(l->values.size()>l.ordinal())
                .collect(Collectors.toMap(Function.identity(), l -> values.get(l.ordinal())));

        // 必須項目が無い
        if(!Arrays.stream(BOM_COL.values())
                .filter(elem->elem.required)
                .allMatch(elem->bomData.containsKey(elem))) {
            logger.fatal("not Found Element");
            return String.format("  > 要素数が足りません [%d行目]", rowNo);
        }

        boolean setupWorkCodeEmpty = StringUtils.isEmpty(bomData.get(BOM_COL.SETUP_WORK_CODE));
        boolean workTypeEmpty = StringUtils.isEmpty(bomData.get(BOM_COL.WORK_TYPE));
        if ( !setupWorkCodeEmpty && workTypeEmpty ) {
            logger.fatal("Element error");
            return String.format("  > %sを指定して下さい。 [%d行目]", header.get(BOM_COL.SETUP_WORK_CODE.ordinal()+1), rowNo);
        }

        // 工程順名
        final String workSelector = bomData.get(BOM_COL.WORK_SELECTOR);
        workflowName =
                this.bomData.get(BOM_COL.ITEM_CODE)
                        + (StringUtils.isEmpty(workSelector) ? "" : ":" + workSelector);

        // 工程名
        workName = bomData.get(BOM_COL.WORK_NUMBER) + "_" + bomData.get(BOM_COL.WORK_CODE);

        // 工程コード
        workCode = bomData.get(BOM_COL.WORK_CODE);
        return "";

    }

    /**
     * データのチェックを実施
     * @return
     */
    public List<String> checkData()
    {
        List<String> ret = new ArrayList<>();
        if (WORK_TYPE.MW.equals(workType)) {
            Arrays.stream(BOM_COL.values())
                    .filter(elem -> elem.required)
                    .filter(elem -> StringUtils.isEmpty(this.bomData.get(elem)))
                    .forEach(elem -> {
                        String category = header.size() > elem.ordinal() ? header.get(elem.ordinal()) : elem.name;
                        ret.add(String.format("   > %sが空白です。取込ファイルを確認してください。 [%d行目]", category, this.getRowNo()));
                    });

        } else {
            if (StringUtils.isEmpty(workCode)) {
                String category = header.size() > BOM_COL.SETUP_WORK_CODE.ordinal() ? header.get(BOM_COL.SETUP_WORK_CODE.ordinal()) : BOM_COL.SETUP_WORK_CODE.name;
                ret.add(String.format("   > %sが空白です。取込ファイルを確認してください。 [%d行目]", category, this.getRowNo()));
            }
        }

        return ret;
    }


    /**
     * 品目（工程順名）を取得する。
     *
     * @return 品目（工程順名）
     */
    public String getItemCode() {
        return bomData.get(BOM_COL.ITEM_CODE);
    }

    /**
     * ファイル入力時のワークコードを返す
     * @return ファイル入力時のワークコード
     */
    public String getBaseWorkCode() { return bomData.get(BOM_COL.WORK_CODE); }

    /**
     * 工程番号を取得する。
     *
     * @return 工程番号
     */
    public String getWorkNumber() {
        return bomData.get(BOM_COL.WORK_NUMBER);
    }

    /**
     * 並列工程を取得
     * @return
     */
    public List<String> getParallelSetupWorkName() {
        if(StringUtils.isEmpty(bomData.get(BOM_COL.PARALLEL_WORK))) {
            return new ArrayList<>();
        }
        return Arrays.asList(bomData.get(BOM_COL.PARALLEL_WORK).split("\\s*,\\s*"));
    }

    /**
     * 並列工程の名称を設定します。
     * @param parallelSetupWorkNames 並列工程の名称のリスト
     */
    public void setParallelSetupWorkName(List<String> parallelSetupWorkNames) {
        if (Objects.nonNull(parallelSetupWorkNames) && !parallelSetupWorkNames.isEmpty()) {
            bomData.put(BOM_COL.PARALLEL_WORK, String.join(",", parallelSetupWorkNames));
        }
    }


    /**
     * 工程コードを取得する。
     *
     * @return 工程コード
     */
    public String getWorkCode() {
        return workCode;
    }

    /**
     * 工程セレクタ名を取得
     * @return
     */
    public String getWorkSelector() {
        return this.bomData.get(BOM_COL.WORK_SELECTOR);
    }


    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return workName;
    }

    /**
     * 工程順を取得
     * @return 工程順名
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * 工程種を返す
     * @return 工程種
     */
    public WORK_TYPE getWorkType() {
        return workType;
    }

    /**
     * 読み取り行数を取得する。
     *
     * @return 読み取り行数
     */
    public int getRowNo() {
        return this.rowNo;
    }

    /**
     *  前段取り工程を作成
     */
    static final Pattern patternFD = Pattern.compile(WORK_TYPE.FD.name);
    public Optional<ImportAsprovaBomCsv> createPreSetupBom()
    {
        final String workType = bomData.get(BOM_COL.WORK_TYPE);
        final String setupWorkCode = bomData.get(BOM_COL.SETUP_WORK_CODE);

        if (StringUtils.isEmpty(workType)
                && StringUtils.isEmpty(setupWorkCode)) {
            return Optional.empty();
        }

        if (!patternFD.matcher(workType).find()) {
            return Optional.empty();
        }

        ImportAsprovaBomCsv ret = new ImportAsprovaBomCsv();
        ret.header = this.header;
        ret.rowNo = this.rowNo;
        ret.workflowName = this.workflowName;
        ret.workType = WORK_TYPE.FD;
        ret.workName = this.workName + "_" + ret.workType.name;
        ret.workCode = setupWorkCode;

        ret.bomData.clear();
        ret.bomData.putAll(this.bomData);
        ret.bomData.put(BOM_COL.PARALLEL_WORK, this.bomData.get(BOM_COL.PARALLEL_SETUP));
        return Optional.of(ret);
    }

    /**
     * 後段取り工程を作成
     */
    static final Pattern patternBD = Pattern.compile(WORK_TYPE.BD.name);
    public Optional<ImportAsprovaBomCsv> createPostSetupBom()
    {
        final String workType = bomData.get(BOM_COL.WORK_TYPE);
        final String setupWorkCode = bomData.get(BOM_COL.SETUP_WORK_CODE);

        if (StringUtils.isEmpty(workType)
                && StringUtils.isEmpty(setupWorkCode)) {
            return Optional.empty();
        }

        if (!patternBD.matcher(workType).find()) {
            return Optional.empty();
        }

        ImportAsprovaBomCsv ret = new ImportAsprovaBomCsv();
        ret.header = this.header;
        ret.rowNo = this.rowNo;
        ret.workflowName = this.workflowName;
        ret.workType = WORK_TYPE.BD;
        ret.workName = this.workName + "_" + ret.workType.name;
        ret.workCode = setupWorkCode;

        ret.bomData.clear();
        ret.bomData.putAll(this.bomData);
        return Optional.of(ret);
    }


    @Override
    public String toString() {
        return "AsprovaBomFormatInfo{"+
                "workflowName=" + workflowName +
                "worktype=" + workType +
                "workname=" + workName +
                "workCOde=" + workCode +
        Stream.of(BOM_COL.values())
                .map(l->l.name+"="+this.bomData.get(l))
                .collect(Collectors.joining(","))
            +"}";
    }
}
