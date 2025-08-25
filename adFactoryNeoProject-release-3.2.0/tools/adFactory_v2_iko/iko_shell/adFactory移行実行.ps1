

#********************************************
#* adFactory　DB移行 実行のためのCD
#********************************************


## sqlファイルのdirへ移動
## ★　実施する環境に変更する事！


echo "★　実行dirへcd"
## cd C:\Users\seo\Desktop\SVN_adFactory\trunk\070_移行\SQL
$SQLPATH="C:\adFactory_v2_iko\iko_sql\"

echo $ENV:SQLPATH

cd $ENV:SQLPATH

#********************************************
#* adFactory　DB移行 実行
#********************************************




## sqlファイルの実行

## 例　psql -d adFactoryDB_V2 -U postgres -f END_VACUUM.sql


echo " ★　　adFactory　リファクタリング移行(SQL)　START　★" ; date



echo "■　1/51　■　01_0_iko_hierarchy.sql : 移行用階層ID新旧対応表" ; date

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


echo "■　2/51　■　01_1_mst_hierarchy.sql : 階層マスタ" ; date
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


echo "■　3/51　■　01_2_con_hierarchy.sql : 階層関連付け" ; date
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


echo "■　4/51　■　02_0_IKO_iko_reason.sql : 移行用理由ID新旧対応表" ; date
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


echo "■　5/51　■　02_1_mat_reason.sql : 理由マスタ" ; date
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

echo "■　6/51　■　03_0_iko_mst_work_property_copy.sql : 工程マスタプロパティのコピー" ; date
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

echo "■　7/51　■　03_1_mst_work.sql : 工程マスタ" ; date
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


echo "■　8/51　■　03_3_mst_work_section.sql : 工程セクション" ; date
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

##echo "■　9/51　■　04_0_mst_workflow_json_view.sql : 工程順マスタ view"  ; date
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


echo "■　10/51　■　04_1_mst_workflow.sql : 工程順マスタ" ; date
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


echo "■　11/51　■　04_2_con_workflow_work.sql : 工程順工程関連付け" ; date
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


echo "■　12/51　■　04_3_con_work_equipment.sql : 工程・設備関連付け" ; date
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


echo "■　13/51　■　04_4_con_work_organization.sql : 工程・組織関連付け" ; date
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


echo "■　14/51　■　05_0_iko_mst_equipment_setting.sql : 移行用設備マスタ設定項目" ; date
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


##echo "■　15/51　■　05_0_mst_equipment_json_view.sql : 移行用設備マスタ設定項目" ; date
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


echo "■　16/51　■　05_1_mst_equipment.sql : 設備マスタ" ; date
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


echo "■　17/51　■　05_2_mst_equipment_type.sql : 設備種別マスタ" ; date
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


echo "■　18/51　■　06_1_mst_object.sql : モノマスタ" ; date
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


echo "■　19/51　■　06_2_mst_object_type.sql : モノ種別マスタ" ; date
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


##echo "■　20/51　■　07_0_mst_organization_json_view.sql : 組織マスタ view" ; date
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


echo "■　21/51　■　07_1_mst_organization.sql : 組織マスタ" ; date
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


echo "■22/51　■　07_2_con_organization_breaktime.sql : 組織・休憩関連付け" ; date
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


echo "■　23/51　■　07_3_con_organization_role.sql : 組織・役割関連付け" ; date
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


echo "■　24/51　■　07_4_con_organization_work_category.sql : 組織・作業区分関連付け" ; date
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


echo "■　25/51　■　08_1_mst_displayed_status.sql : ステータス表示マスタ" ; date
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


echo "■　26/51　■　09_0_iko_mst_role_authority.sql : 移行用役割権限マスタ" ; date
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


echo "■　27/51　■　09_1_mst_role_authority.sql : 役割権限マスタ" ; date
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


echo "■　28/51　■　10_1_mst_holiday.sql : 休日情報" ; date
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



echo "■　29/51　■　11_1_mst_schedule.sql : 予定情報" ; date
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


echo "■　30/51　■　12_1_mst_breaktime.sql : 休憩マスタ設定項目" ; date
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


echo "■　31/51　■　13_1_mst_indirect_work.sql : 間接作業マスタ" ; date
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


echo "■　32/51　■　14_1_mst_authentication_info.sql : 認証情報" ; date
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


echo "■　33/51　■　15_1_mst_work_category.sql : 作業区分マスタ" ; date
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



echo "■　34/51　■　16_1_trn_access_hierarchy.sql : 階層アクセス権" ; date
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


echo "■　35/51　■　17_1_tre_kanban_hierarchy.sql : カンバン階層" ; date
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


echo "■　36/51　■　18_1_mst_kanban_hierarchy.sql : カンバン階層マスタ" ; date
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


echo "■　37/51　■　19_1_con_kanban_hierarchy.sql : カンバン階層関連付け" ; date
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



echo "■　38/51　■　20_0_iko_trn_kanban_property_copy.sql : カンバンプロパティのコピー" ; date
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


echo "■　39/51　■　20_1_trn_kanban.sql : カンバン" ; date
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


echo "■　40/51　■　21_1_con_workkanban_equipment.sql : 工程カンバン・設備関連付け" ; date
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


echo "■　41/51　■　22_1_con_workkanban_organization.sql : 工程カンバン・組織関連付け" ; date
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


echo "■　42/51　■　23_0_iko_trn_work_kanban_property_copy.sql : 工程カンバン プロパティ　copy" ; date
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

##echo "■　43/51　■　23_0_trn_work_kanban_json_view.sql : 工程カンバン　view　　不要" ; date
##psql -f     23_0_trn_work_kanban_json_view.sql


echo "■　44/51　■　23_1_trn_work_kanban.sql : 工程カンバン" ; date
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


echo "■　45/51　■　24_1_trn_work_kanban_working.sql : 工程カンバン作業中リスト" ; date
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


echo "■　46/51　■　25_0_iko_trn_actual_property_copy.sql : 工程実績　工程実績プロパティ　copy" ; date
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



echo "■　47/51　■　25_1_trn_actual_result.sql : 工程実績" ; date
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


##echo "■　47/51　■　26_0_iko_trn_actual_property_copy.sql : 工程実績付加情報　工程実績プロパティ　copy" ; date
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


##echo "■　48/51　■　26_1_trn_actual_adition.sql : 工程実績付加情報" ; date ⇒　ここでは検査結果、サービス情報は持たない。画像データのみである為、移行対象外とする
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


echo "■　49/51　■　27_1_trn_indirect_actual.sql : 間接工数実績" ; date
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


##echo "■　50/51　■　28_1_tmp_warehouse_inventory_actual.sql : 棚卸実績参照用テンポラリ" ; date ⇒　倉庫用テーブルの為、移行対象外とする
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






echo "■　51/51　■　00_0_AFTER_org_seq.sql : 既存シーケンスの内容設定" ; date
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





echo "■　移行後始末　■　99_移行用テーブル_VIEW削除.sql : 移行用VIEW削除" ; date
psql -f     99_移行用テーブル_VIEW削除.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }



echo "■　移行後始末　■　99_移行用テーブルのdrop.sql : 移行用に作成したテーブルの削除" ; date
psql -f     99_移行用テーブルのdrop.sql
 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }





echo "■　最終　■　99_END_VACUUM.sql : バキューム" ; date
psql -f     99_END_VACUUM.sql










echo " ★　adFactory　リファクタリング移行(SQL)　ENDT　★"
date





