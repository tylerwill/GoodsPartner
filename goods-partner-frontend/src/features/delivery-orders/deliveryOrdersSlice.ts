import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import Order from '../../model/Order';

const initialState = {
    isOrderAddressDialogOpen: false,
    orderForAddressModification: {} as Order
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


    }
})

export default deliveryOrdersSlice.reducer
export const {
    setAddressDialogOpen,
    setOrderForAddressModification,
} = deliveryOrdersSlice.actions