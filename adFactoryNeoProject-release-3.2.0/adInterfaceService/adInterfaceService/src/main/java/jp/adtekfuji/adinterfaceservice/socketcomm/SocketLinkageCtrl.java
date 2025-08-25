/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.utility.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.websocket.Session;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PlanChangeCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservice.AdInterfaceConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 外部制御ソケットコントローラー
 *
 * @author ka.Makihara 2019/10/08
 */
public class SocketLinkageCtrl {

    private static final Logger logger = LogManager.getLogger();
    private static SocketCommCtrl contrl = null;
    private static SocketCommCtrl client = null;
    private static SocketLinkageCtrl instance = null;
    static List<LinkageData> ctrls = new ArrayList<>();
    static List<LinkageData> clients = new ArrayList<>();
    private final LinkedList<String> clientMsg = new LinkedList<>();
    private final List<AdInterfaceServiceInterface> plugins;

    /**
     * コンストラクタ
     *
     * @param plugin
     */
    private SocketLinkageCtrl(List<AdInterfaceServiceInterface> plugins) {
        AdInterfaceConfig config = AdInterfaceConfig.getInstance();
        contrl = new SocketCommCtrl(config.getCtrlPort(), new SocketCmdCtrlHandler());     //上位システムとのSocket
        client = new SocketCommCtrl(config.getTermPort(), new SocketCmdClientHandler());   //adProductとのソケット

        this.plugins = plugins;
    }

    /**
     * 外部制御ソケットコントローラーを生成する。
     *
     * @param plugins
     */
    static public void createInstance(List<AdInterfaceServiceInterface> plugins) {
        if (Objects.isNull(instance)) {
            instance = new SocketLinkageCtrl(plugins);
        }
    }

    /**
     *
     * @return
     */
    static public SocketLinkageCtrl getInstance() {
        return instance;
    }

    /**
     *
     */
    public void startService() {
        logger.info("SocketCommandCtrl start...");
        contrl.startService();
        client.startService();
    }

    /**
     *
     */
    public void stopService() {
        logger.info("SocketCommandCtrl stoped...");
        contrl.stopService();
        client.stopService();
    }

    /**
     * 上位コントローラーが接続された
     *
     * @param ctx
     * @throws java.io.IOException
     */
    public void addCtrl(ChannelHandlerContext ctx) throws IOException {
        ctrls.add(new LinkageData(ctx));
    }

    /**
     * クライアント(adProduct)が接続された ※SocketCmdClientHandler.channelActive() から呼ばれる
     *
     * @param ctx
     */
    public void addClient(ChannelHandlerContext ctx) {
        clients.add(new LinkageData(ctx));
    }

    /**
     * WebSocket で Client(adProduct)が接続された
     *
     * @param session
     */
    public void addClientWebSocket(Session session) {
        clients.add(new LinkageData(session));
    }

    /**
     * WebSocket で コントローラー(上位層)が接続された
     *
     * @param session
     */
    public void addCtrlWebSocket(Session session) {
        ctrls.add(new LinkageData(session));
    }

    /**
     *
     * @param channels
     * @param ctx
     */
    private void deleteChannel(List<LinkageData> channels, ChannelHandlerContext ctx) {
        for (LinkageData ch : channels) {
            ChannelHandlerContext cx = ch.getChannel();
            if (Objects.nonNull(cx) && cx.equals(ctx)) {
                channels.remove(ch);
                break;
            }
        }
    }

    /**
     *
     * @param channels
     * @param session
     */
    public void deleteChannel(List<LinkageData> channels, Session session) {
        for (LinkageData ch : channels) {
            Session cx = ch.getSession();
            if (Objects.nonNull(cx) && cx.equals(session)) {
                channels.remove(ch);
                break;
            }
        }
    }

    /**
     * 上位コントローラーが切断された(Socket)
     *
     * @param ctx
     */
    public void deleteSocketCtrl(ChannelHandlerContext ctx) {
        deleteChannel(ctrls, ctx);
    }

    /**
     * 上位コントローラーが切断された(WebSocket)
     *
     * @param session
     */
    public void deleteSocketCtrl(Session session) {
        deleteChannel(ctrls, session);
    }

