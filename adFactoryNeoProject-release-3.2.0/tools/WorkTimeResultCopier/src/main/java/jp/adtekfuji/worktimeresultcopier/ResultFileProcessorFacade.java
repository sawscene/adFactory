package jp.adtekfuji.worktimeresultcopier;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adtekfuji.utility.FileUtils;
import adtekfuji.utility.NetworkFileUtil;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import io.vavr.control.Either;
import jp.adtekfuji.worktimeresultcopier.mail.MailProperty;
import jp.adtekfuji.worktimeresultcopier.mail.MailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.stream.Collectors.*;


/**
 * ResultFileProcessorFacadeクラスは、実績出力システムにおけるファイル処理の統合ファサードを提供します。
 * 本クラスは、対象ファイルの読み込みや分割、解析、保存などの一連の操作をダイレクトに制御します。
 * ファイルに関連するエラー処理やログ出力、通知操作も内包しています。
 * 主な処理内容は以下の通り:
 * - ファイルリストの読込
 * - 設定された基準に基づくファイル分割操作
 * - 書き込み対象ファイルの生成および保存
 * - エラーや不正データの処理
 */
public class ResultFileProcessorFacade {
    public static final String READ_FILE_NAME_LIST_FILE_PREFIX = "ReadFileNameList_";
    public static final String READ_FILE_NAME_LIST_WORK_TIME_RESULT_FILE_PREFIX = "ReadFileNameListWorkTimeResult_";


    private enum FileStatus {
        OUTPUT_FILE_NAMES, // 出力済みファイル
        RETAINED_FILE_NAMES, // 未出力ファイル
        MISSING_FILE_NAMES // 失踪ファイル
    }

    private final static Logger logger = LogManager.getLogger();
    private final static String LINE_SEPARATOR = System.lineSeparator();

    private final OutputActualInfo info;
    /**
     * コンストラクタ
     *
     * @param info 実績出力設定情報
     */
    public ResultFileProcessorFacade(OutputActualInfo info) {
        this.info = info;
    }


    /**
     * ファイル分割処理を実行します。
     * 処理の成功または失敗に応じて、メール送信や終了コードを制御します。
     *
     * @return 処理成功の場合は0を返します。
     *         処理失敗の場合は-1を返します。
     */
    public int invoke() {
        Either<String, String> result = separateFiles();
        result.orElseRun(this::sendErrorMail);
        return result.isRight() ? 1 : -1;
    }


    /**
     * WorkTimeReporterが出力したファイル一覧を取得します。
     * 指定されたフォルダ内で「readFileNameList_」で始まるファイル
     * または「readFileNameListWorkTimeResult.txt」という名前のファイルを検索してリスト化します。
     * 検索対象のフォルダが存在しない場合、エラーメッセージを返します。
     *
     * @return ファイル一覧を含むRightオブジェクトを返します。
     *         失敗した場合、エラーメッセージを含むLeftオブジェクトを返します。
     */
    private static Either<String, List<File>> getLoadTargetListFiles(String readFileNameAddress) {
        final FileFilter isTargetListFile
                = file -> file.getName().startsWith(READ_FILE_NAME_LIST_FILE_PREFIX) || file.getName().startsWith(READ_FILE_NAME_LIST_WORK_TIME_RESULT_FILE_PREFIX);

        // WorkTimeReporterが出力したファイル一覧用のデータを読み込む
        File[] loadTargetFilesOfInFolder
                = new File(readFileNameAddress)
                .listFiles(isTargetListFile);
        if (Objects.isNull(loadTargetFilesOfInFolder)) {
            logger.fatal("Failed to retrieve the list of output files: {}", readFileNameAddress);
            return Either.left("出力ファイル一覧の取得に失敗しました。");
        }
        return Either.right(Arrays.asList(loadTargetFilesOfInFolder));
    }

