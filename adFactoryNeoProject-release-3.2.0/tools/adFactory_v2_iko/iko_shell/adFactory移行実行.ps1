

#********************************************
#* adFactory�@DB�ڍs ���s�̂��߂�CD
#********************************************


## sql�t�@�C����dir�ֈړ�
## ���@���{������ɕύX���鎖�I


echo "���@���sdir��cd"
## cd C:\Users\seo\Desktop\SVN_adFactory\trunk\070_�ڍs\SQL
$SQLPATH="C:\adFactory_v2_iko\iko_sql\"

echo $ENV:SQLPATH

cd $ENV:SQLPATH

#********************************************
#* adFactory�@DB�ڍs ���s
#********************************************




## sql�t�@�C���̎��s

## ��@psql -d adFactoryDB_V2 -U postgres -f END_VACUUM.sql


echo " ���@�@adFactory�@���t�@�N�^�����O�ڍs(SQL)�@START�@��" ; date



echo "���@1/51�@���@01_0_iko_hierarchy.sql : �ڍs�p�K�wID�V���Ή��\" ; date

psql -f 01_0_iko_hierarchy.sql 
if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@2/51�@���@01_1_mst_hierarchy.sql : �K�w�}�X�^" ; date
psql -f 01_1_mst_hierarchy.sql 
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@3/51�@���@01_2_con_hierarchy.sql : �K�w�֘A�t��" ; date
psql -f     01_2_con_hierarchy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@4/51�@���@02_0_IKO_iko_reason.sql : �ڍs�p���RID�V���Ή��\" ; date
psql -f     02_0_IKO_iko_reason.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@5/51�@���@02_1_mat_reason.sql : ���R�}�X�^" ; date
psql -f     02_1_mat_reason.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }

echo "���@6/51�@���@03_0_iko_mst_work_property_copy.sql : �H���}�X�^�v���p�e�B�̃R�s�[" ; date
psql -f     03_0_iko_mst_work_property_copy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }

echo "���@7/51�@���@03_1_mst_work.sql : �H���}�X�^" ; date
psql -f     03_1_mst_work.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@8/51�@���@03_3_mst_work_section.sql : �H���Z�N�V����" ; date
psql -f     03_3_mst_work_section.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }

##echo "���@9/51�@���@04_0_mst_workflow_json_view.sql : �H�����}�X�^ view"  ; date
##psql -f     04_0_mst_workflow_json_view.sql
## if ($? -ne $True)
##    {
##        echo "***** ERROR *****" $?
##        exit
##    }
##    else
##    {
##        echo "***** OK *****"
##    }


echo "���@10/51�@���@04_1_mst_workflow.sql : �H�����}�X�^" ; date
psql -f     04_1_mst_workflow.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@11/51�@���@04_2_con_workflow_work.sql : �H�����H���֘A�t��" ; date
psql -f     04_2_con_workflow_work.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@12/51�@���@04_3_con_work_equipment.sql : �H���E�ݔ��֘A�t��" ; date
psql -f     04_3_con_work_equipment.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@13/51�@���@04_4_con_work_organization.sql : �H���E�g�D�֘A�t��" ; date
psql -f     04_4_con_work_organization.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@14/51�@���@05_0_iko_mst_equipment_setting.sql : �ڍs�p�ݔ��}�X�^�ݒ荀��" ; date
psql -f     05_0_iko_mst_equipment_setting.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


##echo "���@15/51�@���@05_0_mst_equipment_json_view.sql : �ڍs�p�ݔ��}�X�^�ݒ荀��" ; date
##psql -f     05_0_mst_equipment_json_view.sql
## if ($? -ne $True)
##    {
##        echo "***** ERROR *****" $?
##        exit
##    }
##    else
##    {
##        echo "***** OK *****"
##    }


