/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimereporter.mail;

import java.util.ArrayList;
import java.util.List;
import javax.mail.internet.MimeMessage;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
/**
 *
 * @author nar-nakamura
 */
public class MailUtilsTest {

    private Wiser wiser;// ダミーのメールサーバー

    private static final String dummySmtpHost = "localhost";
    private static final int dummySmtpPort = 2500;

    public MailUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        // ダミーのメールサーバーを起動する。
        wiser = new Wiser();
        wiser.setHostname(dummySmtpHost);
        wiser.setPort(dummySmtpPort);
        wiser.start();
    }

    @After
    public void tearDown() {
        // ダミーのメールサーバーを停止する。
        wiser.stop();
    }

    /**
     * Test of send method, of class MailUtils.
     * @throws java.lang.Exception
     */
    @Ignore// ※実際にメール送信されるので、テスト時のみコメントアウトして有効にすること。
    @Test
    public void testSend() throws Exception {
        System.out.println("testSend");

        MailProperty prop = new MailProperty();
        prop.setHost("smtp.adtek-fuji.co.jp");
        prop.setPort(587);
        prop.setConnectionTimeout(10000);
        prop.setTimeout(10000);
//        prop.setCharset("UTF-8");

        String from = "support@adtek-fuji.co.jp";
        String to = "nar-nakamura@adtek-fuji.co.jp";
//        String to = "nar-nakamura@adtek-fuji.co.jp, ki-nakamura@adtek-fuji.co.jp";

        String subject = "テストﾒｰﾙの件名(subject)";
        String content = "テストﾒｰﾙの本文(content)\r\n①Ⅱ";

        MailUtils mail = new MailUtils(prop);
        MailResultEnum ret;
        ret = mail.send(from, to, subject, content);
        assertThat(ret, is(MailResultEnum.SUCCESS));

//        // 無効な宛先を含む送信
//        to = "nar-nakamura@adtek-fuji.co.jp, zzz@adtek-fuji.co.j";
//
//        subject = "テストﾒｰﾙの件名2(subject)";
//        content = "テストﾒｰﾙの本文2(content)\r\n①Ⅱ";
//
//        ret = mail.send(from, to, subject, content);
//        assertThat(ret, is(MailResultEnum.MESSAGING_EXCEPTION));
    }

    /**
     * 
     * @throws Exception 
     */
    @Test
    public void testSendDummy() throws Exception {
        System.out.println("testSendDummy");

        MailProperty prop = new MailProperty();
        prop.setHost(dummySmtpHost);
        prop.setPort(dummySmtpPort);
        prop.setConnectionTimeout(30000);
        prop.setTimeout(30000);
//        prop.setCharset("UTF-8");

        String from = "from@mail.com";
        String to;

        List<String> tos = new ArrayList();
        tos.add("to1@mail.com");
        tos.add("to2@mail.com");

        StringBuilder sb = new StringBuilder();
        for(String s : tos) {
            sb.append(s);
            sb.append(",");
        }
        to = sb.substring(0, sb.length() - 1);

        String subject = "テストﾒｰﾙの件名(subject)";
        String content = "テストﾒｰﾙの本文(content)\r\n①Ⅱ";

        MailUtils mail = new MailUtils(prop);
        MailResultEnum ret;
        ret = mail.send(from, to, subject, content);
        assertThat(ret, is(MailResultEnum.SUCCESS));

        List<WiserMessage> wiserMessages = wiser.getMessages();
        for (WiserMessage wiserMessage : wiserMessages) {
            MimeMessage message = wiserMessage.getMimeMessage();
            assertThat(message.getSubject(), is(subject));
            assertThat(message.getContent(), is(content));
        }
    }
}
