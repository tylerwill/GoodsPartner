import type { TypedUseSelectorHook } from 'react-redux'
import { useDispatch, useSelector } from 'react-redux'
import { AppDispatch, RootState } from '../redux/store'


import {currentDeliverySlice} from '../features/currentDelivery/currentDeliverySlice'

import {bindActionCreators} from '@reduxjs/toolkit'
import {ordersSlice} from '../features/orders/ordersSlice'
import {authSlice} from "../features/auth/authSlice";


// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

const rootActions =  {
    ...currentDeliverySlice.actions,
    ...ordersSlice.actions,
    ...authSlice.actions
}

export const useActions = () => {
    const dispatch = useAppDispatch();

    return bindActionCreators(rootActions, dispatch)
}