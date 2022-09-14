import {CLOSE_CAR_DIALOG, GET_ALL_CARS, OPEN_CAR_DIALOG} from "../actions/car-actions";
import * as actionTypes from "../../redux/actions/action-types";
import cars from "../pages/Cars/Cars";

let initialCars = {
        cars: [
            {
                "id": 51,
                "name": "Mercedes Sprinter",
                "licencePlate": "AA 1111 CT",
                "driver": "Oleg Dudka",
                "weightCapacity": 2000,
                "cooler": false,
                "available": true,
                "loadSize": 0.0,
                "travelCost": 12
            },
            {
                "id": 101,
                "name": "Mercedes Vito",
                "licencePlate": "AA 2222 CT",
                "driver": "Ivan Piddubny",
                "weightCapacity": 1000,
                "cooler": false,
                "available": true,
                "loadSize": 0.0,
                "travelCost": 10
            },
            {
                "id": 151,
                "name": "Mercedes Sprinter",
                "licencePlate": "AA 3333 CT",
                "driver": "Anton Geraschenko",
                "weightCapacity": 2500,
                "cooler": true,
                "available": true,
                "loadSize": 0.0,
                "travelCost": 15
            },
            {
                "id": 201,
                "name": "Mercedes 818",
                "licencePlate": "AA 4444 CT",
                "driver": "Serhiy Kotovich",
                "weightCapacity": 4000,
                "cooler": false,
                "available": true,
                "loadSize": 0.0,
                "travelCost": 20
            }
        ],
        carDialogOpened: false
    }
;

const carsReducer = (state = initialCars, action) => {
    switch (action.type) {
        case GET_ALL_CARS:
            return initialCars;
        case OPEN_CAR_DIALOG:
            return {...state, carDialogOpened: true};
        case CLOSE_CAR_DIALOG:
            return {...state, carDialogOpened: false};

        case actionTypes.ADD_CAR:
            return {
                ...state,
                cars: [...state.cars, action.payload],
            };

        // case actionTypes.ADD_CAR:
        // let newCar = {
        //     id: cars.length === 0 ? 1 : cars[cars.length - 1].id + 1,
        //     name: "",
        //     licence_plate: "",
        //     driver: "",
        //     weight_capacity: "",
        //     cooler: "",
        //     available: "",
        //     travel_cost: ""
        // };
        // state.cars.push(newCar);

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

export default carsReducer;