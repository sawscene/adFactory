package jp.adtekfuji.adfactoryasprovaactualdataoutput;

import io.vavr.control.Either;
import org.apache.logging.log4j.LogManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB接続
 * 
 * @author koga
 */
public class SQLUtil {

    @FunctionalInterface
    public interface SQLFunction<T> {
        T apply(Connection conn) throws SQLException;
    }

    public static<T> Either<String, T> connect(String url, String user, String password, SQLFunction<T> function)
    {
        try(Connection conn = DriverManager.getConnection(url, user, password)) {
            return Either.right(function.apply(conn));
        } catch (SQLException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return Either.left(ex.getMessage());
        }
    }

}

