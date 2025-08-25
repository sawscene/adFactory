/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility.restclient;

import adtekfuji.utility.StringUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * REST クライアントクラス
 * 
 * @param <T> 受信データのエンティティクラス
 * @author s-heya
 */
public abstract class RestClient<T> implements HttpClient<T> {
    private final int CONNECT_TIMEOUT = 3000;
    
    private final static Logger logger = LogManager.getLogger();
    private String baseUri;
    private Boolean isEnableSSL = false;
    private int connectTimeout = CONNECT_TIMEOUT;
    
    /**
     * GETリクエストを発行する。
     * 
     * @param <T> 受信データのエンティティクラス
     * @param method
     * @param args
     * @return
     * @throws Exception 
     * @throws HttpClientException 
     */
    @Override
    public <T> T getRequest(String method,  Object...args) throws Exception, HttpClientException {
        HttpURLConnection connection = null;

        try {
            logger.info(RestClient.class.getSimpleName() + "::getRequest start.");
           
            assert StringUtils.isEmpty(this.baseUri) == false;
            assert StringUtils.isEmpty(method) == false;

            StringBuilder sb = new StringBuilder();
            sb.append(this.baseUri);
            sb.append(method);
           
            for (Object arg : args) {
                if (sb.charAt(args.length -1) != '/' && arg.toString().charAt(0) != '/') {
                    sb.append('/');
                }
                sb.append(arg);
            }
              
            connection = this.openConnection(sb.toString());
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");
            connection.connect();
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new HttpClientException(connection.getResponseCode(), connection.getResponseMessage());
            }
           
            Map headers = connection.getHeaderFields();
            InputStream in = connection.getInputStream();

            try (InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(isr)) {
                
                StringBuilder xml = new StringBuilder();
        
                String line;
                while ((line = reader.readLine()) != null) {
                    xml.append(line);
                    logger.debug(line);
                }
                
                Class<?> entityClass = this.getEntityClass();
                logger.debug("Unmarshalling: {0}", entityClass.getName());
                Persister persister = new Persister();
                T entity = (T) persister.read(entityClass, new StringReader(xml.toString()));
                
                return entity;
            }
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
                 
            logger.info(RestClient.class.getSimpleName() + "::getRequest end.");
        }
    }

    /**
     * POSTリクエストを発行する。
     * 
     * @param <T> 受信データのエンティティクラス
     * @param method メソッド名
     * @param param サーバーに送信するオブジェクト
     * @param clazz サーバーに送信するオブジェクトの型
     * @param args
     * @return
     * @throws Exception 
     * @throws HttpClientException
     */
    @Override
    public <T> T postRequest(String method, Object param,  Class clazz, Object...args) throws Exception, HttpClientException {
       HttpURLConnection connection = null;

        try {
            logger.info(RestClient.class.getSimpleName() + "::postRequest start.");
           
            assert StringUtils.isEmpty(this.baseUri) == false;
            assert StringUtils.isEmpty(method) == false;

            StringBuilder sb = new StringBuilder();
            sb.append(this.baseUri);
            sb.append(method);
            
            for (Object arg : args) {
                if (sb.charAt(args.length -1) != '/' && arg.toString().charAt(0) != '/') {
                    sb.append('/');
                }
                sb.append(arg);
            }
           
            connection = this.openConnection(sb.toString());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/xml");
            //connection.setRequestProperty("Content-Length", String.valueOf(os.size()));
           
            try (OutputStream out = connection.getOutputStream()) {
                Serializer serializer = new Persister();
                serializer.write(param, out);
                logger.debug("Parameter : {0}", out.toString());
                out.flush();
            }
           
            connection.connect();
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new HttpClientException(connection.getResponseCode(), connection.getResponseMessage());
            }
            
            Map headers = connection.getHeaderFields();
            InputStream in = connection.getInputStream();

            try (InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(isr)) {
                StringBuilder xml = new StringBuilder();
        
                String line;
                while ((line = reader.readLine()) != null) {
                    xml.append(line);
                    logger.debug(line);
                }
                
                Class<?> entityClass = this.getEntityClass();
                logger.debug("Unmarshalling: {0}", entityClass.getName());
                Persister persister = new Persister();
                T entity = (T) persister.read(entityClass, new StringReader(xml.toString()));
                
                return entity;
            }
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
                 
            logger.info(RestClient.class.getSimpleName() + "::postRequest end.");
        }        
    }
    
    /**
     * サーバーURIを設定する。
     * 
     * @param serverUri 
     */
    @Override
    public void setServerURI(String serverUri) {
        this.baseUri = serverUri;
    }

    /**
     * SSL通信の有効・無効を設定する。
     * 
     * @param isEnable 
     */
    @Override
    public void setEnableSSL(Boolean isEnable) {
        this.isEnableSSL = isEnable;
    }

    /**
     * 接続タイムアウト時間(msec)を設定する。
     * 
     * @param msec 
     */
    @Override
    public void setConnctTimeout(int msec) {
        this.connectTimeout = msec;
    }
    
    /**
     * HTTPコネクションを開く。
     * 
     * @param url
     * @return
     * @throws Exception 
     */
    private HttpURLConnection openConnection(String uri) throws Exception {
         logger.info("Call the REST: {0}, {1}, {2}", uri, isEnableSSL, connectTimeout);

        URL url = new URL(uri);

        if (this.isEnableSSL) {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            SSLContext ctx = this.getSSLContext();
            SSLSocketFactory factory = ctx.getSocketFactory();
            connection.setSSLSocketFactory(factory);
            connection.setConnectTimeout(this.connectTimeout);
            return connection;           
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(this.connectTimeout);
        return connection;
    }
    
   /**
     * SSLContextを取得する。
     * 
     * @return 
     */
    private SSLContext getSSLContext() throws Exception  {
 
        TrustManager[] certs = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                }
            }
        };

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(getClass().getResourceAsStream("/key/newcert.p12"), "adtekfuji".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, "adtekfuji".toCharArray());
        SSLContext context = SSLContext.getInstance("TLS");

        context.init(kmf.getKeyManagers(), certs, new SecureRandom());
        
        return context;
    } 
    
    /**
     * Tの型を取得する。
     * 参考: http://d.hatena.ne.jp/Nagise/20131121/1385046248
     * 
     * @return 
     */
    private Class<?> getEntityClass() {
        // 実行時の型
        Class<?> clazz = this.getClass();
        // "RestClient<Hoge>"を取得
        Type type = clazz.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)type;
        // RestClientの型変数に対するバインドされた型
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        Class<?> entityClass = (Class<?>)actualTypeArguments[0];
        return entityClass;
    }
}
