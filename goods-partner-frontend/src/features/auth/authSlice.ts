import { createSlice } from "@reduxjs/toolkit"
import {RootState} from "../../redux/store";

const initialState = {
    token: null,
    user: null
}

const authSlice = createSlice({
    name: 'auth',
    initialState: initialState,
    reducers: {
        setUserInfo: (state, action) => {
            const { user, accessToken } = action.payload
            state.user = user
            state.token = accessToken
        },
        logOut: (state, action) => {
            state.user = null
            state.token = null
        }
    },
})

export const { setUserInfo, logOut } = authSlice.actions

export default authSlice.reducer

export const selectCurrentUser = (state:RootState) => state.auth.user
export const selectCurrentToken = (state:RootState) => state.auth.token