    /**
     * クライアント(adProduct)が切断された(Socket)
     *
     * @param ctx
     */
    public void deleteSocketClient(ChannelHandlerContext ctx) {
        deleteChannel(clients, ctx);
    }

    /**
     * クライアント(adProduct)が切断された(WebSocket)
     *
     * @param session
     */
    public void deleteSocketClient(Session session) {
        deleteChannel(clients, session);
    }

    /**
     * リターンメッセージを生成する
     *
     * @param node
     * @param errCode
     * @param sb
     * @return
     */
    private boolean createReturnMessage(ObjectNode node, int errCode, StringBuilder sb, String text) {

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
        return (errCode == 0);
    }

    /**
     * カンバン・工程の状態を確認(有無等)
     *
     * @param node
     * @param params
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    private int checkKanban(ObjectNode node, List<String> params) throws UnsupportedEncodingException {
        int retCode;

        String kanbanName = node.get("KANBAN").asText();
        String cmdStr = node.get("CMD").asText();
        String setsubi = node.get("SETSUBI").asText();

        //カンバンの有無を確認
        KanbanInfoFacade kf = new KanbanInfoFacade();
        EquipmentInfoFacade ef = new EquipmentInfoFacade();

        //工程順が異なると同名のカンバンが作成できるので、
        //  findName() の場合、どのカンバンが取得できるのか・・・
        //  本来なら、List<KanbanInfoEntity> が正しいと思われる
        KanbanInfoEntity kanbanInfoEntity = kf.findName(URLEncoder.encode(kanbanName, "UTF-8"));

        if (Objects.isNull(kanbanInfoEntity)) {
            return -4;
        }

        if (Objects.isNull(kanbanInfoEntity.getKanbanId())) {
            //カンバンIDが無い == カンバンが無い
            return -4;
        }
        if (cmdStr.equals("kanban") || cmdStr.equals("select")) {
            //"kanban"コマンドでは "KOUTEI"確認は不要
            //"select"コマンドでは "KOUTEI"確認は不要
            return 0;
        }

        JsonNode jn = node.get("KOUTEI");
        if (Objects.isNull(jn)) {
            //"KOUTEI" がない
            return -1;
        }
        String workflow = kanbanInfoEntity.getWorkflowName();
        String workName = node.get("KOUTEI").asText();
        node.put("WORKFLOW", workflow);

        //カンバン内で有効な工程の一覧(並列なら複数)で、指定された工程名を持つものを抽出
        List<WorkKanbanInfoEntity> kanbanInfo = kanbanInfoEntity.getWorkKanbanCollection();
        List<WorkKanbanInfoEntity> work = kanbanInfo.stream()
                .filter(kk -> kk.getImplementFlag() == true) //有効(着工)可能
                .filter(kk -> kk.getWorkName().equals(workName)) //工程名が指定されたものと同じ
                .collect(Collectors.toList());
        if (work.isEmpty()) {
            //カンバン内に指定された工程が無い
            return -11;
        }

        //指定された工程以外の工程で作業中のものを抽出
        List<WorkKanbanInfoEntity> current = kanbanInfo.stream()
                .filter(kk -> kk.getWorkStatus() == KanbanStatusEnum.WORKING)
                .filter(kk -> !kk.getWorkName().equals(workName)) //工程名が指定されたもの以外で
                .collect(Collectors.toList());
        if (!current.isEmpty()) {
            //指定された工程以外で"作業中"の工程がある
            List<Long> ids = current.get(0).getEquipmentCollection();
            if (ids.isEmpty()) {
                return -3;
            }
            for (Long id : ids) {
                EquipmentInfoEntity ee = ef.get(id);
                if (ee.getEquipmentName().equals(setsubi)) {
                    //同じ設備で他工程を作業中の場合
                    //  ※ただし、工程に割り当てられた設備がグループ名の場合はとりあえずOKとする
                    //    ( "設備(グループ)で"製造"={product1,priduct2,priduct3,...} で設備に「製造」が割り当ててあり
                    //      SETSUBIの指定で product1 とある場合など、"製造"設備の(グループ内)だが、
                    //      getEquipmentName() で得られるのは "設備" であり、"設備" != "product1" となるのでここではエラーとならない
                    return -10;
                }
            }
        }

        //カンバンのステータスをチェック
        KanbanStatusEnum ee = kanbanInfoEntity.getKanbanStatus();
        switch (ee) {
            case PLANNED:
                retCode = 0;
                break;  //カンバンはOK
            case SUSPEND:
                if (params.get(0).equals("done")) {
                    //中断中は「完了」できない
                    return -12;
                } else {
                    retCode = 1;
                }
                break;
            case INTERRUPT:                 //中止
            case COMPLETION:                 //カンバンは既に完了ずみ
            case PLANNING:
                if (cmdStr.equals("kanban")) {
                    //CMD=="kanabn" の場合(カンバン操作)は「計画中」でもOK
                    return 0;
                }
                return -5;      //カンバンが計画済みではない
            case WORKING:
                //カンバンは作業中
                KanbanStatusEnum workStatus = work.get(0).getWorkStatus();    //指示された工程のステータス
                if (params.get(0).equals("done")) {
                    //"done"で完了させる場合
                    switch (workStatus) {
                        case WORKING:
                            retCode = 0;
                            break;  //作業中
                        case PLANNED:
                            return -12;            //開始していないので完了できない
                        case PLANNING:
                            return -5;             //計画中の工程は完了できない
                        case COMPLETION:
                            return -13;            //完了した工程は完了できない
                        default:
                            return -10;            //
                    }
                } else {
                    // start させる

                    //工程が計画済み(並列工程の場合などで1つが完了(COMPLETION)でも他の工程はPLANNEDの場合がある
                    //カンバンは作業中でもすべての工程が完了していないので、工程を開始させることはOK
                    switch (workStatus) {
                        case PLANNED:
                            retCode = 0;
                            break;  //開始可能
                        case SUSPEND:
                            retCode = 0;
                            break;  //(中断中でも)開始可能
                        case INTERRUPT:
                        case COMPLETION:
                            return -13;            //完了した工程は開始できない
                        default:
                            return -10;            //作業中
                    }
                }
                break;
            default:
                return -99;                  //その他エラー
        }
        return retCode;
    }

    /**
     * JSON文字列から []リストを文字列リストとして取り出す(数値も文字とする) { "params":["aa","mm",1,5,10]} ->
     * List<String> [aa,mm,1,5,10]
     *
     * @param node
     * @param nodeName
     * @return
     */
    private List<String> getParams(JsonNode node, String nodeName) {
        List<String> params = new ArrayList<>();

        JsonNode jsonNode = node.get(nodeName);
        if (Objects.isNull(jsonNode)) {
            return params;
        }

        Iterator<JsonNode> it = jsonNode.elements();
        it.forEachRemaining(o -> {
            // ダブルクォーテーションを取り除く
            params.add(o.toString().replace("\"", ""));
        });

        return params;
    }

