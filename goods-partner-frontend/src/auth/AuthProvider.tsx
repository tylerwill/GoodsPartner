import * as React from 'react'
import {ReactNode, useContext, useMemo} from 'react'
import {AuthUserContext} from './AuthUserContext'
import {useAppSelector} from "../hooks/redux-hooks";
import {selectCurrentUser} from "../features/auth/authSlice";
import {User} from "../model/User";

const AuthContext = React.createContext<AuthUserContext | null>(null)

interface Props {
	children: ReactNode[]
}

export const AuthProvider = ({ children }: Props) => {
	const user = useAppSelector(selectCurrentUser);

	console.log("current user", user);

	// @ts-ignore
	const memoedValue = useMemo<AuthUserContext>(
		() => ({
			user
		}),
		[user]
	)

	return (
		<AuthContext.Provider value={memoedValue}>
			{children}
		</AuthContext.Provider>
	)
}

export default function useAuth() {
	return useContext(AuthContext) as AuthUserContext
}
