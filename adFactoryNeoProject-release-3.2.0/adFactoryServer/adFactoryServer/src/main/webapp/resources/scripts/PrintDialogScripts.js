/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * ロード時実行処理
 */
function onPageLoad() {
    var printLog = document.getElementById("hiddenForm:printLog");
    var dialogFlg = document.getElementById("hiddenForm:hiddenDialog");
    if (!printLog || !dialogFlg) {
        return;
    }
    if (printLog.value !== "" && dialogFlg.value === "false"){
        document.getElementById("buttonForm:printMsg").innerHTML = "Failed to send print request.";
    }
    document.getElementById("printNumInput").focus();
}

/**
 * 入力値確認（数値以外は入力不可）
 */
function numOnly() {
    var key = event.key;
    if ((key === 'Enter') || (key === '0') || (key === '1') || (key === '2') || (key === '3') || (key === '4') || (key === '5')
            || (key === '6') || (key === '7') || (key === '8') || (key === '9') || (key === 'Tab')
            || (key === 'ArrowUp') || (key === 'ArrowDown')
            || (key === 'Backspace') || (key === 'Delete') || (key === 'ArrowRight') || (key === 'ArrowLeft')) {
        return true;
    }
    return false;
}

/**
 * 全角を半角に変換する。
 * 
 * @param {string} value
 */
function toHankaku(value) {
    return value.replace(/[０-９]/g, function(s) {
        return String.fromCharCode(s.charCodeAt(0) - 0xFEE0);
    });
}

/**
 * 部数入力コンボボックス　フォーカスIn
 */
function focusPrintNum(){
    /*値を初期化しないとリストが正常に表示されない*/
    //document.getElementById("printNumInput").value = "";
    var inputField = document.getElementById("printNumInput");
    inputField.selectionStart = 0;
    inputField.selectionEnd = inputField.value.length;
}

/**
 * 部数入力コンボボックス
 */
$(function () {
    $("#printNumInput").on('input', function () {
        var text_value = $(this).val();

        var pattern = new RegExp("[0-9０-９]");
        var result = text_value.match(pattern);
        
        if (result === null) {
            $(this).val("");
            text_value = $(this).val();
        }
       if (text_value === "" || text_value === "0"){
            document.getElementById("buttonForm:printReq").disabled = true;
            document.getElementById("buttonForm:printMsg").innerHTML = "Enter the number of copies.";

        } else {
            document.getElementById("buttonForm:printReq").disabled = false;
            document.getElementById("buttonForm:printMsg").innerHTML = "";
        }
    });
});

/**
 * フォーカス遷移
 */
function onKeyupPrintNum() {
    var m = event.key;
    var text_value = toHankaku($("input#printNumInput").val());
    if(typeof m === 'undefined' && text_value > 0 && text_value !== ""){
        document.getElementById("buttonForm:printReq").focus();
    }
}

function focusOrderNoList() {
    document.getElementById("buttonForm:orderNoList").focus();
}

/**
 * 印刷ボタンにフォーカスを当てる。
 */
function focusPrintButton() {
    var text_value = toHankaku($("input#printNumInput").val());
    if (text_value > 0 && text_value !== "") {
        document.getElementById("buttonForm:printReq").focus();
    }
}

/**
 * HTTPリクエストを同期送信
 * 
 * @param {type} url
 * @param {type} language
 * @returns {undefined}
 */
function sendRequest(url, language) {
    try {
        var request = new XMLHttpRequest();
        request.responseType = '';
        request.open('GET', url, false);
        request.send();
        
        var response = request.responseXML;
        var result = response.getElementsByTagName('result')[0].childNodes[0].nodeValue;

        if (result === "OK") {
            closePrintDialog();
        } else {
            document.getElementById("buttonForm:printMsg").innerHTML = response.getElementsByTagName('message')[0].childNodes[0].nodeValue;
            soundPlay();
        }

    } catch (e) {
        var error = language === "ja" ? "印刷要求の送信に失敗しました。" : "Failed to send print request.";
        document.getElementById("buttonForm:printMsg").innerHTML = error;
        soundPlay();
    }
}

/**
 * 印刷要求をSmaPri Driverに送信する。
 * 
 * @param {string} baseUrl
 * @param {type} language 
 */
function sendPrintRequest(baseUrl, language) {
    var Circulation = $("input#printNumInput").val();
    var request = new XMLHttpRequest();
    var error = language === "ja" ? "印刷要求の送信に失敗しました。" : "Failed to send print request.";

    try {
        var printVal = encodeURI("&(発行枚数)=" + Circulation);
        var urlPath = baseUrl + printVal;
        request.responseType = '';
        request.open('GET', urlPath, true);
        request.send();
    } catch (e) {
        var data = "Send Request fail.";
        document.getElementById("buttonForm:printMsg").innerHTML = error;
        document.getElementById("hiddenForm:printLog").value = data;
        soundPlay();
    }

    request.onload = function () {
        var data = this.response.toString();
        document.getElementById("hiddenForm:printLog").value = data;
        try {

            if (request.readyState === 4) {
                if (request.status === 200) {
                    var xmlDoc = request.responseXML;
                    var result = xmlDoc.getElementsByTagName('result')[0].childNodes[0].nodeValue;
                    if (result === "OK") {
                        document.getElementById("hiddenForm:printReqResult").value = true;
                        document.getElementById("hiddenForm:hiddenDialog").value = true;
                        document.getElementById("hiddenForm").submit();
                    } else {
                        var message = xmlDoc.getElementsByTagName('message')[0].childNodes[0].nodeValue;
                        document.getElementById("buttonForm:printMsg").innerHTML = message;
                        soundPlay();
                    }
                } else {
                    document.getElementById("buttonForm:printMsg").innerHTML = error;
                    soundPlay();
                }
            } else {
                document.getElementById("buttonForm:printMsg").innerHTML = error;
                soundPlay();
            }
        } catch (e) {
            document.getElementById("buttonForm:printMsg").innerHTML = error;
            soundPlay();
        }
        //document.getElementById("hiddenForm").submit();
    };

    request.onerror = function () {
        var data = "Label print fail.";
        document.getElementById("buttonForm:printMsg").innerHTML = error;
        document.getElementById("hiddenForm:printLog").value = data;
        soundPlay();
    };
}

/**
 * エラー・警告時 音声・バイブレーション出力
 */
function soundPlay() {
    // バイブレーション
    window.navigator.vibrate([200, 200, 200, 200, 200]);
    // 「pippi」
    document.getElementById('sound').play();
}

