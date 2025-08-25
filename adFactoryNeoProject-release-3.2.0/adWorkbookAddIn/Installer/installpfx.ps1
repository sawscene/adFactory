# インストールする証明書のパス
$pfxPath = "C:\adFactory\adWorkbook\bin\adWorkbook.pfx"

# 証明書のパスワード
$pfxPassword = ConvertTo-SecureString -String "2712" -Force -AsPlainText

# 証明書を読み込み
$pfx = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2
$pfx.Import($pfxPath, $pfxPassword, [System.Security.Cryptography.X509Certificates.X509KeyStorageFlags]::PersistKeySet)

# "信頼されたルート証明機関"にインストール
$rootStore = New-Object System.Security.Cryptography.X509Certificates.X509Store("Root", "LocalMachine")
$rootStore.Open([System.Security.Cryptography.X509Certificates.OpenFlags]::ReadWrite)
$rootStore.Add($pfx)
$rootStore.Close()

# "信頼された発行元"にインストール
$trustedPublisherStore = New-Object System.Security.Cryptography.X509Certificates.X509Store("TrustedPublisher", "LocalMachine")
$trustedPublisherStore.Open([System.Security.Cryptography.X509Certificates.OpenFlags]::ReadWrite)
$trustedPublisherStore.Add($pfx)
$trustedPublisherStore.Close()
