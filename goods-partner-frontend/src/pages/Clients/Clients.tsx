import {Box, Typography} from '@mui/material'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
import Loading from '../../components/Loading/Loading'
import {useGetClientsAddressesQuery} from "../../api/clients/clients.api";

export const Clients = () => {
	const { data: clientsAddresses, error, isLoading } = useGetClientsAddressesQuery()

	if (isLoading || !clientsAddresses) {
		return <Loading />
	}

	return (
		<section>
			<Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
				<Typography variant='h6' component='h2'>
					Адреси кліентів
				</Typography>

			</Box>
			<Box mt={2}>
				<TableContainer component={Paper}>
					<Table sx={{ minWidth: 650 }} aria-label='simple table'>
						<TableHead sx={{ fontWeight: 'bold' }}>
							<TableRow>
								<TableCell>Клієнт</TableCell>
								<TableCell align='center'>Оригінальна адреса</TableCell>
								<TableCell align='center'>Адреса в системі</TableCell>
								<TableCell align='center'>Статус</TableCell>
							</TableRow>
						</TableHead>

						<TableBody>
							{/*TODO: [Tolik] Fix key*/}
							{clientsAddresses.map(clientAddress => (
								<TableRow key={'clientAddress ' + clientAddress.clientName +  clientAddress.orderAddress}>
									<TableCell>{clientAddress.clientName}</TableCell>
									<TableCell align='center'>{clientAddress.orderAddress}</TableCell>
									<TableCell align='center'>{clientAddress.validAddress}</TableCell>
									<TableCell align='center'>{clientAddress.status}</TableCell>
								</TableRow>
							))}
						</TableBody>
					</Table>
				</TableContainer>
			</Box>
			{error && <ErrorAlert error={error} />}
		</section>
	)
}
