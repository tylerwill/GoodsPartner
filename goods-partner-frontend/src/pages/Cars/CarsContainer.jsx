import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {closeCarDialogActionCreator, openCarDialogActionCreator} from "../../actions/car-actions";
import {
    addCarThunkCreator,
    deleteCarThunkCreator,
    getCarsThunkCreator, getCarThunkCreator,
    updateCarThunkCreator
} from "../../reducers/cars-reducer";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars,
        car:state.carsPage.car,
        isDialogOpened: state.carsPage.carDialogOpened,
        isEditFormOpened: state.carsPage.carEditFormOpened,
    }
}

const CarsContainer = connect(mapStateToProps, {
    openCarDialogActionCreator,
    closeCarDialogActionCreator,
    getCarsThunkCreator,
    deleteCarThunkCreator,
    addCarThunkCreator,
    updateCarThunkCreator,
    getCarThunkCreator
})(Cars);

export default CarsContainer;