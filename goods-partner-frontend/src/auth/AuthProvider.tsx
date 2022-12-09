import * as React from 'react'
import {ReactNode, useContext, useEffect, useMemo, useState} from 'react'
import {usersApi} from "../api/usersApi";
import {AuthUserContext} from "./AuthUserContext";
import {User} from "../model/User";

const AuthContext = React.createContext<AuthUserContext | null>(null);

interface Props {
    children: ReactNode[]
}

export const AuthProvider = ({children}: Props) => {
    const [user, setUser] = useState<User | null>(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        usersApi.getCurrentUser()
            .then((response) => setUser(response.data))
            .catch((_error) => setError(_error))
            .finally(() => setLoading(false));
    }, []);


    // @ts-ignore
    const memoedValue = useMemo<AuthUserContext>(
        () => ({
            user,
            loading,
            error,
        }),
        [user, loading, error]
    );

    return (
        <AuthContext.Provider value={memoedValue}>
            {!loading && children}
        </AuthContext.Provider>
    )

}

export default function useAuth() {
    return useContext(AuthContext);
}
