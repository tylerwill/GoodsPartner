import carsReducer from "../features/cars/carsSlice";
import deliveriesReducer from "../features/deliveries/deliveriesSlice";
import currentDeliveryReducer from "../features/currentDelivery/currentDeliverySlice";
import reportsReducer from "../features/reports/reportsSlice";
import historyReducer from "../features/history/historySlice";

import {configureStore} from "@reduxjs/toolkit";

const store = configureStore({
    reducer: {
        cars: carsReducer,
        deliveriesList: deliveriesReducer,
        reports: reportsReducer,
        currentDelivery: currentDeliveryReducer,
        history: historyReducer,
    }
})

window.reactStore = store;

export default store;