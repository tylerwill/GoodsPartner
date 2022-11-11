import {createAsyncThunk, createSlice, PayloadAction} from '@reduxjs/toolkit'
import {usersApi} from "../../api/usersApi";
import {User} from "../../model/User";

interface UsersState {
    users: Array<User>,
    loading: boolean,
    error: string,
    isAddUserDialogOpen: boolean
}

const initialState: UsersState = {
    users: [],
    loading: false,
    error: '',
    isAddUserDialogOpen: false
};

export const fetchUsers = createAsyncThunk('users/fetch',
    () => usersApi.findAll().then(response => response.data))

export const addUser = createAsyncThunk('users/add',
    (user: User) => usersApi.add(user).then(response => response.data))

export const updateUser = createAsyncThunk('users/update',
    (user: User) => usersApi.update(user).then(response => response.data))

export const deleteUser = createAsyncThunk('users/delete',
    (id: number) => usersApi.delete(id).then(() => id))


const usersSlice = createSlice({
    name: 'users',
    initialState,
    reducers: {
        setAddUserDialogOpen(state, action) {
            state.isAddUserDialogOpen = action.payload;
        }
    },
    extraReducers: builder => {
        // load users
        builder.addCase(fetchUsers.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchUsers.fulfilled, (state, action: PayloadAction<Array<User>>) => {
            state.loading = false
            state.users = action.payload
            state.error = ''
        })
        builder.addCase(fetchUsers.rejected, (state, action) => {
            state.loading = false
            state.users = []
            // TODO: Fix all errors!!
            // state.error = action.error.message
        })

        // add user
        builder.addCase(addUser.fulfilled, (state, action: PayloadAction<User>) => {
            state.users.push(action.payload)
            state.error = ''
        })
        builder.addCase(addUser.rejected, (state, action) => {
            // state.error = action.error.message
        })

        // update user
        builder.addCase(updateUser.fulfilled, (state, action: PayloadAction<User>) => {
            const indexToReplace = state.users.findIndex(user => user.id === action.payload.id);
            state.users[indexToReplace] = action.payload
            state.error = ''
        })
        builder.addCase(updateUser.rejected, (state, action) => {
            // state.error = action.error.message
        })

        // delete user
        builder.addCase(deleteUser.fulfilled, (state, action: PayloadAction<number>) => {
            state.users = state.users.filter((user) => user.id !== action.payload)
            state.error = ''
        })
        builder.addCase(deleteUser.rejected, (state, action) => {
            // state.error = action.error.message
        })
    }
})

export default usersSlice.reducer
export const {setAddUserDialogOpen} = usersSlice.actions