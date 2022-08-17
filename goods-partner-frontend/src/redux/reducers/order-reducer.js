import * as actionTypes from '../actions/action-types';

let initialOrders = {
    date: "",
    orders: []
}

const orderReducer = (state = initialOrders, action) => {
    switch (action.type) {
        case actionTypes.ORDERS_BY_DATE:
            return {
                date: action.orders.date,
                orders: action.orders.orders
            }
        default:
            return state;
    }
}

export default orderReducer;