
param($from, $to)

Write-Host $from
Write-Host $to

if (Test-Path $from) {
    $excel = New-Object -ComObject Excel.Application
    $excel.Visible = $false
    $excel.DisplayAlerts = $false

    try {
        $book = $excel.Workbooks.Open($from, 0, $true)
        $book.ExportAsFixedFormat([Microsoft.Office.Interop.Excel.XlFixedFormatType]::xlTypePDF, $to)
    } catch {
	Write-Error "error: $_"
    } finally {
        if ($book) { $book.Close() }
	[System.Runtime.InteropServices.Marshal]::ReleaseComObject($obj)
        $excel.Quit()
	$excel = $null
        [GC]::Collect()
    }
}

Write-Host "exit xlsx2pdf"
exit 0
