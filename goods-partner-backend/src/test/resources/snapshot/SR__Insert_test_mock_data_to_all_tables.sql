insert into clients (id, name)
values (1, 'ТОВ "Пекарня"');
insert into clients (id, name)
values (2, 'ТОВ "Кондитерська"');
insert into clients (id, name)
values (3, 'ТОВ "Хлібзавод"');

insert into addresses (id, address, client_id)
values (1, 'м. Київ, вул. Металістів, 8, оф. 4-24', 1);
insert into addresses (id, address, client_id)
values (2, 'м. Київ, вул. Хрещатик, 1', 1);
insert into addresses (id, address, client_id)
values (3, 'м. Київ, вул. Молодогвардійська, 22, оф. 35', 1);
insert into addresses (id, address, client_id)
values (4, 'м. Київ, вул. Глибочицька, 12Б, оф. 3', 2);
insert into addresses (id, address, client_id)
values (5, 'м. Київ, вул. Межигірська, 22, оф. 3', 2);
insert into addresses (id, address, client_id)
values (6, 'м. Київ, вул. Політехнічна, 1, оф. 65', 3);

insert into managers (id, first_name, last_name, phone_number, email)
values (1, 'Петро', 'Коваленко', '+380671111111', 'p.kovalenko@goodspartner.com');
insert into managers (id, first_name, last_name, phone_number, email)
values (2, 'Іван', 'Шугай', '+380672222222', 'i.shuhai@goodspartner.com');
insert into managers (id, first_name, last_name, phone_number, email)
values (3, 'Андрій', 'Бублик', '+380673333333', 'a.bublyk@goodspartner.com');

insert into routes (status, total_weight, total_points, total_orders, distance, estimated_time, store_name,
                    store_address, route_points)
values ('DRAFT', 1500.35, 3, 5, 100.45, 360, 'Склад №1', 'Фастів, вул. Широка, 15', '[
  {
    "id": "741a37c1-8f13-4335-95f6-a5b14aaf7fbd",
    "status": "DONE",
    "completedAt": "2022-08-08T12:23:23",
    "clientId": 1,
    "clientName": "ТОВ Пекарня",
    "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
    "addressTotalWeight": 804.98,
    "routePointDistantTime": 60,
    "orders": [
      {
        "id": 9,
        "orderNumber": "986453"
      },
      {
        "id": 10,
        "orderNumber": "426457"
      },
      {
        "id": 12,
        "orderNumber": "586126"
      }
    ]
  },
  {
    "id": "93e632fa-18a6-11ed-861d-0242ac120002",
    "status": "DONE",
    "completedAt": "2022-08-08T15:45:12",
    "clientId": 2,
    "clientName": "ТОВ Кондитерська",
    "address": "м. Київ, вул. Глибочицька, 12Б, оф. 3",
    "addressTotalWeight": 5.5,
    "routePointDistantTime": 180,
    "orders": [
      {
        "id": 3,
        "orderNumber": "45463"
      }
    ]
  },
  {
    "id": "148b44a8-3fce-45fd-91f2-fccabbb52525",
    "status": "DONE",
    "completedAt": "2022-08-08T19:38:19",
    "clientId": 3,
    "clientName": "ТОВ Хлібзавод",
    "address": "м. Київ, вул. Хрещатик, 1",
    "addressTotalWeight": 338.28,
    "routePointDistantTime": 240,
    "orders": [
      {
        "id": 5,
        "orderNumber": "432565"
      }
    ]
  }
]');

insert into routes (status, total_weight, total_points, total_orders, distance, estimated_time, store_name,
                    store_address, route_points)
values ('APPROVED', 2000.20, 3, 5, 150.95, 480, 'Склад №1', 'Фастів, вул. Широка, 15', '[
  {
    "id": "741a37c1-8f13-4335-95f6-a5b14aaf7fbd",
    "status": "DONE",
    "completedAt": "2022-08-08T12:23:23",
    "clientId": 1,
    "clientName": "ТОВ Пекарня",
    "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
    "addressTotalWeight": 804.98,
    "routePointDistantTime": 60,
    "orders": [
      {
        "id": 9,
        "orderNumber": "986453"
      },
      {
        "id": 10,
        "orderNumber": "426457"
      },
      {
        "id": 12,
        "orderNumber": "586126"
      }
    ]
  },
  {
    "id": "93e632fa-18a6-11ed-861d-0242ac120002",
    "status": "DONE",
    "completedAt": "2022-08-08T15:45:12",
    "clientId": 2,
    "clientName": "ТОВ Кондитерська",
    "address": "м. Київ, вул. Глибочицька, 12Б, оф. 3",
    "addressTotalWeight": 5.5,
    "routePointDistantTime": 180,
    "orders": [
      {
        "id": 3,
        "orderNumber": "45463"
      }
    ]
  },
  {
    "id": "148b44a8-3fce-45fd-91f2-fccabbb52525",
    "status": "DONE",
    "completedAt": "2022-08-08T19:38:19",
    "clientId": 3,
    "clientName": "ТОВ Хлібзавод",
    "address": "м. Київ, вул. Хрещатик, 1",
    "addressTotalWeight": 338.28,
    "routePointDistantTime": 240,
    "orders": [
      {
        "id": 5,
        "orderNumber": "432565"
      }
    ]
  }
]');

