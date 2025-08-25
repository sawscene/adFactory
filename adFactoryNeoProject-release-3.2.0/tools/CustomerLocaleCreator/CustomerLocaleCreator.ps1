$OutputEncoding = [System.Text.Encoding]::GetEncoding('Shift_JIS')

# gti���|�W�g���|
$ManagerURL = "git@gitlab.com:adFactory/adFactoryNeoProject.git"
$ProductURL = "git@gitlab.com:adFactory/adProduct.git"

# �o�[�W������
$Versions = @("release-2.1.13","release-2.1.14","release-2.1.15","release-2.1.16","release-2.1.17","release-2.1.18","release-2.1.19")

# ��ƃf�B���N�g��
$OutputPath = "output/"

# locale�t�@�C��
$ManagerFilePath = "adFactoryLocale/Locale.csv"
$ProductFilePath = "Locale.csv"

# �ꎞ�t�@�C��
$TmpFileName  = "${OutputPath}Temp.tar"

$password = "adtekfuji"

# ���P�[���t�@�C��csv���_�E�����[�h����
function DownLoadLocaleCsv($URL, $FilePath, $AppType) {
    process{
        $csvFileName = "${OutputPath}${AppType}_${_}.csv"
        Start-Process -FilePath git -ArgumentList ("archive","--format=tar","--remote=${URL}", $_, $FilePath, "-o", $TmpFileName) -Wait
        tar -zxvf $TmpFileName -C $OutputPath
        Move-Item "${OutputPath}${FilePath}" $csvFileName
        [PSCustomObject]@{
            VersionName = $_
            AppType = $AppType 
            FileName = $csvFileName 
        }
    }
}

# ���P�[���t�@�C����ǂݍ���
function LoadLocaleFile {
    process{
        $csv = Import-Csv -Encoding default $_.FileName
        [PSCustomObject]@{
            VersionName = $_.VersionName
            FileName = $_.FileName
            AppType = $_.AppType
            CSVData = $csv
        }
    }
}

# CSV����L�[���擾����
function getKeys{
    process {
        $_.CSVData | Get-Member -MemberType NoteProperty | Select-Object -ExpandProperty 'Name'
    }
}

# �ڋq���ɕ���
function DecomposeLocaleMapListForEachCustomer($custumerList, $pickupKey) {
    process{
        $custormerLocaleList = @{}
        $custumerList | ForEach-Object { $custormerLocaleList.Add($_, [ordered]@{})}
        foreach($item in $_.CSVData) {
            $count = 0
            #�}�b�v�֕ϊ�
            foreach($customer IN $custumerList) {
                if("" -eq $item.$customer){
                    continue;
                }
                
                if ($null -eq $item.$customer) {
                  continue;
                }
                
                if("" -ne $item.$pickupKey -And $null -ne $item.$pickupKey -And $custormerLocaleList[$customer].Contains($item.'KEY') -eq $False) {
                    $custormerLocaleList[$customer].Add($item.'KEY', $item.$pickupKey)
                }
                ++$count
            }
            if ($count -ne 0) {
                continue;
            }
            #����`�F�b�N�����������炷�ׂĂ̌ڋq�ɓo�^
            foreach($customer IN $custumerList) {
                if("" -ne $item.$pickupKey -And $null -ne $item.$pickupKey -And $custormerLocaleList[$customer].Contains($item.'KEY') -eq $False) {
                    $custormerLocaleList[$customer].Add($item.'KEY', $item.$pickupKey)
                }
            }
        }
        [PSCustomObject]@{
            VersionName = $_.VersionName
            FileName = $_.FileName
            AppType = $_.AppType
            Data = $custormerLocaleList
        }
    }
}

# 
function GroupByCustomer {
    begin{
        $custormerDataList = @{}
    }
    process {
        foreach($customer IN $_.Data.Keys) {
            if($custormerDataList.ContainsKey($customer) -eq $false) {
                $custormerDataList.add($customer, @())
            }
            
            $custormerDataList[$customer] += [PSCustomObject]@{
                VersionName = $_.VersionName
                FileName = $_.FileName
                AppType = $_.AppType
                LocaleMap = $_.Data[$customer]
            }
        }
    }
    end {
        $custormerDataList
    }
}

function CreateDiffList{
    begin{
        $dict = @{}
    }
    process{
        $list = @()
        foreach($key in $_.LocaleMap.Keys) {
            $value = $_.LocaleMap[$key]
            if($dict.$key){
                if ($dict.$key -ne $value) {
                    $dict.$key = $value
                    $list += [PSCustomObject]@{
                        VersionName = $_.VersionName
                        Key = $key
                        Value = $value
                        State = "update"
                    }
                }
            } else {
                $dict.$key = $value
                $list +=[PSCustomObject]@{
                    Key = $key
                    Value = $value
                    State = "add"
                }
            }
        }

        [PSCustomObject]@{
            VersionName = $_.VersionName
            FileName = $_.FileName
            AppType = $_.AppType
            DifLocale = $list
        }
    }

}

