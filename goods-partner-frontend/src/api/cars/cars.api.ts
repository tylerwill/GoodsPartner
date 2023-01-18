import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { apiUrl } from '../../util/util'
import { Car } from '../../model/Car'

type CarsResponse = Car[]

export const carsApi = createApi({
	reducerPath: 'carsApi',
	tagTypes: ['cars'],
	baseQuery: fetchBaseQuery({
		baseUrl: apiUrl,
		credentials: 'include'
	}),
	endpoints: builder => ({
		getCars: builder.query<CarsResponse, void>({
			query: () => `cars`,
			providesTags: [{ type: 'cars', id: 'list' }]
		}),

		addCar: builder.mutation<Car, Partial<Car>>({
			query: newCar => ({
				url: `cars`,
				method: 'POST',
				body: newCar
			}),
			invalidatesTags: [{ type: 'cars', id: 'list' }]
		}),

		updateCar: builder.mutation<Car, Car>({
			query: newCar => ({
				url: `cars/${newCar.id}`,
				method: 'PUT',
				body: newCar
			}),
			invalidatesTags: [{ type: 'cars', id: 'list' }]
		}),

		deleteCar: builder.mutation<void, number>({
			query: carId => ({
				url: `cars/${carId}`,
				method: 'DELETE'
			}),
			invalidatesTags: [{ type: 'cars', id: 'list' }]
		})
	})
})

export const {
	useGetCarsQuery,
	useAddCarMutation,
	useUpdateCarMutation,
	useDeleteCarMutation
} = carsApi
