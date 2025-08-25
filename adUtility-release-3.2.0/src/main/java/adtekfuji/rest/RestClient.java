/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/**
 * RESTful Web API クライアント
 * 
 * @author s-heya
 * @param <T>
 */
public class RestClient<T> {

    private final static Logger logger = LogManager.getLogger();
    private final RestClientProperty property;

    public RestClient() {
        this.property = new RestClientProperty();
    }

    public RestClient(RestClientProperty properties) {
        this.property = properties;
    }

    @Deprecated
    public RestClient(final String uriBase) {
        this.property = new RestClientProperty(uriBase);
    }

    @Deprecated
    public void setUriBase(final String uriBase) {
        this.property.setUriBase(uriBase);
    }

    /**
     * 接続タイムアウト時間を取得する。
     * 
     * @return 
     */
    public Integer getConnectTimeout() {
        return property.getConnectTimeout();
    }

    /**
     * 接続タイムアウト時間を設定する。
     * 
     * @param timeout 
     */
    public void setConnectTimeout(Integer timeout) {
        property.setConnectTimeout(timeout);
    }

    /**
     * 読込タイムアウト時間を取得する。
     * 
     * @return 
     */
    public Integer getReadTimeout() {
        return property.getReadTimeout();
    }

    /**
     * 読込タイムアウト時間を設定する。
     * 
     * @param timeout 
     */
    public void setReadTimeout(Integer timeout) {
        property.setReadTimeout(timeout);
    }

    /**
     * RESTful Web API の Client を作成する。
     * 
     * @return Client
     */
    private Client createClient() {
        Client client = property.isEncryptConnection() ?
                ClientBuilder.newBuilder()
                        .sslContext(this.getSSLContext())
                        .hostnameVerifier(this.createHostNameVerifier()).build()
                : ClientBuilder.newClient();
        
        client.property(ClientProperties.CONNECT_TIMEOUT, property.getConnectTimeout());
        client.property(ClientProperties.READ_TIMEOUT, property.getReadTimeout());

        return client;
    }

    /**
     * SSLContext を取得する。
     * 
     * @return SSLContext
     */
    private SSLContext getSSLContext() {
        SSLContext context = null;

        TrustManager[] certs = new TrustManager[]{
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

        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(getClass().getResourceAsStream("/key/newcert.p12"), "adtekfuji".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, "adtekfuji".toCharArray());
            context = SSLContext.getInstance("TLS");

            context.init(kmf.getKeyManagers(), certs, new SecureRandom());

        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            logger.fatal(ex, ex);
        }
        return context;
    }
    
