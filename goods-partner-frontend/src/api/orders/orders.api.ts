import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl} from "../../util/util";
import Order from "../../model/Order";

type OrdersResponse = Order[];

interface RescheduleRequest {
    rescheduleDate: string,
    orderIds: Array<number>
}

export const ordersApi = createApi({
    reducerPath: 'ordersApi',
    tagTypes: ['orders'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl(),
        credentials: "include",
    }),
    endpoints: (builder) => ({
        getCompleted: builder.query<OrdersResponse, void>({
            query: () => ({
                url: `/orders/completed`,
            }),
            providesTags: [{type: 'orders', id: 'completed'}],
        }),

        getSkipped: builder.query<OrdersResponse, void>({
            query: () => ({
                url: `/orders/skipped`,
            }),
            providesTags: [{type: 'orders', id: 'skipped'}],
        }),

        rescheduleOrders: builder.mutation<Order, RescheduleRequest>({
            query: (rescheduleRequest) => (
                {
                    url: `orders/skipped/reschedule`,
                    method: 'POST',
                    body: rescheduleRequest,

                }),
            invalidatesTags: [{type: 'orders', id: 'skipped'}]
        }),

        deleteOrders: builder.mutation<void, Array<number>>({
            query: (orderIds) => (
                {
                    url: `/orders/skipped`,
                    method: 'DELETE',
                    body: {orderIds},

                }),
            invalidatesTags: [{type: 'orders', id: 'skipped'}]
        }),


    }),
})

export const {
    useDeleteOrdersMutation,
    useRescheduleOrdersMutation,
    useGetCompletedQuery,
    useGetSkippedQuery
} = ordersApi
