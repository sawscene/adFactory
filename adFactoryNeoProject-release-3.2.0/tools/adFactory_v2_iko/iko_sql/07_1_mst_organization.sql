

--�@��������������������������������

--�@���@�@�@�ڍs��DB�Ŏ��{�@

--�@��������������������������������


-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- �g�D�}�X�^
-- �菇1:�@�g�D�}�X�^�̈ڍs

insert 
into mst_organization
select

a_organization_id
,a_organization_name
,a_organization_identify
,a_authority_type
,a_language_type
,a_pass_word
,a_mail_address
,a_fk_update_person_id
,a_update_datetime
,a_remove_flag
,a_work_skill
,b_parent_id

---  ���@�ǉ����
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp 
    from
      VW_DBLIK_adFactoryDB_organization_add_info 
    where
      fk_master_id = a_organization_id
    order by disp
  ) JSON_REC
)
 ---  ���@�T�[�r�X���@���@���̐ݒ�
,null
,1


from dblink
(
'DBLINK_adFactoryDB',
'select
a.organization_id
,a.organization_name
,a.organization_identify
,a.authority_type
,a.language_type
,a.pass_word
,a.mail_address
,a.fk_update_person_id
,a.update_datetime
,a.remove_flag
,a.work_skill
,b.parent_id
from 
mst_organization a LEFT JOIN tre_organization_hierarchy b on  a.organization_id = b.child_id
'
) 
as LINK1
(
a_organization_id int
,a_organization_name text
,a_organization_identify text
,a_authority_type text
,a_language_type text
,a_pass_word text
,a_mail_address text
,a_fk_update_person_id int
,a_update_datetime timestamp
,a_remove_flag boolean
,a_work_skill text
,b_parent_id int
)

;




--- �g�D�}�X�^�@�_���폜�t���O��OFF�Őe�g�DID��NULL�̃f�[�^��0�ɍX�V�@*�_���폜�t���O��ON�̃f�[�^��NULL�̂܂܂Ƃ���

UPDATE mst_organization
set
parent_organization_id=0

where
parent_organization_id is null
and remove_flag = false
;










