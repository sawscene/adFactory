/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * メール処理
 *
 * @author nar-nakamura
 */
public class MailUtils {

    /**
     * ログ出力クラス
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * メール設定情報
     */
    private final MailProperty mailProperty;

    /**
     * 宛先の区切り文字 (","または";")
     */
    private static final String MAIL_ADDRESS_SEP = ",|;";

    /**
     * コンストラクタ
     */
    public MailUtils() {
        this.mailProperty = new MailProperty();
    }

    /**
     * コンストラクタ
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
     * @param sendHtmlMail HTMLメール送信フラグ
     * @return 送信結果
     */
    public ServerErrorTypeEnum send(String from, String to, String subject, String content, Boolean sendHtmlMail) {
        return send(from, to, subject, content, null, sendHtmlMail);
    }

    /**
     * 添付ファイル付きのメールを送信する。
     *
     * @param from 送信者
     * @param to 宛先
     * @param subject 件名
     * @param content 本文
     * @param files 添付ファイル
     * @param sendHtmlMail HTMLメール送信フラグ
     * @return 送信結果
     */
    public ServerErrorTypeEnum send(String from, String to, String subject, String content, List<File> files, Boolean sendHtmlMail) {
        ServerErrorTypeEnum result = ServerErrorTypeEnum.SERVER_FETAL;
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
            props.put("mail.transport.protocol", "smtps");
            props.put("mail.smtp.host", this.mailProperty.getHost());
            props.put("mail.smtp.port", this.mailProperty.getPort());

            // タイムアウト設定
            props.put("mail.smtp.connectiontimeout", this.mailProperty.getConnectionTimeout().toString());
            props.put("mail.smtp.timeout", this.mailProperty.getTimeout());

            final String user = this.mailProperty.getUser();
            final String password = this.mailProperty.getPassword();

            // 
            Session session;
            if (!this.mailProperty.getIsEnableAuth()) {
                // 認証なし
                session = Session.getInstance(props);
            } else {
                // 認証あり
                props.put("mail.smtp.auth", "true");

                // TLS
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");

                props.put("mail.smtp.ssl.trust", this.mailProperty.getHost());

                session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
            }

            Multipart mp = new MimeMultipart();

            // 本文
            MimeBodyPart contentBody = new MimeBodyPart();
            contentBody.setText(content, this.mailProperty.getCharset());
            if (sendHtmlMail) {
                contentBody.setHeader("Content-Type", "text/html; charset=" + this.mailProperty.getContentTransferEncoding());
            } else {
                contentBody.setHeader("Content-Transfer-Encoding", this.mailProperty.getContentTransferEncoding());
            }
            mp.addBodyPart(contentBody);

            // 添付ファイル
            if (Objects.nonNull(files)) {
                for (File file : files) {
                    MimeBodyPart fileBody = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(file);
                    fileBody.setDataHandler(new DataHandler(fds));
                    fileBody.setFileName(MimeUtility.encodeWord(fds.getName()));

                    mp.addBodyPart(fileBody);
                }
            }

            try {
                // メッセージ設定
                MimeMessage message = new MimeMessage(session);
                message.setFrom(fromAddress);
                message.setRecipients(Message.RecipientType.TO, toAddress);
                message.setSubject(subject);
                message.setContent(mp);
                message.setSentDate(new Date());

                // メール送信
                Transport.send(message);

                result = ServerErrorTypeEnum.SUCCESS;

            } catch (AuthenticationFailedException ex) {
                // 認証失敗
                result = ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED;
                logger.fatal(ex, ex);
            } catch (MessagingException ex) {
                // メッセージ異常
                result = ServerErrorTypeEnum.MAIL_MESSAGING_EXCEPTION;
                logger.fatal(ex, ex);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }
}
