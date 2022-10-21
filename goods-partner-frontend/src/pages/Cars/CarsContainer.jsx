import React from 'react';
import {connect} from "react-redux";
import Cars from "./Cars";
import {addCar, deleteCar, loadCars, updateCar,} from "../../reducers/cars-reducer";

const mapStateToProps = (state) => {
    return {
        cars: state.carsPage.cars
    }
}

const CarsContainer = connect(mapStateToProps, {
    loadCars,
    updateCar,
    deleteCar,
    addCar,
})(Cars);

export default CarsContainer;