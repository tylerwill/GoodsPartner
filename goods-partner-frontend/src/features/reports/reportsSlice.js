import {createAsyncThunk, createSlice} from '@reduxjs/toolkit'
import {reportsApi} from "../../api/reportsApi";

const initialState = {
    deliveriesStatistics: [],
    loading: false,
    error: ''
};

export const fetchDeliveriesStatistics = createAsyncThunk('reports/deliveriesStatisticsFetch',
    ({dateFrom, dateTo}) => {
        return reportsApi.getDeliveriesStatistics(dateFrom, dateTo)
            .then(response => response.data);
    })

const reportsSlice = createSlice({
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
            state.error = action.error.message
        })
    }
})

export default reportsSlice.reducer