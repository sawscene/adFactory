
--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 07_2�@�g�D�E�x�e�֘A�t���̈ڍs


insert
into con_organization_breaktime
select 

a_fk_organization_id
,a_fk_breaktime_id




from dblink(
'DBLINK_adFactoryDB',
'select  
a.fk_organization_id
,a.fk_breaktime_id


from 
con_organization_breaktime a'
) 
as aa( 
a_fk_organization_id int
,a_fk_breaktime_id int

 )

;




-- �m�F

--select * from mst_object_type
--order by object_type_id desc
--;

