import {
    ADD_DELIVERY_TO_LIST,
    addDeliveryToList,
    APPROVE_DELIVERY,
    approveDelivery,
    CHANGE_ROUTE_FOR_CURRENT_DELIVERY,
    CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY,
    changeRoutePointForCurrentDelivery,
    SET_CURRENT_DELIVERY,
    SET_CURRENT_HISTORY,
    SET_DELIVERIES,
    SET_DELIVERY_LOADING,
    SET_ORDERS_PREVIEW,
    SET_ORDERS_PREVIEW_LOADING,
    setCurrentDelivery,
    setCurrentHistory,
    setDeliveries,
    setDeliveryLoading,
    setOrdersPreview,
    setOrdersPreviewLoading,
    UPDATE_ADDRESS_FOR_ORDERS_PREVIEW,
    UPDATE_ORDER
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
        deliveryLoading: false,
        deliveryHistory: []
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

        case APPROVE_DELIVERY:
            return approveCurrentDelivery(state, action.payload);

        case CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY:
            return updateStatusesForCurrentDelivery(state, action.payload);

        case CHANGE_ROUTE_FOR_CURRENT_DELIVERY:
            return updateStatusesForCurrentDelivery(state, action.payload);

        case SET_CURRENT_HISTORY:
            return {...state, deliveryHistory: action.payload};

        case UPDATE_ORDER:
            const newState = {
                ...state,
                ordersPreview: {
                    ...state.ordersPreview,
                    orders: replaceOrder(state.ordersPreview.orders, action.payload)
                }
            };
            return newState;
        default:
            return state;
    }
}

function replaceOrder(orders, newOrder) {
    const newOrders = [...orders];
    for (let i = 0; i < newOrders.length; i++) {
        if (newOrders[i].refKey === newOrder.refKey) {
            newOrders[i] = newOrder;
            break;
        }
    }
    return newOrders;
}


function approveCurrentDelivery(state, statuses) {
    if (state.currentDelivery.id !== statuses.deliveryId) {
        return state;
    }
    const newRoutesStatuses = statuses.routesStatus;
    const newRoutes = [...state.currentDelivery.routes];

    for (let i = 0; i < newRoutes.length; i++) {
        const currentRoute = newRoutes[i];
        const newStatus = foundRouteStatus(newRoutesStatuses, currentRoute.id);
        if (newStatus) {
            currentRoute.status = newStatus;
        }
    }

    const newState = {...state, currentDelivery: {...state.currentDelivery, status: statuses.deliveryStatus}};
    return newState;
}

function foundRouteStatus(routes, id) {
    return routes.find(route => route.id === id)?.routeStatus;
}

const updateStatusesForCurrentDelivery = (state, statuses) => {
    if (state.currentDelivery.id !== statuses.deliveryId) {
        return state;
    }

    const newRoutes = [...state.currentDelivery.routes];
    let changedRoute = undefined;

    for (let i = 0; i < newRoutes.length; i++) {
        const currentRoute = newRoutes[i];
        if (currentRoute.id === statuses.routeId) {
            currentRoute.status = statuses.routeStatus;
            currentRoute.finishTime = statuses.routeFinishTime;
            changedRoute = currentRoute;

            break;
        }
    }

    if (statuses.routePointId) {
        const newRoutePoints = [...changedRoute.routePoints];
        updateRoutePointStatusAndCompleteAt(newRoutePoints, statuses);
    }

    const newCurrentDelivery = {...state.currentDelivery, routes: newRoutes};
    newCurrentDelivery.status = statuses.deliveryStatus;
    //
    return {...state, currentDelivery: newCurrentDelivery};
}

function updateRoutePointStatusAndCompleteAt(routePoints, statuses) {
    for (let i = 0; i < routePoints.length; i++) {
        const routePoint = routePoints[i];
        if (routePoint.id === statuses.routePointId) {
            routePoint.status = statuses.routePointStatus;
            routePoint.completedAt = statuses.pointCompletedAt;
            break;
        }
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
    setDeliveryLoading(true);
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
            setDeliveryLoading(false);
        }
    })
}


// TODO [UI]: Do we need async here? https://redux.js.org/tutorials/fundamentals/part-6-async-logic
export const createDelivery = (date) => (dispatch) => {
    dispatch(setDeliveryLoading(true));
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
            dispatch(setDeliveryLoading(false));
        }
    })
}

// util method
const findPreviewOrdersForDelivery = (dispatch, date) => {
    ordersApi.getOrdersByDate(date)
        .then(response => {
            if (response.status === 200) {
                dispatch(setOrdersPreview(response.data));
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
            dispatch(approveDelivery(response.data));
        }
    })
}

export const updateRoutePoint = (routeId, routePointId, action) => (dispatch, getState) => {
    const state = getState();
    const deliveryId = state.deliveries.currentDelivery.id;

    deliveriesApi.applyRoutePointAction(deliveryId, routeId, routePointId, action).then(response => {
        if (response.status === 200) {
            console.log("response", response.status);
            dispatch(changeRoutePointForCurrentDelivery(response.data));
        }
    })
}

export const updateRoute = (routeId, action) => (dispatch, getState) => {
    const state = getState();
    const deliveryId = state.deliveries.currentDelivery.id;

    deliveriesApi.applyRouteAction(deliveryId, routeId, action).then(response => {
        if (response.status === 200) {
            console.log("response", response.status);
            dispatch(changeRoutePointForCurrentDelivery(response.data));
        }
    })
}

export const loadHistory = () => (dispatch, getState) => {
    const deliveryId = getState().deliveries.currentDelivery.id;

    deliveriesApi.findHistory(deliveryId).then(response => {
        if (response.status === 200) {
            dispatch(setCurrentHistory(response.data));
        }
    })
}

export const getOrderById = (id) => (dispatch, getState) => {
    const state = getState();
    const orders = state.deliveries.currentDelivery.orders;
    return orders.find(order => order.id === id);
}


export default deliveriesReducer;