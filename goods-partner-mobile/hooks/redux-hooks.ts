import type { TypedUseSelectorHook } from 'react-redux'
import { useDispatch, useSelector } from 'react-redux'
import { AppDispatch, RootState } from '../redux/store'

import {bindActionCreators} from '@reduxjs/toolkit'
import {authSlice} from "../features/auth/authSlice";


// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

const rootActions =  {
    ...authSlice.actions
}

export const useActions = () => {
    const dispatch = useAppDispatch();

    return bindActionCreators(rootActions, dispatch)
}