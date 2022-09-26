import {
    ADD_CAR,
    addCarActionCreator,
    CLOSE_CAR_DIALOG,
    CLOSE_CAR_EDIT_FORM,
    DELETE_CAR,
    deleteCarAction,
    getCarsAction,
    OPEN_CAR_DIALOG,
    OPEN_CAR_EDIT_FORM,
    SET_CAR,
    SET_CARS,
    setCarActionCreator,
    setCars,
    UPDATE_CAR,
    updateCarAction
} from "../actions/car-actions";
import {carsApi} from "../api/api";

let initialCars = {
    cars: [],
    carDialogOpened: false,
    carEditFormOpened: true,

    name: "",
    licencePlate: "",
    cooler: true,
    available: true,
    weightCapacity: "",
    travelCost: "",

    car: ""
};

const carsReducer = (state = initialCars, action) => {
    switch (action.type) {
        case SET_CARS:
            return {...state, cars: action.payload};
        case OPEN_CAR_DIALOG:
            return {...state, carDialogOpened: true};
        case CLOSE_CAR_DIALOG:
            return {...state, carDialogOpened: false};
        case OPEN_CAR_EDIT_FORM:
            return {...state, carEditFormOpened: true};
        case CLOSE_CAR_EDIT_FORM:
            return {...state, carEditFormOpened: false};

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
        case SET_CAR:
            return {
                ...state,
                car: action.car,
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
        // const updatedCar = action.payload;
        //
        // const updatedCars = state.cars.map((car) => {
        //     if (car.id === updatedCar.id) {
        //         return updatedCar;
        //     }
        //     return cars;
        // });
        //
        // return {
        //     ...state,
        //     cars: updatedCars,
        // };

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
// export const updateCarThunkCreator = (id,car) => (dispatch) => {
//     carsApi.update(id, car).then(response => {
//         console.log("response", response);
//         dispatch(updateCarAction(id, car))
//             .catch((error) => console.log(error));
//     })
// }
export const updateCarThunkCreator = (car) => (dispatch) => {
    debugger;
    carsApi.update(car).then(response => {
        if (response.status === 200) {
            dispatch(setCarActionCreator(car))
                .catch((error) => console.log(error));
        }
    });
}
export const getCarThunkCreator = (id) => (dispatch) => {
    carsApi.findById(id).then(response => {
        console.log("response", response);
        dispatch(setCarActionCreator(response.data))
            .catch((error) => console.log(error));
    })
}

export default carsReducer;
