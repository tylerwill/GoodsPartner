import axios from "axios";
import {currentHost} from "../util/util";

const defaultOptions = {
    baseURL: currentHost()
};

const axiosWithSetting = axios.create(defaultOptions);

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
        console.log("envs", process.env);
        return axiosWithSetting.post(`api/v1/cars`, {...car});
    },

    deleteCar(id) {
        console.log("envs", process.env);
        return axiosWithSetting.delete(`api/v1/cars/${id}`);
    },

    update(id, car) {
        console.log("envs", process.env);
        return axiosWithSetting.put(`api/v1/cars/${id}`, {car});
    }
}