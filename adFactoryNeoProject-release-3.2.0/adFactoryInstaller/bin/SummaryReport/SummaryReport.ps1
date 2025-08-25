#Install-Module -Name MicrosoftPowerBIMgmt
Import-Module MicrosoftPowerBIMgmt
Import-Module MicrosoftPowerBIMgmt.Profile


$DatasetTemplateName = "adFactoryDatasetTemplate"
$ReportTemplateName = "adFactoryReportTemplate"
$ReportTemplateFileName = "./template.pbix"


$tableFileName = "./Table.json"
$parametersFileName = "./Parameters.json"

function Max($l, $r) {
    if($l -le $r) { 
        return $r
    } else {
        return $l
    }
}

function AddPowerBIRow ($datasetId, $tableName, $elements) {
    $maxCount = 10000 # 最大10,000行
    $number = [math]::ceiling($elements.count/$maxCount)
    $time=1
    while($time -le $number) {
        $tmp = @($elements[(($time-1)*$maxCount)..($time*$maxCount-1)])
        if ($tmp.count -eq 1) {
            Add-PowerBIRow -DataSetId $datasetId -TableName $tableName -Rows $tmp[0]
        } else {
            Add-PowerBIRow -DataSetId $datasetId -TableName $tableName -Rows $tmp
        }
        ++$time
    }
}

function update($modelName, $fromDateTime, $toDateTime, $username, $password) {
    $dataSetName = "${modelName}Dataset"
    $reportName = "${modelName}Report"

    $credential = New-Object System.Management.Automation.PSCredential($username, (ConvertTo-SecureString -asPlainText -Force $password))

    # ログイン
    Connect-PowerBIServiceAccount -Credential $credential

    # データベースリストを取得
    $datasetList = Get-PowerBIDataSet
    $dtasetIndex = -1
    $targetDatasetIndex = -1
    if(($datasetList).count -ne 0) {
        $dtasetIndex = [Array]::IndexOf($datasetList.Name, $DatasetTemplateName)
        $targetDatasetIndex = [Array]::IndexOf($datasetList.Name, $dataSetName)
    }

    # サンプルデータセットが無い場合は追加
    if($dtasetIndex -eq -1) {
        $data = Get-Content $tableFileName -Raw|ConvertFrom-Json
        $data.Name = $DatasetTemplateName
        $body = ConvertTo-Json $data -depth 10
        $PushDataset = Invoke-PowerBIRestMethod -Url datasets -Method Post -Body $body
    }

    # レポートリストを取得
    $reportList = Get-PowerBIReport

    $reportIndex = -1
    $targetReportIndex = -1
    if(($reportList).count -ne 0) {
        $reportIndex = [Array]::IndexOf($reportList.Name, $ReportTemplateName)
        $targetReportIndex = [Array]::IndexOf($reportList.Name, $reportName)
    }

    if(-1 -eq $reportIndex) {
        #サンプルレポートが無い場合は追加
        $reportTemplate = New-PowerBIReport -Path $ReportTemplateFileName -Name $ReportTemplateName
    } else {
        $reportTemplate = $reportList[$reportIndex]
    }

    #　DataSet削除
    # if($targetDatasetIndex -ne -1) {
    #     $datasetid = $datasetList[$targetDatasetIndex].id
    #     Write-Host "Delete DataSet id=${datasetid}"
    #     $Url = "datasets/${datasetid}"
    #     Invoke-PowerBIRestMethod -Url $Url -Method Delete
    # }

    Write-Host $fromDateTime
    Write-Host $toDateTime

    # 更新データの取得 (Python実行)
    Write-Host "Python実行"
    python kanban_info.py $modelName $fromDateTime $toDateTime


    # DataSet作成
    $datasetId = -1
    if($targetDatasetIndex -eq -1) {
        Write-Host "Create DataSet"
        $data = Get-Content $tableFileName -Raw|ConvertFrom-Json
        $data.Name = $dataSetName
        $body = ConvertTo-Json $data -depth 10
        $PushDataset = Invoke-PowerBIRestMethod -Url datasets -Method Post -Body $body
        $datasetId = ($PushDataset|ConvertFrom-Json).id
    } else {
        Write-Host "Found DataSet"
        $datasetId = $datasetList[$targetDatasetIndex].id
    }

    Write-Host "data set id = ${datasetId}"

    # カンバン
    Write-Host "Push kanban_info"
    Remove-PowerBIRow -DataSetId $datasetId -TableName kanban_info #既存のテーブルの削除
    $kanban_info = @(Import-Csv -Path ".\kanban_info.csv")
    AddPowerBIRow $datasetId kanban_info $kanban_info

    # 工程カンバン
    Write-Host "Push work_kanban_info"
    Remove-PowerBIRow -DataSetId $datasetId -TableName work_kanban_info #既存のテーブルの削除
    $work_kanban_info = @(Import-Csv -Path ".\work_kanban_info.csv")
    AddPowerBIRow $datasetId work_kanban_info $work_kanban_info

    # 作業実績
    Write-Host "Push actual_work_info"
    Remove-PowerBIRow -DataSetId $datasetId -TableName actual_work_info #既存のテーブルの削除
    $actual_work_info = @(Import-Csv -Path ".\actual_work_info.csv")
    AddPowerBIRow $datasetId actual_work_info $actual_work_info

    # 中断実績
    Write-Host "Push interrupt_work_info"
    Remove-PowerBIRow -DataSetId $datasetId -TableName interrupt_info #既存のテーブルの削除
    $interrupt_info = @(Import-Csv -Path ".\interrupt_work_info.csv")
    AddPowerBIRow $datasetId interrupt_info $interrupt_info

    # 組織情報
    Write-Host "Push organization_info"
    Remove-PowerBIRow -DataSetId $datasetId -TableName organaization #既存のテーブルの削除
    $organaization_info = @(Import-Csv -Path ".\organization_info.csv")
    AddPowerBIRow $datasetId organaization $organaization_info

    # 設備情報
    Write-Host "Push equipment_info"
    Remove-PowerBIRow -DataSetId $datasetId -TableName equipment #既存のテーブルの削除
    $equipment_info = @(Import-Csv -Path ".\equipment_info.csv")
    AddPowerBIRow $datasetId equipment $equipment_info

    $reportWeb = $null
    if(-1 -eq $targetReportIndex) {
        #サンプルレポートが無い場合は追加
        $newReport = Copy-PowerBIReport -Name $reportName -Id $reportTemplate.Id -TargetDatasetId $datasetId
        $reportWeb = ($newReport).webUrl
    } else {
        $reportWeb = $reportList[$targetReportIndex].webUrl
    }


    Write-Host "PowerBI ReportURL : ${reportWeb}"


    # $setting | ConvertTo-Json -Compress | Out-File $settingFileName
}


[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

#作業フォルダの変更
$scriptPath = $MyInvocation.MyCommand.Path
Set-Location (Split-Path -Parent $scriptPath)


$modelName = $Args[0]
$password = $Args[1]
$username = $Args[2]

$baseDateTime = (Get-Date)
$fromDateTime = $baseDateTime.addDays(-30).toString("yyy-MMM-dd HH:mm:ss")
$toDateTime = $baseDateTime.toString("yyy-MMM-dd HH:mm:ss")


update $modelName $fromDateTime $toDateTime $password $username


