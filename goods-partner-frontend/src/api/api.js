import axios from "axios";
import {currentHost} from "../util/util";

const host = currentHost();
const defaultOptions = {
    baseURL: `${host}api/v1/`
};

export const axiosWithSetting = axios.create(defaultOptions);

// TODO [UI Max]: Config axios to not duplicate prefix /api/v1
export const ordersApi = {
    getOrdersByDate(date) {
        return axiosWithSetting.get('/orders', {
            params: {
                date
            }
        });
    }
}

export const deliveriesApi = {
    findById(id) {
        return axiosWithSetting.get(`//deliveries/${id}`);
    },

    linkOrders(id, orders) {
        return axiosWithSetting.post(`//deliveries/${id}/orders`, orders);
    },

    calculate(id) {
        return axiosWithSetting.post(`//deliveries/${id}/calculate`);
    },

    approve(id) {
        return axiosWithSetting.post(`//deliveries/${id}/approve`);
    },

    applyRoutePointAction(deliveryId, routeId, routePointId, action) {
        return axiosWithSetting.post(`//deliveries/${deliveryId}/routes/${routeId}/route-points/${routePointId}/${action}`);
    },

    applyRouteAction(deliveryId, routeId, action) {
        return axiosWithSetting.post(`//deliveries/${deliveryId}/routes/${routeId}/${action}`);
    },

    findHistory(deliveryId) {
        return axiosWithSetting.get(`//deliveries/${deliveryId}/histories`);
    }
}

export const reportsApi = {
    getDeliveriesStatistics(dateFrom, dateTo) {
        return axiosWithSetting.get('//statistics/deliveries', {params: {dateFrom, dateTo}});
    },

}