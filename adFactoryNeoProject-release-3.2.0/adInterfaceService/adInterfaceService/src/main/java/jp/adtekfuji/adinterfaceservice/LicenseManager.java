/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

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

    private static LicenseManager instance;
    private long maxJoin = 0L;
    private final Map<String, Boolean> optionLicenseCollection = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, String> joinCollection = Collections.synchronizedMap(new HashMap<>());
    private Date updated = null;
    private Date expirationDate = null;

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
            instance.setMaxJoin(100);
        }
        return instance;
    }

    /**
     * 作業者端末の最大登録台数を取得する。
     *
     * @return
     */
    public long getMaxJoin() {
        return this.maxJoin;
    }

    /**
     * 作業者端末の最大登録台数を設定する。
     *
     * @param maxJoin
     */
    private void setMaxJoin(long maxJoin) {
        this.maxJoin = maxJoin;
    }

    /**
     * 作業者端末をジョインする。
     *
     * @param equipmentName
     * @param macAddress
     * @return
     */
    public boolean join(String equipmentName, String macAddress) {
        String key = equipmentName.toLowerCase();
        synchronized (this.joinCollection) {
            if (this.joinCollection.containsKey(key)) {
                String value = this.joinCollection.get(key);
                return StringUtils.equals(value, macAddress);
            } else {
                if (this.joinCollection.size() >= this.maxJoin) {
                    logger.info("Connection has been exceeded: " + equipmentName);
                    return false;
                }
                this.joinCollection.put(key, macAddress);
            }
        }
        return true;
    }

    /**
     * 接続情報を削除する。
     *
     * @param equipmentName
     */
    public void remove(String equipmentName) {
        String key = equipmentName.toLowerCase();
        synchronized (this.joinCollection) {
            if (this.joinCollection.containsKey(key)) {
                String value = this.joinCollection.get(key);
                this.joinCollection.remove(key);
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
            this.maxJoin = 0;
            this.optionLicenseCollection.clear();
            this.joinCollection.clear();

            InetAddress localhost = InetAddress.getLocalHost();
            String hostName = localhost.getHostName();

            String filePath = System.getenv("ADFACTORY_HOME") + File.separator + LicenseManager.FILENAME_LICENSE;

            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("License file does not exist.");
                return;
            }
            
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

                Enumeration enumeration = properties.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    if (key.trim().length() == 0) {
                        continue;
                    }
                    if (key.startsWith("@")) {
                        Boolean value = Boolean.valueOf(properties.getProperty(key));
                        this.optionLicenseCollection.put(key, value);
                    } else {
                        if (StringUtils.equals(hostName.toLowerCase(), key.toLowerCase())) {
                            this.maxJoin = Long.valueOf(properties.getProperty(key));
                        }
                    }
                }
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | NumberFormatException | ParseException ex) {
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
        if (this.expirationDate.before(new Date())) {
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
}
