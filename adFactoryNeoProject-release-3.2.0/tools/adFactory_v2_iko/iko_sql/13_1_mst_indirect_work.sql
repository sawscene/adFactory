

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- �菇1:�@�Ԑڍ�ƃ}�X�^���ڍs


insert
into mst_indirect_work

select

a_indirect_work_id
,a_class_number
,a_work_number
,a_work_name
,a_fk_work_category_id

,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.indirect_work_id
,a.class_number
,a.work_number
,a.work_name
,a.fk_work_category_id

from mst_indirect_work a

'
)
as LINK1
(
a_indirect_work_id int
,a_class_number text
,a_work_number text
,a_work_name text 
,a_fk_work_category_id int


)
;


--�@�m�F

--select * from mst_indirect_work
--;