    /**
     * コマンドを検証する。
     *
     * @param node
     * @param sb
     * @return
     * @throws IOException
     */
    private boolean checkCommand(ObjectNode node, StringBuilder sb) throws IOException {
        JsonNode no = node.get("NO");
        if (Objects.isNull(no)) {
            // NOがない
            return createReturnMessage(node, -1, sb, "");
        }

        JsonNode cmd = node.get("CMD");
        if (Objects.isNull(cmd)) {
            // CMDがない
            return createReturnMessage(node, -1, sb, "");
        }
        String cmdStr = cmd.asText();

        // PARAM
        List<String> params = this.getParams(node, "PARAM");

        // 設備名
        JsonNode setsubi = node.get("SETSUBI");

        switch (cmdStr) {
            case "kanban":
                break;

            case "work":
                if (params.isEmpty()) {
                    // PARAMがない
                    return this.createReturnMessage(node, -1, sb, "");
                }

            case "select":
                if (Objects.isNull(setsubi)) {
                    // SETSUBIがない
                    return this.createReturnMessage(node, -1, sb, "");
                }

                JsonNode kanbanNode = node.get("KANBAN");
                if (Objects.nonNull(kanbanNode)) {
                    // カンバン・工程の有無を確認
                    int code = this.checkKanban(node, params);
                    if (code < 0) {
                        return this.createReturnMessage(node, code, sb, "");
                    } else {
                        if (code == 1) {
                            // 中断なので再開させる
                        }
                    }
                }
                break;

            case "organization":
                if (params.isEmpty()) {
                    // PARAMがない
                    return this.createReturnMessage(node, -1, sb, "");
                }
                break;

            default:
                return this.createReturnMessage(node, -1, sb, "");
        }

        return true;
    }

