export interface User {
    id: number
    email: string
    enabled: boolean
    role: UserRole
    userName: string
    heartbeatId: string
}

export enum UserRole {
    DRIVER = "ROLE_DRIVER",
    LOGISTICIAN = "ROLE_LOGISTICIAN",
    ADMIN = "ROLE_ADMIN"
}