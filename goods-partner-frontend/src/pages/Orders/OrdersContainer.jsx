import React from 'react';
import {connect} from "react-redux";
import Orders from "./Orders";

const mapStateToProps = (state) => {
    return {
        ordersPreview: state.deliveries.ordersPreview,
        loading: state.deliveries.ordersPreviewLoading
    }
}

const OrdersContainer = connect(mapStateToProps, {})(Orders);

export default OrdersContainer;