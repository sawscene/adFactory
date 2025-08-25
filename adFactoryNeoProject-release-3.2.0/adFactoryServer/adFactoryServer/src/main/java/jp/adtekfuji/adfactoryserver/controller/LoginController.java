/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.utility.PasswordEncoder;
import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.util.Objects;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginRequest;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.enumerate.LoginAuthTypeEnum;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.service.OrganizationEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ログイン画面 コントローラー
 * 
 * @author s-heya
 */
@Named(value = "login")
@SessionScoped
public class LoginController implements Serializable {
    private final Logger logger = LogManager.getLogger();
  
    @EJB
    private OrganizationEntityFacadeREST organizationService;
    
    /**
     * ユーザー認証情報
     */
    private UserAuth auth;
  
    /**
     * パスワード
     */
    private String password;
    
    /**
     * コンストラクタ
     */
    public LoginController() {
    }

    /**
     * ログイン画面を表示される時に呼び出されます。
     */
    public void view() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        this.auth = (UserAuth) externalContext.getSessionMap().get("userAuth");
        if (Objects.isNull(this.auth)) {
            this.auth = new UserAuth();
        }
    }

    /**
     * ログインをおこなう。
     * 
     * @return 転送先ページのURI
     */
    public String login() {
        try {
            logger.info("login start.");
            
            if (StringUtils.isEmpty(this.auth.getUserId())) {
                // ユーザーIDを入力してください。
                FacesContext.getCurrentInstance().addMessage("loginFrom", new FacesMessage(LocaleUtils.getString("key.errUserId")));
                return null;
            }
            
            OrganizationLoginRequest request;
            
            String loginAuthType = FileManager.getInstance().getSystemProperties().getProperty("loginAuthType", "adFactory");
            switch (LoginAuthTypeEnum.getEnum(loginAuthType)) {
                case LDAP:
                    request = OrganizationLoginRequest.ldapType(this.auth.getUserId(), (new PasswordEncoder()).encodeAES(this.password));
                    break;
                case adFactory:
                default:
                    request = OrganizationLoginRequest.passwordType(this.auth.getUserId(), (new PasswordEncoder()).encode(this.password));
                    break;
            }
           
            // ログイン処理
            OrganizationLoginResult result = this.organizationService.login(request, false, null);
            if (!result.getIsSuccess()) {
                // 不明なエラーが発生しました。
                String key = "key.errUnknown";
                switch (result.getErrorType()) {
                    case NOT_LOGINID_ORGANIZATION:
                        // ユーザーID または パスワードが正しくありません。
                        key = "key.errAuth";
                        break;
                    case NOT_AUTH_ORGANIZATION:
                        key = "key.errAuth";
                        break;
                    default:
                        break;
                }
                
                FacesContext.getCurrentInstance().addMessage("loginFrom", new FacesMessage(LocaleUtils.getString(key)));
                return null;
            }
            
            // Session Fixation 対策
            HttpServletRequest httpRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            httpRequest.changeSessionId();
        
            // 認証成功
            this.auth.setAuthenticated(true);
            
            // 呼出元ページに戻る
            return this.auth.getRequestURI() + "?faces-redirect=true";

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        } finally {
            logger.info("login end.");
        }
    }

    /**
     * ユーザーIDを取得する。
     * 
     * @return ユーザーID
     */
    public String getUserId() {
        if (Objects.isNull(this.auth)) {
            return null;
        }
        return this.auth.getUserId();
    }

    /**
     * ユーザーIDを設定する。
     * 
     * @param userId ユーザーID
     */
    public void setUserId(String userId) {
        this.auth.setUserId(userId);
    }

    /**
     * パスワードを取得する。
     * 
     * @return パスワード 
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定する。
     * 
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
