@echo
REM ----- NTPサーバ構築 ------
REG ADD "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\services\W32Time\TimeProviders\NtpServer" /v "Enabled" /t "REG_DWORD" /d 1 /f
REG ADD "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\services\W32Time\Config" /v "AnnounceFlags" /t "REG_DWORD" /d 5 /f

REM ----- windows time サービス再起動 -----
net stop w32time
net start w32time

REM ----- ntpサーバ受信用ポート解放 -----
netsh advfirewall firewall delete rule name="ntp_in"
netsh advfirewall firewall add rule name="ntp_in" dir=in protocol=udp localport=123 profile=public,private,domain action=allow description="NTP Server receiving port."