insert into routes (status, total_weight, total_points, total_orders, distance, estimated_time, start_time, store_name,
                    store_address, route_points)
values ('INPROGRESS', 1200.40, 3, 5, 245.64, 420, now() - interval '1 hour', 'Склад №2', 'Фастів, вул. Зелена, 5', '[
  {
    "id": "741a37c1-8f13-4335-95f6-a5b14aaf7fbd",
    "status": "DONE",
    "completedAt": "2022-08-08T12:23:23",
    "clientId": 1,
    "clientName": "ТОВ Пекарня",
    "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
    "addressTotalWeight": 804.98,
    "routePointDistantTime": 60,
    "orders": [
      {
        "id": 9,
        "orderNumber": "986453"
      },
      {
        "id": 10,
        "orderNumber": "426457"
      },
      {
        "id": 12,
        "orderNumber": "586126"
      }
    ]
  },
  {
    "id": "93e632fa-18a6-11ed-861d-0242ac120002",
    "status": "DONE",
    "completedAt": "2022-08-08T15:45:12",
    "clientId": 2,
    "clientName": "ТОВ Кондитерська",
    "address": "м. Київ, вул. Глибочицька, 12Б, оф. 3",
    "addressTotalWeight": 5.5,
    "routePointDistantTime": 180,
    "orders": [
      {
        "id": 3,
        "orderNumber": "45463"
      }
    ]
  },
  {
    "id": "148b44a8-3fce-45fd-91f2-fccabbb52525",
    "status": "DONE",
    "completedAt": "2022-08-08T19:38:19",
    "clientId": 3,
    "clientName": "ТОВ Хлібзавод",
    "address": "м. Київ, вул. Хрещатик, 1",
    "addressTotalWeight": 338.28,
    "routePointDistantTime": 240,
    "orders": [
      {
        "id": 5,
        "orderNumber": "432565"
      }
    ]
  }
]');

insert into routes (status, total_weight, total_points, total_orders, distance, estimated_time, start_time, finish_time,
                    spent_time, store_name,
                    store_address, route_points)
values ('COMPLETED', 1870.40, 3, 5, 145.59, 480, now() - interval '10 hour', now() - interval '1 hour', 5400,
        'Склад №2', 'Фастів, вул. Зелена, 5', '[
    {
      "id": "741a37c1-8f13-4335-95f6-a5b14aaf7fbd",
      "status": "DONE",
      "completedAt": "2022-08-08T12:23:23",
      "clientId": 1,
      "clientName": "ТОВ Пекарня",
      "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
      "addressTotalWeight": 804.98,
      "routePointDistantTime": 60,
      "orders": [
        {
          "id": 9,
          "orderNumber": "986453"
        },
        {
          "id": 10,
          "orderNumber": "426457"
        },
        {
          "id": 12,
          "orderNumber": "586126"
        }
      ]
    },
    {
      "id": "93e632fa-18a6-11ed-861d-0242ac120002",
      "status": "DONE",
      "completedAt": "2022-08-08T15:45:12",
      "clientId": 2,
      "clientName": "ТОВ Кондитерська",
      "address": "м. Київ, вул. Глибочицька, 12Б, оф. 3",
      "addressTotalWeight": 5.5,
      "routePointDistantTime": 180,
      "orders": [
        {
          "id": 3,
          "orderNumber": "45463"
        }
      ]
    },
    {
      "id": "148b44a8-3fce-45fd-91f2-fccabbb52525",
      "status": "DONE",
      "completedAt": "2022-08-08T19:38:19",
      "clientId": 3,
      "clientName": "ТОВ Хлібзавод",
      "address": "м. Київ, вул. Хрещатик, 1",
      "addressTotalWeight": 338.28,
      "routePointDistantTime": 240,
      "orders": [
        {
          "id": 5,
          "orderNumber": "432565"
        }
      ]
    }
  ]');

insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (1, 12345, NOW() - interval '72 hour', NOW() + interval '720 hour', 1, 1, 1);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (2, 43532, NOW() - interval '72 hour', NOW() - interval '48 hour', 1, 2, 1);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (3, 45463, NOW() - interval '72 hour', NOW() - interval '24 hour', 1, 3, 1);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (4, 97342, NOW() - interval '48 hour', NOW() + interval '720 hour', 2, 1, 51);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (5, 432565, NOW() - interval '48 hour', NOW() - interval '24 hour', 2, 3, 51);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (6, 356325, NOW() - interval '48 hour', NOW(), 2, 2, 51);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (7, 479324, NOW() - interval '24 hour', NOW() + interval '720 hour', 3, 1, 101);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (8, 477255, NOW() - interval '24 hour', NOW() + interval '24 hour', 3, 3, 101);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (9, 986453, NOW() - interval '24 hour', NOW() - interval '24 hour', 3, 1, 101);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (10, 426457, NOW() - interval '24 hour', NOW() - interval '24 hour', 3, 2, 101);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (11, 426457, NOW() - interval '76 hour', NOW() - interval '48 hour', 3, 2, 151);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (12, 426457, NOW() - interval '76 hour', NOW() - interval '24 hour', 3, 2, 151);
insert into orders (id, number, created_date, shipping_date, address_id, manager_id, route_id)
values (13, 426457, NOW() - interval '76 hour', NOW() - interval '8 hour', 3, 2, 151);

