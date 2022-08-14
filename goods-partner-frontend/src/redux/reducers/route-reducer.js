import * as actionTypes from '../actions/action-types';

let initialRoutes = {
    date: "",
    routes: []
}

const routeReducer = (state = initialRoutes, action) => {
    switch (action.type) {
        case actionTypes.CALCULATE_ROUTES_BY_DATE:
            return {
                date: action.routes.date,
                routes: action.routes.routes,
                carLoadDetails: action.routes.carLoadDetails
            }
        case actionTypes.CHANGE_ROUTE_POINT_STATUS:
            return changeRoutePointStatus(state, action);

        case actionTypes.CHANGE_ROUTE_STATUS:
            return changeRouteStatus(state, action);
        case actionTypes.UPDATE_ROUTE:
            return updateRoute(state, action);
        default:
            return state;
    }
}


const updateRoute = (state, action) => {
    const updatedRoute = action.updatedRoute;
    const newRoutes = state.routes.map(route => {
        if (route.id === updatedRoute.id) {
            if (updatedRoute.status === "IN_PROGRESS") {
                updatedRoute.startTime = new Date();
                updatedRoute.finishTime = null;
            } else if (updatedRoute.status === "COMPLETED") {
                updatedRoute.finishTime = new Date();
                updatedRoute.spentTime = updatedRoute.finishTime - updatedRoute.startTime;
            }
            return updatedRoute;
        }
        return route;
    });

    // TODO: Deep copy
    const newState = {...state};
    newState.routes = newRoutes;
    return newState;
}

const changeRoutePointStatus = (state, action) => {
    const routePointId = action.routePointId;
    const newStatus = action.newStatus;
    const newState = {...state};
    const newRoutes = [...state.routes];
    newState.routes = newRoutes;

    newRoutes.forEach(route => {
        const newRoutePoints = route.routePoints.map(routePoint => {

            const newRoutePoint = {...routePoint};
            if (routePoint.id === routePointId) {
                newRoutePoint.status = newStatus;
            }
            return newRoutePoint;
        })
        route.routePoints = newRoutePoints;
    })

    return newState;
}

const changeRouteStatus = (state, action) => {

    const routeId = action.routeId;
    const newStatus = action.newStatus;
    const newState = {...state};
    const newRoutes = state.routes.map(route => {
        if (route.id === routeId) {
            route.status = newStatus;
        }
        return route;
    });

    newState.routes = newRoutes;

    return newState;
}

export default routeReducer;