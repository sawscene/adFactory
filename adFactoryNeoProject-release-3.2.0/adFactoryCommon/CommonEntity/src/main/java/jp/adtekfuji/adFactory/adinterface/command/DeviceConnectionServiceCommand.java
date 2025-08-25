package jp.adtekfuji.adFactory.adinterface.command;


import java.io.Serializable;

public class DeviceConnectionServiceCommand implements Serializable {
    public enum COMMAND {
        START_SERVICE,
        START_SERVER,
        UPDATE_ORGANIZATION,
        UPDATE_EQUIPMENT
    };

    private static final long serialVersionUID = 1L;

    private COMMAND command;

    public DeviceConnectionServiceCommand() {}

    public DeviceConnectionServiceCommand(COMMAND command) {
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

}
