rem adReporter�̃|�[�g(1099)���J��
netsh advfirewall firewall delete rule name="adReporter_in"
netsh advfirewall firewall add rule name="adReporter_in" dir=in protocol=tcp localport=1099 profile=public,private,domain action=allow description="adReporter port."