    /**
     * 指定されたファイルのリストからターゲットファイルリストをロードします。
     * 各ファイルについてファイル名リストを取得し、それをまとめて1つのリストとして返します。
     *
     * @param loadTargetFiles ロード対象のファイルのリスト
     * @return 処理結果として文字列エラーまたはタプルリストを格納したEitherオブジェクト
     */
    private static Either<String, List<Tuple<File, List<Tuple<String, Date>>>>> loadTargetFileList(List<File> loadTargetFiles) {
            List<Tuple<File, List<Tuple<String, Date>>>> loadTargetFileList = new ArrayList<>();
            for (File loadTargetFile : loadTargetFiles) {
                Either<String, List<Tuple<String, Date>>> result = loadFileNameList(loadTargetFile);
                if (result.isLeft()) {
                    return Either.left(result.getLeft());
                }
                loadTargetFileList.add(new Tuple<>(loadTargetFile, result.getOrElse(new ArrayList<>())));
            }
            return Either.right(loadTargetFileList);
    }


    /**
     * 指定されたディレクトリ内のファイルをフィルタリングしてマッピングを作成します。
     *
     * @param directoryPath ファイルを検索する基準となるディレクトリパス
     * @param predicate ファイル名をフィルタリングするための正規表現パターン
     * @return 正常に処理された場合は、ファイル名(拡張子除去)をキー、ファイルオブジェクトを値とするMapを含むRightを返します。
     *         フォルダ内のファイル読み込みに失敗した場合、エラーメッセージを含むLeftを返します。
     */
    private static Either<String, Map<String, File>> createFileMap(File directoryPath, Pattern predicate) {
        File[] files = directoryPath.listFiles(File::isFile);
        if (Objects.isNull(files)) {
            logger.fatal("Failed to read files in the folder: {}", directoryPath.getAbsolutePath());
            return Either.left("フォルダ内のファイル読み込みに失敗しました。: " + directoryPath.getAbsolutePath());
        }

        return Either.right(
                Arrays.stream(files)
                        .filter(file -> predicate.matcher(file.getName()).find())
                        .collect(toMap(file -> StringUtils.removeExtension(file.getName()), Function.identity(), (a, b) -> b)));
    }

    /**
     * 指定されたファイルアドレスを使用してターゲットファイルリストをロードします。
     * 読み込み対象のファイル名を取得し、それを元にターゲットファイルのリストを生成します。
     *
     * @param readFileNameAddress 読み込み対象ファイル名リストのファイルアドレス
     * @return 成功時にファイルと対応するデータリストのタプルを含むリストを格納したRightオブジェクトを返します。
     *         失敗時にはエラーメッセージを含むLeftオブジェクトを返します。
     */
    private static Either<String, List<Tuple<File, List<Tuple<String, Date>>>>> loadTargetFileList(String readFileNameAddress) {
        return getLoadTargetListFiles(readFileNameAddress)
                .flatMap(ResultFileProcessorFacade::loadTargetFileList);
    }

