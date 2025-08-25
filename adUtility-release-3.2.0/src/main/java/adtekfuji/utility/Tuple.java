/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

/**
 * タプルクラス
 *
 * @param <L>
 * @param <R>
 * @author s-heya
 */
public class Tuple<L, R> {

    private final L left;
    private final R right;

    public Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }
    public R getRight() { return right; }

    /**
     * ハッシュコードを返す。
     *
     * @return
     */
    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    /**
     * オブジェクトが等しいかどうかを返す。
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple)) {
            return false;
        }

        Tuple tuple = (Tuple) o;
        return this.left.equals(tuple.getLeft()) &&  this.right.equals(tuple.getRight());
    }

    /**
     * 文字列を返す。
     *
     * @return
     */
    @Override
    public String toString() {
        return this.left.toString() + "=" + this.right.toString();
    }

}
