import React from 'react';
import {connect} from "react-redux";
import Delivery from "./Delivery";
import {
    approve,
    linkOrdersToDeliveryAndCalculate,
    loadDelivery, updateRoute,
    updateRoutePoint
} from "../../reducers/deliveries-reducer";

const mapStateToProps = (state) => {
    return {
        currentDelivery: state.deliveries.currentDelivery,
        ordersPreview: state.deliveries.ordersPreview
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    loadDelivery,
    linkOrdersToDeliveryAndCalculate,
    approve,
    updateRoutePoint,
    updateRoute
})(Delivery);

export default DeliveriesContainer;