alter table PRODUCT add SKU_CASE varchar(14) null;
alter table PRODUCT add SKU_TIES varchar(14) null;
alter table PRODUCT add SKU_PACK varchar(14) null;
alter table PRODUCT add SKU_HDZN varchar(14) null;
alter table PRODUCT add SKU_PCS varchar(14) null;

create index PRODUCT$IDX on PRODUCT (SKU_CASE);
create index PRODUCT$IDX2 on PRODUCT (SKU_TIES);
create index PRODUCT$IDX3 on PRODUCT (SKU_PACK);
create index PRODUCT$IDX4 on PRODUCT (SKU_HDZN);
create index PRODUCT$IDX5 on PRODUCT (SKU_PCS);

alter table PRODUCT add UNIT_IND_CASE char(1) default 'N' not null;
alter table PRODUCT add UNIT_IND_TIES char(1) default 'N' not null;
alter table PRODUCT add UNIT_IND_PACK char(1) default 'N' not null;
alter table PRODUCT add UNIT_IND_HDZN char(1) default 'N' not null;
alter table PRODUCT add UNIT_IND_PCS char(1) default 'N' not null;

alter table PRODUCT add ACTIVE_UNIT_IND_CASE char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_TIES char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_PACK char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_HDZN char(1) default 'N' not null;
alter table PRODUCT add ACTIVE_UNIT_IND_PCS char(1) default 'N' not null;

alter table PRODUCT add AVAIL_QTY_CASE integer(4) default 0 not null;
alter table PRODUCT add AVAIL_QTY_TIES integer(4) default 0 not null;
alter table PRODUCT add AVAIL_QTY_PACK integer(4) default 0 not null;
alter table PRODUCT add AVAIL_QTY_HDZN integer(4) default 0 not null;
alter table PRODUCT add AVAIL_QTY_PCS integer(4) default 0 not null;

alter table PRODUCT add UNIT_CONV_CASE integer(5) null;
alter table PRODUCT add UNIT_CONV_TIES integer(5) null;
alter table PRODUCT add UNIT_CONV_PACK integer(5) null;
alter table PRODUCT add UNIT_CONV_HDZN integer(5) null;
alter table PRODUCT add UNIT_CONV_PCS integer(5) null;

alter table PRODUCT add GROSS_COST_CASE numeric(10, 2) default 0 not null;
alter table PRODUCT add GROSS_COST_TIES numeric(10, 2) default 0 not null;
alter table PRODUCT add GROSS_COST_PACK numeric(10, 2) default 0 not null;
alter table PRODUCT add GROSS_COST_HDZN numeric(10, 2) default 0 not null;
alter table PRODUCT add GROSS_COST_PCS numeric(10, 2) default 0 not null;

alter table PRODUCT add FINAL_COST_CASE numeric(10, 2) default 0 not null;
alter table PRODUCT add FINAL_COST_TIES numeric(10, 2) default 0 not pj	donull;
alter table PRODUCT add FINAL_COST_PACK numeric(10, 2) default 0 not null;
alter table PRODUCT add FINAL_COST_HDZN numeric(10, 2) default 0 not null;
alter table PRODUCT add FINAL_COST_PCS numeric(10, 2) default 0 not null;

alter table PRODUCT drop column AVAIL_QTY;

update PRODUCT set UNIT_IND_CASE = 'Y', SKU_CASE = CODE, ACTIVE_UNIT_IND_CASE = ACTIVE_IND where UOM_CODE = 'CASE';
update PRODUCT set UNIT_IND_PACK = 'Y', SKU_PACK = CODE, ACTIVE_UNIT_IND_PACK = ACTIVE_IND where UOM_CODE = 'PACK';
update PRODUCT set UNIT_IND_HDZN = 'Y', SKU_HDZN = CODE, ACTIVE_UNIT_IND_HDZN = ACTIVE_IND where UOM_CODE = 'HDZN';
update PRODUCT set UNIT_IND_PCS = 'Y', SKU_PCS = CODE, ACTIVE_UNIT_IND_PCS = ACTIVE_IND where UOM_CODE = 'PCS';
