import * as React from 'react'
import { useEffect } from 'react'
import { currentHost } from '../../util/util'
import { useSnackbar } from 'notistack'
import { useSelector } from 'react-redux'
import { setNotification } from '../../features/notifications/notificationsSlice'
import { useAppDispatch } from '../../hooks/redux-hooks'
import { deliveriesApi } from '../../api/deliveries/deliveries.api'
import { deliveryOrdersApi } from '../../api/delivery-orders/delivery-orders.api'
import { routesApi } from '../../api/routes/routes.api'

function useListenEvents(dispatch) {
	useEffect(() => {
		const sse = new EventSource(`${currentHost()}api/v1/live-event`, {
			withCredentials: true
		})

		function getRealtimeData(notification) {
			if (!notification) {
				sse.close()
			}

			console.log('data', notification)
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

		enqueueSnackbar(currentNotification.message, {
			variant:
				currentNotification.type === 'INFO'
					? 'default'
					: currentNotification.type.toLowerCase()
		})

		if (currentNotification.action?.type === 'DELIVERY_CREATED') {
			dispatch(
				deliveriesApi.util.invalidateTags([{ type: 'deliveries', id: 'list' }])
			)
		} else if (currentNotification.action?.type === 'DELIVERY_UPDATED') {
			const { deliveryId } = currentNotification.action

			dispatch(
				deliveriesApi.util.invalidateTags([
					{ type: 'deliveries', id: deliveryId }
				])
			)
		} else if (currentNotification.action?.type === 'ORDER_UPDATED') {
			const { deliveryId } = currentNotification.action

			dispatch(
				deliveriesApi.util.invalidateTags([
					{ type: 'deliveries', id: deliveryId }
				])
			)
			dispatch(
				deliveryOrdersApi.util.invalidateTags([
					{ type: 'delivery-orders', id: 'forDelivery' }
				])
			)
		} else if (currentNotification.action?.type === 'ROUTE_UPDATED') {
			const { deliveryId } = currentNotification.action
			dispatch(
				deliveriesApi.util.invalidateTags([
					{ type: 'deliveries', id: deliveryId }
				])
			)
			dispatch(
				routesApi.util.invalidateTags([{ type: 'routes', id: 'forDelivery' }])
			)
		}
	}, [currentNotification])
}

function useNotificationsHeartbeat() {
	useEffect(() => {
		const interval = setInterval(() => {
			fetch(`${currentHost()}api/v1/keep-alive`, {
				credentials: 'include',
				method: 'get'
			}).then(response => {
				console.log('heart-beat to server ', response)
			})
		}, 25000)
		return () => clearInterval(interval)
	}, [])
}

function Notifications({ children }) {
	const { enqueueSnackbar } = useSnackbar()
	const dispatch = useAppDispatch()
	const { currentNotification } = useSelector(state => state.notifications)

	useListenEvents(dispatch)
	useProcessNotifications(currentNotification, enqueueSnackbar, dispatch)
	useNotificationsHeartbeat()

	return <>{children}</>
}

export default Notifications
