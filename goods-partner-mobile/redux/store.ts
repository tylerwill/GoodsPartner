import {configureStore} from '@reduxjs/toolkit'
import {setupListeners} from '@reduxjs/toolkit/query'
import {routesApi} from '../api/routes/routes.api'

const store = configureStore({
    reducer: {
        // api
        [routesApi.reducerPath]: routesApi.reducer,
    },

    middleware: getDefaultMiddleware =>
        getDefaultMiddleware()
            .concat(routesApi.middleware)
})


// optional, but required for refetchOnFocus/refetchOnReconnect behaviors
// see `setupListeners` docs - takes an optional callback as the 2nd arg for customization
setupListeners(store.dispatch)

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

export default store
