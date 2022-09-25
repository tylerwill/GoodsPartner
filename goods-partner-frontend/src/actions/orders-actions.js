export const GET_ORDERS_BY_DATE = "GET_ORDERS_BY_DATE";
export const SET_ORDERS_DATA = "SET_ORDERS_DATA";


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

