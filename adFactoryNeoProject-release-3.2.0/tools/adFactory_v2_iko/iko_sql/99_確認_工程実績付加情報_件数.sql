
-- 実績件数
select count(*) from trn_actual_result;


-- 実績プロパティ
select fk_actual_id from trn_actual_property
group by fk_actual_id
;


-- 実績プロパティの無い実績
select *  from trn_actual_result a
where
not exists 
(
select 'X' from trn_actual_property b
where a.actual_id = b.fk_actual_id
)


;




select count(*) from trn_actual_adition;



