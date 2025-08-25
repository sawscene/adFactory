package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import jp.adtekfuji.adFactory.entity.ListWrapper;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentImportEntity;
import jp.adtekfuji.adFactory.entity.ledger.NameValueEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerConditionEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualAditionEntity;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import jp.adtekfuji.adfactoryserver.entity.ledger.LedgerEntity;
import jp.adtekfuji.adfactoryserver.entity.ledger.LedgerFileEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import adtekfuji.utility.Tuple;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.excelreplacer.ExcelReplacer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import javax.imageio.ImageIO;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@Singleton
@Path("ledger")
public class LedgerEntityFacadeREST extends AbstractFacade<LedgerEntity> {
    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    private static final Logger logger = LogManager.getLogger();
    public static final Pattern tagPattern = Pattern.compile("^(?<format>(?<tag>(TAG_ED|TAG_TM)\\([^()=$]+\\)|[^()=$]+)(\\((?<predicate>.+)\\))?)(=(?<value>[^$]+))?(\\$(?<index>\\d+))?$");
    final static FileManager fileManager = FileManager.getInstance();
    final static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
    static private final String TAG_WORD = "TAG_";

    static private final Pattern KANBAN_NO_PATTERN = Pattern.compile("^\\$[1-9][0-9]*\\.");
    @EJB
    private ActualResultEntityFacadeREST actualResultEntityFacadeREST;

    @EJB
    private WorkEntityFacadeREST workEntityFacadeREST;

    @EJB
    private WorkflowEntityFacadeREST workflowEntityFacadeREST;

    @EJB
    private EquipmentEntityFacadeREST equipmentEntityFacadeREST;

    @EJB
    private OrganizationEntityFacadeREST organizationEntityFacadeREST;

    @EJB
    private FormFacadeREST formFacadeREST;

    public LedgerEntityFacadeREST() {
        super(LedgerEntity.class);
    }

    /**
     * EntityManager を取得する。
     *
     * @return EntityManager
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * クラスを初期化する。
     */
    @PostConstruct
    public void initialize() {

    }

