package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker;

/**
 * 接続状態
 */
public interface IConnectStatus {

    /**
     * 接続状態更新
     * @return 次の状態
     */
    IConnectStatus updateConnectState();

    /**
     * ログイン
     * @return 次の状態
     */
    IConnectStatus login();

    /**
     * ログアウト
     * @return 次の状態
     */
    IConnectStatus logout();

    /**
     * 処理実施
     * @param command コマンド
     */
    void doStatusCommand(IWorkStatusCommand command);
    
    /**
     * 作業状態を取得（状態確認用）
     * @return 作業状態
     */
    String getWorkStatus();
    
}
