# �C���X�g�[������ؖ����̃p�X
$pfxPath = "C:\adFactory\adWorkbook\bin\adWorkbook.pfx"

# �ؖ����̃p�X���[�h
$pfxPassword = ConvertTo-SecureString -String "2712" -Force -AsPlainText

# �ؖ�����ǂݍ���
$pfx = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2
$pfx.Import($pfxPath, $pfxPassword, [System.Security.Cryptography.X509Certificates.X509KeyStorageFlags]::PersistKeySet)

# "�M�����ꂽ���[�g�ؖ��@��"�ɃC���X�g�[��
$rootStore = New-Object System.Security.Cryptography.X509Certificates.X509Store("Root", "LocalMachine")
$rootStore.Open([System.Security.Cryptography.X509Certificates.OpenFlags]::ReadWrite)
$rootStore.Add($pfx)
$rootStore.Close()

# "�M�����ꂽ���s��"�ɃC���X�g�[��
$trustedPublisherStore = New-Object System.Security.Cryptography.X509Certificates.X509Store("TrustedPublisher", "LocalMachine")
$trustedPublisherStore.Open([System.Security.Cryptography.X509Certificates.OpenFlags]::ReadWrite)
$trustedPublisherStore.Add($pfx)
$trustedPublisherStore.Close()
