package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import com.mongodb.client.model.Filters;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class MongoDBObserver extends Thread {

    private final static String BASE_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "conf";
    private final static String DEVICE_SETTING_FILE_PATH = BASE_PATH +  File.separator + "DeviceConnectionProperty.json";
    private final static String PROPERTY_NAME = "MongoDB";
    static private final Logger logger = LogManager.getLogger(); // ログ出力用クラス

    Timer timer = new Timer();

    private static final Optional<MailSender> mailSender = MailSender.getInstance();
    final private Map<String, IMongoDBSubject> subjectMapMap = new HashMap<>();
    private boolean execution = false;
    private MongoDBLogLoader mongoDBLoader = null;

    /**
     * コマンドクラス
     */
    private interface Command {
        boolean apply(MongoDBObserver observer);
    }

    /**
     * ログ取得コマンドクラス
     */
    private static class NotifyCommand implements Command {
        final private MongoDBLogLoader mongoDBLogLoader;
        NotifyCommand(MongoDBLogLoader mongoDBLogLoader)
        {
            this.mongoDBLogLoader = mongoDBLogLoader;
        }

        @Override
        public boolean apply(MongoDBObserver observer)
        {
            Map<MongoDBLogLoader.Collection, Bson> filters
                    = observer
                    .subjectMapMap
                    .values()
                    .stream()
                    .map(IMongoDBSubject::getFilters)
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(groupingBy(Map.Entry::getKey,
                            collectingAndThen(
                                    toList(),
                                    values -> Filters.or(
                                            values.stream()
                                                    .map(Map.Entry::getValue)
                                                    .collect(toList()))
                            )));

            // ログを受け取り通知する。
            List<List<Document>> documentsList = mongoDBLogLoader.getDocument(filters);
            documentsList.forEach(documents -> {
                observer.subjectMapMap.values().forEach(subject -> subject.apply(documents));
            });
            return true;
        }
    }

    /**
     * 追加コマンドクラス
     */
    private static class AddSubjectsCommand implements Command {
        List<IMongoDBSubject> subjects;
        AddSubjectsCommand(List<IMongoDBSubject>  subjects) { this.subjects = subjects; }
        @Override
        public boolean apply(MongoDBObserver observer)
        {
            subjects.forEach(subject -> subject.initialize(observer.mongoDBLoader));
            this.subjects.forEach(subject->observer.subjectMapMap.put(subject.getWorkerName(), subject));
            return true;
        }
    }

    /**
     * 削除コマンドクラス
     */
    private static class RemoveSubjectsCommand implements Command {
        List<IMongoDBSubject> subjects;
        RemoveSubjectsCommand(List<IMongoDBSubject> subjects) { this.subjects = subjects; }
        @Override
        public boolean apply(MongoDBObserver observer)
        {
            this.subjects
                    .stream()
                    .map(IMongoDBSubject::getWorkerName)
                    .forEach(observer.subjectMapMap::remove);
            return true;
        }
    }

    /**
     * 停止コマンド
     */
    private static class StopObserverCommand implements Command {
        public boolean apply(MongoDBObserver observer)
        {
            observer.execution = false;
            observer.mongoDBLoader.disconnect();
            return true;
        }
    }

    private final LinkedList<Command> recvQueue = new LinkedList<>();

    private MongoDBObserver()
    {
    }

    /**
     * 初期化
     * @return 初期化
     */
    static Optional<MongoDBObserver> initialize()
    {
        try {
            // メール設定
            Optional<MailSender> mailSender = MailSender.getInstance();
            if (!mailSender.isPresent()) {
                return Optional.empty();
            }

            // 監視ツール設定
            Optional<Map<String, String>> optMongoDBConfig = MongoDBObserver.loadConfigFile("MongoDB");
            if (!optMongoDBConfig.isPresent()) {
                return Optional.empty();
            }

            Map<String, String> mongoDBConfig = optMongoDBConfig.get();
            final String host = mongoDBConfig.get("host");
            final int port = Integer.parseInt(mongoDBConfig.get("port"));
            final String user = mongoDBConfig.get("user");
            final String database = mongoDBConfig.get("database");
            final String password = mongoDBConfig.get("password");
            long loadSettingIntervalMS = Long.parseLong(mongoDBConfig.get("loadIntervalMS"));

            // MongoDBローダーを取得
            MongoDBLogLoader mongoDBLoader = new MongoDBLogLoader(host, port, user, database, password);
            if (!mongoDBLoader.connect()) {
                logger.fatal("DB Connect Error");
                // メールを飛ばす.
                mailSender.ifPresent(sender->sender.send("[adFactory] MongoDB接続エラー", "DBの接続に失敗しました。" + System.getProperty("line.separator") + "MT-LINKの状態を確認して下さい。"));
            }

            MongoDBObserver obj = new MongoDBObserver();
            obj.execution = true;
            obj.mongoDBLoader = mongoDBLoader;
            obj.start();
            obj.schedule(mongoDBLoader, loadSettingIntervalMS);
            return Optional.of(obj);
        } catch(Exception ex) {
            logger.fatal(ex, ex);
            return Optional.empty();
        }
    }

    static MongoDBObserver instance = null;
    static public synchronized Optional<MongoDBObserver> getInstance()
    {
        if (Objects.isNull(instance) || !instance.execution) {
            Optional<MongoDBObserver> obj = initialize();
            obj.ifPresent(item -> instance = item);
            return obj;
        }
        return Optional.of(instance);
    }


    /**
     * このプラグインによって実行されるアクション
     */
    @Override
    public void run()
    {
        while (execution) {
            try {
                synchronized (recvQueue) {
                    if (recvQueue.isEmpty()) {
                        try {
                            recvQueue.wait();
                        } catch (InterruptedException ex) {
                            logger.fatal(ex, ex);
                        }
                        if (recvQueue.isEmpty()) {
                            continue;
                        }
                    }
                    final Command command = recvQueue.removeFirst();
                    command.apply(this);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * 定期的にログを取込む
     * @param mongoDBLoader MongoDBローダ
     * @param loadSettingIntervalMS 読込間隔
     */
    private void schedule(MongoDBLogLoader mongoDBLoader, long loadSettingIntervalMS)
    {
        final NotifyCommand command = new NotifyCommand(mongoDBLoader);
        TimerTask task = new TimerTask() {
            /**
             * このタイマー・タスクによって実行されるアクション
             */
            @Override
            public void run() {
                synchronized (recvQueue) {
                    // 定期取込のコマンドが溜まってる場合は追加しない
                    if (recvQueue
                            .stream()
                            .noneMatch(NotifyCommand.class::isInstance))
                    {
                        recvQueue.add(command);
                        recvQueue.notify();
                    }
                }
            }
        };

        // タイマー設定
        this.timer.schedule(task, 0, loadSettingIntervalMS);
    }


    /**
     * 設定ファイルから情報を取得
     * @param  confType 読込タイプ
     * @return 設定情報
     */
    private static Optional<Map<String, String>> loadConfigFile(String confType)
    {
        if (Files.notExists(Paths.get(DEVICE_SETTING_FILE_PATH))) {
            logger.fatal("Error not found Setting Files");
            return Optional.empty();
        }


        try (Stream<String> item = Files.lines(Paths.get(DEVICE_SETTING_FILE_PATH), StandardCharsets.UTF_8)){
            final String jsonStr = item.collect(joining(System.getProperty("line.separator")));
            List<Map<String, String>> confList = JsonUtils.jsonToMaps((jsonStr));
            if (Objects.isNull(confList) || confList.isEmpty()) {
                return Optional.empty();
            }

            return confList
                    .stream()
                    .filter(conf->conf.containsKey("type"))
                    .filter(conf-> StringUtils.equals(confType,conf.get("type")))
                    .findFirst();

        } catch (Exception e) {
            logger.fatal(e,e);
            return Optional.empty();
        }
    }

    /**
     * 作業者の追加
     * @param subjects 作業者
     * @return 結果
     */
    public boolean addSubjects(List<IMongoDBSubject> subjects)
    {
        Command command = new AddSubjectsCommand(subjects);
        synchronized (recvQueue) {
            recvQueue.add(command);
            recvQueue.notify();
        }
        return true;
    }

    /**
     * 作業者の削除
     * @param subjects 作業者
     * @return 結果
     */
    public boolean removeSubjects(List<IMongoDBSubject> subjects)
    {
        Command command = new RemoveSubjectsCommand(subjects);
        synchronized (recvQueue) {
            recvQueue.add(command);
            recvQueue.notify();
        }
        return true;
    }

    /**
     * 作業停止
     * @return 結果
     */
    public boolean stopObserver()
    {
        Command command = new StopObserverCommand();
        synchronized (recvQueue) {
            recvQueue.add(command);
            recvQueue.notify();
        }
        return true;
    }
}
