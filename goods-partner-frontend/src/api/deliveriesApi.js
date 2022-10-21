import {axiosWithSetting} from "./api";

export const deliveriesApi = {
    findAll() {
        return axiosWithSetting.get('/deliveries');
    },

    create(delivery) {
        return axiosWithSetting.post('/deliveries', delivery)
    },

    findById(id) {
        return axiosWithSetting.get(`/deliveries/${id}`);
    },

    // TODO: Remove
    linkOrders(id, orders) {
        return axiosWithSetting.post(`/deliveries/${id}/orders`, orders);
    },

    calculate(id) {
        return axiosWithSetting.post(`/deliveries/${id}/calculate`);
    },

    approve(id) {
        return axiosWithSetting.post(`/deliveries/${id}/approve`);
    },

    applyRoutePointAction(deliveryId, routeId, routePointId, action) {
        return axiosWithSetting.post(`/deliveries/${deliveryId}/routes/${routeId}/route-points/${routePointId}/${action}`);
    },

    applyRouteAction(deliveryId, routeId, action) {
        return axiosWithSetting.post(`/deliveries/${deliveryId}/routes/${routeId}/${action}`);
    },

    findHistory(deliveryId) {
        return axiosWithSetting.get(`/deliveries/${deliveryId}/histories`);
    }
}