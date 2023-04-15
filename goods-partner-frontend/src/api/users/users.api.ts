import {createApi} from '@reduxjs/toolkit/query/react'
import {User} from '../../model/User'
import {baseQueryWithReauth} from "../api";

type UsersResponse = User[]

export const usersApi = createApi({
	reducerPath: 'usersApi',
	tagTypes: ['users'],
	baseQuery: baseQueryWithReauth,
	endpoints: builder => ({
		getUsers: builder.query<UsersResponse, void>({
			query: () => `users`,
			providesTags: [{ type: 'users', id: 'list' }]
		}),

		addUser: builder.mutation<User, Partial<User>>({
			query: newUser => ({
				url: `users`,
				method: 'POST',
				body: newUser
			}),
			invalidatesTags: [{ type: 'users', id: 'list' }]
		}),

		updateUser: builder.mutation<User, User>({
			query: newUser => ({
				url: `users/${newUser.id}`,
				method: 'PUT',
				body: newUser
			}),
			invalidatesTags: [{ type: 'users', id: 'list' }]
		}),

		deleteUser: builder.mutation<void, number>({
			query: userId => ({
				url: `users/${userId}`,
				method: 'DELETE'
			}),
			invalidatesTags: [{ type: 'users', id: 'list' }]
		}),

		getCurrentUser: builder.query<User, void>({
			query: () => `/users/auth`
		})
	})
})

export const {
	useGetUsersQuery,
	useGetCurrentUserQuery,
	useAddUserMutation,
	useUpdateUserMutation,
	useDeleteUserMutation
} = usersApi
