import { User } from '../model/User'

export interface AuthUserContext {
	user: User | null
	error?: string
}
