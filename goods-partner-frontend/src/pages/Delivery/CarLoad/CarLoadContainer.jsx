import React from 'react';
import {connect} from "react-redux";
import CarLoad from "./CarLoad";

const mapStateToProps = (state) => {
    return {
        carLoads: state.deliveries.currentDelivery.carLoads
    }
}

const CarLoadContainer = connect(mapStateToProps, {})(CarLoad);

export default CarLoadContainer;