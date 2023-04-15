import {
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogTitle,
	Grid,
	Table,
	TableCell,
	TableContainer,
	TableHead,
	TableRow
} from '@mui/material'
import React from 'react'
import InfoTableItem from '../../../../../../components/InfoTableItem/InfoTableItem'
import Paper from '@mui/material/Paper'
import TableBody from '@mui/material/TableBody'
import { useGetRoutePointOrdersQuery } from '../../../../../../api/routes/routes.api'
import Loading from '../../../../../../components/Loading/Loading'
import { formatDecimalNumber } from '../../../../../../util/util'
import {RoutePoint} from "../../../../../../model/Route";

interface RoutePointOrdersDialogProps {
	open: boolean
	closeDialog: () => void
	routePoint: RoutePoint
}

const RoutePointOrdersDialog = ({
	open,
	closeDialog,
	routePoint
}: RoutePointOrdersDialogProps) => {
	const { data: orders, isLoading } = useGetRoutePointOrdersQuery(routePoint.id)

	if (!orders || isLoading) {
		return <Loading />
	}

	const commentText = orders
		.filter(order => order.comment)
		.map(order => order.comment)
		.join(',')

	return (
		<Dialog maxWidth={'lg'} open={open} onClose={closeDialog}>
			<DialogTitle>Деталі точки {routePoint.address}</DialogTitle>
			<DialogContent>
				<Grid container spacing={2}>
					<Grid item xs={12}>
						<InfoTableItem
							title={'Пункт призначення'}
							data={routePoint.address}
						/>
					</Grid>
					<Grid item xs={12}>
						<InfoTableItem
							title={'Коментар'}
							data={commentText.trim().length === 0 ? '-' : commentText}
						/>
					</Grid>
				</Grid>
				<TableContainer
					sx={{ marginTop: 4 }}
					component={Paper}
					style={{
						borderTop: '1px solid rgba(0, 0, 0, 0.1)'
					}}
				>
					<Table sx={{ minWidth: 650 }} size='small' aria-label='a dense table'>
						<TableHead>
							<TableRow>
								<TableCell sx={{ minWidth: '170px' }}>Замовлення</TableCell>
								<TableCell>Артикул</TableCell>
								<TableCell>Кількість</TableCell>
								<TableCell>Упаковка</TableCell>
								<TableCell>Всього</TableCell>
							</TableRow>
						</TableHead>
						<TableBody>
							{orders.map(order =>
								order.products.map(product => {
									const count = product.productPackaging.amount + ' ' + product.productPackaging.measureStandard;
									const totalWeight = product.productUnit.amount + ' ' + product.productUnit.measureStandard;
									const packaging = product.productPackaging.coefficientStandard + ' ' + product.productUnit.measureStandard;
									return (
										<TableRow
											key={'routePointDetails' + product.refKey}
											sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
										>
											<TableCell>{order.orderNumber}</TableCell>
											<TableCell>{product.productName}</TableCell>
											<TableCell> {count}</TableCell>
											<TableCell> {packaging}</TableCell>
											<TableCell> {totalWeight}</TableCell>
										</TableRow>
									)
								})
							)}
						</TableBody>
					</Table>
				</TableContainer>
			</DialogContent>
			<DialogActions>
				<Button
					sx={{ mr: 2, mb: 1 }}
					onClick={() => closeDialog()}
					variant={'contained'}
				>
					Закрити{' '}
				</Button>
			</DialogActions>
		</Dialog>
	)
}

export default RoutePointOrdersDialog
