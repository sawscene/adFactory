


#********************************************
#* adFactory�@DB�ڍs ���s�̂��߂�CD
#********************************************


## sql�t�@�C����dir�ֈړ�
## ���@���{������ɕύX���鎖�I


echo "���@���sdir��cd"
##$SQLPATH="C:\Users\seo\Desktop\SVN_adFactory\trunk\070_�ڍs\SQL\"
$SQLPATH="C:\adFactory_v2_iko\iko_sql\"

echo $ENV:SQLPATH

cd $ENV:SQLPATH

#********************************************
#* adFactory�@DB�ڍs ������ & ���s
#********************************************
## ���O�C���@psql -h localhost -p 15432 -U postgres -d adFactoryDB2

## ���@���@postgres�@���ϐ��ݒ�@���@��


##�@�f�[�^�x�[�X��
Set-Item Env:PGDATABASE "adFactoryDB2"

##�@�z�X�g��
Set-Item Env:PGHOST "localhost"

##�@�|�[�g
Set-Item Env:PGPORT "15432"

##�@���[�U�[
Set-Item Env:PGUSER "postgres"

##�@�p�X���[�h
Set-Item Env:PGPASSWORD "@dtek1977"

##�@�G���[��
Set-Item Env:VERBOSITY "verbose"


$ENV:PGDATABASE
$ENV:PGHOST
$ENV:PGPORT
$ENV:PGUSER
$ENV:PGPASSWORD




#********************************************
#* adFactory�@DB�ڍs ���s SEQ�̍쐬
#********************************************




## sql�t�@�C���̎��s

## ��@psql -d adFactoryDB_V2 -U postgres -f END_VACUUM.sql

echo " ���@���@adFactory�@���t�@�N�^�����O���O�����@START�@���@��";date




echo "���@�ڍs�p�e�[�u���쐬" ; date
psql -f 00_create_iko_tables.sql




echo "���@DBLINK �쐬" ; date
## ����̂�
psql -f 00_0_DBLINK_create.sql 


echo "���@1/2�@���@SEQ�폜" ; date
## �s�v
##psql -f 00_0_BEFOR_seq_drop.sql 


##echo "���@1/2�@���@SEQ�쐬" ; date
## �s�v
##psql -f 00_0_BEFOR_seq.sql 




echo "���@view Drop" ; date
## ����͕s�v
##psql -f 00_0_BEFOR_view_drop.sql


##�@�e��view�쐬�@���@2��ڈȍ~�͏�Lview�̍폜�����{���鎖

## �H�����@JSON�p�@�J���o���v���p�e�B�e���v���[�g�@mst_kanban_property_template
echo "��04_0_mst_workflow_json_view" ; date
psql -f 04_0_mst_workflow_json_view.sql

##�@�ݔ��@JSON�p�@*�ݔ��}�X�^�v���p�e�B�@mst_equipment_property
echo "��05_0_mst_equipment_json_view" ; date
psql -f 05_0_mst_equipment_json_view.sql

##�@�g�D�@JSON�p�@*�g�D�}�X�^�v���p�e�B�@mst_organization_property
echo "��07_0_mst_organization_json_view" ; date
psql -f 07_0_mst_organization_json_view.sql




echo " ���@���@adFactory�@���t�@�N�^�����O���O�����@END�@���@��"
date





