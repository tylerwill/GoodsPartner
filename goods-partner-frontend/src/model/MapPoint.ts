export enum MapPointStatus {
    KNOWN = 'KNOWN',
    UNKNOWN = 'UNKNOWN',
    AUTOVALIDATED = 'AUTOVALIDATED'
}

export default interface MapPoint {
	address: string
	latitude: number
	longitude: number
	status: MapPointStatus
}
