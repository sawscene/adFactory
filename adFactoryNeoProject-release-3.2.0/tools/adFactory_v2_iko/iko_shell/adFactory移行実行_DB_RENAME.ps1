

#********************************************
#* adFactory�@DB�ڍs ���s�̂��߂�CD
#********************************************


## sql�t�@�C����dir�ֈړ�
## ���@���{������ɕύX���鎖�I

cd C:\Users\seo\Desktop\SVN_adFactory\trunk\070_�ڍs\SQL

#********************************************
#* adFactory�@DB�ڍs �ꎟ�I�Ȋ����� & ���s
#********************************************
## ���O�C���@psql -h localhost -p 5432 -U postgres -d adFactoryDB_V2

## ���@���@postgres�@���ϐ��ݒ�@���@��


##�@�f�[�^�x�[�X��
Set-Item Env:PGDATABASE "postgres"
##Set-Item Env:PGDATABASE "adFactoryDB_ORG"

##�@�z�X�g��
Set-Item Env:PGHOST "localhost"

##�@�|�[�g
Set-Item Env:PGPORT "5432"

##�@���[�U�[
Set-Item Env:PGUSER "postgres"

##�@�p�X���[�h
Set-Item Env:PGPASSWORD "postgres"

##�@�G���[��
Set-Item Env:VERBOSITY "verbose"





#********************************************
#* adFactory�@DB�ڍs ���s
#********************************************

##�@DB���@�m�F
##psql -l -U postgres -h localhost


echo "���@50/51�@���@99_DB_NAME_CHAING.sql.sql : rename" ; date


## DB���̕ύX�@adFactoryDB_V2�@�ˁ@adFactoryDB
psql -f     99_DB_NAME_CHAING.sql




## DB���̖̂߂��@adFactoryDB�@�ˁ@adFactoryDB_V2
##psql -f     99_DB_NAME_CHAING_RERUN.sql


##psql -U postgres -h localhost -c "ALTER DATABASE adFactoryDB RENAME TO adFactoryDB_org"


 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }






echo " ���@���@adFactory�@���t�@�N�^�����O�ڍs�@ENDT�@���@��"
date





