import {
    GET_ORDERS_BY_DATE,
    SET_ORDERS_DATA,
    SET_ORDERS_LOADED,
    setOrders,
    setOrdersLoaded
} from "../actions/orders-actions";
import {ordersApi} from "../api/api";

let initialOrders = {
        orders: [],
        loaded: false,
        date: new Date('2022-02-02'),
        totalOrdersWeight: 0
    }
;

const ordersReducer = (state = initialOrders, action) => {
    switch (action.type) {
        case GET_ORDERS_BY_DATE:
            return {...state, date: action.date, loaded: true};
        case SET_ORDERS_DATA:
            const newState = {...state, ...action.orders}
            return newState;
        case SET_ORDERS_LOADED:
            return {...state, loaded: action.loaded};
        default:
            return state;
    }
}


// TODO: Do we need async here? https://redux.js.org/tutorials/fundamentals/part-6-async-logic
export const getOrders = (date) => (dispatch) => {
    dispatch(setOrdersLoaded(false));
    ordersApi.getOrdersByDate(date)
        .then(response => {
            if (response.status === 200) {
                dispatch(setOrders(response.data));
                dispatch(setOrdersLoaded(true));
            }
        })
}

export default ordersReducer;