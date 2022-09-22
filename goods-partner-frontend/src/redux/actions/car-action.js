import * as actionTypes from './action-types';

export const setCars = (cars) => {
    return {
        type: actionTypes.ALL_CARS,
        payload: cars
    }
}

export const getCarsAction = (cars) => {
    return {
        type: actionTypes.ALL_CARS,
        payload: cars
    }
}

export const addCarActionCreator = (car) => {
    return {
        type: actionTypes.ADD_CAR,
        payload: car
    }
}

export const updateCarAction = (id, car) => {
    return {
        type: actionTypes.UPDATE_CAR,
        payload: id, car
    }
}

export const deleteCarAction = (id) => {
    return {
        type: actionTypes.DELETE_CAR,
        payload: id
    }
}

