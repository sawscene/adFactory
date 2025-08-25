package jp.adtekfuji.adFactory.adinterface.command;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SummaryReportNoticeCommand implements Serializable {
    public enum COMMAND {
        LOAD_CONFIG, // 設定読込
        SEND_MAIL,   // メール送信
        SEND_TIME_CHECK, // 送信時間の確認
        START_SERVER   // サービス起動
    };

    public static class SendMailConfig {
        @JsonProperty("sendIndex")
        public int sendIndex;

        SendMailConfig(){}

        public SendMailConfig(int sendIndex)
        {
            this.sendIndex = sendIndex;
        }
    }

    private static final long serialVersionUID = 1L;

    private COMMAND command;
    private String config;

    public SummaryReportNoticeCommand() {}

    public SummaryReportNoticeCommand(COMMAND command) {
        this.command = command;
    }

    /**
     * コマンド取得
     * @return コマンド
     */
    public COMMAND getCommand() {
        return command;
    }

    /**
     * コマンド設定
     * @param command コマンド
     */
    public void setCommand(COMMAND command) {
        this.command = command;
    }


    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
