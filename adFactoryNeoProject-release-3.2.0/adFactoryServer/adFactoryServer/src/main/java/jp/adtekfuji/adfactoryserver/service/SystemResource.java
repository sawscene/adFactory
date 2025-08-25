/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.system.PackageEntity;
import jp.adtekfuji.adFactory.entity.system.SoftwareUpdateEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.system.BrowserLog;
import jp.adtekfuji.adfactoryserver.entity.system.TroubleReportConfig;
import jp.adtekfuji.adfactoryserver.entity.system.TroubleReportEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.service.mail.MailProperty;
import jp.adtekfuji.adfactoryserver.service.mail.MailUtils;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;

/**
 * REST Web Service
 *
 * @author s-heya
 */
@Singleton
@Path("/system")
public class SystemResource {

    private static final String PATH_DEPLOY = System.getenv("ADFACTORY_HOME") + File.separator + "deploy";
    private static final String PATH_SOFTWAREUPDATE = PATH_DEPLOY + File.separator + "softwareupdate.xml";
    private static final String URI_DEPLOY = "/adFactoryServer/deploy/";
    private static final String REGEX_FILENAME = "Setup.*_v\\d+\\.\\d+\\.\\d+(-)?[a-zA-Z]*\\.\\d+.*\\.exe";
    private static final String PATH_LOG = System.getenv("ADFACTORY_HOME") + File.separator + "logs" + File.separator + "adProductWeb";

    private static final String CHARSET = "MS932";

    private final Logger logger = LogManager.getLogger();

    /**
     * Creates a new instance of SystemResource
     */
    public SystemResource() {
    }

    /**
     * ソフトウェア更新情報を取得する。
     *
     * @param authId 認証ID
     * @return SoftwareUpdateEntity
     */
    @Lock(LockType.READ)
    @GET
    @Path("/softwareupdate")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response findSoftwareUpdate(@QueryParam("authId") Long authId) {
        SoftwareUpdateEntity softwareUpdate = null;
        try {
            logger.info(SystemResource.class.getSimpleName() + "::getSoftwareUpdate start.");

            File xml = new File(PATH_SOFTWAREUPDATE);
            if (xml.exists()) {
                softwareUpdate = JAXB.unmarshal(xml, SoftwareUpdateEntity.class);

                for (PackageEntity entity : softwareUpdate.getPackageCollection()) {
                    Pattern p = Pattern.compile(entity.getName() + REGEX_FILENAME);
                    long lastModified = 0;
                    String fileName = null;
                    long size = 0;

                    File[] files = new File(PATH_DEPLOY).listFiles();
                    for (File file : files) {
                        if (file.isDirectory()) {
                            continue;
                        }

                        Matcher m = p.matcher(file.getName());
                        if (m.matches() && lastModified < file.lastModified()) {
                            lastModified = file.lastModified();
                            fileName = file.getName();
                            size = file.length();
                        }
                    }

                    if (!StringUtils.isEmpty(fileName)) {
                        String version = fileName.substring(fileName.indexOf('_') + 2, fileName.lastIndexOf('.')).replace("-", "");
                        entity.setVersion(version);
                        entity.setPath(URI_DEPLOY + fileName);
                        entity.setSize(size);
                        logger.info("Latest Version: {}", entity.getPath());
                    }
                }
            } else {
                softwareUpdate = new SoftwareUpdateEntity();
            }

            try (StringWriter sw = new StringWriter()) {
                JAXB.marshal(softwareUpdate, sw);
                return Response.ok(sw.toString()).build();
            }
        } catch (Exception ex) {
            logger.fatal(ex.getMessage());
            return Response.status(Status.NOT_FOUND).build();
        } finally {
            logger.info(SystemResource.class.getSimpleName() + "::getSoftwareUpdate end.");
        }
    }

    /**
     * ライセンスのオプション情報取得
     *
     * @param authId 認証ID
     * @return システムオプションエンティティ
     */
    @Lock(LockType.READ)
    @GET
    @Path("/option")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<SystemOptionEntity> findLicenseOptions(@QueryParam("authId") Long authId) {
        logger.info("getLicenseOptions");
        Map<String, Boolean> options = LicenseManager.getInstance().getLicenseOptions();
        List<SystemOptionEntity> entities = new ArrayList<>();
        for (Map.Entry<String, Boolean> e : options.entrySet()) {
            entities.add(new SystemOptionEntity(e.getKey(), e.getValue()));
        }
        return entities;
    }

    /**
     * ライセンスのオプション情報取得
     *
     * @param authId 認証ID
     * @return システムオプションエンティティ
     */
    @Lock(LockType.READ)
    @GET
    @Path("/licenseNum")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResultResponse getLicenseNum(@QueryParam("authId") Long authId) {
        logger.info("getLicenseNum");
        StringBuilder sb = new StringBuilder();
        sb.append(LicenseManager.getInstance().getMaxJoinTerminal());
        sb.append(",");
        sb.append(LicenseManager.getInstance().getMaxJoinLite());
        sb.append(",");
        sb.append(LicenseManager.getInstance().getMaxJoinReporter());
        return ResultResponse.success().result(sb.toString());
    }

    /**
     * ライセンスのオプション情報取得
     *
     * ※．非推奨 (使用する場合は注意すること)
     *
     * @param optionName オプション名
     * @param authId 認証ID
     * @return システムオプションエンティティ
     */
    @Lock(LockType.READ)
    @GET
    @Path("/option/{optionName}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public SystemOptionEntity findLicenseOption(@PathParam("optionName") String optionName, @QueryParam("authId") Long authId) {
        logger.info("getLicenseOption:{}", optionName);
        Boolean enable = LicenseManager.getInstance().getLicenseOption(optionName);
        return new SystemOptionEntity(optionName, enable);
    }

