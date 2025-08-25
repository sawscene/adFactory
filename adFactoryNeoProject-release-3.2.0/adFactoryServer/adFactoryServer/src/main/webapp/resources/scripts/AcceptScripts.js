/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * ロード時実行処理
 */
function onPageLoad() {
    var userName = document.getElementById("userInfoForm:userNameOutput").innerHTML;
    if (userName === "") {
        focusUserInfoForm();
    } else {
        var form = document.getElementById("statementForm");
        if (!form) {
            focusItemInfoForm();
        } else {
            focusStatementForm();
        }
    }
    showClock();
    document.getElementById("body").style.paddingBottom = '120px';
}

/*
 * ajax実行時エラー処理（セッションタイムアウト）
 */
function ajaxError() {
    location.href = "index.xhtml";
}

/**
 * 社員情報フォーム フォーカスIn
 */
function focusUserInfoForm() {
    var inputField = document.getElementById("userInfoForm:userIdInput");
    if (inputField) {
        inputField.selectionStart = 0;
        inputField.selectionEnd = inputField.value.length;
        inputField.focus();
    }
}

/**
 * 社員番号テキストボックス フォーカスIn
 */
function focusUserIdInput() {
    document.getElementById("userInfoForm").className = "formCommon form-focused";
}

/**
 * 社員情報フォーム フォーカスOut
 */
function blurUserInfoForm() {
    document.getElementById("userInfoForm").className = "formCommon form-nomal";
}

/**
 * 納品書フォーム フォーカスIn
 */
function focusSupplyNoForm() {
    var inputField = document.getElementById("supplyNoForm:supplyNoInput");
    if (inputField.value) {
        inputField.selectionStart = 0;
        inputField.selectionEnd = inputField.value.length;
    }
    inputField.focus();
}

/**
 * 納品書テキストボックス フォーカスIn
 */
function focusSupplyNoInput() {
    document.getElementById("supplyNoForm").className = "formCommon form-focused";

}

/**
 * 納品書フォーム フォーカスOut
 */
function blurSupplyNoForm() {
    document.getElementById("supplyNoForm").className = "formCommon form-nomal";
}

/**
 * バーコード入力フィールドにフォーカルを設定する。
 */
function focusItemInfoForm() {
    var inputField = document.getElementById("itemInfoForm:barcodeInput");
    if (!inputField) {
        return;
    }
    if (inputField.value) {
        inputField.selectionStart = 0;
        inputField.selectionEnd = inputField.value.length;
    }
    inputField.focus();
}

/**
 * ラベル発行画面のバーコード入力フィールドにフォーカスを設定する。
 */
function focusReprintBarcode() {
    var inputField = document.getElementById("itemInfoForm:barcodeInput");
    if (inputField.value) {
        inputField.selectionStart = 0;
        inputField.selectionEnd = inputField.value.length;
        inputField.focus();
    }
}

/**
 * バーコードテキストボックス フォーカスIn
 */
function focusBarcodeInput() {
    document.getElementById("itemInfoForm").className = "formCommon form-focused";
}

/**
 * 資材情報フォーム フォーカスOut
 */
function blurItemInfoForm() {
    document.getElementById("itemInfoForm").className = "formCommon form-nomal";
}

/**
 * 棚番号フィールドにフォーカスを設定する。
 */
function focusLocationNoInput() {
    var inputField = document.getElementById("registForm:locationNoInput");
    inputField.focus();
}

/**
 * 棚番号(確認用)フィールドにフォーカスを設定する。
 */
function focusCconfirmLocationNoInput() {
    var inputField = document.getElementById("registForm:confirmLocationNoInput");
    inputField.focus();
}

/**
 * 入庫数テキストボックスフォーカスIn
 */
function focusReceivedNumInput() {
    var inputField = document.getElementById("registForm:receivedNumInput");
    inputField.defaultValue = inputField.value;
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
    inputField.click();
}

