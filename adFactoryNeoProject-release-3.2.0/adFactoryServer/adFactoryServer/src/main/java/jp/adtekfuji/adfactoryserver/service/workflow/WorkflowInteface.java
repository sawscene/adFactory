/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.workflow;

import java.util.List;

/**
 *
 * @author ke.yokoi
 */
public interface WorkflowInteface {

    /**
     * 次の工程カンバンに進める
     *
     * @param workId
     * @param serialNumbers
     * @return 
     * @throws Exception
     */
    boolean executeWorkflow(Long workId, List<String> serialNumbers) throws Exception;
}
