import React from 'react';
import {connect} from "react-redux";
import Orders from "./Orders";
import {updateAddressOrdersPreview} from "../../actions/deliveries-actions";

const mapStateToProps = (state) => {
    return {
        ordersPreview: state.deliveries.ordersPreview,
        loading: state.deliveries.ordersPreviewLoading
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        updatePreviewOrderAddress: (newAddress) => dispatch(updateAddressOrdersPreview(newAddress))
    };
};

const OrdersContainer = connect(mapStateToProps, mapDispatchToProps)(Orders);

export default OrdersContainer;