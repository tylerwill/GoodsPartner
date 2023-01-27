import {createSlice} from '@reduxjs/toolkit'
import Delivery from '../../model/Delivery'

const initialState = {
    orderTabIndex: 0,
    currentRouteIndex: 0,
    deleteDeliveryDialogOpen: false,
    deliveryToDelete: {} as Delivery
}

export const currentDeliverySlice = createSlice({
    name: 'currentDelivery',
    initialState,
    reducers: {
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
export const {setOrderTabIndex, setCurrentRouteIndex, setDeleteDeliveryDialogOpen, setDeliveryToDelete} =
    currentDeliverySlice.actions
