/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * 時刻を表示する。
 */
function showClock() {
    var request = new XMLHttpRequest();

    request.open('HEAD', window.location.href, true);
    request.send();
    request.onreadystatechange = function () {
        try {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    var date = new Date(request.getResponseHeader('Date'));
                    var nowHour = setfig(date.getHours());
                    var nowMin = setfig(date.getMinutes());
                   
                    // 日付をローカライズ
                    var str = date.toLocaleDateString() + " " + nowHour + ":" + nowMin;

                    document.getElementById("titleForm:timeLabel").innerHTML = str;
                } else {
                    document.getElementById("titleForm:timeLabel").innerHTML = "Failure";
                }
            }
        } catch (e) {
            document.getElementById("titleForm:timeLabel").innerHTML = "Failure";
        }
    };
}
setInterval('showClock()', 60000);

/**
 * クッキーにユーザー情報を保存する。
 * 
 * @param {type} userId
 */
function writeUserIdCookie(userId) {
    document.cookie = "userId=; expires=0";

    // 有効期限設定(100日間)
    var now = new Date();
    now.setDate(now.getDate() + 100);
    now.setHours(23);
    now.setMinutes(59);
    now.setSeconds(59);

    document.cookie = "userId=" + encodeURIComponent(userId) + ";expires=" + now.toUTCString();
}

/**
 * 入力フィールドをフォーカスを設定する。
 * 
 * @param name 要素名
 */
function focusTextFiled(name) {
    var inputField = document.getElementById(name);
    if (inputField.value) {
        inputField.defaultValue = inputField.value;
        inputField.selectionStart = 0;
        inputField.selectionEnd = inputField.value.length;
    }
    inputField.focus();
}

/**
  * フォームからフォーカスを設定する。
  * 
  * @param name フォーム名
  */
function blurForm(name) {
    var form = document.getElementById(name);
    if (form) {
        form.className = "formCommon form-nomal";
    }
}
    

/**
 * 受入検査の確認ダイアログを表示する。
 * 
 * @param {type} language
 * @returns {undefined}
 */
function confirmInspection(language) {
    var content_ja = "検査済みです。<br/>再度、検査を実施しますか?<br/><br/>";
    var content_en = "Inspection completed.<br/>Perform the inspection again?<br/><br/>";
 
    var confirmBox = $.confirm({
        title: false,
        content: language === "ja" ? content_ja : content_en,
        boxWidth: "800px",
        useBootstrap: false,
        buttons: {
            confirm: {
                text: language === "ja" ? "はい" : "Yes",
                keys: ["enter"],
                action: function(){
                    focusTextFiled('registForm:defectNumInput');
                    confirmBox.close();
                }
            },
            cancel: {
                text: language === "ja" ? "いいえ" : "No",
                keys: ["esc"],
                action: function(){
                    confirmMessage();
                    focusTextFiled('itemInfoForm:barcodeInput');
                    confirmBox.close();
                }
            }
        }
    });
}

function onLoadFinisedStock() {
    var userName = document.getElementById("userInfoForm:userNameOutput").innerHTML;
    if (userName === "") {
        focusUserInfoForm();
    } else {
        var productNoInput = document.getElementById("productNoForm:productNoInput");
        if (productNoInput.value === "") {
            productNoInput.focus();
            return;
        }
        var barcodeInput = document.getElementById("itemInfoForm:barcodeInput");
        if (barcodeInput) {
            barcodeInput.focus();
        }
    }
    showClock();
    document.getElementById("body").style.paddingBottom = '120px';
}

function onLoadPickKanban() {
    var userNameOutput = document.getElementById("userInfoForm:userNameOutput")
    if (userNameOutput && userNameOutput.value === "") {
        focusUserInfoForm();
    } else {
        var deliveryNoInput = document.getElementById("inputForm:deliveryNoInput")
        if (deliveryNoInput) {
            deliveryNoInput.focus();
        }
    }
    showClock();
    document.getElementById("body").style.paddingBottom = '120px';
}

/**
 * メニュー画面に戻る。
 * 
 * @param {string} msg
 * @param {string} language 
 * @param {string} page
 * @returns {undefined}
 */
function goBack(msg, language, page) {
    if (msg !== "") {
        var res = customConfirm(msg, '', language, page);
        if (res === false) {
            return false;
        }
    } else {
        location.href = page ? page : "index.xhtml";
    }
}

/**
 * ページを最下部までスクロールする。
 * 
 * @returns {undefined}
 */
function scrollToBottom() {
    var element = document.documentElement;
    var bottom = element.scrollHeight - element.clientHeight;
    window.scroll(0, bottom);
}