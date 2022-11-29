import {User} from "../model/User";

export interface AuthUserContext {
    user: User | null,
    loading: boolean,
    error?: string
}