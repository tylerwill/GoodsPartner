import MapPoint from './MapPoint'
import {RoutePoint} from './RoutePoint'
import {Car} from './Car'

export interface Route {
    car: Car
    distance: number
    estimatedTime: number
    finishTime: Date
    id: number
    optimization: boolean
    routePoints: RoutePoint[]
    spentTime: number
    startTime: Date
    status: RouteStatus
    store: Store
    totalOrders: number
    totalPoints: number
    totalWeight: number
}

export interface Store {
    address: string
    mapPoint: MapPoint
    name: string
}

export enum RouteStatus {
    DRAFT = "DRAFT",
    APPROVED = "APPROVED",
    INPROGRESS = "INPROGRESS",
    COMPLETED = "COMPLETED"

}
