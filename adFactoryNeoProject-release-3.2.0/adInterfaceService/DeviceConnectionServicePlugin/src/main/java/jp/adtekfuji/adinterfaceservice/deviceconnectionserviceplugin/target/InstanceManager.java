package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target;

import adtekfuji.locale.LocaleUtils;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.Exceptions.BaseException;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.Exceptions.GetEviceConnectionInfoException;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.Exceptions.NewVirtualWorkerException;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.entity.DeviceConnectionEntity;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink.FanucCNCVirtualWorker;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink.OpcuaVirtualWorker;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.VirtualWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * インスタンス管理
 *
 * @author okada
 */
public class InstanceManager {

    /**
     * ログ出力用クラス
     */
    static private final Logger logger = LogManager.getLogger();
    static protected final Optional<MailSender> mailSender = MailSender.getInstance();

    // 仮想作業者(装置状態管理)のObject
    private List<VirtualWorker> virtualWorkerAdminInfos = new ArrayList<>();
    
    /**
     * 仮想作業者(装置状態管理)のインスタンを作成
     * ※VirtualWorkerクラスを継承しているクラスが対象
     * 
     * @param deviceConnectionInfo デバイス接続方法情報
     * @return 仮想作業者(装置状態管理)のインスタン
     */
    private VirtualWorker createVirtualWorker(DeviceConnectionEntity deviceConnectionInfo)
    {
        if ("MT-LINK".equals(deviceConnectionInfo.getDeviceType())) {
            if (OpcuaVirtualWorker.name.equals(deviceConnectionInfo.getConnectType())) {
                return OpcuaVirtualWorker.createInstance(deviceConnectionInfo);
            }
            if (FanucCNCVirtualWorker.name.equals(deviceConnectionInfo.getConnectType())) {
                return FanucCNCVirtualWorker.createInstance(deviceConnectionInfo);
            }
        }

        logger.warn("VirtualWorker instances were not created.[equipmentIdentify:{}][organizationIdentify:{}]", 
                deviceConnectionInfo.getEquipmentIdentify(), deviceConnectionInfo.getOrganizationIdentify());

        return null;
    }

    /**
     * サービス起動
     * 
     * @param deviceConnectionEntities 設定ファイルからの情報
     */
    public void startService(List<Map<String, String>> deviceConnectionEntities) {
        
        try {
            logger.debug("InstanceManager.startService Start");

            // 設定ファイルから情報を取得
            // デバイス接続方法情報
            List<DeviceConnectionEntity> deviceConnectionInfos = getEviceConnectionInfo(deviceConnectionEntities);
            
            // 装置状態管理のインスタンスを作成
            this.virtualWorkerAdminInfos = createVirtualWorker(deviceConnectionInfos);
            
            logger.debug("InstanceManager.startService End");
            
        } catch (BaseException e) {
            exceptionHandling(e);
        } catch (Exception e) {
            exceptionHandling(new BaseException(
                    "Exception error occurs in service startup process",
                    e,true,true,
                    LocaleUtils.getString("key.MailTitle.ExceptionStart"),
                    LocaleUtils.getString("key.MailMsg.ExceptionStart")));
        }
    }

    /**
     * サービス停止
     */
    public void endService() {
        this.virtualWorkerAdminInfos.forEach(VirtualWorker::endProcess);
    }

    /**
     * 組織(仮想作業者)更新処理
     */
    public void updateOrganization() {
        this.virtualWorkerAdminInfos.forEach(VirtualWorker::updateOrganizationProcess);
    }
    
    /**
     * 設置(装置)更新処理
     */
    public void updateEquipment() {
        this.virtualWorkerAdminInfos.forEach(VirtualWorker::updateEquipmentProcess);
    }

    /**
     * 例外エラー処理
     * 
     * @param baseEx 
     */
    private void exceptionHandling(BaseException baseEx){
        
        try {
            
            if(baseEx.getExceptionFlag()){
                // 例外エラーを含む場合
                switch (baseEx.getErrorKbn()) {
                    case FATAL:
                        logger.fatal(baseEx.getMessage(), baseEx);
                        break;
                    case ERROR:
                        logger.error(baseEx.getMessage(), baseEx);
                        break;
                    case WARN:
                        logger.warn(baseEx.getMessage(), baseEx);
                        break;
                    case INFO:
                        logger.info(baseEx.getMessage(), baseEx);
                        break;
                    case DEBUG:
                        logger.debug(baseEx.getMessage(), baseEx);
                        break;
                    case TRACE:                    
                        logger.trace(baseEx.getMessage(), baseEx);
                        break;
                }
            }else{
                // 例外エラーを含まない場合
                // メール内容をログに出力します。
                switch (baseEx.getErrorKbn()) {
                    case FATAL:
                        logger.fatal(baseEx.getSendMailMessage());
                        break;
                    case ERROR:
                        logger.error(baseEx.getSendMailMessage());
                        break;
                    case WARN:
                        logger.warn(baseEx.getSendMailMessage());
                        break;
                    case INFO:
                        logger.info(baseEx.getSendMailMessage());
                        break;
                    case DEBUG:
                        logger.debug(baseEx.getSendMailMessage());
                        break;
                    case TRACE:                    
                        logger.trace(baseEx.getSendMailMessage());
                        break;
                }
            }

            // メール送信
            if(baseEx.getSendMailFlag()){
                mailSender.ifPresent(mail -> mail.send(baseEx.getSendMailTitle(), baseEx.getSendMailMessage()));
            }

        } catch (Exception ex) {
            // 例外エラー処理時の例外は無視する。
            logger.fatal(ex, ex);
        }

    }
        
