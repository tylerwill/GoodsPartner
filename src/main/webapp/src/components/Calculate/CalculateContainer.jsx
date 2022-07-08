import React from "react";
import Calculate from "./Calculate";
import {connect} from "react-redux";
import {compose} from "redux";
import {getCalculatedDataByDate} from "../../redux/thunks/calculate-thunk";

class CalculateContainer extends React.Component {

  getCalculatedDataByDate = (dateForCalculation) => {
    this.props.getCalculatedDataByDate(dateForCalculation);
  }

  render() {
    return <Calculate
        // date={this.props.calculatedData.date}
        orders={this.props.orderData}
        routes={this.props.routeData}
        stores={this.props.storeData}
        getCalculatedDataByDate={this.getCalculatedDataByDate}
    />
  }
}

let mapStateToProps = (state) => {
  return {
    // calculatedData: state.calculation
    orderData: state.orders,
    routeData: state.routes,
    storeData: state.stores
  }
}

export default compose(connect(mapStateToProps, {getCalculatedDataByDate}))(CalculateContainer);