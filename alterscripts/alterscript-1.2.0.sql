insert into SEQUENCE (NAME) values ('PURCHASE_PAYMENT_NO_SEQ');
insert into SEQUENCE (NAME) values ('PURCHASE_PAYMENT_ADJUSTMENT_NO_SEQ');
insert into SEQUENCE (NAME) values ('CREDIT_CARD_STATEMENT_NO_SEQ');

create table CREDIT_CARD (
  ID integer auto_increment,
  USER varchar(20) not null,
  BANK varchar(20) not null,
  CARD_NUMBER varchar(20) not null,
  CUTOFF_DT integer null,
  CUSTOMER_NUMBER varchar(30) null,
  constraint CREDIT_CARD$PK primary key (ID)
);

create table PURCHASE_PAYMENT_ADJ_TYPE (
  ID integer auto_increment,
  CODE varchar(12) not null,
  DESCRIPTION varchar(100) not null,
  constraint PURCHASE_PAYMENT_ADJ_TYPE$PK primary key (ID),
  constraint PURCHASE_PAYMENT_ADJ_TYPE$UK unique (CODE)
);

create table PURCHASE_PAYMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  CREATE_DT date not null,
  ENCODER integer null,
  CANCEL_IND char(1) default 'N' not null,
  CANCEL_DT date null,
  CANCEL_BY integer null,
  constraint PURCHASE_PAYMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT$UK unique (PURCHASE_PAYMENT_NO),
  constraint PURCHASE_PAYMENT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID)
);

create table PURCHASE_PAYMENT_RECEIVING_RECEIPT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  RECEIVING_RECEIPT_ID integer not null,
  constraint PURCHASE_PAYMENT_RECEIVING_RECEIPT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_RECEIVING_RECEIPT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID),
  constraint PURCHASE_PAYMENT_RECEIVING_RECEIPT$FK2 foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID)
);

create table PURCHASE_PAYMENT_CASH_PAYMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  PAID_DT date not null,
  PAID_BY integer null,
  constraint PURCHASE_PAYMENT_CASH_PAYMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_CASH_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID)
);

create table PURCHASE_PAYMENT_CHECK_PAYMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  BANK varchar(30) not null,
  CHECK_DT date not null,
  CHECK_NO varchar(50) not null,
  AMOUNT numeric(10, 2) not null,
  constraint PURCHASE_PAYMENT_CHECK_PAYMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_CHECK_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID)
);

create table PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  CREDIT_CARD_ID integer not null,
  TRANSACTION_DT date not null,
  APPROVAL_CODE varchar(20) not null,
  constraint PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID),
  constraint PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT$FK2 foreign key (CREDIT_CARD_ID) references CREDIT_CARD (ID)
);

create table PURCHASE_PAYMENT_ADJUSTMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ADJUSTMENT_NO integer not null,
  SUPPLIER_ID integer not null,
  PURCHASE_PAYMENT_ADJ_TYPE_ID integer not null,
  AMOUNT numeric(8, 2) not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  constraint PURCHASE_PAYMENT_ADJUSTMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_ADJUSTMENT$UK unique (PURCHASE_PAYMENT_ADJUSTMENT_NO),
  constraint PURCHASE_PAYMENT_ADJUSTMENT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint PURCHASE_PAYMENT_ADJUSTMENT$FK2 foreign key (PURCHASE_PAYMENT_ADJ_TYPE_ID) references PURCHASE_PAYMENT_ADJ_TYPE (ID)
);

create table PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  PURCHASE_PAYMENT_ADJ_TYPE_ID integer not null,
  REFERENCE_NO varchar(30) not null,
  AMOUNT numeric(10, 2) not null,
  constraint PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID),
  constraint PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT$FK2 foreign key (PURCHASE_PAYMENT_ADJ_TYPE_ID) references PURCHASE_PAYMENT_ADJ_TYPE (ID)
);

create table PURCHASE_PAYMENT_BANK_TRANSFER (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  BANK varchar(20) not null,
  REFERENCE_NO varchar(20) not null,
  AMOUNT numeric(10, 2) not null,
  TRANSFER_DT date not null,
  constraint PURCHASE_PAYMENT_BANK_TRANSFER$PK primary key (ID),
  constraint PURCHASE_PAYMENT_BANK_TRANSFER$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID)
);

create table CREDIT_CARD_STATEMENT (
  ID integer auto_increment,
  CUSTOMER_NUMBER varchar(30) not null,
  CREDIT_CARD_ID integer null,
  STATEMENT_DT date not null,
  POST_IND varchar(1) default 'N' not null,
  primary key (ID)
);

create table CREDIT_CARD_STATEMENT_ITEM (
  ID integer auto_increment,
  CREDIT_CARD_STATEMENT_ID integer not null,
  PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID integer not null,
  primary key (ID),
  constraint CREDIT_CARD_STATEMENT_ITEM$FK foreign key (CREDIT_CARD_STATEMENT_ID) references CREDIT_CARD_STATEMENT (ID),
  constraint CREDIT_CARD_STATEMENT_ITEM$FK2 foreign key (PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID) references PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT (ID)
);

create table CREDIT_CARD_PAYMENT (
  ID integer auto_increment,
  CREDIT_CARD_ID integer not null,
  primary key (ID),
  constraint CREDIT_CARD_PAYMENT$FK foreign key (CREDIT_CARD_ID) references CREDIT_CARD (ID)
);

create table CREDIT_CARD_STATEMENT_PAYMENT (
  ID integer auto_increment,
  CREDIT_CARD_STATEMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  PAYMENT_DT date not null,
  PAYMENT_TYPE varchar(20) not null,
  REMARKS varchar(100) not null,
  primary key (ID),
  constraint CREDIT_CARD_STATEMENT_PAYMENT$FK foreign key (CREDIT_CARD_STATEMENT_ID) references CREDIT_CARD_STATEMENT (ID)
);
