

-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
--\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


--�y DBLINK VIEW�쐬�z
--  �� �ǉ����@�g�D�}�X�^�v���p�e�B



--drop View VW_DBLIK_adFactoryDB_organization_add_info;


create or replace View VW_DBLIK_adFactoryDB_organization_add_info as
select * from dblink
('DBLINK_adFactoryDB',
'select 
fk_master_id
,organization_prop_name as key
,organization_prop_type as type
,organization_prop_value as val
,organization_prop_order as odr 


from mst_organization_property'
)
as 
json_rec(
fk_master_id int
,key text
,type text
,val text
,disp text)
;


