/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.asprova;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Asprova生産計画ファイル インポート用データ
 *
 * @author (HN)y-harada
 */
public class ImportAsprovaPlanCsv {
    private final Logger logger = LogManager.getLogger();

    private int rowNo;
    private String kanbanName;           // カンバン名
    private String workName;             // 工程名
    private String workflowName;         // 工程順名
    private ImportAsprovaBomCsv.WORK_TYPE workType;
    private boolean skipFlag = false;

    // Asprova生産計画ファイル情報　定数
    public static final String NOT_SPLIT_TYPE = "S";           // 分割前
    public static final String COMPLETE_WORK_STATUS = "B";      // 完了ステータス
    public static final String NO_PLANED_STATUS = "N"; // 未計画
    public static final String PLANNING_STATUS = "A"; // 計画済み
    public static final String PLANED_STATUS = "ID"; // 指示済
    public static final String WORKING_STATUS = "T"; // 着手済み


    // Asprova生産計画ファイル情報　カラム番号
    public enum ASP_COL {
        CODE(true),                    // コード
        SPLIT_NO(false),               // 分割番号
        ITEM_NAME(false),              // 品名
        ITEM_CODE(true),               // 品目
        NICK_NAME(false),              // ニックネーム
        LOT_COUNT(true),               // 製造数量
        WORK_CODE(true),               // 工程コード
        MAIN_RESOUCE(false),            // 主資源
        SUB_RESOUCE(false),            // 副資源
        TACK_TIME(false),              // タクトタイム
        WORK_START_TIME(false),        // 工程開始日時
        WORK_END_TIME(false),          // 工程終了日時
        MACHINE_CODE(false),            // 機械番号
        WORK_CENTER(false),            // ワークセンタ
        DELIVARY_DATE(false),          // 納期
        ORDER_NUMBER(false),           // 受注番号
        DELIVERY_DESTINATION(false),   // 納入先
        //        DUMMY_FILD(false),             // ダミーフィールド
        MATERIAL(false),               // 材質
        SIZE_DATA(false),              // 寸法
        ORDER(true),                   // オーダ
        SERIAL_NUMBER(false),          // シリアル
        WORK_NUMBER(true),             // 工程番号
        ASP_SERIAL(false),             // Asprovaシリアル(分割番号)
        TYPE(false),                   // 種別
        WORK_STATUS(false),            // ステータス
        MODEL_NAME(false),             // モデル名
        WORK_SELECTOR(false),          // 工程セレクタ
        //        DUMMY_FILD2(false),            // ダミーフィールド
        PRE_SETUP_START_TIME(false),   // 前段取り開始時間
        PRE_SETUP_END_TIME(false),     // 前段取り終了時間
        PRE_SETUP_TACK_TIME(false),    // 前工程のタクトタイム
        POST_SETUP_START_TIME(false),  // 後段取り開始時間
        POST_SETUP_END_TIME(false),    // 後段取り終了時間
        POST_SETUP_TACK_TIME(false),   // 後工程のタクトタイム
        SETUP_RESOURCE(false),
        //        PRE_SETUP_RESOURCE(false),     // 前段取り資源
//        POST_SETUP_RESOURCE(false);    // 後前段取り資源
        PROGRAM_NUMBER(false),    // プログラム番号
        ASSERT_NUMBER(false),    // 資産番号
        SEGMENT(false), // セグメント
        ORDER_SUM_NUMBER(false), // オーダ総数
        ALTERNATIVE_SETUP(false), // 代替段取り
        ALTERNATIVE_WORK(false); // 代替工程

        ASP_COL(boolean required) {
            this.required = required;
        }

        final public boolean required;
    }

    static final public int columnNum = 22;

    // Asprova生産計画ファイル情報　文字列
    public static final String NICKNAME = "ニックネーム";
    public static final String ITEM_NAME = "品名";
    public static final String DELIVARY_DATE = "納期";
    public static final String ORDER_CODE = "製造オーダ";
    public static final String SERIAL_NUMBER = "製造オーダシリアル";
    public static final String ITEM_CODE = "品目";
    public static final String LOT_QUANTITY = "数量";
    public static final String ORDER_NUMBER = "受注番号";
    public static final String DELIVERY_DESTINATION = "ユーザー";
    public static final String MATERIAL = "材質";
    public static final String SIZE_DATA = "寸法";
    public static final String LINK_ASP_FLG = "Asprova連携フラグ";
    public static final String WORK_NUMBER = "工程番号";
    public static final String MACHINE_CODE = "機械番号";
    public static final String WORK_CENTER = "ワークセンタ";
    public static final String CODE = "コード";
    public static final String PRE_SETUP_TIME = "前段取り時間";
    public static final String POST_SETUP_TIME = "後段取り時間";
    public static final String ASSERT_NUMBER = "資産番号";
    public static final String PROGRAM_NUMBER = "プログラム番号";
    public static final String SPLIT_NUMBER = "分割番号";
    public static final String SEGMENT = "セグメント";
    public static final String ORDER_SUM_NUMBER = "オーダ総数";


