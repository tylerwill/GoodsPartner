export interface User {
    id: number,
    email: string,
    enabled: boolean,
    role: UserRole,
    userName: string
}

export enum UserRole {
    DRIVER,
    LOGISTICIAN,
    ADMIN
}