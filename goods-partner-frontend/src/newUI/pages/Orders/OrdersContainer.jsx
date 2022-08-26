import React from 'react';
import {connect} from "react-redux";
import Orders from "./Orders";
import {getOrdersByDate} from "../../actions/orders-actions";

const mapStateToProps = (state) => {
    return {
        orders: state.ordersPage.orders,
        loaded: state.ordersPage.loaded,
        date: state.ordersPage.date
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getByDate: (date) => dispatch(getOrdersByDate(date))
    }
}

const OrdersContainer = connect(mapStateToProps, mapDispatchToProps)(Orders);

export default OrdersContainer;