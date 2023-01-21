import {format} from 'date-fns'
import {DeliveryType} from '../model/DeliveryType'
import {UserRole} from "../model/User";

export const currentHost = () => {
    return process.env.NODE_ENV === 'development'
        ? process.env.REACT_APP_LOCALHOST_BASE_URL
        : process.env.REACT_APP_HEROKU_BASE_URL
}

export const apiUrl = `${currentHost()}api/v1`

export const toHoursAndMinutes = (totalMinutes: number) => {
    const minutes = totalMinutes % 60
    const hours = Math.floor(totalMinutes / 60)

    return `${hours}год. ${minutes}хв.`
}

export const reformatDate = (date: string) => {
    return format(new Date(date), 'dd.MM.yy')
}

export const toDeliveryTypeString = (deliveryType: DeliveryType) => {
    switch (deliveryType) {
        case DeliveryType.POSTAL:
            return 'Доставка поштою'
        case DeliveryType.PRE_PACKING:
            return 'Фасовка'
        case DeliveryType.REGULAR:
            return 'Гранде Дольче'
        case DeliveryType.SELF_SERVICE:
            return 'Самовивіз'
    }
}

export const formatDecimalNumber = (value: number) => {
    return value.toFixed(2)
}

export const toUserRoleString = (role: UserRole) => {
    switch (role) {
        case UserRole.DRIVER:
            return 'Водій'
        case UserRole.ADMIN:
            return 'Адміністратор'
        case UserRole.LOGISTICIAN:
            return 'Логіст'

        default:
            return "Невідома роль";
    }
}
