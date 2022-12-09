import {createSlice} from '@reduxjs/toolkit'

const initialState = {
    tabIndex: 0,
    orderTabIndex: 0,
    currentRouteIndex: 0
};

const currentDeliverySlice = createSlice({
    name: 'currentDelivery',
    initialState,
    reducers: {

        setTabIndex: (state, action) => {
            state.tabIndex = action.payload;
        },
        setOrderTabIndex: (state, action) => {
            state.orderTabIndex = action.payload;
        },
        setCurrentRouteIndex: (state, action) => {
            state.currentRouteIndex = action.payload;
        }
    }
})

export default currentDeliverySlice.reducer
export const {
    setTabIndex,
    setOrderTabIndex,
    setCurrentRouteIndex
} = currentDeliverySlice.actions