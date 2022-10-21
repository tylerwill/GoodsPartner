import React from 'react';
import {connect} from "react-redux";
import Reports from "./Reports";
import {getDeliveriesStatistics} from "../../reducers/reports-reducer";

const mapStateToProps = (state) => {
    return {
        deliveriesStatistics: state.reports.deliveriesStatistics
    }
}

const DeliveriesContainer = connect(mapStateToProps, {
    getDeliveriesStatistics
})(Reports);

export default DeliveriesContainer;