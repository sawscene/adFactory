

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- �菇1:�@�x�������ڍs


insert
into mst_holiday

select

a_holiday_id
,a_holiday_name
,a_holiday_date
,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.holiday_id
,a.holiday_name
,a.holiday_date


from mst_holiday a

'
)
as LINK1
(
a_holiday_id int
,a_holiday_name text
,a_holiday_date timestamp

)
;


--�@�m�F

--select * from mst_holiday
--;


