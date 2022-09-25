import {
    ADD_DELIVERY_TO_LIST,
    addDeliveryToList,
    SET_CURRENT_DELIVERY,
    SET_DELIVERIES,
    setCurrentDelivery,
    setDeliveries
} from "../actions/deliveries-actions";
import {deliveriesApi} from "../api/api";
import { push } from 'react-router-redux';

let initialOrders = {
        deliveriesPreview: [
            {
                "carLoads": [],
                "deliveryDate": null,
                "id": null,
                "orders": [],
                "routes": [],
                "status": null
            }
        ],
        currentDelivery: {
            "carLoads": [],
            "deliveryDate": null,
            "id": null,
            "orders": [],
            "routes": [],
            "status": null
        }
    }
;

const deliveriesReducer = (state = initialOrders, action) => {
    switch (action.type) {
        case SET_DELIVERIES: {
            return {...state, deliveriesPreview: action.payload}
        }
        case SET_CURRENT_DELIVERY: {
            return {...state, currentDelivery: action.payload}
        }
        case ADD_DELIVERY_TO_LIST: {
            return {...state, deliveriesPreview: [...state.deliveriesPreview, action.payload]}
        }
        default:
            return state;
    }
}

export const loadDeliveries = () => (dispatch) => {
    deliveriesApi.findAll().then(response => {
        if (response.status === 200) {
            dispatch(setDeliveries(response.data));
        }
    })
}

export const loadDelivery = (id) => (dispatch) => {
    deliveriesApi.findById(id).then(response => {
        if (response.status === 200) {
            dispatch(setCurrentDelivery(response.data));
        }
    })
}


export const createDelivery = (date) => (dispatch) => {
    // TODO: [UI] remove status creation
    const newDelivery = {deliveryDate: date, status: 'DRAFT'};

    deliveriesApi.create(newDelivery).then(response => {
        if (response.status === 200) {
            const createdDelivery = response.data;
            dispatch(setCurrentDelivery(createdDelivery));
            dispatch(addDeliveryToList(createdDelivery));
            // TODO [UI Max]: redirect not working
            dispatch(push(`/delivery/${createdDelivery.id}`));
        }
    })
}

export default deliveriesReducer;