insert into public.users (id, user_name, email, role, enabled)
values  (1, 'Maxym', 'belosh59@gmail.com', 'ADMIN', true),
        (51, 'Bogdan', 'bogdan.parashchak@gmail.com', 'ADMIN', true),
        (101, 'Taras', 'taras.orlovskyi@gmail.com', 'ADMIN', true),
        (151, 'Tolik', 'trubintolik@gmail.com', 'ADMIN', true),
        (201, 'Kostya', 'kos.nikolaev@gmail.com', 'ADMIN', true),
        (251, 'Zhenya', 'e.podzirey@gmail.com', 'ADMIN', true),
        (301, 'Nastya', 'nastyabondarenko870@gmail.com', 'ADMIN', true),
        (351, 'Sasha', 'abhramatanga@gmail.com', 'ADMIN', true),
        (401, 'Natali', 'natali.poroshina@gmail.com', 'ADMIN', true),
        (451, 'Artem', 'artemzhivolup@gmail.com', 'ADMIN', true),
        (501, 'Гаєвський В.В.', 'gaevskyi@gmail.com', 'ADMIN', true);

INSERT INTO public.cars (name, licence_plate, user_id, weight_capacity, cooler, available, travel_cost, deleted)
VALUES  ('Мерседес Віто Mersedes Vito', 'АІ6399ЕР', 1, 1100, false, true, 9, false),
        ('Мерседес Віто Mersedes Vito', 'АІ1649ЕР', 51, 800, false, true, 9, false),
        ('Mersedes-Benz 316 СDІ', 'AI0992ЕР', 101, 1450, false, true, 11, false),
        ('Mersedes-Benz', 'АІ6349HA', 151, 800, true, true, 11, false),
        ('Mersedes Vito', 'АІ7860НА', 201, 800, false, true, 9, false),
        ('Мерседес 818', 'АІ4091СР', 251, 4200, false, true, 20, false),
        ('Mersedes-Benz Sprinter 314', 'AI3092ОА', 301, 1900, false, true, 11, false),
        ('Mersedes-Benz Sprinter 513CDI', 'AI3097OB', 351, 2000, true, true, 13, false),
        ('Mersedes-Benz Sprinter 314 CDI', 'AI1060ОА', 401, 2500, false, true, 11, false),
        ('Mersedes-Benz Sprinter', 'AI2940CM', 451, 2000, false, false, 10, false),
        ('Renault Kangoo', 'АІ3256КН', 501, 650, false, true, 6, false);

-- insert into public.cars (id, name, licence_plate, weight_capacity, cooler, available, travel_cost, user_id)
-- values  (1, 'Mercedes Sprinter', 'AA 3333 CT', 2500, true, true, 15, 1),
--         (51, 'Mercedes 818', 'AA 4444 CT', 4000, false, true, 20, 51),
--         (101, 'Mercedes Sprinter', 'AA 1111 CT', 2000, false, true, 12, 101),
--         (151, 'Mercedes Vito', 'AA 2222 CT', 1000, false, true, 10, 151);

insert into public.store(id, name, address, latitude, longitude)
values ('5688492e-ede4-45d3-923b-5f9773fd3d4b', 'Склад №1', '15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна', 50.08340335, 29.885050630832627);

insert into public.grandedolce_addresses (order_address, client_name, valid_address, latitude, longitude)
values  ('м. Київ, вул. Металістів, 8, оф. 4-24', 'ТОВ "Пекарня', 'м. Київ, вул. Металістів, 8, оф. 4-24', 50.4500010000000, 30.5233330000000),
        ('м. Київ, вул. Хрещатик, 1', 'ТОВ "Кондитерська', 'м. Київ, вул. Хрещатик, 1', 50.4500011000000, 30.5233334000000),
        ('м. Київ, вул. Глибочицька, 12Б, оф. 3', 'ТОВ "Хлібзавод', 'м. Київ, вул. Глибочицька, 12Б, оф. 3', 50.4500011000000, 30.5233334000000);

