import React from 'react'
import Box from '@mui/material/Box'
import { Button, Typography } from '@mui/material'
import { RoutePoint } from '../../../../../../model/RoutePoint'
import RoutePointSelect from '../RoutePointSelect/RoutePointSelect'
import RoutePointOrdersDialog from '../RoutePointOrdersDialog/RoutePointOrdersDialog'

interface RoutePointDetailsHeaderProps {
	routePoint: RoutePoint
	routePointNumber: number
}

const RoutePointDetailsHeader = ({
	routePoint,
	routePointNumber
}: RoutePointDetailsHeaderProps) => {
	const [orderDialogOpen, setOrderDialogOpen] = React.useState(false)

	return (
		<Box
			sx={{
				width: '100%',
				display: 'flex',
				justifyContent: 'space-between',
				alignItems: 'center'
			}}
		>
			<Typography
				sx={{ fontWeight: 'bold', maxWidth: '450px' }}
				variant='body2'
				component='h2'
			>
				№{routePointNumber}, {routePoint.address}
			</Typography>

			{/*<Button variant="outlined" disabled>Змінити машину</Button>*/}
			<Box sx={{ display: 'flex' }}>
				<Button
					variant='text'
					sx={{ mr: 2 }}
					onClick={() => setOrderDialogOpen(true)}
				>
					Показати деталі
				</Button>
				<RoutePointSelect routePoint={routePoint} />
			</Box>

			{orderDialogOpen && (
				<RoutePointOrdersDialog
					open={orderDialogOpen}
					closeDialog={() => setOrderDialogOpen(false)}
					routePoint={routePoint}
				/>
			)}
		</Box>
	)
}

export default RoutePointDetailsHeader
