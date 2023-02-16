import MapPoint from "./MapPoint";
import {Car} from "./Car";

export interface Task {
    id: number
    description: string
    mapPoint: MapPoint
    car?: Car
    executionDate: string
}