insert into public.deliveries (id, delivery_date, status, formation_status)
values  ('4f0a02c1-083c-4d62-b678-2c5eea17f6d1', '2022-09-15', 'DRAFT', 'CALCULATION_COMPLETED'),
        ('d667c8f0-b961-49c6-b6bc-3f9a98b6c5da', '2022-09-14', 'APPROVED', 'CALCULATION_COMPLETED');

insert into public.car_loads (id, delivery_id, car_id)
values  ('a7bdd2c5-1d65-4dad-8ae5-57213c96f7ba', '4f0a02c1-083c-4d62-b678-2c5eea17f6d1', 51),
        ('8587e3ce-e7ee-4ba3-92b1-8396c423e372', 'd667c8f0-b961-49c6-b6bc-3f9a98b6c5da', 101),
        ('8587e3ce-e7ee-4ba3-92b1-8396c423e100', 'd667c8f0-b961-49c6-b6bc-3f9a98b6c5da', 151);

insert into public.routes (id, status, distance, start_time, finish_time, total_weight, total_points, total_orders, estimated_time, spent_time, optimization, delivery_id, car_id, store_id)
values  (1, 'APPROVED', 250.95, null, null, 2000.20, 3, 5, 480, 0, true,'d667c8f0-b961-49c6-b6bc-3f9a98b6c5da', 101, '5688492e-ede4-45d3-923b-5f9773fd3d4b'),
        (51, 'COMPLETED', 310.50, null, null, 1970.40, 3, 4, 480, 5400, true,'4f0a02c1-083c-4d62-b678-2c5eea17f6d1', 51, '5688492e-ede4-45d3-923b-5f9773fd3d4b'),
        (101, 'COMPLETED', 152.00, null, null, 750.00, 2, 3, 340, 600, true,'4f0a02c1-083c-4d62-b678-2c5eea17f6d1', 151, '5688492e-ede4-45d3-923b-5f9773fd3d4b');

insert into public.route_points (id, status, client_name, address, address_total_weight, route_point_distant_time, completed_at, expected_arrival, expected_completion, delivery_start, delivery_end, route_id)
values
    (1,   'DONE', 'ТОВ Кондитерська', 'м. Київ, вул. Глибочицька, 12Б, оф. 3', 100, 60, '2022-08-08T9:38:19', '9:00', '9:15', '9:00', '18:00', 51),
    (51,  'SKIPPED', 'ТОВ Хлібзавод', 'м. Київ, вул. Хрещатик, 1', 100, 60, '2022-08-08T9:38:19', '9:30', '9:45', '9:00', '18:00', 51),
    (101, 'SKIPPED', 'ТОВ Хлібзавод', 'м. Київ, вул. Хрещатик, 1', 100, 60, '2022-08-08T9:38:19', '9:30', '9:45', '9:00', '18:00', 101),
    (151, 'PENDING', 'ТОВ Пекарня', 'м. Київ, вул. Молодогвардійська, 22, оф. 35', 100, 60, null, '9:00', '9:15', '9:00', '18:00', 1);

