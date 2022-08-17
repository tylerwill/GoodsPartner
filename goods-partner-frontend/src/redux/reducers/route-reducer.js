import * as actionTypes from '../actions/action-types';

let initialRoutes = {
    date: "",
    routes: []
}

const routeReducer = (state = initialRoutes, action) => {
    switch (action.type) {
        case actionTypes.CALCULATE_ROUTES_BY_DATE:
            return calculateRoutesByDate(action);
        case actionTypes.UPDATE_ROUTE_POINT:
            return updateRoutePoint(state, action);

        case actionTypes.UPDATE_ROUTE:
            return updateRoute(state, action);
        default:
            return state;
    }
}

function addMinutes(numOfMinutes, date = new Date()) {
    const newDate = new Date(date);
    newDate.setMinutes(date.getMinutes() + numOfMinutes);

    return newDate;
}

const calculateRoutesByDate = (action) => {
    const newRoutes = action.routes.routes.map(route => {
        let currentTime = new Date();
        currentTime.setHours(9);
        currentTime.setMinutes(0);
        const newRoutePoints = route.routePoints.map(routePoint => {
            const predictedCompleteAt = addMinutes(routePoint.routePointDistantTime + 30, currentTime);
            routePoint.predictedCompleteAt = predictedCompleteAt;
            currentTime = predictedCompleteAt;
            return routePoint;
        });

        route.routePoints = newRoutePoints;
        return route;
    });


    return {
        date: action.routes.date,
        routes: newRoutes.length === 0 ? action.routes.routes : newRoutes,
        carLoadDetails: action.routes.carLoadDetails
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

const updateRoutePoint = (state, action) => {
    const newRoutePoint = action.updatedRoutePoint;
    const newState = {...state};
    const newRoutes = [...state.routes];
    newState.routes = newRoutes;

    newRoutes.forEach(route => {
        const newRoutePoints = route.routePoints.map(routePoint => {

            const routePointCopy = {...routePoint};
            if (routePoint.id === newRoutePoint.id) {
                if (newRoutePoint.status === 'DONE') {
                    newRoutePoint.actualCompleteAt = new Date();
                }

                return newRoutePoint;
            }
            return routePointCopy;
        })
        route.routePoints = newRoutePoints;
    })

    return newState;
}

export default routeReducer;