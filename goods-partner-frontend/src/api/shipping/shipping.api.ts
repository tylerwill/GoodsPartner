import {createApi} from '@reduxjs/toolkit/query/react'
import {DeliveryShipping} from '../../model/DeliveryShipping'
import {baseQueryWithReauth} from "../api";

type DeliveryShippingResponse = DeliveryShipping[]

export const shippingApi = createApi({
	reducerPath: 'shippingApi',
	tagTypes: ['shipping'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getShippingForDelivery: builder.query<DeliveryShippingResponse, string>({
			query: deliveryId => ({
				url: `shipping`,
				params: {
					deliveryId
				}
			}),
			providesTags: [{ type: 'shipping', id: 'forDelivery' }]
		})
	})
})

export const { useGetShippingForDeliveryQuery } = shippingApi
