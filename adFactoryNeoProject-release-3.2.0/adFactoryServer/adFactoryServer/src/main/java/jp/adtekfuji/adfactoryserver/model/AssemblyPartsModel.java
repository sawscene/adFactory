/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import adtekfuji.utility.StringUtils;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.assemblyparts.AssemblyPartsCsv;
import jp.adtekfuji.adfactoryserver.entity.assemblyparts.AssemblyPartsEntity;
import jp.adtekfuji.adfactoryserver.entity.system.TroubleReportConfig;
import jp.adtekfuji.adfactoryserver.service.mail.MailProperty;
import jp.adtekfuji.adfactoryserver.service.mail.MailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 使用部品モデル
 *
 * @author
 */
@Singleton
public class AssemblyPartsModel {

    private final Logger logger = LogManager.getLogger();

    private static final String FILE_DATETIME_FORMAT = "yyyyMMddHHmmss";
    private static final String MAIL_CHARSET = "UTF-8";

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    /**
     * EntityManager を設定する。
     *
     * @param em EntityManager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * 部品トレースを使用するかを取得する。
     *
     * @return 部品トレースを使用するか
     */
    public boolean getEnablePartsTrace() {
        try {
            String enablePartsTrace = FileManager.getInstance().getSystemProperties().getProperty("enablePartsTrace", "false");
            return Boolean.valueOf(enablePartsTrace);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * バッチ処理を実行する。
     */
    public void doBatch() {
        try {
            logger.info("doBatch start.");

            // 使用部品情報をインポートする。
            this.importParts();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("doBatch end.");
        }
    }

    /**
     * PIDを指定して、部品情報を取得する。
     *
     * @param partsId PID
     * @param authId 認証ID
     * @return 部品情報
     */
    @Lock(LockType.READ)
    public AssemblyPartsEntity findParts(String partsId, Long authId) {
        logger.info("findParts: partsId={}, authId={}", partsId, authId);
        try {
            return this.em.find(AssemblyPartsEntity.class, partsId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン親PIDを指定して、使用部品情報の件数を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    public long countByKanbanPartsId(String kanbanPartsId, Long authId) {
        logger.info("countByKanbanPartsId: kanbanPartsId={}, authId={}", kanbanPartsId, authId);
        try {
            TypedQuery<Long> query = em.createNamedQuery("AssemblyPartsEntity.countByKanbanPartsId", Long.class);
            query.setParameter("kanbanPartsId", kanbanPartsId);

            return query.getSingleResult();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン親PIDを指定して、使用部品情報一覧を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 使用部品情報一覧
     */
    @Lock(LockType.READ)
    public List<AssemblyPartsEntity> findByKanbanPartsId(String kanbanPartsId, Integer from, Integer to, Long authId) {
        logger.info("findByKanbanPartsId: kanbanPartsId={}, authId={}", kanbanPartsId, authId);
        try {
            TypedQuery<AssemblyPartsEntity> query = em.createNamedQuery("AssemblyPartsEntity.findByKanbanPartsId", AssemblyPartsEntity.class);
            query.setParameter("kanbanPartsId", kanbanPartsId);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン名を指定して、使用部品情報の件数を取得する。
     *
     * @param kanbanName カンバン名
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    public long countByKanbanName(String kanbanName, Long authId) {
        logger.info("countByKanbanName: kanbanName={}, authId={}", kanbanName, authId);
        try {
            TypedQuery<Long> query = em.createNamedQuery("AssemblyPartsEntity.countByKanbanName", Long.class);
            query.setParameter("kanbanName", kanbanName);

            return query.getSingleResult();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン名を指定して、使用部品情報一覧を取得する。
     *
     * @param kanbanName カンバン名
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 使用部品情報一覧
     */
    @Lock(LockType.READ)
    public List<AssemblyPartsEntity> findByKanbanName(String kanbanName, Integer from, Integer to, Long authId) {
        logger.info("findByKanbanName: kanbanName={}, authId={}", kanbanName, authId);
        try {
            TypedQuery<AssemblyPartsEntity> query = em.createNamedQuery("AssemblyPartsEntity.findByKanbanName", AssemblyPartsEntity.class);
            query.setParameter("kanbanName", kanbanName);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 使用部品情報を更新する。
     *
     * @param partsIds PID一覧
     * @param fixedFlags 使用フラグ一覧
     * @param verInfos 排他用バージョン一覧
     * @param fixedDate 使用確定日時
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    public ResponseEntity updateParts(String[] partsIds, Boolean[] fixedFlags, Integer[] verInfos, Date fixedDate, Long authId) {
        logger.info("updateParts: partsIds={}, authId={}", partsIds, authId);
        try {
            String personNo = null;
            if (Objects.nonNull(authId)) {
                personNo = this.findOrganizationIdentify(authId);
            }

            for (int count = 0; count < partsIds.length; count++) {
                String partsId = partsIds[count];
                Boolean fixedFlag = fixedFlags[count];
                Integer verInfo = verInfos[count];

                AssemblyPartsEntity parts = this.findParts(partsId, authId);
                if (Objects.nonNull(parts)) {
                    if (!Objects.equals(verInfo, parts.getVerInfo())) {
                        this.em.clear();

                        // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                        return ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO);
                    }

                    boolean isUpdate = false;
                    if (fixedFlag) {
                        if (!StringUtils.equals(parts.getFixedFlag(), "Y")
                                || Objects.isNull(parts.getFixedDate())) {
                            parts.setFixedFlag("Y");
                            parts.setFixedDate(fixedDate);
                            isUpdate = true;
                        }
                    } else {
                        if (!StringUtils.equals(parts.getFixedFlag(), "N")
                                || Objects.nonNull(parts.getFixedDate())) {
                            parts.setFixedFlag("N");
                            parts.setFixedDate(null);
                            isUpdate = true;
                        }
                    }

                    if (isUpdate) {
                        parts.setPersonNo(personNo);
                        parts.setUpdateDate(fixedDate);
                        this.em.merge(parts);
                    }
                }
            }
            this.em.flush();

            return ResponseEntity.success();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.em.clear();
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 使用部品情報をインポートする。
     */
    public void importParts() {
        logger.info("importParts start.");
        File importFile = null;
        try {
            // 部品トレースを使用するか
            boolean enablePartsTrace = this.getEnablePartsTrace();
            if (!enablePartsTrace) {
                return;
            }

            // インポート対象フォルダ
            String folderPath = ServiceConfig.getInstance().getImportParts();
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                return;
            }

            // インポート対象ファイルのパターン
            Pattern pattern = Pattern.compile("^bhnuke_(\\d{14})\\.dat$");

            // インポート対象ファイルマップを作成する。(yyyyMMddHHmmssをDateにしてキーとしたFileのマップ)
            Map<Date, File> importFileMap = new LinkedHashMap();

            File[] files = folder.listFiles();
            if (Objects.isNull(files)) {
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                }

                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    // yyyyMMddHHmmss の部分を Date に変換する。
                    Date date = this.convertDate(matcher.group(1), FILE_DATETIME_FORMAT);
                    if (Objects.isNull(date)) {
                        // Date にならない場合はインポート対象外。
                        continue;
                    }

                    // インポート対象ファイルマップに追加する。
                    importFileMap.put(date, file);
                }
            }

            // 対象ファイルを順番にインポートする。
            for (Map.Entry<Date, File> entry : importFileMap.entrySet()) {
                Date fileDate = entry.getKey();
                importFile = entry.getValue();

                // ファイルから使用部品CSV情報に読み込む。
                logger.info("importFile: {}", importFile);
                List<AssemblyPartsCsv> partsInfos = null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(importFile), "UTF-8"))) {
                    CsvToBeanBuilder<AssemblyPartsCsv> csvToBean = new CsvToBeanBuilder(reader)
                            .withSeparator('\t')
                            .withIgnoreQuotations(true)
                            .withType(AssemblyPartsCsv.class);

                    partsInfos = csvToBean.build().parse();
                }

                // 読み込んだ使用部品情報を登録する。
                if (Objects.nonNull(partsInfos)) {
                    this.em.clear();

                    for (AssemblyPartsCsv partsInfo : partsInfos) {
                        AssemblyPartsEntity importParts = new AssemblyPartsEntity(partsInfo);

                        importParts.setUpdateDate(fileDate); // 更新日時

                        AssemblyPartsEntity parts = this.find(partsInfo.getPartsId());
                        if (Objects.isNull(parts)) {
                            // 存在しない場合は新規作成する。
                            this.em.persist(importParts);
                        } else if (fileDate.after(parts.getUpdateDate())
                                && !StringUtils.equals(parts.getFixedFlag(), "Y")) {
                            // 使用部品が使用確定していなくて、更新日時がファイル名の日時より古い場合は更新する。
                            importParts.setVerInfo(parts.getVerInfo());
                            this.em.merge(importParts);
                        }
                    }

                    this.em.flush();
                }

                // インポートファイルをバックアップフォルダに移動する。
                this.moveImportFile(importFile);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // インポートに失敗したら、インポートファイルをバックアップフォルダに移動してメールを送信する。
            if (Objects.nonNull(importFile)) {
                this.moveImportFile(importFile);
                this.sendImportErrorMail(importFile.getName(), ex.getMessage());
            }
        } finally {
            logger.info("importParts end.");
        }
    }

    /**
     * インポートファイルをバックアップフォルダに移動する。
     *
     * @param importFile インポートファイル
     */
    private void moveImportFile(File importFile) {
        try {
            // バックアップフォルダが無い場合は作成する。
            Path backupPath = Paths.get(importFile.getParent(), "backup");
            File backupFolder = backupPath.toFile();
            if (!backupFolder.exists()) {
                backupFolder.mkdir();
            }

            // インポートファイルをバックアップフォルダに移動する。
            Path movePath = Paths.get(backupFolder.getPath(), importFile.getName());
            Files.move(importFile.toPath(), movePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 使用部品インポートのエラーメールを送信する。
     *
     * @param fileName インポートファイル名
     * @param errorMessage エラー詳細メッセージ
     * @return 結果(true:送信成功, false:送信失敗)
     */
    private boolean sendImportErrorMail(String fileName, String errorMessage) {
        boolean result = false;
        try {
            TroubleReportConfig props = TroubleReportConfig.getInstance();
            props.load();

            String mailPassword = props.getReportMailPassword();
            int mailTimeoutMs = props.getReportMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setHost(props.getReportMailServer());
            prop.setPort(props.getReportMailPort());
            prop.setIsEnableAuth(props.getReportMailAuth());
            prop.setUser(props.getReportMailUser());
            prop.setPassword(mailPassword);
            prop.setIsEnableTLS(props.getReportMailTLS());
            prop.setConnectionTimeout(mailTimeoutMs);
            prop.setTimeout(mailTimeoutMs);
            prop.setCharset(MAIL_CHARSET);

            String from = props.getReportMailFrom();
            String to = props.getReportMailTo();

            // 件名
            String subject = "adFactory Trouble Report";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String now = dateFormat.format(new Date());

            String error = errorMessage.replaceAll("\r\n|\n\r|\n|\r", "<br>");

            int fontSize = 11;
            int cellPadding = 8;
            int tableBorder = 1;

            // 本文
            StringBuilder content = new StringBuilder();
            content.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            content.append("<html>");
            content.append("<head>");
            content.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(MAIL_CHARSET).append("\" />");
            content.append("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
            content.append("<style type=\"text/css\">");
            content.append("body { font-size: ").append(fontSize).append("pt; }");
            content.append("table { border-collapse:collapse; border:").append(tableBorder).append("; }");
            content.append("</style>");
            content.append("</head>");
            content.append("<body>");
            content.append("adFactory にて、以下の障害が発生しました。<br><br>");
            content.append("<table border=\"1\" cellpadding=\"").append(cellPadding).append("\"");
            content.append("<tr align=\"left\" valign=\"top\"><td>エラー内容</td><td>出庫部品情報ファイルを読み込みできませんでした。<br>(").append(fileName).append(")</td></tr>");
            content.append("<tr align=\"left\" valign=\"top\"><td>発生日時</td><td>").append(now).append("</td></tr>");
            content.append("<tr align=\"left\" valign=\"top\"><td>エラー詳細</td><td>").append(error).append("</td></tr>");
            content.append("</table>");
            content.append("</body>");
            content.append("</html>");

            // メール送信
            MailUtils mail = new MailUtils(prop);
            ServerErrorTypeEnum mailResult = mail.send(from, to, subject, content.toString(), true);
            if (ServerErrorTypeEnum.SUCCESS.equals(mailResult)) {
                result = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * 使用部品情報を取得する。
     *
     * @param partsId PID
     * @return 使用部品情報
     */
    private AssemblyPartsEntity find(String partsId) {
        try {
            return this.em.find(AssemblyPartsEntity.class, partsId);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 組織IDを指定して、組織識別名を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織識別名
     */
    private String findOrganizationIdentify(long organizationId) {
        try {
            TypedQuery<String> query = this.em.createNamedQuery("OrganizationEntity.findIdentifyById", String.class);
            query.setParameter("organizationId", organizationId);

            return query.getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 日時文字列をDateに変換する。
     * 
     * @param dateString 日時文字列
     * @param format フォーマット文字列
     * @return 日時
     */
    private Date convertDate(String dateString, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }
}
