import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { reportsApi } from '../../api/reportsApi'

const initialState = {
	deliveriesStatistics: [],
	loading: false,
	error: ''
}

interface DateRange {
	dateFrom: string
	dateTo: string
}

export const fetchDeliveriesStatistics = createAsyncThunk(
	'reports/deliveriesStatisticsFetch',
	({ dateFrom, dateTo }: DateRange) => {
		return reportsApi
			.getDeliveriesStatistics(dateFrom, dateTo)
			.then(response => response.data)
	}
)

export const reportsSlice = createSlice({
	reducers: {},
	name: 'reports',
	initialState,
	extraReducers: builder => {
		// load deliveries
		builder.addCase(fetchDeliveriesStatistics.pending, state => {
			state.loading = true
		})
		builder.addCase(fetchDeliveriesStatistics.fulfilled, (state, action) => {
			state.loading = false
			state.deliveriesStatistics = action.payload
			state.error = ''
		})
		builder.addCase(fetchDeliveriesStatistics.rejected, (state, action) => {
			state.loading = false
			state.deliveriesStatistics = []
			// TODO: Pfff.....
			// state.error = action.error.message
		})
	}
})

export default reportsSlice.reducer
