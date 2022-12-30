import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl} from "../../util/util";
import {DeliveryShipping} from "../../model/DeliveryShipping";

type DeliveryShippingResponse = DeliveryShipping[];


export const shippingApi = createApi({
    reducerPath: 'shippingApi',
    tagTypes: ['shipping'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl,
        credentials: "include",
    }),
    endpoints: (builder) => ({
        getShippingForDelivery: builder.query<DeliveryShippingResponse, string>({
            query: (deliveryId) => ({
                url: `shipping`,
                params: {
                    deliveryId
                }
            }),
            providesTags: [{type: 'shipping', id: 'forDelivery'}],
        }),

    }),
})

export const {useGetShippingForDeliveryQuery} = shippingApi