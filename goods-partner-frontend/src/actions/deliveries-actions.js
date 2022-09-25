export const SET_DELIVERIES = "SET_DELIVERIES";
export const CREATE_DELIVERY = "CREATE_DELIVERY";
export const SET_CURRENT_DELIVERY = "SET_CURRENT_DELIVERY";
export const ADD_DELIVERY_TO_LIST = "ADD_DELIVERY_TO_LIST";

export const setDeliveries = (deliveries) => {
    return {
        type: SET_DELIVERIES,
        payload: deliveries
    }
}

export const createDelivery = (delivery) => {
    return {
        type: CREATE_DELIVERY,
        payload: delivery
    }
}

export const addDeliveryToList = (delivery) => {
    return {
        type: ADD_DELIVERY_TO_LIST,
        payload: delivery
    }
}

export const setCurrentDelivery = (delivery) => {
    return {
        type: SET_CURRENT_DELIVERY,
        payload: delivery
    }
}