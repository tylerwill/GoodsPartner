import {createSlice} from "@reduxjs/toolkit"
import {RootState} from "../../redux/store";
import {User} from "../../model/User";

interface IUserState {
    user: User | null;
    token: string | null;
}


function readInitialState() {
    const storageToken = localStorage.getItem('token');
    const storageUser = localStorage.getItem('user');

    return {
        token: storageToken ? JSON.parse (storageToken) : null,
        user: storageUser ? JSON.parse (storageUser) : null
    }
}

export const authSlice = createSlice({
    name: 'auth',
    initialState: readInitialState(),
    reducers: {
        setUserInfo: (state, action) => {
            const {user, accessToken} = action.payload
            state.user = user
            state.token = accessToken
            localStorage.setItem('user', JSON.stringify(user));
            localStorage.setItem('token', JSON.stringify(accessToken));
        },
        logout: (state) => {
            state.user = null;
            state.token = null;
            localStorage.removeItem('user');
            localStorage.removeItem('token');
        }
    },
})

export const {setUserInfo, logout} = authSlice.actions

export default authSlice.reducer

export const selectCurrentUser = (state: RootState) => state.auth.user
export const selectCurrentToken = (state: RootState) => state.auth.token