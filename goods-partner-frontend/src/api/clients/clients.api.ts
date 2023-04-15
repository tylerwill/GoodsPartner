import {createApi} from '@reduxjs/toolkit/query/react'
import {ClientAddress} from '../../model/ClientAddress'
import {baseQueryWithReauth} from "../api";

type ClientsAddressesResponse = ClientAddress[]

export const clientsApi = createApi({
    reducerPath: 'clientsApi',
    tagTypes: ['clients'],
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getClientsAddresses: builder.query<ClientsAddressesResponse, void>({
            query: () => `addresses`,
            providesTags: [{type: 'clients', id: 'list'}]
        }),
        updateClientAddress: builder.mutation<ClientAddress, ClientAddress>({
            query: clientsAddress => ({
                url: `addresses`,
                method: 'PUT',
                body: clientsAddress
            }),
            invalidatesTags: [{type: 'clients', id: 'list'}]
        }),
    })
})

export const {
    useGetClientsAddressesQuery,
    useUpdateClientAddressMutation
} = clientsApi
