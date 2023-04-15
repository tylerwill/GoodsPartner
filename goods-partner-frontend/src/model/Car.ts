export interface Car {
	available: boolean

	cooler: boolean
	driver: Driver
	id: number
	licencePlate: string
	loadSize: number
	name: string
	travelCost: number
	weightCapacity: number
}

export interface Driver {
	id: number
	userName: string
	email: string
}