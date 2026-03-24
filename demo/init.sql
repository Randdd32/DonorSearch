CREATE DATABASE InfraManager COLLATE Cyrillic_General_CI_AS;
GO
USE InfraManager;
GO

CREATE TABLE dbo.[LifeCycleState] ([ID] INT PRIMARY KEY, [Name] NVARCHAR(100));
CREATE TABLE dbo.[Подразделение] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100));
CREATE TABLE dbo.[Пользователи] ([Идентификатор] INT PRIMARY KEY, [IMObjID] INT, [Фамилия] NVARCHAR(50), [Имя] NVARCHAR(50), [Отчество] NVARCHAR(50), [ИД подразделения] INT);
CREATE TABLE dbo.[Здание] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100));
CREATE TABLE dbo.[Этаж] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100), [ИД здания] INT);
CREATE TABLE dbo.[Комната] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100), [ИД этажа] INT);
CREATE TABLE dbo.[Рабочее место] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100), [ИД комнаты] INT);
CREATE TABLE dbo.[Производители] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100));
CREATE TABLE dbo.[ProductCatalogType] ([ID] INT PRIMARY KEY, [Name] NVARCHAR(100));
CREATE TABLE dbo.[Типы оконечного оборудования] ([Идентификатор] INT PRIMARY KEY, [Название] NVARCHAR(100), [ИД производителя] INT, [ProductCatalogTypeID] INT);
CREATE TABLE dbo.[Оконечное оборудование] ([Идентификатор] INT PRIMARY KEY,[Название] NVARCHAR(100), [Инвентарный номер] NVARCHAR(50), [SerialNumber] NVARCHAR(50), [ИД типа ОО] INT, [ИД рабочего места] INT,[ИД комнаты] INT);
CREATE TABLE dbo.[Asset] ([DeviceID] INT PRIMARY KEY, [LifeCycleStateID] INT, [DateReceived] DATETIME, [IsWorking] BIT, [UtilizerID] INT,[UtilizerClassID] INT);
CREATE TABLE dbo.[AdapterType] ([AdapterTypeID] INT PRIMARY KEY,[ProductCatalogTypeID] INT, [VendorID] INT);
CREATE TABLE dbo.[Adapter] ([AdapterID] INT PRIMARY KEY, [TerminalDeviceID] INT, [Name] NVARCHAR(200), [SerialNo] NVARCHAR(50), [AdapterTypeID] INT);
GO

INSERT INTO dbo.[LifeCycleState] VALUES (1, N'Использование'), (2, N'Хранение'), (3, N'Списано'), (4, N'Ремонт'), (5, N'Неучтенное');
INSERT INTO dbo.[Подразделение] VALUES (1, N'Отдел разработки'), (2, N'Бухгалтерия'), (3, N'Отдел кадров'), (4, N'Руководство');
INSERT INTO dbo.[Пользователи] VALUES 
(1, 101, N'Иванов', N'Иван', N'Иванович', 1), (2, 102, N'Петрова', N'Анна', N'Сергеевна', 2),
(3, 103, N'Смирнов', N'Алексей', N'Ильич', 1), (4, 104, N'Сидорова', N'Елена', N'Петровна', 3),
(5, 105, N'Волков', N'Дмитрий', NULL, 4);

INSERT INTO dbo.[Здание] VALUES (1, N'Главный офис'), (2, N'Складской комплекс');
INSERT INTO dbo.[Этаж] VALUES (1, N'Этаж 1', 1), (2, N'Этаж 2', 1), (3, N'Этаж 1', 2);
INSERT INTO dbo.[Комната] VALUES 
(1, N'Кабинет 101', 1), (2, N'Кабинет 205', 2), (3, N'Кабинет 206', 2), 
(4, N'Кабинет 210 (Дирекция)', 2), (5, N'Склад ИТ', 3);

INSERT INTO dbo.[Производители] VALUES 
(1, N'HP'), (2, N'Dell'), (3, N'Lenovo'), (4, N'Gigabyte'), (5, N'ASUS'), 
(6, N'MSI'), (7, N'Intel'), (8, N'AMD'), (9, N'Corsair'), (10, N'Kingston');

INSERT INTO dbo.[ProductCatalogType] VALUES 
(1, N'Системный блок'), (2, N'Видеоадаптер'), (3, N'Память'), (4, N'Блок питания'), (5, N'Процессор'), (6, N'Материнская плата');

INSERT INTO dbo.[Типы оконечного оборудования] VALUES 
(1, N'HP ProDesk 400 G7', 1, 1), (2, N'Dell OptiPlex 7090', 2, 1), (3, N'Lenovo ThinkCentre', 3, 1), (4, N'Сборка Custom PC', NULL, 1);

INSERT INTO dbo.[AdapterType] VALUES 
(1, 2, 4), (2, 2, 5), (3, 2, 6), (4, 3, 9), (5, 3, 10), 
(6, 4, 9), (7, 5, 7), (8, 5, 8), (9, 6, 5), (10, 6, 6);
GO

