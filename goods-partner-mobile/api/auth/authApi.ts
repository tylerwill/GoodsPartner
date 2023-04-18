import {apiSlice, baseQueryWithReauth} from "../api";
import {createApi} from "@reduxjs/toolkit/dist/query/react";

export const authApi = createApi({
    reducerPath: 'authApi',
    tagTypes: ['auth'],
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        login: builder.mutation({
            query: credentials => ({
                url: '/auth/login',
                method: 'POST',
                body: { ...credentials }
            })
        }),
    })
})

export const {
    useLoginMutation
} = authApi