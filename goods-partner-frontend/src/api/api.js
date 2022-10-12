import axios from "axios";
import {currentHost} from "../util/util";

const defaultOptions = {
    baseURL: currentHost()
};

const axiosWithSetting = axios.create(defaultOptions);


// TODO [UI Max]: Config axios to not duplicate prefix /api/v1
export const ordersApi = {
    getOrdersByDate(date) {
        return axiosWithSetting.get('api/v1/orders', {
            params: {
                date
            }
        });
    }
}

export const carsApi = {
    getCars() {
        return axiosWithSetting.get('api/v1/cars');
    },

    add(car) {
        return axiosWithSetting.post(`api/v1/cars`, car);
    },

    deleteCar(id) {
        return axiosWithSetting.delete(`api/v1/cars/${id}`);
    },

    update(car) {
        return axiosWithSetting.put(`api/v1/cars/${car.id}`, car);
    }
}

export const deliveriesApi = {
    findAll() {
        return axiosWithSetting.get('api/v1/deliveries');
    },

    create(delivery) {
        return axiosWithSetting.post('/api/v1/deliveries', delivery)
    },

    findById(id) {
        return axiosWithSetting.get(`/api/v1/deliveries/${id}`);
    },

    linkOrders(id, orders) {
        return axiosWithSetting.post(`/api/v1/deliveries/${id}/orders`, orders);
    },

    calculate(id) {
        return axiosWithSetting.post(`/api/v1/deliveries/${id}/calculate`);
    },

    approve(id) {
        return axiosWithSetting.put(`/api/v1/deliveries/${id}/approve`);
    },

    changeRoutePointStatus(deliveryId, routeId, routePoint) {
        const routePointId = routePoint.id;
        return axiosWithSetting.put(`/api/v1/deliveries/${deliveryId}/routes/${routeId}/route-points/${routePointId}`, routePoint);
    },

    changeRouteStatus(deliveryId, route) {
        return axiosWithSetting.put(`/api/v1/deliveries/${deliveryId}/routes/${route.id}`, route);
    },

    findHistory(deliveryId) {
        return axiosWithSetting.get(`/api/v1/deliveries/${deliveryId}/histories`);
    }
}

export const reportsApi = {
    getDeliveriesStatistics(dateFrom, dateTo) {
        return axiosWithSetting.get('/api/v1/statistics/deliveries', {params: {dateFrom, dateTo}});
    },

}