// TODO: replace all any

export default interface Delivery {
    id: string
    deliveryDate: string,
    status: DeliveryStatus,
    formationStatus: DeliveryFormationStatus,
    orders: Array<any>
    productsShipping: Array<any>
    routes: Array<any>
}

enum DeliveryStatus {
    DRAFT,
    APPROVED,
    COMPLETED
}

enum DeliveryFormationStatus {
    ORDERS_LOADING,
    ORDERS_LOADED,
    ROUTE_CALCULATION,
    COMPLETED
}