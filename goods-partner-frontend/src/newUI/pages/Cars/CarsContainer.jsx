import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {closeCarDialogActionCreator, openCarDialogActionCreator} from "../../actions/car-actions";
import {getCarsThunkCreator} from "../../reducers/cars-reducer";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars,
        isDialogOpened: state.carsPage.carDialogOpened
    }
}

// const mapDispatchToProps = (dispatch) => {
//     return
// }

const CarsContainer = connect(mapStateToProps, {
    openCarDialogActionCreator,
    closeCarDialogActionCreator,
    getCarsThunkCreator
})(Cars);

export default CarsContainer;