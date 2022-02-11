create table TRISYS_SALES_IMPORT (
  ID integer auto_increment,
  FILE varchar(100) not null,
  IMPORT_DT datetime null,
  IMPORT_BY integer null,
  constraint TRISYS_SALES_IMPORT$PK primary key (ID),
  constraint TRISYS_SALES_IMPORT$UK unique (FILE),
  constraint TRISYS_SALES_IMPORT$FK foreign key (IMPORT_BY) references USER (ID)
);

create table TRISYS_SALES (
  ID integer auto_increment,
  TRISYS_SALES_IMPORT_ID integer not null,
  SALE_NO varchar(20) not null,
  TERMINAL varchar(20) not null,  
  SALE_DT date not null,
  constraint TRISYS_SALES$PK primary key (ID),
  constraint TRISYS_SALES$UK unique (SALE_NO),
  constraint TRISYS_SALES$FK foreign key (TRISYS_SALES_IMPORT_ID) references TRISYS_SALES_IMPORT (ID)
);

create table TRISYS_SALES_ITEM (
  ID integer auto_increment,
  TRISYS_SALES_ID integer not null,
  PRODUCT_CODE varchar(20) not null,
  QUANTITY integer not null,
  UNIT_COST numeric(10, 2) not null,
  SELL_PRICE numeric(10, 2) not null,
  constraint TRISYS_SALES_ITEM$PK primary key (ID),
  constraint TRISYS_SALES_ITEMS$UK unique (TRISYS_SALES_ID, PRODUCT_CODE),
  constraint TRISYS_SALES_ITEM$FK foreign key (TRISYS_SALES_ID) references TRISYS_SALES (ID)
);

