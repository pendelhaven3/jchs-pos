create table SEQUENCE (
  NAME varchar(50) not null,
  VALUE integer default 0 not null,
  constraint SEQUENCE$PK unique (NAME)
);

create table SYSTEM_PARAMETER (
  NAME varchar(50) not null,
  VALUE varchar(100) not null,
  constraint SYSTEM_PARAMETER$PK primary key (NAME)
);

create table USER (
  ID integer auto_increment,
  USERNAME varchar(15) not null,
  PASSWORD varchar(100) not null,
  SUPERVISOR_IND char(1) default 'Y' not null,
  MODIFY_PRICING_IND char(1) default 'Y' not null,
  constraint USER$PK primary key (ID),
  constraint USER$UK unique (USERNAME)
);

create table PRODUCT (
  ID integer auto_increment,
  CODE varchar(14) not null,
  DESCRIPTION varchar(40) not null,
  UOM_CODE varchar(10) not null,
  UOM_CODE1 varchar(10) null,
  UOM_QTY numeric(10) not null,
  UOM_QTY1 numeric(10) null,
  GROSS_COST numeric(9,2) default 0 not null,
  GROSS_COST1 numeric(9,2) null,
  FINAL_COST numeric(9,2) default 0 not null,
  FINAL_COST1 numeric(9,2) null,
  MAX_STOCK_LEVEL integer(4) default 0,
  MIN_STOCK_LEVEL integer(4) default 0,
  ACTIVE_IND char(1) default 'Y',
  CREATE_DT datetime default current_timestamp,
  constraint PRODUCT$PK primary key (ID),
  constraint PRODUCT$UK unique (CODE)
);

create table PRODUCT2 (
  ID integer auto_increment,
  DESCRIPTION varchar(50) not null,
  MAX_STOCK_LEVEL integer(4) default 0,
  MIN_STOCK_LEVEL integer(4) default 0,
  ACTIVE_IND char(1) default 'Y',
  UNIT_IND_CASE char(1) null,
  UNIT_IND_TIES char(1) null,
  UNIT_IND_PACK char(1) null,
  UNIT_IND_HDZN char(1) null,
  UNIT_IND_PCS char(1) null,
  ACTIVE_UNIT_IND_CASE char(1) default 'N' not null,
  ACTIVE_UNIT_IND_TIES char(1) default 'N' not null,
  ACTIVE_UNIT_IND_PACK char(1) default 'N' not null,
  ACTIVE_UNIT_IND_HDZN char(1) default 'N' not null,
  ACTIVE_UNIT_IND_PCS char(1) default 'N' not null,
  AVAIL_QTY_CASE integer(4) default 0 not null,
  AVAIL_QTY_TIES integer(4) default 0 not null,
  AVAIL_QTY_PACK integer(4) default 0 not null,
  AVAIL_QTY_HDZN integer(4) default 0 not null,
  AVAIL_QTY_PCS integer(4) default 0 not null,
  UNIT_CONV_CASE integer(5) null,
  UNIT_CONV_TIES integer(5) null,
  UNIT_CONV_PACK integer(5) null,
  UNIT_CONV_HDZN integer(5) null,
  UNIT_CONV_PCS integer(5) null,
  GROSS_COST_CASE numeric(10, 2) default 0 not null,
  GROSS_COST_TIES numeric(10, 2) default 0 not null,
  GROSS_COST_PACK numeric(10, 2) default 0 not null,
  GROSS_COST_HDZN numeric(10, 2) default 0 not null,
  GROSS_COST_PCS numeric(10, 2) default 0 not null,
  FINAL_COST_CASE numeric(10, 2) default 0 not null,
  FINAL_COST_TIES numeric(10, 2) default 0 not null,
  FINAL_COST_PACK numeric(10, 2) default 0 not null,
  FINAL_COST_HDZN numeric(10, 2) default 0 not null,
  FINAL_COST_PCS numeric(10, 2) default 0 not null,
  constraint PRODUCT2$PK primary key (ID)
);

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

create table SUPPLIER_PRODUCT (
  SUPPLIER_ID integer not null,
  PRODUCT_ID integer not null,
  constraint SUPPLIER_PRODUCT$PK primary key (SUPPLIER_ID, PRODUCT_ID),
  constraint SUPPLIER_PRODUCT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint SUPPLIER_PRODUCT$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
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
  CREATED_BY integer not null,
  VAT_INCLUSIVE char(1) default 'Y' not null,
  constraint PURCHASE_ORDER$PK primary key (ID),
  constraint PURCHASE_ORDER$UK unique (PURCHASE_ORDER_NO),
  constraint PURCHASE_ORDER$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint PURCHASE_ORDER$FK2 foreign key (PAYMENT_TERM_ID) references PAYMENT_TERM (ID),
  constraint PURCHASE_ORDER$FK3 foreign key (CREATED_BY) references USER (ID)
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
  constraint PURCHASE_ORDER_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
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
  RECEIVED_BY integer not null,
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
  constraint RECEIVING_RECEIPT$FK3 foreign key (RELATED_PURCHASE_ORDER_NO) references PURCHASE_ORDER (PURCHASE_ORDER_NO),
  constraint RECEIVING_RECEIPT$FK4 foreign key (RECEIVED_BY) references USER (ID),
  constraint RECEIVING_RECEIPT$FK5 foreign key (POST_BY) references USER (ID),
  constraint RECEIVING_RECEIPT$FK6 foreign key (CANCEL_BY) references USER (ID)
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
  constraint RECEIVING_RECEIPT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);