echo "���@16/51�@���@05_1_mst_equipment.sql : �ݔ��}�X�^" ; date
psql -f     05_1_mst_equipment.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@17/51�@���@05_2_mst_equipment_type.sql : �ݔ���ʃ}�X�^" ; date
psql -f     05_2_mst_equipment_type.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@18/51�@���@06_1_mst_object.sql : ���m�}�X�^" ; date
psql -f     06_1_mst_object.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@19/51�@���@06_2_mst_object_type.sql : ���m��ʃ}�X�^" ; date
psql -f     06_2_mst_object_type.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


##echo "���@20/51�@���@07_0_mst_organization_json_view.sql : �g�D�}�X�^ view" ; date
##psql -f     07_0_mst_organization_json_view.sql
## if ($? -ne $True)
##    {
##        echo "***** ERROR *****" $?
##        exit
##    }
##    else
##    {
##        echo "***** OK *****"
##    }


echo "���@21/51�@���@07_1_mst_organization.sql : �g�D�}�X�^" ; date
psql -f     07_1_mst_organization.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "��22/51�@���@07_2_con_organization_breaktime.sql : �g�D�E�x�e�֘A�t��" ; date
psql -f     07_2_con_organization_breaktime.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@23/51�@���@07_3_con_organization_role.sql : �g�D�E�����֘A�t��" ; date
psql -f     07_3_con_organization_role.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@24/51�@���@07_4_con_organization_work_category.sql : �g�D�E��Ƌ敪�֘A�t��" ; date
psql -f     07_4_con_organization_work_category.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@25/51�@���@08_1_mst_displayed_status.sql : �X�e�[�^�X�\���}�X�^" ; date
psql -f     08_1_mst_displayed_status.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@26/51�@���@09_0_iko_mst_role_authority.sql : �ڍs�p���������}�X�^" ; date
psql -f     09_0_iko_mst_role_authority.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@27/51�@���@09_1_mst_role_authority.sql : ���������}�X�^" ; date
psql -f     09_1_mst_role_authority.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@28/51�@���@10_1_mst_holiday.sql : �x�����" ; date
psql -f     10_1_mst_holiday.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }



echo "���@29/51�@���@11_1_mst_schedule.sql : �\����" ; date
psql -f     11_1_mst_schedule.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@30/51�@���@12_1_mst_breaktime.sql : �x�e�}�X�^�ݒ荀��" ; date
psql -f     12_1_mst_breaktime.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@31/51�@���@13_1_mst_indirect_work.sql : �Ԑڍ�ƃ}�X�^" ; date
psql -f     13_1_mst_indirect_work.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@32/51�@���@14_1_mst_authentication_info.sql : �F�؏��" ; date
psql -f     14_1_mst_authentication_info.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@33/51�@���@15_1_mst_work_category.sql : ��Ƌ敪�}�X�^" ; date
psql -f     15_1_mst_work_category.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }



echo "���@34/51�@���@16_1_trn_access_hierarchy.sql : �K�w�A�N�Z�X��" ; date
psql -f     16_1_trn_access_hierarchy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@35/51�@���@17_1_tre_kanban_hierarchy.sql : �J���o���K�w" ; date
psql -f     17_1_tre_kanban_hierarchy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@36/51�@���@18_1_mst_kanban_hierarchy.sql : �J���o���K�w�}�X�^" ; date
psql -f     18_1_mst_kanban_hierarchy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@37/51�@���@19_1_con_kanban_hierarchy.sql : �J���o���K�w�֘A�t��" ; date
psql -f     19_1_con_kanban_hierarchy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }



