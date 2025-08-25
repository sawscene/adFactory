'* ショートカットのリンク先を一括変更するスクリプト
'* 参考: https://pnpk.net/cms/archives/2231
Option Explicit
 
'* 置換対象となるUNCを指定する
Const TARGET_UNC = "C:\adFactory\bin"
'* 置換対象となる文字列を指定する
Const REPLACE_OLD = ".exe"
'* 置換する文字列を指定する
Const REPLACE_NEW = ".bat"

Call Main()
 
Private Sub Main()
   Dim WshShell
   Dim objFileSys
   Dim objDesktop
   Dim objFile
   Dim strFile
   Dim strPath

   Set WshShell = WScript.CreateObject("WScript.Shell")
   strPath = WshShell.SpecialFolders("Desktop")

   Set objFileSys = WScript.CreateObject("Scripting.FileSystemObject")
   Set objDesktop = objFileSys.GetFolder(strPath)

   For Each objFile In objDesktop.Files
      If isShortcut(objFile.Path) = True Then
         Call replaceShortcut(objFile.Path)
      Else
          
      End If
   Next

   'Wscript.Echo "ショートカット書き換え処理が完了しました。"

End Sub
 
'ショートカットかどうかの判定
Function isShortcut(strFile)
   If UCase(Right(strFile, 4)) = ".LNK" OR UCase(Right(strFile, 4)) = ".URL" Then
      isShortcut = True
   Else
      isShortcut = False
   End If
End Function
 
'ショートカットの書き換え
Function replaceShortcut(strFile)
   On Error Resume Next
 
   Dim WshShell
   Dim objShellLink
   Dim strTargetPath
   Dim objIconLocation
    
   Set WshShell = WScript.CreateObject("WScript.Shell")
   Set objShellLink = WshShell.CreateShortcut(strFile)
   strTargetPath = objShellLink.TargetPath
   objIconLocation = objShellLink.IconLocation

   If UCase(Left(strTargetPath, Len(TARGET_UNC))) = UCase(TARGET_UNC) Then
      objShellLink.TargetPath = Replace(strTargetPath, REPLACE_OLD, REPLACE_NEW, 1, 1, 1)
      objShellLink.IconLocation = objIconLocation
      objShellLink.Save
   End If

   'エラー処理
   'If Err <> 0 Then
   '   If Err = -2147024891 Then
   '      WScript.Echo "エラーが発生しました。" & vbCrLf & vbCrLf &_
   '                "以下のファイルに対する書き込み権限が不足しているため" & vbCrLf &_
   '                "ファイルを更新する事が出来ませんでした。" & vbCrLf & vbCrLf &_
   '                strFile & vbCrLf & vbCrLf &_
   '                "ファイルのアクセス権限、または読み取り属性を確認してください。"& vbCrLf &_
   '                "このファイルに対する処理はスキップします。"
   '   Else
   '      WScript.Echo Err.Number & " : " & Err.Description
   '   End If
   'End If

End Function