create table PURCHASE_RETURN (
  ID integer auto_increment,
  PURCHASE_RETURN_NO integer not null,
  RECEIVING_RECEIPT_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  PAID_IND char(1) default 'N' not null,
  PAID_DT date null,
  PAID_BY integer null,
  constraint PURCHASE_RETURN$PK primary key (ID),
  constraint PURCHASE_RETURN$UK unique (PURCHASE_RETURN_NO),
  constraint PURCHASE_RETURN$FK foreign key (RECEIVING_RECEIPT_ID) references RECEIVING_RECEIPT (ID),
  constraint PURCHASE_RETURN$FK2 foreign key (POST_BY) references USER (ID),
  constraint PURCHASE_RETURN$FK3 foreign key (PAID_BY) references USER (ID)
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

create table PURCHASE_RETURN_BAD_STOCK (
  ID integer auto_increment,
  PURCHASE_RETURN_BAD_STOCK_NO integer not null,
  SUPPLIER_ID integer not null,
  POST_IND char(1) default 'N' not null,
  POST_DT date null,
  POST_BY integer null,
  REMARKS varchar(100) null,
  PICKUP_DT date null,
  constraint PURCHASE_RETURN_BAD_STOCK$PK primary key (ID),
  constraint PURCHASE_RETURN_BAD_STOCK$UK unique (PURCHASE_RETURN_BAD_STOCK_NO),
  constraint PURCHASE_RETURN_BAD_STOCK$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID),
  constraint PURCHASE_RETURN_BAD_STOCK$FK2 foreign key (POST_BY) references USER (ID)
);

create table PURCHASE_RETURN_BAD_STOCK_ITEM (
  ID integer auto_increment,
  PURCHASE_RETURN_BAD_STOCK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer not null,
  UNIT_COST numeric(10, 2) not null,
  constraint PURCHASE_RETURN_BAD_STOCK_ITEM$PK primary key (ID),
  constraint PURCHASE_RETURN_BAD_STOCK_ITEM$FK foreign key (PURCHASE_RETURN_BAD_STOCK_ID) references PURCHASE_RETURN_BAD_STOCK (ID),
  constraint PURCHASE_RETURN_BAD_STOCK_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);

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

create table BIR_FORM_2307_REPORT (
  ID integer auto_increment,
  REPORT_NO integer not null,
  FROM_DT date not null,
  TO_DT date not null,
  SUPPLIER_ID integer not null,
  MONTH1_NET_AMT numeric(12, 2) null,
  MONTH2_NET_AMT numeric(12, 2) null,
  MONTH3_NET_AMT numeric(12, 2) null,
  CREATE_DT datetime not null,
  CREATED_BY integer null,
  primary key (ID),
  constraint BIR_FORM_2307_REPORT$FK foreign key (SUPPLIER_ID) references SUPPLIER (ID)
);

create table INVENTORY_CHECK (
  ID integer auto_increment,
  INVENTORY_DT date not null,
  POST_IND char(1) default 'N' not null,
  POST_DT datetime null,
  constraint INVENTORY_CHECK$PK primary key (ID),
  constraint INVENTORY_CHECK$UK unique (INVENTORY_DT)
);

create table AREA (
  ID integer auto_increment,
  NAME varchar(50) not null,
  constraint AREA$PK primary key (ID),
  constraint AREA$UK unique (NAME)
);

create table AREA_INV_REPORT (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  REPORT_NO integer not null,
  AREA_ID integer null,
  CHECKER varchar(50) null,
  DOUBLE_CHECKER varchar(50) null,
  CREATE_BY integer null,
  REVIEW_IND char(1) default 'N' not null,
  REVIEWER varchar(50) null,
  constraint AREA_INV_REPORT$PK primary key (ID),
  constraint AREA_INV_REPORT$UK unique (INVENTORY_CHECK_ID, REPORT_NO),
  constraint AREA_INV_REPORT$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID),
  constraint AREA_INV_REPORT$FK2 foreign key (AREA_ID) references AREA (ID),
  constraint AREA_INV_REPORT$FK3 foreign key (CREATE_BY) references USER (ID)
);

create table AREA_INV_REPORT_ITEM (
  ID integer auto_increment,
  AREA_INV_REPORT_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  QUANTITY integer not null,
  constraint AREA_INV_REPORT_ITEM$PK primary key (ID),
  constraint AREA_INV_REPORT_ITEM$FK foreign key (AREA_INV_REPORT_ID) references AREA_INV_REPORT (ID),
  constraint AREA_INV_REPORT_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);

create table INVENTORY_CHECK_SUMMARY_ITEM (
  ID integer auto_increment,
  INVENTORY_CHECK_ID integer not null,
  PRODUCT_ID integer not null,
  UNIT char(4) not null,
  BEGINNING_INV integer(6) not null,
  ACTUAL_COUNT integer(6) not null,
  COST numeric(10, 2) not null,
  constraint INVENTORY_CHECK_SUMMARY_ITEM$PK primary key (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK foreign key (INVENTORY_CHECK_ID) references INVENTORY_CHECK (ID),
  constraint INVENTORY_CHECK_SUMMARY_ITEM$FK2 foreign key (PRODUCT_ID) references PRODUCT2 (ID)
);

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
