import { Chip } from '@mui/material'
import React from 'react'

const DeliveryStatusChip = ({ status }) => {
	let text
	let color
	switch (status) {
		case 'APPROVED': {
			text = 'Підтверджена'
			color = 'primary'
			break
		}

		case 'DRAFT': {
			text = 'Створена'
			color = 'default'
			break
		}

		case 'COMPLETED': {
			text = 'Закінчена'
			color = 'success'
			break
		}
	}

	return (
		<Chip
			label={text}
			sx={{ color: '#000', borderWidth: '2px' }}
			color={color}
			variant='outlined'
		/>
	)
}

export default DeliveryStatusChip
