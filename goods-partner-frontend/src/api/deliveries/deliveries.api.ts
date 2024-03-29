import {createApi} from '@reduxjs/toolkit/query/react'
import Delivery from '../../model/Delivery'
import {baseQueryWithReauth} from "../api";

type DeliveriesResponse = Delivery[]

export const deliveriesApi = createApi({
	reducerPath: 'deliveriesApi',
	tagTypes: ['deliveries'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getDeliveries: builder.query<DeliveriesResponse, void>({
			query: () => `deliveries`,
			providesTags: [{ type: 'deliveries', id: 'list' }]
		}),

		addDelivery: builder.mutation<Delivery, Partial<Delivery>>({
			query: newDelivery => ({
				url: `deliveries`,
				method: 'POST',
				body: newDelivery
			}),
			invalidatesTags: [{ type: 'deliveries', id: 'list' }]
		}),

		getDelivery: builder.query<Delivery, string>({
			query: (id: string) => `deliveries/${id}`,
			providesTags: (result, error, id) => [{ type: 'deliveries', id }]
		}),

		calculateDelivery: builder.mutation<Delivery, string>({
			query: (id: string) => ({
				url: `deliveries/${id}/calculate`,
				method: 'POST'
			}),
			invalidatesTags: (result, error, id) => [{ type: 'deliveries', id }]
		}),

		approveDelivery: builder.mutation<Delivery, string>({
			query: (id: string) => ({
				url: `deliveries/${id}/approve`,
				method: 'POST'
			}),
			invalidatesTags: (result, error, id) => [{ type: 'deliveries', id }]
		}),

		deleteDelivery: builder.mutation<Delivery, string>({
			query: id => ({
				url: `deliveries/${id}`,
				method: 'DELETE'
			}),
			invalidatesTags: [{ type: 'deliveries', id: 'list' }]
		}),

		resyncDelivery: builder.mutation<Delivery, string>({
			query: id => ({
				url: `deliveries/${id}/sync`,
				method: 'POST'
			}),
			invalidatesTags: [{ type: 'deliveries', id: 'list' }]
		}),
	})
})

export const {
	useGetDeliveriesQuery,
	useAddDeliveryMutation,
	useGetDeliveryQuery,
	useCalculateDeliveryMutation,
	useApproveDeliveryMutation,
	useDeleteDeliveryMutation,
	useResyncDeliveryMutation
} = deliveriesApi
