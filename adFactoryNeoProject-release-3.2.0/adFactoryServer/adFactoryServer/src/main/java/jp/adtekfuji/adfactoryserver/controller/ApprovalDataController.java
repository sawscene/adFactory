/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.utility.StringTime;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.entity.work.DispAddInfoEntity;
import jp.adtekfuji.adFactory.entity.work.TraceSettingEntity;
import jp.adtekfuji.adFactory.enumerate.AccessoryFieldTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.TraceOptionTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalFlowEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalRouteEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkSectionEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalFlowModel;
import static jp.adtekfuji.adfactoryserver.utility.JsonUtils.jsonToObjects;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import jp.adtekfuji.adfactoryserver.utility.XmlSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 * 承認画面 データコントローラー
 *
 * @author shizuka.hirano
 */
@Named(value = "approvalData")
@SessionScoped
public class ApprovalDataController extends BackingBeen {

    /**
     * コロン
     */
    private static final String COLON = " : ";

    /**
     * 文字色黒
     */
    private static final String BLACK = "color:#000000;";

    /**
     * 文字色赤
     */
    private static final String RED = "color:#fc0000;";

    /**
     * 時刻フォーマット
     */
    private static final String TIME_FORMAT_HHMM = "HH:mm";

    /**
     * 日付フォーマット
     */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * ログ出力クラス
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * 承認フローモデル
     */
    @EJB
    private ApprovalFlowModel approvalFlowModel;

    /**
     * エラーメッセージ
     */
    private String errMsg;

    /**
     * 承認
     */
    private final String typeLabelApproval;

    /**
     * 申請ID(項目)
     */
    private final String typeLabelApprovalId;

    /**
     * 申請者(項目)
     */
    private final String typeLabelRequestId;

    /**
     * データ種別(項目)
     */
    private String typeLabelDataType;

    /**
     * コメントプレースホルダー
     */
    private final String typeLabelCommentPlaceholder;

    /**
     * 申請取消ボタン
     */
    private final String typeLabelRequestCancelButton;

    /**
     * 承認ボタン
     */
    private String typeLabelApprovalButton;

    /**
     * 却下ボタン
     */
    private final String typeLabelRemandButton;

    /**
     * 承認取消ボタン
     */
    private final String typeLabelApprovalCancelButton;

    /**
     * リビジョン(項目)
     */
    private final String typeLabelRevision;

    /**
     * 申請コメント(項目)
     */
    private final String typeLabelComment;

    /**
     * 承認ルート(項目)
     */
    private final String typeLabelApprovalRoute;

    /**
     * ドキュメント名(項目)
     */
    private final String typeLabelDocumentName;

    /**
     * ページ番号(項目)
     */
    private final String typeLabelPageNum;

    /**
     * 更新日時(項目)
     */
    private final String typeLabelUpdateDateTime;

    /**
     * 種別(項目)
     */
    private final String typeLabelCat;

    /**
     * 項目名(項目)
     */
    private final String typeLabelKey;

    /**
     * 現在値(項目)
     */
    private final String typeLabelVal;

    /**
     * 基準値(項目)
     */
    private final String typeLabelStandard;

    /**
     * 入力規則(項目)
     */
    private final String typeLabelRules;

    /**
     * オプション(項目)
     */
    private final String typeLabelOpt;

    /**
     * 進捗チェック(項目)
     */
    private final String typeLabelCp;

    /**
     * タグ(項目)
     */
    private final String typeLabelTag;

    /**
     * 品質トレーサビリティ(項目)
     */
    private final String typeLabelTraceQuality;

    /**
     * 「申請取消」ダイアログ表示メッセージ
     */
    private final String typeLabelMsgApplicationCancel;

    /**
     * 「承認」ダイアログ表示メッセージ
     */
    private final String typeLabelMsgApproval;

    /**
     * 「最終承認」ダイアログ表示メッセージ
     */
    private final String typeLabelMsgApprovalFinal;

    /**
     * 「却下」ダイアログ表示メッセージ
     */
    private final String typeLabelMsgCancel;

    /**
     * 「承認取消」ダイアログ表示メッセージ
     */
    private final String typeLabelMsgApprovalCancel;

    /**
     * セッションタイムアウトメッセージ
     */
    private final String typeLabelMsgErrMsg;

    /**
     * 申請ID
     */
    private Long applovalId;

    /**
     * ログイン情報(組織識別名)
     */
    private String orgIdent;

    /**
     * ログイン情報(組織名)
     */
    private String orgName;

    /**
     * ログイン情報(組織Id)
     */
    private Long orgId;

    /**
     * 申請者
     */
    private String requestName;

    /**
     * 工程又は工程順
     */
    private String workName;

    /**
     * リビジョン
     */
    private String revision;

    /**
     * 申請コメント
     */
    private String approvalComent;

    /**
     * 承認ルート名
     */
    private String approvalRoute;

    /**
     * 「コメント記入欄」テキストボックス
     */
    private String commentColumn;

    /**
     * 申請情報
     */
    private ApprovalEntity approvalInfo;

    /**
     * ログインユーザー情報
     */
    private OrganizationEntity loginUserInfo;

    /**
     * 工程変更情報表示フラグ
     */
    private Boolean workFlg;

    /**
     * 工程順変更情報表示フラグ
     */
    private Boolean workFlowFlg;

    /**
     * ボタン制御JSON文字列
     */
    private String buttonControlJson;

    /**
     * 承認者一覧
     */
    private List<DispApprovalHistoryData> additionHistory;

    /**
     * 工程情報
     */
    private List<DispApprovalChangeData> workInfo;

    /**
     * 工程ドキュメント・品質トレーサビリティ情報
     */
    private List<DispWorkInfoData> sectionInfo;

    /**
     * 工程順情報
     */
    private List<DispApprovalChangeData> workflowInfo;

    /**
     * プロセスフローの変更内容
     */
    private List<List<DispApprovalChangeData>> processFlow;

    /**
     * 追加工程の変更内容
     */
    private List<List<DispApprovalChangeData>> separateWork;

    /**
     * メッセージダイアログのヘッダータイトル
     */
    private String headerInfo;

    /**
     * メッセージダイアログのメッセージ
     */
    private String messageInfo;

    /**
     * オープンキー
     */
    private String openKey;

    /**
     * 表示順
     */
    private int dispIndex;

    /**
     * コンストラクタ
     */
    public ApprovalDataController() {
        this.typeLabelApproval = LocaleUtils.getString("approval.approvalTitle");
        this.typeLabelApprovalId = LocaleUtils.getString("approval.approvalId");
        this.typeLabelRequestId = LocaleUtils.getString("approval.applicant");
        this.typeLabelRevision = LocaleUtils.getString("approval.revision");
        this.typeLabelComment = LocaleUtils.getString("approval.comment");
        this.typeLabelApprovalRoute = LocaleUtils.getString("approval.approvalRoute");
        this.typeLabelCommentPlaceholder = LocaleUtils.getString("approval.commentPlaceholder");
        this.typeLabelRequestCancelButton = LocaleUtils.getString("approval.requestCancelButton");
        this.typeLabelApprovalButton = LocaleUtils.getString("approval.approvalTitle");
        this.typeLabelRemandButton = LocaleUtils.getString("approval.remandButton");
        this.typeLabelApprovalCancelButton = LocaleUtils.getString("approval.approvalCancelButton");
        this.typeLabelDocumentName = LocaleUtils.getString("approval.documentName");
        this.typeLabelPageNum = LocaleUtils.getString("approval.pageNum");
        this.typeLabelUpdateDateTime = LocaleUtils.getString("approval.updateDateTime");
        this.typeLabelCat = LocaleUtils.getString("approval.kinds");
        this.typeLabelKey = LocaleUtils.getString("approval.itemName");
        this.typeLabelVal = LocaleUtils.getString("approval.presentValue");
        this.typeLabelStandard = LocaleUtils.getString("approval.standardValue");
        this.typeLabelRules = LocaleUtils.getString("approval.inputRules");
        this.typeLabelOpt = LocaleUtils.getString("approval.option");
        this.typeLabelCp = LocaleUtils.getString("approval.progressCheck");
        this.typeLabelTag = LocaleUtils.getString("key.Tag");
        this.typeLabelTraceQuality = LocaleUtils.getString("approval.qualityTraceability");
        this.typeLabelMsgApplicationCancel = "approval.msgApplicationCancel";
        this.typeLabelMsgApproval = "approval.msgApproval";
        this.typeLabelMsgApprovalFinal = "approval.msgApprovalFinal";
        this.typeLabelMsgCancel = "approval.msgCancel";
        this.typeLabelMsgApprovalCancel = "approval.msgApprovalCancel";
        this.typeLabelMsgErrMsg = "approval.msgErrMsg";
    }

    /**
     * 申請IDを取得する。
     *
     * @return 申請ID
     */
    public Long getApplovalId() {
        return applovalId;
    }

    /**
     * 申請IDを設定する。
     *
     * @param applovalId 申請ID
     */
    public void setApplovalId(Long applovalId) {
        this.applovalId = applovalId;
    }

    /**
     * ログインユーザー識別子を取得する。
     *
     * @return 組織識別子
     */
    public String getOrgIdent() {
        return orgIdent;
    }

    /**
     * ログインユーザー識別子を設定する。
     *
     * @param orgIdent ログインユーザー識別子
     */
    public void setOrgIdent(String orgIdent) {
        this.orgIdent = orgIdent;
    }

    /**
     * ログインユーザー組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 申請者を取得する。
     *
     * @return 申請者名
     */
    public String getRequestName() {
        return requestName;
    }

    /**
     * 工程又は工程順を取得する。
     *
     * @return 工程又は工程順
     */
    public String getWorkName() {
        return workName;
    }

    /**
     * リビジョンを取得する。
     *
     * @return リビジョン
     */
    public String getRevision() {
        return revision;
    }

    /**
     * 申請コメントを取得する。
     *
     * @return 申請コメント
     */
    public String getApprovalComment() {
        return approvalComent;
    }

    /**
     * 承認ルート名を取得する。
     *
     * @return 承認ルート名
     */
    public String getApprovalRoute() {
        return approvalRoute;
    }

    /**
     * 承認者一覧を取得する。
     *
     * @return 承認者一覧
     */
    public List<DispApprovalHistoryData> getAdditionHistory() {
        return additionHistory;
    }

    /**
     * コメント記入欄を取得する。
     *
     * @return コメント記入欄
     */
    public String getCommentColumn() {
        return commentColumn;
    }

    /**
     * コメント記入欄を設定する。
     *
     * @param commentColumn コメント記入欄
     */
    public void setCommentColumn(String commentColumn) {
        this.commentColumn = commentColumn;
    }

    /**
     * 工程変更情報表示フラグを取得する。
     *
     * @return 工程変更情報表示フラグ
     */
    public Boolean getWorkFlg() {
        return workFlg;
    }

    /**
     * 工程順変更情報表示フラグを取得する。
     *
     * @return 工程順変更情報表示フラグ
     */
    public Boolean getWorkFlowFlg() {
        return workFlowFlg;
    }

    /**
     * ボタン制御JSON文字列を取得する。
     *
     * @return ボタン制御JSON文字列
     */
    public String getButtonControlJson() {
        return buttonControlJson;
    }

    /**
     * ボタン制御JSON文字列を設定する。
     *
     * @return ボタン制御JSON文字列
     */
    public String setButtonControlJson() {
        return buttonControlJson;
    }

    /**
     * 工程情報を取得する。
     *
     * @return 工程情報
     */
    public List<DispApprovalChangeData> getWorkInfo() {
        return workInfo;
    }

    /**
     * 工程ドキュメント・品質トレーサビリティ情報を取得する。
     *
     * @return 工程ドキュメント・品質トレーサビリティ情報
     */
    public List<DispWorkInfoData> getSectionInfo() {
        return sectionInfo;
    }

    /**
     * 工程順情報を取得する。
     *
     * @return 工程順情報
     */
    public List<DispApprovalChangeData> getWorkflowInfo() {
        return workflowInfo;
    }