    Map<ASP_COL, String> planData = new HashMap<>();
    List<String> header;


    public boolean setValue(int rowNo, List<String> header, List<String> values) {
        this.rowNo = rowNo;
        this.header = header;

        final int colSize = Math.min(values.size(), ASP_COL.values().length);
        planData = Arrays.stream(ASP_COL.values(), 0, colSize)
                .collect(Collectors.toMap(Function.identity(), col -> values.get(col.ordinal())));

        // 必須項目が無い
        if (Stream.of(ASP_COL.values())
                .filter(col -> col.required)
                .noneMatch(planData::containsKey)) {
            return false;
        }

        if (!StringUtils.isEmpty(getSerialNumber())) {
            // シリアル番号は7桁0詰め
            planData.put(ASP_COL.SERIAL_NUMBER, String.format("%7s", planData.get(ASP_COL.SERIAL_NUMBER)).replace(" ", "0"));
        }

        // カンバン名
        this.kanbanName = getOrderCode();
        if (!StringUtils.isEmpty(getSerialNumber())) {
            this.kanbanName += "-" + getSerialNumber();
        }

        if (!StringUtils.isEmpty(getSplitNo())) {
            // 分割番号は3桁0詰め
            planData.put(ASP_COL.SPLIT_NO, String.format("%3s", planData.get(ASP_COL.SPLIT_NO)).replace(" ", "0"));
            this.kanbanName += ":" + getSplitNo();
        }

        // 工程順名
        this.workflowName = getItemCode();
        if (!StringUtils.isEmpty(getWorkSelector())) {
            this.workflowName += ":" + getWorkSelector();
        }

        // 工程名
        if (StringUtils.isEmpty(planData.get(ASP_COL.ALTERNATIVE_WORK))) {
            this.workName = getDefaultWorkName();
        } else {
            this.workName = planData.get(ASP_COL.ALTERNATIVE_WORK);
        }

        this.workType = ImportAsprovaBomCsv.WORK_TYPE.MW;

        return true;
    }

    /**
     * 標準の工程名を取得する
     * @return
     */
    private String getDefaultWorkName()
    {
        return getWorkNumber() + "_" + getWorkCode();
    }

    /**
     * データのチェックを実施
     *
     * @return
     */
    public List<String> checkData() {
        List<String> ret = new ArrayList<>();

        if (ImportAsprovaBomCsv.WORK_TYPE.MW.equals(workType)) {
            Arrays.stream(ASP_COL.values())
                    .filter(elem -> elem.required)
                    .filter(elem -> StringUtils.isEmpty(this.planData.get(elem)))
                    .forEach(elem -> {
                        String category = header.size() > elem.ordinal() ? header.get(elem.ordinal()) : elem.toString();
                        ret.add(String.format("   > %sが空白です。取込ファイルを確認してください。 [%d行目]", category, this.getRowNo()));
                    });

            if (!this.skipFlag && StringUtils.isEmpty(this.planData.get(ASP_COL.MAIN_RESOUCE)) && !COMPLETE_WORK_STATUS.equals(this.planData.get(ASP_COL.WORK_STATUS))) {
                ret.add(String.format("   > %sが空白です。取込ファイルを確認してください。 [%d行目]", header.get(ASP_COL.MAIN_RESOUCE.ordinal()), this.getRowNo()));
            }


            if (NO_PLANED_STATUS.equals(getWorkStatus())) {
                ret.add(String.format("   > ステータスの設定が異常です。 [%d行目]", this.getRowNo()));
            }

        } else {
            if (!this.skipFlag && StringUtils.isEmpty(this.planData.get(ASP_COL.MAIN_RESOUCE)) && !COMPLETE_WORK_STATUS.equals(this.planData.get(ASP_COL.WORK_STATUS))) {
                ret.add(String.format("   > 段取り用工程コードが空白です。取込ファイルを確認してください。 [%d行目]", this.getRowNo()));
            }

            if (!this.skipFlag && StringUtils.isEmpty(this.planData.get(ASP_COL.MACHINE_CODE)) && !COMPLETE_WORK_STATUS.equals(this.planData.get(ASP_COL.WORK_STATUS))) {
                ret.add(String.format("   > %sが空白です。取込ファイルを確認してください。 [%d行目]", header.get(ASP_COL.MACHINE_CODE.ordinal()), this.getRowNo()));
            }

        }
        return ret;
    }



