
--�@���b�N�m�F

SELECT l.pid, db.datname, c.relname, l.locktype, l.mode
FROM pg_locks l
        LEFT JOIN pg_class c ON l.relation=c.relfilenode
        LEFT JOIN pg_database db ON l.database = db.oid
ORDER BY l.pid;



--�@���b�N����
--???? = pid
SELECT pg_cancel_backend(?????);
