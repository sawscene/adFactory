Param( [string]$localeType, [string]$margeType, [string]$outfile )

$CurrentDir = Split-Path $MyInvocation.MyCommand.Path
$csvFileName = $CurrentDir + "/Locale.csv"

$dict = New-Object 'System.Collections.Generic.SortedDictionary[string, string]'
$Locale = Import-csv -Encoding Default $csvFileName
$Locale | ForEach-Object {$dict[$_.Key]=$_.$localeType}

if("" -ne $margeType){
    $Locale | ForEach-Object { if("" -ne $_.$margeType) {$dict[$_.KEY]=$_.$margeType }}
}

$file = New-Object System.IO.StreamWriter($outfile, $true, [System.Text.Encoding]::GetEncoding("UTF-8"))
foreach($entity in $dict.GetEnumerator()) {
    if ("" -eq $entity.Value) {
        continue
    }
    $txt = $entity.Key+"="+$entity.Value
    $file.WriteLine($txt)
}
$file.close()


