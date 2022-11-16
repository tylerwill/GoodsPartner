import {createAsyncThunk, createSlice, PayloadAction} from '@reduxjs/toolkit'
import {ordersApi} from "../../api/ordersApi";
import Order from '../../model/Order';

const initialState = {
    loading: true,
    error: '',
    tabIndex: 0,
    completedOrders: [] as Array<Order>,
    skippedOrders: [] as Array<Order>,
    selectedOrderIds: [] as Array<number>,
    allSelected: false,
    rescheduleDialogOpen: false,
    deleteOrdersDialogOpen: false
};

interface RescheduleData {
    rescheduleDate: string,
    orderIds: Array<number>
}

export const fetchCompletedOrders = createAsyncThunk('orders/fetchCompleted',
    () => {
        return ordersApi.fetchCompleted()
            .then(response => response.data);
    })

export const fetchSkippedOrders = createAsyncThunk('orders/fetchSkipped',
    () => {
        return ordersApi.fetchSkipped()
            .then(response => response.data);
    })

export const rescheduleSkippedOrders = createAsyncThunk('orders/reschedule',
    ({rescheduleDate, orderIds}: RescheduleData) => {
        return ordersApi.reschedule(rescheduleDate, orderIds)
            .then(() => ordersApi.fetchSkipped()).then(response => response.data);
    })

export const deleteSkippedOrders = createAsyncThunk('orders/delete',
    (orderIds: Array<number>) => {
        return ordersApi.delete(orderIds)
            .then(() => ordersApi.fetchSkipped()).then(response => response.data);;
    })


const ordersSlice = createSlice({
    name: 'orders',
    initialState,
    reducers: {
        setTabIndex: (state, action) => {
            state.tabIndex = action.payload;
        },

        selectOrder: (state, action: PayloadAction<number>) => {
            state.selectedOrderIds.push(action.payload);
            state.allSelected = state.skippedOrders.length === state.selectedOrderIds.length;
        },

        deselectOrder: (state, action: PayloadAction<number>) => {
            const selectedIndex = state.selectedOrderIds.indexOf(action.payload);
            state.selectedOrderIds.splice(selectedIndex, 1);
            state.allSelected = false;
        },

        selectAll(state) {
            state.selectedOrderIds = state.skippedOrders.map(order => order.id);
            state.allSelected = true;
        },

        deselectAll(state) {
            state.selectedOrderIds = [];
            state.allSelected = false;
        },

        setRescheduleDialogOpen(state, payload) {
            state.rescheduleDialogOpen = payload.payload;
        },
        setDeleteOrdersDialogOpen(state, payload) {
            state.deleteOrdersDialogOpen = payload.payload;
        }
    },
    extraReducers: builder => {
        // load completedorders
        builder.addCase(fetchCompletedOrders.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchCompletedOrders.fulfilled, (state, action) => {
            state.loading = false
            state.completedOrders = action.payload
            state.error = ''
        })

        // load fetchSkippedOrders
        builder.addCase(fetchSkippedOrders.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchSkippedOrders.fulfilled, (state, action) => {
            state.loading = false
            state.skippedOrders = action.payload
            state.error = ''
        })

        builder.addCase(deleteSkippedOrders.fulfilled, (state, action) => {
            state.loading = false
            state.skippedOrders = action.payload
            state.error = ''
        })

        builder.addCase(rescheduleSkippedOrders.fulfilled, (state, action) => {
            state.loading = false
            state.skippedOrders = action.payload
            state.error = ''
        })
    }
})

export default ordersSlice.reducer
export const {
    setTabIndex,
    selectAll,
    deselectAll,
    selectOrder,
    deselectOrder,
    setRescheduleDialogOpen,
    setDeleteOrdersDialogOpen
} = ordersSlice.actions