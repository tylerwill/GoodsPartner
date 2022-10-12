import {reportsApi} from "../api/api";
import {SET_DELIVERIES_STATISTICS, setDeliveriesStatistics} from "../actions/reports-actions";

const initialReports = {
    deliveriesStatistics: null,
};

const reportsReducer = (state = initialReports, action) => {
    switch (action.type) {
        case SET_DELIVERIES_STATISTICS:
            return {...state, deliveriesStatistics: action.payload};
        default:
            return state;
    }
}

export const getDeliveriesStatistics = (dateFrom, dateTo) => (dispatch) => {
    reportsApi.getDeliveriesStatistics(dateFrom, dateTo).then(response => {
        if (response.status === 200) {
            dispatch(setDeliveriesStatistics(response.data));
        }
    })
}

export default reportsReducer;
