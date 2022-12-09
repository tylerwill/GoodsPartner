import {axiosWithSetting} from "./api";

export const ordersApi = {
    fetchCompleted() {
        return axiosWithSetting.get('/orders/completed');
    },
    fetchSkipped() {
        return axiosWithSetting.get('/orders/skipped');
    },
    reschedule(rescheduleDate: string, orderIds: Array<number>) {
        return axiosWithSetting.post('/orders/reschedule', {rescheduleDate, orderIds});
    },

    delete(orderIds: Array<number>) {
        return axiosWithSetting.post('/orders/remove', {orderIds});
    }
}