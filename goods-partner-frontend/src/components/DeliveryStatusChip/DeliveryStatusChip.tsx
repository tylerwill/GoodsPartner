import { Chip } from '@mui/material'
import React from 'react'
import {DeliveryStatus} from "../../model/Delivery";
import {OverridableStringUnion} from "@mui/types";
import {ChipPropsColorOverrides} from "@mui/material/Chip/Chip";

const DeliveryStatusChip = ({ status }: {status:DeliveryStatus}) => {
	let text
	let color: OverridableStringUnion<'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning',
		ChipPropsColorOverrides> = 'primary';

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
