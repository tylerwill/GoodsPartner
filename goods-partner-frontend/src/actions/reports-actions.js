export const SET_DELIVERIES_STATISTICS = "SET_DELIVERIES_STATISTICS";

export const setDeliveriesStatistics = (deliveriesStatistics) => {
    return {
        type: SET_DELIVERIES_STATISTICS,
        payload: deliveriesStatistics
    }
}

