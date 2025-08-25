package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility.MessageUtility;

/**
 * ログアウト状態
 */
public class LogoutStatus implements IConnectStatus {
    private static final Logger logger = LogManager.getLogger();
    private static final Optional<MailSender> mailSender = MailSender.getInstance();

    private final VirtualAdProduct virtualAdProduct;     // 仮想adProduct
    private WorkStatus initProcess;                      // 作業状態

    /**
     * コンストラクタ
     * 
     * @param virtualAdProduct 仮想adProductにセット
     * @param initProcess 作業状態にセット
     */
    public LogoutStatus(VirtualAdProduct virtualAdProduct, WorkStatus initProcess) {
        this.virtualAdProduct = virtualAdProduct;
        this.initProcess = initProcess;
    }

    /**
     * 初期化
     * @param virtualAdProduct 仮想アドプロダクト
     * @param initStatus 初期状態
     * @return 次の状態
     */
    static public IConnectStatus initialize(VirtualAdProduct virtualAdProduct, WorkStatus initStatus)
    {
        // 設備ログイン
        EquipmentLoginResult equipmentLoginResult = virtualAdProduct.loginEquipment();
        if (Objects.isNull(equipmentLoginResult) || !equipmentLoginResult.getIsSuccess()) {
            logger.fatal("equipment login fatal");
            // 設備ログイン失敗
            logger.warn(LocaleUtils.getString("key.MailMsg.EquipmentLoginNg1")
                    + MessageUtility.getAnalyzeEquipmentLoginResult(equipmentLoginResult, virtualAdProduct));
            
            // ログアウト状態とする。
            return new LogoutStatus(virtualAdProduct, initStatus);
        }

        // 組織ログイン
        OrganizationLoginResult organizationLoginResult = virtualAdProduct.loginOrganization();
        if (Objects.isNull(organizationLoginResult) || !organizationLoginResult.getIsSuccess()) {
            logger.fatal("organization login fatal");
            // 組織ログイン失敗
            String mailMsg = LocaleUtils.getString("key.MailMsg.OrganizationLoginNg1")
                    + MessageUtility.getAnalyzeOrganizationLoginResult(organizationLoginResult, virtualAdProduct);
            logger.warn(mailMsg);

            // ログアウト状態とする。
            return new LogoutStatus(virtualAdProduct, initStatus);
        }

        // ログインできたので、ログアウト -> ログイン状態へ更新
        LoginStatus loginStatus = new LoginStatus(virtualAdProduct, initStatus);

        // 接続確認
        if (!loginStatus.initialize()) {
            // 接続処理失敗
            String mailMsg = LocaleUtils.getString("key.MailMsg.DevicesLogAdminConnNg")
                    + MessageUtility.getAnalyzeLoginStatusInitialize(virtualAdProduct);
            logger.warn(mailMsg);

            // ログアウト状態とする。
            return new LogoutStatus(virtualAdProduct, initStatus);
        }

        logger.info("success login {}", 
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(), 
                                                     virtualAdProduct.getOrganizationIdentify()));
        
        // 成功したのでログイン状態へ
        return loginStatus;
    }

    /**
     * 接続状態更新
     * @return 次の状態
     */
    @Override
    public IConnectStatus updateConnectState() {
        return login();
    }

    /**
     * 受信コマンド実施
     * @param command 受信コマンド
     */
    @Override
    public void doStatusCommand(IWorkStatusCommand command)
    {
        // ログアウト中は何もしない
    }

    /**
     * ログアウト
     * @return 次のステータス
     */
    @Override
    public  IConnectStatus logout()
    {
        return this;
    }

    /**
     * ログイン
     * @return 次のステータス
     */
    @Override
    public  IConnectStatus login()
    {
        logger.debug("LogoutStatus.login Start {}", getLoginToInformation());

        // 設備ログイン
        EquipmentLoginResult equipmentLoginResult = virtualAdProduct.loginEquipment();
        if (Objects.isNull(equipmentLoginResult) || !equipmentLoginResult.getIsSuccess()) {
            // 設備ログイン失敗
            String mailMsg = LocaleUtils.getString("key.MailMsg.EquipmentLoginNg1") + "<br>"
                    + MessageUtility.getAnalyzeEquipmentLoginResult(equipmentLoginResult, virtualAdProduct);
            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.EquipmentLoginNg1"), mailMsg));
            
            // 状態変更無
            return this;
        }

        // 組織ログイン
        OrganizationLoginResult organizationLoginResult = virtualAdProduct.loginOrganization();
        if (Objects.isNull(organizationLoginResult) || !organizationLoginResult.getIsSuccess()) {
            // 組織ログイン失敗
            String mailMsg = LocaleUtils.getString("key.MailMsg.OrganizationLoginNg1") + "<br>"
                    + MessageUtility.getAnalyzeOrganizationLoginResult(organizationLoginResult, virtualAdProduct);
            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.OrganizationLoginNg1"), mailMsg));
            
            // 状態変更無
            return this;
        }

        // ログインできたので、ログアウト -> ログイン状態へ更新
        LoginStatus loginStatus = new LoginStatus(this.virtualAdProduct, this.initProcess);

        // 初期化
        if (!loginStatus.initialize()) {
            // 接続処理失敗
            String mailMsg = LocaleUtils.getString("key.MailMsg.DevicesLogAdminConnNg") + "<br>"
                    + MessageUtility.getAnalyzeLoginStatusInitialize(this.virtualAdProduct);
            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.DevicesLogAdminConnNg"), mailMsg));
            
            // 状態変更無
            return this;
        }

        logger.info("success login {}", getLoginToInformation());
        logger.debug("LogoutStatus.login End {}", getLoginToInformation());

        // 成功したのでログイン状態へ
        return loginStatus;
    }

    /**
     * 作業状態を取得（状態確認用）
     * 
     * @return 作業状態
     */
    @Override
    public String getWorkStatus()
    {
        return !Objects.isNull(this.initProcess) ? this.initProcess.getClass().getSimpleName() : "";
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
