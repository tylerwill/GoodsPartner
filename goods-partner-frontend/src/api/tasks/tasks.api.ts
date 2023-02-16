import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { apiUrl } from '../../util/util'
import { Task } from '../../model/Task'

type TasksResponse = Task[]

export const tasksApi = createApi({
	reducerPath: 'tasksApi',
	tagTypes: ['tasks'],
	baseQuery: fetchBaseQuery({
		baseUrl: apiUrl,
		credentials: 'include'
	}),
	endpoints: builder => ({
		getTasks: builder.query<TasksResponse, void>({
			query: () => `tasks`,
			providesTags: [{ type: 'tasks', id: 'list' }]
		}),

		addTask: builder.mutation<Task, Partial<Task>>({
			query: newTask => ({
				url: `tasks`,
				method: 'POST',
				body: newTask
			}),
			invalidatesTags: [{ type: 'tasks', id: 'list' }]
		}),

		updateTask: builder.mutation<Task, Task>({
			query: newTask => ({
				url: `tasks/${newTask.id}`,
				method: 'PUT',
				body: newTask
			}),
			invalidatesTags: [{ type: 'tasks', id: 'list' }]
		}),

		deleteTask: builder.mutation<void, number>({
			query: carId => ({
				url: `tasks/${carId}`,
				method: 'DELETE'
			}),
			invalidatesTags: [{ type: 'tasks', id: 'list' }]
		})
	})
})

export const {
	useGetTasksQuery,
	useAddTaskMutation,
	useUpdateTaskMutation,
	useDeleteTaskMutation
} = tasksApi
