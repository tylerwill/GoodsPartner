import {createAsyncThunk, createSlice} from '@reduxjs/toolkit'
import {deliveriesApi} from "../../api/deliveriesApi";

const initialState = {
    deliveries: [],
    loading: false,
    error: ''
};

export const fetchDeliveries = createAsyncThunk('deliveries/fetch',
    () => deliveriesApi.findAll().then(response => response.data))

export const createDelivery = createAsyncThunk('deliveries/create',
    (date) => {
        // TODO: Remove status
        const newDelivery = {deliveryDate: date, status: 'DRAFT'};
        return deliveriesApi.create(newDelivery).then(response => response.data);
    })


const deliveriesSlice = createSlice({
    name: 'deliveries',
    initialState,
    extraReducers: builder => {
        // load deliveries
        builder.addCase(fetchDeliveries.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchDeliveries.fulfilled, (state, action) => {
            state.loading = false
            state.deliveries = action.payload
            state.error = ''
        })
        builder.addCase(fetchDeliveries.rejected, (state, action) => {
            state.loading = false
            state.deliveries = []
            console.log('action', action);
            state.error = action.error.message
        })

        // create delivery
        builder.addCase(createDelivery.fulfilled, (state, action) => {
            state.deliveries.push(action.payload)
            state.error = ''
        })
        builder.addCase(createDelivery.rejected, (state, action) => {
            state.error = action.error.message
        })


    }
})

export default deliveriesSlice.reducer