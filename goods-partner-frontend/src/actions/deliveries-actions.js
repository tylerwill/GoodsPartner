export const SET_DELIVERIES = "SET_DELIVERIES";
export const SET_CURRENT_DELIVERY = "SET_CURRENT_DELIVERY";
export const ADD_DELIVERY_TO_LIST = "ADD_DELIVERY_TO_LIST";
export const SET_ORDERS_PREVIEW_LOADING = "SET_ORDERS_PREVIEW_LOADING";
export const SET_ORDERS_PREVIEW = "SET_ORDERS_PREVIEW";
export const UPDATE_ADDRESS_FOR_ORDERS_PREVIEW = "UPDATE_ADDRESS_FOR_ORDERS_PREVIEW";
export const SET_DELIVERY_LOADING = "SET_DELIVERY_LOADING";
export const CHANGE_CURRENT_DELIVERY_STATUS = "CHANGE_CURRENT_DELIVERY_STATUS";
export const CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY = "CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY";
export const CHANGE_ROUTE_FOR_CURRENT_DELIVERY = "CHANGE_ROUTE_FOR_CURRENT_DELIVERY";

export const setDeliveries = (deliveries) => {
    return {
        type: SET_DELIVERIES,
        payload: deliveries
    }
}

export const addDeliveryToList = (delivery) => {
    return {
        type: ADD_DELIVERY_TO_LIST,
        payload: delivery
    }
}

export const setCurrentDelivery = (delivery) => {
    return {
        type: SET_CURRENT_DELIVERY,
        payload: delivery
    }
}


export const setOrdersPreviewLoading = (loading) => {
    return {
        type: SET_ORDERS_PREVIEW_LOADING,
        payload: loading
    }
}

export const setOrdersPreview = (orders) => {
    return {
        type: SET_ORDERS_PREVIEW,
        payload: orders
    }
}

export const updateAddressOrdersPreview = (newAddress) => {
    return {
        type: UPDATE_ADDRESS_FOR_ORDERS_PREVIEW,
        payload: newAddress
    }
}

export const setDeliveryLoading = (loading) => {
    return {
        type: SET_DELIVERY_LOADING,
        payload: loading
    }
}

export const changeCurrentDeliveryStatus = (status) => {
    return {
        type: CHANGE_CURRENT_DELIVERY_STATUS,
        payload: status
    }
}

export const changeRoutePointForCurrentDelivery = (routeId, newRoutePoint) => {
    return {
        type: CHANGE_ROUTE_POINT_FOR_CURRENT_DELIVERY,
        payload: {
            routeId,
            newRoutePoint
        }
    }
}

export const changeRouteForCurrentDelivery = (route) => {
    return {
        type: CHANGE_ROUTE_FOR_CURRENT_DELIVERY,
        payload: {
            route
        }
    }
}

