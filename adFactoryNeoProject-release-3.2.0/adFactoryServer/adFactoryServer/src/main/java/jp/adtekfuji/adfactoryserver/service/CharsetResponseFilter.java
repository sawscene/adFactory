/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.io.IOException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;


/**
 * レスポンスフィルタ (無効)
 * 
 * レスポンスの Content-Type に charset=UTF-8 を設定して、文字コードを明示する。
 * 有効にするには、アノテーションのコメントを外す。
 * 
 * @author s-heya
 */
//@Provider
//@Priority(Priorities.HEADER_DECORATOR)
public class CharsetResponseFilter implements ContainerResponseFilter {

    /**
     * レスポンスフィルタ
     * 
     * @param reqContext コンテナリクエストコンテキスト
     * @param resContext コンテナレスポンスコンテキスト
     * @throws IOException 
     */
    @Override
    public void filter(final ContainerRequestContext reqContext, final ContainerResponseContext resContext) throws IOException {
        final MultivaluedMap<String, Object> headers = resContext.getHeaders();
        MediaType contentType = resContext.getMediaType();
        if (contentType == null) {
            headers.putSingle("Content-Type", "application/xml;charset=UTF-8");
        } else if (!contentType.toString().contains(";charset=") ) {
            headers.putSingle("Content-Type", contentType.toString() + ";charset=UTF-8");
        }
    }
}