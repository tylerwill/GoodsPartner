import { createSlice } from '@reduxjs/toolkit'

const initialState = {
	currentNotification: null
}

const notificationsSlice = createSlice({
	name: 'notifications',
	initialState,
	reducers: {
		setNotification: (state, action) => {
			const newNotification = action.payload
			state.currentNotification = newNotification
		}
	}
})

export default notificationsSlice.reducer
export const { setNotification } = notificationsSlice.actions