    /**
     * Asprova生産計画ファイル インポート用データ
     */
    public ImportAsprovaPlanCsv() {
    }

    /**
     * コードを取得する。
     *
     * @return コード
     */
    public String getCode() {
        return planData.get(ASP_COL.CODE);
    }


    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getItemName() {
        return planData.get(ASP_COL.ITEM_NAME);
    }


    /**
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getItemCode() {
        return planData.get(ASP_COL.ITEM_CODE);
    }


    /**
     * ニックネームを取得する。
     *
     * @return ニックネーム
     */
    public String getNickName() {
        return planData.get(ASP_COL.NICK_NAME);
    }


    /**
     * ロット数を取得する。
     *
     * @return ロット数
     */
    public String getLotQuantity() {
        return planData.get(ASP_COL.LOT_COUNT);
    }


    /**
     * 工程コードを取得する。
     *
     * @return 工程コード
     */
    public String getWorkCode() {
        return planData.get(ASP_COL.WORK_CODE);
    }


    /**
     * 主資源を取得する。
     *
     * @return 主資源
     */
    public String getMainResource() {
        return planData.get(ASP_COL.MAIN_RESOUCE);
    }


    /**
     * 副資源を取得する。
     *
     * @return 副資源
     */
    public String getSubResource() {
        return planData.get(ASP_COL.SUB_RESOUCE);
    }


    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public String getTactTime() {
        return planData.get(ASP_COL.TACK_TIME);
    }


    /**
     * 工程開始日時を取得する。
     *
     * @return 工程開始日時
     */
    public String getWorkStartTime() {
        return planData.get(ASP_COL.WORK_START_TIME);
    }


    /**
     * 工程終了日時を取得する。
     *
     * @return 工程終了日時
     */
    public String getWorkEndTime() {
        return planData.get(ASP_COL.WORK_END_TIME);
    }


    /**
     * 機械番号を取得する。
     *
     * @return 機械番号
     */
    public String getMachineCode() {
        return planData.get(ASP_COL.MACHINE_CODE);
    }


    /**
     * ワークセンタを取得する。
     *
     * @return ワークセンタ
     */
    public String getWorkCenter() {
        return planData.get(ASP_COL.WORK_CENTER);
    }


    /**
     * 納期を取得する。
     *
     * @return 納期
     */
    public String getDeliveryDate() {
        return planData.get(ASP_COL.DELIVARY_DATE);
    }


    /**
     * 受注番号を取得する。
     *
     * @return 受注番号
     */
    public String getOrderNumber() {
        return planData.get(ASP_COL.ORDER_NUMBER);
    }


    /**
     * 納入先を取得する。
     *
     * @return 納入先
     */
    public String getDeliveryDestination() {
        return planData.get(ASP_COL.DELIVERY_DESTINATION);
    }


    /**
     * 材質を取得する。
     *
     * @return 材質
     */
    public String getMaterial() {
        return planData.get(ASP_COL.MATERIAL);
    }


    /**
     * 寸法を取得する。
     *
     * @return 寸法
     */
    public String getSizeData() {
        return planData.get(ASP_COL.SIZE_DATA);
    }


    /**
     * オーダを取得する。
     *
     * @return オーダ
     */
    public String getOrderCode() {
        return planData.get(ASP_COL.ORDER);
    }


    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNumber() {
        return planData.get(ASP_COL.SERIAL_NUMBER);
    }


    /**
     * 種別を取得する。
     *
     * @return 種別
     */
    public String getType() {
        return planData.get(ASP_COL.TYPE);
    }


    /**
     * 分割番号を取得する。
     *
     * @return 分割番号
     */
    public String getSplitNo() {
        return planData.get(ASP_COL.SPLIT_NO);
    }


    /**
     * 工程番号を取得する。
     *
     * @return 工程番号
     */
    public String getWorkNumber() {
        return planData.get(ASP_COL.WORK_NUMBER);
    }


    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 　工程状態取得
     *
     * @return 工程状態
     */
    public String getWorkStatus() {
        return planData.get(ASP_COL.WORK_STATUS);
    }