function groupByVersion {
    begin{
        $versionMap = [ordered]@{}
    }
    process {
        foreach ($difLocale In $_) {
            $version = $difLocale.VersionName
            if($versionMap.Contains($version) -eq $false) {
                $versionMap.add($version, @())
            }

            $versionMap[$version] += [PSCustomObject]@{
                FileName = $difLocale.FileName
                AppType = $difLocale.AppType
                DifLocale = $difLocale.DifLocale
            }
        }
    }
    end {
        $versionMap
    }
}


function CreateExcell($excelFileName, $localeMap)
{
    $excel = New-Object -ComObject Excel.Application
    $excel.Visible = $False
    $book = $excel.Workbooks.Add()

    $sheet = $book.WorkSheets.item("Sheet1")
    $sheet.name = "Infomation"
    $sheet.Cells.Item(1, 1) = "UpdateDate"
    $sheet.Cells.Item(1, 2) = Get-Date -Format "yyyy/MM/dd HH:mm"
    $sheet.Protect($password)


    foreach ($version IN $localeMap.Keys) {
        $lastSheet = $book.Sheets($book.Sheets.Count)
        $sheet = $book.Worksheets.Add([System.Reflection.Missing]::Value, $lastSheet)
        $sheet.name = $version
        $sheet.Cells.Item(1, 1) = "App"
        $sheet.Cells.Item(1, 2) = "KEY"
        $sheet.Cells.Item(1, 3) = "Value"
        $sheet.Cells.Item(1, 4) = "State"        
        $rowNum = 2

        foreach ($data IN $localeMap[$version]) {
            foreach($entity in $data.DifLocale) {
                $sheet.Cells.Item($rowNum, 1) = $data.AppType
                $sheet.Cells.Item($rowNum, 2) = $entity.KEY
                $sheet.Cells.Item($rowNum, 3) = $entity.Value
                $sheet.Cells.Item($rowNum, 4) = $entity.State
                $rowNum += 1
            }      
        }

	$sheet.Protect($password)

    }
    $lastSheet = $book.Sheets($book.Sheets.Count)
    $sheet = $book.Worksheets.Add([System.Reflection.Missing]::Value, $lastSheet)
    $sheet.name = "Version"
    $rowNum = 1
    foreach ($version IN $localeMap.Keys) {
        $sheet.Cells.Item($rowNum,1) = $version
        $rowNum += 1
    }
    $sheet.Protect($password)

    
    $book.SaveAs($excelFileName)
    $excel.Quit()
    $excel = $null
    [GC]::Collect()
}


if(Test-Path $OutputPath) {
    Remove-Item $OutputPath -Recurse -Force
}

New-Item $OutputPath -Type directory


$adManagerLocaleData = $Versions | DownLoadLocaleCsv $ManagerURL $ManagerFilePath "�Ǘ��[��" | LoadLocaleFile 
$adProductLocaleData = $Versions | DownLoadLocaleCsv $ProductURL $ProductFilePath "��ƒ[��" | LoadLocaleFile 


$keyList =   $adManagerLocaleData | getKeys
$keyList += ($adProductLocaleData | getKeys)
$custormList = [System.Collections.Generic.HashSet[String]]::new([String[]]($keyList))

#�ڋq�ȊO�̏���ݒ�
$Kinds = @("KEY","�W��","�W��_���{��","�q��","�q��_���{��","FUJI","FUJI_���{��", "Locale_locale","Locale_locale_ja_JP","eco_locale","eco_locale_en_US","eco_locale_ja_JP","product_locale","product_locale_en_US","product_locale_ja_JP","lana_locale","lana_locale_en_US","lana_locale_ja_JP","nippo_locale","nippo_locale_en_US","nippo_locale_ja_JP")
foreach($val IN $Kinds) {
    $custormList.Remove($val)
}


$adManagerlocaleListForEachCustomer = $adManagerLocaleData | DecomposeLocaleMapListForEachCustomer $custormList '�W��_���{��' | GroupByCustomer
$adProductlocaleListForEachCustomer = $adProductLocaleData | DecomposeLocaleMapListForEachCustomer $custormList 'product_locale_ja_JP' | GroupByCustomer


$adManagerDifflocaleListForEachCustomer = @{}
foreach ($customer IN $adManagerlocaleListForEachCustomer.Keys) {
    $adManagerDifflocaleListForEachCustomer[$customer] = $adManagerlocaleListForEachCustomer[$customer] | CreateDiffList
}

$adProductDifflocaleListForEachCustomer = @{}
foreach ($customer IN $adProductlocaleListForEachCustomer.Keys) {
    $adProductDifflocaleListForEachCustomer[$customer] = $adProductlocaleListForEachCustomer[$customer] | CreateDiffList
}



$CurrentDir = Split-Path $MyInvocation.MyCommand.Path

foreach ($customer IN $custormList) {
    $localeData = @($adManagerDifflocaleListForEachCustomer[$customer], $adProductDifflocaleListForEachCustomer[$customer]) | groupByVersion
    
    Write-Host "Create ${customer} File ..."
    $excelFileName = "${CurrentDir}\${customer}�l�p�ǉ�����t�@�C��.xlsx"
    if (Test-Path $excelFileName) {
        Remove-Item $excelFileName
    }
    CreateExcell $excelFileName $localeData
}


