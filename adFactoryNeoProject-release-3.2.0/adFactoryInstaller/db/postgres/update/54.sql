-- ��ƃp�����[�^
drop table if exists mst_work_parameters cascade;

create table mst_work_parameters (
  item_number character varying(256) not null
  , workflow_id bigint not null
  , work_parameter jsonb not null
  , constraint mst_work_parameters_pk primary key (item_number,workflow_id)
  , constraint mst_work_parameters_fk1 foreign key (workflow_id) references mst_workflow(workflow_id) on delete cascade
) ;

comment on table mst_work_parameters is '��ƃp�����[�^';
comment on column mst_work_parameters.item_number is '�i��';
comment on column mst_work_parameters.workflow_id is '�H����ID';
comment on column mst_work_parameters.work_parameter is '��ƃp�����[�^';
