'* �V���[�g�J�b�g�̃����N����ꊇ�ύX����X�N���v�g
'* �Q�l: https://pnpk.net/cms/archives/2231
Option Explicit
 
'* �u���ΏۂƂȂ�UNC���w�肷��
Const TARGET_UNC = "C:\adFactory\bin"
'* �u���ΏۂƂȂ镶������w�肷��
Const REPLACE_OLD = ".exe"
'* �u�����镶������w�肷��
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

   'Wscript.Echo "�V���[�g�J�b�g���������������������܂����B"

End Sub
 
'�V���[�g�J�b�g���ǂ����̔���
Function isShortcut(strFile)
   If UCase(Right(strFile, 4)) = ".LNK" OR UCase(Right(strFile, 4)) = ".URL" Then
      isShortcut = True
   Else
      isShortcut = False
   End If
End Function
 
'�V���[�g�J�b�g�̏�������
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

   '�G���[����
   'If Err <> 0 Then
   '   If Err = -2147024891 Then
   '      WScript.Echo "�G���[���������܂����B" & vbCrLf & vbCrLf &_
   '                "�ȉ��̃t�@�C���ɑ΂��鏑�����݌������s�����Ă��邽��" & vbCrLf &_
   '                "�t�@�C�����X�V���鎖���o���܂���ł����B" & vbCrLf & vbCrLf &_
   '                strFile & vbCrLf & vbCrLf &_
   '                "�t�@�C���̃A�N�Z�X�����A�܂��͓ǂݎ�葮�����m�F���Ă��������B"& vbCrLf &_
   '                "���̃t�@�C���ɑ΂��鏈���̓X�L�b�v���܂��B"
   '   Else
   '      WScript.Echo Err.Number & " : " & Err.Description
   '   End If
   'End If

End Function
