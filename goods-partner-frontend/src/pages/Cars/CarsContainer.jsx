import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {closeCarDialogActionCreator} from "../../actions/car-actions";
import {
    addCarThunkCreator,
    closeEditFormThunkCreator,
    deleteCarThunkCreator,
    getCarsThunkCreator,
    getCarThunkCreator,
    openDialogThunkCreator,
    openEditFormThunkCreator,
    updateCarThunkCreator
} from "../../reducers/cars-reducer";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars,
        isDialogOpened: state.carsPage.carDialogOpened,
        isEditFormOpened: state.carsPage.carEditFormOpened,
    }
}

const CarsContainer = connect(mapStateToProps, {
    openEditFormThunkCreator,
    closeEditFormThunkCreator,
    openDialogThunkCreator,
    closeCarDialogActionCreator,
    getCarsThunkCreator,
    deleteCarThunkCreator,
    addCarThunkCreator,
    updateCarThunkCreator,
    getCarThunkCreator,
})(Cars);

export default CarsContainer;