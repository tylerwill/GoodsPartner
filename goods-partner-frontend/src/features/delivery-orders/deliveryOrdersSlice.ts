import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import Order from '../../model/Order';

const initialState = {
    isOrderAddressDialogOpen: false,
    isExcludeOrderDialogOpen: false,
    orderForAddressModification: {} as Order,
    orderToExclude: {} as Order
};


const deliveryOrdersSlice = createSlice({
    name: 'delivery-orders',
    initialState,
    reducers: {
        setAddressDialogOpen: (state, action: PayloadAction<boolean>) => {
            state.isOrderAddressDialogOpen = action.payload;
        },
        setOrderForAddressModification: (state, action: PayloadAction<Order>) => {
            state.orderForAddressModification = action.payload;
        },
        setExcludeDialogOpen: (state, action: PayloadAction<boolean>) => {
            state.isExcludeOrderDialogOpen = action.payload;
        },
        setOrderToExclude: (state, action: PayloadAction<Order>) => {
            state.orderToExclude = action.payload;
        },

    }
})

export default deliveryOrdersSlice.reducer
export const {
    setAddressDialogOpen,
    setOrderForAddressModification,
    setExcludeDialogOpen,
    setOrderToExclude
} = deliveryOrdersSlice.actions