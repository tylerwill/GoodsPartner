import carsReducer from "../features/cars/carsSlice";
import deliveriesReducer from "../features/deliveries/deliveriesSlice";
import reportsReducer from "../features/reports/reportsSlice";
import deliveriesReducerO from "../reducers/deliveries-reducer";
import {configureStore} from "@reduxjs/toolkit";

const store = configureStore({
    reducer: {
        cars: carsReducer,
        deliveries : deliveriesReducer,
        carsPage: carsReducer,
        reports: reportsReducer,
        deliveriesO: deliveriesReducerO
    }
})

window.reactStore = store;

export default store;