import {Box, Button, Chip, Input, Typography} from '@mui/material'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
import Loading from '../../components/Loading/Loading'
import {useGetClientsAddressesQuery, useUpdateClientAddressMutation} from "../../api/clients/clients.api";
import CreateIcon from '@mui/icons-material/Create';
import React, {useEffect, useState} from "react";
import TablePagination from "@mui/material/TablePagination";
import {ChooseAddressDialog} from "../../components/ChooseAddressDialog/ChooseAddressDialog";
import MapPoint, {MapPointStatus} from "../../model/MapPoint";
import {ClientAddress} from "../../model/ClientAddress";
import {OverridableStringUnion} from "@mui/types";
import {ChipPropsColorOverrides} from "@mui/material/Chip/Chip";
import TextField from "@mui/material/TextField";

export const Clients = () => {
    const {data: clientsAddresses, error, isLoading} = useGetClientsAddressesQuery()
    const [updateClientAddress] = useUpdateClientAddressMutation();
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowsPerPage] = useState(25)
    const [isChooseAddressDialogOpen, setIsChooseAddressDialogOpen] = useState(false);
    const [currentMapPoint, setCurrentMapPoint] = useState<MapPoint>({
        status: MapPointStatus.UNKNOWN,
        address: '',
        latitude: 0,
        longitude: 0
    });

    const [filteredClients, setFilteredClients] = useState(clientsAddresses);

    useEffect(() => {
        setFilteredClients(clientsAddresses);
    }, [clientsAddresses]);

    const [clientAddressToUpdate, setClientAddressToUpdate] = useState({} as ClientAddress);

    const handleChangePage = (event: any, newPage: number) => {
        setPage(newPage)
    }

    const handleChangeRowsPerPage = (event: React.BaseSyntheticEvent) => {
        setRowsPerPage(parseInt(event.target.value, 10))
        setPage(0)
    }

    if (isLoading || !filteredClients || !clientsAddresses) {
        return <Loading/>
    }


    const filter = (event: React.ChangeEvent<HTMLInputElement>) => {
        const filterValue = event.target.value.toLowerCase();
        if (filterValue.length >= 2) {
            const newFilteredClients = clientsAddresses
                .filter(client => client.orderAddress?.toLowerCase().includes(filterValue)
                    || client.clientName?.toLowerCase().includes(filterValue)
                    ||  client.mapPoint?.address?.toLowerCase().includes(filterValue));
            setFilteredClients(newFilteredClients);
        }else {
            setFilteredClients(clientsAddresses);
        }
    }

    const handleUpdateAddress = (point: MapPoint) => {
        updateClientAddress({...clientAddressToUpdate, mapPoint: point});
    };

    const handleChangeAddress = (clientAddress: ClientAddress) => {
        const clientIdentifier = filteredClients
            .filter(c => clientAddress.clientName === c.clientName)
            .find(c => clientAddress.orderAddress === c.orderAddress);

        setClientAddressToUpdate(clientIdentifier!);
        setIsChooseAddressDialogOpen(true);
        setCurrentMapPoint(clientIdentifier!.mapPoint);
    }

    return (
        <>
            <Box sx={{display: 'flex', justifyContent: 'space-between'}}>
                <Typography variant='h6' component='h2'>
                    Адреси кліентів
                </Typography>
                <TextField
                    sx={{backgroundColor: '#fff', width:'30%'}}
                    onChange={filter}
                    size={"small"}
                    label="Пошук" variant="outlined"/>
            </Box>
            <Box mt={2}>
                <Paper variant={'outlined'}>
                    <TableContainer>
                        <Table sx={{minWidth: 650}} aria-label='simple table'>
                            <TableHead sx={{fontWeight: 'bold'}}>
                                <TableRow>
                                    <TableCell>Клієнт</TableCell>
                                    <TableCell>Оригінальна адреса</TableCell>
                                    <TableCell>Адреса в системі</TableCell>
                                    <TableCell align='center'>Статус</TableCell>
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {/*TODO: [Tolik] Fix key*/}
                                {filteredClients
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map(clientAddress => (
                                        <TableRow
                                            key={'clientAddress ' + clientAddress.clientName + clientAddress.orderAddress}>
                                            <TableCell>{clientAddress.clientName}</TableCell>
                                            <TableCell>{clientAddress.orderAddress}</TableCell>
                                            <TableCell><Box sx={{display: 'flex', alignItems: 'center'}}>
                                                <Button onClick={() => handleChangeAddress(clientAddress)}
                                                        startIcon={<CreateIcon/>}/> {clientAddress.mapPoint.address}
                                            </Box>
                                            </TableCell>
                                            <TableCell align='center'>
                                                {AddressStatusChip(clientAddress.mapPoint.status)}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <TablePagination
                        rowsPerPageOptions={[25, 50, 100]}
                        component='div'
                        count={filteredClients.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                    />
                </Paper>
            </Box>
            {error && <ErrorAlert error={error}/>}
            {isChooseAddressDialogOpen && <ChooseAddressDialog isOpen={isChooseAddressDialogOpen}
                                                               setIsOpen={setIsChooseAddressDialogOpen}
                                                               onAction={(point) => handleUpdateAddress(point)}
                                                               defaultAddress={clientAddressToUpdate.orderAddress}
                                                               currentMapPoint={currentMapPoint}/>}
        </>
    )
}


const AddressStatusChip = (status: MapPointStatus) => {
    let text
    let color: OverridableStringUnion<'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning',
        ChipPropsColorOverrides>;

    switch (status) {
        case MapPointStatus.AUTOVALIDATED : {
            text = 'Авто'
            color = 'success'
            break
        }

        case MapPointStatus.KNOWN : {
            text = 'Вручну'
            color = 'default'
            break
        }

        case MapPointStatus.UNKNOWN : {
            text = 'Невідомий'
            color = 'error'
            break
        }
    }

    return (
        <Chip
            label={text}
            sx={{color: '#000', borderWidth: '2px'}}
            color={color}
            variant='outlined'
        />
    )
}