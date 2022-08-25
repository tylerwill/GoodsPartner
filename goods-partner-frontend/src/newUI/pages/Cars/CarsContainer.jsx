import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {closeCarDialogActionCreator, openCarDialogActionCreator} from "../../actions/car-action";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars,
        isDialogOpened: state.carsPage.carDialogOpened
    }
}

const mapDispathToProps = (dispatch) => {
    return {
        openDialog:() => dispatch(openCarDialogActionCreator()),
        closeDialog:() => dispatch(closeCarDialogActionCreator()),
    }
}

const CarsContainer = connect(mapStateToProps, mapDispathToProps)(Cars);

export default CarsContainer;