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
        date={this.props.calculatedData.date}
        orders={this.props.calculatedData.orders}
        clients={this.props.calculatedData.clients}
        stores={this.props.calculatedData.stores}
        getCalculatedDataByDate={this.getCalculatedDataByDate}
    />
  }
}

let mapStateToProps = (state) => {
  return {
    calculatedData: state.calculation
  }
}

export default compose(connect(mapStateToProps, {getCalculatedDataByDate}))(CalculateContainer);