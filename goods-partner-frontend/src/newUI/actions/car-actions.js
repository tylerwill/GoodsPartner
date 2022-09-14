import * as actionTypes from "../../redux/actions/action-types";

export const GET_ALL_CARS = "GET_ALL_CARS";
export const OPEN_CAR_DIALOG = "OPEN_CAR_DIALOG";
export const CLOSE_CAR_DIALOG = "CLOSE_CAR_DIALOG";


export const getAllCarsActionCreator = () => {
    return {
        type: GET_ALL_CARS
    }
}

export const openCarDialogActionCreator = () => {
    return {
        type: OPEN_CAR_DIALOG
    }
}

export const closeCarDialogActionCreator = () => {
    return {
        type: CLOSE_CAR_DIALOG
    }
}

export const addCarAction = (car) => {
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
        payload: {id}
    }
}