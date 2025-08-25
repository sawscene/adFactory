/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * パスユーティリティクラス
 *
 * @author s-heya
 */
public class PathUtils {

    /**
     * ルートパス(例: C:\adFactory)を取得する
     *
     * @return カレントパス
     */
    public static String getRootPath() {
        PathUtils myClass = new PathUtils();
        URL url = myClass.getClass().getResource("PathUtils.class");

        assert url != null;

        File dir = null;
        String protocol = url.getProtocol();

        if (protocol.equals("jar")) {
            Pattern p = Pattern.compile("(file:/.*)!/");
            Matcher m = p.matcher(url.toString());
            if (m.find()) {
                assert false;
            }

            try {
                File jarFile = new File(new URI(m.group(1)));
                dir = jarFile.getParentFile();
                dir = dir.getParentFile();
            }
            catch (URISyntaxException ex) {
                assert false;
            }
        }
        else if (protocol.equals("file")) {
            try {
                File classFile = new File(new URI(url.toString()));
                Package pack = myClass.getClass().getPackage();

                if (pack == null) {
                    dir = classFile.getParentFile();
                }
                else {
                    // パッケージ名がある場合、パッケージの階層の分だけ上に上がる
                    String packName = pack.getName();
                    String[] words = packName.split("\\.");
                    dir = classFile.getParentFile();

                    //for (int i = 0; i < words.length; i++) {
                    for (String word : words) {
                        dir = dir.getParentFile();
                    }

                    dir = dir.getParentFile();
                }
            }
            catch (URISyntaxException ex) {
                assert false;
            }
        }

        if (dir == null) {
            assert false;
        }

        return dir.getPath();
    }
    
    /**
     * Windowsのファイルパスで禁止されている文字をハイフンに置き換える。
     * 
     * @param path ファイルパス
     * @return ファイルパス
     */
    public static String replacePath(String path) {
        // Windowsのファイルパスとして禁止されている文字は \/:?"<>|
        Pattern illegalPattern = Pattern.compile("\\\\|/|:|\\*|\\?|\"|<|>|\\|");
        return illegalPattern.matcher(path).replaceAll("-");
    }
}
