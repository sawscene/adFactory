/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.common;

/**
 * 組織インポート情報
 *
 * @author nar-nakamura
 */
public class ImportOrganizationData {

    private String organizationIdentify;    // 組織名
    private String organizationName;        // 組織識別名
    private String parentIdentify;          // 親組織識別名
    private String mailAddress;             // メールアドレス

    /**
     * コンストラクタ
     */
    public ImportOrganizationData() {
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationIdentify() {
        return this.organizationIdentify;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationIdentify 組織名
     */
    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationName 組織識別名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * 親組織識別名を取得する。
     *
     * @return 親組織識別名
     */
    public String getParentIdentify() {
        return this.parentIdentify;
    }

    /**
     * 親組織識別名を設定する。
     *
     * @param parentIdentify 親組織識別名
     */
    public void setParentIdentify(String parentIdentify) {
        this.parentIdentify = parentIdentify;
    }

    /**
     * メールアドレスを取得する。
     * 
     * @return メールアドレス 
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * メールアドレスを設定する。
     * 
     * @param mailAddress メールアドレス
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("ImportOrganizationData{")
                .append("organizationIdentify=").append(this.organizationIdentify)
                .append(", organizationName=").append(this.organizationName)
                .append(", parentIdentify=").append(this.parentIdentify)
                .append(", mailAddress=").append(this.mailAddress)
                .append("}")
                .toString();
    }
}
