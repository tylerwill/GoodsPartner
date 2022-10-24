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


    calculate(delivery) {
        return axiosWithSetting.post(`/deliveries/${delivery.id}/calculate`, delivery);
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