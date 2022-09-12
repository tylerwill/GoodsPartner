export const GET_ORDERS_BY_DATE = "GET_ORDERS_BY_DATE";
export const SET_ORDERS_DATA = "SET_ORDERS_DATA";
export const SET_ORDERS_LOADED = "SET_ORDERS_LOADED";

export const getOrdersByDate = (date) => {
    return {
        type: GET_ORDERS_BY_DATE,
        date
    }
}

export const setOrders = (orders) => {
    return {
        type: SET_ORDERS_DATA,
        orders
    }
}

export const setOrdersLoaded = (loaded) => {
    return {
        type: SET_ORDERS_LOADED,
        loaded
    }
}
