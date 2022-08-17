import * as actionTypes from './action-types';

export const setRoutes = (routes) => {
    return {
        type: actionTypes.CALCULATE_ROUTES_BY_DATE,
        routes
    }
}

export const updateRoutePointAction = (updatedRoutePoint) => {
    return {
        type: actionTypes.UPDATE_ROUTE_POINT,
        updatedRoutePoint
    }
}

export const updateRouteAction = (updatedRoute) => {
    return {
        type: actionTypes.UPDATE_ROUTE,
        updatedRoute
    }
}

