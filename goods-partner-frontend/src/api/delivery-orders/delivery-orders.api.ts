import {createApi} from '@reduxjs/toolkit/query/react'
import Order from '../../model/Order'
import {baseQueryWithReauth} from "../api";

type OrdersResponse = Order[]

interface ExcludeRequest {
	excludeReason: string
	orderId: number
}

// TODO: Cache should contain delivery Id
export const deliveryOrdersApi = createApi({
	reducerPath: 'delivery-ordersApi',
	tagTypes: ['delivery-orders'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getOrdersForDelivery: builder.query<OrdersResponse, string>({
			query: deliveryId => ({
				url: `orders`,
				params: {
					deliveryId
				}
			}),
			providesTags: [{ type: 'delivery-orders', id: 'forDelivery' }]
		}),

		updateOrder: builder.mutation<Order, Order>({
			query: orderToUpdate => ({
				url: `orders/${orderToUpdate.id}`,
				method: 'PUT',
				body: orderToUpdate
			}),
			invalidatesTags: [{ type: 'delivery-orders', id: 'forDelivery' }]
		}),

		excludeOrder: builder.mutation<void, ExcludeRequest>({
			query: request => ({
				url: `/orders/${request.orderId}/exclude`,
				method: 'POST',
				body: { excludeReason: request.excludeReason }
			}),
			invalidatesTags: [{ type: 'delivery-orders', id: 'forDelivery' }]
		})
	})
})

export const {
	useGetOrdersForDeliveryQuery,
	useUpdateOrderMutation,
	useExcludeOrderMutation
} = deliveryOrdersApi
