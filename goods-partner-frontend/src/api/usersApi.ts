import { axiosWithSetting } from './api'

export const usersApi = {
	getCurrentUser() {
		return axiosWithSetting.get(`/users/auth`)
	}
}
