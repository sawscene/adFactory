
--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 06_1�@�g�D�E��Ƌ敪�֘A�t���̈ڍs


insert
into con_organization_work_category
select 

a_fk_organization_id
,a_fk_work_category_id




from dblink(
'DBLINK_adFactoryDB',
'select  
a.fk_organization_id
,a.fk_work_category_id

from 
con_organization_work_category a'
) 
as aa( 
a_fk_organization_id int
,a_fk_work_category_id int



 )

;

-- �m�F

--select * from con_organization_work_category
--order by organization_id,work_category_id
--;

