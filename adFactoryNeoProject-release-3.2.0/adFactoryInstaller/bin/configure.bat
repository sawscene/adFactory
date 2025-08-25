rem adInterfaceのポート(18005)を開く
netsh advfirewall firewall delete rule name="adInterface_in"
netsh advfirewall firewall add rule name="adInterface_in" dir=in protocol=tcp localport=18005-18007 profile=public,private,domain action=allow description="adInterface port."
rem HTTPSのポート(443)を開く
netsh advfirewall firewall delete rule name="HTTPS_in"
netsh advfirewall firewall add rule name="HTTPS_in" dir=in protocol=tcp localport=443 profile=public,private,domain action=allow description="HTTPS port."
rem PostgreSQLのポート(15432)を開く
netsh advfirewall firewall delete rule name="PostgreSQL2_in"
netsh advfirewall firewall add rule name="PostgreSQL2_in" dir=in protocol=tcp localport=15432 profile=public,private,domain action=allow description="PostgreSQL port."
rem FTPのポート(21,50210-50217)を開く
netsh advfirewall firewall delete rule name="ApacheFTP_in"
netsh advfirewall firewall add rule name="ApacheFTP_in" dir=in protocol=tcp localport=21,50210-50217 profile=public,private,domain action=allow description="ApacheFTP port."
rem NTP Serviceのポート(123)を開く
netsh advfirewall firewall delete rule name="NTP_Service_IN"
netsh advfirewall firewall delete rule name="NTP_Service_OUT"
netsh advfirewall firewall add rule name="NTP_Service_IN" dir=in profile=any localip=any remoteip=any protocol=udp localport=123 action=allow description="NTP Service port."
netsh advfirewall firewall add rule name="NTP_Service_OUT" dir=out profile=any localip=any remoteip=any protocol=udp localport=123 remoteport=123 action=allow description="NTP Service port."
