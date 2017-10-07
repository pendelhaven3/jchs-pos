alter table PRODUCT add UNIT_CODE varchar(10) not null;
alter table PRODUCT add UNIT_CODE1 varchar(10) null;
alter table PRODUCT add UNIT_QUANTITY numeric(10) not null;
alter table PRODUCT add UNIT_QUANTITY1 numeric(10) null;

create table SEQUENCE (
  NAME varchar(50) not null,
  VALUE integer default 0 not null,
  constraint SEQUENCE$PK unique (NAME)
);

insert into SEQUENCE (NAME) values ('PURCHASE_ORDER_NO_SEQ');
insert into SEQUENCE (NAME) values ('RECEIVING_RECEIPT_NO_SEQ');

create table PAYMENT_TERM (
  ID integer auto_increment,
  NAME varchar(50),
  NUMBER_OF_DAYS integer(3),
  constraint PAYMENT_TERM$PK primary key (ID),
  constraint PAYMENT_tERM$UK unique (NAME)
);

create table SUPPLIER (
  ID integer auto_increment,
  CODE varchar(15) not null,
  NAME varchar(50) not null,
  ADDRESS varchar(200) null,
  CONTACT_NUMBER varchar(100) null,
  CONTACT_PERSON varchar(100) null,
  FAX_NUMBER varchar(100) null,
  EMAIL_ADDRESS varchar(50) null,
  TIN varchar(20) null,
  PAYMENT_TERM_ID integer null,
  REMARKS varchar(200) null,
  DISCOUNT varchar(30) null,
  VAT_INCLUSIVE char(1) not null,
  constraint SUPPLIER$PK primary key (ID),
  constraint SUPPLIER$UK unique (NAME),
  constraint SUPPLIER$UK2 unique (CODE),
  constraint SUPPLIER$FK foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID)
);

create table PURCHASE_ORDER (
  ID integer auto_increment,
  PURCHASE_ORDER_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  DELIVERY_IND char(1) default 'N' not null,
  PAYMENT_TERM_ID integer null,
  REMARKS varchar(100) null,
  REFERENCE_NO varchar(30) null,
  POST_DT date null,
  CREATED_BY integer null,
  VAT_INCLUSIVE char(1) default 'Y' not null,
  constraint PURCHASE_ORDER$PK primary key (ID),
  constraint PURCHASE_ORDER$UK unique (PURCHASE_ORDER_NO),
  constraint PURCHASE_ORDER$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint PURCHASE_ORDER$FK2 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID)
);

create table PURCHASE_ORDER_ITEM (
  ID integer auto_increment,
  PURCHASE_ORDER_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer(4) not null,
  COST numeric(10, 2) not null,
  ACTUAL_QUANTITY integer(4) null,
  ORDER_IND char(1) default 'N' not null,
  constraint PURCHASE_ORDER_ITEM$PK primary key (ID),
  constraint PURCHASE_ORDER_ITEM$FK foreign key (PURCHASE_ORDER_ID) references PURCHASE_ORDER (ID),
  constraint PURCHASE_ORDER_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);

create table RECEIVING_RECEIPT (
  ID integer auto_increment,
  RECEIVING_RECEIPT_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  POST_BY integer null,
  PAYMENT_TERM_ID integer not null,
  REMARKS varchar(100) null,
  REFERENCE_NO varchar(30) null,
  RECEIVED_DT datetime not null,
  RECEIVED_BY integer null,
  RELATED_PURCHASE_ORDER_NO integer not null,
  VAT_INCLUSIVE char(1) not null,
  VAT_RATE numeric(4, 2) not null,
  CANCEL_IND char(1) default 'N' not null,
  CANCEL_DT date null,
  CANCEL_BY integer null,
  constraint RECEIVING_RECEIPT$PK primary key (ID),
  constraint RECEIVING_RECEIPT$UK unique (RECEIVING_RECEIPT_NO),
  constraint RECEIVING_RECEIPT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint RECEIVING_RECEIPT$FK2 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID),
  constraint RECEIVING_RECEIPT$FK4 foreign key (RELATED_PURCHASE_ORDER_NO) references PURCHASE_ORDER (PURCHASE_ORDER_NO)
);

create table RECEIVING_RECEIPT_ITEM (
  ID integer auto_increment,
  RECEIVING_RECEIPT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer(4) not null,
  COST numeric(10, 2) not null,
  DISCOUNT_1 numeric(4, 2) default 0 not null,
  DISCOUNT_2 numeric(4, 2) default 0 not null,
  DISCOUNT_3 numeric(4, 2) default 0 not null,
  FLAT_RATE_DISCOUNT numeric(8, 2) default 0 not null,
  CURRENT_COST numeric(10, 2) null,
  constraint RECEIVING_RECEIPT_ITEM$PK primary key (ID),
  constraint RECEIVING_RECEIPT_ITEM$FK foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID),
  constraint RECEIVING_RECEIPT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT (ID)
);
