/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adtekfuji.plugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * クラスローダー
 * 
 * @author s-heya
 */
public class AppClassLoader extends URLClassLoader {

    /**
     * コンストラクタ
     * 
     * @param urls
     * @param parent 
     */
    public AppClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
    
    /**
     * クラスパスを追加する。
     * 
     * @param url 
     */
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
