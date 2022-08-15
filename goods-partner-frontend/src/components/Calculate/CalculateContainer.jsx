import React from "react";
import Calculate from "./Calculate";
import {connect} from "react-redux";
import {compose} from "redux";
import {getCalculatedDataByDate, updateRoute, updateRoutePoint} from "../../redux/thunks/calculate-thunk";

class CalculateContainer extends React.Component {

    getCalculatedDataByDate = (dateForCalculation) => {
        this.props.getCalculatedDataByDate(dateForCalculation);
    }

    getAddresses = () => {
        return this.props.routeData.routes.map(route => {
            let addresses = route.routePoints.map(routePoint => routePoint.address);
            addresses.unshift(route.storeAddress);
            return {
                routeId: route.id,
                addresses: addresses
            }
        });
    }

    render() {
        let routeAddresses = this.props.routeData.date === "" ? "" : this.getAddresses(this.props.routeData.routes);
        return <Calculate
            orders={this.props.orderData}
            routes={this.props.routeData}
            routeAddresses={routeAddresses}
            getCalculatedDataByDate={this.getCalculatedDataByDate}
            updateRoutePoint={this.props.updateRoutePoint}
            updateRoute={this.props.updateRoute}
        />
    }
}

let mapStateToProps = (state) => {
    return {
        orderData: state.orders,
        routeData: state.routes
    }
}

export default compose(connect(mapStateToProps, {
    getCalculatedDataByDate,
    updateRoutePoint, updateRoute
}))(CalculateContainer);