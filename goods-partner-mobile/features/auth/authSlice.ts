import * as SecureStore from 'expo-secure-store';
import {createSlice, PayloadAction} from "@reduxjs/toolkit"
import {RootState} from "../../redux/store";
import {User} from "../../model/User";

interface IUserState {
    user: User | null;
    token: string | null;
    error?: boolean
}


function readInitialState() {
    // const storageToken = await loadFromStorage('token');
    // const storageUser = await loadFromStorage('user');

    return {
        token: null,
        user: null,
        error: false
    } as IUserState;
}

export const authSlice = createSlice({
    name: 'auth',
    initialState: readInitialState(),
    reducers: {
        setUserInfo: (state, action) => {
            const {user, accessToken} = action.payload
            state.user = user
            state.token = accessToken
        },
        setAuthError: (state, action: PayloadAction<boolean>) => {
            state.error = action.payload;
        },
        logout: (state) => {
            state.user = null;
            state.token = null;
        }
    },
})


export const {setUserInfo, setAuthError, logout} = authSlice.actions

export default authSlice.reducer

export const selectCurrentUser = (state: RootState) => state.auth.user
export const selectAuthError = (state: RootState) => state.auth.error