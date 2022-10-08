import {
    ADD_CAR,
    addCarActionCreator,
    DELETE_CAR,
    deleteCarAction,
    SET_CARS,
    setCars,
    UPDATE_CAR,
    updateCarAction
} from "../actions/car-actions";
import {carsApi} from "../api/api";

let initialCars = {
    cars: [],
};

const carsReducer = (state = initialCars, action) => {
    switch (action.type) {
        case SET_CARS:
            return {...state, cars: action.payload};

        case ADD_CAR: {
            return {
                ...state,
                cars: [...state.cars, action.payload]
            }
        }

        case DELETE_CAR:
            const filteredCars = state.cars.filter((car) => car.id !== action.payload.id);
            return {
                ...state,
                cars: filteredCars,
            };

        case UPDATE_CAR:
            const cars = [...state.cars];
            const id = cars.findIndex((car) => car.id === action.payload.id
            );
            cars[id] = action.payload;
            return {
                ...state,
                cars
            };

        default:
            return state;
    }
}

export const loadCars = () => (dispatch) => {
    carsApi.getCars().then(response => {
        if (response.status === 200) {
            dispatch(setCars(response.data));
        }
    })
}

export const deleteCar = (id) => (dispatch) => {
    carsApi.deleteCar(id).then(response => {
        dispatch(deleteCarAction(id));
    });
}
export const addCar = (car) => (dispatch) => {
    carsApi.add(car).then(response => {
        if (response.status === 200) {
            console.log("response", response);
            dispatch(addCarActionCreator(car));
        }
    })
}
export const updateCar = (car) => (dispatch) => {
    carsApi.update(car).then(response => {
        dispatch(updateCarAction(car));
    })
}

export default carsReducer;
