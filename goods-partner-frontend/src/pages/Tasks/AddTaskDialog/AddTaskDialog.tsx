import React, {FC, useEffect, useState} from 'react'
import Box from '@mui/material/Box'
import Modal from '@mui/material/Modal'
import Grid from '@mui/material/Grid'
import {Button, MenuItem, Select, SelectChangeEvent, TextField, Typography} from '@mui/material'
import {Car} from "../../../model/Car";
import {Task} from "../../../model/Task";
import CreateIcon from "@mui/icons-material/Create";
import {ChooseAddressDialog} from "../../../components/ChooseAddressDialog/ChooseAddressDialog";
import MapPoint, {MapPointStatus} from "../../../model/MapPoint";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 510,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    padding: '16px 24px'
}

interface AddTaskDialogProps {
    title: string
    closeDialog: () => void
    open: boolean
    actionHandler: (task: Task) => void
    task: Task
    setTask: (task: Task) => void
    cars: Car[]
}

export const AddTaskDialog: FC<AddTaskDialogProps> = ({
                                                          title,
                                                          closeDialog,
                                                          open,
                                                          actionHandler,
                                                          task,
                                                          setTask,
                                                          cars
                                                      }) => {


    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-')
    const [dateOfExecution, setDateOfExecution] = useState(task.executionDate ?? defaultDate)

    useEffect(()=> {
        if(!task.car) {
            const defaultCar = cars[0];
            setTask({...task, car: defaultCar})
        }
    }, [])

    console.log("task", task);
    console.log("execution", dateOfExecution);

    const [isChooseAddressDialogOpen, setIsChooseAddressDialogOpen] = useState(false);
    const [currentMapPoint, setCurrentMapPoint] = useState<MapPoint>(task.mapPoint ?? {
        status: MapPointStatus.UNKNOWN,
        address: '',
        latitude: 0,
        longitude: 0
    });

    const [textDescription, setTextDescription] = useState(task.description ?? '');

    function handleCar(e: SelectChangeEvent) {
        const carLicencePlate = e.target.value
        const car = cars.find(car => car.licencePlate === carLicencePlate)
        // @ts-ignore
        setTask({...task, car: car})
    }

    const saveHandler = () => {
        const newTask = {
            ...task,
            executionDate: dateOfExecution,
            mapPoint: currentMapPoint,
            description: textDescription
        };

        actionHandler(newTask)
        closeDialog()
    }

    const handleChangeAddress = () => {
        setIsChooseAddressDialogOpen(true);
    }

    return (
        <div>
            <Modal
                open={open}
                onClose={closeDialog}
            >
                <Box sx={style}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <Typography variant='h6' component='h3'>
                                {title}
                            </Typography>
                        </Grid>


                        <Grid item xs={12} display={'flex'}>
                            <TextField
                                sx={{flex: '1'}}
                                type='date'
                                placeholder={'Запланувати дату'}
                                defaultValue={dateOfExecution}
                                onChange={e => setDateOfExecution(e.target.value)}
                            />
                        </Grid>

                        <Grid item xs={12} display={'flex'}>

                            <CarSelect
                                cars={cars}
                                currentCar={task.car}
                                onChange={handleCar}
                            />
                        </Grid>


                        <Grid item xs={12} display={'flex'} alignItems={'center'} justifyContent={'space-between'}>
                            <TextField
                                sx={{flex: '1'}}
                                value={currentMapPoint.address}
                                label="Адреса"
                                required variant="outlined"/>

                            <Button onClick={handleChangeAddress}
                                    startIcon={<CreateIcon/>}/>
                        </Grid>

                        <Grid item xs={12} display={'flex'}>
                            <TextField
                                sx={{flex: '1'}}
                                multiline
                                rows={5}
                                required
                                onChange={e => setTextDescription(e.target.value)}
                                value={textDescription}
                                label='Опис завдання'
                                variant='outlined'
                            />
                        </Grid>
                        <Grid item xs={12} display={'flex'}>
                            <Button disabled variant="contained" component="label">
                                Завантажити документи
                                <input hidden accept="*/*" multiple type="file" />
                            </Button>
                        </Grid>

                        <Grid item xs={12} sx={{mt: 2}}>
                            <Box sx={{display: 'flex', justifyContent: 'flex-end'}}>
                                <Button sx={{mr: 2}} variant='outlined' onClick={closeDialog}>
                                    Скасувати
                                </Button>
                                <Button variant='contained' onClick={saveHandler}>
                                    {' '}
                                    Зберегти{' '}
                                </Button>
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Modal>

            {isChooseAddressDialogOpen && <ChooseAddressDialog isOpen={isChooseAddressDialogOpen}
                                                               setIsOpen={setIsChooseAddressDialogOpen}
                                                               onAction={setCurrentMapPoint}
                                                               defaultAddress={''}
                                                               currentMapPoint={currentMapPoint}/>}
        </div>
    )
}

interface CarSelectProps {
    currentCar?: Car
    cars: Car[]
    onChange: (e: SelectChangeEvent) => void
}

const CarSelect: FC<CarSelectProps> = ({currentCar, cars, onChange}) => {
    const menuItems = cars
        .map(car => (
            <MenuItem key={'carSelect' + car.licencePlate}
                      value={car.licencePlate}>
                {car.name} ({car.licencePlate})
            </MenuItem>
        ))
    // FIXME: Strange shitty 95%
    // FIXME: Add label
    // @ts-ignore
    const defaultCar = cars[0];
    currentCar = currentCar ? currentCar : defaultCar
    return (
        <Select value={currentCar?.licencePlate} onChange={onChange} sx={{flex: '1'}}>
            {menuItems}
        </Select>
    )
}
