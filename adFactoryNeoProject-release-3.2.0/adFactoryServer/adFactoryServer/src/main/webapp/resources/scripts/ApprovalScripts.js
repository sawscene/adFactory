/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * コントロールの表示状態を切り替える。
 * 
 * @return {undefined}
 */
function buttonControl() {
    let buttonConJsonVal = document.getElementById("hiddenForm:buttonConJson").value;
    let json = JSON.parse(buttonConJsonVal);

    /* 表示/非表示状態を切り替え */
    let commentDisp = (json.renderedComment ? "block" : "none");
    document.getElementById("buttonForm:commentTextArea").style.display = commentDisp;

    let aplCanDisp = (json.renderedApplicationCancel ? "block" : "none");
    document.getElementById("buttonForm:applyCancelBtn").style.display = aplCanDisp;

    let appDisp = (json.renderedApproval ? "block" : "none");
    document.getElementById("buttonForm:approvalBtn").style.display = appDisp;

    let cancelDisp = (json.renderedCancel ? "block" : "none");
    document.getElementById("buttonForm:cancelBtn").style.display = cancelDisp;

    let appCanDisp = (json.renderedApprovalCancel ? "block" : "none");
    document.getElementById("buttonForm:approvalCancelBtn").style.display = appCanDisp;

    /* 有効/無効状態を切り替え */
    document.getElementById("buttonForm:commentTextArea").disabled = json.disabledComment;
    document.getElementById("buttonForm:applyCancelBtn").disabled = json.disabledApplicationCancel;
    document.getElementById("buttonForm:approvalBtn").disabled = json.disabledApproval;
    document.getElementById("buttonForm:cancelBtn").disabled = json.disabledCancel;
    document.getElementById("buttonForm:approvalCancelBtn").disabled = json.disabledApprovalCancel;

}