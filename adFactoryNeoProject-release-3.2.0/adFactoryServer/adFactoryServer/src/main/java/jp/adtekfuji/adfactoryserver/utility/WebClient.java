/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientResponse;


/**
 * HTTP クライアント
 * 
 * @author s-heya
 */
public class WebClient {

    private final Logger logger = LogManager.getLogger();
    private final String server;

    /**
     * コンストラクタ
     * 
     * @param server 
     */
    public WebClient(String server) {
        this.server = server;
    }

    /**
     * クライアントを作成する。
     * 
     * @return 
     */
    private Client createClient() {
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 3000);
        client.property(ClientProperties.READ_TIMEOUT, 3000);
        return client;
    }

    /**
     * GETリクエストを送信する。
     * 
     * @param command
     * @return 
     */
    public ClientResponse requestGet(String command) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(server);
        sb.append(command);

        Client client = this.createClient();
        WebTarget target = client.target(sb.toString());
        ClientResponse response = target.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);

        switch (response.getStatus()) {
            case 200:
                break;
            default:
                String error = String.format("Status: %s %s", response.getStatus(), response.getStatusInfo().toString());
                throw new RuntimeException(error);
        }
        
        //Client client = ClientBuilder.newClient();
        //Future<Response> futureResp = client.target(sb.toString())
        //        .request()
        //        .async()
        //        .get(new InvocationCallback<Response>() {
        //                @Override
        //                public void completed(final Response resp) {
        //                    logger.info(resp);
        //                }
        //
        //                @Override
        //                public void failed(final Throwable throwable) {
        //                    logger.fatal(throwable);
        //                }
        //            });
        //Response res = futureResp.get();

        return response;
    }
}
