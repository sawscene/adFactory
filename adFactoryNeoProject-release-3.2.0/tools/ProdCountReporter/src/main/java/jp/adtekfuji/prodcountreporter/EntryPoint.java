package jp.adtekfuji.prodcountreporter;

/**
 * エントリーポイント
 * 
 * Java 9以降では、Applicationを継承したクラスのmainから実行できないためここから実行する
 * 「エラー: JavaFXランタイム・コンポーネントが不足しており、このアプリケーションの実行に必要です」
 * 参考：https://torutk.hatenablog.jp/entry/2018/12/01/215113
 * 
 * @author s-heya
 */
public class EntryPoint {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");

        MainApp.main(args);
    }
}