/**
 * 入庫数フィールドからフォーカスが失われた。
 * 
 * @returns {undefined}
 */
function blurReceivedNumInput() {
    var inputField = document.getElementById("registForm:receivedNumInput");
    if (inputField.value === "") {
        inputField.value = inputField.defaultValue;
    }
}

/**
 * 移動数テキストボックスのフォーカス処理
 */
function focusMoveNumInput() {
    var inputField = document.getElementById("registForm:moveNumInput");
    inputField.defaultValue = inputField.value;
    //inputField.value = "";
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
    inputField.click();
}

/**
 * 移動数フィールドからフォーカスが失われた。
 * 
 * @returns {undefined}
 */
function blurMoveNumInput() {
    var inputField = document.getElementById("registForm:moveNumInput");
    if (inputField.value === "") {
        inputField.value = inputField.defaultValue;
    }
}

/**
 * 棚卸在庫数テキストボックスのフォーカス処理
 */
function focusInventoryNumInput() {
    var inputField = document.getElementById("registForm:inventoryNumInput");
    inputField.defaultValue = inputField.value;
    //inputField.value = "";
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
    inputField.click();
}

/**
 * 棚卸在庫数フィールドからフォーカスが失われた。
 * 
 * @returns {undefined}
 */
function blurInventoryNumInput() {
    var inputField = document.getElementById("registForm:inventoryNumInput");
    if (inputField.value === "") {
        inputField.value = inputField.defaultValue;
    }
}

/**
 * 棚番訂正テキストボックスのフォーカス処理
 */
function focusInventoryLocInput() {
    var inputField = document.getElementById("registForm:inventoryLocInput");
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
    inputField.click();
}

/**
 * 入力フィールドにフォーカスを設定する。
 * @param {type} id
 */
function focusInput(id) {
    var inputField = document.getElementById(id);
    inputField.defaultValue = inputField.value;
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
    inputField.click();
}

/**
 * 仕分数フィールドからフォーカスが失われた。
 * 
 * @param {type} id
 */
function blurInput(id) {
    var inputField = document.getElementById(id);
    if (inputField.value === "") {
        inputField.value = inputField.defaultValue;
    }
}

/**
 * 登録ボタンにフォーカスを設定する。
 * 
 * @returns {undefined}
 */
function focusSubmitButton() {
    document.getElementById("buttonForm:submitButton").focus();
}

/**
 * 1桁の数値をを2桁に調整
 * @param {number} num
 */
function setfig(num) {
    // 桁数が1桁のとき先頭に０を加えて2桁に調整
    var ret;
    if (num < 10) {
        ret = "0" + num;
    } else {
        ret = num;
    }
    return ret;
}

/**
 * ラベル印刷設定をCookieに書き込む。
 * 
 * @param {type} key
 */
function writePrintFlgCookie(key) {
    //前回値削除
    document.cookie = key + "=; expires=0";

    //有効期限設定(30日間)
    var now = new Date();
    now.setDate(now.getDate() + 30);
    now.setHours(23);
    now.setMinutes(59);
    now.setSeconds(59);

    var cookieValue = document.getElementById("buttonForm:checkBox").checked;
    document.cookie = key + "=" + encodeURIComponent(cookieValue) + ";expires=" + now.toUTCString();
}

/**
 * 画面遷移時警告ダイアログ表示
 * @param {string} msg
 * @param {string} language 
 */
function warning(msg, language) {
    var field = document.getElementById("itemInfoForm:barcodeInput");
    if (!field) {
        location.href = "index.xhtml";
        return;
    }
    if (field.value !== "") {
        var res = customConfirm(msg, "", language)
        if (res === false) {
            // キャンセルの場合は画面を遷移しない
            return false;
        }
    } else {
        location.href = "index.xhtml";
    }
}

/**
 * 入力値確認（数値以外は入力不可）
 */
