import {
    ADD_CAR,
    addCarActionCreator,
    CLOSE_CAR_DIALOG,
    CLOSE_CAR_EDIT_FORM,
    closeEditFormActionCreator,
    DELETE_CAR,
    deleteCarAction,
    getCarsAction,
    OPEN_CAR_DIALOG,
    OPEN_CAR_EDIT_FORM,
    openCarDialogActionCreator,
    openEditFormActionCreator,
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
    carEditFormOpened: false,

    newCar: {
        name: "",
        licencePlate: "",
        cooler: true,
        available: true,
        weightCapacity: "",
        travelCost: "",
    },
    editedCar: {}
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
            };
        case SET_CAR:
            return {
                ...state,
                car: action.car
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
export const updateCarThunkCreator = (car) => (dispatch) => {
    carsApi.update(car, car.id).then(response => {
        console.log("response", response);
        dispatch(updateCarAction(car))
            .catch((error) => console.log(error));
    })
}

export const openEditFormThunkCreator = () => (dispatch) => {
    dispatch(openEditFormActionCreator())
        .catch((error) => console.log(error));
}

export const closeEditFormThunkCreator = () => (dispatch) => {
    dispatch(closeEditFormActionCreator())
        .catch((error) => console.log(error));
}
export const openDialogThunkCreator = () => (dispatch) => {
    dispatch(openCarDialogActionCreator())
        .catch((error) => console.log(error));
}
export const getCarThunkCreator = (id) => (dispatch) => {
    carsApi.findById(id).then(response => {
        console.log("response", response);
        dispatch(setCarActionCreator(response.data))
            .catch((error) => console.log(error));
    })
}

export default carsReducer;
