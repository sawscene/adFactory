

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������


-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- �菇1:�@�F�؏����ڍs


insert
into mst_authentication_info

select

a_authentication_id
,a_fk_mastger_id
,a_authentication_type
,a_authentication_data
,a_validity_period
,a_use_lock

,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.authentication_id
,a.fk_mastger_id
,a.authentication_type
,a.authentication_data
,a.validity_period
,a.use_lock


from mst_authentication_info a

'
)
as LINK1
(
a_authentication_id int
,a_fk_mastger_id int
,a_authentication_type text
,a_authentication_data text
,a_validity_period timestamp
,a_use_lock boolean


)
;


--�@�m�F

--select * from mst_authentication_info
--;


