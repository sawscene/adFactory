/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author phamvanthanh
 */
public class DummyData {
    private List<KanbanInfoEntity> listCurrentKanban;
    private List<OrganizationInfoEntity> listCurrentOrganization;
    
    public List<KanbanInfoEntity> createKanbanData() {
        listCurrentKanban = new ArrayList<>();
        
        for (int i = 1 ; i < 37 ; i++) {
            listCurrentKanban.add(new KanbanInfoEntity((long)i, (long)i, "Serial " + i, "s" + i));
        }
        
        return listCurrentKanban;
    }
    
    public List<OrganizationInfoEntity> createOrganizationData() {
        listCurrentOrganization = new ArrayList<>();
        
        for (int i = 1 ; i < 18 ; i++) {
            listCurrentOrganization.add(new OrganizationInfoEntity((long)i, "w" + i, "Worker " + i, AuthorityEnum.WORKER));
        }        
        
        return listCurrentOrganization;
    }
}