    /**
     * プロセスフロー情報を取得する。
     *
     * @return プロセスフロー情報
     */
    public List<List<DispApprovalChangeData>> getProcessFlow() {
        return processFlow;
    }

    /**
     * 追加工程情報を取得する。
     *
     * @return 追加工程情報
     */
    public List<List<DispApprovalChangeData>> getSeparateWork() {
        return separateWork;
    }

    /**
     * エラーメッセージを取得する。
     *
     * @return エラーメッセージ
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * エラーメッセージを設定する。
     *
     * @param errMsg エラーメッセージ
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    /**
     * 承認ラベルを取得する。
     *
     * @return　申請IDラベル
     */
    public String getTypeLabelApproval() {
        return typeLabelApproval;
    }

    /**
     * 申請IDラベルを取得する。
     *
     * @return　申請IDラベル
     */
    public String getTypeLabelApprovalId() {
        return typeLabelApprovalId;
    }

    /**
     * 申請者ラベルを取得する。
     *
     * @return　申請者ラベル
     */
    public String getTypeLabelRequestId() {
        return typeLabelRequestId;
    }

    /**
     * 申請者を取得する。
     *
     * @return 申請者
     */
    public String getRequesetName() {
        return this.requestName;
    }

    /**
     * データ種別(項目)を取得する。
     *
     * @return データ種別(項目)
     */
    public String getTypeLabelDataType() {
        return typeLabelDataType;
    }

    /**
     * リビジョンラベルを取得する。
     *
     * @return　リビジョンラベル
     */
    public String getTypeLabelRevision() {
        return typeLabelRevision;
    }

    /**
     * コメントラベルを取得する。
     *
     * @return　コメントラベル
     */
    public String getTypeLabelComment() {
        return typeLabelComment;
    }

    /**
     * コメントプレースホルダーを取得する。
     *
     * @return コメントプレースホルダー
     */
    public String getTypeLabelCommentPlaceholder() {
        return typeLabelCommentPlaceholder;
    }

    /**
     * 申請取消ボタンラベルを取得する。
     *
     * @return 申請取消ボタン
     */
    public String getTypeLabelRequestCancelButton() {
        return typeLabelRequestCancelButton;
    }

    /**
     * 承認ボタンラベルを取得する。
     *
     * @return 承認ボタン
     */
    public String getTypeLabelApprovalButton() {
        return typeLabelApprovalButton;
    }

    /**
     * 却下ボタンラベルを取得する。
     *
     * @return 却下ボタン
     */
    public String getTypeLabelApprovalRemandButton() {
        return typeLabelRemandButton;
    }

    /**
     * 承認取消ボタンラベルを取得する。
     *
     * @return 承認取消ボタン
     */
    public String getTypeLabelApprovalCancelButton() {
        return typeLabelApprovalCancelButton;
    }

    /**
     * 承認ルートラベルラベルを取得する。
     *
     * @return　承認ルートラベル
     */
    public String getTypeLabelApprovalRoute() {
        return typeLabelApprovalRoute;
    }

    /**
     * ドキュメント名ラベルを取得する。
     *
     * @return　ドキュメント名
     */
    public String getTypeLabelDocumentName() {
        return typeLabelDocumentName;
    }

    /**
     * 更新日時ラベルを取得する。
     *
     * @return　更新日時
     */
    public String getTypeLabelUpdateDateTime() {
        return typeLabelUpdateDateTime;
    }

    /**
     * 種別ラベルを取得する。
     *
     * @return　種別
     */
    public String getTypeLabelCat() {
        return typeLabelCat;
    }

    /**
     * 項目名ラベルを取得する。
     *
     * @return　項目名
     */
    public String getTypeLabelKey() {
        return typeLabelKey;
    }

    /**
     * 現在値ラベルを取得する。
     *
     * @return　現在値
     */
    public String getTypeLabelVal() {
        return typeLabelVal;
    }

    /**
     * 基準値ラベルを取得する。
     *
     * @return　基準値
     */
    public String getTypeLabelStandard() {
        return typeLabelStandard;
    }

    /**
     * 入力規則ラベルを取得する。
     *
     * @return　入力規則
     */
    public String getTypeLabelRules() {
        return typeLabelRules;
    }

    /**
     * オプションラベルを取得する。
     *
     * @return　オプション
     */
    public String getTypeLabelOpt() {
        return typeLabelOpt;
    }

    /**
     * 進捗チェックラベルを取得する。
     *
     * @return　進捗チェック
     */
    public String getTypeLabelCp() {
        return typeLabelCp;
    }

    /**
     * タグラベルを取得する。
     *
     * @return　タグ
     */
    public String getTypeLabelTag() {
        return typeLabelTag;
    }

    /**
     * 品質トレーサビリティラベルを取得する。
     *
     * @return　品質トレーサビリティ
     */
    public String getTypeLabelTraceQuality() {
        return typeLabelTraceQuality;
    }

    /**
     * 申請取消ダイアログメッセージを取得する。
     *
     * @return　申請取消ダイアログメッセージ
     */
    public String getTypeLabelMsgApplicationCancel() {
        return typeLabelMsgApplicationCancel;
    }

    /**
     * 承認ダイアログメッセージを取得する。
     *
     * @return　承認ダイアログメッセージ
     */
    public String getTypeLabelMsgApproval() {
        return typeLabelMsgApproval;
    }

    /**
     * 却下ダイアログメッセージを取得する。
     *
     * @return　却下ダイアログメッセージ
     */
    public String getTypeLabelMsgCancel() {
        return typeLabelMsgCancel;
    }

    /**
     * 承認取消ダイアログメッセージを取得する。
     *
     * @return　承認取消ダイアログメッセージ
     */
    public String getTypeLabelMsgApprovalCancel() {
        return typeLabelMsgApprovalCancel;
    }

    /**
     * セッションタイムアウトメッセージを取得する。
     *
     * @return　セッションタイムアウトメッセージ
     */
    public String getTypeLabelMsgErrMsg() {
        return typeLabelMsgErrMsg;
    }

    /**
     * 表示フラグの初期化
     */
    public void initDisplayFlg() {
        workFlg = true;
        workFlowFlg = false;
    }

    /**
     * 承認画面を初期化する。
     *
     */
    @PostConstruct
    public void inital() {
        initDisplayFlg();
    }

