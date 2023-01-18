import React, { useState } from 'react'
import {
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogContentText,
	DialogTitle,
	TextField
} from '@mui/material'

export default ({ open, setOpen, onCreate }) => {
	const handleClose = () => {
		setOpen(false)
	}

	const tomorrow = new Date()
	tomorrow.setDate(tomorrow.getDate() + 1)
	const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-')
	const [calculationDate, setCalculationDate] = useState(defaultDate)

	const handleCreate = () => {
		onCreate(calculationDate)
		setOpen(false)
	}

	return (
		<Dialog
			open={open}
			onClose={handleClose}
			aria-labelledby='alert-dialog-title'
			aria-describedby='alert-dialog-description'
		>
			<DialogTitle id='alert-dialog-title'>{'Створити доставку'}</DialogTitle>
			<DialogContent sx={{ mt: 2 }}>
				<DialogContentText id='alert-dialog-description'>
					Оберіть дату, на яку бажаєте сформувати доставку.
				</DialogContentText>
				<TextField
					sx={{ minWidth: '100%', mt: 2 }}
					id='date'
					type='date'
					defaultValue={calculationDate}
					// TODO: Что это????
					InputLabelProps={{
						shrink: true
					}}
					onChange={e => setCalculationDate(e.target.value)}
				/>
			</DialogContent>
			<DialogActions sx={{ pb: '16px', pr: '24px' }}>
				<Button variant='outlined' onClick={handleClose}>
					Скасувати
				</Button>
				<Button variant='contained' onClick={handleCreate} autoFocus>
					Сформувати
				</Button>
			</DialogActions>
		</Dialog>
	)
}