    /**
     * システム設定を取得する。
     *
     * @param authId 認証ID
     * @return システム設定
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<SystemPropEntity> findSystemProperties(@QueryParam("authId") Long authId) {
        logger.info("getSystemProperties");
        List<SystemPropEntity> props = new ArrayList<>();
        try {
            Properties properties = FileManager.getInstance().getSystemProperties();
            for (Object keyObject: properties.keySet()) {
                String key = keyObject.toString();
                String value = properties.getProperty(key, "");

                Optional<SystemPropEntity> opt = props.stream().filter(p -> key.equals(p.getKey())).findFirst();
                if (opt.isPresent()) {
                    props.remove(opt.get());
                }
                props.add(new SystemPropEntity(key, value));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return props;
    }

    /**
     * キーを指定して、システム設定のプロパティ値を取得する。
     *
     * ※．非推奨 (使用する場合は注意すること)
     *
     * @param key プロパティのキー
     * @param authId 認証ID
     * @return プロパティの値
     */
    @Lock(LockType.READ)
    @GET
    @Path("/property")
    @Produces(MediaType.TEXT_PLAIN)
    @ExecutionTimeLogging
    public String findSystemProperty(@QueryParam("key") String key, @QueryParam("authId") Long authId) {
        logger.info("getSystemProperty: {}", key);
        Properties properties = FileManager.getInstance().getSystemProperties();
        if (!properties.containsKey(key)) {
            logger.info(key + " does not exist.");
            return "";
        }
        return properties.getProperty(key, "");
    }

    /**
     * 障害レポートをADTEKサポート宛にメール送信する。
     *
     * @param entity 障害レポートエンティティ
     * @param authId 認証ID
     * @return メール送信結果
     */
    @POST
    @Path("/trouble")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response sendTroubleReport(TroubleReportEntity entity, @QueryParam("authId") Long authId) {
        logger.info("sendTroubleReport: {}", entity);
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
            prop.setCharset(CHARSET);

            String from = props.getReportMailFrom();
            String to = props.getReportMailTo();

            // 件名
            String subject = String.format("%s %s", props.getReportMailSubject(), entity.getReportId());

            // 本文
            StringBuilder content = new StringBuilder();
            String xml = entityToXmlString(entity, CHARSET, true); // 障害レポートを成形されたXML文字列にする。
            if (!props.getReportMailContent().isEmpty()) {
                content.append(props.getReportMailContent());
                content.append(System.lineSeparator());
                content.append(System.lineSeparator());
            }
            content.append(xml);

            // 添付ファイル
            List<File> files = new ArrayList();
            if (Objects.nonNull(entity.getTlogFile()) && !entity.getTlogFile().isEmpty()) {
                // TLOGファイル
                java.nio.file.Path tlogPath = Paths.get(props.getTlogFtpRootPath(), "data/tlog", entity.getTlogFile());
                File tlogFile = new File(tlogPath.toString());
                if (!tlogFile.exists()) {
                    // 指定されたTLOGファイルがない。
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
                }
                files.add(tlogFile);
            }

            // メール送信
            MailUtils mail = new MailUtils(prop);
            ServerErrorTypeEnum mailResult = mail.send(from, to, subject, content.toString(), files, false);
            if (ServerErrorTypeEnum.SUCCESS.equals(mailResult)) {
                return Response.ok().entity(ResponseEntity.success()).build();
            } else {
                // メール送信失敗
                return Response.serverError().entity(ResponseEntity.failed(mailResult)).build();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // サーバー処理エラー
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * エンティティをXML文字列に変換する。
     *
     * @param entity エンティティ
     * @param encoding 文字エンコード
     * @param isFormat 改行とインデントを使用して出力するか
     * @return XML文字列
     */
    @Lock(LockType.READ)
    private String entityToXmlString(Object entity, String encoding, boolean isFormat) {
        String result = "";
        try {
            JAXBContext context = JAXBContext.newInstance(entity.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isFormat); // 改行とインデントを使用して出力する。
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            // https://stackoverflow.com/questions/69562925/jaxb-marshaller-cannot-set-property-for-characterescapehandler
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
                @Override
                public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException {
                    writer.write(ac, i, j);
                }
            });

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(entity, baos);
            result = baos.toString();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * ディレクトリを作成する。
     *
     * @param target ディレクトリパス
     */
    public boolean createDirectory(String target) {
        File file = new File(target);
        if (file.exists()) {
            return true;
        }

        try {
            Files.createDirectories(file.toPath());
            return true;
        } catch (Exception ex) {
            logger.fatal(ex,ex);
            return false;
        }
    }

    static interface WriteFile {
        void apply(FileWriter write) throws IOException;
    }
    public boolean OpenFile(String target, WriteFile writeFile) {
        File file = new File(target);
        if (!file.exists() || !file.isFile()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException ex) {
                logger.fatal(ex, ex);
                return false;
            }
        }

        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return false;
        }
        try(FileWriter fw = new FileWriter(file, true)) {
            writeFile.apply(fw);
            fw.flush();
            return true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
            return false;
        }
    }

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @POST
    @Path("upload")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response upload(@Context HttpServletRequest httpRequest,  BrowserLog entity) {
        if (StringUtils.isEmpty(entity.getMsg())) {
            return Response.ok().build();
        }

        final String directoryPath
                = PATH_LOG
                + File.separator
                + (!StringUtils.isEmpty(entity.getEqId()) ? entity.getEqId() : httpRequest.getRemoteAddr().replace(':','_'));

        final String FilePath = directoryPath + File.separator + sdf.format(new Date()) + ".log";
        if (createDirectory(directoryPath)) {
            OpenFile(FilePath, fw -> fw.write(entity.getMsg()));
        }

        return Response.ok().build();
    }

}