echo "���@38/51�@���@20_0_iko_trn_kanban_property_copy.sql : �J���o���v���p�e�B�̃R�s�[" ; date
psql -f     20_0_iko_trn_kanban_property_copy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@39/51�@���@20_1_trn_kanban.sql : �J���o��" ; date
psql -f     20_1_trn_kanban.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@40/51�@���@21_1_con_workkanban_equipment.sql : �H���J���o���E�ݔ��֘A�t��" ; date
psql -f     21_1_con_workkanban_equipment.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@41/51�@���@22_1_con_workkanban_organization.sql : �H���J���o���E�g�D�֘A�t��" ; date
psql -f     22_1_con_workkanban_organization.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@42/51�@���@23_0_iko_trn_work_kanban_property_copy.sql : �H���J���o�� �v���p�e�B�@copy" ; date
psql -f     23_0_iko_trn_work_kanban_property_copy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }

##echo "���@43/51�@���@23_0_trn_work_kanban_json_view.sql : �H���J���o���@view�@�@�s�v" ; date
##psql -f     23_0_trn_work_kanban_json_view.sql


echo "���@44/51�@���@23_1_trn_work_kanban.sql : �H���J���o��" ; date
psql -f     23_1_trn_work_kanban.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@45/51�@���@24_1_trn_work_kanban_working.sql : �H���J���o����ƒ����X�g" ; date
psql -f     24_1_trn_work_kanban_working.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


echo "���@46/51�@���@25_0_iko_trn_actual_property_copy.sql : �H�����с@�H�����уv���p�e�B�@copy" ; date
psql -f     25_0_iko_trn_actual_property_copy.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }



echo "���@47/51�@���@25_1_trn_actual_result.sql : �H������" ; date
psql -f     25_1_trn_actual_result.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


##echo "���@47/51�@���@26_0_iko_trn_actual_property_copy.sql : �H�����ѕt�����@�H�����уv���p�e�B�@copy" ; date
##psql -f     26_0_iko_trn_actual_property_copy.sql
## if ($? -ne $True)
##    {
##        echo "***** ERROR *****" $?
##        exit
##    }
##    else
##    {
##        echo "***** OK *****"
##    }


##echo "���@48/51�@���@26_1_trn_actual_adition.sql : �H�����ѕt�����" ; date �ˁ@�����ł͌������ʁA�T�[�r�X���͎����Ȃ��B�摜�f�[�^�݂̂ł���ׁA�ڍs�ΏۊO�Ƃ���
##psql -f     26_1_trn_actual_adition.sql
## if ($? -ne $True)
##    {
##        echo "***** ERROR *****" $?
##        exit
##    }
##    else
##    {
##        echo "***** OK *****"
##    }


echo "���@49/51�@���@27_1_trn_indirect_actual.sql : �ԐڍH������" ; date
psql -f     27_1_trn_indirect_actual.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }


##echo "���@50/51�@���@28_1_tmp_warehouse_inventory_actual.sql : �I�����юQ�Ɨp�e���|����" ; date �ˁ@�q�ɗp�e�[�u���ׁ̈A�ڍs�ΏۊO�Ƃ���
##psql -f     28_1_tmp_warehouse_inventory_actual.sql
## if ($? -ne $True)
##    {
##        echo "***** ERROR *****" $?
##        exit
##    }
##    else
##    {
##        echo "***** OK *****"
##    }






echo "���@51/51�@���@00_0_AFTER_org_seq.sql : �����V�[�P���X�̓��e�ݒ�" ; date
psql -f     00_0_AFTER_org_seq.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }





echo "���@�ڍs��n���@���@99_�ڍs�p�e�[�u��_VIEW�폜.sql : �ڍs�pVIEW�폜" ; date
psql -f     99_�ڍs�p�e�[�u��_VIEW�폜.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }



echo "���@�ڍs��n���@���@99_�ڍs�p�e�[�u����drop.sql : �ڍs�p�ɍ쐬�����e�[�u���̍폜" ; date
psql -f     99_�ڍs�p�e�[�u����drop.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }





echo "���@�ŏI�@���@99_END_VACUUM.sql : �o�L���[��" ; date
psql -f     99_END_VACUUM.sql










echo " ���@adFactory�@���t�@�N�^�����O�ڍs(SQL)�@ENDT�@��"
date





