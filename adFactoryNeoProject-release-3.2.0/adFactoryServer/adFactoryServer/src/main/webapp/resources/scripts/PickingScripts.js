/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * ロード時実行処理
 * 
 * @param {string} mode 
 */
function onPageLoad(mode) {
    var userName = document.getElementById("userInfoForm:userNameOutput").innerHTML;
    if (userName === "") {
        focusUserInfoForm();
    } else {
        if (mode === 'NORMAL') {
            focusPickingIdInfoForm();
        } else {
            focusBarcodeForm();
        }
    }
    showClock();
    document.getElementById("body").style.paddingBottom = '120px';
}

/**
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
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
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
 * 出庫指示番号フォーム フォーカスIn
 */
function focusPickingIdInfoForm() {
    var pickingIdInput = document.getElementById("pickingIdForm:pickingIdInput");
    if (pickingIdInput) {
        pickingIdInput.selectionStart = 0;
        pickingIdInput.selectionEnd = pickingIdInput.value.length;
        pickingIdInput.focus();
    }
}

/**
 * 出庫指示番号テキストボックス フォーカスIn
 */
function focusPickingIdInput() {
    document.getElementById("pickingIdForm").className = "formCommon form-focused";

}

/**
 *出庫指示番号フォーム フォーカスOut
 */
function blurPickingIdInfoForm() {
    document.getElementById("pickingIdForm").className = "formCommon form-nomal";
}

/**
 *資材情報フォーム フォーカスIn
 */
function focusBarcodeForm() {
    var barcodeInput = document.getElementById("itemInfoForm:barcodeInput");
    if (barcodeInput) {
        barcodeInput.selectionStart = 0;
        barcodeInput.selectionEnd = barcodeInput.value.length;
        barcodeInput.focus();
    }
}
/**
 * バーコードテキストボックス フォーカスIn
 */
function focusBarcodeInput() {
    document.getElementById("itemInfoForm").className = "formCommon form-focused";
}

/**
 *資材情報フォーム フォーカスOut
 */
function blurBarcodeForm() {
    document.getElementById("itemInfoForm").className = "formCommon form-nomal";
}

/**
 * 出庫数テキストボックス フォーカスIn
 */
function focusStockOutValInput() {
    var stockOutValInput = document.getElementById("stockOutForm:stockOutValInput");
    if (stockOutValInput) {
        stockOutValInput.defaultValue = stockOutValInput.value;
        //inputField.value = "";
        stockOutValInput.selectionStart = 0;
        stockOutValInput.selectionEnd = stockOutValInput.value.length;
        stockOutValInput.focus();
        stockOutValInput.click();
    }
}

/**
 * 出庫数フィールドからフォーカスが失われた。
 * 
 * @returns {undefined}
 */
function blurStockOutValInput() {
    var stockOutValInput = document.getElementById("stockOutForm:stockOutValInput");
    if (stockOutValInput && stockOutValInput.value === "") {
        stockOutValInput.value = stockOutValInput.defaultValue;
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
 * 画面遷移時警告ダイアログ表示
 * @param {string} msg
 * @param {string} language
 */
function pickingWarning(msg, language) {
    var pickingIdInput = document.getElementById("pickingIdForm:pickingIdInput");
    if (pickingIdInput && pickingIdInput.value !== "") {
        confirmExit(msg, "pickingIdInput", language);
    } else {
        exitPickingScreen();
    }
    return true;
}

/**
 * ピッキング終了確認ダイアログを表示する。
 * 
 *  @param {string} msg
 *  @param {string} focus
 *  @param {string} language
 */
function confirmExit(msg, focus, language) {
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
                    exitPickingScreen();
                    location.href = "index.xhtml";
                    confirmBox.close();
                }
            },
            cancel: {
                text: language === "ja" ? "キャンセル" : "Cancel",
                keys: ["esc"],
                action: function(){
                    focusChange(focus);
                    confirmBox.close();
                }
            }
        }
    });
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
 * 入力値確認（数値以外は入力不可）
 */
