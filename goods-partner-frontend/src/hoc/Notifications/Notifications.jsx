import * as React from 'react'
import {useEffect} from 'react'
import {currentHost} from '../../util/util'
import {closeSnackbar, SnackbarProvider, useSnackbar} from 'notistack'
import {useSelector} from 'react-redux'
import {setNotification} from '../../features/notifications/notificationsSlice'
import {useAppDispatch, useAppSelector} from '../../hooks/redux-hooks'
import {deliveriesApi} from '../../api/deliveries/deliveries.api'
import {deliveryOrdersApi} from '../../api/delivery-orders/delivery-orders.api'
import {routesApi} from '../../api/routes/routes.api'
import useAuth from "../../auth/AuthProvider";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import {selectCurrentUser} from "../../features/auth/authSlice";

function useListenEvents(dispatch, heartbeatId) {
    useEffect(() => {

        const sse = new EventSource(`${currentHost()}api/v1/live-event/${heartbeatId}`, {
            withCredentials: true
        })

        function getRealtimeData(notification) {
            if (!notification) {
                sse.close()
            }

            if (notification.type === 'HEARTBEAT') {
                return
            }
            dispatch(setNotification(notification))
        }

        sse.onmessage = e => getRealtimeData(JSON.parse(e.data))
        sse.onerror = error => {
            // error log here
            console.log('error in live events', error)
            sse.close()
        }

        return () => {
            sse.close()
        }
    }, [])
}

function useProcessNotifications(
    currentNotification,
    enqueueSnackbar,
    dispatch
) {
    useEffect(() => {
        if (currentNotification == null) {
            return
        }
        console.log("current notification, ", currentNotification);
        enqueueSnackbar(currentNotification.message, {
            variant:
                currentNotification.type === 'INFO'
                    ? 'default'
                    : currentNotification.type.toLowerCase()
        })

        if (currentNotification.action?.type === 'DELIVERY_CREATED') {
            dispatch(
                deliveriesApi.util.invalidateTags([{type: 'deliveries', id: 'list'}])
            )
        } else if (currentNotification.action?.type === 'DELIVERY_UPDATED') {
            const {deliveryId} = currentNotification.action

            dispatch(
                deliveriesApi.util.invalidateTags([
                    {type: 'deliveries', id: deliveryId},
                    {type: 'deliveries', id: "list"}
                ])
            )
        } else if (currentNotification.action?.type === 'ORDER_UPDATED') {
            const {deliveryId} = currentNotification.action

            dispatch(
                deliveriesApi.util.invalidateTags([
                    {type: 'deliveries', id: deliveryId}
                ])
            )
            dispatch(
                deliveryOrdersApi.util.invalidateTags([
                    {type: 'delivery-orders', id: 'forDelivery'}
                ])
            )
        } else if (currentNotification.action?.type === 'ROUTE_UPDATED') {
            const {deliveryId} = currentNotification.action
            dispatch(
                deliveriesApi.util.invalidateTags([
                    {type: 'deliveries', id: deliveryId}
                ])
            )
            dispatch(
                routesApi.util.invalidateTags([{type: 'routes', id: 'forDelivery'}])
            )
        }
    }, [currentNotification])
}

function useNotificationsHeartbeat(heartbeatId) {
    useEffect(() => {
        const interval = setInterval(() => {
            fetch(`${currentHost()}api/v1/live-event/${heartbeatId}/keep-alive`, {
                credentials: 'include',
                method: 'get'
            }).then(response => {
                console.log('heart-beat to server ', response)
            })
        }, 25000)
        return () => clearInterval(interval)
    }, [])
}

function Notifications({children}) {
    const {user} = useAuth();

    const heartbeatId = user.heartbeatId;
    const {enqueueSnackbar} = useSnackbar()
    const dispatch = useAppDispatch()
    const {currentNotification} = useSelector(state => state.notifications)

    useListenEvents(dispatch, heartbeatId)
    useProcessNotifications(currentNotification, enqueueSnackbar, dispatch)
    useNotificationsHeartbeat(heartbeatId)

    return <SnackbarProvider
        anchorOrigin={{
            vertical: 'top',
            horizontal: 'right'
        }}
        action={key => (
            <IconButton
                aria-label="Close notification"
                color="inherit"
                onClick={() => closeSnackbar(key)}
            >
                <CloseIcon fontSize="small"/>
            </IconButton>
        )}
    >{children}</SnackbarProvider>
}

export default Notifications
