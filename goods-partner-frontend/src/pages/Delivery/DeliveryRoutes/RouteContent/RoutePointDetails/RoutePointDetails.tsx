import React from 'react'
import Box from '@mui/material/Box'
import RoutePointDetailsHeader from './RoutePointDetailsHeader/RoutePointDetailsHeader'
import RoutePointDetailsBody from './RoutePointDetailsBody/RoutePointDetailsBody'
import {RoutePoint} from "../../../../../model/Route";

interface RoutePointDetailsProps {
	routePoint: RoutePoint
	routePointNumber: number
}

const RoutePointDetails = ({
	routePoint,
	routePointNumber
}: RoutePointDetailsProps) => {
	return (
		<Box
			sx={{
				width: '100%',
				background: 'rgba(0, 0, 0, 0.02)',
				borderRadius: '6px',
				p: 2
			}}
		>
			<RoutePointDetailsHeader
				routePoint={routePoint}
				routePointNumber={routePointNumber}
			/>
			<Box sx={{ mt: 3 }}>
				<RoutePointDetailsBody routePoint={routePoint} />
			</Box>
		</Box>
	)
}

export default RoutePointDetails
