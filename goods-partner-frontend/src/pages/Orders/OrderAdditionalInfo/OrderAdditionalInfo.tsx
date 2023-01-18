import Grid from '@mui/material/Grid'
import { Checkbox, Typography } from '@mui/material'
import React from 'react'
import Order from '../../../model/Order'
import Box from '@mui/material/Box'

const OrderAdditionalInfo = ({ order }: { order: Order }) => {
	const from = order.deliveryStart ?? '09:00'
	const to = order.deliveryFinish ?? '18:00'

	return (
		<Grid
			sx={{
				mt: 2,
				pb: 2,
				background: 'rgba(0, 0, 0, 0.02)',
				borderRadius: '6px'
			}}
			container
			spacing={2}
		>
			<Grid item xs={2}>
				<Typography
					sx={{ mb: 1 }}
					variant='caption'
					display='block'
					gutterBottom
				>
					Статус
				</Typography>
				<Typography variant='caption' display='block' gutterBottom>
					???
				</Typography>
			</Grid>

			<Grid item xs={1}>
				<Typography
					sx={{ mb: 1 }}
					variant='caption'
					display='block'
					gutterBottom
				>
					Час доставки
				</Typography>
				<Typography variant='caption' display='block' gutterBottom>
					{from + ' - ' + to}
				</Typography>
			</Grid>

			<Grid item xs={1}>
				<Typography
					sx={{ mb: 1 }}
					variant='caption'
					display='block'
					gutterBottom
				>
					Вага
				</Typography>
				<Typography variant='caption' display='block' gutterBottom>
					{order.orderWeight} кг
				</Typography>
			</Grid>

			<Grid item xs={2}>
				<Typography
					sx={{ mb: 1 }}
					variant='caption'
					display='block'
					gutterBottom
				>
					Менеджер
				</Typography>
				<Typography variant='caption' display='block' gutterBottom>
					{order.managerFullName}
				</Typography>
			</Grid>

			<Grid item xs={4}>
				<Typography
					sx={{ mb: 1 }}
					variant='caption'
					display='block'
					gutterBottom
				>
					Коментар
				</Typography>
				<Typography variant='caption' display='block' gutterBottom>
					{order.comment}
				</Typography>
			</Grid>

			<Grid item xs={2}>
				<Typography
					sx={{ mb: 1 }}
					variant='caption'
					display='block'
					gutterBottom
				>
					Заморозка
				</Typography>
				<Box>
					<Typography variant='caption' display='block' gutterBottom>
						<Checkbox
							sx={{ p: 0 }}
							size={'small'}
							disabled={true}
							checked={order.frozen}
						/>{' '}
						Потребує заморозки
					</Typography>
				</Box>
			</Grid>
		</Grid>
	)
}

export default OrderAdditionalInfo
