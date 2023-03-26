// TODO: replace all any

import {Route} from './Route'
import Order from './Order'

export default interface Delivery {
    id: string
    deliveryDate: string
    status: DeliveryStatus
    formationStatus: DeliveryFormationStatus
    orders: Array<Order>
    productsShipping: Array<any>
    routes: Array<Route>
}

export enum DeliveryStatus {
    DRAFT = 'DRAFT',
    APPROVED = 'APPROVED',
    COMPLETED = 'COMPLETED'
}

export enum DeliveryFormationStatus {
    ORDERS_LOADING = 'ORDERS_LOADING',
    ORDERS_LOADED = 'ORDERS_LOADED',
    ORDERS_LOADING_FAILED = 'ORDERS_LOADING_FAILED',

    ROUTE_CALCULATION = 'ROUTE_CALCULATION',
    READY_FOR_CALCULATION = 'READY_FOR_CALCULATION',
    ROUTE_CALCULATION_FAILED = 'ROUTE_CALCULATION_FAILED',
    CALCULATION_COMPLETED = 'CALCULATION_COMPLETED',
}