    /**
     * ホスト名検証ハンドラを作成する。
     * 
     * @return HostnameVerifier
     */
    private HostnameVerifier createHostNameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
    }
    
    /**
     * GET
     *
     * @param path
     * @param retType
     * @return
     */
    public List<T> findAll(String path, GenericType<List<T>> retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        WebTarget target = client.target(sb.toString());
        Response response = target.request(property.getMediaType()).get();
        
        switch (response.getStatus()) {
            case 200:  // OK
                break;
            default:
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                throw new RuntimeException(error);
        }
        return response.readEntity(retType);
    }

    /**
     * GET
     *
     * @param path
     * @param id
     * @param retType
     * @return
     */
    public Object find(String path, Object id, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        sb.append("/");
        sb.append(id.toString());

        Client client = createClient();
        WebTarget target = client.target(sb.toString());
        Response response = target.request(property.getMediaType()).get();

        switch (response.getStatus()) {
            case 200:  // OK
                break;
            default:
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                throw new RuntimeException(error);
        }

        return response.readEntity(retType);
    }

    /**
     * GET
     *
     * @param path
     * @param retType
     * @return
     */
    public Object find(String path, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        WebTarget target = client.target(sb.toString());
        Response response = target.request(property.getMediaType()).get();
        
        switch (response.getStatus()) {
            case 200:  // OK
                break;
            default:
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                throw new RuntimeException(error);
        }
        
        return response.readEntity(retType);
    }

    /**
     * GET
     *
     * @param path
     * @param mediaType
     * @param retType
     * @return
     */
    public Object find(String path, MediaType mediaType, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        WebTarget target = client.target(sb.toString());
        Response response = target.request(mediaType).get();
        
        switch (response.getStatus()) {
            case 200:  // OK
                break;
            default:
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                throw new RuntimeException(error);
        }
        
        return response.readEntity(retType);
    }

    /**
     * GET
     *
     * @param path
     * @param retType
     * @return
     */
    public List<T> find(String path, GenericType<List<T>> retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        WebTarget target = client.target(sb.toString());
        Response response = target.request(property.getMediaType()).get();
        
        switch (response.getStatus()) {
            case 200:  // OK
                break;
            default:
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                error = error + " ### : " + sb.toString();
                throw new RuntimeException(error);
        }
        
        return response.readEntity(retType);
    }

    /**
     * GET
     *
     * @param path
     * @param id
     * @param retType
     * @return
     */
    public List<T> find(String path, Object id, GenericType<List<T>> retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        sb.append("/");
        sb.append(id.toString());
        
        Client client = createClient();
        WebTarget target = client.target(sb.toString());
        Response response = target.request(property.getMediaType()).get();
        
        switch (response.getStatus()) {
            case 200:  // OK
                break;
            default:
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                error = error + " ### : " + sb.toString();
                throw new RuntimeException(error);
        }
        
        return response.readEntity(retType);
    }

    /**
     * POST
     *
     * @param path
     * @param entity
     * @return 
     */
    public Response post(String path, Object entity) {
        Client client = createClient();
        Invocation.Builder builder = client.target(path).request(property.getMediaType());
        return builder.post(Entity.entity(entity, property.getMediaType()));
    }

    /**
     * POST
     *
     * @param path
     * @param entity
     * @param retType
     * @return
     */
    public Object post(String path, Object entity, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        Invocation.Builder builder = client.target(sb.toString()).request(property.getMediaType());
        Response response = builder.post(Entity.entity(entity, property.getMediaType()));
        
        switch (response.getStatus()) {
            case 200:   // OK
            case 201:   // CREATED
            case 500:   // Internal Server Error
                if (Objects.isNull(retType)) {
                    return null;
                }
                return response.readEntity(retType);
            case 202:   // Accepted
            case 204:   // No Content
                return null;
            default:    // OK, CREATED
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                error = error + " ### : " + sb.toString();
                throw new RuntimeException(error);
        }
    }

    /**
     * POST
     *
     * @param path
     * @param entity
     * @param retType
     * @return
     */
    public List<T> post(String path, Object entity, GenericType<List<T>> retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        Invocation.Builder builder = client.target(sb.toString()).request(property.getMediaType());
        Response response = builder.post(Entity.entity(entity, property.getMediaType()));
        
        switch (response.getStatus()) {
            case 200:   // OK
            case 201:   // CREATED
            case 500:   // Internal Server Error
            case 202:   // Accepted
            case 204:   // No Content
                if (Objects.isNull(retType)) {
                    return null;
                }
                logger.info(response.getStatus());
                return response.readEntity(retType);
            default:    // OK, CREATED
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                error = error + " ### : " + sb.toString();
                throw new RuntimeException(error);
        }
    }

    /**
     * postexp
     *
     * @param path
     * @param entity
     * @return
     */
    public int postexp(String path, Object entity) {
        Client client = createClient();
        Invocation.Builder builder = client.target(path).request(property.getMediaType());
        Response response = builder.post(Entity.entity(entity, property.getMediaType()));
        return response.getStatus();
    }

    /**
     * POST
     *
     * @param path
     * @param entity
     * @param retType
     * @return
     */
    public Object postByGeneric(String path, Object entity, GenericType<List<T>> retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        
        Client client = createClient();
        Invocation.Builder builder = client.target(sb.toString()).request(property.getMediaType());
        Response response = builder.post(Entity.entity(entity, property.getMediaType()));
        
        switch (response.getStatus()) {
            case 200:   // OK
            case 201:   // CREATED
            case 500:   // Internal Server Error
                if (Objects.isNull(retType)) {
                    return null;
                }
                return response.readEntity(retType);
            case 202:   // Accepted
            case 204:   // No Content
                return null;
            default:    // OK, CREATED
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                error = error + " ### : " + sb.toString();
                throw new RuntimeException(error);
        }
    }

    /**
     * PUT
     *
     * @param path
     * @param entity
     * @param mediaType
     * @param retType
     * @return
     */
    public Object put(String path, Object entity, MediaType mediaType, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        return sendRequest(sb.toString(), entity, mediaType, retType);
    }

    /**
     * PUT
     *
     * @param path RESTパス
     * @param entity Body
     * @param resType Responce type
     * @param contentType Content type
     * @param retType Result class type
     * @return Result object
     */
    public Object put(String path, Object entity, MediaType resType, MediaType contentType, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        return sendRequest(sb.toString(), entity, resType, contentType, retType, null);
    }

    /**
     * PUT
     *
     * @param path
     * @param entity
     * @param id
     * @param retType
     * @return
     */
    public Object put(String path, Object entity, Integer id, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        sb.append("/");
        sb.append(id);
        return sendRequest(sb.toString(), entity, property.getMediaType(), retType);
    }

    /**
     * PUT
     *
     * @param path
     * @param entity
     * @param retType
     * @return
     */
    public Object put(String path, Object entity, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        return sendRequest(sb.toString(), entity, property.getMediaType(), retType);
    }

    /**
     * PUT
     *
     * @param path RESTパス
     * @param entity Body
     * @param retType Result class type
     * @param readTimeout 読込タイムアウト時間(ms)
     * @return Result object
     */
    public Object putWithTimeout(String path, Object entity, Class retType, int readTimeout) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        return this.sendRequest(sb.toString(), entity, property.getMediaType(), property.getMediaType(), retType, readTimeout);
    }

    /**
     * PUT
     *
     * @param path
     * @param entity
     */
    public void put(String path, Object entity) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        sendRequest(sb.toString(), entity, property.getMediaType(), null);
    }

    /**
     * 送信
     *
     * @param uri RESTパス
     * @param entity Body
     * @param contentType Result content type
     * @param retType Result class type
     * @return Result object
     */
    private Object sendRequest(String uri, Object entity, MediaType contentType, Class retType) {
        return this.sendRequest(uri, entity, property.getMediaType(), contentType, retType, null);
    }

    /**
     * HTTP 要求を送信する。
     *
     * @param uri RESTパス
     * @param entity Body
     * @param resType Response Type
     * @param contentType Content Type
     * @param retType Result class type
     * @param readTimeout 読込タイムアウト時間(ms)
     * @return Result object
     */
    private Object sendRequest(String uri, Object entity, MediaType resType, MediaType contentType, Class retType, Integer readTimeout) {
        Client client = createClient();

        if (Objects.nonNull(readTimeout)) {
            client.property(ClientProperties.READ_TIMEOUT, readTimeout);
        } else {
            client.property(ClientProperties.READ_TIMEOUT, property.getReadTimeout());
        }

        if (Objects.isNull(entity)) {
            client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        }

        Invocation.Builder builder = client.target(uri).request(contentType);
        Response response = builder.put(Entity.entity(entity, resType));

        switch (response.getStatus()) {
            case 200:   // OK
            case 201:   // CREATED
            case 500:   // Internal Server Error
                if (Objects.isNull(retType)) {
                    return null;
                }
                return response.readEntity(retType);
            case 202:   // Accepted
            case 204:   // No Content
                return null;
            default:    // OK, CREATED
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                throw new RuntimeException(error);
        }
    }

    /**
     * PUT
     *
     * @param path
     * @param entity
     * @param retType
     * @return
     */
    public List<T> put(String path, Object entity, GenericType<List<T>> retType) {
        try {
            logger.info("sendRequestCollection start: {}", path);

            StringBuilder sb = new StringBuilder();
            sb.append(this.property.getUriBase());
            sb.append(path);

            Client client = createClient();
            Invocation.Builder builder = client.target(sb.toString()).request(property.getMediaType());
            Response response = builder.put(Entity.entity(entity, property.getMediaType()));

            switch (response.getStatus()) {
                case 200:   // OK
                case 201:   // CREATED
                case 500:   // Internal Server Error
                    if (Objects.isNull(retType)) {
                        return null;
                    }
                    return response.readEntity(retType);
                default:    // OK, CREATED
                    String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                    throw new RuntimeException(error);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("sendRequestCollection end.");
        }
    }

    /**
     * DELETE
     *
     * @param path
     * @param id
     */
    public void delete(String path, Integer id) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        sb.append("/");
        sb.append(id);
        sendDelete(sb.toString(), null);
    }

    /**
     * DELETE
     *
     * @param path
     * @param id
     * @param retType
     * @return
     */
    public Object delete(String path, Long id, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        sb.append("/");
        sb.append(id);
        return sendDelete(sb.toString(), retType);
    }

    /**
     * DELETE
     *
     * @param path
     * @param retType
     * @return
     */
    public Object delete(String path, Class retType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property.getUriBase());
        sb.append(path);
        return sendDelete(sb.toString(), retType);
    }

    /**
     * DELETE 要求を送信する。
     * 
     * @param uri
     * @param retType
     * @return 
     */
    private Object sendDelete(String uri, Class retType) {
        Client client = createClient();
        Invocation.Builder builder = client.target(uri).request(property.getMediaType());
        Response response = builder.delete();

        switch (response.getStatus()) {
            case 200:   // OK
            case 201:   // CREATED
            case 500:   // Internal Server Error
                if (Objects.isNull(retType)) {
                    return null;
                }
                return response.readEntity(retType);
            case 202:   // Accepted
            case 204:   // No Content
                return null;
            default:    // OK, CREATED
                String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().toString());
                error = error + " ### : " + uri;
                throw new RuntimeException(error);
        }
    }

    /**
     * HTML 形式にエンコードする
     *
     * @param str
     * @return
     */
    public static String encode(String str) {
        try {
            String encodeStr = URLEncoder.encode(str, "UTF-8");
            encodeStr = encodeStr.replace("*", "%2a");
            encodeStr = encodeStr.replace("-", "%2d");
            return encodeStr;
        }
        catch (Exception ex) {
            logger.fatal(ex);
        }
        return null;
    }
    
    /**
     * ファイルをアップロードする。
     * 
     * @param path
     * @param filePath
     * @param retType
     * @return 
     */
    public Object upload(String path, String filePath, Class retType) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(property.getUriBase());
            sb.append(path);

            Client client = createClient();
            WebTarget target = client.register(MediaType.MULTIPART_FORM_DATA_TYPE).target(sb.toString());

            MultiPart multiPart = new MultiPart();
            multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

            FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                new File(filePath),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
            multiPart.bodyPart(fileDataBodyPart);

            Response response = target.request(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(multiPart, multiPart.getMediaType()));
            return response.readEntity(retType);
         
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ファイルをアップロードする。
     * 
     * @param <T>
     * @param path
     * @param filePath
     * @param retType
     * @return 
     */
    public <T> T upload(String path, String filePath, GenericType<T> retType) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(property.getUriBase());
            sb.append(path);

            Client client = createClient();
            WebTarget target = client.register(MediaType.MULTIPART_FORM_DATA_TYPE).target(sb.toString());

            MultiPart multiPart = new MultiPart();
            multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

            FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                new File(filePath),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
            multiPart.bodyPart(fileDataBodyPart);

            Response response = target.request(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(multiPart, multiPart.getMediaType()));
            return response.readEntity(retType);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ファイルをダウンロードする。
     * 
     * @param path パス
     * @param retType 戻り値の型
     * @return retTypeで指定した型のレスポンス
     */
    public Object download(String path, Class retType) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(property.getUriBase());
            sb.append(path);

            Client client = createClient();
            WebTarget target = client.target(sb.toString());
            Response response = target.request(MediaType.APPLICATION_OCTET_STREAM).get();

            switch (response.getStatus()) {
                case 200:   // OK
                    if (Objects.isNull(retType)) {
                        return null;
                    }
                    return response.readEntity(retType);
                default:
                    String error = String.format("Code:%s Info:%s", response.getStatus(), response.getStatusInfo().getFamily().toString());
                    throw new RuntimeException(error);
            }
         
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
}
