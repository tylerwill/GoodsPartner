import React, { useCallback } from 'react'
import Box from '@mui/material/Box'
import { Button } from '@mui/material'
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore'
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess'
import Paper from '@mui/material/Paper'
import TableContainer from '@mui/material/TableContainer'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import TableBody from '@mui/material/TableBody'
import TablePagination from '@mui/material/TablePagination'
import ProductRow from './OrderRow/OrderRow'
import { useGetScheduledQuery } from '../../../api/orders/orders.api'
import Loading from '../../../components/Loading/Loading'

const ScheduledOrders = () => {
	const { data: scheduledOrders } = useGetScheduledQuery()

	const [page, setPage] = React.useState(0)
	const [rowsPerPage, setRowsPerPage] = React.useState(25)

	const [collapseAll, setCollapseAll] = React.useState(false)
	const [expandAll, setExpandAll] = React.useState(false)

	const collapseAllHandler = useCallback(() => {
		setCollapseAll(true)
		setExpandAll(false)
	}, [])

	const expandAllHandler = useCallback(() => {
		setExpandAll(true)
		setCollapseAll(false)
	}, [])

	const reset = useCallback(() => {
		setExpandAll(false)
		setCollapseAll(false)
	}, [])

	const handleChangePage = (event: any, newPage: number) => {
		setPage(newPage)
	}

	const handleChangeRowsPerPage = (event: React.BaseSyntheticEvent) => {
		setRowsPerPage(parseInt(event.target.value, 10))
		setPage(0)
	}

	if (!scheduledOrders) {
		return <Loading />
	}

	// Avoid a layout jump when reaching the last page with empty rows.
	const emptyRows =
		page > 0
			? Math.max(0, (1 + page) * rowsPerPage - scheduledOrders.length)
			: 0

	console.log('scheduledOrders', scheduledOrders)
	return (
		<Box>
			<Box sx={{ display: 'flex', mb: 2 }}>
				<Button onClick={expandAllHandler}>
					<UnfoldMoreIcon
						sx={{ mr: 1, transform: 'rotate(45deg)', width: '0.75em' }}
					/>{' '}
					розгорнути всі
				</Button>
				<Button onClick={collapseAllHandler}>
					<UnfoldLessIcon
						sx={{ mr: 1, transform: 'rotate(45deg)', width: '0.75em' }}
					/>{' '}
					Згорнути всі
				</Button>
			</Box>

			<Paper variant={'outlined'}>
				<TableContainer
					style={{
						minHeight: '800px',
						borderTop: '1px solid rgba(0, 0, 0, 0.1)'
					}}
				>
					<Table aria-label='collapsible table'>
						<TableHead>
							<TableRow>
								<TableCell />
								<TableCell sx={{ minWidth: '135px' }}>№ замовлення</TableCell>
								<TableCell>Дата</TableCell>
								<TableCell>Клієнт</TableCell>
								<TableCell>Адреса</TableCell>
							</TableRow>
						</TableHead>
						<TableBody>
							{/*TODO: [Tolik] Think about keys */}
							{scheduledOrders
								.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
								.map((order, index) => {
									return (
										<ProductRow
											order={order}
											key={'scheduledOrders' + index}
											keyPrefix={'scheduledOrders' + 'subTable'}
											expandAll={expandAll}
											collapseAll={collapseAll}
											reset={reset}
										/>
									)
								})}
							{emptyRows > 0 && (
								<TableRow
									style={{
										height: 53 * emptyRows
									}}
								>
									<TableCell colSpan={7} />
								</TableRow>
							)}
						</TableBody>
					</Table>
				</TableContainer>
				<TablePagination
					rowsPerPageOptions={[25, 50, 100]}
					component='div'
					count={scheduledOrders.length}
					rowsPerPage={rowsPerPage}
					page={page}
					onPageChange={handleChangePage}
					onRowsPerPageChange={handleChangeRowsPerPage}
				/>
			</Paper>
		</Box>
	)
}

export default ScheduledOrders
