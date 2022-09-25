import {SET_DELIVERIES, setDeliveries} from "../actions/deliveries-actions";
import {deliveriesApi} from "../api/api";

let initialOrders = {
        deliveries: [
            {
                "carLoads": [
                    {
                        "car": {
                            "available": true,
                            "cooler": true,
                            "driver": "string",
                            "id": 0,
                            "licencePlate": "string",
                            "loadSize": 0,
                            "name": "string",
                            "travelCost": 0,
                            "weightCapacity": 0
                        },
                        "orders": [
                            {
                                "address": "string",
                                "clientName": "string",
                                "comment": "string",
                                "createdDate": "2022-09-24",
                                "deliveryFinish": {
                                    "hour": 0,
                                    "minute": 0,
                                    "nano": 0,
                                    "second": 0
                                },
                                "deliveryStart": {
                                    "hour": 0,
                                    "minute": 0,
                                    "nano": 0,
                                    "second": 0
                                },
                                "frozen": true,
                                "id": 0,
                                "managerFullName": "string",
                                "mapPoint": {
                                    "address": "string",
                                    "latitude": 0,
                                    "longitude": 0,
                                    "status": "AUTOVALIDATED"
                                },
                                "orderNumber": "string",
                                "orderWeight": 0,
                                "products": [
                                    {
                                        "amount": 0,
                                        "productName": "string",
                                        "storeName": "string",
                                        "totalProductWeight": 0,
                                        "unitWeight": 0
                                    }
                                ],
                                "refKey": "string"
                            }
                        ]
                    }
                ],
                "deliveryDate": "2022-09-20",
                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "orders": [
                    {
                        "address": "string",
                        "clientName": "string",
                        "comment": "string",
                        "createdDate": "2022-09-24",
                        "deliveryFinish": {
                            "hour": 0,
                            "minute": 0,
                            "nano": 0,
                            "second": 0
                        },
                        "deliveryStart": {
                            "hour": 0,
                            "minute": 0,
                            "nano": 0,
                            "second": 0
                        },
                        "frozen": true,
                        "id": 0,
                        "managerFullName": "string",
                        "mapPoint": {
                            "address": "string",
                            "latitude": 0,
                            "longitude": 0,
                            "status": "AUTOVALIDATED"
                        },
                        "orderNumber": "string",
                        "orderWeight": 0,
                        "products": [
                            {
                                "amount": 0,
                                "productName": "string",
                                "storeName": "string",
                                "totalProductWeight": 0,
                                "unitWeight": 0
                            }
                        ],
                        "refKey": "string"
                    }
                ],
                "routes": [
                    {
                        "car": {
                            "available": true,
                            "cooler": true,
                            "driver": "string",
                            "id": 0,
                            "licencePlate": "string",
                            "loadSize": 0,
                            "name": "string",
                            "travelCost": 0,
                            "weightCapacity": 0
                        },
                        "distance": 0,
                        "estimatedTime": 0,
                        "finishTime": "2022-09-24T22:48:28.074Z",
                        "id": 0,
                        "optimization": true,
                        "routePoints": [
                            {
                                "address": "string",
                                "addressTotalWeight": 0,
                                "clientId": 0,
                                "clientName": "string",
                                "completedAt": "2022-09-24T22:48:28.074Z",
                                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                "mapPoint": {
                                    "address": "string",
                                    "latitude": 0,
                                    "longitude": 0,
                                    "status": "AUTOVALIDATED"
                                },
                                "orders": [
                                    {
                                        "comment": "string",
                                        "id": 0,
                                        "orderNumber": "string",
                                        "orderTotalWeight": 0
                                    }
                                ],
                                "routePointDistantTime": 0,
                                "status": "DONE"
                            }
                        ],
                        "spentTime": 0,
                        "startTime": "2022-09-24T22:48:28.074Z",
                        "status": "APPROVED",
                        "storeAddress": "string",
                        "storeName": "string",
                        "totalOrders": 0,
                        "totalPoints": 0,
                        "totalWeight": 0
                    }
                ],
                "status": "APPROVED"
            }
        ]
    }
;

const deliveriesReducer = (state = initialOrders, action) => {
    switch (action.type) {
        case SET_DELIVERIES: {
            return {...state, deliveries: action.payload}
        }
        default:
            return state;
    }
}

export const getDeliveries = () => (dispatch) => {
    deliveriesApi.findAll().then(response => {
        if (response.status === 200) {
            dispatch(setDeliveries(response.data));
        }
    })
}


export default deliveriesReducer;