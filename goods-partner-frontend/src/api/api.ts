import axios from 'axios'
import {BaseQueryFn, createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl, currentHost} from '../util/util'

import {logOut, setUserInfo} from '../features/auth/authSlice'
import {RootState} from "../redux/store";

const host = currentHost()

const defaultOptions = {
    baseURL: `http://16.16.91.239:80/api/1`,
    withCredentials: true
}

export const axiosWithSetting = axios.create(defaultOptions)

const baseQuery = fetchBaseQuery({
    baseUrl: apiUrl,
    credentials: 'include',
    prepareHeaders: (headers, {getState}) => {
        const state = getState() as RootState;
        const token = state.auth.token
        if (token) {
            headers.set("authorization", `Bearer ${token}`)
        }
        return headers
    }
})

export const baseQueryWithReauth: BaseQueryFn = async (args,
                                                api,
                                                extraOptions) => {
    let result = await baseQuery(args, api, extraOptions)

    if (result?.error?.status === 403) {
        console.log('sending refresh token')
        // send refresh token to get new access token
        const refreshResult = await baseQuery('/refresh', api, extraOptions)
        console.log("refreshResult", refreshResult)
        const state = api.getState() as RootState;
        // if (refreshResult?.data) {
        //     const user = state.auth.user
        //     // store the new token
        //     const refreshData = refreshResult.data as object;
        //     api.dispatch(setUserInfo({...refreshData, user}))
        //     // retry the original query with new access token
        //     result = await baseQuery(args, api, extraOptions)
        // } else {
        //     console.log("here");
        //     api.dispatch(logOut())
        // }
    }

    return result
}

export const apiSlice = createApi({
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({})
})