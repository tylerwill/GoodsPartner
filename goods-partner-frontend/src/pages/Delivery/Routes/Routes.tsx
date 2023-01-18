import React from 'react'
import Grid from '@mui/material/Grid'
import RoutesSidebar from './RoutesSidebar/RoutesSidebar'
import RouteContent from './RouteContent/RouteContent'
import { useDispatch } from 'react-redux'
import { setCurrentRouteIndex } from '../../../features/currentDelivery/currentDeliverySlice'
import { useGetRoutesForDeliveryQuery } from '../../../api/routes/routes.api'
import { useParams } from 'react-router-dom'
import { useAppSelector } from '../../../hooks/redux-hooks'
import { useGetDeliveryQuery } from '../../../api/deliveries/deliveries.api'
import Loading from '../../../components/Loading/Loading'

const Routes = () => {
	const dispatch = useDispatch()
	const { currentRouteIndex } = useAppSelector(state => state.currentDelivery)

	const { deliveryId } = useParams()

	const {
		data: delivery,
		isLoading: isDeliveryLoading,
		error: deliveryError
	} = useGetDeliveryQuery(String(deliveryId))

	const {
		data: routes,
		isLoading: isRoutesLoading,
		error: routesError
	} = useGetRoutesForDeliveryQuery(String(deliveryId))

	const setCurrentRouteIndexHandler = (index: number) =>
		dispatch(setCurrentRouteIndex(index))

	if (!routes || !delivery) {
		return <Loading />
	}

	const currentRoute = routes[currentRouteIndex]

	return (
		<Grid container spacing={2} sx={{ padding: '0 24px' }}>
			<Grid item xs={3}>
				<RoutesSidebar
					routes={routes}
					currentRoute={currentRoute}
					setCurrentRouteIndex={setCurrentRouteIndexHandler}
				/>
			</Grid>
			<Grid item xs={9}>
				<RouteContent
					deliveryDate={delivery.deliveryDate}
					route={currentRoute}
				/>
			</Grid>
		</Grid>
	)
}

export default Routes
