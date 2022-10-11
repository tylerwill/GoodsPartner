import {
    ADD_DELIVERY_TO_LIST,
    addDeliveryToList,
    CHANGE_CURRENT_DELIVERY_STATUS, CHANGE_ROUTE_FOR_CURRENT_DELIVERY,
    CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY,
    changeCurrentDeliveryStatus, changeRouteForCurrentDelivery,
    changeRoutePointForCurrentDelivery,
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
        deliveriesPreview: [],
        currentDelivery: {
            "productsShipping": [],
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

        case CHANGE_CURRENT_DELIVERY_STATUS:
            const newVar = {...state, currentDelivery: {...state.currentDelivery, status: action.payload}};
            return newVar;

        case CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY:
            return updateRoutePointForCurrentDelivery(state, action.payload.routeId, action.payload.newRoutePoint);

        case CHANGE_ROUTE_FOR_CURRENT_DELIVERY:
            return updateRouteForCurrentDelivery(state, action.payload.route);
        default:
            return state;
    }
}

const updateRoutePointForCurrentDelivery = (state, routeId, newRoutePoint) => {
    const currentDelivery = {...state.currentDelivery};
    const updatedRoutes = [...currentDelivery.routes];
    const routeToUpdate = updatedRoutes.find(route => route.id === routeId);

    const updatedRoutePoints = routeToUpdate.routePoints;

    for (let i = 0; i < updatedRoutePoints.length; i++) {
        const oldRoutePoint = updatedRoutePoints[i];
        if (oldRoutePoint.id === newRoutePoint.id) {
            updatedRoutePoints[i] = newRoutePoint;
            break;
        }
    }

    return {...state, currentDelivery: {...state.currentDelivery, routes: updatedRoutes}};
}

const updateRouteForCurrentDelivery = (state, route) => {
    const currentDelivery = state.currentDelivery;
    const updatedRoutes = [...currentDelivery.routes];

    for (let i = 0; i < updatedRoutes.length; i++) {
        const oldRoute = updatedRoutes[i];
        if (oldRoute.id === route.id) {
            updatedRoutes[i] = route;
            break;
        }
    }

    debugger;
    const newState = {...state, currentDelivery: {...state.currentDelivery, routes: updatedRoutes}};
    return newState;
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
                    dispatch(setCurrentDelivery(response.data));
                    // TODO: [UI Max] Think about loading logic. If error happen we should set loading to false
                    dispatch(setDeliveryLoading(false));
                }
            });
        }
    });

}

export const approve = (deliveryId) => (dispatch) => {
    deliveriesApi.approve(deliveryId).then(response => {
        if (response.status === 200) {
            dispatch(changeCurrentDeliveryStatus('APPROVED'));
        }
    })
}

export const updateRoutePoint = (routeId, newRoutePoint) => (dispatch, getState) => {
    const state = getState();
    const deliveryId = state.deliveries.currentDelivery.id;
    console.log("update in reducer:", newRoutePoint);
    deliveriesApi.changeRoutePointStatus(deliveryId, routeId, newRoutePoint).then(response => {
        if (response.status === 200) {
            dispatch(changeRoutePointForCurrentDelivery(routeId, newRoutePoint));
        }
    })
}

export const updateRoute = (route) => (dispatch, getState) => {
    const state = getState();
    const deliveryId = state.deliveries.currentDelivery.id;
    console.log("update route in reducer:", route);
    deliveriesApi.changeRouteStatus(deliveryId, route).then(response => {
        if (response.status === 200) {
            dispatch(changeRouteForCurrentDelivery(route));
        }
    })
}

export default deliveriesReducer;