package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import adtekfuji.utility.Tuple;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static java.util.stream.Collectors.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MongoDBLogLoader {

    public final static String collectionName = "CollectionName";

    // コレクション名
    public enum Collection {
        L1Signal_Pool_Active,
        L1Signal_Pool,
        Program_History,
    };

    static private final Bson ping = new BasicDBObject("ping", 1); // 通信確認
    static private final Logger logger = LogManager.getLogger(); // ログ出力用クラス

    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;

    Map<Collection, MongoCollection<Document>> collectionMap = new HashMap();

    final private String host;
    final private int port;
    final private String userName;
    final private String database;
    final private char[] password;

    MongoDBLogLoader(String host, int port, String userName, String database, String password)
    {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.database = database;
        this.password = password.toCharArray();
    }

    /**
     * 接続状態にあるかを悪人
     * @return 接続状態
     */
    boolean isConnect() {
        return Objects.nonNull(mongoClient)
                && Objects.nonNull(mongoDatabase)
                && Stream.of(Collection.values())
                .map(collectionMap::get)
                .allMatch(Objects::nonNull);
    }

    /**
     * 接続
     * @return 接続結果
     */
    boolean connect()
    {
        // 再接続する場合は一度切断する
        disconnect();

        try {
            ServerAddress serverAddress = new ServerAddress(this.host, this.port);
            MongoCredential credential = MongoCredential.createCredential(this.userName, this.database, this.password);
            MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
            this.mongoClient = new MongoClient(serverAddress, credential, mongoClientOptions);
            this.mongoDatabase = mongoClient.getDatabase(this.database);
            this.mongoDatabase.runCommand(ping);

            Stream.of(Collection.values())
                    .forEach(collection -> collectionMap.put(collection, this.mongoDatabase.getCollection(collection.toString())));

            return isConnect();
        } catch (Exception ex) {
            logger.fatal("DB Connect Error");
            logger.fatal(ex, ex);
            disconnect();
            return false;
        }
    }

    /**
     * ログ取得
     * @param filters フィルタ
     * @return ログ
     */
    public List<Document> getDocument(Collection collection, Bson find, Bson sort, Integer limit) {
        FindIterable<Document> iter
                = collectionMap
                .get(collection)
                .find(find);

        if (Objects.nonNull(sort)) {
            iter = iter.sort(sort);
        }

        if(Objects.nonNull(limit)) {
            iter = iter.limit(limit);
        }

        return StreamSupport
                .stream(iter.spliterator(), false)
                .collect(toList());
    }

    /**
     * ログ取得
     * @param filters フィルタ
     * @return ログ
     */
    public List<List<Document>> getDocument(Map<MongoDBLogLoader.Collection, Bson> filters)
    {
//        logger.info("start getDocument");
        if (!isConnect()) {
            if (!connect()) {
                return new ArrayList<>();
            }
        }

        try {
            List<Document> documents = new ArrayList<>();
            for (Collection collection : Collection.values()) {
                if (!filters.containsKey(collection)) {
                    continue;
                }

                List<Document> docs = this.getDocument(collection, filters.get(collection), null, null);
                docs.forEach(doc->doc.put(collectionName.toString(), collection.toString()));
                documents.addAll(docs);
            }

//            logger.info("MongoDB Document Num {}", documents.size());

            // 更新日でソートして返す
            return documents
                    .stream()
                    .map(item-> new Tuple<>(item.getDate("updatedate"), item))
                    .collect(groupingBy(
                            Tuple::getLeft,
                            collectingAndThen(
                                    toList(),
                                    list->list.stream().map(Tuple::getRight).collect(toList())
                            )))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .collect(toList());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            logger.fatal("DB Connect Error");
            disconnect();
            return new ArrayList<>();
        }
    }

    /**
     * 切断処理
     */
    public void disconnect()
    {
        if (Objects.nonNull(mongoClient)) {
            mongoClient.close();
        }
        mongoClient = null;
        mongoDatabase = null;
        collectionMap.clear();
    }
}
