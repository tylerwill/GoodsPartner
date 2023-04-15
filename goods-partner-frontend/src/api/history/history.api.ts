import {createApi} from '@reduxjs/toolkit/query/react'
import {DeliveryHistory} from '../../model/DeliveryHistory'
import {baseQueryWithReauth} from "../api";

type DeliveryHistoryResponse = DeliveryHistory[]

export const historyApi = createApi({
	reducerPath: 'historyApi',
	tagTypes: ['history'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getHistoryForDelivery: builder.query<DeliveryHistoryResponse, string>({
			query: deliveryId => ({
				url: `histories`,
				params: {
					deliveryId
				}
			}),
			keepUnusedDataFor: 15,
		})
	})
})

export const { useGetHistoryForDeliveryQuery } = historyApi
