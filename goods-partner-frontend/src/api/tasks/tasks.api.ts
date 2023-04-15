import {createApi} from '@reduxjs/toolkit/query/react'
import {Task} from '../../model/Task'
import {baseQueryWithReauth} from "../api";

type TasksResponse = Task[]

export const tasksApi = createApi({
	reducerPath: 'tasksApi',
	tagTypes: ['tasks'],
	baseQuery: baseQueryWithReauth,
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
