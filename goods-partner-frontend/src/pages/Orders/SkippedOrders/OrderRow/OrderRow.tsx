import React from 'react'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import IconButton from '@mui/material/IconButton'
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp'
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown'
import Box from '@mui/material/Box'
import Collapse from '@mui/material/Collapse'
import TableContainer from '@mui/material/TableContainer'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableBody from '@mui/material/TableBody'
import Order from '../../../../model/Order'
import { formatDecimalNumber, reformatDate } from '../../../../util/util'
import { Checkbox } from '@mui/material'
import { useAppDispatch, useAppSelector } from '../../../../hooks/redux-hooks'
import {
	deselectOrder,
	selectOrder
} from '../../../../features/orders/ordersSlice'
import OrderAdditionalInfo from '../../OrderAdditionalInfo/OrderAdditionalInfo'

interface Props {
	order: Order
	keyPrefix: string
	collapseAll: boolean
	expandAll: boolean
	reset: () => void
}

const OrderRow = ({
	order,
	keyPrefix,
	collapseAll,
	expandAll,
	reset
}: Props) => {
	const [orderTableOpen, setOrderTableOpen] = React.useState(false)
	const isTableOpened = expandAll || (orderTableOpen && !collapseAll)

	const { selectedOrderIds } = useAppSelector(state => state.orders)

	const dispatch = useAppDispatch()
	const isSelected = selectedOrderIds.includes(order.id)
	const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.checked) {
			dispatch(selectOrder(order.id))
		} else {
			dispatch(deselectOrder(order.id))
		}
		event.preventDefault()
	}

	return (
		<>
			<TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
				<TableCell>
					<IconButton
						aria-label='expand row'
						size='small'
						onClick={() => {
							setOrderTableOpen(!orderTableOpen)
							reset()
						}}
					>
						{isTableOpened ? (
							<KeyboardArrowUpIcon />
						) : (
							<KeyboardArrowDownIcon />
						)}
					</IconButton>
				</TableCell>
				<TableCell padding='checkbox'>
					<Checkbox
						color='primary'
						checked={isSelected}
						onChange={handleChange}
					/>
				</TableCell>
				<TableCell component='th' scope='row'>
					{order.orderNumber}
				</TableCell>
				<TableCell>{reformatDate(order.shippingDate)}</TableCell>
				<TableCell>{order.clientName}</TableCell>
				<TableCell>{order.address}</TableCell>
			</TableRow>
			<TableRow>
				<TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={7}>
					<Collapse in={isTableOpened} timeout='auto' unmountOnExit>
						<Box sx={{ margin: 2 }}>
							<TableContainer component={Paper}>
								<Table sx={{ minWidth: 650 }} size='small'>
									<TableHead>
										<TableRow>
											<TableCell sx={{ width: '500px' }}>Артикул</TableCell>
											<TableCell>Кількість</TableCell>
											<TableCell>Упаковка</TableCell>
											<TableCell>Загальна вага</TableCell>
										</TableRow>
									</TableHead>
									<TableBody>
										{order.products.map(product => (
											<TableRow
												key={keyPrefix + product.productName}
												sx={{
													'&:last-child td, &:last-child th': { border: 0 }
												}}
											>
												<TableCell component='th' scope='row'>
													{product.productName}
												</TableCell>
												<TableCell> {product.amount}</TableCell>
												<TableCell>
													{' '}
													{formatDecimalNumber(product.unitWeight)}{' '}
													{product.measure}
												</TableCell>
												<TableCell>
													{' '}
													{formatDecimalNumber(product.totalProductWeight)} кг
												</TableCell>
											</TableRow>
										))}
									</TableBody>
								</Table>
							</TableContainer>

							<OrderAdditionalInfo order={order} />
						</Box>
					</Collapse>
				</TableCell>
			</TableRow>
		</>
	)
}

export default OrderRow
