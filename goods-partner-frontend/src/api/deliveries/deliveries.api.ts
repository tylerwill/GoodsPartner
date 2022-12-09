import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import Delivery from '../../model/Delivery'
import {apiUrl} from "../../util/util";

type DeliveriesResponse = Delivery[];


export const deliveriesApi = createApi({
        reducerPath: 'deliveriesApi',
        tagTypes: ['deliveries'],
        baseQuery: fetchBaseQuery({
            baseUrl: apiUrl(),
            credentials: "include",
        }),
        endpoints: (builder) => ({
            getDeliveries: builder.query<DeliveriesResponse, void>({
                query: () => `deliveries`,
                providesTags: [{type: 'deliveries', id: 'list'}],
            }),

            addDelivery: builder.mutation<Delivery, Partial<Delivery>>({
                query: (newDelivery) => ({
                    url: `deliveries`,
                    method: 'POST',
                    body: newDelivery,

                }),
                invalidatesTags: [{type: 'deliveries', id: 'list'}]
            }),

            getDelivery: builder.query<Delivery, string>({
                query: (id: string) => `deliveries/${id}`,
                providesTags: (result, error, id) => [{type: 'deliveries', id}],
            }),

            calculateDelivery: builder.mutation<Delivery, string>({
                query: (id: string) => ({
                    url: `deliveries/${id}/calculate`,
                    method: 'POST'
                }),
                invalidatesTags: (result, error, id) => [{type: 'deliveries', id}]
            }),

            approveDelivery: builder.mutation<Delivery, string>({
                query: (id: string) => ({
                    url: `deliveries/${id}/approve`,
                    method: 'POST'
                }),
                invalidatesTags: (result, error, id) => [{type: 'deliveries', id}]
            }),
        }),
    }
)

export const {
    useGetDeliveriesQuery,
    useAddDeliveryMutation,
    useGetDeliveryQuery,
    useCalculateDeliveryMutation,
    useApproveDeliveryMutation
} = deliveriesApi