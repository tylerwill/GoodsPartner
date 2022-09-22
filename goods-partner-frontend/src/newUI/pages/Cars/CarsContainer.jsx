import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {closeCarDialogActionCreator, openCarDialogActionCreator} from "../../actions/car-actions";
import {
    addCarThunkCreator,
    deleteCarThunkCreator,
    getCarsThunkCreator,
    updateCarThunkCreator
} from "../../reducers/cars-reducer";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars,
        isDialogOpened: state.carsPage.carDialogOpened
    }
}

const CarsContainer = connect(mapStateToProps, {
    openCarDialogActionCreator,
    closeCarDialogActionCreator,
    getCarsThunkCreator,
    deleteCarThunkCreator,
    addCarThunkCreator,
    updateCarThunkCreator,
})(Cars);

export default CarsContainer;