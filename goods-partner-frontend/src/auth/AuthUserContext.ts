import { User } from '../model/User'

export interface AuthUserContext {
	user: User
	error?: string
}
