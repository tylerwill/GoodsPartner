import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl} from "../../util/util";
import {Route} from "../../model/Route";
import {RoutePoint} from "../../model/RoutePoint";

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

        startRoute: builder.mutation<Route, number>({
            query: (id: number) => ({
                url: `routes/${id}/start`,
                method: 'POST'
            }),
            invalidatesTags: [{type: 'routes', id: 'forDelivery'}]
        }),

        completeRoute: builder.mutation<Route, number>({
            query: (id: number) => ({
                url: `routes/${id}/complete`,
                method: 'POST'
            }),
            invalidatesTags: [{type: 'routes', id: 'forDelivery'}]
        }),

        resetRoutePoint: builder.mutation<RoutePoint, number>({
            query: (id: number) => ({
                url: `route-points/${id}/reset`,
                method: 'POST'
            }),
            invalidatesTags: [{type: 'routes', id: 'forDelivery'}]
        }),

        skipRoutePoint: builder.mutation<RoutePoint, number>({
            query: (id: number) => ({
                url: `route-points/${id}/skip`,
                method: 'POST'
            }),
            invalidatesTags: [{type: 'routes', id: 'forDelivery'}]
        }),

        completeRoutePoint: builder.mutation<RoutePoint, number>({
            query: (id: number) => ({
                url: `route-points/${id}/complete`,
                method: 'POST'
            }),
            invalidatesTags: [{type: 'routes', id: 'forDelivery'}]
        }),
    }),
})

export const {
    useGetRoutesForDeliveryQuery, useStartRouteMutation, useCompleteRouteMutation,
    useResetRoutePointMutation, useCompleteRoutePointMutation, useSkipRoutePointMutation
} = routesApi