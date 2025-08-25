/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * データベース操作処理用クラス(テスト用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
public class DatabaseControll {

    public static void reset(EntityManager em, EntityTransaction tx) {
        tx.begin();
        // ユニットテンプレート
        em.createQuery("DELETE FROM ConUnitTemplateAssociateEntity").executeUpdate();
        em.createQuery("DELETE FROM ConUnitTemplateHierarchyEntity").executeUpdate();
        em.createQuery("DELETE FROM TreeUnitTemplateHierarchyEntity").executeUpdate();
        em.createQuery("DELETE FROM UnitTemplateHierarchyEntity").executeUpdate();
        em.createQuery("DELETE FROM UnitTemplateEntity").executeUpdate();
        em.createQuery("DELETE FROM UnitTemplatePropertyEntity").executeUpdate();

        // 生産ユニット
        em.createQuery("DELETE FROM ConUnitAssociateEntity").executeUpdate();
        em.createQuery("DELETE FROM ConUnitHierarchyEntity").executeUpdate();
        em.createQuery("DELETE FROM TreeUnitHierarchyEntity").executeUpdate();
        em.createQuery("DELETE FROM UnitHierarchyEntity").executeUpdate();
        em.createQuery("DELETE FROM UnitEntity").executeUpdate();
        em.createQuery("DELETE FROM UnitPropertyEntity").executeUpdate();

        // その他
        em.createQuery("DELETE FROM TVer").executeUpdate();

        tx.commit();
    }

}
