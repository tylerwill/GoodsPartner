export interface User {
    id: number
    email: string
    enabled: boolean
    role: UserRole
    userName: string
    heartbeatId: string
}

export enum UserRole {
    DRIVER = "DRIVER",
    LOGISTICIAN = "LOGISTICIAN",
    ADMIN = "ADMIN"
}