    /**
     * 設定ファイルから情報を取得
     * 
     * @param deviceConnectionEntities デバイス接続方法
     * @throws GetEviceConnectionInfoException 設定ファイルから情報を取得時のエラー
     */
    private List<DeviceConnectionEntity> getEviceConnectionInfo(List<Map<String, String>> deviceConnectionEntities) throws GetEviceConnectionInfoException {

        try {
            logger.debug("InstanceManager.getEviceConnectionInfo Start");
            
            // デバイス接続方法情報が無い場合
            if (Objects.isNull(deviceConnectionEntities) || deviceConnectionEntities.isEmpty()) {
                throw new GetEviceConnectionInfoException(true,
                        LocaleUtils.getString("key.MailTitle.FileNot"),
                        LocaleUtils.getString("key.MailMsg.FileNot"));
            }

            // デバイス接続方法情報を保存
            final List<DeviceConnectionEntity> dConnectionInfos
                    = deviceConnectionEntities
                    .stream()
                    .map(DeviceConnectionEntity::new)
                    .collect(toList());

            // データチェック
            checkDeviceConnection(dConnectionInfos);

            logger.debug("InstanceManager.getEviceConnectionInfo End");

            return dConnectionInfos;
            
        } catch (GetEviceConnectionInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new GetEviceConnectionInfoException(
                    "Exception error occurs in the process of getting information from the configuration file",
                    e,true,true,
                    LocaleUtils.getString("key.MailTitle.ExceptionSetting"),
                    LocaleUtils.getString("key.MailMsg.ExceptionSetting"));
        }
    }

    /**
     * DeviceConnectionEntityのデータチェック
     * @param dConnectionInfos
     * @throws GetEviceConnectionInfoException 設定ファイルから情報を取得時のエラー
     */
    private void checkDeviceConnection(List<DeviceConnectionEntity> dConnectionInfos) throws GetEviceConnectionInfoException {

        // 必須チェック
        for (DeviceConnectionEntity deviceConnectionInfo : dConnectionInfos) {
            // 必須チェック
            if (!deviceConnectionInfo.checkRequired()) {
                String message
                        = LocaleUtils.getString("key.MailMsg.Indispensable") + "<br>"
                        + "equipmentIdentify : " + deviceConnectionInfo.getEquipmentIdentify() + "<br>"
                        + "organizationIdentify : " + deviceConnectionInfo.getOrganizationIdentify() + "<br>"
                        + "deviceType : " + deviceConnectionInfo.getDeviceType() + "<br>";


                throw new GetEviceConnectionInfoException(true,
                        LocaleUtils.getString("key.MailTitle.Indispensable"),
                        message);
            }
        }

        // 設備重複チェック
        if (dConnectionInfos.size()
                != dConnectionInfos
                .stream()
                .map(DeviceConnectionEntity::getEquipmentIdentify)
                .collect(toSet())
                .size()) {

            String distinctList
                    = dConnectionInfos
                    .stream()
                    .map(DeviceConnectionEntity::getEquipmentIdentify)
                    .collect(groupingBy(Function.identity()))
                    .values()
                    .stream()
                    .filter(list->list.size() > 1)
                    .map(list->list.get(0))
                    .collect(joining("<br>"));

            throw new GetEviceConnectionInfoException(true,
                    LocaleUtils.getString("key.MailTitle.Duplication"),
                    LocaleUtils.getString("key.MailMsg.Duplication") + "<br>" + distinctList);
        }

        // 設備重複チェック
        if (dConnectionInfos.size()
                != dConnectionInfos
                .stream()
                .map(DeviceConnectionEntity::getOrganizationIdentify)
                .collect(toSet())
                .size()) {

            String distinctList
                    = dConnectionInfos
                    .stream()
                    .map(DeviceConnectionEntity::getOrganizationIdentify)
                    .collect(groupingBy(Function.identity()))
                    .values()
                    .stream()
                    .filter(list->list.size() > 1)
                    .map(list->list.get(0))
                    .collect(joining("<br>"));

            throw new GetEviceConnectionInfoException(true,
                    LocaleUtils.getString("key.MailTitle.Duplication"),
                    LocaleUtils.getString("key.MailMsg.Duplication") + "<br>" + distinctList);
        }
    }

    /**
     * 装置状態管理のインスタンスを作成
     * 
     * @param dConnectionInfos デバイス接続方法情報
     * @throws NewVirtualWorkerException 装置状態管理のインスタンスを作成時のエラー
     */
    private List<VirtualWorker> createVirtualWorker(List<DeviceConnectionEntity> dConnectionInfos) throws NewVirtualWorkerException{

        try {
            logger.debug("InstanceManager.createVirtualWorker Start");

            ExecutorService exec = Executors.newCachedThreadPool();

            // 設定ファイルに登録されている情報分作成
            // 但し、有効な設定情報のみ処理を行う。
            List<VirtualWorker> virtualWorkers
                    = dConnectionInfos
                    .stream()
                    .filter(DeviceConnectionEntity::isEnable)
                    .map(this::createVirtualWorker)
                    .filter(Objects::nonNull)
                    .collect(toList());

            virtualWorkers.forEach(exec::submit);

            logger.debug("InstanceManager.createVirtualWorker End");

            return virtualWorkers;

        } catch (Exception e) {
            logger.fatal(e,e);
            throw new NewVirtualWorkerException(
                    "Exception error occurs in the process of creating an instance of equipment state management",
                    e,true,true,
                    LocaleUtils.getString("key.MailTitle.ExceptionNew"),
                    LocaleUtils.getString("key.MailMsg.ExceptionNew"));
        }
    }
}
