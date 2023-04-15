import currentDeliveryReducer from '../features/currentDelivery/currentDeliverySlice'
import reportsReducer from '../features/reports/reportsSlice'
import authReducer from '../features/auth/authSlice'
// @ts-ignore
import notificationsReducer from '../features/notifications/notificationsSlice'

import {configureStore, isRejectedWithValue, Middleware, MiddlewareAPI} from '@reduxjs/toolkit'
import ordersReducer from '../features/orders/ordersSlice'
import deliveryOrdersReducer from '../features/delivery-orders/deliveryOrdersSlice'
import {deliveriesApi} from '../api/deliveries/deliveries.api'
import {setupListeners} from '@reduxjs/toolkit/query'
import {deliveryOrdersApi} from '../api/delivery-orders/delivery-orders.api'
import {routesApi} from '../api/routes/routes.api'
import {usersApi} from '../api/users/users.api'
import {carsApi} from '../api/cars/cars.api'
import {historyApi} from '../api/history/history.api'
import {shippingApi} from '../api/shipping/shipping.api'
import {ordersApi} from '../api/orders/orders.api'
import {tasksApi} from '../api/tasks/tasks.api'
import {clientsApi} from '../api/clients/clients.api'
import {authApi} from '../api/auth/authApi'
import {settingsApi} from '../api/settings/settings.api'
import {enqueueSnackbar} from "notistack";


const rtkQueryErrorLogger: Middleware =
    (api: MiddlewareAPI) => (next) => (action) => {
        if (isRejectedWithValue(action)) {
            enqueueSnackbar(action.payload.data.message, {
                autoHideDuration: null,
                variant: "error",
                hideIconVariant: true
            });
        }

        return next(action)
    }

const store = configureStore({
    reducer: {
        reports: reportsReducer,
        currentDelivery: currentDeliveryReducer,
        notifications: notificationsReducer,
        orders: ordersReducer,
        deliveryOrders: deliveryOrdersReducer,
        auth: authReducer,

        // api
        [deliveriesApi.reducerPath]: deliveriesApi.reducer,
        [deliveryOrdersApi.reducerPath]: deliveryOrdersApi.reducer,
        [routesApi.reducerPath]: routesApi.reducer,
        [usersApi.reducerPath]: usersApi.reducer,
        [historyApi.reducerPath]: historyApi.reducer,
        [shippingApi.reducerPath]: shippingApi.reducer,
        [ordersApi.reducerPath]: ordersApi.reducer,
        [carsApi.reducerPath]: carsApi.reducer,
        [clientsApi.reducerPath]: clientsApi.reducer,
        [tasksApi.reducerPath]: tasksApi.reducer,
        [settingsApi.reducerPath]: settingsApi.reducer,
        [authApi.reducerPath]: authApi.reducer
    },

    middleware: getDefaultMiddleware =>
        getDefaultMiddleware()
            .concat(deliveriesApi.middleware)
            .concat(deliveryOrdersApi.middleware)
            .concat(routesApi.middleware)
            .concat(usersApi.middleware)
            .concat(historyApi.middleware)
            .concat(shippingApi.middleware)
            .concat(carsApi.middleware)
            .concat(ordersApi.middleware)
            .concat(clientsApi.middleware)
            .concat(tasksApi.middleware)
            .concat(settingsApi.middleware)
            .concat(authApi.middleware)
            .concat(rtkQueryErrorLogger)
})


// optional, but required for refetchOnFocus/refetchOnReconnect behaviors
// see `setupListeners` docs - takes an optional callback as the 2nd arg for customization
setupListeners(store.dispatch)

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

export default store
