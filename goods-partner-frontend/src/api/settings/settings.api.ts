import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { apiUrl } from '../../util/util'
import {Settings} from "../../model/Settings";


export const settingsApi = createApi({
    reducerPath: 'settingsApi',
    tagTypes: ['settings'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl,
        credentials: 'include'
    }),
    endpoints: builder => ({
        getSettings: builder.query<Settings, void>({
            query: () => `settings`,
            providesTags: [{ type: 'settings', id: 'list' }]
        }),
        updateSettings: builder.mutation<Settings, Settings>({
            query: settings => ({
                url: `settings`,
                method: 'PUT',
                body: settings
            }),
            invalidatesTags: [{ type: 'settings', id: 'list' }]
        }),
    })
})

export const {
    useGetSettingsQuery,
    useUpdateSettingsMutation
} = settingsApi
