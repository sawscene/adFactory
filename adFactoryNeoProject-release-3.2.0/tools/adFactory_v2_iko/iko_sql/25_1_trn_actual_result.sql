

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������


-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@�H�����т��ڍs


insert
into trn_actual_result

select

a_actual_id
,a_fk_kanban_id
,a_fk_work_kanban_id
,a_implement_datetime
,a_transaction_id
,a_fk_equipment_id
,a_fk_organization_id
,a_fk_workflow_id
,a_fk_work_id
,a_actual_status
,a_work_time
,a_interrupt_reason
,a_delay_reason
,a_comp_num
,a_pair_id
,a_non_work_time
,(select reason_id_new from iko_reason where a_interrupt_reason = reason and reason_type = 1 )     -- ���f���Rid
,(select reason_id_new from iko_reason where a_delay_reason = reason  and reason_type = 2 )        -- �x�����Rid
,(select kanban_name   from trn_kanban where a_fk_kanban_id = kanban_id )                          -- �J���o����
,(select equipment_name from mst_equipment where a_fk_equipment_id = equipment_id )                -- �ݔ���
,(select organization_name from mst_organization where a_fk_organization_id = organization_id )    -- �g�D��
,(select workflow_name from mst_workflow where a_fk_workflow_id = workflow_id )                    -- �H������
,(select work_name from mst_work where a_fk_work_id = work_id )                                    -- �H����
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
,1       --  �r���p�o�[�W����


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.actual_id
,a.fk_kanban_id
,a.fk_work_kanban_id
,a.implement_datetime
,a.transaction_id
,a.fk_equipment_id
,a.fk_organization_id
,a.fk_workflow_id
,a.fk_work_id
,a.actual_status
,a.work_time
,a.interrupt_reason
,a.delay_reason
,a.comp_num
,a.pair_id
,a.non_work_time



from trn_actual_result a

'
)
as LINK1
(

a_actual_id int
,a_fk_kanban_id int
,a_fk_work_kanban_id int
,a_implement_datetime timestamp
,a_transaction_id int
,a_fk_equipment_id int
,a_fk_organization_id int
,a_fk_workflow_id int
,a_fk_work_id int
,a_actual_status text
,a_work_time int
,a_interrupt_reason text
,a_delay_reason text
,a_comp_num int
,a_pair_id int
,a_non_work_time int



)
;




-- �e�햼�̂��擾�ł��Ă��Ȃ��ꍇ�A���̂ɁfNONE�f��ݒ肷��
 -- �J���o����
UPDATE trn_actual_result
set kanban_name = 'NONE'
where kanban_name IS NULL
;


-- �ݔ���
UPDATE trn_actual_result
set equipment_name = 'NONE'
where equipment_name IS NULL
;

-- �g�D��
UPDATE trn_actual_result
set organization_name = 'NONE'
where organization_name IS NULL
;

-- �H������
UPDATE trn_actual_result
set workflow_name = 'NONE'
where workflow_name IS NULL
;

-- �H����
UPDATE trn_actual_result
set work_name = 'NONE'
where work_name IS NULL
;




--�@�m�F

--select * from trn_actual_result
--where workflow_name = 'NONE'
--order by actual_id
--;

--select * from trn_actual_result order by actual_id;


--truncate table trn_actual_result;



--select * from trn_kanban where kanban_id = ?? ;                           -- �J���o����
--select * from mst_equipment where equipment_id = 114 ;                -- �ݔ���
--select * from mst_organization where organization_id = 286 ;   -- �g�D��
--select * from mst_workflow where workflow_id = 295;                   -- �H������
--select * from mst_work where work_id  = ??;                                   -- �H����

