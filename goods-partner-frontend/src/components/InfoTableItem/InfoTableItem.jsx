import Box from '@mui/material/Box'
import { Typography } from '@mui/material'
import React from 'react'

const InfoTableItem = ({ title, data }) => {
	return (
		<Box>
			<Typography sx={{ fontSize: '12px', color: 'rgba(0, 0, 0, 0.6)' }}>
				{title}
			</Typography>
			<Typography sx={{ fontSize: '12px' }}>{data}</Typography>
		</Box>
	)
}

export default InfoTableItem