    /**
     * 指定した階層IDの階層情報を取得する。
     *
     * @param id     階層ID
     * @param authId 認証ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public LedgerEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws Exception {
        logger.info("find: id={}, authId={}", id, authId);
        return this.find(id);
    }

    /**
     * 親IDからの要素を取得
     *
     * @param ids
     * @param authId
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("children")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<LedgerEntity> findChild(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", ids, authId);
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            TypedQuery<LedgerEntity> query = this.em.createNamedQuery("LedgerEntity.findByParentIds", LedgerEntity.class);
            query.setParameter("parentIds", ids);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }


    static final Pattern pattern = Pattern.compile("0/template/\\d{17}");

    /**
     * 帳票情報を登録する。
     *
     * @param entity 工程順階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(LedgerEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 帳票情報を登録する。
            super.create(entity);
            em.flush();

            // 一時保存ファイルがある
            if (pattern.matcher(entity.getLedgerPhysicalFileName()).find()) {
                File fromFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, entity.getLedgerPhysicalFileName()));
                if (fromFile.isFile()) {
                    fileManager.createDirectory(FileManager.Data.REPORT, entity.getLedgerId().toString() + "/template");
                    String toFileName = entity.getLedgerId().toString() + "/template/" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, toFileName));
                    Files.move(fromFile.toPath(), toFile.toPath());
                    entity.setLedgerPhysicalFileName(toFileName);
                }
            }

            // 作成した情報を元に、戻り値のURIを作成する
            URI uri = new URI(new StringBuilder("ledger/").append(entity.getLedgerId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した階層IDの階層情報を削除する。
     *
     * @param ids     工程階層ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", ids, authId);
        try {
            // 階層情報を削除する。
            ids.stream()
                    .map(super::find)
                    .filter(Objects::nonNull)
                    .forEach(super::remove);
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 階層情報を更新する。
     *
     * @param entity 工程階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(LedgerEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 自身は親階層に指定できない。
            if (Objects.isNull(entity.getParentHierarchyId()) || Objects.isNull(entity.getLedgerId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 排他用バージョンを確認する。
            LedgerEntity target = super.find(entity.getLedgerId());
            if (!Objects.equals(target.getVerInfo(), entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);


            // 一時保存ファイルがある
            if (pattern.matcher(entity.getLedgerPhysicalFileName()).find()) {
                File fromFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, entity.getLedgerPhysicalFileName()));
                if (fromFile.isFile()) {
                    fileManager.createDirectory(FileManager.Data.REPORT, entity.getLedgerId().toString() + "/template");
                    String toFileName = entity.getLedgerId().toString() + "/template/" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, toFileName));
                    Files.move(fromFile.toPath(), toFile.toPath());
                    entity.setLedgerPhysicalFileName(toFileName);
                }
            }

            // 階層情報を更新する。
            super.edit(entity);
            em.flush();
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }


    /**
     * 帳票アップロード
     *
     * @param inputStreams 帳票ファイル
     * @param authId       w承認ID
     * @return
     */
    @POST
    @Path(value = "upload/template")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response uploadLedgerFile(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        logger.info("uploadLedgerFile: start");

        fileManager.createDirectory(FileManager.Data.REPORT, "0/template/");
        final String fileName = "0/template/" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File inputFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, fileName));

        // ファイル取込
        logger.info("importFile: " + inputFile);
        BufferedInputStream bf = new BufferedInputStream(inputStreams.get(0));
        byte[] buff = new byte[1024];
        try (OutputStream out = Files.newOutputStream(inputFile.toPath())) {
            int len;
            while ((len = bf.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
            out.flush();
            URI uri = new URI(fileName);
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    static final String folderName = "adFactoryReport";

    /**
     * 帳票出力する工程の一覧を取得する
     * @param ledgerId 帳票ID
     * @param equipmentIds 設備ID
     * @param organizationIds 組織ID
     * @param limit 件数
     * @param fromDate 開始日
     * @param toDate 完了日
     * @param authId 認証ID
     * @return 帳票リスト
     */
    @Lock(LockType.READ)
    @GET
    @Path("report/search")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> searchHistory(@QueryParam("ledgerId") Long ledgerId, @QueryParam("eId") List<Long> equipmentIds, @QueryParam("oId") List<Long> organizationIds, @QueryParam("limit") Integer limit, @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("authId") Long authId) {
        logger.info("searchHistory: ledgerId={}, equipmentIds={}, organizationIds={}, from={}, to={}, authId={}", ledgerId, equipmentIds, organizationIds, fromDate, toDate, authId);
        try {


            int index = 1;
            List<Function<Query, Query>> parameterSetter = new ArrayList<>();

            String sql = "WITH target AS (SELECT X.* FROM mst_ledger ml, jsonb_to_recordset(ml.ledger_target) AS X(hierarchy_id BIGINT, workflow_id BIGINT, work_id BIGINT) WHERE ml.ledger_id = ?1), kanban_id AS (WITH RECURSIVE kanban_hierarchy AS (SELECT mkh.kanban_hierarchy_id id FROM mst_kanban_hierarchy mkh WHERE mkh.hierarchy_name = '" + folderName + "' UNION DISTINCT SELECT mkh2.child_id AS kanban_hierarch_id FROM tre_kanban_hierarchy mkh2, kanban_hierarchy WHERE mkh2.parent_id = kanban_hierarchy.id) SELECT ckh.kanban_id FROM kanban_hierarchy JOIN con_kanban_hierarchy ckh ON ckh.kanban_hierarchy_id = kanban_hierarchy.id), workflow_id AS (WITH RECURSIVE workflow_hierarchy AS (SELECT target.hierarchy_id FROM target WHERE target.hierarchy_id IS NOT NULL UNION DISTINCT SELECT mh2.hierarchy_id FROM mst_hierarchy mh2, workflow_hierarchy WHERE workflow_hierarchy.hierarchy_id = mh2.parent_hierarchy_id) SELECT work_workflow_id FROM workflow_hierarchy JOIN con_hierarchy ch ON ch.hierarchy_id = workflow_hierarchy.hierarchy_id UNION DISTINCT SELECT target.workflow_id FROM target WHERE target.work_id IS NULL) SELECT tar.* FROM (SELECT twk.work_kanban_id FROM trn_work_kanban twk JOIN workflow_id ON twk.workflow_id = workflow_id.work_workflow_id JOIN kanban_id ON twk.kanban_id = kanban_id.kanban_id UNION DISTINCT SELECT twk2.work_kanban_id FROM trn_work_kanban twk2 JOIN kanban_id ON twk2.kanban_id = kanban_id.kanban_id JOIN target ON target.work_id = twk2.work_id AND target.workflow_id = twk2.workflow_id) workkanban_id JOIN trn_actual_result tar ON tar.work_kanban_id = workkanban_id.work_kanban_id";
            List<String> actualResultCondition = new ArrayList<>();
            actualResultCondition.add("tar.remove_flag = FALSE");

            // 開始日時
            if (!StringUtils.isEmpty(fromDate)) {
                final Date date = sf.parse(fromDate);
                final int num = ++index;
                actualResultCondition.add("tar.implement_datetime >= " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, date));
            }

            // 完了日時
            if (!StringUtils.isEmpty(toDate)) {
                final Date date = sf.parse(toDate);
                final int num = ++index;
                actualResultCondition.add("tar.implement_datetime <= " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, date));
            }

            // 設備
            if (Objects.nonNull(equipmentIds) && !equipmentIds.isEmpty()) {
                java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("integer", equipmentIds.toArray());
                final int num = ++index;
                actualResultCondition.add("equipment_id IN (WITH RECURSIVE eq_ids AS ( SELECT me.equipment_id id FROM mst_equipment me WHERE me.equipment_id = ANY(?" + (num) + ") UNION ALL SELECT me2.equipment_id id FROM mst_equipment me2, eq_ids WHERE me2.parent_equipment_id = eq_ids.id) SELECT eq_ids.id FROM eq_ids)");
                parameterSetter.add((query) -> query.setParameter(num, idArray));
            }

            // 組織
            if (Objects.nonNull(organizationIds) && !organizationIds.isEmpty()) {
                java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("integer", organizationIds.toArray());
                final int num = ++index;
                actualResultCondition.add("organization_id IN (WITH RECURSIVE org_ids AS ( SELECT mo.organization_id id FROM mst_organization mo WHERE mo.organization_id = ANY(?" + (num) + ") UNION ALL SELECT mo2.organization_id id FROM mst_organization mo2, org_ids WHERE mo2.parent_organization_id = org_ids.id) SELECT org_ids.id FROM org_ids)");
                parameterSetter.add((query) -> query.setParameter(num, idArray));
            }

            sql += " WHERE " + String.join(" AND ", actualResultCondition);
            sql += " ORDER BY implement_datetime DESC";

            // 表示数
            if (Objects.nonNull(limit)) {
                final int num = ++index;
                sql += " LIMIT( ?" + num + " )";
                parameterSetter.add((query) -> query.setParameter(num, limit));
            }

            Query query = em.createNativeQuery(sql, ActualResultEntity.class);
            query.setParameter(1, ledgerId);
            for (Function<Query, Query> setter : parameterSetter) {
                query = setter.apply(query);
            }
            List<ActualResultEntity> ret = query.getResultList();
            // 実施時刻の昇順でソート
            return ret.stream().sorted(Comparator.comparing(ActualResultEntity::getImplementDatetime)).collect(toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * セル情報置換IF
     */
    interface Replacer {
           Replacer apply(Cell cell);
        String getValue(String format);
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

        @Override
        public String getValue(String format) {
            return text;
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
            Optional<String> result = getValueImpl(cell.getStringCellValue());
            Replacer newReplacer = new TextReplacer(result.orElseGet(() -> new SimpleDateFormat().format(date)));
            newReplacer.apply(cell);

            return result.isPresent()
                    ? newReplacer
                    : this;
        }

        @Override
        public String getValue(String format) {
            Optional<String> result = getValueImpl(format);
            return result.orElseGet(() -> new SimpleDateFormat().format(date));
        }

        private Optional<String> getValueImpl(String format) {
            List<String> cellTag = Arrays.asList(format.split("\\."));
            Matcher m1 = tagPattern.matcher(cellTag.get(cellTag.size() - 1));
            SimpleDateFormat sdf;
            try {
                if (m1.find() && StringUtils.nonEmpty(m1.group("predicate"))) {
                    sdf = new SimpleDateFormat(m1.group("predicate"));
                    return Optional.of(sdf.format(date));
                }
                return Optional.empty();
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
    }

    /**
     * 設備プロパティ－用リプレーサー
     */
    static class EquipmentPropertyReplacer implements  Replacer {

        @lombok.Getter
        final EquipmentEntity equipmentEntity;
        final Map<String, String> addInfoMap;
        EquipmentPropertyReplacer(EquipmentEntity equipmentEntity) {
            this.equipmentEntity = equipmentEntity;
            addInfoMap
                    = JsonUtils.jsonToObjects(equipmentEntity.getEquipmentAddInfo(), EquipmentImportEntity.addInfoJson[].class)
                    .stream()
                    .collect(toMap(EquipmentImportEntity.addInfoJson::getKey, EquipmentImportEntity.addInfoJson::getVal, (a, b)->a));
        }

        @Override
        public Replacer apply(Cell cell) {
            Optional<String> result = getValueImpl(cell.getStringCellValue());
            Replacer newReplacer = new TextReplacer(result.orElse(""));
            newReplacer.apply(cell);

            return result.isPresent()
                    ? newReplacer
                    : this;
        }

        @Override
        public String getValue(String format) {
            return getValueImpl(format).orElse("");
        }

        private Optional<String> getValueImpl(String format) {
            List<String> cellTag = Arrays.asList(format.split("\\."));
            Matcher m1 = tagPattern.matcher(cellTag.get(cellTag.size()-1));
            if (m1.find() && StringUtils.nonEmpty(m1.group("predicate"))) {
                return Optional.of(addInfoMap.getOrDefault(m1.group("predicate"), ""));
            }
            return Optional.empty();
        }

    }

    /**
     * 追加情報の置き換え
     */
    static class ActualAddtionReplacer implements Replacer {
        final Long actualAdditionId;
        final static ActualResultInfoFacade facade = new ActualResultInfoFacade();

        ActualAddtionReplacer(Long actualAdditionId) {
            this.actualAdditionId = actualAdditionId;
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
                data = facade.downloadFileData(actualAdditionId);
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

        public String getValue(String format) {
            return actualAdditionId.toString();
        }
    }

    /**
     * タグ削除
     */
    static class DeleteTagReplacer implements Replacer {
        @Override
        public Replacer apply(Cell cell) {
            List<String> cellTag = Arrays.asList(cell.getStringCellValue().split("\\."));
            if (cellTag.isEmpty()) {
                Replacer newReplacer = new TextReplacer("");
                newReplacer.apply(cell);
                return newReplacer;
            }

            Matcher m1 = tagPattern.matcher(cellTag.get(cellTag.size()-1));
            if (!m1.find() || StringUtils.isEmpty(m1.group("tag"))) {
                Replacer newReplacer = new TextReplacer("");
                newReplacer.apply(cell);
                return newReplacer;
            }

            String some = m1.group("tag");

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

            Replacer newReplacer = new TextReplacer("");
            newReplacer.apply(cell);
            return newReplacer;
        }

        public String getValue(String format) {
            return "";
        }
    }

    /**
     * エラータグ置換
     */
    static class ErrorTagReplacer implements Replacer {
        @Override
        public Replacer apply(Cell cell) {
            List<String> cellTag = Arrays.asList(cell.getStringCellValue().split("\\."));
            if (cellTag.isEmpty()) {
                Replacer newReplacer = new TextReplacer("");
                newReplacer.apply(cell);
                return newReplacer;
            }

            Matcher m1 = tagPattern.matcher(cellTag.get(cellTag.size()-1));
            if (!m1.find() || StringUtils.isEmpty(m1.group("tag"))) {
                Replacer newReplacer = new TextReplacer("");
                newReplacer.apply(cell);
                return newReplacer;
            }
            String some = m1.group("tag");

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

        @Override
        public String getValue(String format) {
            return "";
        }
    }

    /**
     * 置換選択
     * @param text タグ情報
     * @return 置換オブジェクト
     */
    Replacer getReplacer(String text) {
        return Objects.isNull(text) ? new ErrorTagReplacer() : new TextReplacer(text);
    }

    /**
     * 日にち用
     * @param date 日にち
     * @return リプレーサー
     */
    Replacer getReplacer(Date date) {
        return Objects.isNull(date) ? new ErrorTagReplacer() : new DateReplacer(date);
    }

    /**
     *  数値用
     * @param num 数値
     * @return リプレーサー
     */
    Replacer getReplacer(Long num) {
        return Objects.isNull(num) ? new ErrorTagReplacer() : new ActualAddtionReplacer(num);
    }

    /**
     * 設備用
     * @param equipmentEntity 設備
     * @return リプレーサー
     */
    Replacer getReplacer(EquipmentEntity equipmentEntity) {
        return Objects.isNull(equipmentEntity) ? new ErrorTagReplacer() : new EquipmentPropertyReplacer(equipmentEntity);
    }

    /**
     * 帳票出力
     * @param inputFile
     * @param outputFile
     * @param replacerMapList
     * @param isNoRemoveTag
     * @return
     */
    boolean exportLedger(File inputFile, File outputFile, List<Map<String, Replacer>> replacerMapList, boolean isNoRemoveTag) {
        Map<String, Replacer> tagMap = new HashMap<>();
        Replacer defaultTagReplacer = isNoRemoveTag ? new ErrorTagReplacer() : new DeleteTagReplacer();

        try (FileInputStream fis = new FileInputStream(inputFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)
        ) {
            for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING) || Objects.isNull(cell.getStringCellValue())) {
                            continue;
                        }

                        final String tagName = cell.getStringCellValue();
                        Replacer replacer = tagMap.get(tagName);
                        if (Objects.isNull(replacer)) {
                            List<Matcher> elements
                                    = Arrays.stream(tagName.split("\\."))
                                    .map(tagPattern::matcher)
                                    .collect(toList());
                            if (!elements.stream().allMatch(Matcher::find)) {
                                // 全てフォーマットにマッチしなかった。
                                continue;
                            }

                            Map<String, Replacer> newTagMap = createTagMap(elements, 0, replacerMapList);
                            newTagMap.forEach(tagMap::putIfAbsent);
                            replacer = newTagMap.getOrDefault(tagName, defaultTagReplacer);
                        }

                        Replacer newReplacer = replacer.apply(cell);
                        if (Objects.nonNull(newReplacer)) {
                            tagMap.put(tagName, newReplacer);
                        } else {
                            tagMap.put(tagName, new ErrorTagReplacer());
                        }
                    }
                }
            }

            workbook.setForceFormulaRecalculation(true);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
                fos.flush();
                return true;
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                return false;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        }

//        Replacer defaultTagReplacer = isNoRemoveTag ? new ErrorTagReplacer() : new DeleteTagReplacer();
//
//        try (FileInputStream fis = new FileInputStream(inputFile);
//             XSSFWorkbook workbook = new XSSFWorkbook(fis)
//        ) {
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                XSSFSheet sheet = workbook.getSheetAt(i);
//                for (Row row : sheet) {
//                    for (Cell cell : row) {
//                        if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
//                                || Objects.isNull(cell.getStringCellValue())) {
//                            continue;
//                        }
//
//                        final String tagName = cell.getStringCellValue();
//                        Replacer replacer
//                                = tagMap.computeIfAbsent(
//                                tagName,
//                                key -> tagMap.getOrDefault(tagName.split("\\(")[0], defaultTagReplacer));
//
//                        Replacer newReplacer = replacer.apply(cell);
//                        if (Objects.nonNull(newReplacer)) {
//                            tagMap.put(tagName, newReplacer);
//                        } else {
//                            tagMap.put(tagName, new ErrorTagReplacer());
//                        }
//                    }
//                }
//            }
//
//            workbook.setForceFormulaRecalculation(true);
//            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
//                workbook.write(fos);
//                fos.flush();
//                return true;
//            } catch (Exception ex) {
//                logger.fatal(ex, ex);
//                return false;
//            }
//        } catch (Exception ex) {
//            logger.fatal(ex, ex);
//            return false;
//        }
    }

    /**
     * タグを作成する
     * @param elements 正規表現にマッチしたデータ
     * @param startIndex 連結した時の版号
     * @param data タグリスト
     * @return タグリスト
     */
    private static Map<String, Replacer> createTagMap(List<Matcher> elements, Integer startIndex, List<Map<String, Replacer>> data) {
        if (elements.isEmpty() || data.isEmpty()) {
            return new HashMap<>();
        }

        final Matcher matcher = elements.get(0);

        final String tag = matcher.group("tag");
        if (StringUtils.isEmpty(tag)) {
            return new HashMap<>();
        }

        // <tag>(<predicate>)=<value>$<index>
        final String value = matcher.group("value");    // 実行結果
        final String index = matcher.group("index");    // 配列の版号
        final String format = matcher.group("format");  // 全体

        Map<String, Replacer> ret = new HashMap<>();
        if (elements.size() == 1) {
            // 最後の要素
            if (StringUtils.nonEmpty(value)) {
                // 値を設定してる場合
                Map<String, List<Map<String, Replacer>>> groupingData
                        = data
                        .stream()
                        .filter(d -> d.containsKey(tag))
                        .map(d -> new Tuple<>(d.get(tag).getValue(format), d))
                        .filter(d -> StringUtils.nonEmpty(d.getLeft()))
                        .collect(groupingBy(
                                Tuple::getLeft,
                                collectingAndThen(toList(), d -> d.stream().map(Tuple::getRight).collect(toList()))));

                groupingData.forEach((_key, _value) -> {
                    List<Tuple<String, Replacer>> tmp
                            = _value
                            .stream()
                            .map(_tuple -> new Tuple<>(format + "=" + _key, _tuple.get(tag)))
                            .collect(toList());

                    ret.put(tmp.get(0).getLeft(), tmp.get(0).getRight());
                    for (int n = 0; n < tmp.size(); ++n) {
                        final String suffix = String.format("$%d", n + 1);
                        ret.put(format + "=" + _key + suffix, tmp.get(n).getRight());
                    }
                });
                return ret;
            }

            // 値を設定していない場合
            List<Replacer> replaces
                    = data
                    .stream()
                    .map(d -> d.get(tag))
                    .filter(Objects::nonNull)
                    .filter(d -> StringUtils.nonEmpty(d.getValue(format)))
                    .collect(toList());
            if (replaces.isEmpty()) {
                return new HashMap<>();
            }

            ret.put(format, replaces.get(0));
            for (Replacer replace : replaces) {
                final String suffix = String.format("$%d", ++startIndex);
                ret.put(format + suffix, replace);
            }
            return ret;
        }

        if (StringUtils.nonEmpty(value)) {
            // 値を設定してる場合
            List<Map.Entry<String, List<Map<String, Replacer>>>> groupingData
                    = data
                    .stream()
                    .filter(d -> d.containsKey(tag))
                    .map(d -> new Tuple<>(d.get(tag).getValue(format), d))
                    .filter(d -> StringUtils.nonEmpty(d.getLeft()))
                    .collect(groupingBy(Tuple::getLeft,
                            collectingAndThen(toList(), d->d.stream().map(Tuple::getRight).collect(toList()))))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(toList());

            if (StringUtils.nonEmpty(index)) {
                // 値設定があり かつ インデックス指定あり
                Integer nextIndex = 0;
                for (Map.Entry<String, List<Map<String, Replacer>>> groupingDataEntry : groupingData) {
                    for (int m = 0; m < groupingDataEntry.getValue().size(); ++m) {
                        Map<String, Replacer> newTagMap = createTagMap(elements.subList(1, elements.size()), nextIndex, Collections.singletonList(groupingDataEntry.getValue().get(m)));
                        if (newTagMap.isEmpty()) {
                            continue;
                        }
                        final String newTag = format + "=" + groupingDataEntry.getKey() + String.format("$%d", ++startIndex) + ".";
                        newTagMap.forEach((_key, _value) -> ret.computeIfAbsent(newTag + _key, k -> _value));
                    }
                }
            } else {
                // 値指定あり かつ インデックス指定なし
                Integer nextIndex = 0;
                for (Map.Entry<String, List<Map<String, Replacer>>> groupingDataEntry : groupingData) {
                    final String newTag = format + "=" + groupingDataEntry.getKey() + ".";
                    Map<String, Replacer> newTagMap = createTagMap(elements.subList(1, elements.size()), nextIndex, groupingDataEntry.getValue());
                    newTagMap.forEach((_key, _value) -> ret.computeIfAbsent(newTag + _key, k -> _value));
                }
            }
            return ret;
        }

        if (StringUtils.nonEmpty(index)) {
            // 値指定なし かつ インデックス指定あり
            List<Map<String, Replacer>> orderData
                    = data
                    .stream()
                    .filter(d -> d.containsKey(tag))
                    .map(d -> new Tuple<>(d.get(tag).getValue(format), d))
                    .filter(d -> StringUtils.nonEmpty(d.getLeft()))
                    .sorted(Comparator.comparing(Tuple::getLeft))
                    .map(Tuple::getRight)
                    .collect(toList());

            Integer nextIndex = 0;
            for (Map<String, Replacer> orderDatum : orderData) {
                Map<String, Replacer> newTagMap = createTagMap(elements.subList(1, elements.size()), nextIndex, Collections.singletonList(orderDatum));
                if (newTagMap.isEmpty()) {
                    continue;
                }

                final String newTag = format + String.format("$%d", ++startIndex) + ".";
                newTagMap.forEach((_key, _value) -> ret.computeIfAbsent(newTag + _key, k -> _value));
            }
            return ret;
        }

        List<Map<String, Replacer>> orderData
                = data
                .stream()
                .filter(d -> d.containsKey(tag))
                .map(d -> new Tuple<>(d.get(tag).getValue(format), d))
                .filter(d -> StringUtils.nonEmpty(d.getLeft()))
                .sorted(Comparator.comparing(Tuple::getLeft))
                .map(Tuple::getRight)
                .collect(toList());

        for (Map<String, Replacer> orderDatum : orderData) {
            final String newTag = format + ".";
            Map<String, Replacer> newTagMap = createTagMap(elements.subList(1, elements.size()), startIndex, Collections.singletonList(orderDatum));
            if (newTagMap.isEmpty()) {
                continue;
            }
            newTagMap.forEach((_key, _value) -> ret.computeIfAbsent(newTag + _key, k -> _value));
        }
        return ret;
    }

    /**
     *  グルーピングするキーを作成
     * @param actualResult 工程実績
     * @return グルーピングキー
     */
    private String createGroupKey(ActualResultEntity actualResult) {
        return actualResult.getWorkflowId() + "#####"
                + actualResult.getEquipmentId() + "#####"
                + actualResult.getOrganizationId() + "####"
                + actualResult.getImplementDatetime();
    }
    @Lock(LockType.READ)
    @POST
    @Path("report/out")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response reportOut(@QueryParam("ledgerId") Long ledgerId, @QueryParam("eId") List<Long> eid, @QueryParam("oId") List<Long> oid, @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, ListWrapper<Long> actualResultIds, @QueryParam("authId") Long authId) {

        try {
            Date now = new Date();
            Map<String, Replacer> dateReplacer = new HashMap<>();

            // 開始日時
            if (!StringUtils.isEmpty(fromDate)) {
                dateReplacer.put("TAG_START_DATE", getReplacer(sf.parse(fromDate)));
            }

            // 完了日時
            if (!StringUtils.isEmpty(toDate)) {
                dateReplacer.put("TAG_END_DATE", getReplacer(sf.parse(toDate)));
            }


            List<EquipmentEntity> equipmentEntities = equipmentEntityFacadeREST.findAll();
            Map<Long, EquipmentEntity> equipmentEntityMap = equipmentEntities.stream().collect(toMap(EquipmentEntity::getEquipmentId, Function.identity(), (a, b) -> a));

            List<OrganizationEntity> organizationEntities = organizationEntityFacadeREST.findAll();
            Map<Long, OrganizationEntity> organizationEntityMap = organizationEntities.stream().collect(toMap(OrganizationEntity::getOrganizationId, Function.identity(), (a, b) -> a));

            // 指定設備
            Map<Long, Tuple<EquipmentEntity, Replacer>> equipmentEntityReplacerMap = new HashMap<>();
            if (Objects.nonNull(eid) && !eid.isEmpty()) {
                Map<Long, List<EquipmentEntity>> equipmentParentMap = equipmentEntities.stream().collect(groupingBy(EquipmentEntity::getParentEquipmentId));
                Set<Long> typeId
                        = equipmentEntityFacadeREST.getEquipmentType()
                        .stream()
                        .filter(equipmentTypeEntity -> Objects.equals(EquipmentTypeEnum.MANUFACTURE, equipmentTypeEntity.getName()) || Objects.equals(EquipmentTypeEnum.MEASURE, equipmentTypeEntity.getName()))
                        .map(EquipmentTypeEntity::getEquipmentTypeId)
                        .collect(toSet());

                formFacadeREST.getEquipmentDescendants(new HashSet<>(eid), equipmentParentMap)
                        .values()
                        .stream()
                        .filter(entry -> typeId.contains(entry.getEquipmentTypeId()))
                        .forEach(equipmentEntity -> equipmentEntityReplacerMap.put(equipmentEntity.getEquipmentId(), new Tuple<>(equipmentEntity, getReplacer(equipmentEntity))));
            }

            LedgerEntity ledgerEntity = this.find(ledgerId);
            File fromFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, ledgerEntity.getLedgerPhysicalFileName()));
            String ext = ledgerEntity.getLedgerFileName().substring(ledgerEntity.getLedgerFileName().lastIndexOf("."));
            // テンプレートファイルの存在確認
            if (!fromFile.isFile()) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            LedgerConditionEntity ledgerConditionEntity = ledgerEntity.getLedgerCondition();

            // カンバンID一覧を指定して、工程実績情報一覧を取得する。
            List<ActualResultEntity> actualResultEntities
                    = actualResultEntityFacadeREST
                    .find(actualResultIds.getList(), authId);

            // 工程順
            List<WorkflowEntity> workflowEntities
                    = workflowEntityFacadeREST
                    .find(actualResultEntities
                            .stream()
                            .map(ActualResultEntity::getWorkflowId)
                            .distinct()
                            .collect(toList()), authId
                    );

            // 工程順タグを抽出
            Map<Long, Map<String, Replacer>> workflowTagMap
                    = workflowEntities
                    .stream()
                    .collect(toMap(
                            WorkflowEntity::getWorkflowId,
                        workflowEntity ->
                                new HashMap<>() {{
                                    put("TAG_WORKFLOW_NAME", getReplacer(workflowEntity.getWorkflowName())); // 工程順名

                                    // 工程順版数
                                    String rev = workflowEntity.getWorkflowRevision();
                                    put("TAG_WORKFLOW_REVISION", getReplacer(StringUtils.isEmpty(rev) ? "1" : rev)); // 工程順版数

                                    put("TAG_WORKFLOW_UPDATE_DATE", getReplacer(workflowEntity.getUpdateDatetime())); // 工程順更新日

                                    // 工程順更新者
                                    OrganizationEntity organizationEntity = organizationEntityMap.get(workflowEntity.getUpdatePersonId());
                                    if (Objects.nonNull(organizationEntity)) {
                                        put("TAG_WORKFLOW_UPDATER", getReplacer(organizationEntity.getOrganizationName()));
                                    }
                                }}));

            // 工程実績付加情報取得
            final Map<Long, List<ActualAditionEntity>> ActualAditionEntityMap = new HashMap<>();
            if (!actualResultEntities.isEmpty()) {
                TypedQuery<ActualAditionEntity> actualAdditionEntityTypedQuery = this.em.createNamedQuery("ActualAditionEntity.findByActualIDWithoutImage", ActualAditionEntity.class);
                actualAdditionEntityTypedQuery.setParameter("actualID", actualResultEntities.stream().map(ActualResultEntity::getActualId).collect(toList()));

                List<ActualAditionEntity> additionEntities = actualAdditionEntityTypedQuery.getResultList();
                ActualAditionEntityMap.putAll(additionEntities.stream().collect(groupingBy(ActualAditionEntity::getActualId)));
            }

            // 設備プロパティの取得
            List<Long> findEquipmentIds
                    = actualResultEntities
                    .stream()
                    .map(ActualResultEntity::getEquipmentId)
                    .filter(equipmentId -> !equipmentEntityReplacerMap.containsKey(equipmentId)) // 未登録な物のみ取得
                    .distinct()
                    .toList();

            if (!findEquipmentIds.isEmpty()) {
                findEquipmentIds
                        .stream()
                        .map(equipmentEntityMap::get)
                        .filter(Objects::nonNull)
                        .forEach(entity -> equipmentEntityReplacerMap.put(entity.getEquipmentId(), new Tuple<>(entity, getReplacer(entity))));
            }

            // 関連する工程情報を取得
            Map<Long, Map<String, Replacer>> workTeramMap
                    = workEntityFacadeREST
                    .find(actualResultEntities
                            .stream()
                            .map(ActualResultEntity::getWorkId)
                            .distinct()
                            .collect(toList()))
                    .stream()
                    .collect(toMap(
                            WorkEntity::getWorkId,
                            (workEntity) -> {
                                List<CheckInfoEntity> checkInfoEntities = JsonUtils.jsonToObjects(workEntity.getWorkCheckInfo(), CheckInfoEntity[].class);
                                return checkInfoEntities
                                        .stream()
                                        .filter(checkInfoEntity -> Objects.nonNull(checkInfoEntity.getTag()))
                                        .collect(toMap(
                                                checkInfoEntity -> WorkPropertyCategoryEnum.CUSTOM.equals(checkInfoEntity.getCat()) ? checkInfoEntity.getTag() + "_CUSTOM_TERM" : checkInfoEntity.getTag() + "_TERM",
                                                checkInfoEntity -> getReplacer(checkInfoEntity.getKey()),
                                                (a, b) -> a));
                            }));

            // 工程実績毎のタグを抽出
            Set<Long> useEquipmentIdSet = new HashSet<>();
            List<Tuple<List<ActualResultEntity>, Map<String, Replacer>>> replaceMaps = new ArrayList<>();
            actualResultEntities
                    .stream()
                    .collect(groupingBy(this::createGroupKey, toList()))
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(actuals -> actuals.getFirst().getImplementDatetime()))
                    .forEach(actuals -> {
                        ActualResultEntity actualResultEntity = actuals.getFirst();
                        // 工程順を追加
                        Map<String, Replacer> replaceMap = new HashMap<>(workflowTagMap.getOrDefault(actualResultEntity.getWorkflowId(), new HashMap<>()));

                        // 工程実績を変更
                        replaceMap.put("TAG_WORK_NAME", getReplacer(actualResultEntity.getWorkName())); // 工程
                        replaceMap.put("TAG_WORK_ORGANIZATION", getReplacer(actualResultEntity.getOrganizationName())); // 作業者
                        replaceMap.put("TAG_HIS_ORGANIZATION", getReplacer(actualResultEntity.getOrganizationName())); // 作業者
                        replaceMap.put("TAG_WORK_ACTUAL_END", getReplacer(actualResultEntity.getImplementDatetime())); // 日付
                        replaceMap.put("TAG_WORK_EQUIPMENT", getReplacer(actualResultEntity.getEquipmentName())); // 作業設備
                        replaceMap.put("TAG_HIS_EQUIPMENT", getReplacer(actualResultEntity.getEquipmentName())); // 作業設備

                        Tuple<EquipmentEntity, Replacer> equipment = equipmentEntityReplacerMap.get(actualResultEntity.getEquipmentId());
                        if (Objects.nonNull(equipment)) {
                            useEquipmentIdSet.add(equipment.getLeft().getEquipmentId());
                            replaceMap.put("TAG_WORK_EQUIPMENT_IDENTIFY", getReplacer(equipment.getLeft().getEquipmentIdentify())); // 設備管理名
                            replaceMap.put("TAG_WORK_EQUIPMENT_PROPERTY", equipment.getRight()); // 設備追加項目
                            replaceMap.put("TAG_WORK_EQUIPMENT_LAST_CALIBRATION_DATE", getReplacer(equipment.getLeft().getCalLastDate())); // 前回の校正日
                            replaceMap.put("TAG_WORK_EQUIPMENT_NEXT_CALIBRATION_DATE", getReplacer(equipment.getLeft().getCalNextDate())); // 次回の校正日
                            OrganizationEntity organizationEntity = organizationEntityMap.get(equipment.getLeft().getUpdatePersonId());
                            if (Objects.nonNull(organizationEntity)) {
                                replaceMap.put("TAG_WORK_EQUIPMENT_CALIBRATION_EXECUTOR", getReplacer(organizationEntity.getOrganizationIdentify())); // 校正者
                            }
                        }

                        // 作業項目名を登録
                        actuals.forEach(actual -> replaceMap.putAll(workTeramMap.computeIfAbsent(actual.getWorkId(), id ->new HashMap<>())));

                        // 品質トレサ情報設定
                        actuals.stream()
                                .map(ActualResultEntity::getActualAddInfo)
                                .filter(Objects::nonNull)
                                .map(addInfo -> JsonUtils.jsonToObjects(addInfo, ActualPropertyEntity[].class))
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .filter(item -> CustomPropertyTypeEnum.TYPE_TRACE.equals(item.getActualPropType()))
                                .filter(item -> Objects.nonNull(item.getActualPropValue()))
                                .forEach(item -> {
                                    // 入力値が日付時かの判定
                                    Date date = DateUtils.parse(item.getActualPropValue());
                                    if (Objects.isNull(date)) {
                                        replaceMap.put(item.getActualPropName(), getReplacer(item.getActualPropValue()));
                                    } else {
                                        replaceMap.put(item.getActualPropName(), getReplacer(date));
                                    }
                                });

                        // 工程実績付加情報
                        ActualAditionEntityMap
                                .computeIfAbsent(actualResultEntity.getActualId(), key -> new ArrayList<>())
                                .forEach(item -> replaceMap.put(item.getTag(), getReplacer(item.getActualAditionId())));
                        replaceMaps.add(new Tuple<>(actuals, replaceMap));
                    });

            // 実績の無い設備情報を登録
            equipmentEntityReplacerMap
                    .entrySet()
                    .stream()
                    .filter(entrySet -> !useEquipmentIdSet.contains(entrySet.getKey()))
                    .forEach(entity -> {
                        Map<String, Replacer> replaceMap = new HashMap<>();
                        Tuple<EquipmentEntity, Replacer> equipment = entity.getValue();
                        replaceMap.put("TAG_WORK_EQUIPMENT_IDENTIFY", getReplacer(equipment.getLeft().getEquipmentIdentify())); // 設備管理名
                        replaceMap.put("TAG_WORK_EQUIPMENT_PROPERTY", equipment.getRight()); // 設備追加項目
                        replaceMap.put("TAG_WORK_EQUIPMENT_LAST_CALIBRATION_DATE", getReplacer(equipment.getLeft().getCalLastDate())); // 前回の校正日
                        replaceMap.put("TAG_WORK_EQUIPMENT_NEXT_CALIBRATION_DATE", getReplacer(equipment.getLeft().getCalNextDate())); // 次回の校正日
                        OrganizationEntity organizationEntity = organizationEntityMap.get(equipment.getLeft().getUpdatePersonId());
                        if (Objects.nonNull(organizationEntity)) {
                            replaceMap.put("TAG_WORK_EQUIPMENT_CALIBRATION_EXECUTOR", getReplacer(organizationEntity.getOrganizationIdentify())); // 校正者
                        }

                        replaceMaps.add(new Tuple<>(null, replaceMap));
                    });

            if (Objects.equals(ledgerConditionEntity.getLedgerType(), LedgerTypeEnum.AGGREGATION)) {
                // ************************ 集約 *******************
                final List<Map<String, Replacer>> replacerMapList
                        = replaceMaps
                        .stream()
                        .map(Tuple::getRight)
                        .collect(toList());
                replacerMapList.add(dateReplacer);

                fileManager.createDirectory(FileManager.Data.REPORT, ledgerEntity.getLedgerId().toString() + File.separator + "output");
                fileManager.createDirectory(FileManager.Data.REPORT, ledgerEntity.getLedgerId().toString() + File.separator + "output" + File.separator + new SimpleDateFormat("yyyyMM").format(now));
                String toFileName = ledgerEntity.getLedgerId().toString() + File.separator + "output" + File.separator + new SimpleDateFormat("yyyyMM").format(now) + File.separator + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + ext;
                File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, toFileName));

                // 帳票出力
                if (!exportLedger(fromFile, toFile, replacerMapList, ledgerConditionEntity.getNoRemoveTags())) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
                }

                final List<Date> implementDate
                        = replaceMaps
                        .stream()
                        .map(Tuple::getLeft)
                        .filter(Objects::nonNull)
                        .map(List::getFirst)
                        .map(ActualResultEntity::getImplementDatetime)
                        .sorted()
                        .toList();

                final String organizationIds
                        = JsonUtils.objectsToJson(
                                replaceMaps
                                        .stream()
                                        .map(Tuple::getLeft)
                                        .filter(Objects::nonNull)
                                        .map(List::getFirst)
                                        .map(ActualResultEntity::getOrganizationId)
                                        .distinct()
                                        .collect(toList()));

                final String equipmentIds
                        = JsonUtils.objectsToJson(
                        replaceMaps
                                .stream()
                                .map(Tuple::getRight)
                                .map(map -> map.get("TAG_WORK_EQUIPMENT_PROPERTY"))
                                .filter(EquipmentPropertyReplacer.class::isInstance)
                                .map(EquipmentPropertyReplacer.class::cast)
                                .map(EquipmentPropertyReplacer::getEquipmentEntity)
                                .map(EquipmentEntity::getEquipmentId)
                                .distinct()
                                .collect(toList()));

                final String keyword
                        = JsonUtils.objectsToJson(
                        ledgerConditionEntity
                                .getKeyTag()
                                .stream()
                                .filter(entity -> !entity.isEmpty())
                                .map(entity -> {
                                    String tagName = entity.getValue();
                                    Matcher matcher = tagPattern.matcher(tagName);
                                    if (!matcher.find() || StringUtils.isEmpty(matcher.group("tag"))) {
                                        return new NameValueEntity(tagName, "");
                                    }
                                    String value
                                            = replacerMapList
                                            .stream()
                                            .map(l -> l.get(matcher.group("tag")))
                                            .filter(Objects::nonNull)
                                            .map(l->l.getValue(matcher.group("format")))
                                            .filter(StringUtils::nonEmpty)
                                            .findFirst()
                                            .orElse("");
                                    return new NameValueEntity(tagName, value);
                                })
                                .collect(toList()));

                LedgerFileEntity ledgerFileEntity = new LedgerFileEntity(
                        ledgerEntity.getLedgerId(),
                        authId,
                        keyword,
                        new Date(),
                        implementDate.isEmpty() ? null : implementDate.getFirst(),
                        implementDate.isEmpty() ? null : implementDate.getLast(),
                        organizationIds,
                        equipmentIds,
                        toFileName);


                em.persist(ledgerFileEntity);
                ledgerEntity.setLastImplementDatetime(now);
                em.flush();
                return Response.ok().entity(ResponseEntity.success()).build();

            }

            // キー
            final List<Matcher> keys
                    = ledgerConditionEntity
                    .getKeyTag()
                    .stream()
                    .map(NameValueEntity::getValue)
                    .filter(Objects::nonNull)
                    .map(tagPattern::matcher)
                    .filter(Matcher::find)
                    .filter(matcher -> StringUtils.nonEmpty(matcher.group("tag")))
                    .toList();

            if (Objects.equals(ledgerConditionEntity.getLedgerType(), LedgerTypeEnum.KEY_AGREGATION) && !keys.isEmpty()) {
                // ************************ キー集約 *******************
                Map<String, List<Tuple<List<ActualResultEntity>, Map<String, Replacer>>>> tagMaps
                        = replaceMaps
                        .stream()
                        .map(entity -> new Tuple<>(
                                keys.stream().map(key -> entity.getRight().getOrDefault(key.group("tag"), new TextReplacer("")).getValue(key.group("format"))).collect(toList()),
                                entity
                        ))
                        .filter(item -> item.getLeft().stream().anyMatch(StringUtils::nonEmpty))
                        .collect(groupingBy(
                                item -> String.join("####", item.getLeft()),
                                collectingAndThen(toList(), list -> list.stream().map(Tuple::getRight).collect(toList()))));

                int no = 0;
                for (List<Tuple<List<ActualResultEntity>, Map<String, Replacer>>> tagMap : tagMaps.values()) {
                    fileManager.createDirectory(FileManager.Data.REPORT, ledgerEntity.getLedgerId().toString() + File.separator + "output");
                    fileManager.createDirectory(FileManager.Data.REPORT, ledgerEntity.getLedgerId().toString() + File.separator + "output" + File.separator + new SimpleDateFormat("yyyyMM").format(now));
                    String toFileName = ledgerEntity.getLedgerId().toString() + File.separator + "output" + File.separator + new SimpleDateFormat("yyyyMM").format(now) + File.separator + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + "_" + (no++) + ext;
                    File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, toFileName));

                    List<Map<String, Replacer>> replacerMapList
                            = tagMap
                            .stream()
                            .map(Tuple::getRight)
                            .collect(toList());
                    replacerMapList.add(dateReplacer);

                    // 帳票出力
                    if (!exportLedger(fromFile, toFile, replacerMapList, ledgerConditionEntity.getNoRemoveTags())) {
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
                    }

                    List<ActualResultEntity> actualResultEntityList
                            = tagMap
                            .stream()
                            .map(Tuple::getLeft)
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .toList();

                    final List<Date> implementDate
                            = actualResultEntityList
                            .stream()
                            .map(ActualResultEntity::getImplementDatetime)
                            .sorted()
                            .toList();

                    final String organizationIds
                            = JsonUtils.objectsToJson(
                            actualResultEntityList
                                    .stream()
                                    .map(ActualResultEntity::getOrganizationId)
                                    .distinct()
                                    .collect(toList()));

                    final String equipmentIds
                            = JsonUtils.objectsToJson(
                            replacerMapList
                                    .stream()
                                    .map(map -> map.get("TAG_WORK_EQUIPMENT_PROPERTY"))
                                    .filter(EquipmentPropertyReplacer.class::isInstance)
                                    .map(EquipmentPropertyReplacer.class::cast)
                                    .map(EquipmentPropertyReplacer::getEquipmentEntity)
                                    .map(EquipmentEntity::getEquipmentId)
                                    .distinct()
                                    .collect(toList()));

                    final String keyword
                            = JsonUtils.objectsToJson(
                            ledgerConditionEntity
                                    .getKeyTag()
                                    .stream()
                                    .filter(entity -> !entity.isEmpty())
                                    .map(entity -> {
                                        String tagName = entity.getValue();
                                        Matcher matcher = tagPattern.matcher(tagName);
                                        if (!matcher.find() || StringUtils.isEmpty(matcher.group("tag"))) {
                                            return new NameValueEntity(tagName, "");
                                        }
                                        String value
                                                = replacerMapList
                                                .stream()
                                                .map(l -> l.get(matcher.group("tag")))
                                                .filter(Objects::nonNull)
                                                .map(l->l.getValue(matcher.group("format")))
                                                .filter(StringUtils::nonEmpty)
                                                .findFirst()
                                                .orElse("");
                                        return new NameValueEntity(tagName, value);
                                    })
                                    .collect(toList()));

                    LedgerFileEntity ledgerFileEntity = new LedgerFileEntity(
                            ledgerEntity.getLedgerId(),
                            authId,
                            keyword,
                            new Date(),
                            implementDate.isEmpty() ? null : implementDate.getFirst(),
                            implementDate.isEmpty() ? null : implementDate.getLast(),
                            organizationIds,
                            equipmentIds,
                            toFileName);

                    em.persist(ledgerFileEntity);
                    ledgerEntity.setLastImplementDatetime(now);
                    em.flush();
                }
            } else {
                // ************************ 個別 *******************
                int no = 0;
                for (Tuple<List<ActualResultEntity>, Map<String, Replacer>> tagMap : replaceMaps) {
                    if (Objects.isNull(tagMap.getLeft())) {
                        continue;
                    }

                    fileManager.createDirectory(FileManager.Data.REPORT, ledgerEntity.getLedgerId().toString() + File.separator + "output");
                    fileManager.createDirectory(FileManager.Data.REPORT, ledgerEntity.getLedgerId().toString() + File.separator + "output" + File.separator + new SimpleDateFormat("yyyyMM").format(now));
                    String toFileName = ledgerEntity.getLedgerId().toString() + File.separator + "output" + File.separator + new SimpleDateFormat("yyyyMM").format(now) + File.separator + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + "_" + (no++) + ext;
                    File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, toFileName));

                    List<Map<String, Replacer>> replacerMapList
                            = Arrays.asList(tagMap.getRight(), dateReplacer);
                    // 帳票出力
                    if (!exportLedger(fromFile, toFile, replacerMapList, ledgerConditionEntity.getNoRemoveTags())) {
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
                    }

                    final String keyword
                            = JsonUtils.objectsToJson(
                            ledgerConditionEntity
                                    .getKeyTag()
                                    .stream()
                                    .filter(entity -> !entity.isEmpty())
                                    .map(entity -> {
                                        String tagName = entity.getValue();
                                        Matcher matcher = tagPattern.matcher(tagName);
                                        if (!matcher.find() || StringUtils.isEmpty(matcher.group("tag"))) {
                                            return new NameValueEntity(tagName, "");
                                        }
                                        String value
                                                = replacerMapList
                                                .stream()
                                                .map(l -> l.get(matcher.group("tag")))
                                                .filter(Objects::nonNull)
                                                .map(l -> l.getValue(matcher.group("format")))
                                                .filter(StringUtils::nonEmpty)
                                                .findFirst()
                                                .orElse("");
                                        return new NameValueEntity(tagName, value);
                                    })
                                    .collect(toList()));

                    ActualResultEntity actualResultEntity = tagMap.getLeft().getFirst();
                    LedgerFileEntity ledgerFileEntity = new LedgerFileEntity(
                            ledgerEntity.getLedgerId(),
                            authId,
                            keyword,
                            new Date(),
                            actualResultEntity.getImplementDatetime(),
                            actualResultEntity.getImplementDatetime(),
                            JsonUtils.objectsToJson(Collections.singletonList(actualResultEntity.getOrganizationId())),
                            JsonUtils.objectsToJson(Collections.singletonList(actualResultEntity.getEquipmentId())),
                            toFileName);
                    em.persist(ledgerFileEntity);
                    ledgerEntity.setLastImplementDatetime(now);
                    em.flush();
                }
            }
            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
    }
}
