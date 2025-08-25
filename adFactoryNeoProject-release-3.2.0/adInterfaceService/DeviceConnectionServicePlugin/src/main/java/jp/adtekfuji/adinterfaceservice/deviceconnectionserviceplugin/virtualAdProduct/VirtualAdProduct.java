package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct;

import adtekfuji.clientservice.*;
import jp.adtekfuji.adFactory.entity.kanban.*;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.entity.search.ProducibleWorkKanbanCondition;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * 仮想adProduct
 */
public class VirtualAdProduct {

    private final String equipmentIdentify;         // 設備管理名(装置名)
    private final String organizationIdentify;      // 組織識別名(仮想作業者名)
    private final String password;
    private Long equipmentId = null;    // 設備ID
    private Long organizationId = null; // 組織ID
    private Long tid = 0L;

    private static final Logger logger = LogManager.getLogger();
    private static EquipmentInfoFacade equipmentInfoFacade = null;
    private static OrganizationInfoFacade organizationInfoFacade = null;
    private static KanbanInfoFacade kanbanInfoFacade = null;
    private static WorkKanbanInfoFacade workKanbanInfoFacade = null;
    private static SystemResourceFacade systemResourceFacade = null;
    private static InetAddress addr = null;
    private static Set<String> optionLicenses = null;

    /**
     * コンストラクタ
     * 
     * @param equipmentIdentify 設備管理名(装置名)
     * @param organizationIdentify 組織識別名(仮想作業者名)
     * @param password パスワード
     * @throws UnknownHostException 
     */
    private  VirtualAdProduct(String equipmentIdentify, String organizationIdentify, String password) throws UnknownHostException{
        if (Objects.isNull(equipmentInfoFacade))    equipmentInfoFacade = new EquipmentInfoFacade();
        if (Objects.isNull(addr))                    addr = InetAddress.getLocalHost();
        if (Objects.isNull(organizationInfoFacade)) organizationInfoFacade = new OrganizationInfoFacade();
        if (Objects.isNull(kanbanInfoFacade))       kanbanInfoFacade = new KanbanInfoFacade();
        if (Objects.isNull(workKanbanInfoFacade))   workKanbanInfoFacade = new WorkKanbanInfoFacade();
        if (Objects.isNull(systemResourceFacade))   systemResourceFacade = new SystemResourceFacade();

        this.equipmentIdentify = equipmentIdentify;
        this.organizationIdentify = organizationIdentify;
        this.password = password;
    }

    private boolean enableLicenses(String licensesName) {
        if (Objects.isNull(optionLicenses)) {
            optionLicenses
                    = systemResourceFacade.getLicenseOptions()
                    .stream()
                    .filter(SystemOptionEntity::getEnable)
                    .map(SystemOptionEntity::getOptionName)
                    .collect(toSet());
        }
        return optionLicenses.contains(licensesName);
    }

    /**
     * ログイン中の設備ID取得
     * @return 設備ID
     */
    public Long getLoginEquipmentId()
    {
        return this.equipmentId;
    }

    /**
     * ログイン中の組織ID取得
     * @return 組織ID
     */
    public Long getLoginOrganizationId()
    {
        return this.organizationId;
    }

    /**
     * 設備管理名(装置名)取得
     * @return 設備管理名(装置名)
     */
    public String getEquipmentIdentify()
    {
        return this.equipmentIdentify;
    }

    /**
     * 組織識別名(仮想作業者名)取得
     * @return 組織識別名(仮想作業者名)
     */
    public String getOrganizationIdentify()
    {
        return this.organizationIdentify;
    }

    /**
     * 仮想adProductインスタンス作成
     * 
     * @param equipmentIdentify 設備管理名(装置名)
     * @param organizationIdentify 組織識別名(仮想作業者名)
     * @param password パスワード
     * @return 新しいインスタンス
     */
    static public synchronized Optional<VirtualAdProduct> createInstance(String equipmentIdentify, String organizationIdentify, String password){
        try {
            return Optional.of(new VirtualAdProduct(equipmentIdentify, organizationIdentify, password));
        } catch (UnknownHostException e) {
            return Optional.empty();
        }
    }