insert into products (id, name, kg, store_id)
values (1, '3434 Паста шоколадна', 1.52, 1);
insert into products (id, name, kg, store_id)
values (2, '353 Стружка кокосова', 0.34, 1);
insert into products (id, name, kg, store_id)
values (3, '6798 Фарба харчова червона', 0.52, 1);
insert into products (id, name, kg, store_id)
values (4, '576853 Масло екстра', 20.56, 1);
insert into products (id, name, kg, store_id)
values (5, '66784 Арахісова паста', 0.55, 1);
insert into products (id, name, kg, store_id)
values (6, '8795 Мука екстра', 123.56, 2);
insert into products (id, name, kg, store_id)
values (7, '56743 Форми пасхальні', 4.65, 2);
insert into products (id, name, kg, store_id)
values (8, '4695 Фарба харчова зелена', 3.54, 2);
insert into products (id, name, kg, store_id)
values (9, '8452 Масло 1й гатунок', 34.44, 1);
insert into products (id, name, kg, store_id)
values (10, '46643 Фарба харчова синя', 5.78, 2);

insert into stores (id, name, address)
values (1, 'Склад №1', 'Фастів, вул. Широка, 15');
insert into stores (id, name, address)
values (2, 'Склад №2', 'Фастів, вул. Зелена, 5');

insert into orders_products (id, order_id, product_id, count)
values (1, 1, 1, 1);
insert into orders_products (id, order_id, product_id, count)
values (2, 1, 2, 2);

insert into orders_products (id, order_id, product_id, count)
values (3, 2, 3, 3);
insert into orders_products (id, order_id, product_id, count)
values (4, 2, 4, 4);

insert into orders_products (id, order_id, product_id, count)
values (5, 3, 5, 5);
insert into orders_products (id, order_id, product_id, count)
values (6, 3, 5, 5);

insert into orders_products (id, order_id, product_id, count)
values (7, 4, 5, 5);
insert into orders_products (id, order_id, product_id, count)
values (8, 4, 5, 5);

insert into orders_products (id, order_id, product_id, count)
values (9, 5, 8, 8);
insert into orders_products (id, order_id, product_id, count)
values (10, 5, 9, 9);

insert into orders_products (id, order_id, product_id, count)
values (11, 6, 10, 10);
insert into orders_products (id, order_id, product_id, count)
values (12, 6, 1, 1);

insert into orders_products (id, order_id, product_id, count)
values (13, 7, 10, 10);
insert into orders_products (id, order_id, product_id, count)
values (14, 7, 2, 2);

insert into orders_products (id, order_id, product_id, count)
values (15, 8, 3, 3);
insert into orders_products (id, order_id, product_id, count)
values (16, 8, 4, 4);

insert into orders_products (id, order_id, product_id, count)
values (17, 9, 5, 5);
insert into orders_products (id, order_id, product_id, count)
values (18, 9, 6, 6);

insert into orders_products (id, order_id, product_id, count)
values (19, 10, 7, 7);
insert into orders_products (id, order_id, product_id, count)
values (20, 10, 8, 8);

insert into cars (name, licence_plate, driver, weight_capacity, cooler, travel_cost, available)
values ('Mercedes Sprinter', 'AA 1111 CT', 'Oleg Dudka', 2000, FALSE, 12, TRUE);
insert into cars (name, licence_plate, driver, weight_capacity, cooler, travel_cost, available)
values ('Mercedes Vito', 'AA 2222 CT', 'Ivan Piddubny', 1000, FALSE, 10, TRUE);
insert into cars (name, licence_plate, driver, weight_capacity, cooler, travel_cost, available)
values ('Mercedes Sprinter', 'AA 3333 CT', 'Anton Geraschenko', 2500, TRUE, 15, TRUE);
insert into cars (name, licence_plate, driver, weight_capacity, cooler, travel_cost, available)
values ('Mercedes 818', 'AA 4444 CT', 'Serhiy Kotovich', 4000, FALSE, 20, TRUE);

insert into users (id, user_name, email, role, enabled)
values (1, 'User 1', 'e.podzirey@gmail.com', 'ADMIN', TRUE);
insert into users (id, user_name, email, role, enabled)
values (2, 'User 2', 'office.anestar@gmail.com', 'DRIVER', TRUE);