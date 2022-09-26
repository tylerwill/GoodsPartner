import {
    ADD_CAR,
    addCarActionCreator,
    CLOSE_CAR_DIALOG, DELETE_CAR,
    OPEN_CAR_DIALOG,
    SET_CARS,
    setCars, UPDATE_CAR,
    updateCarAction
} from "../actions/car-actions";
import cars from "../pages/Deliveries/Deliveries";
import {carsApi} from "../api/api";
import {deleteCarAction, getCarsAction} from "../actions/car-actions";

let initialCars = {
    cars: [],
    carDialogOpened: false,

    name: "",
    licencePlate: "",
    cooler: true,
    available: true,
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

        case ADD_CAR: {
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

        case DELETE_CAR:
            return {
                ...state,
                cars: state.cars.filter((car) => car.id !== action.id),
                // id: state.findIndex(state => state.id === action.payload)
            };

        case UPDATE_CAR:
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
