
--�@��������������������������������

--�@���@�@�@�ڍs��DB�Ŏ��{�@

--�@��������������������������������




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- �ڍs�p���RID�V���Ή��\�ւ̈ڍs

-- �菇1:�@���f���R�̈ڍs

insert 
into iko_reason
select
1
,a_interrupt_id
,a_interrupt_id
,a_interrupt_reason
,a_font_color
,a_back_color
,a_light_pattern
,0


from dblink(
'DBLINK_adFactoryDB',
'select  a.interrupt_id,a.interrupt_reason,a.font_color,a.back_color,a.light_pattern
from 
mst_interrupt_reason a'
) 
as aa( a_interrupt_id int ,a_interrupt_reason text ,a_font_color text ,a_back_color text ,a_light_pattern text )

;

-- �菇1-1.�@�V�[�P���X�̍쐬


--CREATE SEQUENCE mst_reason_reason_id_seq INCREMENT BY 1;

-- �菇1-2.�@�V�[�P���X�̏����l�ݒ�

select setval('mst_reason_reason_id_seq',(select max(reason_id_new) from iko_reason));




-- �菇1:�@�x�����R�̈ڍs


-- �ڍs�p���RID�V���Ή��\�ւ̈ڍs

-- �菇2:�@���f���R�̈ڍs

insert 
into iko_reason
select
2
,( nextval('mst_reason_reason_id_seq'))
,a_delay_id
,a_delay_reason
,a_font_color
,a_back_color
,a_light_pattern
,0


from dblink(
'DBLINK_adFactoryDB',
'select  a.delay_id,a.delay_reason,a.font_color,a.back_color,a.light_pattern
from 
mst_delay_reason a'
) 
as aa( a_delay_id int ,a_delay_reason text ,a_font_color text ,a_back_color text ,a_light_pattern text )

;




-- �菇3:�@���R�i�ďo���R�j�̈ڍs�@���@�ďo���R�݂̂��ݒ肳��Ă���

insert 
into iko_reason
select
0
,( nextval('mst_reason_reason_id_seq'))
,0
,a_reason
,'#000000'
,'#FFFFFF'
,'LIGHTING'
,a_reason_order


from dblink(
'DBLINK_adFactoryDB',
'select  a.reason , a.reason_order
from 
mst_reason a'
) 
as aa( a_reason text ,a_reason_order int )

;


