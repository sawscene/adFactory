package jp.adtekfuji.adFactory.enumerate;

public enum WorkflowDateCheckErrorTypeEnum {
    TagDuplicate("key.tagDuplicate", "key.tagDuplicateDetail"), // 品質トレサのタグが重複している
    TagServerError("key.ServerConnectFail", "key.ServerConnectFailDetail"); // サーバーの接続エラー

    private String type; // エラータイプ
    private String message; // メッセージ

    WorkflowDateCheckErrorTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }
}
