import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl} from "../../util/util";
import {Route} from "../../model/Route";

type RoutesResponse = Route[];


export const routesApi = createApi({
    reducerPath: 'routesApi',
    tagTypes: ['routes'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl(),
        credentials: "include",
    }),
    endpoints: (builder) => ({
        getRoutesForDelivery: builder.query<RoutesResponse, string>({
            query: (deliveryId) => ({
                url: `routes`,
                params: {
                    deliveryId
                }
            }),
            providesTags: [{type: 'routes', id: 'forDelivery'}],
        }),


        // getDelivery: builder.query<Delivery, string>({
        //     query: (id: string) => `deliveries/${id}`,
        // }),
    }),
})

export const {useGetRoutesForDeliveryQuery} = routesApi