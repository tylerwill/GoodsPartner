import axios from "axios";
import {currentHost} from "../../util/util";

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