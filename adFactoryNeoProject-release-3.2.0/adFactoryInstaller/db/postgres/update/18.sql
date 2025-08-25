-- 「資材情報」テーブルにカラムを追加する。
ALTER TABLE trn_material ADD spec character varying(128); -- 型式・仕様
ALTER TABLE trn_material ADD unit_no character varying(64); -- ユニット番号
ALTER TABLE trn_material ADD note character varying(256); -- 備考

comment on column trn_material.spec is '型式・仕様';
comment on column trn_material.unit_no is 'ユニット番号';
comment on column trn_material.note is '備考';
