

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@��Ƌ敪�}�X�^���ڍs


insert
into mst_work_category

select

a_work_category_id
,a_work_category_name

,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.work_category_id
,a.work_category_name


from mst_work_category a

'
)
as LINK1
(
a_work_category_id int
,a_work_category_name text



)
;


--�@�m�F

--select * from mst_work_category
--;


