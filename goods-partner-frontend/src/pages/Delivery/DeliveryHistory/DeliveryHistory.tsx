import React from 'react'
import Box from '@mui/material/Box'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import { useParams } from 'react-router-dom'
import Loading from '../../../components/Loading/Loading'
import { useGetHistoryForDeliveryQuery } from '../../../api/history/history.api'

const DeliveryHistory = () => {
	const { deliveryId } = useParams()

	const { data: historyForDelivery, isLoading } = useGetHistoryForDeliveryQuery(
		String(deliveryId)
	)

	if (isLoading) {
		return <Loading />
	}

	return (
		<Box sx={{ paddingLeft: '24px', paddingRight: '24px' }}>
			<TableContainer
				component={Paper}
				style={{
					borderTop: '1px solid rgba(0, 0, 0, 0.1)'
				}}
			>
				<Table sx={{ minWidth: 650 }} size='small' aria-label='a dense table'>
					<TableHead>
						<TableRow>
							<TableCell sx={{ minWidth: '170px' }}>Дата</TableCell>
							<TableCell>Подія</TableCell>
							<TableCell align='right'>Користувач</TableCell>
							<TableCell align='right'>Роль</TableCell>
						</TableRow>
					</TableHead>
					<TableBody>
						{historyForDelivery?.map(history => (
							<TableRow
								key={history.id}
								sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
							>
								<TableCell component='th' scope='row'>
									{history.createdAt.replace('T', ' ')}
								</TableCell>
								<TableCell>{history.action}</TableCell>
								<TableCell align='right'>{history.userEmail}</TableCell>
								<TableCell align='right'>{history.role}</TableCell>
							</TableRow>
						))}
					</TableBody>
				</Table>
			</TableContainer>
		</Box>
	)
}

export default DeliveryHistory
