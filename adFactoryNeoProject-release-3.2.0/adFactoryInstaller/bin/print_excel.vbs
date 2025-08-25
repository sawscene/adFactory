Option Explicit

Dim obj
Dim objWkBk

Set obj = CreateObject("Excel.Application")
Set objWkBk = obj.Workbooks.Open(WScript.Arguments(0))

If WScript.Arguments.Count = 2 Then
    obj.ActiveWindow.SelectedSheets.PrintOut ,,WScript.Arguments(1)
Else 
    On Error Resume Next
    obj.ActiveWindow.SelectedSheets.PrintOut()
    If Err.Number <> 0 Then
        objWkBk.Close
    End If
End If

objWkBk.Save
objWkBk.Close
Set objWkBk = Nothing
obj.Quit
Set obj = Nothing
