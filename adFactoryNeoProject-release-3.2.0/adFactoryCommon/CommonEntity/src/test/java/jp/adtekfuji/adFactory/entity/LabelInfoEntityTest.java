/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ラベルマスタのテストクラス
 * 
 * @author kentarou.suzuki
 */
public class LabelInfoEntityTest {

    /**
     * コンストラクタ
     */
    public LabelInfoEntityTest() {
    }

    /**
     * テストクラスが初期化される際に一度だけ呼び出される。
     */
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     * テストクラスが解放される際に一度だけ呼び出される。
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * テストメソッドを実行する前に呼び出される。
     */
    @Before
    public void setUp() {
    }

    /**
     * テストメソッドを実行した後に呼び出される。
     */
    @After
    public void tearDown() {
    }

    /**
     * エンティティからXMLに正しくシリアライズされることをテストする。
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testXml() throws Exception {
        System.out.println("testXml");

        LabelInfoEntity entity = new LabelInfoEntity(1L, "name1");
        entity.setFontColor("#00FF00");
        entity.setBackColor("#0000FF");
        entity.setLabelPriority(2);

        Document xml = EntityToXml.getXml(entity);
        String[] expectedNode1 = {"label"};
        String[] expectedNode2 = {"labelId", "labelName", "fontColor", "backColor", "labelPriority"};
        String[] expectedValue2 = {"1", "name1", "#00FF00", "#0000FF", "2"};

        Integer level1 = 0;
        Integer level2 = 0;
        for (Node node1 = xml.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
            if (node1 instanceof Element) {
                System.out.print(node1.getNodeName() + ":" + node1.getTextContent() + CR);
                assertThat(node1.getNodeName(), is(expectedNode1[level1]));
                level1++;
            }
            level2 = 0;
            for (Node node2 = node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2 instanceof Element) {
                    System.out.print(node2.getNodeName() + ":" + node2.getTextContent() + CR);
                    assertThat(node2.getNodeName(), is(expectedNode2[level2]));
                    assertThat(node2.getTextContent(), is(expectedValue2[level2]));
                    level2++;
                }
            }
        }
    }

    /**
     * プロパティバインドが正しく機能していることをテストする。
     * 
     * @throws ConfigurationException
     * @throws IOException 
     */
    @Test
    public void testBindProperty() throws ConfigurationException, IOException {
        System.out.println("testBindProperty");

        LabelInfoEntity entity1 = new LabelInfoEntity(1L, "name1");
        entity1.setFontColor("#FFFFFF");
        entity1.setBackColor("#000000");
        entity1.setLabelPriority(2);
        LabelInfoEntity entity2 = new LabelInfoEntity(2L, "name2");
        entity2.setFontColor("#000000");
        entity2.setBackColor("#FFFFFF");
        entity2.setLabelPriority(3);

        assertThat(entity1.getLabelId(), is(1L));
        assertThat(entity1.getLabelName(), is("name1"));
        assertThat(entity1.getFontColor(), is("#FFFFFF"));
        assertThat(entity1.getBackColor(), is("#000000"));
        assertThat(entity1.getLabelPriority(), is(2));
        assertThat(entity2.getLabelId(), is(2L));
        assertThat(entity2.getLabelName(), is("name2"));
        assertThat(entity2.getFontColor(), is("#000000"));
        assertThat(entity2.getBackColor(), is("#FFFFFF"));
        assertThat(entity2.getLabelPriority(), is(3));

        entity2.labelIdProperty().bind(entity1.labelIdProperty());
        entity2.labelNameProperty().bind(entity1.labelNameProperty());
        entity2.fontColorProperty().bind(entity1.fontColorProperty());
        entity2.backColorProperty().bind(entity1.backColorProperty());
        entity2.labelPriorityProperty().bind(entity1.labelPriorityProperty());
        entity1.setLabelId(3L);
        entity1.setLabelName("name3");
        entity1.setFontColor("#FF00FF");
        entity1.setBackColor("#00FF00");
        entity1.setLabelPriority(4);

        assertThat(entity1.getLabelId(), is(3L));
        assertThat(entity1.getLabelName(), is("name3"));
        assertThat(entity1.getFontColor(), is("#FF00FF"));
        assertThat(entity1.getBackColor(), is("#00FF00"));
        assertThat(entity1.getLabelPriority(), is(4));
        assertThat(entity2.getLabelId(), is(3L));
        assertThat(entity2.getLabelName(), is("name3"));
        assertThat(entity2.getFontColor(), is("#FF00FF"));
        assertThat(entity2.getBackColor(), is("#00FF00"));
        assertThat(entity2.getLabelPriority(), is(4));
    }
}
