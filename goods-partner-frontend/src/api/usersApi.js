import {axiosWithSetting} from "./api";

export const usersApi = {
    findAll() {
        return axiosWithSetting.get('/users');
    },

    add(user) {
        return axiosWithSetting.post(`/users`, user);
    },

    delete(id) {
        return axiosWithSetting.delete(`/users/${id}`);
    },

    update(user) {
        return axiosWithSetting.put(`/users/${user.id}`, user);
    }
}