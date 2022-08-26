import {GET_ORDERS_BY_DATE} from "../actions/orders-actions";

let initialOrders = {
        orders: {
            "validOrders": [
                {
                    "id": 1,
                    "orderNumber": "45678",
                    "products": [
                        {
                            "productName": "Наповнювач фруктово-ягідний (декоргель) (12 кг)",
                            "amount": 1,
                            "storeName": "Склад №1",
                            "unitWeight": 12.0,
                            "totalProductWeight": 12.0
                        },
                        {
                            "productName": "66784 Арахісова паста",
                            "amount": 1,
                            "storeName": "Склад №2",
                            "unitWeight": 20.0,
                            "totalProductWeight": 20.0
                        }
                    ],
                    "createdDate": "2022-02-17",
                    "clientName": "Домашня випічка",
                    "address": "Бровари, Марії Лагунової, 11",
                    "managerFullName": "Балашова Лариса",
                    "orderWeight": 32.0,
                    "addressValid": true
                },
                {
                    "id": 2,
                    "orderNumber": "75578",
                    "products": [
                        {
                            "productName": "66784 Арахісова паста",
                            "amount": 1,
                            "storeName": "Склад №2",
                            "unitWeight": 20.0,
                            "totalProductWeight": 20.0
                        }
                    ],
                    "createdDate": "2022-02-16",
                    "clientName": "ТОВ Пекарня",
                    "address": "м. Київ, вул. Металістів, 8, оф. 4-24",
                    "managerFullName": "Шульженко Олексій",
                    "orderWeight": 20.0,
                    "addressValid": true
                }
            ],
            "invalidOrders": [
                {
                    "id": 3,
                    "orderNumber": "45678",
                    "products": [
                        {
                            "productName": "Наповнювач фруктово-ягідний (декоргель) (12 кг)",
                            "amount": 1,
                            "storeName": "Склад №1",
                            "unitWeight": 12.0,
                            "totalProductWeight": 12.0
                        }
                    ],
                    "createdDate": "2022-02-17",
                    "clientName": "Домашня випічка",
                    "address": "Бровари, Марії Лагунової, 11",
                    "managerFullName": "Балашова Лариса",
                    "orderWeight": 12.0,
                    "addressValid": false
                },
                {
                    "id": 4,
                    "orderNumber": "75578",
                    "products": [
                        {
                            "productName": "66784 Арахісова паста",
                            "amount": 1,
                            "storeName": "Склад №2",
                            "unitWeight": 20.0,
                            "totalProductWeight": 20.0
                        }
                    ],
                    "createdDate": "2022-02-16",
                    "clientName": "ТОВ Пекарня",
                    "address": "м. Київ, вул. Металістів, 8, оф. 4-24",
                    "managerFullName": "Шульженко Олексій",
                    "orderWeight": 20.0,
                    "addressValid": false
                },
                {
                    "id": 4,
                    "orderNumber": "75578",
                    "products": [
                        {
                            "productName": "66784 Арахісова паста",
                            "amount": 1,
                            "storeName": "Склад №2",
                            "unitWeight": 20.0,
                            "totalProductWeight": 20.0
                        }
                    ],
                    "createdDate": "2022-02-16",
                    "clientName": "ТОВ Пекарня",
                    "address": "м. Київ, вул. Металістів, 8, оф. 4-24",
                    "managerFullName": "Шульженко Олексій",
                    "orderWeight": 20.0,
                    "addressValid": false
                }
            ]
        },
        loaded: false,
        date: null
    }
;

const ordersReducer = (state = initialOrders, action) => {
    switch (action.type) {
        case GET_ORDERS_BY_DATE:
            return {...state, date: action.date, loaded: true};
        default:
            return state;
    }
}

export default ordersReducer;