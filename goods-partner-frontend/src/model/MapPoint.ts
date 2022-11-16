import {MapPointStatus} from "./MapPointStatus";

export default interface MapPoint {
    address: string,
    latitude: number,
    longitude: number,
    status: MapPointStatus
}