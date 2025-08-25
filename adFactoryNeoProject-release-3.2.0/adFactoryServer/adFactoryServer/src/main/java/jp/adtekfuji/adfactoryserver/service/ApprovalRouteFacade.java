/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalRouteEntity;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalFlowModel;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;

/**
 * 承認ルート
 *
 * @author nar-nakamura
 */
@Singleton
@Path("approval-route")
public class ApprovalRouteFacade {

    @Inject
    private ApprovalFlowModel model;

    /**
     * 承認ルート情報を登録する。
     *
     * @param entity 承認ルート情報
     * @param authId 認証ID
     * @return 結果
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response add(ApprovalRouteEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        ResponseEntity response = this.model.addApprovalRoute(entity, authId);
        if (response.isSuccess()) {
            URI uri = new URI(response.getUri());
            return Response.created(uri).entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * ルートIDを指定して、承認ルート情報をコピーする。(承認順情報一覧もコピー)
     *
     * @param id ルートID
     * @param authId 認証ID
     * @return 結果
     * @throws URISyntaxException 
     */
    @POST
    @Path("copy/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response copy(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws URISyntaxException {
        ResponseEntity response = this.model.copyApprovalRoute(id, authId);
        if (response.isSuccess()) {
            URI uri = new URI(response.getUri());
            return Response.created(uri).entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 承認ルート情報を更新する。(承認順情報一覧も更新)
     *
     * @param entity
     * @param authId
     * @return
     * @throws URISyntaxException 
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response update(ApprovalRouteEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        ResponseEntity response = this.model.updateApprovalRoute(entity, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * ルートIDを指定して、承認ルート情報を削除する。(承認順情報も削除)
     *
     * @param id ルートID
     * @param authId 認証ID
     * @return 結果
     */
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.model.removeApprovalRoute(id, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * ルートIDを指定して、承認ルート情報を取得する。(承認順情報も取得)
     *
     * @param id ルートID
     * @param authId 認証ID
     * @return 承認ルート情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public ApprovalRouteEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        return this.model.findApprovalRoute(id, authId);
    }

    /**
     * 承認ルート情報一覧を取得する。(承認順情報も取得)
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 承認ルート情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public List<ApprovalRouteEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        return this.model.findApprovalRouteRange(from, to, authId);
    }

    /**
     * 承認ルート情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        long count = this.model.countAllApprovalRoute(authId);
        return String.valueOf(count);
    }
}
