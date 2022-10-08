export const SET_CARS = "SET_CARS";
export const ADD_CAR = "ADD_CAR";
export const UPDATE_CAR = "UPDATE_CAR";
export const DELETE_CAR = "DELETE_CAR";

export const setCars = (cars) => {
    return {
        type: SET_CARS,
        payload: cars
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
