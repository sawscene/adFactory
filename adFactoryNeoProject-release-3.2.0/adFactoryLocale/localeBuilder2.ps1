

Param( [string]$localeType, [string]$margeType, [string]$outfile )

$CurrentDir = Split-Path $MyInvocation.MyCommand.Path
$tmpfile = $CurrentDir + "/locale.properties.tmp" 
$csvFileName = $CurrentDir + "/Locale.csv"

$dict = New-Object 'System.Collections.Generic.SortedDictionary[string, string]'
$Locale = Import-csv -Encoding Default $csvFileName
$Locale | ForEach-Object {$dict[$_.Key]=$_.$localeType}

if("" -ne $margeType){
    $Locale | ForEach-Object { if("" -ne $_.$margeType) {$dict[$_.KEY]=$_.$margeType }}
}

$file = New-Object System.IO.StreamWriter($tmpfile, $true, [System.Text.Encoding]::GetEncoding("Shift-Jis"))
foreach($entity in $dict.GetEnumerator()) {
    if ("" -eq $entity.Value) {
        continue
    }
    $txt = $entity.Key+"="+$entity.Value
    $file.WriteLine($txt)
}
$file.close()

$sjis =  ([System.Text.Encoding]::GetEncoding(932))
get-content $tmpfile | Set-Content -Encoding UTF8 $outfile

#$JAVAEXE = $env:JAVA_HOME + "\bin\native2ascii.exe" 
#Start-Process -FilePath $JAVAEXE -argumentList ("-encoding MS932", $tmpfile, $outfile)  -NoNewWindow -Wait

#Remove-Item $tmpfile
