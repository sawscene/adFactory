/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.BeforeClass;

/**
 * 品番マスタ情報RESTのユニットテスト
 * 
 * @author s-heya
 */
public class DsItemFacadeTest {
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static DsItemFacade rest = null;

    /**
     * ユニットテストの初期設定
     */
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        rest = new DsItemFacade();
        rest.setEntityManager(em);
    }
}
