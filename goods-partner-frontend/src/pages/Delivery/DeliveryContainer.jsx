import React from 'react';
import {connect} from "react-redux";
import Delivery from "./Delivery";
import {
    approve, getOrderById,
    linkOrdersToDeliveryAndCalculate,
    loadDelivery, updateRoute,
    updateRoutePoint
} from "../../reducers/deliveries-reducer";

const mapStateToProps = (state) => {
    return {
        currentDelivery: state.deliveries.currentDelivery,
        ordersPreview: state.deliveries.ordersPreview,
        deliveriesLoading: state.deliveries.deliveryLoading
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    loadDelivery,
    linkOrdersToDeliveryAndCalculate,
    approve,
    updateRoutePoint,
    updateRoute,
    getOrderById
})(Delivery);

export default DeliveriesContainer;