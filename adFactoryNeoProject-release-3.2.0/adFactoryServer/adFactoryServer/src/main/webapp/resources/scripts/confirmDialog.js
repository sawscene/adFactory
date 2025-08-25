/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 警告ダイアログを表示する。
 * 
 *  @param {string} msg
 *  @param {string} focus
 */
function customAlert(msg, focus) {
    var alertBox = $.alert({
        title: false,
        content: msg + "<br/>",
        boxWidth: "800px",
        useBootstrap: false,
        buttons: {
            confirm: {
                text: "OK",
                keys: ["enter","esc"],
                action: function(){
                    focusChange(focus);
                    alertBox.close();
                }
            }
        }
    });
}

/**
 * 確認ダイアログを表示する。
 * 
 *  @param {string} msg
 *  @param {string} focus
 *  @param {string} language 
 *  @param {string} page
 */
function customConfirm(msg, focus, language, page) {
    var confirmBox = $.confirm({
        title: false,
        content: msg + "<br/>",
        boxWidth: "800px",
        useBootstrap: false,
        buttons: {
            confirm: {
                text: "OK",
                keys: ["enter"],
                action: function(){
                    location.href = page ? page : "index.xhtml";
                    confirmBox.close();
                }
            },
            cancel: {
                text: language === "ja" ? "キャンセル" : "Cancel",
                keys: ["esc"],
                action: function(){
                    if (focus !== "") {
                        focusChange(focus);
                    }
                    confirmBox.close();
                }
            }
        }
    });
}
