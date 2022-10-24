import {createAsyncThunk, createSlice} from '@reduxjs/toolkit'
import {reportsApi} from "../../api/reportsApi";
import {deliveriesApi} from "../../api/deliveriesApi";

const initialState = {
    historyForDelivery: [],
    loading: true,
    error: ''
};

export const fetchHistoryForDelivery = createAsyncThunk('history/fetchHistoryForDelivery',
    (id) => deliveriesApi.findHistory(id).then(response => response.data)
)

const historySlice = createSlice({
    name: 'history',
    initialState,
    extraReducers: builder => {
        // load deliveries
        builder.addCase(fetchHistoryForDelivery.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchHistoryForDelivery.fulfilled, (state, action) => {
            state.loading = false
            state.historyForDelivery = action.payload
            state.error = ''
        })
        builder.addCase(fetchHistoryForDelivery.rejected, (state, action) => {
            state.loading = false
            state.historyForDelivery = []
            state.error = action.error.message
        })
    }
})

export default historySlice.reducer