insert into public.grandedolce_orders (id, order_number, shipping_date, client_name, address, manager, excluded, dropped, delivery_type, order_weight, ref_key, comment, delivery_id, reschedule_date, car_load_id, route_point_id, delivery_start, delivery_finish, products)
values
    (1, '123', '2022-09-14', 'ТОВ "Кондитерська', 'м. Київ, вул. Хрещатик, 1', 'Ірина Шефір', false, false, 'REGULAR', 750.00, 'ref1', 'Терміново', 'd667c8f0-b961-49c6-b6bc-3f9a98b6c5da', null, '8587e3ce-e7ee-4ba3-92b1-8396c423e372', 1, '09:00:00', '17:00:00',
     '[{"amount": 10, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська біла (диски) (15 кг)", "totalProductWeight": 150.0}, {"amount": 10, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська чорна (диски) (15 кг)", "totalProductWeight": 150.0}, {"amount": 4, "storeName": "Склад №1", "unitWeight": 13.0, "productName": "Каравела горіхова паста (AX52CG) 13 кг", "totalProductWeight": 52.0}, {"amount": 3, "storeName": "Склад №1", "unitWeight": 10.0, "productName": "Наповнювач персик джем (г/я 10кг)", "totalProductWeight": 30.0}]'),
    (51, '254', '2022-09-15', 'ТОВ "Хлібзавод', 'м. Київ, вул. Глибочицька, 12Б, оф. 3', 'Антон Василенко', false, false, 'POSTAL', 532.00, 'ref2', '', '4f0a02c1-083c-4d62-b678-2c5eea17f6d1', null, 'a7bdd2c5-1d65-4dad-8ae5-57213c96f7ba', 51, '09:30:00', '16:30:00',
     '[{"amount": 30, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська чорна (диски) (15 кг)", "totalProductWeight": 450.0}, {"amount": 4, "storeName": "Склад №1", "unitWeight": 13.0, "productName": "Каравела горіхова паста (AX52CG) 13 кг", "totalProductWeight": 52.0}, {"amount": 3, "storeName": "Склад №1", "unitWeight": 10.0, "productName": "Наповнювач персик джем (г/я 10кг)", "totalProductWeight": 30.0}]'),
    (101, '432', '2022-09-14', 'ТОВ "Хлібзавод', 'м. Київ, вул. Глибочицька, 12Б, оф. 3', 'Ірина Шефір', false, true, 'REGULAR', 1420.00, 'ref3', '', 'd667c8f0-b961-49c6-b6bc-3f9a98b6c5da', null, '8587e3ce-e7ee-4ba3-92b1-8396c423e372', 101, '09:00:00', '14:00:00',
     '[{"amount": 20, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська біла (диски) (15 кг)", "totalProductWeight": 300.0}, {"amount": 20, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська чорна (диски) (15 кг)", "totalProductWeight": 300.0}, {"amount": 40, "storeName": "Склад №1", "unitWeight": 13.0, "productName": "Каравела горіхова паста (AX52CG) 13 кг", "totalProductWeight": 520.0}, {"amount": 30, "storeName": "Склад №1", "unitWeight": 10.0, "productName": "Наповнювач персик джем (г/я 10кг)", "totalProductWeight": 300.0}]'),
    (151, '254', '2022-09-15', 'ТОВ "Пекарня', 'м. Київ, вул. Металістів, 8, оф. 4-24', 'Андрій Бугаенєко', true, false, 'REGULAR', 382.00, 'ref4', 'Забрати документи', '4f0a02c1-083c-4d62-b678-2c5eea17f6d1', null, 'a7bdd2c5-1d65-4dad-8ae5-57213c96f7ba', 151, '10:00:00', '15:00:00',
     '[{"amount": 10, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська біла (диски) (15 кг)", "totalProductWeight": 150.0}, {"amount": 10, "storeName": "Склад №1", "unitWeight": 15.0, "productName": "Глазур кондитерська чорна (диски) (15 кг)", "totalProductWeight": 150.0}, {"amount": 4, "storeName": "Склад №1", "unitWeight": 13.0, "productName": "Каравела горіхова паста (AX52CG) 13 кг", "totalProductWeight": 52.0}, {"amount": 3, "storeName": "Склад №1", "unitWeight": 10.0, "productName": "Наповнювач персик джем (г/я 10кг)", "totalProductWeight": 30.0}]');

SELECT setval('route_points_sequence', COALESCE(max(id), 1)) FROM "route_points";
ALTER SEQUENCE route_points_sequence INCREMENT BY 50;

SELECT setval('cars_id_sequence', COALESCE(max(id), 1)) FROM "cars";
ALTER SEQUENCE cars_id_sequence INCREMENT BY 50;

SELECT setval('users_id_sequence', COALESCE(max(id), 1)) FROM "users";
ALTER SEQUENCE users_id_sequence INCREMENT BY 50;

commit;


SELECT setval('orders_external_id_sequence', COALESCE(max(id), 1)) FROM "route_points";
ALTER SEQUENCE orders_external_id_sequence INCREMENT BY 50;

SELECT setval('routes_sequence', COALESCE(max(id), 1)) FROM "route_points";
ALTER SEQUENCE routes_sequence INCREMENT BY 50;

