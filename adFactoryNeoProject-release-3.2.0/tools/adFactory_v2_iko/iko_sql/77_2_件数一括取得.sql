
-- Œ”ˆêŠ‡æ“¾

select relname, n_live_tup  from pg_stat_user_tables where schemaname='public'
and relname not like ('$$%')
order by relname;



select relname , n_live_tup , n_dead_tup  from pg_stat_user_tables where schemaname='public'
--and relname not like ('$$%')
order by relname;


