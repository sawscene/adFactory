

--�@��������������������������������

--�@���@�@�@�ڍs��DB�Ŏ��{�@

--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@�x�e�}�X�^���ڍs


insert
into mst_breaktime

select

a_breaktime_id
,a_name
,a_starttime
,a_endtime
,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.breaktime_id
,a.name
,a.starttime
,a.endtime


from mst_breaktime a

'
)
as LINK1
(
a_breaktime_id int
,a_name text
,a_starttime timestamp
,a_endtime timestamp



)
;


--�@�m�F

--select * from mst_breaktime
--;


