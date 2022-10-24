import React from 'react';
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

export default function CarFormDialog({closeDialog, open, actionHandler, car, setCar}) {
    function handleACooler(e) {
        setCar({...car, cooler: e.target.checked});
    }

    function handleAvailable(e) {
        setCar({...car, available: e.target.checked});
    }

    const saveHandler = () => {
        actionHandler(car);
        closeDialog();
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
                            <TextField id="outlined-basic"
                                       value={car.name}
                                       required
                                       onChange={(e) => setCar({...car, name: e.target.value})}
                                       label="Модель авто"
                                       variant="outlined"/>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic"
                                       value={car.licencePlate}
                                       onChange={(e) => setCar({...car, licencePlate: e.target.value})}
                                       required label="Номер авто" variant="outlined"/>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField id="outlined-basic"
                                       value={car.driver}
                                       onChange={(e) => setCar({...car,driver: e.target.value})} label="Водій"
                                       required variant="outlined"/>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField id="outlined-basic"
                                       value={car.weightCapacity}
                                       onChange={(e) => setCar({...car, weightCapacity: e.target.value})}
                                       required label="Вантажопідйомність, т" variant="outlined"/>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField id="outlined-basic"
                                       value={car.travelCost}
                                       onChange={(e) => setCar({...car, travelCost: e.target.value})}
                                       required label="Витрати палива, л/100км" variant="outlined"/>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <FormGroup>
                                <FormControlLabel control={<Checkbox/>} checked={car.cooler}
                                                  onChange={handleACooler}
                                                  label="Морозильна камера"/>

                                <FormControlLabel control={<Checkbox/>} checked={car.available}
                                                  onChange={handleAvailable} label="Доступність автомобіля"/>
                            </FormGroup>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <Box sx={{display: 'flex', justifyContent: 'flex-end'}}>
                                <Button sx={{mr: 2}} variant="outlined" onClick={closeDialog}>Скасувати</Button>
                                <Button variant="outlined" onClick={saveHandler}> Зберегти </Button>
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Modal>
        </div>
    );
}