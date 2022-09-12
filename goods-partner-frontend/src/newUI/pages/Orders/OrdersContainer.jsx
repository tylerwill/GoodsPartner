import React from 'react';
import {connect} from "react-redux";
import Orders from "./Orders";
import {getOrders} from "../../reducers/orders-reducer";

const mapStateToProps = (state) => {
    return {
        validOrders: state.ordersPage.validOrders,
        invalidOrders: state.ordersPage.invalidOrders,
        loaded: state.ordersPage.loaded,
        date: state.ordersPage.date
    }
}

const OrdersContainer = connect(mapStateToProps, {
    getOrders
})(Orders);

export default OrdersContainer;