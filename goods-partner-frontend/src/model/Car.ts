// TODO: Replace user string with User

import {Driver} from "./Driver";

export interface Car {
    available: boolean,

    cooler: boolean,
    driver: Driver,
    id: number,
    licencePlate: string,
    loadSize: number,
    name: string,
    travelCost: number,
    weightCapacity: number
}