

--�@DB�����N�̃C���X�g�[��

create extension dblink;

--�@���@���@��DB�ւ̐ڑ�

-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �f�[�^�x�[�X�@�ؒf
select dblink_disconnect('DBLINK_adFactoryDB');



-- �f�[�^�x�[�X�@�N���C�A���g�G���R�[�f�B���O�̊m�F�ƕύX
select current_setting('client_encoding');

set client_encoding to 'utf8';
