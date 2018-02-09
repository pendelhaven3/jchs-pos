insert into SEQUENCE (NAME) values ('PURCHASE_RETURN_NO_SEQ');

create table PURCHASE_RETURN (
  ID integer auto_increment,
  PURCHASE_RETURN_NO integer not null,
  RECEIVING_RECEIPT_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  REMARKS varchar(100) null,
  PAID_IND char(1) default 'N' not null,
  PAID_DT date null,
  constraint PURCHASE_RETURN$PK primary key (ID),
  constraint PURCHASE_RETURN$UK unique (PURCHASE_RETURN_NO),
  constraint PURCHASE_RETURN$FK foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID)
);

create table PURCHASE_RETURN_ITEM (
  ID integer auto_increment,
  PURCHASE_RETURN_ID integer not null,
  RECEIVING_RECEIPT_ITEM_ID integer not null,
  QUANTITY integer not null,
  constraint PURCHASE_RETURN_ITEM$PK primary key (ID),
  constraint PURCHASE_RETURN_ITEM$FK foreign key (PURCHASE_RETURN_ID) references PURCHASE_RETURN (ID),
  constraint PURCHASE_RETURN_ITEM$FK2 foreign key (RECEIVING_RECEIPT_ITEM_ID) references RECEIVING_RECEIPT_ITEM (ID)
);
