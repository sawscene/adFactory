

--�@��������������������������������

--�@���@�@�@�ڍs��DB�Ŏ��{�@

--�@��������������������������������


-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLIMK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=postgres');



-- �菇1:�@�H���J���o���v���p�e�B�̃R�s�[


insert
into iko_trn_actual_property

select

a_actual_prop_id
,a_fk_actual_id
,a_actual_prop_name
,a_actual_prop_type
,a_actual_prop_value
,a_actual_prop_order

,null

from dblink 
(
'DBLIMK_adFactoryDB',
'select 
a.actual_prop_id
,a.fk_actual_id
,a.actual_prop_name
,a.actual_prop_type
,a.actual_prop_value
,a.actual_prop_order



from trn_actual_property a

'
)
as LINK1
(
a_actual_prop_id int
,a_fk_actual_id int
,a_actual_prop_name text
,a_actual_prop_type text 
,a_actual_prop_value text
,a_actual_prop_order int

)
;



-- �A�i���C�Y�Ƃ�C���f�b�N�X

--ANALYZE VERBOSE iko_trn_actual_property;

--REINDEX table iko_trn_actual_property;

VACUUM  FULL    ANALYZE     iko_trn_actual_property   ;



--�@�m�F

--select * from iko_trn_actual_property
--;

