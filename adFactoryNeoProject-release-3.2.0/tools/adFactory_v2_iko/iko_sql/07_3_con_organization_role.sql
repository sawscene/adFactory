
--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 06_1�@�g�D�E�����֘A�t���̈ڍs


insert
into con_organization_role
select 

a_fk_organization_id
,a_fk_role_id



from dblink(
'DBLINK_adFactoryDB',
'select  
a.fk_organization_id
,a.fk_role_id

from 
con_organization_role a'
) 
as aa( 
a_fk_organization_id int
,a_fk_role_id int


 )

;

-- �m�F

--select * from con_organization_role
--order by organization_id,role_id
--;

