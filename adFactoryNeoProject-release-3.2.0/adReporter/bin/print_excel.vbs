Option Explicit
 
Dim obj
Dim objWkBk
 
Set obj = CreateObject("Excel.Application")
Set objWkBk = obj.Workbooks.Open(WScript.Arguments(0))
obj.Application.Run "addPicture"
obj.Workbooks(1).Sheets.Select
obj.ActiveWindow.SelectedSheets.PrintOut()
objWkBk.Save
objWkBk.Close
Set objWkBk = Nothing
obj.Quit
Set obj = Nothing
