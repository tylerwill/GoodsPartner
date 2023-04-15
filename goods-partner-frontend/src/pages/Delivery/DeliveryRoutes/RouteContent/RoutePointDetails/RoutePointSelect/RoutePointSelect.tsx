import {
	FormControl,
	MenuItem,
	Select,
	SelectChangeEvent,
	styled
} from '@mui/material'
import React, { useCallback } from 'react'
import { RoutePoint } from '../../../../../../model/RoutePoint'
import {
	useCompleteRoutePointMutation,
	useResetRoutePointMutation,
	useSkipRoutePointMutation
} from '../../../../../../api/routes/routes.api'

interface RoutePointSelectProps {
	routePoint: RoutePoint
}

const RoutePointSelect = ({ routePoint }: RoutePointSelectProps) => {
	const { status } = routePoint
	const selectColor = getSelectColor(status)

	const [resetRoutePoint] = useResetRoutePointMutation()
	const [skipRoutePoint] = useSkipRoutePointMutation()
	const [completeRoutePoint] = useCompleteRoutePointMutation()

	const handleChange = useCallback((event: SelectChangeEvent<unknown>) => {
		switch (event.target.value) {
			case 'PENDING': {
				resetRoutePoint(routePoint.id)
				break
			}
			case 'DONE': {
				completeRoutePoint(routePoint.id)
				break
			}
			case 'SKIPPED': {
				skipRoutePoint(routePoint.id)
				break
			}
		}
	}, [])
	const CustomSelect = styled(Select)(() => ({
		'&.MuiOutlinedInput-root': {
			'& fieldset': {
				borderColor: selectColor
			}
		},
		'& .MuiOutlinedInput-input': {
			padding: '4px 16px',
			textTransform: 'uppercase',
			fontSize: '13px',
			fontWeight: 500,
			color: selectColor
		}
	}))

	return (
		<div>
			<FormControl>
				<CustomSelect
					value={status}
					onChange={handleChange}
					autoWidth
					MenuProps={{ MenuListProps: { disablePadding: true } }}
				>
					<MenuItem value={'PENDING'}>В очікуванні</MenuItem>
					<MenuItem value={'DONE'}>Готово</MenuItem>
					<MenuItem value={'SKIPPED'}>Пропущено</MenuItem>
				</CustomSelect>
			</FormControl>
		</div>
	)
}

function getSelectColor(status: string) {
	switch (status) {
		case 'PENDING':
			return '#1976D2'
		case 'DONE':
			return '#2E7D32'
		case 'SKIPPED':
			return '#ED6C02'
	}
}

export default RoutePointSelect
