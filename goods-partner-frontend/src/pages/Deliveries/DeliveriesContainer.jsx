import React from 'react';
import {connect} from "react-redux";
import Deliveries from "./Deliveries";
import {createDelivery, loadDeliveries} from "../../reducers/deliveries-reducer";

const mapStateToProps = (state) => {
    return {
        deliveries: state.deliveries.deliveriesPreview
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    loadDeliveries,
    createDelivery
})(Deliveries);

export default DeliveriesContainer;