/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.mail;

import jp.adtekfuji.adfactoryserver.service.mail.MailProperty;
import jp.adtekfuji.adfactoryserver.service.mail.MailUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

/**
 * メール処理のテスト
 *
 * @author nar-nakamura
 */
public class MailUtilsTest {

    private Wiser wiser;// ダミーのメールサーバー

    private static final String dummySmtpHost = "localhost";
    private static final int dummySmtpPort = 20;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

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
     * メール送信テスト
     *
     * @throws java.lang.Exception
     */
    @Ignore// ※実際にメール送信されるので、テスト時のみコメントアウトして有効にすること。
    @Test
    public void testSend() throws Exception {
        System.out.println("testSend");

        MailProperty prop = new MailProperty();
        prop.setHost("smtp.adtek-fuji.co.jp");
        prop.setPort(587);
        prop.setIsEnableAuth(true);
        prop.setUser("adfactory");
        prop.setPassword("");// パスワードを入れる
        prop.setIsEnableTLS(true);
        prop.setConnectionTimeout(10000);
        prop.setTimeout(10000);
//        prop.setCharset("UTF-8");

        String from = "adfactory@adtek-fuji.co.jp";
        String to = "adfactory@adtek-fuji.co.jp";

        String subject = "テストﾒｰﾙの件名(subject)";
        String content = "テストﾒｰﾙの本文(content)\r\n①Ⅱ";

        MailUtils mail = new MailUtils(prop);
        ServerErrorTypeEnum ret;
        ret = mail.send(from, to, subject, content, false);
        assertThat(ret, is(ServerErrorTypeEnum.SUCCESS));

//        // 無効な宛先を含む送信
//        to = "adfactory@adtek-fuji.co.jp, zzz@adtek-fuji.co.j";
//
//        subject = "テストﾒｰﾙの件名2(subject)";
//        content = "テストﾒｰﾙの本文2(content)\r\n①Ⅱ";
//
//        ret = mail.send(from, to, subject, content);
//        assertThat(ret, is(ServerErrorTypeEnum.MAIL_MESSAGING_EXCEPTION));
    }

    /**
     * 添付ファイル付きメール送信テスト
     *
     * @throws Exception 
     */
    @Ignore// ※実際にメール送信されるので、テスト時のみコメントアウトして有効にすること。
    @Test
    public void testSend2() throws Exception {
        System.out.println("testSend2");

        MailProperty prop = new MailProperty();
        prop.setHost("smtp.adtek-fuji.co.jp");
        prop.setPort(587);
        prop.setIsEnableAuth(true);
        prop.setUser("adfactory");
        prop.setPassword("");// パスワードを入れる
        prop.setIsEnableTLS(true);
        prop.setConnectionTimeout(10000);
        prop.setTimeout(10000);

        String from = "adfactory@adtek-fuji.co.jp";
        String to = "adfactory@adtek-fuji.co.jp";

        String subject = "テストﾒｰﾙの件名(subject)";
        String content = "テストﾒｰﾙの本文(content)\r\n①Ⅱ";

        String fileName1 = "file①.txt";
        String fileName2 = "file②.txt";
        File file1 = tempFolder.newFile(fileName1);
        File file2 = tempFolder.newFile(fileName2);

        List<File> files = new ArrayList();
        files.add(file1);
        files.add(file2);

        MailUtils mail = new MailUtils(prop);
        ServerErrorTypeEnum ret;
        ret = mail.send(from, to, subject, content, files, false);
        assertThat(ret, is(ServerErrorTypeEnum.SUCCESS));
    }
    
    /**
     * HTMLメール送信テスト
     *
     * @throws java.lang.Exception
     */
    @Ignore// ※実際にメール送信されるので、テスト時のみコメントアウトして有効にすること。
    @Test
    public void testSend3() throws Exception {
        System.out.println("testSend3");

        MailProperty prop = new MailProperty();
        prop.setHost("smtp.adtek-fuji.co.jp");
        prop.setPort(587);
        prop.setIsEnableAuth(true);
        prop.setUser("adfactory");
        prop.setPassword("");// パスワードを入れる
        prop.setIsEnableTLS(true);
        prop.setConnectionTimeout(10000);
        prop.setTimeout(10000);
        prop.setCharset("UTF-8");

        String from = "adfactory@adtek-fuji.co.jp";
        String to = "adfactory@adtek-fuji.co.jp";

        String subject = "テストﾒｰﾙの件名(subject)";
        String content = "<html><head></head><body><p>テストﾒｰﾙの本文(content)</p><p>①Ⅱ</p></body></html>";

        MailUtils mail = new MailUtils(prop);
        ServerErrorTypeEnum ret;
        ret = mail.send(from, to, subject, content, false);
        assertThat(ret, is(ServerErrorTypeEnum.SUCCESS));
    }
    
