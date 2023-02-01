import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import Order from '../../model/Order'

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
}

export const ordersSlice = createSlice({
    name: 'orders',
    initialState,
    reducers: {
        setTabIndex: (state, action) => {
            state.tabIndex = action.payload
        },

        selectOrder: (state, action: PayloadAction<number>) => {
            state.selectedOrderIds.push(action.payload)
            state.allSelected =
                state.skippedOrders.length === state.selectedOrderIds.length
        },

        deselectOrder: (state, action: PayloadAction<number>) => {
            const selectedIndex = state.selectedOrderIds.indexOf(action.payload)
            state.selectedOrderIds.splice(selectedIndex, 1)
            state.allSelected = false
        },

        selectAll(state, action: PayloadAction<Order[]>) {
            const skippedOrders = action.payload;
            state.selectedOrderIds = skippedOrders.map(order => order.id)
            state.allSelected = true
            console.log("skipped orders array", skippedOrders)
            console.log("selected orders ids", state.selectedOrderIds)
        },

        deselectAll(state) {
            state.selectedOrderIds = []
            state.allSelected = false
        },

        setRescheduleDialogOpen(state, payload) {
            state.rescheduleDialogOpen = payload.payload
        },
        setDeleteOrdersDialogOpen(state, payload) {
            state.deleteOrdersDialogOpen = payload.payload
        }
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
