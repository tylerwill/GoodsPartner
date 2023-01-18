import { axiosWithSetting } from './api'
import { Car } from '../model/Car'

export const carsApi = {
	findAll() {
		return axiosWithSetting.get('/cars')
	},

	add(car: Car) {
		return axiosWithSetting.post(`/cars`, car)
	},

	delete(id: Car) {
		return axiosWithSetting.delete(`/cars/${id}`)
	},

	update(car: Car) {
		return axiosWithSetting.put(`/cars/${car.id}`, car)
	}
}
