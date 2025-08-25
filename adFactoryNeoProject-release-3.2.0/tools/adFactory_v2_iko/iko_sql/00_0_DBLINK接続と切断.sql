

--　DBリンクのインストール

create extension dblink;

--　■　■　旧DBへの接続

-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- データベース　切断
select dblink_disconnect('DBLINK_adFactoryDB');



-- データベース　クライアントエンコーディングの確認と変更
select current_setting('client_encoding');

set client_encoding to 'utf8';
