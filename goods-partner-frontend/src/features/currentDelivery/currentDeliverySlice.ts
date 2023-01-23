import {createSlice} from '@reduxjs/toolkit'
import Delivery from '../../model/Delivery'

const initialState = {
    tabIndex: 0,
    orderTabIndex: 0,
    currentRouteIndex: 0,
    deleteDeliveryDialogOpen: false,
    deliveryToDelete: {} as Delivery
}

export const currentDeliverySlice = createSlice({
    name: 'currentDelivery',
    initialState,
    reducers: {
        setTabIndex: (state, action) => {
            state.tabIndex = action.payload
        },
        setOrderTabIndex: (state, action) => {
            state.orderTabIndex = action.payload
        },
        setCurrentRouteIndex: (state, action) => {
            state.currentRouteIndex = action.payload
        },
        setDeleteDeliveryDialogOpen(state, action) {
            state.deleteDeliveryDialogOpen = action.payload;
        },
        setDeliveryToDelete(state, action) {
            state.deliveryToDelete = action.payload;
        }
    }
})

export default currentDeliverySlice.reducer
export const {setTabIndex, setOrderTabIndex, setCurrentRouteIndex, setDeleteDeliveryDialogOpen, setDeliveryToDelete} =
    currentDeliverySlice.actions
