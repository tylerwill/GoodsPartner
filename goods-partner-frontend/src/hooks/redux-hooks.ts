import type { TypedUseSelectorHook } from 'react-redux'
import { useDispatch, useSelector } from 'react-redux'
import { AppDispatch, RootState } from '../redux/store'


import {currentDeliverySlice} from '../features/currentDelivery/currentDeliverySlice'
import {reportsSlice} from '../features/reports/reportsSlice'
import notificationsReducer from '../features/notifications/notificationsSlice'

import {bindActionCreators, configureStore} from '@reduxjs/toolkit'
import {ordersSlice} from '../features/orders/ordersSlice'
import deliveryOrdersReducer from '../features/delivery-orders/deliveryOrdersSlice'


// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

const rootActions =  {
    ...currentDeliverySlice.actions,
    ...reportsSlice.actions,
    ...ordersSlice.actions
}

export const useActions = () => {
    const dispatch = useAppDispatch();

    return bindActionCreators(rootActions, dispatch)
}