import {UserRole} from "./User";

export interface DeliveryHistory {
	id: string
	deliveryId: string
	createdAt: string
	role: UserRole
	userEmail: string
	action: string
}