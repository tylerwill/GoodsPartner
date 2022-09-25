import React from 'react';
import {connect} from "react-redux";
import Delivery from "./Delivery";
import {loadDelivery} from "../../reducers/deliveries-reducer";

const mapStateToProps = (state) => {
    return {
        currentDelivery: state.deliveries.currentDelivery
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    loadDelivery
})(Delivery);

export default DeliveriesContainer;