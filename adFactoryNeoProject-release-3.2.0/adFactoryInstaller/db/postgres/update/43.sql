-- 組織・理由区分関連付け
create table con_organization_reason (
  organization_id bigint not null
  , reason_category_id bigint not null
  , reason_type integer not null
  , constraint con_organization_reason_pk primary key (organization_id,reason_category_id)
) ;

comment on table con_organization_reason is '組織・理由区分関連付け';
comment on column con_organization_reason.organization_id is '組織ID';
comment on column con_organization_reason.reason_category_id is '理由区分ID';
comment on column con_organization_reason.reason_type is '理由種別';

