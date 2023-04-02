import { Alert, Snackbar } from '@mui/material'
import React from 'react'

const ErrorAlert = ({ error }) => {
	return (
		<Snackbar
			anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
			open={true}
			autoHideDuration={6000}
		>
			<Alert severity='error' sx={{ width: '100%' }}>
				{error}
			</Alert>
		</Snackbar>
	)
}

export default ErrorAlert
