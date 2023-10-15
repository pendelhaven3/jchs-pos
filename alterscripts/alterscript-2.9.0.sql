create table RECEIVE_DELIVERY (
  ID integer auto_increment,
  SUPPLIER_ID integer not null,
  RECEIVE_DT datetime not null,
  RECEIVE_BY integer not null,
  REMARKS varchar(100) null,
  POSTED boolean default false not null,
  POST_DT datetime null,
  POST_BY integer null,	
  constraint RECEIVE_DELIVERY$PK primary key (ID),
  constraint RECEIVE_DELIVERY$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint RECEIVE_DELIVERY$FK2 foreign key (RECEIVE_BY) references USER (ID)
);

create table RECEIVE_DELIVERY_ITEM (
  ID integer auto_increment,
  RECEIVE_DELIVERY_ID integer not null,
  CODE varchar(20) not null,
  UNIT varchar(4) not null,
  QUANTITY integer(4) not null,
  constraint RECEIVE_DELIVERY_ITEM$PK primary key (ID),
  constraint RECEIVE_DELIVERY_ITEM$UK unique (ID, CODE),
  constraint RECEIVE_DELIVERY_ITEM$FK foreign key (RECEIVE_DELIVERY_ID) references RECEIVE_DELIVERY (ID)
);
