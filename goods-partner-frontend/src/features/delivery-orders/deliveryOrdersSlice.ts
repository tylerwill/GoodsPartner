import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import Order from '../../model/Order';

const initialState = {
    isOrderAddressDialogOpen: false,
    isExcludeOrderDialogOpen: false,
    isDeliveryTypeDialogOpen: false,
    orderForModification: {} as Order
};


const deliveryOrdersSlice = createSlice({
    name: 'delivery-orders',
    initialState,
    reducers: {
        setAddressDialogOpen: (state, action: PayloadAction<boolean>) => {
            state.isOrderAddressDialogOpen = action.payload;
        },
        setOrderForModification: (state, action: PayloadAction<Order>) => {
            state.orderForModification = action.payload;
        },
        setExcludeDialogOpen: (state, action: PayloadAction<boolean>) => {
            state.isExcludeOrderDialogOpen = action.payload;
        },
        setDeliveryTypeDialogOpen: (state, action: PayloadAction<boolean>) => {
            state.isDeliveryTypeDialogOpen = action.payload;
        },

    }
})

export default deliveryOrdersSlice.reducer
export const {
    setAddressDialogOpen,
    setOrderForModification,
    setExcludeDialogOpen,
    setDeliveryTypeDialogOpen,
} = deliveryOrdersSlice.actions