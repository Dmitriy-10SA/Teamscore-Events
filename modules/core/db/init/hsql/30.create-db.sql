-- Первичная инициализация типов событий и дополнительных полей

-- Тип события: Мероприятие
insert into EVENTS_EVENT_TYPE (ID, VERSION, CREATE_TS, CREATED_BY, NAME, DESCRIPTION)
values ('11111111-1111-1111-1111-111111111111', 1, current_timestamp, 'admin', 'Мероприятие', 'Корпоративное мероприятие');

-- Тип события: Собрание
insert into EVENTS_EVENT_TYPE (ID, VERSION, CREATE_TS, CREATED_BY, NAME, DESCRIPTION)
values ('22222222-2222-2222-2222-222222222222', 1, current_timestamp, 'admin', 'Собрание', 'Рабочее собрание');

-- Тип события: Праздник
insert into EVENTS_EVENT_TYPE (ID, VERSION, CREATE_TS, CREATED_BY, NAME, DESCRIPTION)
values ('33333333-3333-3333-3333-333333333333', 1, current_timestamp, 'admin', 'Праздник', 'Праздничное мероприятие');

-- Дополнительные поля для типа "Мероприятие"

-- Место проведения (текстовое поле)
insert into EVENTS_EVENT_FIELD (ID, VERSION, CREATE_TS, CREATED_BY, ID_, NAME, TYPE_, EVENT_TYPE_ID)
values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 1, current_timestamp, 'admin', 'location', 'Место проведения', 'Строка', '11111111-1111-1111-1111-111111111111');

-- Программа (файл)
insert into EVENTS_EVENT_FIELD (ID, VERSION, CREATE_TS, CREATED_BY, ID_, NAME, TYPE_, EVENT_TYPE_ID)
values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 1, current_timestamp, 'admin', 'program', 'Программа', 'Бинарный', '11111111-1111-1111-1111-111111111111');

-- Организатор (текстовое поле)
insert into EVENTS_EVENT_FIELD (ID, VERSION, CREATE_TS, CREATED_BY, ID_, NAME, TYPE_, EVENT_TYPE_ID)
values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 1, current_timestamp, 'admin', 'organizer', 'Организатор', 'Строка', '11111111-1111-1111-1111-111111111111');

-- Контакты организатора (текстовое поле)
insert into EVENTS_EVENT_FIELD (ID, VERSION, CREATE_TS, CREATED_BY, ID_, NAME, TYPE_, EVENT_TYPE_ID)
values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 1, current_timestamp, 'admin', 'organizer-contacts', 'Контакты организатора', 'Строка', '11111111-1111-1111-1111-111111111111');

-- Дополнительные поля для типа "Собрание"

-- Повестка (многострочное поле)
insert into EVENTS_EVENT_FIELD (ID, VERSION, CREATE_TS, CREATED_BY, ID_, NAME, TYPE_, EVENT_TYPE_ID)
values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 1, current_timestamp, 'admin', 'agenda', 'Повестка', 'Многострочный', '22222222-2222-2222-2222-222222222222');

-- Место проведения (текстовое поле)
insert into EVENTS_EVENT_FIELD (ID, VERSION, CREATE_TS, CREATED_BY, ID_, NAME, TYPE_, EVENT_TYPE_ID)
values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 1, current_timestamp, 'admin', 'meeting-location', 'Место проведения', 'Строка', '22222222-2222-2222-2222-222222222222');

-- Тип "Праздник" не имеет дополнительных полей
