import carsReducer from "../features/cars/carsSlice";
import usersReducer from "../features/users/usersSlice";
import currentDeliveryReducer from "../features/currentDelivery/currentDeliverySlice";
import reportsReducer from "../features/reports/reportsSlice";
import historyReducer from "../features/history/historySlice";
import notificationsReducer from "../features/notifications/notificationsSlice";

import {configureStore} from "@reduxjs/toolkit";
import ordersSlice from "../features/orders/ordersSlice";
import {deliveriesApi} from "../api/deliveries/deliveries.api";
import {setupListeners} from "@reduxjs/toolkit/query";
import {ordersApi} from "../api/orders/orders.api";
import {routesApi} from "../api/routes/routes.api";
import {usersApi} from "../api/users/users.api";

const store = configureStore({
    reducer: {
        cars: carsReducer,
        users: usersReducer,
        reports: reportsReducer,
        currentDelivery: currentDeliveryReducer,
        history: historyReducer,
        notifications: notificationsReducer,
        orders: ordersSlice,

        // api
        [deliveriesApi.reducerPath]: deliveriesApi.reducer,
        [ordersApi.reducerPath]: ordersApi.reducer,
        [routesApi.reducerPath]: routesApi.reducer,
        [usersApi.reducerPath]: usersApi.reducer,

    },

    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware()
            .concat(deliveriesApi.middleware)
            .concat(ordersApi.middleware)
            .concat(routesApi.middleware)
            .concat(usersApi.middleware),
})

// optional, but required for refetchOnFocus/refetchOnReconnect behaviors
// see `setupListeners` docs - takes an optional callback as the 2nd arg for customization
setupListeners(store.dispatch);

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

export default store;
