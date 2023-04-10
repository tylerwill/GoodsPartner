import {format} from 'date-fns'
import Order from "../model/Order";
import {DeliveryType} from "../model/Delivery";

export const currentHost = () => {
    return import.meta.env.DEV
        ? import.meta.env.VITE_LOCALHOST_BASE_URL
        : import.meta.env.VITE_PROD_BASE_URL
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

export const isTimeShifted = (order: Order) => {
    return order.deliveryStart !== '09:00' || order.deliveryFinish !== '18:00';
}
