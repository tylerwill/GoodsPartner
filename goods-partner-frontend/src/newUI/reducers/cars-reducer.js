import {CLOSE_CAR_DIALOG, GET_ALL_CARS, OPEN_CAR_DIALOG, SET_CARS, setCars} from "../actions/car-actions";
import * as actionTypes from "../../redux/actions/action-types";
import cars from "../pages/Cars/Cars";
import {carsApi} from "../api/api";

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
                cars: state.cars.filter((car) => car.id !== action.payload),
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


export default carsReducer;