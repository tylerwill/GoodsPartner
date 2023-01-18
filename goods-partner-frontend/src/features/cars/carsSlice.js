import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { carsApi } from '../../api/carsApi'

const initialState = {
	cars: [],
	loading: false,
	error: ''
}

export const fetchCars = createAsyncThunk('cars/fetch', () =>
	carsApi.findAll().then(response => response.data)
)

export const addCar = createAsyncThunk('cars/add', car =>
	carsApi.add(car).then(response => response.data)
)

export const updateCar = createAsyncThunk('cars/update', car =>
	carsApi.update(car).then(response => response.data)
)

export const deleteCar = createAsyncThunk('cars/delete', id =>
	carsApi.delete(id).then(() => id)
)

const carsSlice = createSlice({
	name: 'cars',
	initialState,
	extraReducers: builder => {
		// load cars
		builder.addCase(fetchCars.pending, state => {
			state.loading = true
		})
		builder.addCase(fetchCars.fulfilled, (state, action) => {
			state.loading = false
			state.cars = action.payload
			state.error = ''
		})
		builder.addCase(fetchCars.rejected, (state, action) => {
			state.loading = false
			state.cars = []
			state.error = action.error.message
		})

		// add car
		builder.addCase(addCar.fulfilled, (state, action) => {
			state.cars.push(action.payload)
			state.error = ''
		})
		builder.addCase(addCar.rejected, (state, action) => {
			state.error = action.error.message
		})

		// update car
		builder.addCase(updateCar.fulfilled, (state, action) => {
			const indexToReplace = state.cars.findIndex(
				car => car.id === action.payload.id
			)
			state.cars[indexToReplace] = action.payload
			state.error = ''
		})
		builder.addCase(updateCar.rejected, (state, action) => {
			state.error = action.error.message
		})

		// delete car
		builder.addCase(deleteCar.fulfilled, (state, action) => {
			state.cars = state.cars.filter(car => car.id !== action.payload)
			state.error = ''
		})
		builder.addCase(deleteCar.rejected, (state, action) => {
			state.error = action.error.message
		})
	}
})

export default carsSlice.reducer
