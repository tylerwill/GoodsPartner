import {createApi} from '@reduxjs/toolkit/query/react'
import {Settings} from "../../model/Settings";
import {baseQueryWithReauth} from "../api";


export const settingsApi = createApi({
    reducerPath: 'settingsApi',
    tagTypes: ['settings'],
    baseQuery: baseQueryWithReauth,
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
