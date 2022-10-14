import React from 'react';
import {connect} from "react-redux";
import Orders from "./Orders";
import {updateAddressOrdersPreview, updateOrderAction} from "../../../actions/deliveries-actions";

const mapStateToProps = (state) => {
    return {
        currentDeliveryOrders: state.deliveries.currentDelivery.orders,
        ordersPreview: state.deliveries.ordersPreview,
        loading: state.deliveries.ordersPreviewLoading
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        updatePreviewOrderAddress: (newAddress) => dispatch(updateAddressOrdersPreview(newAddress)),
        updateOrder: (order)=> dispatch(updateOrderAction(order))
    };
};

const OrdersContainer = connect(mapStateToProps, mapDispatchToProps)(Orders);

export default OrdersContainer;