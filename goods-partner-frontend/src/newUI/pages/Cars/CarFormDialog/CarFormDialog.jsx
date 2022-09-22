import React from 'react';
import Box from '@mui/material/Box';
import Modal from '@mui/material/Modal';
import Grid from '@mui/material/Grid';
import {Button, Checkbox, FormControlLabel, FormGroup, TextField, Typography} from '@mui/material';
import {addCarThunkCreator} from "../../../reducers/cars-reducer";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 510,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    padding: '16px 24px',
};

export default function CarFormDialog({closeDialog, open}) {

    let carName = React.createRef();
    let carLicencePlate = React.createRef();
    let carDriver = React.createRef();
    let carWeighCapacity = React.createRef();
    let carTravelCost = React.createRef();
    let carCooler = React.createRef();
    let carAvailable = React.createRef();

    let addCar = () => {
        debugger;
        let name = carName.current.value;
        let licencePlate = carLicencePlate.current.value;
        let driver = carDriver.current.value;
        let weightCapacity = carWeighCapacity.current.value;
        let travelCost = carTravelCost.current.value;
        let cooler = carCooler.current.value;
        let available = carAvailable.current.value;

        let car = {name, licencePlate, driver, weightCapacity, travelCost, cooler, available};
        addCarThunkCreator(car)
    }

    return (
        <div>
            <Modal
                open={open}
                onClose={closeDialog}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box sx={style}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <Typography variant="h6" component="h3">
                                Додати авто
                            </Typography>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic" inputRef={carName} label="Модель авто" variant="outlined"/>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic" inputRef={carLicencePlate} label="Номер авто"
                                       variant="outlined"/>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField id="outlined-basic" inputRef={carDriver} label="Водій" variant="outlined"/>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic" inputRef={carWeighCapacity} label="Вантажопідйомність, т"
                                       variant="outlined"/>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField id="outlined-basic" inputRef={carTravelCost} label="Витрати палива, л/100км"
                                       variant="outlined"/>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <FormGroup>
                                <FormControlLabel control={<Checkbox/>} inputRef={carCooler} label="Морозильна камера"/>
                                <FormControlLabel control={<Checkbox defaultChecked/>} inputRef={carAvailable}
                                                  label="Доступність автомобіля"/>
                            </FormGroup>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <Box sx={{display: 'flex', justifyContent: 'flex-end'}}>
                                <Button sx={{mr: 2}} variant="outlined" onClick={closeDialog}>Скасувати</Button>
                                <Button variant="outlined" onClick={addCar}> Зберегти </Button>
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Modal>
        </div>
    );
}