    /**
     * ダミーのメールサーバーでの、メール送信テスト
     *
     * @throws Exception 
     */
    @Test
    public void testSendDummy() throws Exception {
        System.out.println("testSendDummy");

        MailProperty prop = new MailProperty();
        prop.setHost(dummySmtpHost);
        prop.setPort(dummySmtpPort);
        prop.setIsEnableAuth(false);
        prop.setConnectionTimeout(30000);
        prop.setTimeout(30000);
//        prop.setCharset("UTF-8");

        String from = "smtp_testuser@mail.com";
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
        ServerErrorTypeEnum ret;
        ret = mail.send(from, to, subject, content, false);
        assertThat(ret, is(ServerErrorTypeEnum.SUCCESS));

        List<WiserMessage> wiserMessages = wiser.getMessages();
        for (WiserMessage wiserMessage : wiserMessages) {
            MimeMessage message = wiserMessage.getMimeMessage();
            assertThat(message.getSubject(), is(subject));
            Multipart mp = (Multipart) message.getContent();
            assertThat(mp.getBodyPart(0).getContent(), is(content));
        }
    }

    /**
     * ダミーのメールサーバーでの、添付ファイル付きメール送信テスト
     *
     * @throws Exception 
     */
    @Test
    public void testSendDummy2() throws Exception {
        System.out.println("testSendDummy2");

        MailProperty prop = new MailProperty();
        prop.setHost(dummySmtpHost);
        prop.setPort(dummySmtpPort);
        prop.setIsEnableAuth(false);
        prop.setConnectionTimeout(30000);
        prop.setTimeout(30000);
//        prop.setCharset("UTF-8");

        String from = "smtp_testuser@mail.com";
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

        String fileName1 = "file①.txt";
        String fileName2 = "file②.txt";
        File file1 = tempFolder.newFile(fileName1);
        File file2 = tempFolder.newFile(fileName2);

        List<File> files = new ArrayList();
        files.add(file1);
        files.add(file2);

        MailUtils mail = new MailUtils(prop);
        ServerErrorTypeEnum ret;
        ret = mail.send(from, to, subject, content, files, false);
        assertThat(ret, is(ServerErrorTypeEnum.SUCCESS));

        List<WiserMessage> wiserMessages = wiser.getMessages();
        for (WiserMessage wiserMessage : wiserMessages) {
            MimeMessage message = wiserMessage.getMimeMessage();
            assertThat(message.getSubject(), is(subject));

            Multipart mp = (Multipart) message.getContent();
            assertThat(mp.getBodyPart(0).getContent(), is(content));
            assertThat(MimeUtility.decodeText(mp.getBodyPart(1).getFileName()), is(file1.getName()));
            assertThat(MimeUtility.decodeText(mp.getBodyPart(2).getFileName()), is(file2.getName()));
        }
    }
    
    /**
     * ダミーのHTMLメールサーバーでの、メール送信テスト
     *
     * @throws Exception 
     */
    @Test
    public void testSendDummy3() throws Exception {
        System.out.println("testSendDummy3");

        MailProperty prop = new MailProperty();
        prop.setHost(dummySmtpHost);
        prop.setPort(dummySmtpPort);
        prop.setIsEnableAuth(false);
        prop.setConnectionTimeout(30000);
        prop.setTimeout(30000);
//        prop.setCharset("UTF-8");

        String from = "smtp_testuser@mail.com";
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
        String content = "<html><head></head><body><p>テストﾒｰﾙの本文(content)</p><p>①Ⅱ</p></body></html>";

        MailUtils mail = new MailUtils(prop);
        ServerErrorTypeEnum ret;
        ret = mail.send(from, to, subject, content, false);
        assertThat(ret, is(ServerErrorTypeEnum.SUCCESS));

        List<WiserMessage> wiserMessages = wiser.getMessages();
        for (WiserMessage wiserMessage : wiserMessages) {
            MimeMessage message = wiserMessage.getMimeMessage();
            assertThat(message.getSubject(), is(subject));
            Multipart mp = (Multipart) message.getContent();
            assertThat(mp.getBodyPart(0).getContent(), is(content));
        }
    }
}