function numOnly() {
    var key = event.key;
    if ((key === 'Enter') || (key === '0') || (key === '1') || (key === '2') || (key === '3') || (key === '4') || (key === '5')
            || (key === '6') || (key === '7') || (key === '8') || (key === '9') || (key === '-')
            || (key === 'Tab') || (key === 'ArrowUp') || (key === 'ArrowDown')
            || (key === 'Backspace') || (key === 'Delete') || (key === 'ArrowRight') || (key === 'ArrowLeft')) {
        return true;
    }
    if (typeof key === 'undefined') {
        document.getElementById("registForm:receivedNumInput").blur();
    }
    return false;
}

/**
 * エラー・警告時 音声・バイブレーション出力
 */
function soundPlay() {
    // バイブレーション
    window.navigator.vibrate([200, 200, 200, 200, 200]);
    //「pippi」
    document.getElementById('sound').play();
}

/**
 * イベントリスナー
 */
document.addEventListener('DOMContentLoaded', function () {
    history.pushState(null, null, null);

    window.addEventListener('popstate', function () {
        history.pushState(null, null, null);
    });
});

/**
 * フォーカス遷移
 *  @param {string} name
 */
function focusChange(name) {
    var userName = document.getElementById("userInfoForm:userNameOutput").innerHTML;
    var supplyCode = document.getElementById("itemInfoForm:barcodeInput").value;

    switch (name) {
        case "userIdInput":
            focusUserInfoForm();
            break;

        case "BarcodeInput":
            if (userName === "") {
                focusUserInfoForm();
            } else {
                focusItemInfoForm();
            }
            break;

        case "locationNoInput":
            if (userName === "") {
                focusUserInfoForm();
            } else if (supplyCode === "") {
                focusItemInfoForm();
            } else {
                focusLocationNoInput();
            }
            break;

        case "ReceivedNumInput":
            if (userName === "") {
                focusUserInfoForm();
            } else if (supplyCode === "") {
                focusItemInfoForm();
            } else {
                focusReceivedNumInput();
            }
            break;

        case "iventoryLoc":
            focusInventoryLocInput();
            break;

        default :
            var field = document.getElementById(name);
            if (field) {
                field.focus();
                return;
            }
            
            var userIdInput = document.getElementById("userInfoForm:userIdInput");
            if (userIdInput) {
                focusUserInfoForm();
            } else {
                focusItemInfoForm();
            }
            break;
    }
}

/**
 * 登録ボタンをクリックする。
 * 
 * @returns {undefined}
 */
function clickSubmitButton() {
    var button = document.getElementById("buttonForm:submitButton");
    button.click();
}

/**
 * 受入数確認ダイアログを表示する。
 * 
 * @param {type} planedNum
 * @param {type} receivedNum
 * @param {type} language 
 * @returns {undefined}
 */
function confirmReciveNum(planedNum, receivedNum, language) {
    var content_ja = "入庫数が間違っています。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;納入予定数:&nbsp;" + planedNum + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;入庫数:&nbsp;" + receivedNum + "<br/><br/>";
    var content_en = "Incorrect number of entries.<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Requested quantity:&nbsp;" + planedNum + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;Entered quantity:&nbsp;" + receivedNum + "<br/><br/>";
 
    var confirmBox = $.confirm({
        title: false,
        content: language === "ja" ? content_ja : content_en,
        boxWidth: "800px",
        useBootstrap: false,
        buttons: {
            confirm: {
                text: language === "ja" ? "登録" : "Regist",
                keys: ["enter"],
                action: function(){
                    clickSubmitButton();
                    confirmBox.close();
                }
            },
            cancel: {
                text: language === "ja" ? "修正" : "Modify",
                keys: ["esc"],
                action: function(){
                    focusReceivedNumInput();
                    confirmBox.close();
                }
            }
        }
    });
}

function focusPrintButton() {
    var text_value = toHankaku($("input#printNumInput").val());
    if (text_value > 0 && text_value !== "") {
        document.getElementById("buttonForm:printReq").focus();
    }
}

