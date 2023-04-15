import MapPoint from './MapPoint'
import {DeliveryType} from "./Delivery";

export default interface Order {
	address: string
	clientName: string
	comment: string
	deliveryFinish: string
	deliveryStart: string
	deliveryType: DeliveryType
	dropped: boolean
	excluded: boolean
	frozen: boolean
	id: number
	managerFullName: string

	mapPoint: MapPoint
	orderNumber: string
	orderWeight: number
	products: Array<Product>
	refKey: string
	excludeReason:string

	rescheduleDate: string

	shippingDate: string
}

export interface Product {
	amount: number
	coefficient: number
	measure: string
	productName: string
	storeName: string
	totalProductWeight: number
	unitWeight: number
	refKey: string
	productUnit: ProductMeasureDetails
	productPackaging: ProductMeasureDetails
}

export interface ProductMeasureDetails {
	amount: number
	coefficientStandard: number
	measureStandard: string
}