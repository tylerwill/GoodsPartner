import {applyMiddleware, combineReducers, compose, legacy_createStore} from "redux";
import thunkMiddleware from "redux-thunk";
import calculateReducer from "./reducers/calculate-reducer";

let reducers = combineReducers({
  calculation : calculateReducer
});

const composeEnhancers = (window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({
  trace: true,
  traceLimit: 25
})) || compose;

const store = legacy_createStore(reducers, composeEnhancers(applyMiddleware(thunkMiddleware)
));

window.reactStore = store;

export default store;