import {axiosWithSetting} from "./api";

export const carsApi = {
    findAll() {
        return axiosWithSetting.get('/cars');
    },

    add(car) {
        return axiosWithSetting.post(`/cars`, car);
    },

    delete(id) {
        return axiosWithSetting.delete(`/cars/${id}`);
    },

    update(car) {
        return axiosWithSetting.put(`/cars/${car.id}`, car);
    }
}