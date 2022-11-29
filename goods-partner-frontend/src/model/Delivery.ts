// TODO: replace all any

import {Route} from "./Route";
import Order from "./Order";

export default interface Delivery {
    id: string
    deliveryDate: string,
    status: DeliveryStatus,
    formationStatus: DeliveryFormationStatus,
    orders: Array<Order>
    productsShipping: Array<any>
    routes: Array<Route>
}

enum DeliveryStatus {
    DRAFT,
    APPROVED,
    COMPLETED
}

export enum DeliveryFormationStatus {
    ORDERS_LOADING,
    ORDERS_LOADED,
    ROUTE_CALCULATION,
    COMPLETED
}