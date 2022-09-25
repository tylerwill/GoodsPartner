import {applyMiddleware, combineReducers, compose, legacy_createStore} from "redux";
import thunkMiddleware from "redux-thunk";
import carsReducer from "../reducers/cars-reducer";
import ordersReducer from "../reducers/orders-reducer";
import deliveriesReducer from "../reducers/deliveries-reducer";

let reducers = combineReducers({
    carsPage: carsReducer,
    ordersPage: ordersReducer,
    deliveriesPage: deliveriesReducer
});

const composeEnhancers = (window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({
    trace: true,
    traceLimit: 25
})) || compose;

const store = legacy_createStore(reducers, composeEnhancers(applyMiddleware(thunkMiddleware)
));

window.reactStore = store;

export default store;