import {axiosWithSetting} from "./api";
import {User} from "../model/User";

export const usersApi = {
    findAll() {
        return axiosWithSetting.get('/users');
    },

    add(user: User) {
        return axiosWithSetting.post(`/users`, user);
    },

    delete(id: number) {
        return axiosWithSetting.delete(`/users/${id}`);
    },

    update(user: User) {
        return axiosWithSetting.put(`/users/${user.id}`, user);
    },

    getCurrentUser() {
        return axiosWithSetting.get(`/users/auth`);
    }
}