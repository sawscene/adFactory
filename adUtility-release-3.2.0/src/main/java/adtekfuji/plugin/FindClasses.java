/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.plugin;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author ke.yokoi
 */
public class FindClasses {

    private FindClasses() {
    }

    /**
     * Find a list of class from the specified package.
     *
     * @author ke.yokoi
     * @param rootPackageName specified package name
     * @return classes list
     * @throws java.io.IOException file not found
     */
    public static List<Class> find(String rootPackageName) throws IOException {
        return findImp(Arrays.asList(rootPackageName));
    }

    public static List<Class> find(String... rootPackageNames) throws IOException {
        return findImp(Arrays.asList(rootPackageNames));
    }

    public static List<Class> find(List<String> rootPackageNames) throws IOException {
        return findImp(rootPackageNames);
    }

    private static List<Class> findImp(List<String> rootPackageNames) throws IOException {
        List<Class> classes = new ArrayList<>();

        try {
            ClassLoader classLoader = PluginLoader.getClassLoarder();
            
            for (String rootPackageName : rootPackageNames) {
                String resourceName = packageNameToResourceName(rootPackageName);
                for (Enumeration<URL> e = classLoader.getResources(resourceName); e.hasMoreElements();) {
                    URL url = e.nextElement();
                    String protocol = url.getProtocol();
                    if (Objects.nonNull(protocol)) {
                        switch (protocol) {
                            case "file":
                                classes.addAll(findClassesWithFile(classLoader, rootPackageName, new File(url.getFile())));
                                break;
                            case "jar":
                                classes.addAll(findClassesWithJarFile(classLoader, rootPackageName, url));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }

        return classes;
    }

    private static String fileNameToClassName(String name) {
        return name.substring(0, name.length() - ".class".length());
    }

    private static String resourceNameToClassName(String resourceName) {
        return fileNameToClassName(resourceName).replace('/', '.');
    }

    private static boolean isClassFile(String fileName) {
        return fileName.endsWith(".class");
    }

    private static String packageNameToResourceName(String packageName) {
        return packageName.replace('.', '/');
    }

    public static List<Class> findClassesWithFile(ClassLoader classLoader, String packageName, File dir) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();

        for (String path : dir.list()) {
            File entry = new File(dir, path);
            if (entry.isFile() && isClassFile(entry.getName())) {
                classes.add(classLoader.loadClass(packageName + "." + fileNameToClassName(entry.getName())));
            } else if (entry.isDirectory()) {
                classes.addAll(findClassesWithFile(classLoader, packageName + "." + entry.getName(), entry));
            }
        }
        return classes;
    }

    public static List<Class> findClassesWithJarFile(ClassLoader classLoader, String rootPackageName, URL jarFileUrl) throws ClassNotFoundException, IOException {
        List<Class> classes = new ArrayList<>();

        JarURLConnection jarUrlConnection = (JarURLConnection) jarFileUrl.openConnection();
        try (JarFile jarFile = jarUrlConnection.getJarFile()) {
            Enumeration<JarEntry> jarEnum = jarFile.entries();
            String packageNameAsResourceName = packageNameToResourceName(rootPackageName);
            while (jarEnum.hasMoreElements()) {
                JarEntry jarEntry = jarEnum.nextElement();
                if (jarEntry.getName().startsWith(packageNameAsResourceName) && isClassFile(jarEntry.getName())) {
                    classes.add(classLoader.loadClass(resourceNameToClassName(jarEntry.getName())));
                }
            }
        }
        return classes;
    }

}
