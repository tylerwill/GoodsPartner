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

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

export default store;