function numOnly() {
    var key = event.key;
    if ((key === 'Enter') || (key === '0') || (key === '1') || (key === '2') || (key === '3') || (key === '4') || (key === '5')
            || (key === '6') || (key === '7') || (key === '8') || (key === '9')
            || (key === 'Tab') || (key === 'ArrowUp') || (key === 'ArrowDown')
            || (key === 'Backspace') || (key === 'Delete') || (key === 'ArrowRight') || (key === 'ArrowLeft')) {
        return true;
    }
    return false;
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
 *  @param {string} focus
 */
function focusChange(focus) {
    var userName = document.getElementById("userInfoForm:userNameOutput").innerHTML;
    var pickingIdInput = document.getElementById("pickingIdForm:pickingIdInput");
    var pickingId = pickingIdInput ? pickingIdInput.value : undefined;
    var barcodeInput = document.getElementById("itemInfoForm:barcodeInput");
    var barcode = barcodeInput ? barcodeInput.value : undefined;

    switch (focus) {
        case "userIdInput":
            focusUserInfoForm();
            break;

        case "pickingIdInput":
            if (userName === "") {
                focusUserInfoForm();
            } else {
                focusPickingIdInfoForm();
            }
            break;

        case "BarcodeInput":
            if (userName === "") {
                focusUserInfoForm();
            } else if (pickingId === "") {
                focusPickingIdInfoForm();
            } else {
                focusBarcodeForm();
            }
            break;

        case "StockOutValInput":
            if (userName === "") {
                focusUserInfoForm();
            } else if (pickingId === "") {
                focusPickingIdInfoForm();
            } else if (barcode === "") {
                focusBarcodeForm();
            } else {
                focusStockOutValInput();
            }
            break;

        default :
            var userIdInput = document.getElementById("userInfoForm:userIdInput");
            if (userIdInput) {
                focusUserInfoForm();
            } else {
                focusItemInfoForm();
            }
            break;
    }
}

function soundPlay() {
    // バイブレーション
    window.navigator.vibrate([200, 200, 200, 200, 200]);
    //「pippi」
    document.getElementById('sound').play();
}

/**
 * ラベル印刷チェックボックスCookie登録
 */
function writePrintFlgCookie() {
    // 前回値削除
    document.cookie = "pickingPrintFlg=; expires=0";

    // 有効期限設定(30日間)
    var now = new Date();
    now.setDate(now.getDate() + 30);
    now.setHours(23);
    now.setMinutes(59);
    now.setSeconds(59);

    var cookieValue = document.getElementById("buttonForm:checkBox").checked;
    document.cookie = "pickingPrintFlg=" + encodeURIComponent(cookieValue) + ";expires=" + now.toUTCString();
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
 * 出庫数確認ダイアログを表示する。
 * 
 * @param {type} requiredNum
 * @param {type} deliveryNum
 * @param {type} language 
 * @returns {undefined}
 */
function confirmDeliveryNum(requiredNum, deliveryNum, language) {
    var content_ja = "出庫数が間違っています。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;要求数:&nbsp;" + requiredNum + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;出庫数:&nbsp;" + deliveryNum + "<br/><br/>";
    var content_en = "Incorrect number of deliveries.<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Requested quantity:&nbsp;" + requiredNum + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;Entered quantity:&nbsp;" + deliveryNum + "<br/><br/>";
            
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
                    // 出庫数にフォーカスを当てる
                    focusStockOutValInput();
                    confirmBox.close();
                }
            }
        }
    });
}

/**
 * 資材情報フォーム フォーカスIn
 */
function focusItemInfoForm() {
    var inputField = document.getElementById("itemInfoForm:barcodeInput");
    if (!inputField) {
        return;
    }
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
    inputField.focus();
}

/**
 * ポップアップメッセージを表示する。
 * 
 * @returns
 */
function popupBox(msg) {
    var label = document.getElementById("popupText");
    if (label) {
        label.innerHTML = msg;
    }
    new popup($("#popupBox"), $("#container")).load();
}

function popup(popup ,container) {
    var thisPopup = this,
        timer,
        counter = 2;

    thisPopup.load = function() {
        container.animate({
            "opacity": "0.3"
        },250, function() {           
            popup.fadeIn("250");            
        });

        container.off("click").on("click", function() {
            thisPopup.unload();
        });

        timer = setInterval(function() {
            counter--;
            if(counter < 0) {
                thisPopup.unload();
            }
        }, 1000);
    };

    thisPopup.unload = function() {
        clearInterval(timer);
        popup.fadeOut("250", function(){
            container.animate({
                "opacity": "1"
            }, 250);
        });
    };
}
