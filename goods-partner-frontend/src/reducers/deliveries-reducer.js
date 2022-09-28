import {
    ADD_DELIVERY_TO_LIST,
    addDeliveryToList,
    SET_CURRENT_DELIVERY,
    SET_DELIVERIES,
    SET_DELIVERY_LOADING,
    SET_ORDERS_PREVIEW,
    SET_ORDERS_PREVIEW_LOADING,
    setCurrentDelivery,
    setDeliveries,
    setDeliveryLoading,
    setOrdersPreview,
    setOrdersPreviewLoading,
    UPDATE_ADDRESS_FOR_ORDERS_PREVIEW
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
        orderAddressDialogOpen: false,
        deliveryLoading: false
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

        case SET_DELIVERY_LOADING:
            return {...state, deliveryLoading: action.payload};
        case SET_ORDERS_PREVIEW:
            return {...state, ordersPreview: action.payload};
        case UPDATE_ADDRESS_FOR_ORDERS_PREVIEW:
            const newOrdersPreview = updateAddressForPreviewOrder(state.ordersPreview, action.payload);
            return {...state, ordersPreview: newOrdersPreview};
        default:
            return state;
    }
}

const updateAddressForPreviewOrder = (oldOrdersPreview, newAddress) => {
    const updatedOrders = oldOrdersPreview.orders.map(order => {
        if (newAddress.refKey !== order.refKey) {
            return order;
        }

        return {...order, address: newAddress.address, mapPoint: newAddress.mapPoint};
    })
    return {...oldOrdersPreview, orders: updatedOrders};
}

// ----------------- THUNKS -------------------------

export const loadDeliveries = () => (dispatch) => {
    setDeliveryLoading(true);
    deliveriesApi.findAll().then(response => {
        if (response.status === 200) {
            dispatch(setDeliveries(response.data));
        }
        setDeliveryLoading(false);
    })
}

export const loadDelivery = (id) => (dispatch) => {
    deliveriesApi.findById(id).then(response => {
        if (response.status === 200) {
            const delivery = response.data;

            // FIXME [UI]: Reload orders if status -> Draft
            if (delivery.status === 'DRAFT' && delivery.orders.length === 0) {
                findPreviewOrdersForDelivery(dispatch, delivery.deliveryDate);
            } else {
                dispatch(setOrdersPreviewLoading(false));
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

// util method
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

export const linkOrdersToDeliveryAndCalculate = () => (dispatch, getState) => {
    dispatch(setDeliveryLoading(true));
    // TODO: [UI] Getting all state maybe bad idea. Refactor this when moving to slices
    const state = getState();
    const orders = state.deliveries.ordersPreview.orders;
    const deliveryId = state.deliveries.currentDelivery.id;
    deliveriesApi.linkOrders(deliveryId, orders).then(response => {
        if (response.status === 200) {
            deliveriesApi.calculate(deliveryId).then(response => {
                if (response.status === 200) {
                    setCurrentDelivery(response.data);
                    // TODO: [UI Max] Think about loading logic. If error happen we should set loading to false
                    dispatch(setDeliveryLoading(false));
                }
            });
        }
    });

}


export default deliveriesReducer;