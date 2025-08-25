package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.entity.DeviceConnectionEntity;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.LogoutStatus;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.VirtualWorker;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.WorkStatus;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * MTLINK仮想作業者
 */
public class OpcuaVirtualWorker extends VirtualWorker implements IMongoDBSubject{

    static final public String name = "OPCUA";

    // 接続
    final BooleanSupplier connect = () -> {
        MongoDBObserver.getInstance().ifPresent(observer -> observer.addSubjects(Collections.singletonList(this)));
        return true;
    };

    // 切断
    final BooleanSupplier disconnect = () -> {
        MongoDBObserver.getInstance().ifPresent(observer -> observer.removeSubjects(Collections.singletonList(this)));
        return true;
    };

    Tuple<Date, Double> programName = null; // プログラム名
    Tuple<Date, Double> status = null; // 状態
    Tuple<Date, Double> counter = null; // カウンタ
    Tuple<Date, Double> workNum = null; // ワーク数
    final String machineName; // 装置名
    final String programSignalName; // プログラム信号名
    final String workNumberSignalName; // ワーク数信号名
    final String statusSignalName; // 信号名
    final String counterSignalName; // カウンター信号名
    final String workerName;
    final String startCommandType; // 開始タイプ

    /**
     * インスタンス生成
     * @param deviceConnectionInfo 接続情報
     * @return MTLink仮想作業者
     */
    public static OpcuaVirtualWorker createInstance(DeviceConnectionEntity deviceConnectionInfo)
    {
        final String workerName = deviceConnectionInfo.getEquipmentIdentify() + "-" + deviceConnectionInfo.getOrganizationIdentify();
        logger.info("crate MTLinkVirtualWorker : {}", workerName);
        
        Optional<VirtualAdProduct> optionalVirtualAdProduct = VirtualAdProduct.createInstance(
                deviceConnectionInfo.getEquipmentIdentify(),
                deviceConnectionInfo.getOrganizationIdentify(),
                deviceConnectionInfo.getPassword());
        if(!optionalVirtualAdProduct.isPresent()) {
            // エラーにする。
            return null;
        }
        VirtualAdProduct virtualAdProduct = optionalVirtualAdProduct.get();

        OpcuaVirtualWorker worker =  new OpcuaVirtualWorker(workerName, deviceConnectionInfo);

        // 初回ログイン
        worker.connectStatus = LogoutStatus.initialize(
                virtualAdProduct,
                WorkStatus.getWaitInstructionStatus(worker.connect, worker.disconnect));
        return worker;
    }

    /**
     * コンストラクタ
     *
     */
    private OpcuaVirtualWorker(String workerName, DeviceConnectionEntity deviceConnectionInfo) {
        this.deviceConnectionInfo = deviceConnectionInfo;
        this.workerName = workerName;
        this.machineName
                = StringUtils.isEmpty(deviceConnectionInfo.getMachineName())
                ? deviceConnectionInfo.getEquipmentIdentify()
                : deviceConnectionInfo.getMachineName();
        this.programSignalName
                = StringUtils.isEmpty(deviceConnectionInfo.getProgramSignalName())
                ? "mainprog"
                : deviceConnectionInfo.getProgramSignalName();
        this.statusSignalName = deviceConnectionInfo.getStatusSignalName();
        this.counterSignalName = deviceConnectionInfo.getCounterSignalName();
        this.workNumberSignalName = deviceConnectionInfo.getWorkNumberSignalName();
        this.startCommandType = deviceConnectionInfo.getStartCommandType();
 
        Date now = new Date();
        this.programName = new Tuple<>(now, null); // プログラム名
        this.status = new Tuple<>(now, null); // 状態
        this.counter = new Tuple<>(now, null); // カウンタ
        this.workNum = new Tuple<>(now, 1.0); // ワーク数
    }


