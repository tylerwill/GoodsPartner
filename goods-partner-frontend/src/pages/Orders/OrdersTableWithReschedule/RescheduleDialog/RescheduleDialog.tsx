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

interface Props {
	open: boolean
	setOpen: (open: boolean) => void
	onAction: (rescheduleDate: string) => void
}

export default ({ open, setOpen, onAction }: Props) => {
	const handleClose = () => {
		setOpen(false)
	}

	const tomorrow = new Date()
	tomorrow.setDate(tomorrow.getDate() + 1)
	const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-')
	const [rescheduleDate, setRescheduleDate] = useState(defaultDate)

	const handleCreate = () => {
		onAction(rescheduleDate)
		setOpen(false)
	}

	return (
		<Dialog
			open={open}
			onClose={handleClose}
			aria-labelledby='alert-dialog-title'
			aria-describedby='alert-dialog-description'
		>
			<DialogTitle id='alert-dialog-title'>Запланувати доставку</DialogTitle>
			<DialogContent sx={{ mt: 2 }}>
				<DialogContentText id='alert-dialog-description'>
					Оберіть дату, на яку ви бажаєте запланувати доставку
				</DialogContentText>
				<TextField
					sx={{ minWidth: '100%', mt: 2 }}
					id='date'
					type='date'
					defaultValue={rescheduleDate}
					// TODO: Что это????
					InputLabelProps={{
						shrink: true
					}}
					onChange={e => setRescheduleDate(e.target.value)}
				/>
			</DialogContent>
			<DialogActions sx={{ pb: '16px', pr: '24px' }}>
				<Button variant='outlined' onClick={handleClose}>
					Скасувати
				</Button>
				<Button variant='contained' onClick={handleCreate} autoFocus>
					Запланувати
				</Button>
			</DialogActions>
		</Dialog>
	)
}
