package jp.adtekfuji.prodcountreporter;


import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


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


    /*
     * ファイルをロードし、DataTable型で返します
     * @param fileName ファイル名(tsv形式のファイル)
     * @return DataTable
     */
    public static Optional<DataTable> LoadFile(final String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.print("ファイルが存在しません");
            return Optional.empty();
        }

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            // 一行目はヘッダ
            String header;
            if ((header = br.readLine()) == null) {
                return Optional.empty();
            }

            AtomicInteger index = new AtomicInteger(0);
            LinkedHashMap<String, Integer> headerMap = Arrays.stream(header.split("\t", -1))
                    .collect(toMap(str -> str, str -> index.getAndIncrement(), (a, b) -> b, LinkedHashMap::new));

            if (headerMap.entrySet().stream().anyMatch(e -> e.getKey().isEmpty())) {
                System.out.println("Error : Header have NULL!!");
                return Optional.empty();
            }
            if (headerMap.isEmpty()) {
                System.out.println("Error : Header Empty!!");
                return Optional.empty();
            }

            // body追加
            List<List<String>> bodyArray = new ArrayList<>();
            String body;
            while ((body = br.readLine()) != null) {
                ArrayList<String> bodies = new ArrayList<>(Arrays.asList(body.split("\t", -1)));

                if (bodies.size() != headerMap.size()) {
                    System.out.println("Error : Header Size Not Eq Body");
                    return Optional.empty();
                }

                bodyArray.add(
                        bodies.stream()
                                .map(textMetacharactersConverter)
                                .collect(toList())
                );
            }

            return Optional.of(new DataTable(headerMap, bodyArray));

        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    static Function<String, String> metacharactersTextConverter = StringUtil.CreateReplaceFunctor(Collections.singletonList("(\\n|\\t|\\\\)"),
            new HashMap<String, String>() {
                {
                    put("\t", "\\t");
                    put("\n", "\\n");
                    put("\\", "\\\\");
                }
            }::get);


    static Function<String, String> textMetacharactersConverter = StringUtil.CreateReplaceFunctor(Collections.singletonList("(\\\\n|\\\\t|\\\\\\\\)"),
            new HashMap<String, String>() {
                {
                    put("\\t", "\t");
                    put("\\n", "\n");
                    put("\\\\", "\\");
                }
            }::get);

    /*
     * adFactoryDataをtsvファイル形式で保存
     * @param fileName ファイル名(tsv形式のファイル)
     * @return boolean
     */
    public boolean saveFile(String fileName) {
        File file = new File(fileName);
        // ファイル生成
        try {
            if (!file.createNewFile()) {
                System.out.println("False : createNewFile  " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.join("\t", headerMap.keySet()) + "\n");

            for (List<String> body : bodyArray) {
                bw.write(
                        body.stream()
                                .map(metacharactersTextConverter)
                                .collect(joining("\t"))
                                + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * labelの行を取得する
     * @param label ラベル名
     * @return ArrayList<String> 取得行
     */
    public Optional<List<String>> getColumnList(final String label) {
        Integer index = headerMap.get(label);
        if (Objects.isNull(index)) {
            return Optional.empty();
        }

        List<String> ret = new ArrayList<>();
        for (List<String> list : bodyArray) {
            ret.add(list.get(index));
        }
        return Optional.of(ret);
    }

    public Optional<List<List<String>>> getColumnList(final List<String> label) {
        if (label.stream().noneMatch(headerMap::containsKey)) {
            return Optional.empty();
        }

        List<Integer> indexList = label.stream().map(headerMap::get).collect(toList());
        return Optional.of(bodyArray.stream().map(l -> indexList.stream().map(l::get).collect(toList())).collect(toList()));
    }


    /*
     * labelの列を削除する
     * @param label ラベル名
     * @return DataTable 削除された新しいテーブル
     */
    public Optional<DataTable> getRemoveLabelColumnTable(String label) {
        if (!headerMap.containsKey(label)) {
            return Optional.empty();
        }

        final int index = headerMap.get(label);
        LinkedHashMap<String, Integer> header = new LinkedHashMap<>(headerMap);

        header.remove(label);

        header.forEach((key, val) -> {
            if (val > index) {
                header.replace(key, val - 1);
            }
        });

        List<List<String>> ret = new ArrayList<>();
        for (List<String> body : bodyArray) {
            int count = 0;
            ArrayList<String> bodyArray = new ArrayList<>();
            for (String elem : body) {
                if (index != count++) {
                    bodyArray.add(elem);
                }
            }
            ret.add(bodyArray);
        }

        return Optional.of(new DataTable(header, ret));
    }

    public Integer getIndex(String name) {
        return headerMap.get(name);
    }

    public List<String> getHeaderList() {
        return new ArrayList<>(headerMap.keySet());
    }

    public List<String> getData(int index) {
        return new ArrayList<>(bodyArray.get(index));
    }

    public void forEach(Consumer<List<String>> consumer) {
        bodyArray.forEach(consumer);
    }


    public boolean replaceColumn(String label, Function<String, String> functor) {
        Integer index = headerMap.get(label);
        if (Objects.isNull(index)) {
            System.out.printf("replaceColumn Error : %s is Not Exit", label);
            return false;
        }

        bodyArray.forEach(elem -> elem.set(index, functor.apply(elem.get(index))));
        return true;
    }

    public int columnSize() {
        return bodyArray.size();
    }

    public boolean isEmpty() {
        return bodyArray.isEmpty();
    }

    public Optional<DataTable> addDataTable(DataTable elm) {
        if (!headerMap.equals(elm.headerMap)) {
            return Optional.empty();
        }
        if (!bodyArray.addAll(elm.bodyArray)) {
            return Optional.empty();
        }
        return Optional.of(this);
    }


    public List<DataTable> Split(int size) {
        int bodySize = bodyArray.size();

        List<DataTable> ret = new ArrayList<>();
        for (int n = 0; n < bodySize; n += size) {
            ret.add(new DataTable(headerMap, bodyArray.subList(n, Math.min(n + size, bodySize))));
        }
        return ret;
    }

    public Optional<DataTable> filter(String label, Predicate<String> func) {
        if (!headerMap.containsKey(label)) {
            System.out.printf("filter error : notFount label %s", label);
            return Optional.empty();
        }

        Integer index = headerMap.get(label);
        return Optional.of(new DataTable(headerMap, bodyArray.stream().filter(l -> func.test(l.get(index))).collect(toList())));

    }

    public List<Map<String, String>> toMapList() {
        return bodyArray.stream()
                .map(l -> headerMap
                        .entrySet()
                        .stream()
                        .collect(HashMap<String, String>::new, (m, e) -> m.put(e.getKey(), l.get(e.getValue())), HashMap::putAll))
                .collect(toList());
    }

}
