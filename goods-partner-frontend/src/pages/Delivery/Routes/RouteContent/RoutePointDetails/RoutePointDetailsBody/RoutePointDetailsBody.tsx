import Grid from '@mui/material/Grid'
import InfoTableItem from '../../../../../../components/InfoTableItem/InfoTableItem'
import React from 'react'
import {RoutePoint} from "../../../../../../model/Route";

const RoutePointDetailsBody = ({ routePoint }: { routePoint: RoutePoint }) => {

	const completedAt =
		routePoint.completedAt === null ? '-' : routePoint.completedAt
	return (
		<Grid container spacing={2}>
			<Grid item xs={4}>
				<InfoTableItem
					title={'Час доставки'}
					data={
						routePoint.deliveryStart + ' - ' + routePoint.deliveryEnd
					}
				/>
			</Grid>
			<Grid item xs={4}>
				<InfoTableItem
					title={'Вага'}
					data={routePoint.addressTotalWeight + ' кг'}
				/>
			</Grid>
			<Grid item xs={4}>
				<InfoTableItem title={'Клієнт'} data={routePoint.clientName} />
			</Grid>
			<Grid item xs={4}>
				<InfoTableItem
					title={'Прибуття, прогноз'}
					data={routePoint.expectedArrival}
				/>
			</Grid>
			<Grid item xs={4}>
				<InfoTableItem
					title={'Завершення, прогноз'}
					data={routePoint.expectedCompletion}
				/>
			</Grid>
			<Grid item xs={4}>
				<InfoTableItem title={'Завершення, факт'} data={completedAt} />
			</Grid>
		</Grid>
	)
}

export default RoutePointDetailsBody