    /**
     * 工程資産番号
     *
     * @return 工程資産番号
     */
    public String getAssertNumber() {
        return planData.get(ASP_COL.ASSERT_NUMBER);
    }

    /**
     * プログラム番号
     *
     * @return プログラム番号
     */
    public String getProgramNumber() {
        return planData.get(ASP_COL.PROGRAM_NUMBER);
    }

    public KanbanStatusEnum getWorkKanbanStatus() {

        final String status = this.getWorkStatus();
        if (ImportAsprovaPlanCsv.COMPLETE_WORK_STATUS.equals(status)) {
            return KanbanStatusEnum.COMPLETION;
        }

        if (ImportAsprovaPlanCsv.WORKING_STATUS.equals(status)) {
            return KanbanStatusEnum.WORKING;
        }

        return KanbanStatusEnum.PLANNED;
    }


    /**
     * モデル名取得
     *
     * @return モデル名
     */
    public String getModelName() {
        return planData.get(ASP_COL.MODEL_NAME);
    }

    /**
     * ワークセレクタ名取得
     *
     * @return ワークセレクタ名
     */
    public String getWorkSelector() {
        return planData.get(ASP_COL.WORK_SELECTOR);
    }

    /**
     * 前段取り時間取得
     *
     * @return
     */
    public String getPreSetupTime() {
        return planData.get(ASP_COL.PRE_SETUP_TACK_TIME);
    }

    /**
     * 後段取り時間取得
     *
     * @return
     */
    public String getPostSetupTime() {
        return planData.get(ASP_COL.POST_SETUP_TACK_TIME);
    }

    /**
     * セグメント取得
     *
     * @return
     */
    public String getSegment() {
        return planData.get(ASP_COL.SEGMENT);
    }

    /**
     * オーダ総数
     *
     * @return
     */
    public String getOrderSumNumber() {
        return planData.get(ASP_COL.ORDER_SUM_NUMBER);
    }

    /**
     * 工程順名取得
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    public int getRowNo() {
        return rowNo;
    }

    /**
     * スキップするか?
     * @return
     */
    public boolean getSkipFlag() {
        return skipFlag;
    }

