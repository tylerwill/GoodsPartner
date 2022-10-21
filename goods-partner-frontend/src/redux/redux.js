import {applyMiddleware, combineReducers, compose, legacy_createStore} from "redux";
import thunkMiddleware from "redux-thunk";
import carsReducer from "../reducers/cars-reducer";
import deliveriesReducer from "../reducers/deliveries-reducer";
import reportsReducer from "../reducers/reports-reducer";

let reducers = combineReducers({
    carsPage: carsReducer,
    deliveries: deliveriesReducer,
    reports: reportsReducer
});

const composeEnhancers = (window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({
    trace: true,
    traceLimit: 25
})) || compose;

const store = legacy_createStore(reducers, composeEnhancers(applyMiddleware(thunkMiddleware)
));

window.reactStore = store;

export default store;