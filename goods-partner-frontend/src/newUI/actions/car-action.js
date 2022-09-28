export const GET_ALL_CARS = "GET_ALL_CARS";
export const OPEN_CAR_DIALOG = "OPEN_CAR_DIALOG";
export const CLOSE_CAR_DIALOG = "CLOSE_CAR_DIALOG";

export const getAllCarsActionCreator = () => {
    return {
        type: GET_ALL_CARS
    }
}

export const openCarDialogActionCreator = () => {
    return {
        type: OPEN_CAR_DIALOG
    }
}

export const closeCarDialogActionCreator = () => {
    return {
        type: CLOSE_CAR_DIALOG
    }
}