    /**
     * カンバン操作コマンド処理
     *
     * @param node
     * @param sb
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean command_kanban(ObjectNode node, StringBuilder sb) throws UnsupportedEncodingException, Exception {
        boolean retCode = true; //処理可能なコマンドなら true 
        List<String> params = getParams(node, "PARAM");

        JsonNode kanbanNode = node.get("KANBAN");
        if (Objects.isNull(kanbanNode)) {
            createReturnMessage(node, -1, sb, "");
            return true;
        }
        if (params.isEmpty()) {
            createReturnMessage(node, -6, sb, "");
            return true;
        }

        KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //calendar.add(Calendar.MINUTE, 5);
        // 現在の時間でカンバンを計画済みに設定するので、カンバンの最初の工程を「開始」
        // した時は必ず、遅れて「開始」扱いとなってしまう。
        // これを回避しようとするなら、現在の時間 +α(例えば5分)で「計画済み」とする

        PlanChangeCondition condition = new PlanChangeCondition(calendar.getTime(), DateUtils.min(), DateUtils.min());

        String kanbanName = node.get("KANBAN").asText();
        KanbanInfoEntity kanbanInfoEntity = kanbanInfoFacade.findName(URLEncoder.encode(kanbanName, "UTF-8"));

        if (Objects.isNull(kanbanInfoEntity.getKanbanId())) {
            // カンバン無し
            createReturnMessage(node, -4, sb, "");
            return true;
        }

        List<Long> kanbanIds = new ArrayList<>();
        kanbanIds.add(kanbanInfoEntity.getKanbanId());
        ResponseEntity ret;

        switch (params.get(0)) {
            case "planned":
                if (kanbanInfoEntity.getKanbanStatus() == KanbanStatusEnum.PLANNED) {
                    //すでに PLANNED(計画済み)なら無視する
                    createReturnMessage(node, 0, sb, "");
                    break;
                }
                ResponseEntity entity = kanbanInfoFacade.planChange(condition, kanbanIds, null); //計画時間の変更
                if (!entity.isSuccess()) {
                    //計画時間変更の失敗
                    //計画中でないとPLANNEDにできないので
                    createReturnMessage(node, -14, sb, "");
                }
                //時間を変更した時に entity を取得しなおす
                kanbanInfoEntity = kanbanInfoFacade.findName(URLEncoder.encode(kanbanName, "UTF-8"));

                //「計画済み」
                kanbanInfoEntity.setKanbanStatus(KanbanStatusEnum.PLANNED);

                try {
                    entity = kanbanInfoFacade.update(kanbanInfoEntity);
                    if (!entity.isSuccess()) {
                        //「計画済み」へ移行の失敗
                        createReturnMessage(node, -15, sb, "");
                    } else {
                        createReturnMessage(node, 0, sb, "");
                    }
                } catch (Exception ex) {
                    createReturnMessage(node, -99, sb, ex.toString());
                }
                break;

            case "complete":
                // ネットワークの接続確認

                // カンバンを強制的に完了にする
                ret = kanbanInfoFacade.updateStatus(Arrays.asList(kanbanInfoEntity.getKanbanId()), KanbanStatusEnum.COMPLETION, true, null);
                if (ret.getErrorType() == ServerErrorTypeEnum.SUCCESS) {
                    // すべてのadProductにキャンセルコマンドを送信
                    SocketCmdClientHandler handler = (SocketCmdClientHandler) client.getHandler();
                    handler.sendAll(new TCommand(null, node));
                    createReturnMessage(node, 0, sb, "");
                } else {
                    createReturnMessage(node, -99, sb, ret.getErrorType().toString());
                }
                break;

            case "cancel":
                // カンバンを計画中に戻す
                String workName = node.get("KOUTEI").asText();
                if (StringUtils.isEmpty(workName)) {
                    // パラメータ未定義
                    createReturnMessage(node, -6, sb, "");
                    return true;
                }

                Optional<WorkKanbanInfoEntity> opt = kanbanInfoEntity.getWorkKanbanCollection().stream().filter(o -> StringUtils.equals(o.getWorkName(), workName)).findFirst();
                if (!opt.isPresent()) {
                    // 追加工程から探す
                    opt = kanbanInfoEntity.getSeparateworkKanbanCollection().stream().filter(o -> StringUtils.equals(o.getWorkName(), workName)).findFirst();
                    if (!opt.isPresent()) {
                        // 指定した工程が見つからない
                        createReturnMessage(node, -11, sb, "");
                        return true;
                    }
                }

                WorkKanbanInfoEntity workKanban = opt.get();
                if (workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION) {
                    // 指定した工程が完了しているため、キャンセル不可
                    createReturnMessage(node, -17, sb, "");
                    return true;
                }

                // カンバンを強制的に計画中にする
                ret = kanbanInfoFacade.updateStatus(Arrays.asList(kanbanInfoEntity.getKanbanId()), KanbanStatusEnum.PLANNING, true, null);
                if (ret.getErrorType() == ServerErrorTypeEnum.SUCCESS) {
                    // すべてのadProductにキャンセルコマンドを送信
                    SocketCmdClientHandler handler = (SocketCmdClientHandler) client.getHandler();
                    handler.sendAll(new TCommand(null, node));
                    createReturnMessage(node, 0, sb, "");
                } else {
                    createReturnMessage(node, -99, sb, ret.getErrorType().toString());
                }
                retCode = true;
                break;

            default:
                createReturnMessage(node, -6, sb, "");
                retCode = false;
                break;
        }
        return retCode;
    }

    /**
     * 組織マスタ操作コマンド処理
     *
     * @param node
     * @param sb
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    private boolean command_organization(ObjectNode node, StringBuilder sb) throws UnsupportedEncodingException, Exception {
        boolean ret = true;

        // PARAM の検証は checkCommand()で行っている
        List<String> params = this.getParams(node, "PARAM");

        switch (params.get(0).toLowerCase()) {
            case "update":
                // 組織マスタの更新
                this.plugins.stream().forEach(plugin -> {
                    plugin.notice(node);
                });
                this.createReturnMessage(node, 0, sb, "");
                break;

            default:
                // パラメータ未定義
                this.createReturnMessage(node, -6, sb, "");
                ret = false;
                break;
        }

        return ret;
    }

    /**
     * adProduct へ情報を送信すること無しに実行可能なコマンド
     *
     * @param node
     * @param sb
     * @return
     */
    private boolean execBuiltinCommand(ObjectNode node, StringBuilder sb) throws Exception {
        boolean retCode;

        String cmdStr = node.get("CMD").asText().toLowerCase();

        try {
            switch (cmdStr) {
                case "kanban":
                    retCode = command_kanban(node, sb);
                    break;
                case "organization":
                    retCode = command_organization(node, sb);
                    break;
                default:
                    retCode = false;
            }
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
        return retCode;
    }

    /**
     * 上位からのコマンド(JSON文字列)を処理して応答を返す
     *
     * @param channel 外部システムとの接続チャンネル
     * @param recvMsg
     * @param node
     * @return
     * @throws IOException
     */
    public String execCommand(Channel channel, String recvMsg, ObjectNode node) throws IOException, Exception {
        logger.info("RECV: " + recvMsg);

        StringBuilder sb = new StringBuilder();

        if (checkCommand(node, sb)) {
            if (execBuiltinCommand(node, sb)) {
                // adInterfaceで処理
                return sb.toString();

            } else {
                String termName = node.get("SETSUBI").asText();
                SocketCmdClientHandler handler = (SocketCmdClientHandler) client.getHandler();

                if (handler.send(termName, new TCommand(channel, node))) {
                    // コマンドの送信に成功した場合はnullを返す
                    return null;
                }

                this.createReturnMessage(node, -3, sb, "");
                return sb.toString();
            }

        } else {
            logger.info("checkCommand FALSE:" + sb.toString());
            return sb.toString();
        }
    }

    /**
     * メッセージを送信する。
     *
     * @param channel
     * @param cmdStr
     * @throws IOException
     */
    private void sendMsg(LinkageData channel, String msg) throws IOException {
        logger.info("sendMsg::" + msg);
        channel.sendText(msg);
    }
}