    /**
     * 設備ログイン
     * @return
     */
    public EquipmentLoginResult loginEquipment(){
        EquipmentLoginResult result = null;
        for (int n=0; n<5; ++n) {
            result = this.loginEquipment(this.equipmentIdentify);
            if (result.getIsSuccess()) {
                return result;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }
        return result;
    }

    /**
     * 組織ログイン
     * @return
     */
    public OrganizationLoginResult loginOrganization()
    {
        OrganizationLoginResult result = null;
        for (int n=0; n<5; ++n) {
            result = this.loginOrganization(this.organizationIdentify, this.password);
            if (result.getIsSuccess()) {
                return result;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }
        return result;
    }

    /**
     * 設備ログイン
     * @param equipmentIdentify 設備識別名
     * @return ログイン結果
     */
    private EquipmentLoginResult loginEquipment(String equipmentIdentify) {
        logger.info("VirtualAdProduct Equipment Login : {}", equipmentIdentify);

        final EquipmentLoginRequest request = new EquipmentLoginRequest(EquipmentLoginRequest.LoginType.IDENT_NAME, EquipmentTypeEnum.TERMINAL, equipmentIdentify, addr.getHostAddress());
        EquipmentLoginResult result = equipmentInfoFacade.login(request);
        if (Objects.nonNull(result)) {
            logger.info("VirtualAdProduct Equipment LoginResult : {}", result.getErrorType());
            if (result.getIsSuccess()) {
                if (!result.getEquipmentId().equals(this.equipmentId)) {
                    this.tid = 0L;
                    this.equipmentId = result.getEquipmentId();
                }
            }
        } else {
            result = new EquipmentLoginResult();
            result.setErrorType(ServerErrorTypeEnum.SERVER_FETAL);
        }

        return result;
    }

    /**
     * 組織ログイン
     * @param organizationIdentify 組織識別名
     * @param password パスワード
     * @return ログイン結果
     */
    private OrganizationLoginResult loginOrganization(String organizationIdentify, String password) {
        logger.info("VirtualAdProduct User Login : {}", organizationIdentify);

        StringBuilder sb = new StringBuilder();
        OrganizationLoginResult result = organizationInfoFacade.login(organizationIdentify, password, sb);
        if (Objects.isNull(result)) {
            result = new OrganizationLoginResult();
            result.setErrorType(ServerErrorTypeEnum.SERVER_FETAL);
        } else {
            if (result.getIsSuccess()) {
                if (!result.getOrganizationId().equals(this.organizationId)) {
                    this.tid = 0L;
                    this.organizationId = result.getOrganizationId();
                }
            }
        }

        logger.info("VirtualAdProduct User Login Result : {}", result.getErrorType());
        result.setMessage(sb.toString());
        return result;
    }

    /**
     * 設備探索
     * @param condition
     * @param from
     * @param to
     * @return
     */
    public List<WorkKanbanInfoEntity> searchProductWorkKanban(ProducibleWorkKanbanCondition condition, long from, long to) {
        return workKanbanInfoFacade.findProductWorkKanban(condition, from, to);
    }

    /**
     * 作業開始
     * @param workKanbanIds 作業開始する工程ID
     * @return 実施結果
     */
    public ActualProductReportResult startWork(List<Long> workKanbanIds, Date startDateTime) {

        if (Objects.isNull(this.equipmentId) || Objects.isNull(this.organizationId)) {
            return null;
        }

        ActualProductReportEntity report = new ActualProductReportEntity();
        report.setTransactionId(this.tid);
        report.setEquipmentId(this.equipmentId);
        report.setOrganizationId(this.organizationId);
        report.setSupportMode(false);
        report.setIsSchedule(Boolean.TRUE);
        report.setStatus(KanbanStatusEnum.PLANNED);
        report.setLaterRework(false);

        report.setWorkKanbanCollection(workKanbanIds);
        report.setReportDatetime(startDateTime);
        report.setStatus(KanbanStatusEnum.WORKING);

        ActualProductReportResult result = kanbanInfoFacade.multiReport(report);
        if (Objects.nonNull(result)) {
            this.tid = result.getNextTransactionID();
        }
        return result;
    }

    /**
     * 作業完了
     * @param workKanbanIds 作業を完了する工程ID
     * @return 作業完了結果
     */
    public ActualProductReportResult compWork(List<Long> workKanbanIds, Date compDateTime) {

        if (Objects.isNull(this.equipmentId) || Objects.isNull(this.organizationId)) {
            return null;
        }

        ActualProductReportEntity report = new ActualProductReportEntity();
        report.setTransactionId(this.tid);
        report.setEquipmentId(this.equipmentId);
        report.setOrganizationId(this.organizationId);
        report.setSupportMode(false);
        report.setIsSchedule(Boolean.FALSE);
        report.setStatus(KanbanStatusEnum.COMPLETION);
        report.setLaterRework(false);
        report.setWorkKanbanCollection(workKanbanIds);
        report.setReportDatetime(compDateTime);

        ActualProductReportResult result = kanbanInfoFacade.multiReport(report);
        if (Objects.nonNull(result)) {
            this.tid = result.getNextTransactionID();
        }

        return result;
    }

    /**
     * 作業中断
     * @param workKanbanIds 中断する作業
     * @return 中断結果
     */
    public ActualProductReportResult suspendWork(List<Long> workKanbanIds) {
        if (Objects.isNull(this.equipmentId) || Objects.isNull(this.organizationId)) {
            return null;
        }

        ActualProductReportEntity report = new ActualProductReportEntity();
        report.setTransactionId(this.tid);
        report.setEquipmentId(this.equipmentId);
        report.setOrganizationId(this.organizationId);
        report.setSupportMode(false);
        report.setIsSchedule(Boolean.FALSE);
        report.setStatus(KanbanStatusEnum.SUSPEND);
        report.setLaterRework(false);
        report.setWorkKanbanCollection(workKanbanIds);
        report.setReportDatetime(new Date());

        ActualProductReportResult result = kanbanInfoFacade.multiReport(report);
        if (Objects.nonNull(result)) {
            this.tid = result.getNextTransactionID();
        }
        return result;
    }

}
