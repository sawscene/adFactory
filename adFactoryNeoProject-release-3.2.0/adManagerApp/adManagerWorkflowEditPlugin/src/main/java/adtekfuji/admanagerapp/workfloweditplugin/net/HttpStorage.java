/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.net;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.locale.LocaleUtils;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTPストレージ
 *
 * @author s-heya
 */
public class HttpStorage implements RemoteStorage, UITask {
    private static final Logger logger = LogManager.getLogger();
    private static final String PROTOCOL_HTTPS = "https";
 
    private final StringProperty messageProperty = new SimpleStringProperty();
    private final DoubleProperty progressProperty = new SimpleDoubleProperty();
 
    private String baseUri;
    private SSLContext sslContetxt;
    private int index;
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * アップローダー
     */
    public class Uploader implements Callable<Boolean>, UITask {
        private final String path;
        private final Map<String, String> transfers;
        private final Set<String> deletes;
        private final StringProperty messageProperty = new SimpleStringProperty();
        private final DoubleProperty progressProperty = new SimpleDoubleProperty();
        
        public Uploader(String path, Map<String, String> transfers, Set<String> deletes) {
            this.path = path;
            this.transfers = transfers;
            this.deletes = deletes;
        }

        @Override
        public StringProperty messageProperty() {
            return this.messageProperty;
        }

        @Override
        public DoubleProperty progressProperty() {
            return this.progressProperty;
        }
        
        @Override
        public Boolean call() throws Exception {
            FTPClient ftpClient = new FTPClient();

            try {
                logger.info(Uploader.class.getSimpleName() + "::upload start: {},{},{}", path, transfers, deletes);

                for (String filePath : transfers.values()) {
                    File file = new File(filePath);
                    if (!file.exists() || !file.isFile()) {
                        String msg = "!!! Not found file: " + file;
                        throw new RemoteStorageException(-1, msg);
                    }
                }

                final String message = LocaleUtils.getString("key.UploadDocuments");
                index = 0;

                ftpClient.connect(baseUri);
                int reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    String msg = "Connect fail.";
                    throw new RemoteStorageException(-1, msg);
                }

                // ログイン
                ftpClient.login("adtek", "adtek");

                ftpClient.setBufferSize(1024);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                // ディレクトリをチェック
                if (!ftpClient.changeWorkingDirectory(path)) {
                    ftpClient.makeDirectory(path);

                    if (!ftpClient.changeWorkingDirectory(path)) {
                        String msg = "Directory does not exist: " + path;
                        throw new RemoteStorageException(-1, msg);
                    }
                }

                // 既存ファイルを削除
                for (String fileName : deletes) {
                    String filePath = path + "/" + fileName;

                    FTPFile[] files = ftpClient.listFiles(filePath);
                    if (0 < files.length) {
                        logger.info("Delete the file: " + filePath);
                        int retry = 3;
                        while (!ftpClient.deleteFile(filePath)) {
                            logger.info("Could not delete the file: " + filePath);
                            if (0 >= retry) {
                                return false;
                            }
                            retry--;
                            TimeUnit.SECONDS.sleep(3L);
                        }
                    }
                }

                // ファイルをアップロード
                for (Map.Entry<String, String> data : transfers.entrySet()) {

                    index++;

                    String trans = path + "/transfer";
                    String target = path + "/" + data.getKey();
                    String filePath = data.getValue();
                    File file = new File(filePath);

                    try (OutputStream output = new BufferedOutputStream(ftpClient.storeFileStream(trans))) {
                        CopyStreamListener listener = new CopyStreamListener() {
                            @Override
                            public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
                                int transferred = (int) Math.round(((double) totalBytesTransferred / (double) streamSize) * 100D);
                                messageProperty.set(message + "(" + index + "/" + transfers.size() + ")");
                                progressProperty.set(transferred);
                            }

                            @Override
                            public void bytesTransferred(CopyStreamEvent event) {
                                progressProperty.set(0);
                            }
                        };

                        Util.copyStream(new FileInputStream(file), output, ftpClient.getBufferSize(), file.length(), listener);
                    }

                    ftpClient.completePendingCommand();
                    logger.info("File transferred.");

                    //ftpClient.deleteFile(target);
                    //logger.info("File deletsd: " + target);

                    if (ftpClient.rename(trans, target)) {
                        logger.info("File renamed.");
                    } else {
                        logger.info("Could not rename the file: " + filePath);
                    }
                }

                return true;

            } catch (Exception ex) {
                logger.fatal(ex);
                messageProperty.set(ex.getMessage());
                throw ex;
                
            } finally {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }

