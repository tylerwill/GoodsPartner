import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { apiUrl } from '../../util/util'
import { ClientAddress } from '../../model/ClientAddress'

type ClientsAddressesResponse = ClientAddress[]

export const clientsApi = createApi({
    reducerPath: 'clientsApi',
    tagTypes: ['clients'],
    baseQuery: fetchBaseQuery({
        baseUrl: apiUrl,
        credentials: 'include'
    }),
    endpoints: builder => ({
        getClientsAddresses: builder.query<ClientsAddressesResponse, void>({
            query: () => `addresses`,
            providesTags: [{ type: 'clients', id: 'list' }]
        })
    })
})

export const {
    useGetClientsAddressesQuery
} = clientsApi
