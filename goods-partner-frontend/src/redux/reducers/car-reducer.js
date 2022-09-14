import * as actionTypes from '../actions/action-types';
import cars from "../../newUI/pages/Cars/Cars";

let initialCars = {
    cars: []
};

const carReducer = (state = initialCars, action) => {
    switch (action.type) {
        case actionTypes.ALL_CARS:
            return {
                cars: action.payload
            }

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

export default carReducer;