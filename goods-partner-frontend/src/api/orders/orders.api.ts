import {createApi} from '@reduxjs/toolkit/query/react'
import Order from '../../model/Order'
import {baseQueryWithReauth} from "../api";

type OrdersResponse = Order[]

interface RescheduleRequest {
	rescheduleDate: string
	orderIds: Array<number>
}

export const ordersApi = createApi({
	reducerPath: 'ordersApi',
	tagTypes: ['orders'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getCompleted: builder.query<OrdersResponse, void>({
			query: () => ({
				url: `/orders/completed`
			}),
			keepUnusedDataFor: 1,
			providesTags: [{ type: 'orders', id: 'completed' }]
		}),

		getSkipped: builder.query<OrdersResponse, void>({
			query: () => ({
				url: `/orders/skipped`
			}),
			keepUnusedDataFor: 1,
			providesTags: [{ type: 'orders', id: 'skipped' }]
		}),

		getScheduled: builder.query<OrdersResponse, void>({
			query: () => ({
				url: `/orders/scheduled`,
			}),
			keepUnusedDataFor: 1,
			providesTags: [{ type: 'orders', id: 'scheduled' }]
		}),

		rescheduleOrders: builder.mutation<Order, RescheduleRequest>({
			query: rescheduleRequest => ({
				url: `orders/skipped/reschedule`,
				method: 'POST',
				body: rescheduleRequest
			}),
			invalidatesTags: [{ type: 'orders', id: 'skipped' },{ type: 'orders', id: 'scheduled' }]
		}),

		deleteOrders: builder.mutation<void, Array<number>>({
			query: orderIds => ({
				url: `/orders/skipped`,
				method: 'DELETE',
				body: { orderIds }
			}),
			invalidatesTags: [{ type: 'orders', id: 'skipped' },{ type: 'orders', id: 'scheduled' }]
		})
	})
})

export const {
	useDeleteOrdersMutation,
	useRescheduleOrdersMutation,
	useGetCompletedQuery,
	useGetSkippedQuery,
	useGetScheduledQuery
} = ordersApi
