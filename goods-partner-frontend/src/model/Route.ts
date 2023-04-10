import MapPoint from './MapPoint'
import {Car} from './Car'

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
	status: string
	matchingExpectedDeliveryTime: boolean
}

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
	status: string
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
