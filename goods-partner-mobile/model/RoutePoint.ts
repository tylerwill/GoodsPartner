import MapPoint from './MapPoint'

export interface RoutePoint {
    address: string
    addressTotalWeight: number
    clientName: string
    completedAt: string
    deliveryEnd: string
    deliveryStart: string
    expectedArrival: string
    expectedCompletion: string
    id: number
    mapPoint: MapPoint
    routePointDistantTime: number
    status: RoutePointStatus
    matchingExpectedDeliveryTime: boolean
}

export enum RoutePointStatus {
    INPROGRESS="INPROGRESS",
    PENDING = "PENDING",
    DONE = "DONE",
    SKIPPED = "SKIPPED"
}