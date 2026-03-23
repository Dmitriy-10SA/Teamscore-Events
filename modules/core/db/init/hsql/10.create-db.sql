-- begin EVENTS_EVENT
create table EVENTS_EVENT
(
    ID                  varchar(36)  not null,
    VERSION             integer      not null,
    CREATE_TS           timestamp,
    CREATED_BY          varchar(50),
    UPDATE_TS           timestamp,
    UPDATED_BY          varchar(50),
    DELETE_TS           timestamp,
    DELETED_BY          varchar(50),
    --
    NAME                varchar(255) not null,
    DESCRIPTION         longvarchar,
    START_DATE_TIME     timestamp    not null,
    END_DATE_TIME       timestamp    not null,
    RESPONSIBLE_USER_ID varchar(36)  not null,
    STATUS              varchar(50)  not null,
    TYPE_ID             varchar(36)  not null,
    --
    primary key (ID)
);
-- end EVENTS_EVENT

-- begin EVENTS_EVENT_TYPE
create table EVENTS_EVENT_TYPE
(
    ID          varchar(36)  not null,
    VERSION     integer      not null,
    CREATE_TS   timestamp,
    CREATED_BY  varchar(50),
    UPDATE_TS   timestamp,
    UPDATED_BY  varchar(50),
    DELETE_TS   timestamp,
    DELETED_BY  varchar(50),
    --
    NAME        varchar(255) not null,
    DESCRIPTION longvarchar,
    --
    primary key (ID)
);
-- end EVENTS_EVENT_TYPE

-- begin EVENTS_EVENT_FIELD_VALUE
create table EVENTS_EVENT_FIELD_VALUE
(
    ID              varchar(36) not null,
    VERSION         integer     not null,
    CREATE_TS       timestamp,
    CREATED_BY      varchar(50),
    UPDATE_TS       timestamp,
    UPDATED_BY      varchar(50),
    DELETE_TS       timestamp,
    DELETED_BY      varchar(50),
    --
    EVENT_ID        varchar(36) not null,
    EVENT_FIELD_ID  varchar(36) not null,
    STRING_VALUE    longvarchar,
    TEXT_VALUE      longvarchar,
    FILE_ID         varchar(36),
    DATE_VALUE      date,
    DATE_TIME_VALUE timestamp,
    --
    primary key (ID)
);
-- end EVENTS_EVENT_FIELD_VALUE

-- begin EVENTS_EVENT_FIELD
create table EVENTS_EVENT_FIELD
(
    ID            varchar(36)  not null,
    VERSION       integer      not null,
    CREATE_TS     timestamp,
    CREATED_BY    varchar(50),
    UPDATE_TS     timestamp,
    UPDATED_BY    varchar(50),
    DELETE_TS     timestamp,
    DELETED_BY    varchar(50),
    --
    ID_           varchar(40)  not null,
    NAME          varchar(255) not null,
    TYPE_         varchar(50)  not null,
    EVENT_TYPE_ID varchar(36)  not null,
    --
    primary key (ID)
);
-- end EVENTS_EVENT_FIELD