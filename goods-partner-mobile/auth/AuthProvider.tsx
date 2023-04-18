import * as React from 'react'
import {useContext, useEffect} from 'react'
import {useActions, useAppSelector} from "../hooks/redux-hooks";
import {selectCurrentUser} from "../features/auth/authSlice";
import {User} from "../model/User";
import * as SecureStore from "expo-secure-store";
import {useLoginMutation} from "../api/auth/authApi";
import Spinner from "react-native-loading-spinner-overlay";

const AuthContext = React.createContext<AuthUserContext>({})

const USER_INFO_STORAGE_KEY = 'user_info';

interface Props {
    children: any
}

export interface AuthUserContext {
    authState?: { user: User | null, authenticated: boolean | null }
    onLogin?: (login: string, password: string) => Promise<any>
    onLogout?: () => Promise<any>
}

export const useAuth = () => {
    return useContext(AuthContext)
}
export const AuthProvider = ({children}: Props) => {
    const [login, results] = useLoginMutation();
    const user = useAppSelector(selectCurrentUser);

    const {setUserInfo, setAuthError} = useActions();
    console.log("current user", user);

    useEffect(() => {
        const loadUserInfo = async () => {
            const userInfo = await loadFromStorage(USER_INFO_STORAGE_KEY);
            if (userInfo) {
                setUserInfo(JSON.parse(userInfo));
            }
            console.log("load from storage", userInfo);
        }
        loadUserInfo();
    }, [])

    const onLogin = async (username: string, password: string) => {
        try {
            const userInfo = await login({username, password}).unwrap()

            setUserInfo(userInfo);
            await saveToStorage('userInfo', JSON.stringify(userInfo));
            console.log("store to storage", userInfo);
            setAuthError(false);

        } catch (e) {
            console.log("Error in provider", e);
            setAuthError(true);
        }
    }

    const value: AuthUserContext = {
        authState: {user: user, authenticated: user !== null},
        onLogin
    }
    return (
        <AuthContext.Provider value={value}>
            <Spinner visible={results.isLoading}/>
            {children}
        </AuthContext.Provider>
    )
}


const saveToStorage = (key, value) => {
    return SecureStore.setItemAsync(key, value);
};

const loadFromStorage = (key) => {
    return SecureStore.getItemAsync(key);
};

const removeFromStorage = (key) => {
    return SecureStore.deleteItemAsync(key);
};

