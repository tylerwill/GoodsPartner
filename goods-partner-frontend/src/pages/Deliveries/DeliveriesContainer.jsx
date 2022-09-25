import React from 'react';
import {connect} from "react-redux";
import Deliveries from "./Deliveries";
import {getDeliveries} from "../../reducers/deliveries-reducer";
// import {
//
// } from "../../reducers/deliveries-reducer";

const mapStateToProps = (state) => {
    return {
        deliveries: state.deliveriesPage.deliveries
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    getDeliveries
})(Deliveries);

export default DeliveriesContainer;