/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import adtekfuji.utility.CipherHelper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ライセンス管理クラス
 *
 * @author s-heya
 */
public class LicenseManager {

    private final Logger logger = LogManager.getLogger();

    private static final byte[] AES_KEY = {(byte) 0x97, (byte) 0xa2, (byte) 0xd4, 0x26, (byte) 0xe2, (byte) 0xe8, (byte) 0xac, 0x52, (byte) 0xe5, 0x7f, 0x5c, 0x0a, 0x1a, 0x48, 0x5a, 0x67};
    private static final String AES_ALGORITHM = "AES_128/CBC/NOPADDING";
    private static final String FILENAME_LICENSE = "conf" + File.separator + "adFactory.license";
    private static final String LIC_WEAK = "WEAK";

    private static LicenseManager instance;
    private long maxJoinTerminal = 0L;
    private long maxJoinLite = 0L;
    private long maxJoinReporter = 0L;
    private final Map<String, String> joinTerminal = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, String> joinLite = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, String> joinReporter = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Boolean> optionLicenseCollection = Collections.synchronizedMap(new HashMap<>());
    private Date updated = null;
    private Date expirationDate = null;

    final String KEY_LITE_OPTION = "@LiteOption";
    final String KEY_LITE_LIC = "@LiteLic";
    final String KEY_REPORTER_OPTION = "@ReporterOption";
    final String KEY_REPORTER_LIC = "@ReporterLic";
        
