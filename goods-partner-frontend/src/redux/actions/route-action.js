import * as actionTypes from './action-types';

export const setRoutes = (routes) => {
  return {
    type: actionTypes.CALCULATE_ROUTES_BY_DATE,
    routes
  }
}

export const changeRoutePointStatusAction = (routePointId, newStatus) => {
  return {
    type: actionTypes.CHANGE_ROUTE_POINT_STATUS,
    routePointId,
    newStatus
  }
}

export const changeRouteStatusAction = (routeId, newStatus) => {
  return {
    type: actionTypes.CHANGE_ROUTE_STATUS,
    routeId,
    newStatus
  }
}

export const updateRouteAction = (updatedRoute) => {
  return {
    type: actionTypes.UPDATE_ROUTE,
    updatedRoute
  }
}

