package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker;

import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility.MessageUtility;

/**
 * ログイン状態
 */
public class LoginStatus implements IConnectStatus {
    private static final Logger logger = LogManager.getLogger();
    private static final Optional<MailSender> mailSender = MailSender.getInstance();

    private final VirtualAdProduct virtualAdProduct; // 仮想adProduct
    private WorkStatus workStatus;                   // 作業状態
    private WorkStatus initWorkStatus;               // 作業初期状態

    /**
     * コンストラクタ
     * 
     * @param virtualAdProduct 仮想adProductにセット
     * @param initWorkStatus 作業状態と作業初期状態にセット
     */
    public LoginStatus(VirtualAdProduct virtualAdProduct, WorkStatus initWorkStatus) {
        this.virtualAdProduct = virtualAdProduct;
        this.workStatus = initWorkStatus;
        this.initWorkStatus = initWorkStatus;

    }

    public boolean initialize()
    {
        this.workStatus = this.initWorkStatus;
        return this.workStatus.connect(virtualAdProduct);
    }

    /**
     * 接続状態更新
     * @return 次の状態
     */
    @Override
    public IConnectStatus updateConnectState() {
        logger.debug("LoginStatus.updateConnectState Start {}", getLoginToInformation());
        
        // 設備ログイン
        EquipmentLoginResult equipmentLoginResult = virtualAdProduct.loginEquipment();
        if (Objects.isNull(equipmentLoginResult) || !equipmentLoginResult.getIsSuccess()) {
            // 設備ログイン失敗、作業中状態で有れば中断を行う
            if (this.workStatus.disconnect(virtualAdProduct)) {
                String mailMsg = LocaleUtils.getString("key.MailMsg.EquipmentLoginNg2")
                        + MessageUtility.getAnalyzeEquipmentLoginResult(equipmentLoginResult, virtualAdProduct);
                logger.warn(mailMsg);
                mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.EquipmentLoginNg2"), mailMsg));
                
                // ログアウト状態に遷移
                return new LogoutStatus(this.virtualAdProduct, this.initWorkStatus);
            }
        }

        // 組織ログイン
        OrganizationLoginResult organizationLoginResult = virtualAdProduct.loginOrganization();
        if (Objects.isNull(organizationLoginResult) || !organizationLoginResult.getIsSuccess()) {
            // 組織ログイン失敗、作業中状態で有れば中断を行う
            if (this.workStatus.disconnect(virtualAdProduct)) {
                String mailMsg = LocaleUtils.getString("key.MailMsg.OrganizationLoginNg2")
                        + MessageUtility.getAnalyzeOrganizationLoginResult(organizationLoginResult, virtualAdProduct);
                logger.warn(mailMsg);
                mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.OrganizationLoginNg2"), mailMsg));
                
                // ログアウト状態に遷移
                return new LogoutStatus(this.virtualAdProduct, this.initWorkStatus);
            }
        }

        logger.info("success login {}", getLoginToInformation());
        logger.debug("LoginStatus.updateConnectState End {}", getLoginToInformation());
        
        // メール送信
        return this;
    }

    /**
     * 処理
     */
    @Override
    public void doStatusCommand(IWorkStatusCommand command)
    {
        WorkStatus nextStatus = workStatus.command(this.virtualAdProduct, command);
        if (Objects.nonNull(nextStatus)) {
            this.workStatus = nextStatus;
        }
    }

    /**
     * ログアウト処理
     * @return 次の状態
     */
    @Override
    public  IConnectStatus logout()
    {
        if (!this.workStatus.disconnect(virtualAdProduct)) {
            logger.fatal("logout error !!");
        }

        // 成功、失敗にかかわらずログアウトする。
        return new LogoutStatus(this.virtualAdProduct, this.initWorkStatus);
    }

    /**
     * ログイン
     * @return 次の状態
     */
    @Override
    public  IConnectStatus login()
    {
        return this;
    }

    /**
     * 作業状態を取得（状態確認用）
     * 
     * @return 作業状態
     */
    @Override
    public String getWorkStatus()
    {
        return !Objects.isNull(this.workStatus) ? this.workStatus.getClass().getSimpleName() : "";
    }

    /**
     * ログイン先情報の取得
     * @return ログイン先情報
     */
    private String getLoginToInformation(){
        
        return MessageUtility.getLoginToInformation(this.virtualAdProduct.getEquipmentIdentify(), 
                                                    this.virtualAdProduct.getOrganizationIdentify());
        
    }

}
