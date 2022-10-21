import {axiosWithSetting} from "./api";

export const reportsApi = {
    getDeliveriesStatistics(dateFrom, dateTo) {
        return axiosWithSetting.get('/statistics/deliveries', {params: {dateFrom, dateTo}});
    }
}