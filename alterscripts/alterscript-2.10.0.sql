create table ECASH_TYPE (
  ID integer auto_increment,
  CODE varchar(50) not null,
  constraint ECASH_TYPE$PK primary key (ID),
  constraint ECASH_TYPE$UK unique (CODE)
);

insert into ECASH_TYPE values (1, 'GCASH');
insert into ECASH_TYPE values (2, 'MAYA');

create table ECASH_RECEIVER (
  ID integer auto_increment,
  NAME varchar(100) not null,
  ECASH_TYPE_ID integer not null,
  constraint ECASH_RECEIVER$PK primary key (ID),
  constraint ECASH_RECEIVER$UK unique (NAME),
  constraint ECASH_RECEIVER$FK foreign key (ECASH_TYPE_ID) references ECASH_TYPE (ID)
);

create table PURCHASE_PAYMENT_ECASH_PAYMENT (
  ID integer auto_increment,
  PURCHASE_PAYMENT_ID integer not null,
  AMOUNT numeric(10, 2) not null,
  ECASH_RECEIVER_ID integer not null,
  REFERENCE_NO varchar(50) not null,
  PAID_DT date not null,
  PAID_BY integer not null,
  constraint PURCHASE_PAYMENT_ECASH_PAYMENT$PK primary key (ID),
  constraint PURCHASE_PAYMENT_ECASH_PAYMENT$FK foreign key (PURCHASE_PAYMENT_ID) references PURCHASE_PAYMENT (ID),
  constraint PURCHASE_PAYMENT_ECASH_PAYMENT$FK2 foreign key (ECASH_RECEIVER_ID) references ECASH_RECEIVER (ID),
  constraint PURCHASE_PAYMENT_ECASH_PAYMENT$FK3 foreign key (PAID_BY) references USER (ID)
);
