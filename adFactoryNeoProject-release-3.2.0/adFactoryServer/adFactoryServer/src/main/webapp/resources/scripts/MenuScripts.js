/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * ロード時実行処理
 * @param {string} altMsg
 */
function onPageLoad(altMsg){
    
    setWindowSize();
    
    if(altMsg !== ""){
        soundPlay();
    }
}

/**
 * 表示サイズ設定
 */
function setWindowSize(){

    //アプリの高さ
    var aplicatonHaeight = 1500;
    //ブラウザの高さ取得
    var windowHeight = window.innerHeight;
    //　ブラウザの倍率設定
    var zoom = windowHeight/aplicatonHaeight;

    document.body.style.transformOrigin = 'top';
    document.body.style.height = windowHeight + "px";
    document.body.style.transform = 'scale(' + zoom + ')';
    document.body.style.margin='0px';
}
window.addEventListener("resize", setWindowSize,false);

/**
 * クッキー書き込み
 */
function writeCookie() {
    //前回値削除
    document.cookie = "areaName=;";
    document.cookie = "locale=;";

    //有効期限設定(100日間)
    var now = new Date();
    now.setDate(now.getDate() + 100);
    now.setHours(23);
    now.setMinutes(59);
    now.setSeconds(59);

    var areaName = document.getElementById("areaName").value;
    document.cookie = "areaName=" + encodeURIComponent(areaName) + ";expires=" + now.toUTCString();

    var locale = document.getElementById("locale").value;
    if (locale) {
        document.cookie = "locale=" + encodeURIComponent(locale) + ";expires=" + now.toUTCString();
    } else {
        document.cookie = "locale=ja;expires=" + now.toUTCString();
    }
}

document.addEventListener('DOMContentLoaded', function() {
  history.pushState(null, null, null);

  window.addEventListener('popstate', function(){
    history.pushState(null, null, null);
  });
});

/**
 * エラー・警告時 音声・バイブレーション出力
 */
function soundPlay() {
    // バイブレーション
    window.navigator.vibrate([200, 200, 200, 200, 200]);
    //「pippi」
    document.getElementById('sound').play();
}
