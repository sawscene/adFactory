package jp.adtekfuji.adfactoryserver.service;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jarsey の設定
 * 
 * @author s-heya
 */
public class JerseyConfig extends ResourceConfig {

    /**
     * コンストラクタ
     */
    public JerseyConfig() {
        this.packages("pakage").register(MultiPartFeature.class);
    }    
}
