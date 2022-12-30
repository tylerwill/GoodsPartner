import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react'
import {apiUrl} from "../../util/util";
import {DeliveryHistory} from "../../model/DeliveryHistory";

type DeliveryHistoryResponse = DeliveryHistory[];


export const historyApi = createApi({
    reducerPath: 'historyApi',
    tagTypes: ['history'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl,
        credentials: "include",
    }),
    endpoints: (builder) => ({
        getHistoryForDelivery: builder.query<DeliveryHistoryResponse, string>({
            query: (deliveryId) => ({
                url: `histories`,
                params: {
                    deliveryId
                }
            }),
        }),
    }),
})

export const {useGetHistoryForDeliveryQuery} = historyApi