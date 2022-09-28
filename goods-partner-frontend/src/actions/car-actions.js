export const GET_ALL_CARS = "GET_ALL_CARS";
export const SET_CARS = "SET_CARS";
export const OPEN_CAR_DIALOG = "OPEN_CAR_DIALOG";
export const CLOSE_CAR_DIALOG = "CLOSE_CAR_DIALOG";
export const OPEN_CAR_EDIT_FORM = "OPEN_CAR_EDIT_FORM";
export const CLOSE_CAR_EDIT_FORM = "CLOSE_CAR_EDIT_FORM";
export const ADD_CAR = "ADD_CAR";
export const UPDATE_CAR = "UPDATE_CAR";
export const DELETE_CAR = "DELETE_CAR";
export const SET_CAR = "SET_CAR";


export const setCars = (cars) => {
    return {
        type: SET_CARS,
        payload: cars
    }
}


export const getCarsAction = () => {
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

export const openEditFormActionCreator = () => {
    return {
        type: OPEN_CAR_EDIT_FORM
    }
}
export const closeEditFormActionCreator = () => {
    return {
        type: CLOSE_CAR_EDIT_FORM
    }
}
export const addCarActionCreator = (car) => {
    return {
        type: ADD_CAR,
        payload: car
    }
}

export const updateCarAction = (car) => {
    return {
        type: UPDATE_CAR,
        payload: car
    }
}

export const deleteCarAction = (id) => {
    return {
        type: DELETE_CAR,
        payload: {id}
    }
}

export const setCarActionCreator = (car) => {
    return {
        type: SET_CAR,
        payload: car
    }
}