-- ==========================================
-- КОМПЬЮТЕРЫ (30 штук) И ИХ СТАТУСЫ
-- ==========================================
-- ПК 1-10: В эксплуатации (Штраф 50)
INSERT INTO dbo.[Оконечное оборудование] VALUES 
(1, N'PC-DEV-01', N'INV-001', N'SN-A01', 4, NULL, 2), (2, N'PC-DEV-02', N'INV-002', N'SN-A02', 4, NULL, 2),
(3, N'PC-DEV-03', N'INV-003', N'SN-A03', 4, NULL, 3), (4, N'PC-ACC-01', N'INV-004', N'SN-A04', 1, NULL, 1),
(5, N'PC-ACC-02', N'INV-005', N'SN-A05', 1, NULL, 1), (6, N'PC-HR-01', N'INV-006', N'SN-A06', 2, NULL, 1),
(7, N'PC-DIR-01', N'INV-007', N'SN-A07', 3, NULL, 4), (8, N'PC-DEV-04', N'INV-008', N'SN-A08', 4, NULL, 2),
(9, N'PC-DEV-05', N'INV-009', N'SN-A09', 4, NULL, 3), (10, N'PC-ACC-03', N'INV-010', N'SN-A10', 1, NULL, 1);

INSERT INTO dbo.[Asset] VALUES 
(1, 1, '2023-01-10', 1, 101, 9), (2, 1, '2023-01-10', 1, 103, 9), (3, 1, '2023-05-15', 1, 103, 9), 
(4, 1, '2022-11-20', 1, 102, 9), (5, 1, '2022-11-20', 1, 2, 102), (6, 1, '2021-08-05', 1, 104, 9),
(7, 1, '2024-01-10', 1, 105, 9), (8, 1, '2023-06-01', 1, 1, 102), (9, 1, '2023-06-01', 1, 1, 102), 
(10, 1, '2022-12-01', 1, 2, 102);

-- ПК 11-13: На складе (Идеальные доноры, Штраф 1), ПК 14-15: Неучтенные (штраф 20)
INSERT INTO dbo.[Оконечное оборудование] VALUES 
(11, N'PC-STORE-01', N'INV-011', N'SN-B01', 4, NULL, 5), (12, N'PC-STORE-02', N'INV-012', N'SN-B02', 1, NULL, 5),
(13, N'PC-STORE-03', N'INV-013', N'SN-B03', 2, NULL, 5), (14, N'PC-UNACCOUNTED-01', N'INV-014', N'SN-B04', 3, NULL, 5),
(15, N'PC-UNACCOUNTED-02', N'INV-015', N'SN-B05', 4, NULL, 5);

INSERT INTO dbo.[Asset] VALUES 
(11, 2, '2020-02-15', 1, 1, 102), (12, 2, '2021-03-10', 1, 1, 102), (13, 2, '2021-03-10', 1, 1, 102),
(14, 5, '2022-07-20', 1, 1, 102), (15, 5, '2023-10-05', 1, 1, 102);

-- ПК 16-25: Списаны (Лучшие доноры, Штраф 0)
INSERT INTO dbo.[Оконечное оборудование] VALUES 
(16, N'PC-OLD-01', N'INV-016', N'SN-C01', 1, NULL, 5), (17, N'PC-OLD-02', N'INV-017', N'SN-C02', 1, NULL, 5),
(18, N'PC-OLD-03', N'INV-018', N'SN-C03', 2, NULL, 5), (19, N'PC-OLD-04', N'INV-019', N'SN-C04', 2, NULL, 5),
(20, N'PC-OLD-05', N'INV-020', N'SN-C05', 3, NULL, 5), (21, N'PC-OLD-06', N'INV-021', N'SN-C06', 4, NULL, 5),
(22, N'PC-OLD-07', N'INV-022', N'SN-C07', 4, NULL, 5), (23, N'PC-OLD-08', N'INV-023', N'SN-C08', 4, NULL, 5),
(24, N'PC-OLD-09', N'INV-024', N'SN-C09', 1, NULL, 5), (25, N'PC-OLD-10', N'INV-025', N'SN-C10', 1, NULL, 5);

INSERT INTO dbo.[Asset] VALUES 
(16, 3, '2015-01-01', 0, 1, 102), (17, 3, '2015-02-01', 0, 1, 102), (18, 3, '2016-05-12', 0, 1, 102),
(19, 3, '2016-06-15', 0, 1, 102), (20, 3, '2017-08-20', 0, 1, 102), (21, 3, '2018-09-10', 0, 1, 102),
(22, 3, '2018-10-11', 0, 1, 102), (23, 3, '2019-11-25', 0, 1, 102), (24, 3, '2015-12-05', 0, 1, 102),
(25, 3, '2016-01-20', 0, 1, 102);

-- ПК 26-30: В ремонте (Штраф 20)
INSERT INTO dbo.[Оконечное оборудование] VALUES 
(26, N'PC-REP-01', N'INV-026', N'SN-D01', 4, NULL, 5), (27, N'PC-REP-02', N'INV-027', N'SN-D02', 4, NULL, 5),
(28, N'PC-REP-03', N'INV-028', N'SN-D03', 1, NULL, 5), (29, N'PC-REP-04', N'INV-029', N'SN-D04', 2, NULL, 5),
(30, N'PC-REP-05', N'INV-030', N'SN-D05', 3, NULL, 5);

