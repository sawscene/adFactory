/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc.progressdb;

import adtekfuji.adbridgebi.jdbc.DbConnector;
import java.util.Objects;

/**
 * adFactoryProgressDB 接続
 *
 * @author nar-nakamura
 */
public class ProgressDbConnector extends DbConnector {

    // adFactoryProgressDB の接続情報
    private static final int POSTGRESS_DB_PORT = 5432;
    private static final String POSTGRESS_DB_NAME = "adFactoryProgressDB";
    private static final String POSTGRESS_DB_USER = "postgres";
    private static final String POSTGRESS_DB_PASSWORD = "@dtek1977";

    private static DbConnector instance = null;

    /**
     * adFactoryProgressDB 接続のインスタンスを取得する。
     *
     * @return 
     */
    public static DbConnector getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DbConnector("localhost", POSTGRESS_DB_PORT, POSTGRESS_DB_NAME, POSTGRESS_DB_USER, POSTGRESS_DB_PASSWORD);
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
    public ProgressDbConnector(String serverAddress, int dbPort, String dbName, String dbUser, String dbPassword) {
        super(serverAddress, dbPort, dbName, dbUser, dbPassword);
    }
}