    /**
     * 指定されたリストからファイル名と日時のペア情報を出力ファイルに書き込みます。
     * 書き込み先ファイルが存在しない場合は新規作成します。
     *
     * @param retainedFiles ファイル名と日時のペア情報を格納したリスト
     * @param readFileNameAddress 書き込み先ファイルのアドレス
     * @return 書き込みが成功した場合はtrue、失敗した場合はfalse
     */
    private static boolean writeReadFileNameListWorkTimeResultFile(List<Tuple<String, Date>> retainedFiles, String readFileNameAddress)
    {
        if (Objects.isNull(retainedFiles) || retainedFiles.isEmpty()) {
            return true;
        }

        String fileName = readFileNameAddress + File.separator + READ_FILE_NAME_LIST_WORK_TIME_RESULT_FILE_PREFIX + sdf.format(new Date()) + ".txt";
        File readFileNameWorkTimeResult = new File(fileName);
        try {
            if (!readFileNameWorkTimeResult.exists()) {
                if (!readFileNameWorkTimeResult.createNewFile()) {
                    logger.error("Failed to create file: {}", readFileNameWorkTimeResult.getAbsolutePath());
                    return false;
                }
            }

            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(readFileNameWorkTimeResult)))) {
                for (Tuple<String, Date> retainedFile : retainedFiles) {
                    pw.println(retainedFile.getLeft() + "," + sdf.format(retainedFile.getRight()));
                }
                return true;
            }
        } catch (Exception ex) {
            if (readFileNameWorkTimeResult.exists()) {
                readFileNameWorkTimeResult.delete();
            }
            logger.fatal(ex, ex);
            return false;
        }
    }


    final static Pattern outFilePattern = Pattern.compile("^Q.*\\.log$");
    final static Pattern inFilePattern = Pattern.compile("^Q.*\\.csv$");


    /**
     * outフォルダおよびinフォルダ内のファイルを整理し、指定された共有フォルダに処理結果を保存します。
     * このメソッドは以下の処理を実行します:
     * - 指定されたフォルダに接続し、ファイルマップを作成
     * - ファイル分類 (出力されたファイル、保持状態のファイル、紛失したファイル)
     * - 紛失ファイルに関する通知、および管理ファイルの更新
     * - 管理ファイル削除が失敗した場合や、エラーが発生した場合のログ出力
     *
     * @return 左要素としてエラーメッセージ、右要素として処理結果文字列を格納したEitherオブジェクト
     */
    private Either<String, String> separateFiles() {
        List<String> errorMessages = new ArrayList<>();

        // 共有フォルダ
        File sharFolder = new File(info.getShareFolder());

        // outフォルダ接続
        Either<String, File> outFolderEither = NetworkFileUtil.connect(info.getOutFolder(), info.getUser(), info.getPassword());
        if (outFolderEither.isLeft()) {
            return Either.left(outFolderEither.getLeft());
        }
        File outFolder = outFolderEither.get();

        // inフォルダ接続
        Either<String, File> inFolderEither = NetworkFileUtil.connect(info.getInFolder(), info.getUser(), info.getPassword());
        if (inFolderEither.isLeft()) {
            return Either.left(inFolderEither.getLeft());
        }
        File inFolder = inFolderEither.get();

        // outフォルダにあるファイル一覧の取得
        Either<String, Map<String, File>> outFolderFileMapEither = createFileMap(outFolder, outFilePattern);
        if (outFolderFileMapEither.isLeft()) {
            return Either.left(outFolderFileMapEither.getLeft());
        }
        Map<String, File> outFolderFileMap = outFolderFileMapEither.get();

        // inフォルダにあるファイル一覧を取得
        Either<String, Map<String, File>> inFolderFileMapEither = createFileMap(inFolder, inFilePattern);
        if (inFolderFileMapEither.isLeft()) {
            return Either.left(inFolderFileMapEither.getLeft());
        }
        Map<String, File> inFolderFileMap = inFolderFileMapEither.get();

        String readFileNameAddress = info.getReadFileNameAddress();
        if (StringUtils.isEmpty(readFileNameAddress)) {
            readFileNameAddress = System.getProperty("user.dir");
        }

        // 実績出力したがまだ処理していないファイル一覧を取得する。
        Either<String, List<Tuple<File, List<Tuple<String, Date>>>>> targetFileListEither = loadTargetFileList(readFileNameAddress);
        if (targetFileListEither.isLeft()) {
            return Either.left(targetFileListEither.getLeft());
        }

        List<Tuple<File, List<Tuple<String, Date>>>> targetFileList = targetFileListEither.get();
        try {
            // 管理ファイルの削除
            List<Tuple<String, Date>> targetFileList2 = new ArrayList<>();
            for (Tuple<File, List<Tuple<String, Date>>> targetFileTuple : targetFileList) {
                if (targetFileTuple.getLeft().delete()) {
                    targetFileList2.addAll(targetFileTuple.getRight());
                } else {
                    // 消せない場合、ファイルが残ったままなので次回に回す。
                    logger.error("Failed to delete the management file. fileName: {}", targetFileTuple.getLeft().getName());
                    errorMessages.add(String.format("管理ファイルの削除に失敗しました。 ファイル名: %s", targetFileTuple.getLeft().getName()));
                }
            }

            /*
              outフォルダ、inフォルダの中に有無を元にカテゴライズするヘルパー関数
             */
            Function<String, FileStatus> categorizeFilesFunction = (filename) -> {
                if (outFolderFileMap.containsKey(filename)) {
                    return FileStatus.OUTPUT_FILE_NAMES;
                } else if (inFolderFileMap.containsKey(filename)) {
                    return FileStatus.RETAINED_FILE_NAMES;
                } else {
                    return FileStatus.MISSING_FILE_NAMES;
                }
            };

            // カテゴライズする。
            Map<FileStatus, List<Tuple<String, Date>>> categorizedFiles
                    = targetFileList2
                    .stream()
                    .collect(groupingBy(pair -> categorizeFilesFunction.apply(pair.getLeft())));


            List<Tuple<String, Date>> retainedFiles = categorizedFiles.getOrDefault(FileStatus.RETAINED_FILE_NAMES, new ArrayList<>());

            // ファイル出力結果を設定
            Either.sequence(categorizedFiles.getOrDefault(FileStatus.OUTPUT_FILE_NAMES, new ArrayList<>())
                            .stream()
                            .map(Tuple::getLeft)
                            .map(file -> createResultFile(outFolderFileMap.get(file), inFolder, sharFolder, (fileName) -> retainedFiles.add(new Tuple<>(StringUtils.removeExtension(fileName), new Date()))))
                            .collect(toList()))
                    .mapLeft(left -> left.collect(joining(System.lineSeparator())))
                    .map(right -> right.collect(joining(System.lineSeparator())))
                    .orElseRun(errorMessages::add);

            // 24時間前の時刻をミリ秒単位で計算
            final long beforeOneDay = new Date().getTime() - (24 * 60 * 60 * 1000);
            Function<Tuple<String, Date>, Boolean> isExpired = data -> data.getRight().getTime() < beforeOneDay;


            // inフォルダにもoutフォルダにもない(紛失)場合は通知する。
            List<Tuple<String, Date>> missingTargetFiles = new ArrayList<>();
            categorizedFiles.getOrDefault(FileStatus.MISSING_FILE_NAMES, new ArrayList<>())
                    .forEach(data -> {
                        if (isExpired.apply(data)) {
                            missingTargetFiles.add(data);
                        } else {
                            retainedFiles.add(data);
                        }
                    });
            if (!missingTargetFiles.isEmpty()) {
                logger.error("The following files has been lost." + System.lineSeparator() 
                        + missingTargetFiles
                        .stream()
                        .map(data -> String.format("fileName : %s, creationDate: %s", data.getLeft(), sdf.format(data.getRight())))
                        .collect(joining(System.lineSeparator())) + System.lineSeparator());
                errorMessages.add(
                        "以下のファイルが紛失しました。" + System.lineSeparator()
                                + missingTargetFiles
                                .stream()
                                .map(data -> String.format("ファイル名 : %s, 作成日: %s", data.getLeft(), sdf.format(data.getRight())))
                                .collect(joining(System.lineSeparator())) + System.lineSeparator());
            }

            // 次回のチェックリストに保存する
            if (!writeReadFileNameListWorkTimeResultFile(retainedFiles, readFileNameAddress)) {
                errorMessages.add("管理ファイルの出力に失敗しました。下記ファイルの確認を手動で実施してください。" + System.lineSeparator()
                        + retainedFiles.stream().map(Tuple::getLeft).collect(joining(System.lineSeparator())) + System.lineSeparator());
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            errorMessages.add(0, "不明なエラーが発生しました。");
        }

        if (!errorMessages.isEmpty()) {
            return Either.left(errorMessages.stream().collect(joining(System.lineSeparator())));
        }

        return Either.right("");
    }


    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    final static Pattern fileNameLinePattern = Pattern.compile("(?<fileName>.*),\\s*(?<date>\\d{14})$");

    /**
     * 指定された文字列を解析し、ファイル名と日付のタプルを返します。
     * 入力文字列が空である場合やフォーマットが異なる場合、エラーを返します。
     *
     * @param line 対象となる1行の文字列データ
     * @return ファイル名と日付のタプルが格納されたEither型オブジェクト。
     *         正常に解析できた場合Rightにタプル (ファイル名, 日付) が返され、エラーの場合Leftにエラーメッセージが格納されます。
     */
    private static Either<String, Tuple<String, Date>> parseFileNameListLine(String line) {
        // 空行の場合は処理をスキップ
        if (StringUtils.isEmpty(line)) {
            return Either.right(new Tuple<>(null, null));
        }

        Matcher matcher = fileNameLinePattern.matcher(line);
        if (matcher.find()) {
            try {
                return Either.right(new Tuple<>(matcher.group("fileName"), sdf.parse(matcher.group("date"))));
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
        logger.fatal("File format is abnormal. line:{}", line);
        return Either.left("ファイルフォーマット異常です。");
    }


    /**
     * 指定されたファイルからファイル名リストを読み込みます。
     * ファイル内の各行を解析して、ファイル名と日付のペアのリストを作成します。
     * 解析中にエラーが発生した場合、エラーメッセージを返します。
     *
     * @param file 対象のファイルオブジェクト
     * @return ファイル名と日付のペアのリストを含む成功結果、またはエラーメッセージを含む失敗結果
     */
    private static Either<String, List<Tuple<String, Date>>> loadFileNameList(File file) {
        try {
            List<Tuple<String, Date>> fileNameList = new ArrayList<>();
            for (String line : Files.readAllLines(file.toPath())) {
                Either<String, Tuple<String, Date>> result = parseFileNameListLine(line);
                if (result.isLeft()) {
                    logger.error("ファイル名 : {}", file.getName());
                    return Either.left(String.format("%s ファイル名 : %s, ライン: %s", result.getLeft(), file.getName(), line));
                }
                if (StringUtils.nonEmpty(result.get().getLeft())) {
                    fileNameList.add(result.get());
                }
            }
            return Either.right(fileNameList);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
            return Either.left("ファイルの読み込みに失敗しました。ファイル名: " + file.getName());
        }
    }

    static final Pattern SuccessPattern = Pattern.compile("^(?<result>([^,]*,){4}[^,]*)");
    /**
     * WorkTimesResultクラスは、処理結果を表すデータを格納および操作するためのクラスです。
     * 主に処理結果を保持する文字列、および関連するデータを格納します。
     * また、成功かどうかの判定や結果のフォーマットを提供するメソッドを持ちます。
     */
    static public class WorkTimesResult {
        public static final String SUCCESS = "SUCCESS";

        private final String result;
        private final String data;

        public WorkTimesResult(String result, String data) {
            this.result = result;
            this.data = data;
        }

        public String getString() {
            if (SUCCESS.equals(result)) {
                Matcher m = SuccessPattern.matcher(data);
                return m.find()
                        ? m.group("result")
                        : data;
            }
            return String.format("%s, \"[%s]\"", result, data);
        }

        static String getErrorHeader() {
            return "エラー内容, [出力内容]";
        }

        static boolean isSuccess(WorkTimesResult result) {
            return SUCCESS.equals(result.result);
        }
    }

    /**
     * 指定された文字列を解析し、WorkTimesResultオブジェクトを生成します。
     *
     * @param line 入力となる1行の文字列。カンマ区切りのデータが期待されます。
     * @return 解析された結果を元に作成されたWorkTimesResultオブジェクト。
     *         データが不完全または指定された条件を満たさない場合は、"SUCCESS"をステータスとするWorkTimesResultとなります。
     */
    private static WorkTimesResult parseWorkTimesResultFileLine(String line) {
        String[] data = line.split(",", -1);
        if (data.length < 6) {
            return new WorkTimesResult("フォーマット異常", line);
        }
        if (data.length == 6 || StringUtils.isEmpty(data[6])) {
            return new WorkTimesResult("SUCCESS", line);
        }

        if (data.length < 9) {
            return new WorkTimesResult("フォーマット異常", line);
        }

        return new WorkTimesResult(data[8], line);
    }

    /**
     * 指定されたファイルを読み込み、作業時間結果のデータをパースします。
     *
     * @param file パース対象の作業時間結果ファイル
     * @return 作業時間結果のリストを含むRight、またはエラーメッセージを含むLeft
     */
    private static Either<String, List<WorkTimesResult>> parseWorkTimesResultFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return Either.right(lines
                    .stream()
                    .filter(StringUtils::nonEmpty)
                    .map(ResultFileProcessorFacade::parseWorkTimesResultFileLine)
                    .collect(toList()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Either.left("WorkTimes結果ファイル読み込み異常。 ファイル名: " + file.getName());
        }
    }


    /**
     * 勤怠結果のリストを指定されたファイルに書き込むメソッドです。
     * 各勤怠結果は文字列形式で出力されます。対象ファイルの保存先フォルダが
     * 存在しない場合は、自動的にフォルダを生成します。
     *
     * @param results 書き込み対象となるWorkTimesResultのリスト
     * @param filePath 書き込み先のファイルパス
     * @return 書き込みが成功した場合はtrue、失敗した場合はfalse
     */
    private static boolean writeWorkTimesResult(List<WorkTimesResult> results, File filePath, String header, String charsetName) {
        File directory = filePath.getParentFile();
        if (directory != null && !directory.exists()) {
            // フォルダが存在しない場合は生成する
            if (!directory.mkdirs()) {
                logger.error("Failed to create the folder: {}", directory.getAbsolutePath());
                return false;
            }
        }

        try (Writer writer
                     = new BufferedWriter(
                             new OutputStreamWriter(
                                     new FileOutputStream(filePath), charsetName))) {
            if (StringUtils.nonEmpty(header)) {
                writer.write(header + System.lineSeparator());
            }
            writer.write(results.stream().map(WorkTimesResult::getString).collect(joining(System.lineSeparator())));
            return true;
        } catch (Exception ex) {
            logger.error("Failed to output the file");
            logger.fatal(ex, ex);
            return false;
        }
    }

    /**
     * 指定されたソースファイルをターゲットファイルにコピーします。
     *
     * @param source コピー元のファイルを表すFileオブジェクト
     * @param target コピー先のファイルを表すFileオブジェクト
     * @return ファイルのコピーが成功した場合はtrue、失敗した場合はfalse
     */
    private static boolean copyFile(File source, File target) {
        try{
            Files.copy(source.toPath(), target.toPath());
        }catch(Exception ex){
            logger.fatal(ex, ex);
            return false;
        }
        return true;
    }

    /**
     * ファイル名を生成し、一意なファイルが存在しない場合にそのファイルオブジェクトを返します。
     * ファイル名が衝突した場合は、インデックスを付与して新しい名称を試行します。
     * 最大で10000件まで試行し、それ以上衝突が発生した場合はエラーメッセージを返します。
     *
     * @param fileName 基となるファイル名
     * @return 一意なファイル名が生成できた場合はFile型のRightを返し、
     *         生成に失敗した場合はエラーメッセージを含むString型のLeftを返します
     */
    private static Either<String, File> generateFileName(String fileName) {
        int index = 0;
        while (index < 10000) {
            File file = new File(fileName + "_" + index);
            if (!file.exists()) {
                return Either.right(file);
            }
            ++index;
        }
        logger.fatal("Failed to generate the file name");
        return Either.left("ファイルの名称生成に失敗しました。");
    }

    /**
     * 勤務時間結果のリストをもとに、結果ファイルを指定された場所に作成します。
     *
     * @param results 勤務時間結果のリスト
     * @param targetFile 作成先の結果ファイル
     * @return ファイル作成に成功した場合は生成されたファイル名を含むRightを返します。
     *         エラーが発生した場合はエラーメッセージを含むLeftを返します。
     */
    static Either<String, String> createWorkTimesResultFile(List<WorkTimesResult> results, File targetFile, String header, String charsetName) {
        if (results.isEmpty()) {
            return Either.right("");
        }

        File workingFolder = new File(System.getProperty("user.dir"));
        if (!workingFolder.exists()) {
            logger.fatal("The working directory is not specified correctly");
            return Either.left("作業ディレクトリが正しく指定されていません。");
        }

        return generateFileName(workingFolder.getPath() + File.separator + targetFile.getName())
                .flatMap(tmpFile -> {
                    // 一時ファイルの書き込み && ファイルのコピー
                    if (!writeWorkTimesResult(results, tmpFile, header, charsetName) || !copyFile(tmpFile, targetFile)) {
                        if (tmpFile.exists()) {
                            tmpFile.delete();
                        }
                        return Either.left("ファイルの生成に失敗しました。");
                    }
                    // コピー済みのファイルの削除
                    tmpFile.delete();
                    
                    return Either.right(tmpFile.getName());
                });
    }
    
    /**
     * 出力するファイル名を生成する
     * 
     * @param fileName 処理対象のファイル名
     * @return ファイル名の末尾に「#数字」を追加したファイル名、元からファイル名の末尾に「#数字」がある場合は数字を1増やしている
     * 
     */
    private static String generateCsvFileName(String fileName) {
        String fileNameWithoutExtention = StringUtils.removeExtension(fileName);
        int lastIndex = fileName.lastIndexOf("#");
        
        if (lastIndex == -1) {
            return fileNameWithoutExtention + "#1.csv";
        } else {
            int csvFileNameNum = Integer.parseInt(fileNameWithoutExtention.substring(lastIndex + 1)) + 1;
            return fileNameWithoutExtention.substring(0, lastIndex + 1) + csvFileNameNum + ".csv";
        }
    }

    /**
     * 指定されたファイルを解析し、成功した結果と失敗した結果をそれぞれ別のフォルダに出力するメソッド。
     *
     * @param targetFile 処理対象のファイル
     * @param successDataFolder 成功した結果のCSVを保存するフォルダ
     * @param errorDataFolder 失敗した結果のCSVを保存するフォルダ
     * @return 処理結果を示す文字列。成功時は空文字が右値として返され、失敗時はエラーメッセージが左値として返される
     */
    private static Either<String, String> createResultFile(File targetFile, File successDataFolder, File errorDataFolder, Consumer<String> fileNameConsumer) {
        // WorkTimesから出力したファイルの解析
        Either<String, List<WorkTimesResult>> workTimeResults = parseWorkTimesResultFile(targetFile);
        if (workTimeResults.isLeft()) {
            return Either.left(workTimeResults.getLeft());
        }

        // 成功の結果と失敗の結果に分ける。
        Map<Boolean, List<WorkTimesResult>> workTimesResultGroup
                = workTimeResults.get()
                .stream()
                .collect(groupingBy(WorkTimesResult::isSuccess));

        List<WorkTimesResult> successResult = workTimesResultGroup.getOrDefault(true, new ArrayList<>());
        List<WorkTimesResult> errorResult = workTimesResultGroup.getOrDefault(false, new ArrayList<>());
        if (errorResult.isEmpty()) {
            // エラーが一つもない場合は登録が成功しているため終了
            return Either.right("");
        }

        String csvFileName = generateCsvFileName(targetFile.getName());
        // 成功した結果をInフォルダへ書き込み
        File fileInSuccessDataFolder = new File(successDataFolder.getPath(), csvFileName);
        Either<String, String> successWriteTimeResult = createWorkTimesResultFile(successResult, fileInSuccessDataFolder, null, "UTF-8");
        if (successWriteTimeResult.isRight()) {
            if (StringUtils.nonEmpty(successWriteTimeResult.get()) && Objects.nonNull(fileNameConsumer)) {
                fileNameConsumer.accept(successWriteTimeResult.get());
            }
        } else {
            successResult.forEach(workTimeResult -> errorResult.add(new WorkTimesResult(successWriteTimeResult.getLeft(), workTimeResult.data)));
        }

        // 失敗した結果を共有フォルダへ書き込み
        Either<String, String> errorWriteTimeResult = createWorkTimesResultFile(errorResult, new File(errorDataFolder.getPath(), csvFileName), WorkTimesResult.getErrorHeader(), "Shift_JIS");
        if (errorWriteTimeResult.isLeft()) {
            String notWriteErrorTxt
                    = errorResult
                    .stream()
                    .map(WorkTimesResult::getString)
                    .collect(joining(System.lineSeparator()));
            return Either.left(
                    "エラーファイルの出力に失敗しました。 ファイル名 : " + csvFileName + System.lineSeparator()
                            + WorkTimesResult.getErrorHeader() + System.lineSeparator()
                            + notWriteErrorTxt + System.lineSeparator());
        }
        return Either.right("");
    }


    /**
     * エラーメールを送信するメソッド。
     * 指定されたエラー内容を元にメールを作成し、事前に設定されたメールアドレスに送信します。
     *
     * @param message エラーの具体的な内容を記載したメッセージ
     */
    private void sendErrorMail(String message) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setUser(info.getErrorMailUser());
            prop.setPassword(info.getErrorMailPassword());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);

            String from = info.getErrorMailFrom();
            String to = info.getErrorMailTo();
            String subject = "【adFactory 工数連携サポートツール】 連携結果取込エラー";

            StringBuilder content = new StringBuilder();
            content.append("adFactory 工数連携サポートツールの連携結果取込でエラーが発生しました。");
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append("エラー発生日時: ");
            content.append(dateFormatter.format(new Date()));
            content.append(LINE_SEPARATOR);
            content.append("エラー詳細:");
            content.append(LINE_SEPARATOR);
            content.append(message);

            MailUtils mail = new MailUtils(prop);
            mail.send(from, to, subject, content.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

}
