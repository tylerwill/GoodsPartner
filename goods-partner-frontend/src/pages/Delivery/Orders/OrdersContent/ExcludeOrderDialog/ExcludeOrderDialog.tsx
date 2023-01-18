import React, { useCallback, useState } from 'react'
import {
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogContentText,
	DialogTitle,
	TextField
} from '@mui/material'
import {
	useAppDispatch,
	useAppSelector
} from '../../../../../hooks/redux-hooks'
import { setExcludeDialogOpen } from '../../../../../features/delivery-orders/deliveryOrdersSlice'
import { useExcludeOrderMutation } from '../../../../../api/delivery-orders/delivery-orders.api'

const ExcludeOrderDialog = () => {
	const dispatch = useAppDispatch()

	const [excludeReason, setExcludeReason] = useState('')

	const order = useAppSelector(
		state => state.deliveryOrders.orderForModification
	)
	const isOpen = useAppSelector(
		state => state.deliveryOrders.isExcludeOrderDialogOpen
	)

	const [excludeOrder] = useExcludeOrderMutation()

	const handleClose = useCallback(
		() => dispatch(setExcludeDialogOpen(false)),
		[dispatch, setExcludeDialogOpen]
	)

	const handleExcludeOrder = useCallback(
		(excludeReason: string, orderId: number) => {
			excludeOrder({ orderId: order.id, excludeReason: excludeReason })
			handleClose()
		},
		[]
	)

	// @ts-ignore
	return (
		<Dialog
			sx={{ minWidth: '400px' }}
			maxWidth={'md'}
			open={isOpen}
			onClose={handleClose}
		>
			<DialogTitle>Вилучити замовлення</DialogTitle>
			<DialogContent>
				<DialogContentText>
					Вкажіть причину вилучення замовлення
				</DialogContentText>
				<TextField
					sx={{ minWidth: '100%', mt: 2 }}
					value={excludeReason}
					onChange={e => setExcludeReason(e.target.value)}
				/>
			</DialogContent>
			<DialogActions>
				<Button onClick={handleClose}>Скасувати</Button>
				<Button
					onClick={() => handleExcludeOrder(excludeReason, order.id)}
					disabled={excludeReason.length === 0}
				>
					Зберегти
				</Button>
			</DialogActions>
		</Dialog>
	)
}

export default ExcludeOrderDialog
