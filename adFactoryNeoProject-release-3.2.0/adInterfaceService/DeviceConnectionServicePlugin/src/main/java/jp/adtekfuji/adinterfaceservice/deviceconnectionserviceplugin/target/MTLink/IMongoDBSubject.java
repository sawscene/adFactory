package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

/**
 * MongoDB 主体
 */
public interface IMongoDBSubject {
    /**
     * 名称取得
     * @return 名称
     */
    String getWorkerName();

    /**
     * MongoDB用フィルタ取得
     * @return フィルタ
     */
    Map<MongoDBLogLoader.Collection, Bson> getFilters();

    /**
     * Documentのログ解析
     * @param document ドキュメント
     */
    void apply(List<Document> documents);


    /**
     * 初期化
     * @param loader //mongoDBローダ
     */
    void initialize(MongoDBLogLoader loader);
}
