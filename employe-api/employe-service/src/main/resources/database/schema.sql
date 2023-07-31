drop table if exists EMPLOYEE;
drop table if exists EMPLOYEE_HOBBY;
drop table if exists HOBBY;

create table EMPLOYEE
(
    UUID     varchar(120) not null,
    FULLNAME varchar(120) not null,
    EMAIL    varchar(100) not null,
    BIRTHDAY date         not null,
    primary key (UUID)
);

create table EMPLOYEE_HOBBY
(
    ID          varchar(120) not null,
    EMPLOYEE_ID varchar(120) not null,
    HOBBY_ID    varchar(120) not null,
    primary key (ID)
);
create table HOBBY
(
    ID         varchar(120) not null,
    HOBBY_NAME varchar(120) not null,
    primary key ( ID)
);

ALTER TABLE EMPLOYEE_HOBBY
    ADD CONSTRAINT FK_EMPLOYEE_HOBBY_PIPE
        FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (UUID);

ALTER TABLE EMPLOYEE_HOBBY
    ADD CONSTRAINT FK_HOBBY_EMPLOYEE_PIPE
        FOREIGN KEY (HOBBY_ID) REFERENCES HOBBY (ID);