INSERT INTO dbo.[Asset] VALUES 
(26, 4, '2023-05-10', 0, 1, 102), (27, 4, '2023-06-15', 0, 1, 102), (28, 4, '2022-04-20', 0, 1, 102),
(29, 4, '2021-08-30', 0, 1, 102), (30, 4, '2020-09-15', 0, 1, 102);

-- ПК 31 (но с 2 видеокартами для проверки списков и одна не должна пройти проверку по совместимости)
INSERT INTO dbo.[Оконечное оборудование] VALUES 
(31, N'PC-RENDER-RIG-01', N'INV-031', N'SN-RIG-01', 1, NULL, 1);

INSERT INTO dbo.[Asset] VALUES 
(31, 1, '2024-01-01', 1, 101, 9);

GO

-- ==========================================
-- 4. АДАПТЕРЫ (ДЕТАЛИ) ДЛЯ ПК
-- ==========================================
-- ПК 1 (Целевой ПК с "проблемой")
INSERT INTO dbo.[Adapter] VALUES 
(101, 1, N'ASUS ROG STRIX B550-F GAMING', N'MB-001', 9),
(102, 1, N'AMD Ryzen 5 5600X', N'CPU-001', 8),
(103, 1, N'Corsair Vengeance LPX 32GB (2x16GB) DDR4 3200MHz', N'RAM-001', 4),
(104, 1, N'Gigabyte GeForce RTX 3060 12GB', N'GPU-BROKEN', 1), -- Эта деталь "сгорела", для нее ищем донора
(105, 1, N'Corsair RM750x 750W CP-9020092-UK', N'PSU-001', 6);

-- ПК 11 (На складе, Идеальный донор RTX 3060, но опечатка в имени для проверки NEEDS_REVIEW)
INSERT INTO dbo.[Adapter] VALUES 
(1101, 11, N'ASUS TUF GAMING B550-PLUS', N'MB-011', 9),
(1102, 11, N'AMD Rizen 5 5600', N'CPU-011', 8),
(1103, 11, N'Kingston FURY Beast 16GB DDR4', N'RAM-011', 5),
(1104, 11, N'Gigabite RTX 3060 12G Windforce', N'GPU-011', 1), -- Идеальный донор с опечаткой!
(1105, 11, N'Corsair CX650M', N'PSU-011', 6);

-- ПК 16 (Списан, Донор RTX 2060 - проверим, пропустит ли его правило по мощности БП и длине)
INSERT INTO dbo.[Adapter] VALUES 
(1601, 16, N'MSI B450 TOMAHAWK MAX', N'MB-016', 10),
(1602, 16, N'AMD Ryzen 5 3600', N'CPU-016', 8),
(1603, 16, N'Corsair Vengeance LPX 16GB DDR4', N'RAM-016', 4),
(1604, 16, N'ASUS Dual GeForce RTX 2060', N'GPU-016', 2), -- Хороший донор, статус Списан
(1605, 16, N'Corsair VS550', N'PSU-016', 6);

-- ПК 2 (В эксплуатации - Донор RTX 4060, должен получить штраф CRITICAL 50 баллов)
INSERT INTO dbo.[Adapter] VALUES 
(201, 2, N'ASUS PRIME B550M-A', N'MB-002', 9),
(202, 2, N'AMD Ryzen 7 5800X', N'CPU-002', 8),
(203, 2, N'Kingston FURY Beast 32GB DDR4 3600MHz', N'RAM-002', 5),
(204, 2, N'MSI GeForce RTX 4060 VENTUS 2X', N'GPU-002', 3), -- Мощный донор, но нельзя трогать!
(205, 2, N'Corsair RM850e', N'PSU-002', 6);

INSERT INTO dbo.[Adapter] VALUES 
(3103, 31, N'ROG Astral OC X3 OC Asus', N'GPU-RIG-1', 2),
(3103, 31, N'Asus Dual GeForce RTX 3060 V2 OC Edition', N'GPU-RIG-1', 2);

-- Заполним еще пару ПК случайными деталями для массовки
INSERT INTO dbo.[Adapter] VALUES 
(301, 3, N'Intel Core i5-12400F', N'CPU-003', 7),
(302, 3, N'MSI PRO B660M-A DDR4', N'MB-003', 10),
(303, 3, N'Kingston 16GB DDR4', N'RAM-003', 5),
(1201, 12, N'Intel Core i7-10700K', N'CPU-012', 7),
(1202, 12, N'ASUS ROG STRIX Z490-E', N'MB-012', 9),
(1203, 12, N'Corsair Vengeance 32GB DDR4', N'RAM-012', 4),
(1204, 12, N'ASUS GeForce GTX 1660 SUPER', N'GPU-012', 2),
(2601, 26, N'AMD Ryzen 3 3100', N'CPU-026', 8),
(2602, 26, N'Gigabyte A320M-S2H', N'MB-026', 9),
(2603, 26, N'MSI GeForce GTX 1050 Ti', N'GPU-026', 3);
GO