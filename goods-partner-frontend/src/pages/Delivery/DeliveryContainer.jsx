import React from 'react';
import {connect} from "react-redux";
import Delivery from "./Delivery";
import {linkOrdersToDeliveryAndCalculate, loadDelivery} from "../../reducers/deliveries-reducer";

const mapStateToProps = (state) => {
    return {
        currentDelivery: state.deliveries.currentDelivery,
        ordersPreview: state.deliveries.ordersPreview
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    loadDelivery,
    linkOrdersToDeliveryAndCalculate
})(Delivery);

export default DeliveriesContainer;