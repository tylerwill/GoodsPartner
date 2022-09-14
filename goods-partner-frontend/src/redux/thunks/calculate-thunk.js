import {carsApi, orderApi, routeApi} from "../../api/api";
import {setOrders} from "../actions/order-action";
import {setRoutes, updateRouteAction, updateRoutePointAction} from "../actions/route-action";
import {addCarAction, deleteCarAction, setCars, updateCarAction} from "../actions/car-action";

export const getCalculatedDataByDate = (date) => async dispatch => {
    let orders = await orderApi.getOrdersByDateRequest(date);
    let routes = await routeApi.getRoutesByDateRequest(date);
    dispatch(setOrders(orders.data));
    dispatch(setRoutes(routes.data));
}

export const updateRoutePoint = (updatedRoutePoint) => async dispatch => {
    dispatch(updateRoutePointAction(updatedRoutePoint));
}

export const updateRoute = (updatedRoute) => async dispatch => {
    dispatch(updateRouteAction(updatedRoute));
}

export const getCars = () => async dispatch => {
    let response = await carsApi.getAll();
    dispatch(setCars(response.data));
}

export const addCar = (car) => async dispatch => {
    dispatch(addCarAction(car));
}

export const updateCar = (id, car) => async dispatch => {
    dispatch(updateCarAction(car));
}

export const deleteCar = (id) => async dispatch => {
    dispatch(deleteCarAction(id));
}