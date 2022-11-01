import carsReducer from "../features/cars/carsSlice";
import usersReducer from "../features/users/usersSlice";
import deliveriesReducer from "../features/deliveries/deliveriesSlice";
import currentDeliveryReducer from "../features/currentDelivery/currentDeliverySlice";
import reportsReducer from "../features/reports/reportsSlice";
import historyReducer from "../features/history/historySlice";
import notificationsReducer from "../features/notifications/notificationsSlice";

import {configureStore} from "@reduxjs/toolkit";

const store = configureStore({
    reducer: {
        cars: carsReducer,
        users: usersReducer,
        deliveriesList: deliveriesReducer,
        reports: reportsReducer,
        currentDelivery: currentDeliveryReducer,
        history: historyReducer,
        notifications: notificationsReducer
    }
})

window.reactStore = store;

export default store;