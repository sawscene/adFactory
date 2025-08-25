

#********************************************
#* adFactory�@DB�ڍs 
#********************************************


#********************************************
#* adFactory�@���ϐ�path�ǉ��@powershell�p
#********************************************

## path�ǉ��@postgres��
Set-Item Env:Path "$Env:Path;C:\adFactory\3rd\postgreSQL11\bin\"


## path�ǉ��@adFactory�ڍs��(shell)

Set-Item Env:Path "$Env:Path;C:\adFactory_v2_iko\iko_shell\"


## path�ǉ��@adFactory�ڍs��(sql)
Set-Item Env:Path "$Env:Path;C:\adFactory_v2_iko\iko_sql\"




#********************************************
#* adFactory�@�ϐ��ǉ��@powershell�p
#********************************************

## �ڍs���{LOG�z�u�ꏊ(�ϐ��ݒ�)
Set-Item Env:LOGPATH "C:\adFactory_v2_iko\data\log\"


## �ڍs���{�o�b�N�A�b�v�f�[�^�z�u�ꏊ(�ϐ��ݒ�)
Set-Item Env:BUCKUPPATH "C:\adFactory_v2_iko\data\backup\"


## �ڍs���{shell�z�u�ꏊ(�ϐ��ݒ�)
Set-Item Env:SHELLPATH "C:\adFactory_v2_iko\iko_shell\"


## �ڍs���{SQL�z�u�ꏊ(�ϐ��ݒ�)
Set-Item Env:SQLPATH "C:\adFactory_v2_iko\iko_sql\"


#********************************************
#* adFactory�@DB�ڍs ���{
#********************************************

echo " ���@���@��adFactory�@���t�@�N�^�����O�ڍs�J�n�i�e�j�@START�@���@���@��";date

## ���O�t�@�C��������

$formatted_date = (Get-Date).ToString("yyyyMMdd")
$dmp_file_name = "adFactoryDB_"+$formatted_date+".log"
$fullpathlog = $ENV:LOGPATH+$dmp_file_name





##�@�f�B���N�g����shell�z�u�ꏊ�Ɉړ�

cd $Env:SHELLPATH




## �ڍs���{����view�̍폜��쐬
adFactory�ڍs���O����.ps1 > $fullpathlog 


## �ڍs���{
adFactory�ڍs���s.ps1 >>  $fullpathlog 


echo " ���@���@��adFactory�@���t�@�N�^�����O�ڍs�I���i�e�j�@END�@���@���@��";date

## ��L���s�����s��Ȃ��ŋN��������@
##powershell -ExecutionPolicy RemoteSigned .\adFactory�ڍs���s.ps1





