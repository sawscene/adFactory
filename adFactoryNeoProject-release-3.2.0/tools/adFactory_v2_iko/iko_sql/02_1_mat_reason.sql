
--�@��������������������������������

--�@���@�@�@�ڍs��DB�Ŏ��{�@

--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- ���R�}�X�^�ւ̈ڍs

-- �菇1:�@���R�}�X�^�̈ڍs

insert 
into mst_reason
select
reason_id_new
,0
,reason_type
,reason
,font_color
,back_color
,light_pattern
,reason_order
,1


from iko_reason

;



-- �m�F

--select * from iko_reason;

--select reason,count(*) from iko_reason
--group by reason
--having count(*) > 1
--;

