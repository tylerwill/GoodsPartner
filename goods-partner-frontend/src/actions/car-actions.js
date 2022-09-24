
export const GET_ALL_CARS = "GET_ALL_CARS";
export const SET_CARS = "SET_CARS";
export const OPEN_CAR_DIALOG = "OPEN_CAR_DIALOG";
export const CLOSE_CAR_DIALOG = "CLOSE_CAR_DIALOG";
export const ADD_CAR = "ADD_CAR";
export const UPDATE_CAR = "UPDATE_CAR";
export const DELETE_CAR = "DELETE_CAR";


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

export const addCarActionCreator = (car) => {
    return {
        type: ADD_CAR,
        payload: car
    }
}

export const updateCarAction = (id, car) => {
    return {
        type: UPDATE_CAR,
        payload: id, car
    }
}

export const deleteCarAction = (id) => {
    return {
        type: DELETE_CAR,
        payload: {id}
    }
}