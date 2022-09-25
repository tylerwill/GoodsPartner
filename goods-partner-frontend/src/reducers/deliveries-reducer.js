import {
    ADD_DELIVERY_TO_LIST,
    addDeliveryToList,
    SET_CURRENT_DELIVERY,
    SET_DELIVERIES,
    SET_ORDERS_PREVIEW,
    SET_ORDERS_PREVIEW_LOADING,
    setCurrentDelivery,
    setDeliveries,
    setOrdersPreview,
    setOrdersPreviewLoading
} from "../actions/deliveries-actions";
import {deliveriesApi, ordersApi} from "../api/api";
import {push} from 'react-router-redux';

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
        },
        ordersPreview: null,
        ordersPreviewLoading: false,
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
        case SET_ORDERS_PREVIEW_LOADING:
            return {...state, ordersPreviewLoading: action.payload};
        case SET_ORDERS_PREVIEW:
            return {...state, ordersPreview: action.payload};
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
            const delivery = response.data;

            // FIXME [UI]: Reload orders if status -> Draft
            if (delivery.status === 'DRAFT') {
                findPreviewOrdersForDelivery(dispatch, delivery.deliveryDate);
            }
            dispatch(setCurrentDelivery(delivery));
        }
    })
}


// TODO [UI]: Do we need async here? https://redux.js.org/tutorials/fundamentals/part-6-async-logic
export const createDelivery = (date) => (dispatch) => {
    // TODO: [UI] remove status creation
    const newDelivery = {deliveryDate: date, status: 'DRAFT'};

    deliveriesApi.create(newDelivery).then(response => {
        if (response.status === 200) {
            const createdDelivery = response.data;
            dispatch(setCurrentDelivery(createdDelivery));
            findPreviewOrdersForDelivery(dispatch, createdDelivery.deliveryDate);
            dispatch(addDeliveryToList(createdDelivery));
            // TODO [UI Max]: redirect not working
            dispatch(push(`/delivery/${createdDelivery.id}`));
        }
    })
}

const findPreviewOrdersForDelivery = (dispatch, date) => {
    dispatch(setOrdersPreviewLoading(true));
    ordersApi.getOrdersByDate(date)
        .then(response => {
            if (response.status === 200) {
                dispatch(setOrdersPreview(response.data));
                dispatch(setOrdersPreviewLoading(false));
            }
        })
}

export default deliveriesReducer;