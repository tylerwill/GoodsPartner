import React from 'react';
import {connect} from "react-redux";
import Routes from "./Routes";
import deliveries from "../../Deliveries/Deliveries";

const mapStateToProps = (state) => {
    return {
        routes: state.deliveries.currentDelivery.routes
    }
}

const RoutesContainer = connect(mapStateToProps, {})(Routes);

export default RoutesContainer;