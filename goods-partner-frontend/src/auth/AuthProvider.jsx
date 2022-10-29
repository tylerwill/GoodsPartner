import * as React from 'react'
import {useState, useEffect, useMemo, useContext} from "react";
import {usersApi} from "../api/usersApi";

const AuthContext = React.createContext()

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        usersApi.getCurrentUser()
            .then((response) => setUser(response.data))
            .catch((_error) => setError(_error))
            .finally(() => setLoading(false));
    }, []);

    const memoedValue = useMemo(
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
