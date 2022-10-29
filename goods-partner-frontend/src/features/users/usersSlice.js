import {createAsyncThunk, createSlice} from '@reduxjs/toolkit'
import {usersApi} from "../../api/usersApi";

const initialState = {
    users: [],
    loading: false,
    error: ''
};

export const fetchUsers = createAsyncThunk('users/fetch',
    () => usersApi.findAll().then(response => response.data))

export const addUser = createAsyncThunk('users/add',
    (user) => usersApi.add(user).then(response => response.data))

export const updateUser = createAsyncThunk('users/update',
    (user) => usersApi.update(user).then(response => response.data))

export const deleteUser = createAsyncThunk('users/delete',
    (id) => usersApi.delete(id).then(() => id))


const usersSlice = createSlice({
    name: 'users',
    initialState,
    extraReducers: builder => {
        // load users
        builder.addCase(fetchUsers.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchUsers.fulfilled, (state, action) => {
            state.loading = false
            state.users = action.payload
            state.error = ''
        })
        builder.addCase(fetchUsers.rejected, (state, action) => {
            state.loading = false
            state.users = []
            state.error = action.error.message
        })

        // add user
        builder.addCase(addUser.fulfilled, (state, action) => {
            state.users.push(action.payload)
            state.error = ''
        })
        builder.addCase(addUser.rejected, (state, action) => {
            state.error = action.error.message
        })

        // update user
        builder.addCase(updateUser.fulfilled, (state, action) => {
            const indexToReplace = state.users.findIndex(user => user.id === action.payload.id);
            state.users[indexToReplace] = action.payload
            state.error = ''
        })
        builder.addCase(updateUser.rejected, (state, action) => {
            state.error = action.error.message
        })

        // delete user
        builder.addCase(deleteUser.fulfilled, (state, action) => {
            state.users = state.users.filter((user) => user.id !== action.payload)
            state.error = ''
        })
        builder.addCase(deleteUser.rejected, (state, action) => {
            state.error = action.error.message
        })
    }
})

export default usersSlice.reducer