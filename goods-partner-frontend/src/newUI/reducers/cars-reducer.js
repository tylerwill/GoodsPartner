import {
    addCarActionCreator,
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
    carDialogOpened: true,

    name: "",
    licencePlate: "",
    cooler: "",
    available: "",
    weightCapacity: "",
    travelCost: ""
};

const carsReducer = (state = initialCars, action) => {
    switch (action.type) {
        case SET_CARS:
            return {...state, cars: action.payload};
        case OPEN_CAR_DIALOG:
            return {...state, carDialogOpened: true};
        case CLOSE_CAR_DIALOG:
            return {...state, carDialogOpened: false};

        case actionTypes.ADD_CAR: {
            let newCar = {
                name: state.name,
                licencePlate: state.licencePlate,
                cooler: state.cooler,
                available: state.available,
                weightCapacity: state.weightCapacity,
                travelCost: state.travelCost
            };
            return {
                ...state,
                cars: [...state.cars, newCar]
            }
        }

        case actionTypes.DELETE_CAR:
            return {
                ...state,
                cars: state.cars.filter((car) => car.id !== action.id),
                // id: state.findIndex(state => state.id === action.payload)
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
    carsApi.deleteCar(id).then(response => {
        console.log("response", response);
        dispatch(deleteCarAction(id));
        dispatch(getCarsAction())
            .catch((error) => console.log(error));
    });
}
export const addCarThunkCreator = (car) => (dispatch) => {
    carsApi.add(car).then(response => {
        if (response.status === 200) {
            console.log("response", response);
            dispatch(addCarActionCreator(car));
            dispatch(getCarsThunkCreator)
                .catch((error) => console.log(error));
        }
    })
}
export const updateCarThunkCreator = (id, car) => (dispatch) => {
    carsApi.update(id, car).then(response => {
        console.log("response", response);
        dispatch(updateCarAction(id, car))
            .catch((error) => console.log(error));
    })
}

export default carsReducer;
