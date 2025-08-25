package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.SendConveyProgram;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.SendMoveHome;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.SendStop;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;

import java.io.IOException;
import java.util.function.Consumer;

class ActionSendConveyorCommand implements ActionCommand {

    final String reason;

    ActionSendConveyorCommand(String reason) {
        this.reason = reason;
    }

    long programNumber = 10; // プログラム番号 0～
    final long charging = 10; //充電
    final long payoutAndReturn = 11; // 払出 -> 返却
    final long payout = 12; // 払出
    /**
     * 送信コマンド実行
     */
    @Override
    public void executeCommand() {
    }

    /**
     * 反映 & 削除するか?
     * @param receiveData 受信データ
     * @param consumer 送信用コンシューマ
     * @return 削除するか?
     */
    @Override
    public boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) throws IOException {
        if (StringUtils.equals("AMRホーム", this.reason)) {
            // ホーム移動
            SendMoveHome sendMoveHome = new SendMoveHome();
            connector.send(sendMoveHome.crateSendData());
            consumer.accept(new WaitProgramChangeProcessCommand(null));
            return true;
        } else if (StringUtils.equals("AMR停止", this.reason)) {
            // プログラム停止
            SendStop sendStop = new SendStop();
            connector.send(sendStop.crateSendData());
            consumer.accept(new WaitProgramStopProcessCommand());
            return true;
        }

        if (StringUtils.equals("AMR充電", this.reason)) {
            programNumber = charging;
        } else if (StringUtils.equals("AMR払出返却", this.reason)){
            programNumber = payoutAndReturn;
        } else if (StringUtils.equals("AMR払出", this.reason)){
            programNumber = payout;
        }

        // AMRへプログラム実行を送信
        SendConveyProgram sendConveyProgram = new SendConveyProgram(0, programNumber);
        connector.send(sendConveyProgram.crateSendData());

        // プログラム実行待ち
        consumer.accept(new WaitProgramChangeProcessCommand(programNumber));
        return true;
    }
}
