/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * メール処理
 *
 * @author nar-nakamura
 */
public class MailUtils {

    private final static Logger logger = LogManager.getLogger();

    private final MailProperty mailProperty;// メール設定情報

    private static final String MAIL_ADDRESS_SEP = ",|;";// 宛先の区切り文字 (","または";")

    /**
     *
     */
    public MailUtils() {
        this.mailProperty = new MailProperty();
    }

    /**
     *
     * @param mailProperty メール設定情報
     */
    public MailUtils(MailProperty mailProperty) {
        this.mailProperty = mailProperty;
    }

    /**
     * メールを送信する。
     *
     * @param from 送信者
     * @param to 宛先
     * @param subject 件名
     * @param content 本文
     * @return 送信結果
     */
    public MailResultEnum send(String from, String to, String subject, String content) {
        MailResultEnum result = MailResultEnum.FAILED;
        try {
            Properties props = new Properties();

            // 送信元
            Address fromAddress = new InternetAddress(from);
            // 宛先
            List<Address> toAddrList = new ArrayList();
            for (String sepTo : to.split(MAIL_ADDRESS_SEP)) {
                String toAddr = sepTo.trim();
                if (toAddr.isEmpty()) {
                    continue;
                }
                toAddrList.add(new InternetAddress(toAddr));
            }
            Address[] toAddress = toAddrList.toArray(new Address[]{});

            // 接続設定
            props.put("mail.smtp.host", this.mailProperty.getHost());
            props.put("mail.smtp.port", this.mailProperty.getPort());

            // タイムアウト設定
            props.put("mail.smtp.connectiontimeout", this.mailProperty.getConnectionTimeout().toString());
            props.put("mail.smtp.timeout", this.mailProperty.getTimeout());

            final String user = this.mailProperty.getUser();
            final String password = this.mailProperty.getPassword();

            props.setProperty("mail.smtp.starttls.enable", this.mailProperty.getStarttlsEnable().toString());

            // 
            Session session;
            if (user.isEmpty()) {
                // 認証なし
                session = Session.getInstance(props);
            } else {
                // 認証あり
                props.setProperty("mail.smtp.auth", "true");
                session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
            }

            try {
                // メッセージ設定
                MimeMessage message = new MimeMessage(session);
                message.setFrom(fromAddress);
                message.setRecipients(Message.RecipientType.TO, toAddress);
                message.setSubject(subject, this.mailProperty.getCharset());
                message.setText(content, this.mailProperty.getCharset());
                message.setHeader("Content-Transfer-Encoding", this.mailProperty.getContentTransferEncoding());
                message.setSentDate(new Date());

                // メール送信
                Transport.send(message);

                result = MailResultEnum.SUCCESS;

            } catch (AuthenticationFailedException ex) {
                // 認証失敗
                result = MailResultEnum.AUTHENTICATION_FAILED;
                logger.fatal(ex, ex);
            } catch (MessagingException ex) {
                // メッセージ異常
                result = MailResultEnum.MESSAGING_EXCEPTION;
                logger.fatal(ex, ex);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        // メール送信に異常が発生したときログにエラーメッセージを表示
        if (!MailResultEnum.SUCCESS.equals(result)) {
            logger.fatal("メール送信失敗 : {}", result);
            logger.fatal(content);
        }

        return result;
    }
}
