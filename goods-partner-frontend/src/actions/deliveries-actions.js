export const SET_DELIVERIES = "SET_DELIVERIES";

export const setDeliveries = (deliveries) => {
    return {
        type: SET_DELIVERIES,
        payload: deliveries
    }
}