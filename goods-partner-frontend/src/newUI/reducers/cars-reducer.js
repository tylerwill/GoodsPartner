import {
    addCarAction,
    CLOSE_CAR_DIALOG,
    OPEN_CAR_DIALOG,
    SET_CARS,
    setCars,
    updateCarAction
} from "../actions/car-actions";
import * as actionTypes from "../../redux/actions/action-types";
import cars from "../pages/Cars/Cars";
import {carsApi} from "../api/api";
import {deleteCarAction, getCarsAction} from "../../redux/actions/car-action";

let initialCars = {
        cars: [],
        carDialogOpened: false
    }
;

const carsReducer = (state = initialCars, action) => {
    debugger;
    switch (action.type) {
        case SET_CARS:
            return {...state, cars: action.payload};
        case OPEN_CAR_DIALOG:
            return {...state, carDialogOpened: true};
        case CLOSE_CAR_DIALOG:
            return {...state, carDialogOpened: false};

        case actionTypes.ADD_CAR:
            return {
                ...state,
                cars: [...state.cars, action.payload],
            };

        case actionTypes.DELETE_CAR:
            return {
                ...state,
                cars: state.cars.filter((car) => car.id !== action.id),
                id: state.findIndex(state => state.id === action.payload)
            };

        case actionTypes.UPDATE_CAR:
            const updatedCar = action.payload;

            const updatedCars = state.cars.map((car) => {
                if (car.id === updatedCar.id) {
                    return updatedCar;
                }
                return cars;
            });

            return {
                ...state,
                cars: updatedCars,
            };

        default:
            return state;
    }
}

export const getCarsThunkCreator = () => (dispatch) => {
    carsApi.getCars().then(response => {
        if (response.status === 200) {
            dispatch(setCars(response.data));
        }
    })
}

export const deleteCarThunkCreator = (id) => (dispatch) => {
    debugger
    carsApi.delete(id).then(response => {
        console.log("response", response);
        dispatch(deleteCarAction());
        dispatch(getCarsAction())
            .catch((error) => console.log(error));
    })
}

export const addCarThunkCreator = (car) => (dispatch) => {
    debugger
    carsApi.add(car).then(response => {
        console.log("response", response);
        dispatch(addCarAction(car));
        dispatch(getCarsAction())
            .catch((error) => console.log(error));
    })
}

export const updateCarThunkCreator = (id, car) => (dispatch) => {
    debugger
    carsApi.update(id, car).then(response => {
        console.log("response", response);
        dispatch(updateCarAction(id, car))
            .catch((error) => console.log(error));
    })
}

export default carsReducer;

export const deleteCar = (id) => {
    return function (dispatch) {
        carsApi.delete(id).then((response) => {
            console.log("response", response);
            dispatch(deleteCarAction());
            dispatch(getCarsAction())
                .catch((error) => console.log(error));

        });
    }
}