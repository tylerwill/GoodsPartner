import {axiosWithSetting} from "./api";
import Delivery from "../model/Delivery";
import {RouteAction, RoutePointAction} from "../model/Actions";

export const deliveriesApi = {
    findAll() {
        return axiosWithSetting.get('/deliveries');
    },

    findAllForDriver() {
        return axiosWithSetting.get('/deliveries/by-driver');
    },

    create(delivery: Delivery) {
        return axiosWithSetting.post('/deliveries', delivery)
    },

    findById(id: number) {
        return axiosWithSetting.get(`/deliveries/${id}`);
    },

    findByIdForDriver(id: number) {
        return axiosWithSetting.get(`/deliveries/${id}/by-driver`);
    },


    calculate(delivery: Delivery) {
        return axiosWithSetting.post(`/deliveries/${delivery.id}/calculate`, delivery);
    },

    approve(id: number) {
        return axiosWithSetting.post(`/deliveries/${id}/approve`);
    },

    applyRoutePointAction(deliveryId: number, routeId: number, routePointId: number, action: RoutePointAction) {
        return axiosWithSetting.post(`/deliveries/${deliveryId}/routes/${routeId}/route-points/${routePointId}/${action}`);
    },

    applyRouteAction(deliveryId: number, routeId: number, action: RouteAction) {
        return axiosWithSetting.post(`/deliveries/${deliveryId}/routes/${routeId}/${action}`);
    },

    findHistory(deliveryId: number) {
        return axiosWithSetting.get(`/deliveries/${deliveryId}/histories`);
    }
}