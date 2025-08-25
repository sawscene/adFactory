/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc.adfactorydb;

import adtekfuji.adbridgebi.common.AdBridgeBIConfig;
import adtekfuji.adbridgebi.jdbc.DbConnector;
import java.util.Objects;

/**
 * adFactoryDB 接続
 *
 * @author nar-nakamura
 */
public class AdFactoryDbConnector extends DbConnector {

    // adFactoryDB の接続情報
    private static final int ADFACTORY_DB_PORT = 15432;
    private static final String ADFACTORY_DB_NAME = "adFactoryDB2";
    private static final String ADFACTORY_DB_USER = "postgres";
    private static final String ADFACTORY_DB_PASSWORD = "@dtek1977";

    private static DbConnector instance = null;

    /**
     * adFactoryDB 接続のインスタンスを取得する。
     *
     * @return 
     */
    public static DbConnector getInstance() {
        if (Objects.isNull(instance)) {
            String serverAddress = AdBridgeBIConfig.getInstance().getServerAddress();
            instance = new DbConnector(serverAddress, ADFACTORY_DB_PORT, ADFACTORY_DB_NAME, ADFACTORY_DB_USER, ADFACTORY_DB_PASSWORD);
        }
        return instance;
    }

    /**
     * コンストラクタ
     *
     * @param serverAddress サーバーアドレス
     * @param dbPort データベースのポート番号
     * @param dbName データベース名
     * @param dbUser データベースのユーザー
     * @param dbPassword データベースのパスワード
     */
    public AdFactoryDbConnector(String serverAddress, int dbPort, String dbName, String dbUser, String dbPassword) {
        super(serverAddress, dbPort, dbName, dbUser, dbPassword);
    }
}
