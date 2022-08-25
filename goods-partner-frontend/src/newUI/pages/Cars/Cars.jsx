import React from "react";
import {Box, Button, ListItemIcon, ListItemText, Menu, MenuItem, Typography} from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import IconButton from "@mui/material/IconButton";
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteForeverOutlinedIcon from '@mui/icons-material/DeleteForeverOutlined';
import CarFormDialog from "./CarFormDialog/CarFormDialog";

const Cars = ({cars, openDialog, closeDialog, isDialogOpened}) => {
    return <section>
        <Box sx={{display: 'flex', justifyContent: 'space-between'}}>
            <Typography variant="h6" component="h2">
                Автомобілі
            </Typography>

            <Button onClick={openDialog} variant="contained">Додати авто</Button>

        </Box>
        <Box mt={2}>
            <TableContainer component={Paper}>
                <Table sx={{minWidth: 650}} aria-label="simple table">
                    <TableHead sx={{fontWeight: 'bold'}}>
                        <TableRow>
                            <TableCell>Модель авто</TableCell>
                            <TableCell align="center">Номер авто</TableCell>
                            <TableCell align="left">Водій</TableCell>
                            <TableCell align="center">Вантажопідйомність, т</TableCell>
                            <TableCell align="center">Витрати палива, л/100км</TableCell>
                            <TableCell align="center">Морозильна камера</TableCell>
                            <TableCell align="center">Доступність</TableCell>
                            <TableCell/>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {/*// TODO: [Tolik] Fix key*/}
                        {cars.map((car) => (
                            <TableRow key={"tableCarId " + car.id}>
                                <TableCell>{car.name}</TableCell>
                                <TableCell align="center">{car.licencePlate}</TableCell>
                                <TableCell>{car.driver}</TableCell>
                                <TableCell align="center">{car.weightCapacity}</TableCell>
                                <TableCell align="center">{car.travelCost}</TableCell>
                                <TableCell align="center">{car.cooler ? <CheckIcon/> : <CloseIcon/>}</TableCell>
                                <TableCell align="center">{car.available ? <CheckIcon/> : <CloseIcon/>}</TableCell>
                                <TableCell><BasicMenu/></TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
        <CarFormDialog closeDialog={closeDialog} open={isDialogOpened}/>
    </section>
}

function BasicMenu() {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    return (
        <div>
            <IconButton
                aria-label="more"
                id="long-button"
                aria-controls={open ? 'long-menu' : undefined}
                aria-expanded={open ? 'true' : undefined}
                aria-haspopup="true"
                onClick={handleClick}
            >
                <MoreVertIcon/>
            </IconButton>
            <Menu
                id="basic-menu"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                MenuListProps={{
                    'aria-labelledby': 'basic-button',
                }}
            >
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <EditOutlinedIcon/>
                    </ListItemIcon>
                    <ListItemText>Редагувати</ListItemText>
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <DeleteForeverOutlinedIcon sx={{color: '#D32F2F'}}/>
                    </ListItemIcon>
                    <ListItemText sx={{color: '#D32F2F'}}>Видалити</ListItemText>
                </MenuItem>
            </Menu>
        </div>
    );
}

export default Cars;
