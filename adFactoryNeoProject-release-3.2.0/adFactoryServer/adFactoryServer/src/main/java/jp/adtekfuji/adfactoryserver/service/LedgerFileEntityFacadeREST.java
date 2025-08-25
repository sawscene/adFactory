package jp.adtekfuji.adfactoryserver.service;

import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerFileSearchEntity;
import jp.adtekfuji.adFactory.entity.ledger.NameValueEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.ledger.LedgerFileEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.function.Function;


@Singleton
@Path("ledger/file")
public class LedgerFileEntityFacadeREST extends AbstractFacade<LedgerFileEntity> {
    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    private static final Logger logger = LogManager.getLogger();

    final static FileManager fileManager = FileManager.getInstance();

    public LedgerFileEntityFacadeREST() {
        super(LedgerFileEntity.class);
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
     * 指定した帳票ファイルIDの情報を取得
     * @param ledgerId 階層ID
     * @param authId 認証ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public LedgerFileEntity find(@PathParam("id") Long ledgerId, @QueryParam("authId") Long authId) throws Exception {
        try {
            logger.info("find: ledgerId={}, authId={}", ledgerId, authId);
            LedgerFileEntity ret = this.find(ledgerId);

            // ファイルの存在確認
            File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, ret.getFilePath()));
            if (toFile.exists()) {
                return ret;
            }

            this.remove(ret);
            return null;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 帳票ファイルリストを取得
     * @param ledgerFileSearchEntity 検索条件
     * @param authId 承認者
     * @return 帳票ファイルリスト
     */
    @Lock(LockType.READ)
    @POST
    @Path("children")
    @Produces({"application/xml", "application/json"})
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<LedgerFileEntity> findChild(LedgerFileSearchEntity ledgerFileSearchEntity, @QueryParam("authId") Long authId) {
        logger.info("find: ledgerIds={}, authId={}", ledgerFileSearchEntity, authId);
        try {
            if (Objects.isNull(ledgerFileSearchEntity.getLedgerIds()) || ledgerFileSearchEntity.getLedgerIds().isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sql = new StringBuilder("SELECT l.* FROM trn_ledger_file l");

            int index = 0;
            List<Function<Query, Query>> parameterSetter = new ArrayList<>();
            List<String> condition = new ArrayList<>();
            {
                final int num = ++index;
                condition.add("l.ledger_id = ANY(?" + (num) + ")");
                try (Connection connection = this.em.unwrap(Connection.class)){
                    final java.sql.Array idArray = connection.createArrayOf("bigint", ledgerFileSearchEntity.getLedgerIds().toArray());
                    parameterSetter.add(query->query.setParameter(num, idArray));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    return new ArrayList<>();
                }
            }

            List<NameValueEntity> keywords = ledgerFileSearchEntity.getKeywords();
            if (Objects.nonNull(keywords)) {
                for (int n=0; n<keywords.size(); ++n) {
                    final String label = "label" + n;
                    final String name = keywords.get(n).getName();
                    sql.append(" JOIN jsonb_to_recordset(l.key_word) AS ")
                            .append(label)
                            .append("(name TEXT, value TEXT) ON ")
                            .append(label)
                            .append(".name='")
                            .append(keywords.get(n).getName())
                            .append("'");

                    sql.append(" AND ")
                            .append(label)
                            .append(".value LIKE '%")
                            .append(keywords.get(n).getValue())
                            .append("%'");
                }
            }


            if (Objects.nonNull(ledgerFileSearchEntity.getFromDatetime())) {
                final int num = ++index;
                condition.add("l.create_datetime>=?" + (num));
                parameterSetter.add(query->query.setParameter(num, ledgerFileSearchEntity.getFromDatetime()));
            }

            if (Objects.nonNull(ledgerFileSearchEntity.getToDatetime())) {
                final int num = ++index;
                condition.add("l.create_datetime<=?" + (num));
                parameterSetter.add(query->query.setParameter(num, ledgerFileSearchEntity.getToDatetime()));
            }
            sql.append(" WHERE ").append(String.join(" AND ", condition));

            if (Objects.nonNull(ledgerFileSearchEntity.getLimit())) {
                final int num = ++index;
                sql.append(" ORDER BY l.create_datetime DESC LIMIT(?").append(num).append(")");
                parameterSetter.add(query->query.setParameter(num, ledgerFileSearchEntity.getLimit()));
            }

            Query query = em.createNativeQuery(sql.toString(), LedgerFileEntity.class);
            for(Function<Query, Query> setter : parameterSetter ) {
                query = setter.apply(query);
            }

            List<LedgerFileEntity> ledgerFileEntities = query.getResultList();

            List<LedgerFileEntity> ret = new ArrayList<>();
            for (LedgerFileEntity ledgerFileEntity : ledgerFileEntities) {
                File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, ledgerFileEntity.getFilePath()));
                if (toFile.exists()) {
                    ret.add(ledgerFileEntity);
                } else {
                    this.remove(ledgerFileEntity);
                }
            }
            return ret;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定した階層IDの階層情報を削除する。
     *
     * @param ids    帳票ファイルID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        try {
            logger.info("remove: id={}, authId={}", ids, authId);
            // 階層情報を削除する。
            for (Long id : ids) {
                try {
                    LedgerFileEntity entity = this.find(id);
                    if (Objects.isNull(entity)) {
                        continue;
                    }

                    File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, entity.getFilePath()));
                    if (toFile.exists()) {
                        if (!toFile.delete()) {
                            logger.fatal("Error!!: File Delete Error {}", entity.getFilePath());
                        }
                    }
                    super.remove(entity);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 付加情報IDで実績付加情報のRAWデータを取得する
     *
     * @param id 付加情報ID
     * @param authId 認証ID
     * @return Response 帳票データ
     */
    @GET
    @Path("raw/{id}")
    @Produces("application/octet-stream")
    @ExecutionTimeLogging
    public Response getLedgerFile(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("download: id={}, authId={}", id, authId);
        try {
            LedgerFileEntity result = this.find(id, authId);
            if (Objects.isNull(result)) {
                return Response.status(404).
                        entity("FILE NOT FOUND").
                        type("text/plain").
                        build();
            }

            File toFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, result.getFilePath()));
            Response.ResponseBuilder builder = Response.ok(toFile);
            builder.header("Content-Disposition", "attachment; filename=" + toFile.getName());
            return builder.build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
}
