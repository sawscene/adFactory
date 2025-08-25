/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalFlowModel;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;

/**
 * 承認フロー
 *
 * @author nar-nakamura
 */
@Singleton
@Path("approval")
public class ApprovalFlowFacade {

    @Inject
    private ApprovalFlowModel model;

    /**
     * 工程・工程順の変更を申請する。
     *
     * @param entity 申請情報
     * @param authId 認証ID
     * @return 結果
     */
    @POST
    @Path("apply")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response apply(ApprovalEntity entity, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.model.apply(entity, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 工程・工程順の変更申請を取り消す。
     *
     * @param entity 申請情報
     * @param authId 認証ID
     * @return 結果
     */
    @PUT
    @Path("cancel")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response cancelApply(ApprovalEntity entity, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.model.cancelApply(entity, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }
}
