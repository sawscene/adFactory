/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Address;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.utility.JsonUtils;
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
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;

/**
 * メール処理
 *
 * @author shizuka.hirano
 */
public class MailSender {

    private final static String BASE_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "conf";
    private final static String DEVICE_SETTING_FILE_PATH = BASE_PATH +  File.separator + "DeviceConnectionProperty.json";
    private final static String PROPERTY_NAME = "Mail";

    /**
     * ログ出力用クラス
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * メール設定情報
     */
    private final MailProperty mailProperty;

    /**
     *  宛先の区切り文字 (","または";")
     */
    private static final String MAIL_ADDRESS_SEP = ",|;";

    /**
     * コンストラクタ
     */
    public MailSender() {
        this.mailProperty = new MailProperty();
    }

    /**
     * 設定ファイルから情報を取得
     *
     * @return 設定情報
     */
    private static Optional<Map<String, String>> loadConfigFile(String confType) {

        if (Files.notExists(Paths.get(DEVICE_SETTING_FILE_PATH))) {
            logger.fatal("Error not found Config File");
            return Optional.empty();
        }

        try (Stream<String> item = Files.lines(Paths.get(DEVICE_SETTING_FILE_PATH), StandardCharsets.UTF_8)) {
            final String jsonStr = item.collect(Collectors.joining(System.getProperty("line.separator")));
            List<Map<String, String>> confList = JsonUtils.jsonToMaps((jsonStr));
            if (Objects.isNull(confList) || confList.isEmpty()) {
                return Optional.empty();
            }

            return confList
                    .stream()
                    .filter(conf -> conf.containsKey("type"))
                    .filter(conf -> StringUtils.equals(confType, conf.get("type")))
                    .findFirst();
        } catch (Exception e) {
            logger.fatal(e, e);
            return Optional.empty();
        }
    }


    static Optional<MailSender> initialize()
    {
        try {
            // メール設定
            Optional<Map<String, String>> optMailConfig = loadConfigFile("Mail");
            if (!optMailConfig.isPresent()) {
                return Optional.empty();
            }

            Map<String, String> mailConfig = optMailConfig.get();
            MailProperty mailProperty = new MailProperty();
            mailProperty.setHost(mailConfig.get("host"));
            mailProperty.setPort(Integer.valueOf(mailConfig.get("port")));
            mailProperty.setIsEnableAuth(Boolean.parseBoolean(mailConfig.getOrDefault("enable_auth", "false")));
            mailProperty.setUser(mailConfig.getOrDefault("user", ""));
            mailProperty.setPassword(mailConfig.getOrDefault("password", ""));
            mailProperty.setIsEnableTLS(Boolean.parseBoolean(mailConfig.getOrDefault("tsl", "false")));
            mailProperty.setConnectionTimeout(Integer.valueOf(mailConfig.getOrDefault("timeout", "30000")));
            mailProperty.setTimeout(Integer.valueOf(mailConfig.getOrDefault("timeout", "30000")));
            mailProperty.setCharset(mailConfig.getOrDefault("charset", "MS932"));
            mailProperty.setMailFrom(mailConfig.getOrDefault("from", "anonymous"));
            mailProperty.setMailTo(mailConfig.getOrDefault("to", "[]"));

            return Optional.of(new MailSender(mailProperty));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Optional.empty();
        }

    }

    /**
     * コンストラクタ
     * 
     * @param mailProperty メール設定情報
     */
    private MailSender(MailProperty mailProperty) {
        this.mailProperty = mailProperty;
    }

    private static MailSender instance = null;
    public static Optional<MailSender> getInstance() {
        if (Objects.isNull(instance)) {
            Optional<MailSender> obj = initialize();
            obj.ifPresent(item -> instance = item);
            return obj;
        }

        return Optional.of(instance);
    }

    /**
     * メールを送信する。
     *
     * @param from 送信者
     * @param toAddList 宛先
     * @param subject 件名
     * @param content 本文
     * @return 送信結果
     */
    public ServerErrorTypeEnum send(String subject, String content) {
        return send(null, subject, content, null);
    }


    /**
     * メールを送信する。
     *
     * @param from 送信者
     * @param toAddList 宛先
     * @param subject 件名
     * @param content 本文
     * @return 送信結果
     */
    public ServerErrorTypeEnum send(List<String> toAddList, String subject, String content) {
        return send(toAddList, subject, content, null);
    }

    /**
     * 添付ファイル付きのメールを送信する。
     *
     * @param from 送信者
     * @param toAddList 宛先
     * @param subject 件名
     * @param content 本文
     * @param files 添付ファイル
     * @return 送信結果
     */
    public ServerErrorTypeEnum send( List<String> toAddList, String subject, String content, List<File> files) {
        ServerErrorTypeEnum result = ServerErrorTypeEnum.SERVER_FETAL;
        try {
            Properties props = new Properties();

            // 送信元
            Address fromAddress = new InternetAddress(this.mailProperty.getMailFrom());
            // 宛先
            if (Objects.isNull(toAddList) || toAddList.isEmpty()) {
                String mailTo = this.mailProperty.getMailTo();
                if (StringUtils.isEmpty(mailTo)) {
                    logger.fatal("Mail Setting Error");
                    return ServerErrorTypeEnum.SERVER_FETAL;
                }

                toAddList = Arrays.asList(mailTo.split(MAIL_ADDRESS_SEP));
            }

            Address[] toAddress = new InternetAddress[toAddList.size()];
            int index = 0;
            for (String toAdd : toAddList) {
                toAddress[index] = new InternetAddress(toAdd);
                index++;
            }

                
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
                message.setSubject(subject);
                message.setText(content, this.mailProperty.getCharset());
                message.setHeader("Content-Type", "text/html; charset=" + this.mailProperty.getContentTransferEncoding());
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
