package jp.adtekfuji.prodcountreporter;

import io.vavr.control.Either;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;


public class HttpUtil {

    @FunctionalInterface
    public interface HttpFunction<T> {
        T apply(HttpURLConnection conn) throws ProtocolException;
    }

    public static<T> Either<String, T> connect(String httpUrl, HttpFunction<T> function)
    {
        HttpURLConnection con = null;
        try {
            URL url = new URL(httpUrl); //  java.net.MalformedURLException
            con = (HttpURLConnection) url.openConnection(); //  java.io.IOException
            return Either.right(function.apply(con));
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
            return Either.left(ex.getMessage());
        } finally {
            if(Objects.nonNull(con)) con.disconnect();
        }
    }

    public static Either<String, Boolean> write(HttpURLConnection con, String message) {
        try(OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os)) {
            osw.write(message);
            osw.flush();
            con.connect();
            return Either.right(true);
        } catch(IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return Either.left("HTTP書込異常"+ex.getMessage());
        }
    }

    public static Either<String, String> response(HttpURLConnection con)
    {
        int status;
        try {
            status = con.getResponseCode();
        } catch(IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return Either.left("レスポンス異常");
        }

        if (HttpURLConnection.HTTP_OK != status) {
            return Either.left("レスポンス異常 : " + status);
        }

        String encoding = con.getContentEncoding();
        if (null == encoding) {
            encoding = "UTF-8";
        }

        try (final InputStream in = con.getInputStream();
             final InputStreamReader inReader = new InputStreamReader(in, encoding);
             final BufferedReader bufReader = new BufferedReader(inReader)) {
            String line;
            // 1行ずつテキストを読み込む
            StringBuilder sb = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                sb.append(line);
            }
            return Either.right(sb.toString());
        } catch (IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return Either.left("レスポンス異常 : " + ex.getMessage());
        }

    }
}