    public void setSkipFlag(boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    public String getAlternativeSetupName() {
        return this.planData.get(ASP_COL.ALTERNATIVE_SETUP);
    }

    private void removeSetupInfo() {
        this.planData.remove(ASP_COL.PRE_SETUP_START_TIME);
        this.planData.remove(ASP_COL.PRE_SETUP_END_TIME);
        this.planData.remove(ASP_COL.PRE_SETUP_TACK_TIME);
        this.planData.remove(ASP_COL.POST_SETUP_START_TIME);
        this.planData.remove(ASP_COL.POST_SETUP_END_TIME);
        this.planData.remove(ASP_COL.POST_SETUP_TACK_TIME);
        this.planData.remove(ASP_COL.SETUP_RESOURCE);
    }

    public String get(ASP_COL col) {
        return planData.get(col);
    }

    public String getOrDefault(ASP_COL col, String defaultValue) {
        return planData.getOrDefault(col, defaultValue);
    }

    public String createPreSetupName() {
        return StringUtils.isEmpty(this.planData.get(ASP_COL.ALTERNATIVE_SETUP))
                ? this.getDefaultWorkName() + "_" + ImportAsprovaBomCsv.WORK_TYPE.FD.name
                : this.planData.get(ASP_COL.ALTERNATIVE_SETUP);
    }

    public String createPostSetupName() {
        return StringUtils.isEmpty(this.planData.get(ASP_COL.ALTERNATIVE_SETUP))
                ? this.getDefaultWorkName() + "_" + ImportAsprovaBomCsv.WORK_TYPE.BD.name
                : this.planData.get(ASP_COL.ALTERNATIVE_SETUP);
    }

    /**
     * 前段取り用計画を作成
     *
     * @return 前段取り用計画を作成
     */
    public Optional<ImportAsprovaPlanCsv> createPreSetupPlan() {
        ImportAsprovaPlanCsv ret = new ImportAsprovaPlanCsv();
        ret.header = this.header;
        ret.workType = ImportAsprovaBomCsv.WORK_TYPE.FD;
        ret.planData = new HashMap<>(this.planData);
        ret.rowNo = this.rowNo;
        ret.kanbanName = this.kanbanName;
        ret.workflowName = this.workflowName;
        ret.workName = createPreSetupName();

        if (StringUtils.nonEmpty(planData.get(ASP_COL.PRE_SETUP_START_TIME))) {
            ret.planData.put(ASP_COL.WORK_START_TIME, this.planData.get(ASP_COL.PRE_SETUP_START_TIME));
        }

        if (StringUtils.nonEmpty(planData.get(ASP_COL.PRE_SETUP_END_TIME))) {
            ret.planData.put(ASP_COL.WORK_END_TIME, this.planData.get(ASP_COL.PRE_SETUP_END_TIME));
        }

        if (StringUtils.nonEmpty(this.planData.get(ASP_COL.SETUP_RESOURCE))) {
            ret.planData.put(ASP_COL.MAIN_RESOUCE, this.planData.get(ASP_COL.SETUP_RESOURCE));
        }

        if (StringUtils.nonEmpty(planData.get(ASP_COL.PRE_SETUP_TACK_TIME))) {
            ret.planData.put(ASP_COL.TACK_TIME, this.planData.get(ASP_COL.PRE_SETUP_TACK_TIME));
        }

        if (StringUtils.isEmpty(planData.get(ASP_COL.PRE_SETUP_START_TIME))
                && StringUtils.isEmpty(planData.get(ASP_COL.PRE_SETUP_END_TIME))
                && StringUtils.isEmpty(planData.get(ASP_COL.PRE_SETUP_TACK_TIME))
        ) {
            ret.setSkipFlag(true);
            ret.planData.put(ASP_COL.MAIN_RESOUCE, "");
        }

        // 段取り情報は削除
        ret.removeSetupInfo();
        return Optional.of(ret);
    }

    /**
     * 後段取り用計画を作成
     *
     * @return 後段取り用計画
     */
    public Optional<ImportAsprovaPlanCsv> createPostSetupPlan() {
        ImportAsprovaPlanCsv ret = new ImportAsprovaPlanCsv();
        ret.workType = ImportAsprovaBomCsv.WORK_TYPE.BD;
        ret.planData = new HashMap<>(this.planData);
        ret.rowNo = this.rowNo;
        ret.kanbanName = this.kanbanName;
        ret.workflowName = this.workflowName;
        ret.workName = createPostSetupName();

        if (StringUtils.nonEmpty(planData.get(ASP_COL.POST_SETUP_START_TIME))) {
            ret.planData.put(ASP_COL.WORK_START_TIME, this.planData.get(ASP_COL.POST_SETUP_START_TIME));
        }

        if (StringUtils.nonEmpty(planData.get(ASP_COL.POST_SETUP_END_TIME))) {
            ret.planData.put(ASP_COL.WORK_END_TIME, this.planData.get(ASP_COL.POST_SETUP_END_TIME));
        }

        if (StringUtils.nonEmpty(planData.get(ASP_COL.SETUP_RESOURCE))) {
            ret.planData.put(ASP_COL.MAIN_RESOUCE, this.planData.get(ASP_COL.SETUP_RESOURCE));
        }

        if (StringUtils.nonEmpty(planData.get(ASP_COL.POST_SETUP_TACK_TIME))) {
            ret.planData.put(ASP_COL.TACK_TIME, this.planData.get(ASP_COL.POST_SETUP_TACK_TIME));
        }

        // 後段取り工程名
        if (StringUtils.isEmpty(planData.get(ASP_COL.POST_SETUP_START_TIME))
                && StringUtils.isEmpty(planData.get(ASP_COL.POST_SETUP_END_TIME))
                && StringUtils.isEmpty(planData.get(ASP_COL.POST_SETUP_TACK_TIME))
        ) {
            ret.setSkipFlag(true);
            ret.planData.put(ASP_COL.MAIN_RESOUCE, "");
        }

        // 段取り情報は削除
        ret.removeSetupInfo();

        return Optional.of(ret);
    }

    @Override
    public String toString() {
        return new StringBuilder("AsprovaPlanFormatInfo{")
                .append("kanbanName=").append(this.kanbanName)
                .append("workflowName=").append(this.workflowName)
                .append("workName=").append(this.workName)
                .append("skipFlag=").append(this.skipFlag)
                .append(planData.entrySet()
                        .stream()
                        .map(entry -> entry.getKey().toString() + "=" + entry.getValue())
                        .collect(Collectors.joining(",")))
                .append("}").toString();
    }
}