                logger.info(Uploader.class.getSimpleName() + "::upload end.");
            }
        }
    }
       
    @Override
    public StringProperty messageProperty() {
        return this.messageProperty;
    }

    @Override
    public DoubleProperty progressProperty() {
        return this.progressProperty;
    }

    /**
     * ファイルをダウンロードして、ローカルストレージに保存する。
     *
     * @param path
     * @param dest
     * @return
     * @throws Exception
     */
    @Override
    public boolean download(String path, String dest) throws Exception {
        try {
            logger.info(HttpStorage.class.getSimpleName() + "::download start: " + path + ", " + dest);

            URI parent = new URI(baseUri);
            URI relative = new URI(path);
            URI uri = parent.resolve(parent.relativize(relative));
            HttpURLConnection conn = (HttpURLConnection) this.openConnection(uri);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.connect();

            int httpStatusCode = conn.getResponseCode();

            if (httpStatusCode != HttpURLConnection.HTTP_OK){
                throw new RemoteStorageException(httpStatusCode, conn.getResponseMessage());
            }

            // Input Stream
            try (DataInputStream in = new DataInputStream(conn.getInputStream())) {

                // Output Stream
                try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dest)))) {

                    // Read Data
                    byte[] b = new byte[4096];
                    int readByte = 0;

                    while (-1 != (readByte = in.read(b))) {
                        out.write(b, 0, readByte);
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //DialogBox.alert(null, ex);
            throw ex;
        }
        finally {
            logger.info(HttpStorage.class.getSimpleName() + "::download end.");
        }
    }

    /**
     * ファイルをアップロードする。
     *
     * @param path
     * @param transfers
     * @return
     * @throws Exception
     */
    @Override
    public boolean upload(String path, Map<String, String> transfers) throws Exception {
        FTPClient ftpClient = new FTPClient();

        try {
            logger.info(Uploader.class.getSimpleName() + "::upload start: {},{},{}", path, transfers);

            for (String filePath : transfers.values()) {
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    logger.warn("!!! Not found file: " + file);
                    return false;
                }
            }

            final String message = LocaleUtils.getString("key.UploadDocuments");
            index = 0;

            ftpClient.connect(baseUri);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.fatal("Connect fail.");
                return false;
            }

            // ログイン
            if (ftpClient.login("adtek", "adtek") == false) {
                logger.fatal("Login fail.");
                return false;
            }

            ftpClient.setBufferSize(1024);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // ディレクトリをチェック
            if (!ftpClient.changeWorkingDirectory(path)) {
                logger.fatal("Directory does not exist: " + path);
                if (!ftpClient.makeDirectory(path)) {
                    logger.fatal("Failed to create directory: " + path);
                    return false;
                }

                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.fatal("Directory does not exist: " + path);
                    return false;
                }
            }

            // ファイルをアップロード
            for (Map.Entry<String, String> data : transfers.entrySet()) {

                index++;

                String trans = path + "/transfer";
                String target = path + "/" + data.getKey();
                String filePath = data.getValue();
                File file = new File(filePath);

                try (OutputStream output = new BufferedOutputStream(ftpClient.storeFileStream(trans))) {
                    CopyStreamListener listener = new CopyStreamListener() {
                        @Override
                        public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
                           int transferred = (int) Math.round(((double) totalBytesTransferred / (double) streamSize) * 100D);
                        }

                        @Override
                        public void bytesTransferred(CopyStreamEvent event) {
                        }
                    };

                    Util.copyStream(new FileInputStream(file), output, ftpClient.getBufferSize(), file.length(), listener);
                }

                ftpClient.completePendingCommand();
                logger.info("File transferred.");

                //ftpClient.deleteFile(target);
                //logger.info("File deletsd: " + target);

                if (ftpClient.rename(trans, target)) {
                    logger.info("File renamed.");
                } else {
                    logger.info("Could not rename the file: " + filePath);
                }
            }

            return true;

        } catch (Exception ex) {
            logger.fatal(ex);
            return false;

        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }

            logger.info(Uploader.class.getSimpleName() + "::upload end.");
        }
    }
    
    /**
     * ファイルをアップロードする。
     *
     * @param path
     * @param transfers
     * @param deletes
     * @return
     */
    @Override
    public Task newUploader(String path, Map<String, String> transfers, Set<String> deletes) {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                FTPClient ftpClient = new FTPClient();

                try {
                    logger.info(HttpStorage.class.getSimpleName() + "::upload start: {},{},{}", path, transfers, deletes);

                    for (String filePath : transfers.values()) {
                        File file = new File(filePath);
                        if (!file.exists() || !file.isFile()) {
                            String msg = "!!! Not found file: " + file;
                            logger.fatal(msg);
                            messageProperty.set(msg);
                            throw new RemoteStorageException(-1, msg);
                        }
                    }

                    final String message = LocaleUtils.getString("key.UploadDocuments");
                    index = 0;

                    ftpClient.connect(baseUri);
                    int reply = ftpClient.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        String msg = "Connect fail.";
                        logger.fatal(msg);
                        messageProperty.set(msg);
                        throw new RemoteStorageException(-1, msg);
                    }

                    // ログイン
                    ftpClient.login("adtek", "adtek");

                    ftpClient.setBufferSize(1024);
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    // ディレクトリをチェック
                    if (!ftpClient.changeWorkingDirectory(path)) {
                        ftpClient.makeDirectory(path);
                        if (!ftpClient.changeWorkingDirectory(path)) {
                            String msg = "Directory does not exist: " + path;
                            logger.fatal(msg);
                            messageProperty.set(msg);
                            throw new RemoteStorageException(-1, msg);
                        }
                    }

                    // 既存ファイルを削除
                    //for (String fileName : deletes) {
                    //    String filePath = path + "/" + fileName;
                    //
                    //    FTPFile[] files = ftpClient.listFiles(filePath);
                    //    if (0 < files.length) {
                    //        logger.info("Delete the file: " + filePath);
                    //        int retry = 3;
                    //        while (!ftpClient.deleteFile(filePath)) {
                    //            logger.info("Could not delete the file: " + filePath);
                    //            if (0 >= retry) {
                    //                return false;
                    //            }
                    //            retry--;
                    //            TimeUnit.SECONDS.sleep(3L);
                    //        }
                    //    }
                    //}

                    // ファイルをアップロード
                    for (Map.Entry<String, String> data : transfers.entrySet()) {

                        index++;

                        String trans = path + "/transfer";
                        String target = path + "/" + data.getKey();
                        String filePath = data.getValue();
                        File file = new File(filePath);

                        try (OutputStream output = new BufferedOutputStream(ftpClient.storeFileStream(trans))) {
                            CopyStreamListener listener = new CopyStreamListener() {
                                @Override
                                public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
                                    int transferred = (int) Math.round(((double) totalBytesTransferred / (double) streamSize) * 100D);
                                    updateMessage(message + "(" + index + "/" + transfers.size() + ")");
                                    updateProgress(transferred, 100);
                                }

                                @Override
                                public void bytesTransferred(CopyStreamEvent event) {
                                    updateProgress(0, 100);
                                }
                            };

                            Util.copyStream(new FileInputStream(file), output, ftpClient.getBufferSize(), file.length(), listener);
                        }

                        ftpClient.completePendingCommand();
                        logger.info("File transferred.");

                        //ftpClient.deleteFile(target);
                        //logger.info("File deletsd: " + target);

                        if (ftpClient.rename(trans, target)) {
                            logger.info("File renamed.");
                        } else {
                            logger.info("Could not rename the file: " + filePath);
                        }
                    }

                    return true;

                } catch (Exception ex) {
                    logger.fatal(ex);
                    messageProperty.set(ex.getMessage());
                    throw ex;

                } finally {
                    if (ftpClient.isConnected()) {
                        ftpClient.disconnect();
                    }

                    logger.info(HttpStorage.class.getSimpleName() + "::upload end.");
                }
            }
        };
    }
   
    /**
     * アップローダーを生成する。
     * 
     * @param path
     * @param transfers
     * @param deletes
     * @return 
     */
    @Override
    public Object createUploader(String path, Map<String, String> transfers, Set<String> deletes) {
        return new Uploader(path, transfers, deletes);
    }

    /**
     * HTTPストレージを構成する。
     *
     * @param server
     * @param user
     * @param password
     */
    @Override
    public void configuration(String server, String user, String password) {
        this.baseUri = server;
    }

    /**
     * HTTPコネクションを開く。
     *
     * @param url
     * @return
     * @throws Exception
     */
    private HttpURLConnection openConnection(URI uri) throws Exception {
        int connectTimeout = ClientServiceProperty.getConnectTimeout();
        logger.info("HttpURLConnection: ", uri + " " + connectTimeout);

        URL url = uri.toURL();
        if (url.getProtocol().equals(PROTOCOL_HTTPS)) {
            this.initSSLContext();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(connectTimeout);
            return connection;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(connectTimeout);
        return connection;
    }

    /**
     * SSLContextを初期化する。
     */
    private void initSSLContext() throws Exception  {

        if (Objects.isNull(sslContetxt)) {
            String password = "adtekfuji";

            TrustManager[] certs = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                }
            };

            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(this.getClass().getResourceAsStream("/jp/adtekfuji/adappeco/key/newcert.p12"), password.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, password.toCharArray());

            sslContetxt = SSLContext.getInstance("TLS");
            sslContetxt.init(kmf.getKeyManagers(), certs, null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContetxt.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((String hostname, SSLSession session) -> true);
        }
    }
}
