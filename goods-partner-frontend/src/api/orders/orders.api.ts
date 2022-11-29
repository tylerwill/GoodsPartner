import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl} from "../../util/util";
import Order from "../../model/Order";

type OrdersResponse = Order[];


export const ordersApi = createApi({
    reducerPath: 'ordersApi',
    tagTypes: ['orders'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl(),
        credentials: "include",
    }),
    endpoints: (builder) => ({
        getOrdersForDelivery: builder.query<OrdersResponse, string>({
            query: (deliveryId) => ({
                url: `orders`,
                params: {
                    deliveryId
                }
            }),
            providesTags: [{type: 'orders', id: 'forDelivery'}],
        }),


        // getDelivery: builder.query<Delivery, string>({
        //     query: (id: string) => `deliveries/${id}`,
        // }),
    }),
})

export const {useGetOrdersForDeliveryQuery} = ordersApi