    /**
     * ユーザー認証をおこなう。
     */
    public void authUser() {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

            // adManagerからアクセスされた場合は、ログイン画面に遷移しない
            if ("rvZE/xl8isESD7AkAFlvVA==".equals(this.openKey)) {
                // オープンキーを消去
                this.openKey = null;

                if (Objects.isNull(this.applovalId) || Objects.isNull(this.orgIdent)) {
                    this.errMsg = LocaleUtils.getString("key.errParam");
                    this.applovalId = null;
                    this.orgIdent = null;

                    String contextPath = context.getRequestContextPath();
                    context.redirect(contextPath + "/blank.xhtml");
                    return;
                }

                loginUserInfo = approvalFlowModel.findOrganizationByIdentify(this.orgIdent);
                if (Objects.isNull(loginUserInfo) || !loginUserInfo.getOrganizationIdentify().equals(this.orgIdent)) {
                    this.errMsg = LocaleUtils.getString("key.errOrg");
                    String contextPath = context.getRequestContextPath();
                    context.redirect(contextPath + "/blank.xhtml");
                    return;
                }

                this.orgName = loginUserInfo.getOrganizationName();
                this.orgIdent = loginUserInfo.getOrganizationIdentify();
                this.orgId = loginUserInfo.getOrganizationId();

                // リロード
                String contextPath = context.getRequestContextPath();
                context.redirect(contextPath + "/approval/index.xhtml");
                return;
            }

            // ページの再読み込み時はログイン画面に遷移しない
            if (Objects.nonNull(this.loginUserInfo)
                    && StringUtils.equals(this.loginUserInfo.getOrganizationIdentify(), this.orgIdent)) {

                if (Objects.isNull(this.applovalId) || Objects.isNull(this.orgIdent)) {
                    this.errMsg = LocaleUtils.getString("key.errParam");
                    this.applovalId = null;
                    this.orgIdent = null;

                    String contextPath = context.getRequestContextPath();
                    context.redirect(contextPath + "/blank.xhtml");
                }
                return;
            }

            UserAuth userAuth = (UserAuth) context.getSessionMap().get("userAuth");
            if (Objects.isNull(userAuth) || !userAuth.isAuthenticated()) {
                // ユーザー未認証の場合
                if (Objects.isNull(this.applovalId) || Objects.isNull(this.orgIdent)) {
                    this.errMsg = LocaleUtils.getString("key.errParam");
                    this.applovalId = null;
                    this.orgIdent = null;

                    String contextPath = context.getRequestContextPath();
                    context.redirect(contextPath + "/blank.xhtml");
                    return;
                }

                // ユーザー認証情報
                userAuth = new UserAuth(this.orgIdent, "/approval/index.xhtml");
                userAuth.getParameterMap().put("applovalId", this.applovalId);
                userAuth.getParameterMap().put("orgIdent", this.orgIdent);

                // ログイン画面へ遷移
                context.getSessionMap().put("userAuth", userAuth);
                String contextPath = context.getRequestContextPath();
                context.redirect(contextPath + "/login.xhtml");
                return;
            }

            // ユーザー認証情報を削除
            context.getSessionMap().remove("userAuth");

            this.applovalId = (Long) userAuth.getParameterMap().get("applovalId");
            this.orgIdent = (String) userAuth.getParameterMap().get("orgIdent");

            //ログインユーザー情報を取得する
            //EJB 「組織情報取得」の 呼び出し
            loginUserInfo = approvalFlowModel.findOrganizationByIdentify(this.orgIdent);
            if (Objects.isNull(loginUserInfo) || !loginUserInfo.getOrganizationIdentify().equals(this.orgIdent)) {
                this.errMsg = LocaleUtils.getString("key.errOrg");
                String contextPath = context.getRequestContextPath();
                context.redirect(contextPath + "/blank.xhtml");
                return;
            }

            //　ログイン情報
            this.orgName = loginUserInfo.getOrganizationName();
            this.orgIdent = loginUserInfo.getOrganizationIdentify();
            this.orgId = loginUserInfo.getOrganizationId();

            this.preRenderView();

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     */
    public void preRenderView() {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

            //EJB 「申請情報の取得」の 呼び出し
            this.approvalInfo = approvalFlowModel.findApproval(this.applovalId);
            if (Objects.isNull(approvalInfo) || !approvalInfo.getApprovalId().equals(this.applovalId)) {
                this.errMsg = LocaleUtils.getString("approval.errApproval");
                String contextPath = context.getRequestContextPath();
                context.redirect(contextPath + "/blank.xhtml");
                return;
            }

            //EJB 「組織情報取得」の 呼び出し
            OrganizationEntity approvalUserInfo = approvalFlowModel.findOrganization(approvalInfo.getRequestorId());
            if (Objects.isNull(approvalUserInfo) || !approvalUserInfo.getOrganizationId().equals(approvalInfo.getRequestorId())) {
                this.errMsg = LocaleUtils.getString("approval.errReqOrg");
                String contextPath = context.getRequestContextPath();
                context.redirect(contextPath + "/blank.xhtml");
                return;
            }

            //　申請者情報
            this.requestName = approvalUserInfo.getOrganizationName();
            this.approvalComent = approvalInfo.getComment();

            WorkEntity oldWorkInfo = new WorkEntity();
            WorkEntity newWorkInfo = new WorkEntity();
            WorkflowEntity oldWorkflowInfo = new WorkflowEntity();
            WorkflowEntity newWorkflowInfo = new WorkflowEntity();
            if (approvalInfo.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                //工程情報(変更前)を取得
                //EJB 「工程情報取得」の呼び出し
                if (Objects.nonNull(approvalInfo.getOldData())) {
                    oldWorkInfo = approvalFlowModel.findWork(approvalInfo.getOldData());
                }

                //工程情報(変更後)を取得
                //EJB 「工程情報取得」の呼び出し
                if (Objects.nonNull(approvalInfo.getNewData())) {
                    newWorkInfo = approvalFlowModel.findWork(approvalInfo.getNewData());
                }

                if (Objects.isNull(newWorkInfo.getWorkId()) || !newWorkInfo.getWorkId().equals(approvalInfo.getNewData())) {
                    this.errMsg = LocaleUtils.getString("approval.errNewWork");
                    String contextPath = context.getRequestContextPath();
                    context.redirect(contextPath + "/blank.xhtml");
                    return;
                }

                this.typeLabelDataType = LocaleUtils.getString("key.Process");
                this.workName = newWorkInfo.getWorkName();
                this.revision = newWorkInfo.getWorkRev().toString();

            } else if (approvalInfo.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                //工程順情報(変更前)を取得
                //EJB 「工程順情報取得」の呼び出し
                if (Objects.nonNull(approvalInfo.getOldData())) {
                    oldWorkflowInfo = approvalFlowModel.findWorkflow(approvalInfo.getOldData());
                }

                //工程順情報(変更後)を取得
                //EJB 「工程順情報取得」の呼び出し
                if (Objects.nonNull(approvalInfo.getNewData())) {
                    newWorkflowInfo = approvalFlowModel.findWorkflow(approvalInfo.getNewData());
                }

                if (Objects.isNull(newWorkflowInfo.getWorkflowId()) || !newWorkflowInfo.getWorkflowId().equals(approvalInfo.getNewData())) {
                    this.errMsg = LocaleUtils.getString("approval.errNewWorkflow");
                    String contextPath = context.getRequestContextPath();
                    context.redirect(contextPath + "/blank.xhtml");
                    return;
                }

                this.typeLabelDataType = LocaleUtils.getString("key.OrderProcesses");
                this.workName = newWorkflowInfo.getWorkflowName();
                this.revision = newWorkflowInfo.getWorkflowRev().toString();
            }

            ApprovalRouteEntity approvalRouteInfo;
            try {
                approvalRouteInfo = approvalFlowModel.findApprovalRoute(approvalInfo.getRouteId(), loginUserInfo.getOrganizationId());
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                approvalRouteInfo = null;
            }

            if (Objects.isNull(approvalRouteInfo)) {
                this.errMsg = LocaleUtils.getString("approval.errApprovalRoute");
                String contextPath = context.getRequestContextPath();
                context.redirect(contextPath + "/blank.xhtml");
                return;
            }

            //　承認ルート名
            this.approvalRoute = approvalRouteInfo.getRouteName();
            // 承認フロー情報
            List<ApprovalFlowEntity> approvalOrderList = this.approvalInfo.getApprovalFlows();
            // 承認者名取得
            List<Long> orgIdList = new ArrayList<>();
            approvalOrderList.forEach(action -> orgIdList.add(action.getApproverId()));
            // 承認者組織情報取得
            List<OrganizationEntity> historyOrgList = this.approvalFlowModel.findOrganizationByIds(orgIdList);
            //表示用承認順情報
            List<DispApprovalHistoryData> dispHistoryList = new ArrayList<>();

            if (!approvalOrderList.isEmpty() && !historyOrgList.isEmpty()) {
                approvalOrderList.forEach((info) -> {
                    DispApprovalHistoryData entity = new DispApprovalHistoryData();
                    // 申請者以外
                    if (!(info.getApprovalOrder() == 0)) {
                        // 最終承認者
                        if (info.getApprovalFinal()) {
                            entity.setApprovalOrder(LocaleUtils.getString("approval.finalAuthorizer"));
                        } else {
                            // 承認者
                            entity.setApprovalOrder(LocaleUtils.getString("approval.authorizer") + info.getApprovalOrder().toString());
                        }

                        // 承認者名取得
                        for (OrganizationEntity org : historyOrgList) {
                            if (Objects.equals(info.getApproverId(), org.getOrganizationId())) {
                                entity.setApprovalName(org.getOrganizationName());
                                break;
                            }
                        }

                        String status = "";
                        switch (info.getApprovalState()) {
                            case APPROVE:
                            case FINAL_APPROVE:
                                status = LocaleUtils.getString("approval.approved");
                                break;
                            case REJECT:
                                status = LocaleUtils.getString("approval.rejected");
                                break;
                            default:
                                int index = approvalOrderList.indexOf(info);
                                if (index == 0 || approvalOrderList.get(index - 1).getApprovalState().equals(ApprovalStatusEnum.APPROVE)) {
                                    if (this.approvalInfo.getApprovalState().equals(ApprovalStatusEnum.CANCEL_APPLY)) {
                                        status = "";
                                    } else {
                                        status = LocaleUtils.getString("approval.approvalPending");
                                    }
                                } else {
                                    if (approvalOrderList.get(index - 1).getApprovalState().equals(ApprovalStatusEnum.APPROVE)) {
                                        status = LocaleUtils.getString("approval.approvalPending");
                                    } else {
                                        status = "";
                                    }
                                }
                                break;
                        }
                        entity.setApprovalState(status);
                        entity.setApprovalComment(info.getComment());
                        dispHistoryList.add(entity);
                    }
                });
            }

            // 表示用承認履歴を設定
            this.additionHistory = dispHistoryList;
            if (this.typeLabelDataType.equals(LocaleUtils.getString("key.OrderProcesses"))) {
                this.workFlg = false;
                this.workFlowFlg = true;
            } else {
                this.workFlg = true;
                this.workFlowFlg = false;
            }

            // 工程情報の取得
            if (typeLabelDataType.equals(LocaleUtils.getString("key.Process"))) {
                addWorkInfo(oldWorkInfo, newWorkInfo);
            } else {
                addWorkflowInfo(oldWorkflowInfo, newWorkflowInfo);
            }
//ボタンの活性制御
            this.switchingButton(getLoginApprovalFlow());
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 申請取消確認ダイアログを開く。
     */
    public void openCancelApplyDialog() {
        this.openDialog("/approval/confirmDialog.xhtml", "key.Confirm", this.typeLabelMsgApplicationCancel);
        logger.info("openCancelApplyDialog: {} {}", "key.Confirm", this.typeLabelMsgApplicationCancel);
    }

    /**
     * 承認確認ダイアログを開く。
     */
    public void openApprovalDialog() {
        Optional<ApprovalFlowEntity> optApprover = approvalInfo.getApprovalFlows().stream()
                .filter(p -> Objects.equals(loginUserInfo.getOrganizationId(), p.getApproverId()))
                .findFirst();
        if (optApprover.isPresent()) {
            String messageKey;
            if (optApprover.get().getApprovalFinal()) {
                messageKey = this.typeLabelMsgApprovalFinal;
            } else {
                messageKey = this.typeLabelMsgApproval;
            }
            this.openDialog("/approval/confirmDialog.xhtml", "key.Confirm", messageKey);
            logger.info("openApprovalDialog: {} {}", "key.Confirm", messageKey);
        }
    }

    /**
     * 却下確認ダイアログを開く。
     */
    public void openRejectDialog() {
        this.openDialog("/approval/confirmDialog.xhtml", "key.Confirm", this.typeLabelMsgCancel);
        logger.info("openRejectDialog: {} {}", "key.Confirm", this.typeLabelMsgCancel);
    }

    /**
     * 承認取消確認ダイアログを開く。
     */
    public void openCancelApprovalDialog() {
        this.openDialog("/approval/confirmDialog.xhtml", "key.Confirm", this.typeLabelMsgApprovalCancel);
        logger.info("openDialog: {} {}", "key.Confirm", this.typeLabelMsgApprovalCancel);
    }

    /**
     * ダイアログを閉じる。
     */
    @Override
    public void closeDialog() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        String action = params.get("action");

        //RequestContext.getCurrentInstance().closeDialog(action);
        PrimeFaces.current().dialog().closeDynamic(action);
        logger.info("closeDialog: {} {}", id, action);
    }

    /**
     * 申請取消ボタン クリック時処理
     *
     * @param event イベント
     */
    public void postApplicationCancelRequest(SelectEvent event) {
        String result = (String) event.getObject();
        if (StringUtils.equals(LocaleUtils.getString("NO"), result)) {
            return;
        }

        this.approvalInfo.setComment(this.commentColumn);
        ResponseEntity res = this.approvalFlowModel.cancelApply(this.approvalInfo, this.orgId);

        switch (res.getErrorType()) {
            case SUCCESS:
                this.headerInfo = LocaleUtils.getString("key.success");
                this.messageInfo = LocaleUtils.getString("approval.compRequestCancel");
                this.commentColumn = "";
                break;
            case MAIL_AUTHENTICATION_FAILED:
                this.headerInfo = LocaleUtils.getString("key.Error");
                this.messageInfo = LocaleUtils.getString("approval.errSendRequestCancel");
                logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                break;
            default:
                this.headerInfo = LocaleUtils.getString("key.Error");
                this.messageInfo = LocaleUtils.getString("approval.errMsgRequestCancel");
                logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                break;
        }

        this.switchingButton(null);
        this.openMessageDialog();
    }

    /**
     * 承認ボタン クリック時処理
     *
     * @param event イベント
     */
    public void postApprovalRequest(SelectEvent event) {
        String result = (String) event.getObject();
        if (StringUtils.equals(LocaleUtils.getString("NO"), result)) {
            return;
        }

        Optional<ApprovalFlowEntity> entity = approvalInfo.getApprovalFlows().stream().filter((info) -> info.getApproverId().equals(this.orgId)).findFirst();
        if (entity.isPresent()) {
            ApprovalFlowEntity approvalFlow = entity.get();
            approvalFlow.setComment(this.commentColumn);
            approvalFlow.setApprovalDatetime(new Date());

            ResponseEntity res;
            if (approvalFlow.getApprovalFinal()) {
                res = approvalFlowModel.finalApprove(approvalFlow, orgId);
            } else {
                res = approvalFlowModel.approve(approvalFlow, orgId);
            }

            switch (res.getErrorType()) {
                case SUCCESS:
                    this.headerInfo = LocaleUtils.getString("key.success");
                    if (approvalFlow.getApprovalFinal()) {
                        this.messageInfo = LocaleUtils.getString("approval.compApprovalFinal");
                    } else {
                        this.messageInfo = LocaleUtils.getString("approval.compApproval");
                    }
                    this.commentColumn = "";
                    break;
                case MAIL_AUTHENTICATION_FAILED:
                    this.headerInfo = LocaleUtils.getString("key.Error");
                    this.messageInfo = LocaleUtils.getString("approval.errSendApproval");
                    logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                    break;
                default:
                    this.headerInfo = LocaleUtils.getString("key.Error");
                    if (approvalFlow.getApprovalFinal()) {
                        this.messageInfo = LocaleUtils.getString("approval.errMsgApprovalFinal");
                    } else {
                        this.messageInfo = LocaleUtils.getString("approval.errMsgApproval");
                    }
                    logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                    break;
            }

            this.switchingButton(getLoginApprovalFlow());
            this.openMessageDialog();
        }
    }

    /**
     * 却下ボタン クリック時処理
     *
     * @param event イベント
     */
    public void postCancelRequest(SelectEvent event) {
        String result = (String) event.getObject();
        if (StringUtils.equals(LocaleUtils.getString("NO"), result)) {
            return;
        }

        Optional<ApprovalFlowEntity> entity = approvalInfo.getApprovalFlows().stream().filter((info) -> info.getApproverId().equals(this.orgId)).findFirst();
        if (entity.isPresent()) {
            entity.get().setComment(this.commentColumn);
            entity.get().setApprovalDatetime(new Date());

            ResponseEntity res = approvalFlowModel.reject(entity.get(), orgId);

            switch (res.getErrorType()) {
                case SUCCESS:
                    this.headerInfo = LocaleUtils.getString("key.success");
                    this.messageInfo = LocaleUtils.getString("approval.compCancel");
                    this.commentColumn = "";
                    break;
                case MAIL_AUTHENTICATION_FAILED:
                    this.headerInfo = LocaleUtils.getString("key.Error");
                    this.messageInfo = LocaleUtils.getString("approval.errSendCancel");
                    logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                    break;
                default:
                    this.headerInfo = LocaleUtils.getString("key.Error");
                    this.messageInfo = LocaleUtils.getString("approval.errMsgCancel");
                    logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                    break;
            }

            this.switchingButton(getLoginApprovalFlow());
            this.openMessageDialog();
        }
    }

    /**
     * 承認取消ボタン クリック時処理
     *
     * @param event イベント
     */
    public void postApprovalCancelRequest(SelectEvent event) {
        String result = (String) event.getObject();
        if (StringUtils.equals(LocaleUtils.getString("NO"), result)) {
            return;
        }

        Optional<ApprovalFlowEntity> entity = approvalInfo.getApprovalFlows().stream().filter((info) -> info.getApproverId().equals(this.orgId)).findFirst();
        if (entity.isPresent()) {
            entity.get().setComment(this.commentColumn);
            entity.get().setApprovalDatetime(new Date());

            ResponseEntity res = approvalFlowModel.cancelApprove(entity.get(), orgId);

            switch (res.getErrorType()) {
                case SUCCESS:
                    this.headerInfo = LocaleUtils.getString("key.success");
                    this.messageInfo = LocaleUtils.getString("approval.compApprovalCancel");
                    this.commentColumn = "";
                    break;
                case MAIL_AUTHENTICATION_FAILED:
                    this.headerInfo = LocaleUtils.getString("key.Error");
                    this.messageInfo = LocaleUtils.getString("approval.errSendApprovalCancel");
                    logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                    break;
                default:
                    this.headerInfo = LocaleUtils.getString("key.Error");
                    this.messageInfo = LocaleUtils.getString("approval.errMsgApprovalCancel");
                    logger.fatal(this.messageInfo + ", ServerErrorType: " + res.getErrorType());
                    break;
            }
            this.switchingButton(getLoginApprovalFlow());
            this.openMessageDialog();
        }
    }

    /**
     * メッセージダイアログを開く。
     */
    private void openMessageDialog() {
        PrimeFaces.current().ajax().update("messageDialog");
        PrimeFaces.current().ajax().update("messageDialogPanel");
        PrimeFaces.current().executeScript("PF('messageDialog').show()");
    }

    /**
     * ログインユーザーの承認フロー情報を取得する
     *
     * @return ログインユーザーの承認フロー情報
     */
    private ApprovalFlowEntity getLoginApprovalFlow() {
        this.approvalInfo = approvalFlowModel.findApproval(this.applovalId);
        Optional<ApprovalFlowEntity> optApprovalFlowInfo = approvalInfo.getApprovalFlows().stream().filter((approval) -> loginUserInfo.getOrganizationId().equals(approval.getApproverId())).findFirst();

        if (optApprovalFlowInfo.isPresent()) {
            return optApprovalFlowInfo.get();
        }

        return null;
    }

    /**
     * 工程情報を設定する
     *
     * @param oldInfo 変更前の工程情報
     * @param newInfo 変更後の工程情報
     */
    private void addWorkInfo(WorkEntity oldInfo, WorkEntity newInfo) {
        List<DispApprovalChangeData> workInfos = new ArrayList<>();

        String oldRev = "";
        if (Objects.nonNull(oldInfo.getWorkRev())) {
            oldRev = oldInfo.getWorkRev().toString();
        }
        String newRev = "";
        if (Objects.nonNull(newInfo.getWorkRev())) {
            newRev = newInfo.getWorkRev().toString();
        }
        DispApprovalChangeData revisionEntity = setBasicChangeInfo(LocaleUtils.getString("approval.revision"), oldRev, newRev);
        workInfos.add(revisionEntity);

        DispApprovalChangeData workNumberEntity = setBasicChangeInfo(LocaleUtils.getString("approval.workNumber"), oldInfo.getWorkNumber(), newInfo.getWorkNumber());
        workInfos.add(workNumberEntity);

        String oldTaktTime = "";
        if (Objects.nonNull(oldInfo.getTaktTime())) {
            oldTaktTime = StringTime.convertMillisToStringTime(oldInfo.getTaktTime());
        }
        String newTaktTime = "";
        if (Objects.nonNull(newInfo.getTaktTime())) {
            newTaktTime = StringTime.convertMillisToStringTime(newInfo.getTaktTime());
        }
        DispApprovalChangeData taktTimeEntity = setBasicChangeInfo(LocaleUtils.getString("approval.taktTime"), oldTaktTime, newTaktTime);
        workInfos.add(taktTimeEntity);

        DispApprovalChangeData contentEntity = setBasicChangeInfo(LocaleUtils.getString("approval.content"), oldInfo.getContent(), newInfo.getContent());
        workInfos.add(contentEntity);

        DispApprovalChangeData backColorEntity = setBasicChangeInfo(LocaleUtils.getString("approval.backColor"), oldInfo.getBackColor(), newInfo.getBackColor());
        workInfos.add(backColorEntity);

        DispApprovalChangeData fontColorEntity = setBasicChangeInfo(LocaleUtils.getString("approval.fontColor"), oldInfo.getFontColor(), newInfo.getFontColor());
        workInfos.add(fontColorEntity);

        boolean isExistOldDisp = Objects.nonNull(oldInfo.getDisplayItems());
        List<DispAddInfoEntity> dispAddOldInfos = isExistOldDisp ? jsonToObjects(oldInfo.getDisplayItems(), DispAddInfoEntity[].class)
                : new ArrayList<>();
        List<DispAddInfoEntity> sortedOldDisp = dispAddOldInfos.stream().sorted(Comparator.comparing(DispAddInfoEntity::getOrder)).collect(Collectors.toList());
        String dispAddOldInfo = setTargetTypeDispAddInfo(sortedOldDisp).toString();

        boolean isExistNewDisp = Objects.nonNull(newInfo.getDisplayItems());
        List<DispAddInfoEntity> dispAddNewInfos = isExistNewDisp ? jsonToObjects(newInfo.getDisplayItems(), DispAddInfoEntity[].class)
                : new ArrayList<>();
        List<DispAddInfoEntity> sortedNewDisp = dispAddNewInfos.stream().sorted(Comparator.comparing(DispAddInfoEntity::getOrder)).collect(Collectors.toList());
        String dispAddNewInfo = setTargetTypeDispAddInfo(sortedNewDisp).toString();

        DispApprovalChangeData displayItemsEntity = setBasicChangeInfo(LocaleUtils.getString("approval.displayItems"), dispAddOldInfo, dispAddNewInfo);
        workInfos.add(displayItemsEntity);

        List<String> useOldInfoList = new ArrayList<>();
        if (Objects.nonNull(oldInfo.getUseParts())) {
            useOldInfoList = Arrays.asList(oldInfo.getUseParts().split("\\s*,\\s*"));
        }
        String useOldInfo = "";
        useOldInfo = useOldInfoList.stream().map((info) -> info + "\n").reduce(useOldInfo, String::concat);

        List<String> useNewInfoList = new ArrayList<>();
        if (Objects.nonNull(newInfo.getUseParts())) {
            useNewInfoList = Arrays.asList(newInfo.getUseParts().split("\\s*,\\s*"));
        }
        String useNewInfo = "";
        useNewInfo = useNewInfoList.stream().map((info) -> info + "\n").reduce(useNewInfo, String::concat);

        DispApprovalChangeData userPartsEntity = setBasicChangeInfo(LocaleUtils.getString("approval.userParts"), useOldInfo, useNewInfo);
        workInfos.add(userPartsEntity);

        boolean isExistOldAdd = Objects.nonNull(oldInfo.getWorkAddInfo());
        List<AddInfoEntity> addOldInfos = isExistOldAdd ? jsonToObjects(oldInfo.getWorkAddInfo(), AddInfoEntity[].class)
                : new ArrayList<>();
        String addOldInfo = "";
        addOldInfo = addOldInfos.stream().map((info) -> info.getKey() + "\n    " + LocaleUtils.getString(info.getType().getResourceKey()) + "\n    " + info.getVal() + "\n").reduce(addOldInfo, String::concat);

        boolean isExistNewAdd = Objects.nonNull(newInfo.getWorkAddInfo());
        List<AddInfoEntity> addNewInfos = isExistNewAdd ? jsonToObjects(newInfo.getWorkAddInfo(), AddInfoEntity[].class)
                : new ArrayList<>();
        String addNewInfo = "";
        addNewInfo = addNewInfos.stream().map((info) -> info.getKey() + "\n    " + LocaleUtils.getString(info.getType().getResourceKey()) + "\n    " + info.getVal() + "\n").reduce(addNewInfo, String::concat);

        DispApprovalChangeData addInfoEntity = setBasicChangeInfo(LocaleUtils.getString("approval.addInfo"), addOldInfo, addNewInfo);
        workInfos.add(addInfoEntity);

        workInfos.forEach((info) -> {
            if (info.getApprovalOld().equals(info.getApprovalNew())) {
                info.setApprovalColor(BLACK);
            } else {
                info.setApprovalColor(RED);
            }
        });

        this.workInfo = workInfos;

        // 工程セクション情報
        List<WorkSectionEntity> sectionOldList = oldInfo.getWorkSectionCollection();
        List<WorkSectionEntity> sectionNewList = newInfo.getWorkSectionCollection();

        //　表示用項目情報
        List<DispWorkInfoData> workDispList = new ArrayList<>();

        // 品質トレーサビリティ
        boolean isExistOldCheck = Objects.nonNull(oldInfo.getWorkCheckInfo());
        List<CheckInfoEntity> oldCheckInfo = isExistOldCheck ? jsonToObjects(oldInfo.getWorkCheckInfo(), CheckInfoEntity[].class)
                : new ArrayList<>();

        boolean isExistNewCheck = Objects.nonNull(newInfo.getWorkCheckInfo());
        List<CheckInfoEntity> newCheckInfo = isExistNewCheck ? jsonToObjects(newInfo.getWorkCheckInfo(), CheckInfoEntity[].class)
                : new ArrayList<>();

        // 工程セクション情報(変更前)を軸に、工程セクション情報(変更後)との突合せを行う
        if (Objects.nonNull(sectionOldList)) {
            sectionOldList.forEach((oldSection) -> {
                Optional<WorkSectionEntity> optSection;
                if (Objects.nonNull(sectionNewList)) {
                    optSection = sectionNewList.stream().filter((info) -> Objects.equals(oldSection.getWorkSectionOrder(), info.getWorkSectionOrder())).findFirst();
                } else {
                    optSection = Optional.empty();
                }
                List<DispApprovalChangeData> sheetList = new ArrayList<>();
                // シート情報
                DispWorkInfoData workDisp = new DispWorkInfoData();

                if (optSection.isPresent()) {
                    // 変更前後がともに存在する
                    WorkSectionEntity newSection = optSection.get();

                    //シート名
                    if (Objects.isNull(oldSection.getDocumentTitle())) {
                        workDisp.setSheetName("");
                    } else {
                        workDisp.setSheetName(oldSection.getDocumentTitle());
                    }

                    //ドキュメント名
                    DispApprovalChangeData documentInfo = setDocumentChangeInfo(typeLabelDocumentName, oldSection.getFileName(), newSection.getFileName());
                    sheetList.add(documentInfo);

                    // ページ番号
                    String oldPageNum = StringUtils.endsWith(oldSection.getFileName(), "pdf") && Objects.nonNull(oldSection.getPageNum()) ? String.valueOf(oldSection.getPageNum() + 1) : "";
                    String newPageNum = StringUtils.endsWith(newSection.getFileName(), "pdf") && Objects.nonNull(newSection.getPageNum()) ? String.valueOf(newSection.getPageNum() + 1) : "";
                    sheetList.add(setDocumentChangeInfo(typeLabelPageNum, oldPageNum, newPageNum));

                    //更新日時
                    String oldUpdate = "";
                    if (Objects.nonNull(oldSection.getFileUpdated())) {
                        oldUpdate = StringTime.convertDateToString(oldSection.getFileUpdated(), DATE_FORMAT);
                    }

                    String newUpdate = "";
                    if (Objects.nonNull(newSection.getFileUpdated())) {
                        newUpdate = StringTime.convertDateToString(newSection.getFileUpdated(), DATE_FORMAT);
                    }

                    DispApprovalChangeData fileUpdateInfo = setDocumentChangeInfo(typeLabelUpdateDateTime, oldUpdate, newUpdate);

                    sheetList.add(fileUpdateInfo);
                    workDisp.setSheetInfo(sheetList);

                    if (Objects.isNull(oldSection.getWorkSectionOrder())) {
                        workDisp.setPageNum(0);
                    } else {
                        workDisp.setPageNum(oldSection.getWorkSectionOrder());
                    }

                    List<List<DispApprovalChangeData>> traceabilityList = getTraceabilityList(oldCheckInfo, newCheckInfo, oldSection.getWorkSectionOrder());
                    workDisp.setDispInfo(traceabilityList);
                    workDispList.add(workDisp);
                } else {
                    // 変更前のみ存在する

                    //シート名
                    if (Objects.isNull(oldSection.getDocumentTitle())) {
                        workDisp.setSheetName("");
                    } else {
                        workDisp.setSheetName(oldSection.getDocumentTitle());
                    }

                    //ドキュメント名
                    DispApprovalChangeData documentInfo = setDocumentChangeInfo(typeLabelDocumentName, oldSection.getFileName(), "");
                    sheetList.add(documentInfo);

                    // ページ番号
                    String oldPageNum = StringUtils.endsWith(oldSection.getFileName(), "pdf") && Objects.nonNull(oldSection.getPageNum()) ? String.valueOf(oldSection.getPageNum() + 1) : "";
                    sheetList.add(setDocumentChangeInfo(typeLabelPageNum, oldPageNum, ""));

                    //更新日時
                    String udpateTime = "";
                    if (Objects.nonNull(oldSection.getFileUpdated())) {
                        udpateTime = StringTime.convertDateToString(oldSection.getFileUpdated(), DATE_FORMAT);
                    }

                    DispApprovalChangeData fileUpdateInfo = setDocumentChangeInfo(typeLabelUpdateDateTime, udpateTime, "");

                    sheetList.add(fileUpdateInfo);
                    workDisp.setSheetInfo(sheetList);

                    if (Objects.isNull(oldSection.getWorkSectionOrder())) {
                        workDisp.setPageNum(0);
                    } else {
                        workDisp.setPageNum(oldSection.getWorkSectionOrder());
                    }

                    List<List<DispApprovalChangeData>> traceabilityList = getTraceabilityList(oldCheckInfo, newCheckInfo, oldSection.getWorkSectionOrder());
                    workDisp.setDispInfo(traceabilityList);
                    workDispList.add(workDisp);
                }
            });
        }

        // 工程セクション情報(変更後)を軸に、工程セクション情報(変更前)との突合せを行う
        if (Objects.nonNull(sectionNewList)) {
            sectionNewList.forEach((newSection) -> {
                Optional<WorkSectionEntity> optSection;
                if (Objects.nonNull(sectionOldList)) {
                    optSection = sectionOldList.stream().filter((info) -> Objects.equals(newSection.getWorkSectionOrder(), info.getWorkSectionOrder())).findFirst();
                } else {
                    optSection = Optional.empty();
                }

                List<DispApprovalChangeData> sheetList = new ArrayList<>();
                DispWorkInfoData workDisp = new DispWorkInfoData();
                if (!optSection.isPresent()) {
                    // 変更後のみ存在する

                    //シート名
                    if (Objects.isNull(newSection.getDocumentTitle())) {
                        workDisp.setSheetName("");
                    } else {
                        workDisp.setSheetName(newSection.getDocumentTitle());
                    }

                    //ドキュメント名
                    DispApprovalChangeData documentInfo = setDocumentChangeInfo(typeLabelDocumentName, "", newSection.getFileName());
                    sheetList.add(documentInfo);

                    // ページ番号
                    String newPageNum = StringUtils.endsWith(newSection.getFileName(), "pdf") && Objects.nonNull(newSection.getPageNum()) ? String.valueOf(newSection.getPageNum() + 1) : "";
                    sheetList.add(setDocumentChangeInfo(typeLabelPageNum, "", newPageNum));

                    //更新日時
                    String update = "";
                    if (Objects.nonNull(newSection.getFileUpdated())) {
                        update = StringTime.convertDateToString(newSection.getFileUpdated(), DATE_FORMAT);
                    }
                    DispApprovalChangeData fileUpdateInfo = setDocumentChangeInfo(typeLabelUpdateDateTime, "", update);

                    sheetList.add(fileUpdateInfo);
                    workDisp.setSheetInfo(sheetList);

                    if (Objects.isNull(newSection.getWorkSectionOrder())) {
                        workDisp.setPageNum(0);
                    } else {
                        workDisp.setPageNum(newSection.getWorkSectionOrder());
                    }

                    List<List<DispApprovalChangeData>> traceabilityList = getTraceabilityList(oldCheckInfo, newCheckInfo, newSection.getWorkSectionOrder());
                    workDisp.setDispInfo(traceabilityList);
                    workDispList.add(workDisp);
                }
            });
        }

        workDispList.sort(Comparator.comparing(item -> item.getPageNum()));
        this.sectionInfo = workDispList;
    }

    /**
     * 表示項目設定
     *
     * @param sortedDisp 表示項目一覧
     * @return 表示項目文字列
     */
    private StringBuilder setTargetTypeDispAddInfo(List<DispAddInfoEntity> sortedDisp) {
        StringBuilder dispAddNewInfo = new StringBuilder();
        for (DispAddInfoEntity info : sortedDisp) {
            switch (info.getTarget()) {
                case WORK:
                    dispAddNewInfo.append(LocaleUtils.getString("key.Process")).append(COLON);
                    break;
                case WORKKANBAN:
                    dispAddNewInfo.append(LocaleUtils.getString("key.WorkKanban")).append(COLON);
                    break;
                case KANBAN:
                    dispAddNewInfo.append(LocaleUtils.getString("key.Kanban")).append(COLON);
                    break;
                default:
                    break;
            }
            dispAddNewInfo.append(info.getName()).append("\n");
        }
        return dispAddNewInfo;
    }

    /**
     * 工程順情報を設定する
     *
     * @param oldInfo 変更前の工程順情報
     * @param newInfo 変更後の工程順情報
     */
    private void addWorkflowInfo(WorkflowEntity oldInfo, WorkflowEntity newInfo) {
        List<DispApprovalChangeData> workflowInfos = new ArrayList<>();

        String oldRev = "";
        if (Objects.nonNull(oldInfo.getWorkflowRev())) {
            oldRev = oldInfo.getWorkflowRev().toString();
        }
        String newRev = "";
        if (Objects.nonNull(newInfo.getWorkflowRev())) {
            newRev = newInfo.getWorkflowRev().toString();
        }
        DispApprovalChangeData revisionEntity = setBasicChangeInfo(LocaleUtils.getString("approval.revision"), oldRev, newRev);
        workflowInfos.add(revisionEntity);

        DispApprovalChangeData modelNameEntity = setBasicChangeInfo(LocaleUtils.getString("approval.modelName"), oldInfo.getModelName(), newInfo.getModelName());
        workflowInfos.add(modelNameEntity);

        DispApprovalChangeData workNumberNameEntity = setBasicChangeInfo(LocaleUtils.getString("approval.workNumber"), oldInfo.getWorkflowNumber(), newInfo.getWorkflowNumber());
        workflowInfos.add(workNumberNameEntity);

        StringBuilder oldWorkingTime = new StringBuilder();
        if (Objects.nonNull(oldInfo.getOpenTime())) {
            oldWorkingTime.append(StringTime.convertDateToString(oldInfo.getOpenTime(), TIME_FORMAT_HHMM)).append(" - ");
        }
        if (Objects.nonNull(oldInfo.getCloseTime())) {
            if (Objects.isNull(oldInfo.getOpenTime())) {
                oldWorkingTime.append(" - ");
            }
            oldWorkingTime.append(StringTime.convertDateToString(oldInfo.getCloseTime(), TIME_FORMAT_HHMM));
        }
        StringBuilder newWorkingTime = new StringBuilder();
        if (Objects.nonNull(newInfo.getOpenTime())) {
            newWorkingTime.append(StringTime.convertDateToString(newInfo.getOpenTime(), TIME_FORMAT_HHMM)).append(" - ");
        }
        if (Objects.nonNull(newInfo.getCloseTime())) {
            if (Objects.isNull(newInfo.getOpenTime())) {
                newWorkingTime.append(" - ");
            }
            newWorkingTime.append(StringTime.convertDateToString(newInfo.getCloseTime(), TIME_FORMAT_HHMM));
        }
        DispApprovalChangeData workingTimeFrameEntity = setBasicChangeInfo(LocaleUtils.getString("approval.workingTimeFrame"), oldWorkingTime.toString(), newWorkingTime.toString());
        workflowInfos.add(workingTimeFrameEntity);

        String oldSchedulePolicy = "";
        String newSchedulePolicy = "";
        if (Objects.nonNull(oldInfo.getApprovalId())) {
            oldSchedulePolicy = LocaleUtils.getString(oldInfo.getSchedulePolicy().getResourceKey());
        }
        if (Objects.nonNull(newInfo.getApprovalId())) {
            newSchedulePolicy = LocaleUtils.getString(newInfo.getSchedulePolicy().getResourceKey());
        }

        DispApprovalChangeData workOrderEntity
                = setBasicChangeInfo(LocaleUtils.getString("approval.workOrder"), oldSchedulePolicy, newSchedulePolicy);
        workflowInfos.add(workOrderEntity);

        StringBuilder oldLedgerPathes = new StringBuilder();
        if (Objects.nonNull(oldInfo.getLedgerPath())) {
            List<String> oldPathes = Arrays.asList(oldInfo.getLedgerPath().split("\\|"));
            oldPathes.forEach((ledgerPath) -> {
                oldLedgerPathes.append(ledgerPath);
                if (oldPathes.indexOf(ledgerPath) != oldPathes.size() - 1) {
                    oldLedgerPathes.append("\n");
                }
            });
        }
        StringBuilder newLedgerPathes = new StringBuilder();
        if (Objects.nonNull(newInfo.getLedgerPath())) {
            List<String> newPathes = Arrays.asList(newInfo.getLedgerPath().split("\\|"));
            newPathes.forEach((ledgerPath) -> {
                newLedgerPathes.append(ledgerPath);
                if (newPathes.indexOf(ledgerPath) != newPathes.size() - 1) {
                    newLedgerPathes.append("\n");
                }
            });
        }

        DispApprovalChangeData ledgerPathEntity = setBasicChangeInfo(LocaleUtils.getString("approval.ledgerPath"), oldLedgerPathes.toString(), newLedgerPathes.toString());
        workflowInfos.add(ledgerPathEntity);

        boolean isExistOldAdd = Objects.nonNull(oldInfo.getWorkflowAddInfo());
        List<AddInfoEntity> addOldInfos = isExistOldAdd ? jsonToObjects(oldInfo.getWorkflowAddInfo(), AddInfoEntity[].class)
                : new ArrayList<>();
        String addOldInfo = "";
        addOldInfo = addOldInfos.stream().map((info) -> info.getKey() + "\n    " + info.getType() + "\n    " + info.getVal() + "\n").reduce(addOldInfo, String::concat);

        boolean isExistNewAdd = Objects.nonNull(newInfo.getWorkflowAddInfo());
        List<AddInfoEntity> addNewInfos = isExistNewAdd ? jsonToObjects(newInfo.getWorkflowAddInfo(), AddInfoEntity[].class)
                : new ArrayList<>();
        String addNewInfo = "";
        addNewInfo = addNewInfos.stream().map((info) -> info.getKey() + "\n    " + info.getType() + "\n    " + info.getVal() + "\n").reduce(addNewInfo, String::concat);

        DispApprovalChangeData addInfoEntity = setBasicChangeInfo(LocaleUtils.getString("approval.addInfo"), addOldInfo, addNewInfo);
        workflowInfos.add(addInfoEntity);

        workflowInfos.forEach((info) -> {
            if (info.getApprovalOld().equals(info.getApprovalNew())) {
                info.setApprovalColor(BLACK);
            } else {
                info.setApprovalColor(RED);
            }
        });

        this.workflowInfo = workflowInfos;

        // プロセスフローの比較結果
        this.processFlow = this.compareProcessFlow(oldInfo.getConWorkflowWorkCollection(), newInfo.getConWorkflowWorkCollection());

        // 追加工程の比較結果
        this.separateWork = this.compareProcessFlow(oldInfo.getConWorkflowSeparateworkCollection(), newInfo.getConWorkflowSeparateworkCollection());
    }

    /**
     * プロセスフローを比較する。
     *
     * @param oldList 変更前の工程関連付け情報一覧
     * @param newList 変更後の工程関連付け情報一覧
     * @return 変更前後のプロセスフロー情報
     */
    private List<List<DispApprovalChangeData>> compareProcessFlow(List<ConWorkflowWorkEntity> oldList, List<ConWorkflowWorkEntity> newList) {
        List<List<DispApprovalChangeData>> resultList = new ArrayList<>();

        String labelWorkName = LocaleUtils.getString("key.ProcessName");
        String labelRev = LocaleUtils.getString("approval.revision");
        String labelOrgnizaion = LocaleUtils.getString("key.orgnizaiton");
        String labelEquipment = LocaleUtils.getString("key.equipment");
        String labelSkip = LocaleUtils.getString("key.skip");
        String labelExec = LocaleUtils.getString("key.exec");
        String labelNoExec = LocaleUtils.getString("key.noExec");
        String labelStdDays = LocaleUtils.getString("key.standardDays");
        String labelStdTime = LocaleUtils.getString("key.standardTime");

        SimpleDateFormat daysFormat = new SimpleDateFormat("D");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        // 組織・設備情報を取得
        Set<Long> orgIds = new HashSet<>();
        Set<Long> equipIds = new HashSet<>();
        if (Objects.nonNull(oldList)) {
            oldList.stream().forEach(o -> {
                orgIds.addAll(o.getOrganizationCollection());
                equipIds.addAll(o.getEquipmentCollection());
            });
        }
        if (Objects.nonNull(newList)) {
            newList.stream().forEach(o -> {
                orgIds.addAll(o.getOrganizationCollection());
                equipIds.addAll(o.getEquipmentCollection());
            });
        }

        Map<Long, String> orgMap = this.approvalFlowModel.findOrganizationByIds(new ArrayList<>(orgIds))
                .stream().collect(Collectors.toMap(o -> o.getOrganizationId(), o -> o.getOrganizationName()));

        Map<Long, String> equipMap = this.approvalFlowModel.findEquipmentByIds(new ArrayList<>(equipIds))
                .stream().collect(Collectors.toMap(e -> e.getEquipmentId(), e -> e.getEquipmentName()));

        // 表示順の並べ替えは不要
        int index = 0;

        if (Objects.nonNull(oldList)) {
            for (index = 0; index < oldList.size(); index++) {
                List<DispApprovalChangeData> result = new ArrayList<>();
                ConWorkflowWorkEntity oldValue = oldList.get(index);
                WorkEntity oldWork = this.approvalFlowModel.findWork(oldValue.getWorkId());

                if (index < newList.size()) {
                    // 変更前後ともに存在する
                    ConWorkflowWorkEntity newValue = newList.get(index);
                    WorkEntity newWork = this.approvalFlowModel.findWork(newValue.getWorkId());

                    // 工程名
                    result.add(setBasicChangeInfo(labelWorkName, oldValue.getWorkName(), newValue.getWorkName()));

                    // リビジョン
                    result.add(setBasicChangeInfo(labelRev, String.valueOf(oldValue.getWorkRev()), String.valueOf(newValue.getWorkRev())));

                    // 承認
                    result.add(setBasicChangeInfo(this.typeLabelApproval, toApprovalStatus(oldWork.getApprovalState()), toApprovalStatus(newWork.getApprovalState())));

                    // 標準作業日
                    result.add(setBasicChangeInfo(labelStdDays, daysFormat.format(oldValue.getStandardStartTime()), daysFormat.format(newValue.getStandardStartTime())));

                    // 標準作業時間
                    String oldTime = timeFormat.format(oldValue.getStandardStartTime()) + " - " + timeFormat.format(oldValue.getStandardEndTime());
                    String newTime = timeFormat.format(newValue.getStandardStartTime()) + " - " + timeFormat.format(newValue.getStandardEndTime());
                    result.add(setBasicChangeInfo(labelStdTime, oldTime, newTime));

                    // スキップ
                    result.add(setBasicChangeInfo(labelSkip, (oldValue.getSkipFlag() ? labelExec : labelNoExec), (newValue.getSkipFlag() ? labelExec : labelNoExec)));

                    // 設備名
                    String[] oldEquip = oldValue.getEquipmentCollection().stream().sorted()
                            .map(o -> equipMap.containsKey(o) ? equipMap.get(o) : "").toArray(String[]::new);
                    String[] newEquip = newValue.getEquipmentCollection().stream().sorted()
                            .map(o -> equipMap.containsKey(o) ? equipMap.get(o) : "").toArray(String[]::new);
                    result.add(setBasicChangeInfo(labelEquipment, String.join("\n", oldEquip), String.join("\n", newEquip)));

                    // 組織名
                    String[] oldOrg = oldValue.getOrganizationCollection().stream().sorted()
                            .map(o -> orgMap.containsKey(o) ? orgMap.get(o) : "").toArray(String[]::new);
                    String[] newOrg = newValue.getOrganizationCollection().stream().sorted()
                            .map(o -> orgMap.containsKey(o) ? orgMap.get(o) : "").toArray(String[]::new);
                    result.add(setBasicChangeInfo(labelOrgnizaion, String.join("\n", oldOrg), String.join("\n", newOrg)));

                } else {
                    // 変更前しか存在しない
                    // 工程名
                    result.add(setBasicChangeInfo(labelWorkName, oldValue.getWorkName(), ""));

                    // リビジョン
                    result.add(setBasicChangeInfo(labelRev, String.valueOf(oldValue.getWorkRev()), ""));

                    // 承認
                    result.add(setBasicChangeInfo(this.typeLabelApproval, toApprovalStatus(oldWork.getApprovalState()), ""));

                    // 標準作業日
                    result.add(setBasicChangeInfo(labelStdDays, daysFormat.format(oldValue.getStandardStartTime()), ""));

                    // 標準作業時間
                    String oldTime = timeFormat.format(oldValue.getStandardStartTime()) + " - " + timeFormat.format(oldValue.getStandardEndTime());
                    result.add(setBasicChangeInfo(labelStdTime, oldTime, ""));

                    // スキップ
                    result.add(setBasicChangeInfo(labelSkip, (oldValue.getSkipFlag() ? labelExec : labelNoExec), ""));

                    // 設備名
                    String[] oldEquip = oldValue.getEquipmentCollection().stream().sorted()
                            .map(o -> equipMap.containsKey(o) ? equipMap.get(o) : "").toArray(String[]::new);
                    result.add(setBasicChangeInfo(labelEquipment, String.join("\n", oldEquip), ""));

                    // 組織名
                    String[] oldOrg = oldValue.getOrganizationCollection().stream().sorted()
                            .map(o -> orgMap.containsKey(o) ? orgMap.get(o) : "").toArray(String[]::new);
                    result.add(setBasicChangeInfo(labelOrgnizaion, String.join("\n", oldOrg), ""));
                }

                resultList.add(result);
            }
        }

        if (Objects.nonNull(newList)) {
            for (; index < newList.size(); index++) {
                // 変更後しか存在しない
                List<DispApprovalChangeData> result = new ArrayList<>();
                ConWorkflowWorkEntity newValue = newList.get(index);
                WorkEntity newWork = this.approvalFlowModel.findWork(newValue.getWorkId());

                // 工程名
                result.add(setBasicChangeInfo(labelWorkName, "", newValue.getWorkName()));

                // リビジョン
                result.add(setBasicChangeInfo(labelRev, "", String.valueOf(newValue.getWorkRev())));

                // 承認
                result.add(setBasicChangeInfo(this.typeLabelApproval, "", toApprovalStatus(newWork.getApprovalState())));

                // 標準作業日
                result.add(setBasicChangeInfo(labelStdDays, "", daysFormat.format(newValue.getStandardStartTime())));

                // 標準作業時間
                String newTime = timeFormat.format(newValue.getStandardStartTime()) + " - " + timeFormat.format(newValue.getStandardEndTime());
                result.add(setBasicChangeInfo(labelStdTime, "", newTime));

                // スキップ
                result.add(setBasicChangeInfo(labelSkip, "", (newValue.getSkipFlag() ? labelExec : labelNoExec)));

                // 設備名
                String[] newEquip = newValue.getEquipmentCollection().stream().sorted()
                        .map(o -> equipMap.containsKey(o) ? equipMap.get(o) : "").toArray(String[]::new);
                result.add(setBasicChangeInfo(labelEquipment, "", String.join("\n", newEquip)));

                // 組織名
                String[] newOrg = newValue.getOrganizationCollection().stream().sorted()
                        .map(o -> orgMap.containsKey(o) ? orgMap.get(o) : "").toArray(String[]::new);
                result.add(setBasicChangeInfo(labelOrgnizaion, "", String.join("\n", newOrg)));

                resultList.add(result);
            }
        }

        resultList.forEach(list -> {
            list.forEach(o -> {
                if (o.getApprovalOld().equals(o.getApprovalNew())) {
                    o.setApprovalColor(BLACK);
                } else {
                    o.setApprovalColor(RED);
                }
            });
        });

        return resultList;
    }

    /**
     * 承認状態を表示名に変換する。
     *
     * @param status 承認状態
     * @return 表示名
     */
    private String toApprovalStatus(ApprovalStatusEnum status) {
        String name = null;
        switch (status) {
            case UNAPPROVED:        // 未承認
            case APPLY:             // 申請中
            case CANCEL_APPLY:      // 申請取消
            case REJECT:            // 却下
            default:
                name = LocaleUtils.getString("approval.unapproved");
                break;
            case FINAL_APPROVE:     // 最終承認済
                name = LocaleUtils.getString("approval.finalApproved");
                break;
        }
        return name;
    }

    /**
     * トレーサビリティ情報を設定する
     *
     * @param oldCheckInfo 変更前の検査情報
     * @param newCheckInfo 変更後の検査情報
     * @param pageNum 表示順
     * @return 変更前後の検査情報リスト(画面表示用)
     */
    private List<List<DispApprovalChangeData>> getTraceabilityList(List<CheckInfoEntity> oldCheckInfo, List<CheckInfoEntity> newCheckInfo, Integer pageNum) {
        List<List<DispApprovalChangeData>> traceablilityLists = new ArrayList<>();

        dispIndex = 1;
        List<CheckInfoEntity> oldCheckInfos = oldCheckInfo.stream().filter((info) -> Objects.equals(info.getPage(), pageNum))
                .sorted(Comparator.comparing(CheckInfoEntity::getDisp))
                .map((p) -> {
                    p.setDisp(dispIndex);
                    dispIndex++;
                    return p;
                }).collect(Collectors.toList());

        dispIndex = 1;
        List<CheckInfoEntity> newCheckInfos = newCheckInfo.stream().filter((info) -> Objects.equals(info.getPage(), pageNum))
                .sorted(Comparator.comparing(CheckInfoEntity::getDisp))
                .map((p) -> {
                    p.setDisp(dispIndex);
                    dispIndex++;
                    return p;
                }).collect(Collectors.toList());

        // 検査情報(変更前)を軸に、検査情報(変更後)との突合せを行う
        oldCheckInfos.forEach((oldCheck) -> {
            Optional<CheckInfoEntity> optCheckInfo = newCheckInfos.stream().filter((info) -> Objects.equals(oldCheck.getDisp(), info.getDisp())).findFirst();
            List<DispApprovalChangeData> traceabilityList = new ArrayList<>();
            if (optCheckInfo.isPresent()) {
                // 変更前後がともに存在する
                CheckInfoEntity newCheck = optCheckInfo.get();

                DispApprovalChangeData catEntity = setBasicChangeInfo(typeLabelCat, getTraceabilityType(oldCheck), getTraceabilityType(newCheck));
                traceabilityList.add(catEntity);

                DispApprovalChangeData keyEntity = setBasicChangeInfo(typeLabelKey, oldCheck.getKey(), newCheck.getKey());
                traceabilityList.add(keyEntity);

                DispApprovalChangeData valEntity = setBasicChangeInfo(typeLabelVal, oldCheck.getVal(), newCheck.getVal());
                traceabilityList.add(valEntity);

                StringBuilder oldStandard = new StringBuilder();
                if (Objects.nonNull(oldCheck.getMin()) && !oldCheck.getMin().isNaN()) {
                    oldStandard.append(oldCheck.getMin());
                    oldStandard.append(" - ");
                }
                if (Objects.nonNull(oldCheck.getMax()) && !oldCheck.getMax().isNaN()) {
                    if (Objects.isNull(oldCheck.getMin()) || oldCheck.getMin().isNaN()) {
                        oldStandard.append("　 - ");
                    }
                    oldStandard.append(oldCheck.getMax());
                }

                StringBuilder newStandard = new StringBuilder();
                if (Objects.nonNull(newCheck.getMin()) && !newCheck.getMin().isNaN()) {
                    newStandard.append(newCheck.getMin());
                    newStandard.append(" - ");
                }
                if (Objects.nonNull(newCheck.getMax()) && !newCheck.getMax().isNaN()) {
                    if (Objects.isNull(newCheck.getMin()) || newCheck.getMin().isNaN()) {
                        newStandard.append("　 - ");
                    }
                    newStandard.append(newCheck.getMax());
                }
                DispApprovalChangeData standardEntity = setBasicChangeInfo(typeLabelStandard, oldStandard.toString(), newStandard.toString());
                traceabilityList.add(standardEntity);

                DispApprovalChangeData rulesEntity = setBasicChangeInfo(typeLabelRules, oldCheck.getRules(), newCheck.getRules());
                traceabilityList.add(rulesEntity);

                DispApprovalChangeData optEntity = setBasicChangeInfo(typeLabelOpt, getTraceOption(oldCheck), getTraceOption(newCheck));
                traceabilityList.add(optEntity);

                DispApprovalChangeData cpEntity = setBasicChangeInfo(typeLabelCp,
                        StringTime.convertMillisToStringTime(oldCheck.getCp()),
                        StringTime.convertMillisToStringTime(newCheck.getCp()));
                traceabilityList.add(cpEntity);

                DispApprovalChangeData tagEntity = setBasicChangeInfo(typeLabelTag, oldCheck.getTag(), newCheck.getTag());
                traceabilityList.add(tagEntity);

                traceablilityLists.add(traceabilityList);
            } else {
                // 変更前のみ存在する
                DispApprovalChangeData catEntity = setBasicChangeInfo(typeLabelCat, getTraceabilityType(oldCheck), "");
                traceabilityList.add(catEntity);

                DispApprovalChangeData keyEntity = setBasicChangeInfo(typeLabelKey, oldCheck.getKey(), "");
                traceabilityList.add(keyEntity);

                DispApprovalChangeData valEntity = setBasicChangeInfo(typeLabelVal, oldCheck.getVal(), "");
                traceabilityList.add(valEntity);

                StringBuilder oldStandard = new StringBuilder();
                if (Objects.nonNull(oldCheck.getMin()) && !oldCheck.getMin().isNaN()) {
                    oldStandard.append(oldCheck.getMin());
                    oldStandard.append(" - ");
                }
                if (Objects.nonNull(oldCheck.getMax()) && !oldCheck.getMax().isNaN()) {
                    if (Objects.isNull(oldCheck.getMin()) || oldCheck.getMin().isNaN()) {
                        oldStandard.append("　 - ");
                    }
                    oldStandard.append(oldCheck.getMax());
                }

                DispApprovalChangeData standardEntity = setBasicChangeInfo(typeLabelStandard, oldStandard.toString(), "");
                traceabilityList.add(standardEntity);

                DispApprovalChangeData rulesEntity = setBasicChangeInfo(typeLabelRules, oldCheck.getRules(), "");
                traceabilityList.add(rulesEntity);

                DispApprovalChangeData optEntity = setBasicChangeInfo(typeLabelOpt, getTraceOption(oldCheck), "");
                traceabilityList.add(optEntity);

                DispApprovalChangeData cpEntity = setBasicChangeInfo(typeLabelCp, StringTime.convertMillisToStringTime(oldCheck.getCp()), "");
                traceabilityList.add(cpEntity);

                DispApprovalChangeData tagEntity = setBasicChangeInfo(typeLabelTag, oldCheck.getTag(), "");
                traceabilityList.add(tagEntity);

                traceablilityLists.add(traceabilityList);
            }
        });

        // 検査情報(変更後)を軸に、検査情報(変更前)との突合せを行う
        newCheckInfos.forEach((newCheck) -> {
            Optional<CheckInfoEntity> optCheckInfo = oldCheckInfos.stream().filter((info) -> Objects.equals(newCheck.getDisp(), info.getDisp())).findFirst();
            List<DispApprovalChangeData> traceabilityList = new ArrayList<>();
            if (!optCheckInfo.isPresent()) {
                // 変更後のみ存在する
                DispApprovalChangeData catEntity = setBasicChangeInfo(typeLabelCat, "", getTraceabilityType(newCheck));
                traceabilityList.add(catEntity);

                DispApprovalChangeData keyEntity = setBasicChangeInfo(typeLabelKey, "", newCheck.getKey());
                traceabilityList.add(keyEntity);

                DispApprovalChangeData valEntity = setBasicChangeInfo(typeLabelVal, "", newCheck.getVal());
                traceabilityList.add(valEntity);

                StringBuilder newStandard = new StringBuilder();
                if (Objects.nonNull(newCheck.getMin()) && !newCheck.getMin().isNaN()) {
                    newStandard.append(newCheck.getMin());
                    newStandard.append(" - ");
                }
                if (Objects.nonNull(newCheck.getMax()) && !newCheck.getMax().isNaN()) {
                    if (Objects.isNull(newCheck.getMin()) || newCheck.getMin().isNaN()) {
                        newStandard.append("　 - ");
                    }
                    newStandard.append(newCheck.getMax());
                }

                DispApprovalChangeData standardEntity = setBasicChangeInfo(typeLabelStandard, "", newStandard.toString());
                traceabilityList.add(standardEntity);

                DispApprovalChangeData rulesEntity = setBasicChangeInfo(typeLabelRules, "", newCheck.getRules());
                traceabilityList.add(rulesEntity);

                DispApprovalChangeData optEntity = setBasicChangeInfo(typeLabelOpt, "", getTraceOption(newCheck));
                traceabilityList.add(optEntity);

                DispApprovalChangeData cpEntity = setBasicChangeInfo(typeLabelCp, "", StringTime.convertMillisToStringTime(newCheck.getCp()));
                traceabilityList.add(cpEntity);

                DispApprovalChangeData tagEntity = setBasicChangeInfo(typeLabelTag, "", newCheck.getTag());
                traceabilityList.add(tagEntity);

                traceablilityLists.add(traceabilityList);
            }
        });

        traceablilityLists.forEach((list) -> {
            list.forEach((info) -> {
                if (info.getApprovalOld().equals(info.getApprovalNew())) {
                    info.setApprovalColor(BLACK);
                } else {
                    info.setApprovalColor(RED);
                }
            });
        });

        return traceablilityLists;
    }

    /**
     * 表示用変更内容(ドキュメント情報)を設定する
     *
     * @param title 項目名
     * @param oldSection 変更前の項目値
     * @param newSection 変更後の項目値
     * @return 表示用変更内容
     */
    private DispApprovalChangeData setDocumentChangeInfo(String title, String oldSection, String newSection) {
        DispApprovalChangeData changeInfo = new DispApprovalChangeData();
        changeInfo.setApprovalTitle(title);

        if (Objects.isNull(oldSection)) {
            changeInfo.setApprovalOld("");
        } else {
            changeInfo.setApprovalOld(oldSection);
        }

        if (Objects.isNull(newSection)) {
            changeInfo.setApprovalNew("");
        } else {
            changeInfo.setApprovalNew(newSection);
        }

        if (changeInfo.getApprovalOld().equals(changeInfo.getApprovalNew())) {
            changeInfo.setApprovalColor(BLACK);
        } else {
            changeInfo.setApprovalColor(RED);
        }

        return changeInfo;
    }

    /**
     * 表示用変更内容(基本情報・品質トレーサビリティ)を設定する
     *
     * @param title 項目名
     * @param oldValue 変更前の項目値
     * @param newValue 変更後の項目値
     * @return 表示用変更内容
     */
    private DispApprovalChangeData setBasicChangeInfo(String title, String oldValue, String newValue) {
        DispApprovalChangeData changeInfo = new DispApprovalChangeData();
        changeInfo.setApprovalTitle(title);

        if (Objects.isNull(oldValue)) {
            changeInfo.setApprovalOld("");
        } else {
            changeInfo.setApprovalOld(oldValue);
        }

        if (Objects.isNull(newValue)) {
            changeInfo.setApprovalNew("");
        } else {
            changeInfo.setApprovalNew(newValue);
        }

        return changeInfo;
    }

    /**
     * 品質トレーサビリティ種別表示取得
     *
     * @param oldCheck 変更前の検査情報
     * @return プロパティ種別
     */
    private String getTraceabilityType(CheckInfoEntity oldCheck) {
        WorkPropertyCategoryEnum cat = oldCheck.getCat();
        if (!Arrays.asList(WorkPropertyCategoryEnum.values()).contains(cat)) {
            return "";
        }

        return LocaleUtils.getString(cat.getResourceKey());
    }

    /**
     * 品質トレーサビリティのオプション取得
     *
     * @param keys 検査情報
     * @return 品質トレーサビリティのオプション
     */
    private String getTraceOption(CheckInfoEntity checkInfo) {
        StringBuilder sb = new StringBuilder();
        String option = checkInfo.getOpt();

        String[] fields = new String[]{};
        if (!StringUtils.isEmpty(option)) {
            try {
                TraceSettingEntity traceSetting = (TraceSettingEntity) XmlSerializer.deserialize(TraceSettingEntity.class, option);
                if (Objects.nonNull(traceSetting)) {
                    for (TraceOptionTypeEnum key : traceSetting.getKeys()) {
                        switch (key) {
                            case FIELDS:
                                String fieldsValue = traceSetting.getValue(TraceOptionTypeEnum.FIELDS.toString());
                                if (!StringUtils.isEmpty(fieldsValue)) {
                                    fields = fieldsValue.split("\\|", 0);
                                }
                                break;
                            // 管理番号の各識別名はチェックの有無にかかわらず存在するがチェックがない場合はボタンに表示しない
                            case REFERENCE_NUMBER:
                                break;
                            // 項目数はカスタムフィールドの子要素のため表示する必要はない
                            case FIELD_SIZE:
                                break;
                            default:
                                sb.append(LocaleUtils.getString(key.getResourceKey()));
                                sb.append(",");
                                break;
                        }
                    }

                    // カスタム設定値リスト
                    String traceCustomString = LocaleUtils.getString("key.TraceCustoms");
                    if (WorkPropertyCategoryEnum.PRODUCT.equals(checkInfo.getCat())) {
                        // 完成品種別の場合 「構成部品」という文字列をオプションボタンに追加
                        // (カスタム設定値リストと同じところに構成部品データを格納したため)
                        traceCustomString = LocaleUtils.getString("key.Component");
                    }
                    if (!traceSetting.getTraceCustoms().isEmpty()) {
                        sb.append(traceCustomString);
                        sb.append(",");
                    }
                }
            } catch (Exception ex1) {
                try {
                    fields = option.split("\\|", 0);
                } catch (Exception ex2) {
                    logger.fatal(ex2, ex2);
                }
            }
        }

        for (String field : fields) {
            if (AccessoryFieldTypeEnum.LOT.equals(field)) {
                sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.LOT.getResourceKey()));
                sb.append(",");
            } else if (AccessoryFieldTypeEnum.SERIAL.equals(field)) {
                sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.SERIAL.getResourceKey()));
                sb.append(",");
            } else if (AccessoryFieldTypeEnum.QUANTITY.equals(field)) {
                sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.QUANTITY.getResourceKey()));
                sb.append(",");
            } else if (AccessoryFieldTypeEnum.EQUIPMENT.equals(field)) {
                sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.EQUIPMENT.getResourceKey()));
                sb.append(",");
            } else if (AccessoryFieldTypeEnum.CUSTOM.equals(field)) {
                sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.CUSTOM.getResourceKey()));
                sb.append(",");
            } else if (AccessoryFieldTypeEnum.PARTS_ID.equals(field)) {
                sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.PARTS_ID.getResourceKey()));
                sb.append(",");
            }
        }

        if (0 < sb.length()) {
            int index = sb.lastIndexOf(",");
            sb.deleteCharAt(index);
        }

        return sb.toString();
    }

    /**
     * ボタン表示切替
     *
     * @param approvalFlowInfo 承認フロー情報
     */
    private void switchingButton(ApprovalFlowEntity approvalFlowInfo) {
        // コントロールの有効状態(true：無効、false：有効)
        boolean disabledComment;
        boolean disabledApplicationCancel;
        boolean disabledApproval;
        boolean disabledCancel;
        boolean disabledApprovalCancel;
        // コントロールの表示状態(true：表示、false：非表示)
        boolean renderedComment;
        boolean renderedApplicationCancel;
        boolean renderedApproval;
        boolean renderedCancel;
        boolean renderedApprovalCancel;

        // ログインユーザーが申請者または承認者以外の場合
        if (Objects.isNull(approvalFlowInfo) && !approvalInfo.getRequestorId().equals(loginUserInfo.getOrganizationId())) {
            // 全て非活性
            this.commentColumn = "";
            disabledComment = true;
            disabledApplicationCancel = true;
            disabledApproval = true;
            disabledCancel = true;
            disabledApprovalCancel = true;
            // 全て非表示
            renderedComment = false;
            renderedApplicationCancel = false;
            renderedApproval = false;
            renderedCancel = false;
            renderedApprovalCancel = false;

            this.buttonControlJson
                    = getControlViewStateJson(renderedComment, renderedApplicationCancel, renderedApproval, renderedCancel, renderedApprovalCancel,
                            disabledComment, disabledApplicationCancel, disabledApproval, disabledCancel, disabledApprovalCancel);
            return;
        }
        // 申請情報.承認状態が申請中以外の場合
        if (!this.approvalInfo.getApprovalState().equals(ApprovalStatusEnum.APPLY)) {
            // コメントテキストエリアと各種ボタンは全て非活性
            this.commentColumn = "";
            disabledComment = true;
            disabledApplicationCancel = true;
            disabledApproval = true;
            disabledCancel = true;
            disabledApprovalCancel = true;
            if (Objects.nonNull(approvalFlowInfo)) {
                // ログインユーザーが承認者の場合の表示
                renderedComment = true;
                renderedApplicationCancel = false;
                renderedApproval = true;
                renderedCancel = true;
                renderedApprovalCancel = false;

                if (approvalFlowInfo.getApprovalFinal()) {
                    this.typeLabelApprovalButton = LocaleUtils.getString("key.approval.finalApprove");
                } else {
                    this.typeLabelApprovalButton = LocaleUtils.getString("approval.approvalTitle");
                }
            } else {
                // ログインユーザーが申請者の表示
                renderedComment = true;
                renderedApplicationCancel = true;
                renderedApproval = false;
                renderedCancel = false;
                renderedApprovalCancel = false;

            }

            this.buttonControlJson
                    = getControlViewStateJson(renderedComment, renderedApplicationCancel, renderedApproval, renderedCancel, renderedApprovalCancel,
                            disabledComment, disabledApplicationCancel, disabledApproval, disabledCancel, disabledApprovalCancel);
            return;
        }
        // ログインユーザーが承認者/最終承認者
        if (Objects.nonNull(approvalFlowInfo)) {
            // ログインユーザーが承認待ちの承認者のときtrue
            boolean isPossible = approvalInfo.getApprovalFlows().stream()
                    .allMatch(p -> (p.getApprovalOrder() < approvalFlowInfo.getApprovalOrder()
                    && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.APPROVE))
                    || (p.getApprovalOrder() >= approvalFlowInfo.getApprovalOrder()
                    && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.UNAPPROVED)));

            // ログインユーザーが最終承認者
            if (approvalFlowInfo.getApprovalFinal()) {
                // 最終承認者の表示対象
                this.typeLabelApprovalButton = LocaleUtils.getString("key.approval.finalApprove");
                renderedComment = true;
                renderedApplicationCancel = false;
                renderedApproval = true;
                renderedCancel = true;
                renderedApprovalCancel = false;

                // 表示されないものを非活性
                disabledApplicationCancel = true;
                disabledApprovalCancel = true;
                // 承認待ちの場合のみ活性
                if (approvalFlowInfo.getApprovalState().equals(ApprovalStatusEnum.UNAPPROVED) && isPossible) {
                    disabledComment = false;
                    disabledApproval = false;
                    disabledCancel = false;
                } else {
                    this.commentColumn = "";
                    disabledComment = true;
                    disabledApproval = true;
                    disabledCancel = true;
                }
            } else {
                // 中間承認者の表示対象
                this.typeLabelApprovalButton = LocaleUtils.getString("approval.approvalTitle");
                if (isPossible) {
                    // 承認待ちの場合の表示対象
                    renderedComment = true;
                    renderedApplicationCancel = false;
                    renderedApproval = true;
                    renderedCancel = true;
                    renderedApprovalCancel = false;
                    // 承認待ちの場合の活性状態
                    disabledComment = false;
                    disabledApplicationCancel = true;
                    disabledApproval = false;
                    disabledCancel = false;
                    disabledApprovalCancel = true;
                } else if (approvalFlowInfo.getApprovalState().equals(ApprovalStatusEnum.APPROVE)) {
                    // 承認済の場合の表示対象
                    renderedComment = true;
                    renderedApplicationCancel = false;
                    renderedApproval = false;
                    renderedCancel = false;
                    renderedApprovalCancel = true;
                    // 承認済の場合の活性状態
                    // 次の承認者が承認済みの場合のみ、コメントテキストエリアと承認取消ボタンは非活性
                    ApprovalFlowEntity nextApprover = approvalInfo.getApprovalFlows().get(approvalFlowInfo.getApprovalOrder());
                    boolean commentAndApprovalCancelDisabled = ApprovalStatusEnum.APPROVE.equals(nextApprover.getApprovalState());
                    disabledComment = commentAndApprovalCancelDisabled;
                    disabledApplicationCancel = true;
                    disabledApproval = true;
                    disabledCancel = true;
                    disabledApprovalCancel = commentAndApprovalCancelDisabled;
                } else {
                    // 上記以外の場合の表示対象
                    renderedComment = true;
                    renderedApplicationCancel = false;
                    renderedApproval = true;
                    renderedCancel = true;
                    renderedApprovalCancel = false;
                    // 上記以外の場合の活性状態
                    this.commentColumn = "";
                    disabledComment = true;
                    disabledApplicationCancel = true;
                    disabledApproval = true;
                    disabledCancel = true;
                    disabledApprovalCancel = true;
                }
            }

            this.buttonControlJson
                    = getControlViewStateJson(renderedComment, renderedApplicationCancel, renderedApproval, renderedCancel, renderedApprovalCancel,
                            disabledComment, disabledApplicationCancel, disabledApproval, disabledCancel, disabledApprovalCancel);
            return;
        }
        // ログインユーザーが申請者
        if (loginUserInfo.getOrganizationId().equals(approvalInfo.getRequestorId())) {
            // 申請者の場合の表示対象
            renderedComment = true;
            renderedApplicationCancel = true;
            renderedApproval = false;
            renderedCancel = false;
            renderedApprovalCancel = false;
            //  申請者の場合の活性状態
            disabledComment = false;
            disabledApplicationCancel = false;
            disabledApproval = true;
            disabledCancel = true;
            disabledApprovalCancel = true;

            this.buttonControlJson
                    = getControlViewStateJson(renderedComment, renderedApplicationCancel, renderedApproval, renderedCancel, renderedApprovalCancel,
                            disabledComment, disabledApplicationCancel, disabledApproval, disabledCancel, disabledApprovalCancel);
        }
    }

    /**
     * 画面コントロールの表示状態を格納するJson文字列を取得する。
     *
     * @param renderedComment コメントテキストエリアの表示状態(true：表示、false：非表示)
     * @param renderedApplicationCancel 申請取消ボタンの表示状態(true：表示、false：非表示)
     * @param renderedApproval 承認ボタンの表示状態(true：表示、false：非表示)
     * @param renderedCancel 却下ボタンの表示状態(true：表示、false：非表示)
     * @param renderedApprovalCancel 承認取消ボタンの表示状態(true：表示、false：非表示)
     * @param disabledComment コメントテキストエリアの有効状態(true：無効、false：有効)
     * @param disabledApplicationCancel 申請取消ボタンの有効状態(true：無効、false：有効)
     * @param disabledApproval 承認ボタンの有効状態(true：無効、false：有効)
     * @param disabledCancel 却下ボタンの有効状態(true：無効、false：有効)
     * @param disabledApprovalCancel 承認取消ボタンの有効状態(true：無効、false：有効)
     * @return 画面コントロールの表示状態を格納するJson文字列
     */
    private String getControlViewStateJson(boolean renderedComment, boolean renderedApplicationCancel, boolean renderedApproval,
            boolean renderedCancel, boolean renderedApprovalCancel, boolean disabledComment, boolean disabledApplicationCancel,
            boolean disabledApproval, boolean disabledCancel, boolean disabledApprovalCancel
    ) {

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"renderedComment\": ");
        json.append(renderedComment).append(", ");
        json.append("\"renderedApplicationCancel\": ");
        json.append(renderedApplicationCancel).append(", ");
        json.append("\"renderedApproval\": ");
        json.append(renderedApproval).append(", ");
        json.append("\"renderedCancel\": ");
        json.append(renderedCancel).append(", ");
        json.append("\"renderedApprovalCancel\": ");
        json.append(renderedApprovalCancel).append(", ");
        json.append("\"disabledComment\": ");
        json.append(disabledComment).append(", ");
        json.append("\"disabledApplicationCancel\": ");
        json.append(disabledApplicationCancel).append(", ");
        json.append("\"disabledApproval\": ");
        json.append(disabledApproval).append(", ");
        json.append("\"disabledCancel\": ");
        json.append(disabledCancel).append(", ");
        json.append("\"disabledApprovalCancel\": ");
        json.append(disabledApprovalCancel);
        json.append("}");

        return json.toString();
    }

    /**
     * メッセージダイアログのヘッダータイトルを取得する。
     *
     * @return メッセージダイアログのヘッダータイトル
     */
    public String getHeaderInfo() {
        return headerInfo;
    }

    /**
     * メッセージダイアログのヘッダータイトルを設定する。
     *
     * @param headerInfo メッセージダイアログのヘッダータイトル
     */
    public void setHeaderInfo(String headerInfo) {
        this.headerInfo = headerInfo;
    }

    /**
     * メッセージダイアログのメッセージを取得する。
     *
     * @return メッセージダイアログのメッセージ
     */
    public String getMessageInfo() {
        return messageInfo;
    }

    /**
     * メッセージダイアログのメッセージを設定する。
     *
     * @param messageInfo メッセージダイアログのメッセージ
     */
    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    /**
     * オープンキーを取得する。
     *
     * @return オープンキー
     */
    public String getOpenKey() {
        return openKey;
    }

    /**
     * オープンキーを設定する。
     *
     * @param openKey オープンキー
     */
    public void setOpenKey(String openKey) {
        this.openKey = openKey;
    }

}