    /**
     * コンストラクタ
     */
    private LicenseManager() {
        try {
            String day = "2016-01-01";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.updated = sdf.parse(day);
        } catch (Exception ex) {
        }
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static LicenseManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new LicenseManager();
        }
        instance.initialize();
        return instance;
    }

    /**
     * テスト用にライセンス管理を初期化する。
     *
     * @return
     */
    public static LicenseManager setupTest() {
        if (Objects.isNull(instance)) {
            instance = new LicenseManager();
            instance.initialize();
            instance.setMaxJoinTerminal(100);
        }
        return instance;
    }

    /**
     * 作業者端末のライセンス数を取得する。
     *
     * @return 作業者端末の最大登録台数
     */
    public long getMaxJoinTerminal() {
        return this.maxJoinTerminal;
    }

    /**
     * 作業者端末のライセンス数を設定する。
     *
     * @param maxJoinTerminal 作業者端末のライセンス数
     */
    private void setMaxJoinTerminal(long maxJoinTerminal) {
        this.maxJoinTerminal = maxJoinTerminal;
    }

    /**
     * Lite ライセンス数を取得する。
     *
     * @return Lite ライセンス数
     */
    public long getMaxJoinLite() {
        return this.getLicenseOption(KEY_LITE_OPTION) ? maxJoinLite : 0;
    }

    /**
     * Reporter ライセンス数を取得する。
     *
     * @return Reporter ライセンス数
     */
    public long getMaxJoinReporter() {
        return this.getLicenseOption(KEY_REPORTER_OPTION) ? maxJoinReporter : 0;
    }

    /**
     * 作業者端末をジョインする。
     *
     * @param equipmentName 設備識別子名
     * @param macAddress MACアドレス
     * @param type 設備種別
     * @return
     */
    public boolean join(String equipmentName, String macAddress, EquipmentTypeEnum type) {
        if (Objects.isNull(type)) {
            return false;
        }
    
        String key = equipmentName.toLowerCase();
        synchronized (joinTerminal) {

            Map<String, String> joinMap = null;
            long maxJoin = 0;

            switch (type) {
                case TERMINAL:
                    maxJoin = this.maxJoinTerminal;
                    joinMap = this.joinTerminal;
                    break;
                case LITE:
                    if (!this.isLicenceOption(KEY_LITE_OPTION)) {
                        return false;
                    }
                    
                    maxJoin = this.maxJoinLite;
                    joinMap = this.joinLite;
                    break;
                case REPORTER:
                    if (!this.isLicenceOption(KEY_REPORTER_OPTION)) {
                        return false;
                    }

                    maxJoin = this.maxJoinReporter;
                    joinMap = this.joinReporter;
                    break;
                default:
                    return false;
            }

            if (joinMap.containsKey(key)) {
                Properties properties = FileManager.getInstance().getSystemProperties();
                if (LIC_WEAK.equals(properties.getProperty("licLevel", ""))) {
                    // ライセンス認証を緩くする
                    logger.info("Weak license authentication.");
                    return true;
                }

                String value = joinMap.get(key);
                return StringUtils.equals(value, macAddress);
            } else {
                if (joinMap.size() >= maxJoin) {
                    logger.info("Connection has been exceeded: name={} type={} max={} join={}", equipmentName, type, maxJoin, joinMap.size());
                    return false;
                }
                joinMap.put(key, macAddress);
            }
        }
        return true;
    }

    /**
     * 接続情報を削除する。
     *
     * @param equipmentName
     */
    public void remove(String equipmentName, EquipmentTypeEnum equipmentType) {
        String name = equipmentName.toLowerCase();
        synchronized (joinTerminal) {
            switch (equipmentType) {
                case TERMINAL:
                    if (this.joinTerminal.containsKey(name)) {
                        this.joinTerminal.remove(name);
                    }
                    break;
                case LITE:
                    if (this.joinLite.containsKey(name)) {
                        this.joinLite.remove(name);
                    }
                    break;
                case REPORTER:
                    if (this.joinReporter.containsKey(name)) {
                        this.joinReporter.remove(name);
                    }
                    break;
            }
        }
    }

    /**
     * ライセンス管理を初期化する。
     */
    private void initialize() {
        
        try {
            Date now = new Date();
            Date day = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
            if (!day.after(this.updated)) {
                return;
            }

            this.updated = day;
            this.maxJoinTerminal = 0;
            this.maxJoinLite = 0;
            this.maxJoinReporter = 0;
            this.joinTerminal.clear();
            this.joinLite.clear();
            this.joinReporter.clear();
            this.optionLicenseCollection.clear();

            InetAddress localhost = InetAddress.getLocalHost();
            String hostName = localhost.getHostName();

            String filePath = System.getenv("ADFACTORY_HOME") + File.separator + LicenseManager.FILENAME_LICENSE;

            File file = new File(filePath);
            try (FileInputStream stream = new FileInputStream(file)) {

                byte[] iv = new byte[16];
                int readBytes = stream.read(iv, 0, 16);

                byte[] buffer = new byte[(int) file.length() - readBytes];
                stream.read(buffer, 0, buffer.length);

                String source = CipherHelper.decrypt(buffer, AES_KEY, iv, AES_ALGORITHM);

                Properties properties = new Properties();
                properties.load(new ByteArrayInputStream(source.getBytes("ISO-8859-1")));

                // ライセンス有効期限は設定日の23:59:59.999まで
                String expiration = properties.getProperty("@Expiration");
                this.expirationDate = DateUtils.parseDate(expiration + " 23:59:59.999", new String[]{"yyyy/MM/dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSS"});
                logger.info("License expiration date: " + this.expirationDate);

                if (this.expirationDate.before(new Date())) {
                    // ライセンスの有効期限が切れている
                    return;
                }

                properties.remove("@Expiration");
                
                String contract = properties.getProperty("@Contract");
                if (!StringUtils.isEmpty(contract)) {
                    Date date = DateUtils.parseDate(contract + " 23:59:59.999", new String[]{"yyyy/MM/dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSS"});
                    if (date.after(now)) {
                        this.optionLicenseCollection.put("adWorkbookAddIn", true);
                    }
                }
                properties.remove("@Contract");
                logger.info("Contract expiration date: " + contract);

                Enumeration enumeration = properties.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    if (key.trim().length() == 0) {
                        continue;
                    }
                    
                    if (key.startsWith(KEY_LITE_LIC)) {
                        // 作業者端末(Lite) ライセンス数
                        try {
                            this.maxJoinLite = Long.valueOf(properties.getProperty(key));
                        } catch (Exception e) {
                        }
                        continue;
                    }
                    
                    if (key.startsWith(KEY_REPORTER_LIC)) {
                        // 作業者端末(Reporter) ライセンス数
                        try {
                            this.maxJoinReporter = Long.valueOf(properties.getProperty(key));
                        } catch (Exception e) {
                        }
                        continue;
                    }

                    if (key.startsWith("@")) {
                        // オプションライセンス
                        Boolean value = Boolean.valueOf(properties.getProperty(key));
                        this.optionLicenseCollection.put(key, value);
                        continue;
                    }
                    
                    if (StringUtils.equals(hostName.toLowerCase(), key.toLowerCase())) {
                        // 作業者端末 ライセンス数
                        this.maxJoinTerminal = Long.valueOf(properties.getProperty(key));
                    }
                }
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | ParseException | NumberFormatException ex) {
            logger.fatal(ex.getMessage());
        }
    }

    /**
     * 指定されたオプションがライセンス許可されているか
     *
     * @param key
     * @return
     */
    public boolean isLicenceOption(String key) {
        if (Objects.isNull(this.expirationDate) || this.expirationDate.before(new Date())) {
            // ライセンスの有効期限が切れている
            return false;
        }
        return this.optionLicenseCollection.getOrDefault(key, false);
    }

    /**
     * ライセンス期限取得
     *
     * @return 期限
     */
    public Date getLicenseDate() {
        return this.expirationDate;
    }

    /**
     * オプション設定取得
     *
     * @param key
     * @return true/false
     */
    public Boolean getLicenseOption(String key) {
        return this.optionLicenseCollection.getOrDefault(key, false);
    }

    /**
     * オプション設定取得
     *
     * @return コレクション
     */
    public Map<String, Boolean> getLicenseOptions() {
        return this.optionLicenseCollection;
    }
    
    /**
     * ライセンス数をチェックする。
     * 
     * @param equipmentType 設備種別情報
     * @param count 設備登録数
     * @return true: OK、false: NG
     */
    public boolean checkLicense(EquipmentTypeEntity equipmentType, long count) {
        if (Objects.isNull(equipmentType) || Objects.isNull(equipmentType.getName())) {
            return false;
        }
        
        switch (equipmentType.getName()) {
            case TERMINAL:
                return this.maxJoinTerminal <= count;
            case LITE:
                if (!optionLicenseCollection.containsKey(KEY_LITE_OPTION) || !optionLicenseCollection.get(KEY_LITE_OPTION)) {
                    return false;
                }
                return this.maxJoinLite <= count;
            case REPORTER:
                if (!optionLicenseCollection.containsKey(KEY_REPORTER_OPTION) || !optionLicenseCollection.get(KEY_REPORTER_OPTION)) {
                    return false;
                }
                return this.maxJoinReporter <= count;
            default:
                break;
        }
        
        return false;
    }
}
