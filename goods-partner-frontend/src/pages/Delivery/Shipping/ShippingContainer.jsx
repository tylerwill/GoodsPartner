import React from 'react';
import {connect} from "react-redux";
import Shipping from "./Shipping";

const mapStateToProps = (state) => {
    return {
        productsShipping: state.deliveries.currentDelivery.productsShipping
    }
}

const ShippingContainer = connect(mapStateToProps, {})(Shipping);

export default ShippingContainer;