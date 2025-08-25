/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import java.util.Objects;

/**
 * 内部コマンド
 *
 * @author s-heya
 */
public class TCommand {

    private final Channel channel;
    private final ObjectNode node;

    /**
     * コンストラクタ
     *
     * @param channel
     * @param node
     */
    public TCommand(Channel channel, ObjectNode node) {
        this.channel = channel;
        this.node = node;
    }

    /**
     * 要求元のChannelHandlerインスタンスを取得する。
     *
     * @return
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * ノードを取得する。
     *
     * @return
     */
    public ObjectNode getNode() {
        return this.node;
    }

    /**
     * 応答コマンドを生成する
     *
     * @param node
     * @param errCode
     * @param text
     * @return
     */
    public static String createResponse(ObjectNode node, int errCode, String text) {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"NO\":").append(node.get("NO").toString());

        JsonNode jn = node.get("SETSUBI");
        if (Objects.nonNull(jn)) {
            sb.append(",\"SETSUBI\":\"").append(jn.asText()).append("\"");
        }

        sb.append(",\"ERROR\":").append(String.valueOf(errCode));
        sb.append(",\"DETAIL\":");
        switch (errCode) {
            case 0:
                sb.append("\"\"");
                break; //エラーなし
            case -1:
                sb.append("\"フォーマットエラー\"");
                break;
            case -2:
                sb.append("\"未ログイン\"");
                break;
            case -3:
                sb.append("\"設備無し\"");
                break;
            case -4:
                sb.append("\"カンバン無し\"");
                break;
            case -5:
                sb.append("\"カンバン未計画\"");
                break;
            case -6:
                sb.append("\"パラメータ未定義\"");
                break;
            case -10:
                sb.append("\"カンバン作業中\"");
                break;
            case -11:
                sb.append("\"工程無し\"");
                break;
            case -12:
                sb.append("\"未着工\"");
                break;
            case -13:
                sb.append("\"完了済み\"");
                break;
            case -14:
                sb.append("\"計画日時変更失敗\"");
                break;
            case -15:
                sb.append("\"ステータス移行失敗\"");
                break;
            case -17:
                sb.append("\"作業完了\"");
                break;
            default:
                sb.append("\"").append(text).append("\"");
                break;
        }

        sb.append("}");
        return sb.toString();
    }
}
