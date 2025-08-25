/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.net;

import adtekfuji.utility.StringUtils;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javafx.concurrent.Task;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * リモートストレージ
 * @author s-heya
 * adtekfuji.admanagerapp.workfloweditplugin.netからコピー y-harada
 */
public interface RemoteStorage {
    boolean download(String path, String dest) throws Exception;
    boolean upload(String path, Map<String, String> source) throws Exception;
    Task newUploader(String path, Map<String, String> transfers, Set<String> deletes);
    Object createUploader(String path, Map<String, String> transfers, Set<String> deletes);
    void configuration(String server, String user, String password);

    /**
     * アップロードファイル名を生成する。
     * 
     * @param fileName ファイル名
     * @return アップロードファイル名
     */
    public static String getUploadFileName(String fileName) {
        String name;
                
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(InetAddress.getLocalHost().getHostName());
            sb.append("-");
            sb.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            sb.append(".");
            sb.append(StringUtils.getSuffix(fileName));
            name = sb.toString();
            
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder();

            // ランダム文字列で生成
            sb.append(RandomStringUtils.randomAlphanumeric(8));
            sb.append("-");
            sb.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            sb.append(".");
            sb.append(StringUtils.getSuffix(fileName));
            name = sb.toString();
        }
        
        return name;
    }
}
