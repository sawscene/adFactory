
--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������


--  ���@���@�e�[�u���̗p�r��ύX�@�����I�ȃe�[�u���̈׈ڍs�s�v�Ƃ���@���@��




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLIMK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=postgres');


-- �J���o��
-- �菇1:�@�H�����ѕt�����̈ڍs


insert 
into trn_actual_adition
select

a_actual_id
---  ���@��������
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp
    from
	    (select 
				fk_actual_id
				,actual_prop_name as key
				,actual_prop_type as type
				,actual_prop_value as val
				,actual_prop_order as disp 
				
				from iko_trn_actual_property

    		where
      		fk_actual_id = LINK1.a_actual_id
						
		) ZZ

  ) JSON_REC
)

,null    --  �T�[�r�X���@���@���̐ݒ�
,null    --  �摜�f�[�^


from dblink
(
'DBLIMK_adFactoryDB',
'select
a.actual_id

from 
trn_actual_result a 
'
) 
as LINK1
(
a_actual_id int
)

;


