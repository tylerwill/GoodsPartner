import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import * as React from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableRow from '@mui/material/TableRow';
import {Button, FormControl, MenuItem, Modal, Select, TableCell} from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import RouteMap from "../RoutMap/RouteMap";
import {useLoadScript} from "@react-google-maps/api";

const RouteDetails = ({
                          date,
                          routeAddresses,
                          changeRouteStatus,
                          updateRoute,
                          route,
                          route: {
                              id,
                              distance,
                              estimatedTime,
                              finishTime,
                              spentTime,
                              startTime,
                              status,
                              totalOrders,
                              totalPoints,
                              totalWeight,
                              storeName,
                              storeAddress,
                          }
                      }) => {

    const style = {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 900,
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
    };

    const [open, setOpen] = React.useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const {isLoaded} = useLoadScript({
        googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_VAR
    })
    debugger;
    const handleChange = (event) => {
        const newRoute = {...route};
        newRoute.status = event.target.value;
        updateRoute(newRoute);
    };

    const createStatusSelect = (routeId) => {
        let background;

        if (status === "DRAFT") {
            background = "#1976d2";
        } else if (status === "APPROVED") {
            background = "#2e7d32";
        } else if (status === "IN_PROGRESS") {
            background = "#ffab00";
        } else if (status === "COMPLETED") {
            background = "#2e7d32";
        } else {
            background = '#A9A9A9';
        }

        return (<FormControl sx={{m: 1}}>
            <Select
                sx={{
                    textAlign: 'center',
                    backgroundColor: background,
                    color: '#fff',
                    textTransform: 'UPPERCASE',
                    minWidth: '150px',
                    fontSize: '13px'
                }}
                value={status}
                onChange={(event) => handleChange(event)}
                inputProps={{'aria-label': 'Without label'}}
            >
                <MenuItem value={'DRAFT'}>Новий</MenuItem>
                <MenuItem value={'APPROVED'}>Затвердженний</MenuItem>
                <MenuItem value={'IN_PROGRESS'}>В роботі</MenuItem>
                <MenuItem value={'COMPLETED'}>Завершений</MenuItem>
                <MenuItem value={'INCOMPLETE'}>Незавершений</MenuItem>
            </Select>
        </FormControl>);
    }

    const statusSelect = createStatusSelect();
    const estimatedTimeHours = estimatedTime && (estimatedTime / 60)
        .toLocaleString(undefined, {maximumFractionDigits: 0}) + " год.";

    const estimatedTimeSeconds = estimatedTime && (estimatedTime % 60)
        .toLocaleString(undefined, {maximumFractionDigits: 0}) + " хв.";

    const estimatedTimeString  = estimatedTimeHours + " " + estimatedTimeSeconds;

    return (
        <TableContainer component={Paper} sx={{marginTop: '10px'}}>
            <Table aria-label="spanning table">
                <TableBody>
                    <TableRow>
                        <TableCell align="left">
                            Маршрут №{id} від {date}
                        </TableCell>
                        <TableCell align="left">
                            {statusSelect}
                        </TableCell>
                        <TableCell align="left">
                            {
                                isLoaded &&
                                <>
                                    <Button onClick={handleOpen}>Показати на карті</Button>
                                    <Modal
                                        open={open}
                                        onClose={handleClose}
                                        aria-labelledby="modal-modal-title"
                                        aria-describedby="modal-modal-description"
                                    >
                                        <Box sx={style}>
                                            <Typography id="modal-modal-title" variant="h6" component="h2">
                                                Route #{id}
                                            </Typography>
                                                <RouteMap
                                                    addresses={
                                                        routeAddresses
                                                            .filter(routeAdd => routeAdd.routeId === id)
                                                            .map(route => route.addresses)
                                                    }
                                                />
                                        </Box>
                                    </Modal>
                                </>
                            }
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell align="left">
                            Кількість адрес: {totalPoints}
                        </TableCell>
                        <TableCell align="left">
                            Кількість замовлень: {totalOrders}
                        </TableCell>
                        <TableCell align="left">
                            Загальна маса: {totalWeight}
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell align="left">
                            Склад: {storeName}
                        </TableCell>
                        <TableCell colSpan={2} align="left">
                            Адреса складу: {storeAddress}
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell align="left">
                            Автомобіль: {route.car.name}
                        </TableCell>
                        <TableCell align="left">
                            Номер: {route.car.licencePlate}
                        </TableCell>
                        <TableCell align="left">
                            Вантажність: {route.car.weightCapacity}
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell colSpan={3} align="left">
                            Відстань: {distance} км
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell colSpan={3} align="left">
                            Розрахунковий час виконання: {estimatedTimeString}
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell colSpan={3} align="left">
                            Початок виконання: {startTime && startTime.toLocaleTimeString('en-US', {
                            hour12: false,
                            hour: "numeric",
                            minute: "numeric"
                        })}
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell colSpan={3} align="left">
                            Кінець виконання: {finishTime && finishTime.toLocaleTimeString('en-US', {
                            hour12: false,
                            hour: "numeric",
                            minute: "numeric"
                        })}
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell colSpan={3} align="left">
                            Фактичний час виконання: {spentTime && (spentTime / 1000 / 60)
                            .toLocaleString(undefined, {maximumFractionDigits: 2}) + " хв."}
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    );
}

export default RouteDetails;