    /**
     * 初期状態設定
     * @param loader mongoDBローダ
     */
    @Override
    public void initialize(MongoDBLogLoader loader)
    {
        Date now = new Date();

        // 現在のプログラム名を取得
        Bson programNameFilter = Filters.and(
                Filters.in("L1Name", this.machineName),
                Filters.in("signalname", programSignalName),
                Filters.lte("updatedate", now),
                Filters.ne("value",null),
                Filters.ne("value", "")
        );

        List<Document> programDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool_Active, programNameFilter, new BasicDBObject("updatedate", -1), 1);
        if( programDoc.isEmpty() ) {
            programDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool, programNameFilter, new BasicDBObject("updatedate", -1), 1);
        }


        programName = new Tuple<>(now, null);
        if (!programDoc.isEmpty()) {
            final Double value = programDoc.get(0).getDouble("value");
            if (Objects.nonNull(value)) {
                programName = new Tuple<>(now, value);
            }
        }

        // ステータスを取得
        Bson statusFilter = Filters.and(
                Filters.in("L1Name", this.machineName),
                Filters.in("signalname", this.statusSignalName),
                Filters.lte("updatedate", now),
                Filters.ne("value",null),
                Filters.ne("value", "")
        );

        List<Document> stateDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool_Active, statusFilter, new BasicDBObject("updatedate", -1), 1);
        if( stateDoc.isEmpty() ) {
            stateDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool, statusFilter, new BasicDBObject("updatedate", -1), 1);
        }

        this.status = new Tuple<>(now, null);
        if (!stateDoc.isEmpty()) {
            final Double value = stateDoc.get(0).getDouble("value");
            if (Objects.nonNull(value)) {
                this.status = new Tuple<>(now, value);
            }
        }

        // カウンタを取得
        Bson counterFilter = Filters.and(
                Filters.in("L1Name", this.machineName),
                Filters.in("signalname", this.counterSignalName),
                Filters.lte("updatedate", now),
                Filters.ne("value",null),
                Filters.ne("value", "")
        );
        List<Document> counterDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool_Active, counterFilter, new BasicDBObject("updatedate", -1), 1);
        if( counterDoc.isEmpty() ) {
            counterDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool, counterFilter, new BasicDBObject("updatedate", -1), 1);
        }
        this.counter = new Tuple<>(now, null);
        if(!counterDoc.isEmpty()) {
            final Double value = counterDoc.get(0).getDouble("value");
            if (Objects.nonNull(value)) {
                this.counter = new Tuple<>(now, value);
            }
        }

        if (!StringUtils.isEmpty(this.workNumberSignalName)) {
            // ワーク数
            Bson workNumberFilter = Filters.and(
                    Filters.in("L1Name", this.machineName),
                    Filters.in("signalname", this.workNumberSignalName),
                    Filters.lte("updatedate", now),
                    Filters.ne("value", null),
                    Filters.ne("value", "")
            );
            List<Document> workNumDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool_Active, workNumberFilter, new BasicDBObject("updatedate", -1), 1);
            if (workNumDoc.isEmpty()) {
                workNumDoc = loader.getDocument(MongoDBLogLoader.Collection.L1Signal_Pool, workNumberFilter, new BasicDBObject("updatedate", -1), 1);
            }
            this.workNum = new Tuple<>(now, 1.0);
            if (!workNumDoc.isEmpty()) {
                final Double value = workNumDoc.get(0).getDouble("value");
                if (Objects.nonNull(value)) {
                    this.workNum = new Tuple<>(now, value);
                }
            }
        }

        logger.info("**** initialize {} programName:{}, status:{}, counter:{}", machineName, this.programName.getRight(), this.status.getRight(), this.counter.getRight());
    }

    /**
     * 作業者名取得
     * @return 作業者名
     */
    @Override
    public String getWorkerName()
    {
        return this.workerName;
    }

    /**
     * 最終更新日取得
     * @return 最終更新日
     */
    private Date getLastUpdateDate()
    {
        return Stream.of(programName, status, counter)
                .filter(Objects::nonNull)
                .map(Tuple::getLeft)
                .max(Comparator.comparing(Function.identity()))
                .orElse(new Date());
    }

    /**
     * フィルタ取得
     * @return フィルタ
     */
    @Override
    public Map<MongoDBLogLoader.Collection, Bson> getFilters()
    {
        final Date lastUpdate = getLastUpdateDate();
        Map<MongoDBLogLoader.Collection, Bson> ret = new HashMap<>();

        // プログラム名、ステータス、 カウンター情報、 ワーク数
        List<Bson> signals = new ArrayList<>();
        signals.add(Filters.in("signalname", programSignalName));
        signals.add(Filters.in("signalname", this.statusSignalName));
        signals.add(Filters.in("signalname", this.counterSignalName));
        if(!StringUtils.isEmpty(this.workNumberSignalName)) {
            signals.add(Filters.in("signalname", this.workNumberSignalName));
        }

        
        Bson filter = Filters.and(
                Filters.in("L1Name", this.machineName),
                Filters.or(signals),
                Filters.gt("updatedate", lastUpdate),
                Filters.ne("value",null),
                Filters.ne("value", "")
        );

        ret.put(MongoDBLogLoader.Collection.L1Signal_Pool_Active, filter);
        ret.put(MongoDBLogLoader.Collection.L1Signal_Pool,        filter);

        return ret;
    }

    /**
     * ドキュメント
     * @param document ドキュメント
     */
    @Override
    public void apply(List<Document> documents)
    {
        documents
                .stream()
                .filter(document -> {
                    final String collectionName = document.getString(MongoDBLogLoader.collectionName.toString());
                    // 対象の信号の確認
                    if (!StringUtils.equals(collectionName, MongoDBLogLoader.Collection.L1Signal_Pool.toString())
                            && !StringUtils.equals(collectionName, MongoDBLogLoader.Collection.L1Signal_Pool_Active.toString())) {
                        return false;
                    }
                    return StringUtils.equals(machineName, document.getString("L1Name"));
                })
                .sorted(Comparator.comparing(document -> {
                    final String signalName = document.getString("signalname");
                    // カウンタは最後にする
                    return Objects.nonNull(signalName) && StringUtils.equals(signalName, this.counterSignalName) ? 9999 : 0;
                }))
                .forEach(this::apply);
    }

    private void apply(Document document)
    {
        try {
            final Date updateDate = document.getDate("updatedate");
            final String signalName = document.getString("signalname");
            if (Objects.isNull(updateDate)
                    || Objects.isNull(signalName)) {
                logger.info("MongoDB update skip {} : date {}, signal {}",machineName, updateDate, signalName);
                return;
            }

            // ------------ 信号の種類で切り分け
            // プログラム名の場合
            if (StringUtils.equals(signalName, programSignalName)) {
                Double value = document.getDouble("value");
                if (Objects.isNull(value) && Objects.nonNull(this.programName)) {
                    value = this.programName.getRight();
                }

                logger.info("**** MongoDB change prog {} : {} -> {}", machineName, Objects.nonNull(this.programName) ? this.programName.getRight() : null, value);
                this.programName = new Tuple<>(updateDate, value);
                return;
            }

            // ステータスの場合
            if (StringUtils.equals(signalName, this.statusSignalName)) {
                Double value = document.getDouble("value");
                if (Objects.isNull(value) && Objects.nonNull(this.status)) {
                    value = this.status.getRight();
                }

                logger.info("**** MongoDB change state {} : {} -> {}", this.machineName, Objects.nonNull(this.status) ? this.status.getRight() : null, value);
                this.status = new Tuple<>(updateDate, value);
                return;
            }

            // ワーク数
            if (!StringUtils.isEmpty(this.workNumberSignalName) && StringUtils.equals(signalName, this.workNumberSignalName)) {
                Double value = document.getDouble("value");
                if (Objects.isNull(value) && Objects.nonNull(this.workNum)) {
                    value = this.workNum.getRight();
                }

                logger.info("**** MongoDB change state {} : {} -> {}", this.machineName, Objects.nonNull(this.workNum) ? this.workNum.getRight() : null, value);
                this.workNum = new Tuple<>(updateDate, value);
                return;
            }

            // 一つも更新されていない(カウンタが変更されていない)場合
            if (!StringUtils.equals(signalName, this.counterSignalName)) {
                logger.info("!! MongoDB unknown signal {}, {}", this.machineName, signalName);
                return;
            }

            final Double counter = document.getDouble("value");
            if (Objects.isNull(counter)) {
                logger.info("!! MongoDB counter null {}", this.machineName);
                return;
            }

            // カウンタの情報が無い場合は更新のみして更新しない
            if (Objects.isNull(this.counter) || Objects.isNull(this.counter.getRight())) {
                this.counter = new Tuple<>(updateDate, counter);
                logger.info("!! MongoDB new counter {}, {}", this.machineName, counter);
                return;
            }

            // カウンタが更新されていない
            if (Math.abs(this.counter.getRight() - counter) < 0.5) {
                logger.info("!! MongoDB counter not update {}, {}", this.machineName, counter);
                this.counter = new Tuple<>(updateDate, counter);
                return;
            }

            logger.info("**** MongoDB change counter {} : {} -> {}", this.machineName, Objects.isNull(this.counter.getRight())?null:this.counter.getRight(), counter);
            this.counter = new Tuple<>(updateDate, counter);

            // 情報がそろっていない
            if (Objects.isNull(this.programName.getRight()) || Objects.isNull(this.status.getRight())) {
                logger.info("!! MongoDB info Error {}, {}, {}", this.machineName, this.programName.getRight(), this.status.getRight());
                return;
            }

            if (this.status.getRight() > 0.5) {
                //  開始状態
//                String msg = String.format("OPCUA Start : %s %s", machineName, programName);
//                mailSender.ifPresent(mail -> mail.send("開始",msg));
                StartCommand command;
                if (!StringUtils.equals("countContinue", this.startCommandType)) {
                    command = new StartCommand(this.connect, this.disconnect, (long)(this.programName.getRight()+0.5), (long)(this.workNum.getRight() + 0.5), updateDate);
                } else {
                    command = new StartCounterContinueCommand(this.connect, this.disconnect, (long)(this.programName.getRight()+0.5), (long)(this.workNum.getRight() + 0.5), updateDate);
                }
                this.sendCommand(command);
            } else {
                // 完了状態
//                String msg = String.format("OPCUA Comp : %s %s", machineName, programName);
//                mailSender.ifPresent(mail -> mail.send("終了",msg));
                CompletionCommand command = new CompletionCommand(this.connect, this.disconnect, updateDate);
                this.sendCommand(command);
            }

        } catch (Exception ex) {
            logger.fatal(ex,ex);
        }
    }
}
