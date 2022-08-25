import React from 'react';
import {connect} from "react-redux";
import Orders from "./Orders";

const mapStateToProps = (state) => {
    return {

    }
}

const mapDispathToProps = (dispatch) => {
    return {

    }
}

const OrdersContainer = connect(mapStateToProps, mapDispathToProps)(Orders);

export default OrdersContainer;