import { DeliveryType } from './DeliveryType'
import MapPoint from './MapPoint'
import Product from './Product'

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

	rescheduleDate: string

	shippingDate: string
}
