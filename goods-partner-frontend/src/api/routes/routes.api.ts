import {createApi} from '@reduxjs/toolkit/query/react'
import {Route} from '../../model/Route'
import {RoutePoint} from '../../model/RoutePoint'
import Order from '../../model/Order'
import {baseQueryWithReauth} from "../api";

type RoutesResponse = Route[]

export const routesApi = createApi({
	reducerPath: 'routesApi',
	tagTypes: ['routes'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getRoutesForDelivery: builder.query<RoutesResponse, string>({
			query: deliveryId => ({
				url: `routes`,
				params: {
					deliveryId
				}
			}),
			providesTags: [{ type: 'routes', id: 'forDelivery' }]
		}),

		startRoute: builder.mutation<Route, number>({
			query: (id: number) => ({
				url: `routes/${id}/start`,
				method: 'POST'
			}),
			invalidatesTags: [{ type: 'routes', id: 'forDelivery' }]
		}),

		completeRoute: builder.mutation<Route, number>({
			query: (id: number) => ({
				url: `routes/${id}/complete`,
				method: 'POST'
			}),
			invalidatesTags: [{ type: 'routes', id: 'forDelivery' }]
		}),

		resetRoutePoint: builder.mutation<RoutePoint, number>({
			query: (id: number) => ({
				url: `route-points/${id}/reset`,
				method: 'POST'
			}),
			invalidatesTags: [{ type: 'routes', id: 'forDelivery' }]
		}),

		skipRoutePoint: builder.mutation<RoutePoint, number>({
			query: (id: number) => ({
				url: `route-points/${id}/skip`,
				method: 'POST'
			}),
			invalidatesTags: [{ type: 'routes', id: 'forDelivery' }]
		}),

		completeRoutePoint: builder.mutation<RoutePoint, number>({
			query: (id: number) => ({
				url: `route-points/${id}/complete`,
				method: 'POST'
			}),
			invalidatesTags: [{ type: 'routes', id: 'forDelivery' }]
		}),

		getRoutePointOrders: builder.query<Array<Order>, number>({
			query: (id: number) => ({
				url: `route-points/${id}/orders`,
				method: 'GET'
			})
		})
	})
})

export const {
	useGetRoutesForDeliveryQuery,
	useStartRouteMutation,
	useCompleteRouteMutation,
	useResetRoutePointMutation,
	useCompleteRoutePointMutation,
	useSkipRoutePointMutation,
	useGetRoutePointOrdersQuery
} = routesApi
