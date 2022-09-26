import React, {useState} from 'react';
import Box from '@mui/material/Box';
import Modal from '@mui/material/Modal';
import Grid from '@mui/material/Grid';
import {Button, Checkbox, FormControlLabel, FormGroup, TextField, Typography} from '@mui/material';

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

export default function CarFormDialog({closeDialog, open, addCar}) {

    const [name, setName] = useState('');
    const [licencePlate, setLicencePlate] = useState('');
    const [driver, setDriver] = useState('');
    const [weightCapacity, setWeightCapacity] = useState('');
    const [travelCost, setTravelCost] = useState('');
    const [cooler, setCooler] = useState(true);
    const [available, setAvailable] = useState(true);

    function handleACooler(e) {
        setCooler(e.target.checked);
    };

    function handleAvailable(e) {
        setAvailable(e.target.checked);
    }

    let addCarHandler = () => {
        const car = {name, licencePlate, driver, weightCapacity, travelCost, cooler, available};
        const result = addCar(car)
        console.log("function", result);
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
                            <TextField id="outlined-basic" onChange={(e) => setName(e.target.value)} label="Модель авто"
                                       variant="outlined"/>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic" onChange={(e) => setLicencePlate(e.target.value)}
                                       label="Номер авто" variant="outlined"/>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField id="outlined-basic" onChange={(e) => setDriver(e.target.value)} label="Водій"
                                       variant="outlined"/>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic" onChange={(e) => setWeightCapacity(e.target.value)}
                                       label="Вантажопідйомність, т" variant="outlined"/>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField id="outlined-basic" onChange={(e) => setTravelCost(e.target.value)}
                                       label="Витрати палива, л/100км" variant="outlined"/>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <FormGroup>
                                <FormControlLabel control={<Checkbox/>} checked={cooler} onChange={handleACooler}
                                                  label="Морозильна камера"/>
                                <FormControlLabel control={<Checkbox defaultChecked/>} checked={available}
                                                  onChange={handleAvailable} label="Доступність автомобіля"/>
                            </FormGroup>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <Box sx={{display: 'flex', justifyContent: 'flex-end'}}>
                                <Button sx={{mr: 2}} variant="outlined" onClick={closeDialog}>Скасувати</Button>
                                <Button variant="outlined" onClick={addCarHandler}> Зберегти </Button>
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Modal>
        </div>
    );
}