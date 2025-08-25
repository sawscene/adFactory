/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;
 
/**
 * トリプレットクラス
 * 
 * @author s-heya
 * @param <U> 最初のフィールド
 * @param <V> 2番目のフィールド
 * @param <T> 3番目のフィールド
 */
public class Triplet<U, V, T>
{
    public final U first;
    public final V second;
    public final T third;
 
    /**
     * コンストラクタ
     * 
     * @param first 最初のフィールド
     * @param second 2番目のフィールド
     * @param third 3番目のフィールド
     */
    private Triplet(U first, V second, T third)
    {
        this.first = first;
        this.second = second;
        this.third = third;
    }
 
    /**
     * オブジェクトが等しいかどうかを返す。
     * 
     * @param o オブジェクト
     * @return true: 等しい、false: 異なる
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
 
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
 
        Triplet triplet = (Triplet) o;
 
        if (!first.equals(triplet.first) ||
                !second.equals(triplet.second) ||
                !third.equals(triplet.third)) {
            return false;
        }
 
        return true;
    }
 
    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        result = 31 * result + third.hashCode();
        return result;
    }
 
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }
 
    // トリプレットの型付き不変インスタンスを作成するためのファクトリメソッド
    public static <U, V, T> Triplet <U, V, T> of(U a, V b, T c) {
        return new Triplet <>(a, b, c);
    }
}
