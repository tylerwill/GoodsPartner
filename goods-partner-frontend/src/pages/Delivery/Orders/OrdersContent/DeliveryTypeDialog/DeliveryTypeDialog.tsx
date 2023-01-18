import React, { useCallback } from 'react'
import {
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogContentText,
	DialogTitle,
	MenuItem,
	Select,
	SelectChangeEvent
} from '@mui/material'
import {
	useAppDispatch,
	useAppSelector
} from '../../../../../hooks/redux-hooks'
import { setDeliveryTypeDialogOpen } from '../../../../../features/delivery-orders/deliveryOrdersSlice'
import { DeliveryType } from '../../../../../model/DeliveryType'
import { toDeliveryTypeString } from '../../../../../util/util'
import { useUpdateOrderMutation } from '../../../../../api/delivery-orders/delivery-orders.api'
import Order from '../../../../../model/Order'


const DeliveryTypeDialog = () => {
	const dispatch = useAppDispatch()

	const order = useAppSelector(
		state => state.deliveryOrders.orderForModification
	)

	const isOpen = useAppSelector(
		state => state.deliveryOrders.isDeliveryTypeDialogOpen
	)
	const handleClose = useCallback(
		() => dispatch(setDeliveryTypeDialogOpen(false)),
		[dispatch, setDeliveryTypeDialogOpen]
	)

	const [deliveryType, setDeliveryType] = React.useState(order.deliveryType)
	const [updateOrder] = useUpdateOrderMutation()

	const handleDeliveryTypeUpdate = useCallback(
		(order: Order, deliveryType: DeliveryType) => {
			const updatedOrder = { ...order, deliveryType }
			updateOrder(updatedOrder)
			handleClose()
		},
		[]
	)

	const handleSelect = useCallback((event: SelectChangeEvent<DeliveryType>) => {
		// FIXME: Пи-пи-пи-здец
		const deliveryType =
			DeliveryType[event.target.value as keyof typeof DeliveryType]
		setDeliveryType(deliveryType)
	}, [])

	// @ts-ignore
	return (
		<Dialog
			sx={{ minWidth: '400px' }}
			maxWidth={'md'}
			open={isOpen}
			onClose={handleClose}
		>
			<DialogTitle>Змінити тип доставки</DialogTitle>
			<DialogContent>
				<DialogContentText>
					Оберіть новий тип доставки з переліку:
				</DialogContentText>
				<DeliveryTypeSelect
					currentType={deliveryType}
					onChange={handleSelect}
				/>
			</DialogContent>
			<DialogActions>
				<Button onClick={handleClose}>Скасувати</Button>
				<Button
					onClick={() => handleDeliveryTypeUpdate(order, deliveryType)}
					disabled={deliveryType === order.deliveryType}
				>
					Зберегти
				</Button>
			</DialogActions>
		</Dialog>
	)
}

interface DeliveryTypeSelectProps {
	currentType: DeliveryType
	onChange: (event: SelectChangeEvent<DeliveryType>) => void
}

const DeliveryTypeSelect: React.FC<DeliveryTypeSelectProps> = ({
	currentType,
	onChange
}) => {
	const deliveryTypes = Object.values(DeliveryType)

	const menuItems = React.useMemo(
		() =>
			deliveryTypes.map(type => (
				<MenuItem key={'deliveryTypeSelect' + type} value={type}>
					{toDeliveryTypeString(type as DeliveryType)}
				</MenuItem>
			)),
		[deliveryTypes]
	)

	return (
		<Select
			value={currentType}
			onChange={onChange}
			sx={{ width: '100%', mt: 1 }}
		>
			{menuItems}
		</Select>
	)
}

export default DeliveryTypeDialog
