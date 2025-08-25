/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adfactorysettingtool.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author phamvanthanh
 */
public class PropertyFileUtils {
    private static final Logger logger = LogManager.getLogger();
    private static HashMap<String, PropertyInfo> listFileContent;

    /**
     * Update some elements of property file
     *
     * @param updateData
     * @param path
     * @param fileName
     * @param fileSuffix
     * @return
     * @throws java.io.IOException
     */
    public static String updatePropertiyInfo(HashMap<String, String> updateData, String path, 
            String fileName, String fileSuffix) throws IOException {
        File file = new File(path + File.separator + fileName + fileSuffix);
        BufferedReader br=null;
        BufferedWriter bw=null;

        try {
            //Edit item of file
            for (String key : updateData.keySet()) {
                if (key != null && !"".equals(key) && !"\n".equals(key) && !"\n".equals(updateData.get(key))) {
                    listFileContent.get(fileName).edit(key, updateData.get(key));
                }
            }

            //Save to file
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(listFileContent.get(fileName).toString());
        } finally {
            if (br != null) br.close();
            if (bw != null) bw.close();
        }
        return listFileContent.get(fileName).toString();
    }

    /**
     * Read content of property file
     * 
     * @param path
     * @param fileName
     * @param fileSuffix
     * @return 
     * @throws IOException 
     */
    public static PropertyInfo readFileContent(String path, String fileName, String fileSuffix) throws IOException {
        File file = new File(path + File.separator + fileName + fileSuffix);
        String line;
        String[] lineSplit;
        BufferedReader br = null;
        PropertyInfo propertyInfo;
        
        try {
            propertyInfo = new PropertyInfo();
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                lineSplit = line.split(Constants.EQUAL_SYMBOL);
                if (lineSplit.length == 2) {
                    propertyInfo.add(lineSplit[0].trim(), lineSplit[1].trim());
                } else if (lineSplit.length > 0 && lineSplit.length < 2) {
                    propertyInfo.add(lineSplit[0].trim(), Constants.EMPTY_SYMBOL);
                } else if (lineSplit.length > 2) {
                    propertyInfo.add(lineSplit[0].trim(), 
                            line.replace((lineSplit[0] + Constants.EQUAL_SYMBOL), Constants.EMPTY_SYMBOL).trim());
                }
            }
        } finally {
            if (br != null) br.close();
        }
        
        listFileContent.put(fileName, propertyInfo);
        return propertyInfo;
    }
    
    /**
     * Read content of all properties file in the path parameter, 
     * that files has suffix is fileSuffix parameter
     * 
     * @param path
     * @param fileSuffix
     * @return
     * @throws IOException 
     */
    public static HashMap<String, PropertyInfo> readAllFilesContent(String path, final String fileSuffix) throws IOException {
        listFileContent = new HashMap<>();
        String fileName;
        BufferedReader br = null;
        BufferedWriter bw = null;    
        
        try {
            //List all files
            File dir = new File(path);
            File[] foundFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(fileSuffix));
                }
            });

            //Read content of each file
            for (File file : foundFiles) {
                fileName = file.getName().split("\\.")[0];          
                readFileContent(path, fileName, fileSuffix);
            }
            
        } catch (Exception ex) {
            logger.error("[Exception]" + ex.getMessage());
        } finally {
            if (br!=null) br.close();
            if (bw!=null) bw.close();
        }
        return listFileContent;
    }
}
