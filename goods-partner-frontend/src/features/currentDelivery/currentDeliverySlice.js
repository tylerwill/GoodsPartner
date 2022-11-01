import {createAsyncThunk, createSlice} from '@reduxjs/toolkit'
import {deliveriesApi} from "../../api/deliveriesApi";

const initialState = {
    delivery: null,
    loading: true,
    error: '',
    tabIndex: 0,
    orderTabIndex: 0,
    currentRouteIndex: 0
};

export const fetchDelivery = createAsyncThunk('currentDelivery/fetch',
    (id) => {
        return deliveriesApi.findById(id)
            .then(response => response.data);
    })

export const fetchDeliveryForDriver = createAsyncThunk('currentDelivery/fetchForDriver',
    (id) => {
        return deliveriesApi.findByIdForDriver(id)
            .then(response => response.data);
    })

export const calculateDelivery = createAsyncThunk('currentDelivery/calculate',
    (delivery) => {
        return deliveriesApi.calculate(delivery)
            .then(response => response.data);
    })

export const approveDelivery = createAsyncThunk('currentDelivery/approve',
    (id) => {
        return deliveriesApi.approve(id)
            .then(response => response.data);
    })

export const updateRouteStatus = createAsyncThunk('currentDelivery/updateRouteStatus',
    ({routeId, action}, {getState}) => {
        const {id} = getState().currentDelivery.delivery;
        return deliveriesApi.applyRouteAction(id, routeId, action)
            .then(response => response.data);
    })

export const updateRoutePointStatus = createAsyncThunk('currentDelivery/updateRoutePointStatus',
    ({routeId, routePointId, action}, {getState}) => {
        const {id} = getState().currentDelivery.delivery;
        return deliveriesApi.applyRoutePointAction(id, routeId, routePointId, action)
            .then(response => response.data);
    })

const updateStatuses = (state, statuses) => {
    if (state.delivery.id !== statuses.deliveryId) {
        return;
    }

    const routeToUpdate = state.delivery.routes.find(route => route.id === statuses.routeId);
    routeToUpdate.status = statuses.routeStatus;
    routeToUpdate.finishTime = statuses.routeFinishTime;

    if (statuses.routePointId) {
        const routePointToUpdate = routeToUpdate.routePoints.find(point => point.id === statuses.routePointId);
        routePointToUpdate.status = statuses.routePointStatus;
        routePointToUpdate.completedAt = statuses.pointCompletedAt;
    }

    state.delivery.status = statuses.deliveryStatus;
}

export const selectOrdersByIds = (state, ids) => {
    return state.delivery.orders.filter(order => ids.some(id => id === order.id));
}

function approveCurrentDelivery(state, statuses) {
    if (state.delivery.id !== statuses.deliveryId) {
        return;
    }

    const newRoutesStatuses = statuses.routesStatus;
    const routes = state.delivery.routes;

    for (let i = 0; i < routes.length; i++) {
        const currentRoute = routes[i];
        const newStatus = newRoutesStatuses.find(route => route.id === currentRoute.id).routeStatus;
        if (newStatus) {
            currentRoute.status = newStatus;
        }
    }
}


const currentDeliverySlice = createSlice({
    name: 'currentDelivery',
    initialState,
    reducers: {
        updateOrder: (state, action) => {
            const updatedOrder = action.payload;
            const {refKey} = updatedOrder;
            const orders = state.delivery.orders;

            state.delivery.orders = orders.map(order => order.refKey !== refKey ? order : updatedOrder);
        },

        updateAddressForOrder: (state, action) => {
            const orderAddressInfo = action.payload;
            const updatedOrder = state.delivery.orders.find(order => order.refKey === orderAddressInfo.refKey);
            updatedOrder.address = orderAddressInfo.address;
            updatedOrder.mapPoint = orderAddressInfo.mapPoint;
            updatedOrder.mapPoint.status = 'AUTOVALIDATED';
        },
        setTabIndex: (state, action) => {
            state.tabIndex = action.payload;
        },
        setOrderTabIndex: (state, action) => {
            state.orderTabIndex = action.payload;
        },
        setCurrentRouteIndex: (state, action) => {
            state.currentRouteIndex = action.payload;
        }
    },
    extraReducers: builder => {
        // load delivery
        builder.addCase(fetchDelivery.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchDelivery.fulfilled, (state, action) => {
            state.loading = false
            state.delivery = action.payload
            state.error = ''
        })
        builder.addCase(fetchDelivery.rejected, (state, action) => {
            state.loading = false
            state.delivery = null
            state.error = action.error.message
        })

        // calculate delivery
        builder.addCase(calculateDelivery.fulfilled, (state, action) => {
            state.delivery = action.payload
            state.error = ''
        })
        builder.addCase(calculateDelivery.rejected, (state, action) => {
            state.error = action.error.message
        })

        // approve delivery
        builder.addCase(approveDelivery.fulfilled, (state, action) => {
            approveCurrentDelivery(state, action.payload);
            state.error = ''
        })
        builder.addCase(approveDelivery.rejected, (state, action) => {
            state.error = action.error.message
        })

        // action on route
        builder.addCase(updateRouteStatus.fulfilled, (state, action) => {
            updateStatuses(state, action.payload);
            state.error = ''
        })
        builder.addCase(updateRouteStatus.rejected, (state, action) => {
            state.error = action.error.message
        })

        // action on route point
        builder.addCase(updateRoutePointStatus.fulfilled, (state, action) => {
            updateStatuses(state, action.payload);
            state.error = ''
        })
        builder.addCase(updateRoutePointStatus.rejected, (state, action) => {
            state.error = action.error.message
        })

        // load delivery for driver
        builder.addCase(fetchDeliveryForDriver.pending, state => {
            state.loading = true
        })
        builder.addCase(fetchDeliveryForDriver.fulfilled, (state, action) => {
            state.loading = false
            state.delivery = action.payload
            state.error = ''
        })
        builder.addCase(fetchDeliveryForDriver.rejected, (state, action) => {
            state.loading = false
            state.delivery = null
            state.error = action.error.message
        })

    }
})

export default currentDeliverySlice.reducer
export const {
    updateOrder,
    updateAddressForOrder,
    setTabIndex,
    setOrderTabIndex,
    setCurrentRouteIndex
} = currentDeliverySlice.actions