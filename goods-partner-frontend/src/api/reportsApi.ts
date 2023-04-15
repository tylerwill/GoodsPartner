import {axiosWithSetting} from './api'

export const reportsApi = {
	getDeliveriesStatistics(dateFrom: string, dateTo: string) {
		return axiosWithSetting.get('/statistics/deliveries', {
			params: { dateFrom, dateTo }
		})
	}
}
