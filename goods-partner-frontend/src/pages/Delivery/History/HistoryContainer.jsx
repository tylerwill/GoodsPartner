import React from 'react';
import {connect} from "react-redux";
import {loadHistory} from "../../../reducers/deliveries-reducer";

import History from "./History";

const mapStateToProps = (state) => {
    return {
        deliveryHistory:state.deliveries.deliveryHistory
    }
}

const HistoryContainer = connect(mapStateToProps, {
    loadHistory
})(History);

export default HistoryContainer;