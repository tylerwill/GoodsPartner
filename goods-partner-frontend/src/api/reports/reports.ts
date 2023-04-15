import {createApi} from '@reduxjs/toolkit/query/react'
import {baseQueryWithReauth} from "../api";

interface ReportRequest {
    dateFrom: string,
    dateTo: string
}

export const reportsApi = createApi({
    reducerPath: 'reportsApi',
    tagTypes: ['reports'],
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getDeliveriesStatistics: builder.query<ReportRequest, any>({
            query: ({dateFrom, dateTo}) => ({
                url: `/statistics/deliveries`,
                params: {
                    dateFrom, dateTo
                }
            }),
            providesTags: [{type: 'reports', id: 'list'}]
        }),
    })
})

export const {
    useLazyGetDeliveriesStatisticsQuery
} = reportsApi
