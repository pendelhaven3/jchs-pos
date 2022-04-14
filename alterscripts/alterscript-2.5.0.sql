alter table TRISYS_SALES_IMPORT add STATUS varchar(20) null;
alter table TRISYS_SALES_IMPORT add FAILED_LINE varchar(500) null;
update TRISYS_SALES_IMPORT set STATUS = 'SUCCESS';
