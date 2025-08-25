package adtekfuji.utility;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {
    /**
     * 2つの配列をtuple型へまとめる
     * @param s1 配列1
     * @param s2 配列2
     * @param size 配列サイズ
     * @return tuple結合する
     * @param <T> 1つ目の引数の型
     * @param <U> 2つ目の引数の型
     */
    public static <T,U> Stream<Tuple<T,U>> zip(Stream<T> s1, Stream<U> s2, int size) {
        TupleIterator<T,U,Tuple<T,U>> itr = new TupleIterator<>(s1.iterator(), s2.iterator(), Tuple::new);
        int characteristics = Spliterator.IMMUTABLE | Spliterator.NONNULL;
        if (size < 0) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(itr, characteristics), false);
        } else {
            return StreamSupport.stream(
                    Spliterators.spliterator(itr, size, characteristics), false);
        }
    }

    /**
     *  2つの配列をtuple型へまとめる
     * @param s1 配列1
     * @param s2 配列2
     * @return tuple結合する
     * @param <T> 1つ目の引数の型
     * @param <U> 2つ目の引数の型
     */
    public static <T,U> Stream<Tuple<T,U>> zip(Stream<T> s1, Stream<U> s2) {
        return zip(s1, s2, -1);
    }

    /** 2つのイテレータから、要素を合成するイテレータ。 */
    private static class TupleIterator<T, U, R> implements Iterator<R> {
        private final Iterator<T> i1;
        private final Iterator<U> i2;
        private final BiFunction<T,U,R> mapper;
        public TupleIterator(Iterator<T> i1, Iterator<U> i2, BiFunction<T,U,R> mapper) {
            this.i1 = i1;
            this.i2 = i2;
            this.mapper = mapper;
        }
        @Override public boolean hasNext() {
            return i1.hasNext() && i2.hasNext();
        }
        @Override public R next() {
            return mapper.apply(i1.next(), i2.next());
        }
    }
}
