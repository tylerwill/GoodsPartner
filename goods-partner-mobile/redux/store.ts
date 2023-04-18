import {configureStore, isRejectedWithValue, Middleware, MiddlewareAPI} from '@reduxjs/toolkit'
import {setupListeners} from '@reduxjs/toolkit/query'
import {authApi} from '../api/auth/authApi'
import {routesApi} from '../api/routes/routes.api'
import authReducer from '../features/auth/authSlice'

const rtkQueryErrorLogger: Middleware =
    (api: MiddlewareAPI) => (next) => (action) => {
        if (isRejectedWithValue(action)) {
            console.log('error', action.payload);
        }

        return next(action)
    }

const store = configureStore({
    reducer: {
        auth: authReducer,


        [routesApi.reducerPath]: routesApi.reducer,
        [authApi.reducerPath]: authApi.reducer
    },

    middleware: getDefaultMiddleware =>
        getDefaultMiddleware()
            .concat(routesApi.middleware)
            .concat(authApi.middleware)
            .concat(rtkQueryErrorLogger)
    ,
})

// optional, but required for refetchOnFocus/refetchOnReconnect behaviors
// see `setupListeners` docs - takes an optional callback as the 2nd arg for customization
setupListeners(store.dispatch)

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

export default store
