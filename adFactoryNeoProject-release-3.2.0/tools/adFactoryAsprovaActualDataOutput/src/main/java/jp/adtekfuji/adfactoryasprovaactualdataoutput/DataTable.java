package jp.adtekfuji.adfactoryasprovaactualdataoutput;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * DBのデータクラス
 */
public class DataTable {

    private final LinkedHashMap<String, Integer> headerMap;
    private final List<List<String>> bodyArray;

    /**
     * コンストラクタ
     *
     * @param header ヘッダデータ
     * @param body   ボディーデータ
     */
    private DataTable(LinkedHashMap<String, Integer> header, List<List<String>> body) {
        headerMap = new LinkedHashMap<>(header);
        bodyArray = body.stream().map(ArrayList::new).collect(toList());
    }

    /**
     * 配列よりデータを作成
     *
     * @param data 一行目はヘッダとする事
     * @return DataTableを生成して返す
     */
    public static Optional<DataTable> create(String[]... data) {
        return create(Stream.of(data).map(l -> Stream.of(l).collect(toList())).collect(toList()));
    }

    /**
     * Listの多次元配列よりDataTableを生成
     *
     * @param data 多次元配列
     * @return DataTableを生成して返す
     */
    public static Optional<DataTable> create(List<List<String>> data) {
        if (data.isEmpty()) {
            return Optional.empty();
        }

        int headerSize = data.get(0).size();
        if (data.stream().map(List::size).anyMatch(n -> n != headerSize)) {
            return Optional.empty();
        }

        if (data.get(0).stream().anyMatch(str -> str.isEmpty())) {
            return Optional.empty();
        }

        AtomicInteger index = new AtomicInteger(0);
        LinkedHashMap<String, Integer> headerMap = data.get(0)
                .stream()
                .collect(toMap(str -> str, str -> index.getAndIncrement(), (a, b) -> b, LinkedHashMap::new));

        if (headerMap.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new DataTable(headerMap, data.size() <= 1 ? new ArrayList<>() : data.subList(1, data.size())));
    }


    /**
     * SQLの実行よりDataTableを返す
     *
     * @param resultSet SQL実行結果
     * @return DataTableを生成して返す
     */
    public static Optional<DataTable> create(ResultSet resultSet) throws SQLException {

        ResultSetMetaData meta = resultSet.getMetaData();
        final int columnCount = meta.getColumnCount();

        // ヘッダが無い場合は異常とする
        if (columnCount <= 0) {
            return Optional.empty();
        }

        LinkedHashMap<String, Integer> headerMap = new LinkedHashMap<>();
        for (int i = 0; i < columnCount; ++i) {
            headerMap.put(meta.getColumnName(i + 1), i);
        }

        //本体
        List<List<String>> body = new ArrayList<>();

        while (resultSet.next()) {
            ArrayList<String> bodyElement = new ArrayList<>();
            for (int i = 1; i <= columnCount; ++i) {
                bodyElement.add(resultSet.getString(i));
            }
            body.add(bodyElement);
        }

        return Optional.of(new DataTable(headerMap, body));

    }

    /**
     * 実績情報をListにして返す。
     *
     * @return 実績情報リスト
     */
    public List<Map<String, String>> toMapList() {
        return bodyArray.stream()
                .map(l -> headerMap
                        .entrySet()
                        .stream()
                        .collect(HashMap<String, String>::new, (m, e) -> m.put(e.getKey(), l.get(e.getValue())), HashMap::putAll))
                .collect(toList());
    }
}
