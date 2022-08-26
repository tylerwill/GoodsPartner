import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {closeCarDialogActionCreator, openCarDialogActionCreator} from "../../actions/car-actions";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars,
        isDialogOpened: state.carsPage.carDialogOpened
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        openDialog:() => dispatch(openCarDialogActionCreator()),
        closeDialog:() => dispatch(closeCarDialogActionCreator()),
    }
}

const CarsContainer = connect(mapStateToProps, mapDispatchToProps)(Cars);

export default CarsContainer;