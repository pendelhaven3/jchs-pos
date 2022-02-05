create table USER (
  ID integer auto_increment,
  USERNAME varchar(15) not null,
  PASSWORD varchar(100) not null,
  SUPERVISOR_IND char(1) default 'Y' not null,
  MODIFY_PRICING_IND char(1) default 'Y' not null,
  constraint USER$PK primary key (ID),
  constraint USER$UK unique (USERNAME)
);

insert into USER
VALUES
(1, 'ADMIN', '9ojpu4j2w2obEO2QfwNaL3QhuhmNpWjgONyQ6LPlUVE=', 'Y', 'Y');

update PURCHASE_ORDER set CREATED_BY = 1;

alter table PURCHASE_ORDER modify column CREATED_BY integer not null;

update RECEIVING_RECEIPT set RECEIVED_BY = 1;

alter table RECEIVING_RECEIPT modify column RECEIVED_BY integer not null;

alter table PRODUCT add ACTIVE_IND char(1) default 'Y';

alter table PRODUCT add AVAIL_QTY integer(4) default 0 not null;
