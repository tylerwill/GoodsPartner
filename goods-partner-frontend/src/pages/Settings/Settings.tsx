import {Box, Button, InputAdornment, TextField, Typography} from '@mui/material'
import Paper from '@mui/material/Paper'
import React, {useEffect, useState} from "react";
import Grid from "@mui/material/Grid";
import {useGetSettingsQuery, useUpdateSettingsMutation} from "../../api/settings/settings.api";
import Loading from "../../components/Loading/Loading";
import Divider from "@mui/material/Divider";
import {ConfirmationDialog} from "../../components/ConfirmationDialog/ConfirmationDialog";

export const Settings = () => {
    const {data: settings, error, isLoading} = useGetSettingsQuery()
    const [updateSettings] = useUpdateSettingsMutation()

    const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);

    // client routing properties
    const [maxRouteTimeMinutes, setMaxRouteTimeMinutes] = useState(0);
    const [unloadingTimeMinutes, setUnloadingTimeMinutes] = useState(0);
    const [maxTimeProcessingSolutionSeconds, setMaxTimeProcessingSolutionSeconds] = useState(0);
    const [depotStartTime, setDepotStartTime] = useState('');
    const [depotFinishTime, setDepotFinishTime] = useState('');
    const [defaultDeliveryStartTime, setDefaultDeliveryStartTime] = useState('');
    const [defaultDeliveryFinishTime, setDefaultDeliveryFinishTime] = useState('');

    // keywords
    const [prePacking, setPrePacking] = useState([] as string []);
    const [selfService, setSelfService] = useState([] as string []);
    const [postal, setPostal] = useState([] as string []);


    useEffect(() => {
        if (settings) {
            setMaxRouteTimeMinutes(settings.clientRoutingProperties.maxRouteTimeMinutes);
            setMaxTimeProcessingSolutionSeconds(settings.clientRoutingProperties.maxTimeProcessingSolutionSeconds);
            setUnloadingTimeMinutes(settings.clientRoutingProperties.unloadingTimeMinutes);
            setDepotStartTime(settings.clientRoutingProperties.depotStartTime);
            setDepotFinishTime(settings.clientRoutingProperties.depotFinishTime);
            setDefaultDeliveryStartTime(settings.clientRoutingProperties.defaultDeliveryStartTime);
            setDefaultDeliveryFinishTime(settings.clientRoutingProperties.defaultDeliveryFinishTime);

            setPrePacking(settings.clientBusinessProperties.prePacking.keywords);
            setSelfService(settings.clientBusinessProperties.selfService.keywords);
            setPostal(settings.clientBusinessProperties.postal.keywords);
        }
    }, [isLoading, settings])

    if (isLoading || !settings) {
        return <Loading/>
    }


    const updateSettingsHandler = () => {
        const newClientRoutingProperties = {
            ...settings.clientRoutingProperties,
            maxRouteTimeMinutes,
            unloadingTimeMinutes,
            maxTimeProcessingSolutionSeconds,
            depotStartTime,
            depotFinishTime,
            defaultDeliveryStartTime,
            defaultDeliveryFinishTime
        }

        const newClientBusinessProperties = {
            prePacking: {
                ...settings.clientBusinessProperties.prePacking,
                keywords: prePacking
            },
            selfService: {
                ...settings.clientBusinessProperties.selfService,
                keywords: selfService
            },
            postal: {
                ...settings.clientBusinessProperties.postal,
                keywords: postal
            },
        }

        const newSettings = {
            ...settings,
            clientRoutingProperties: newClientRoutingProperties,
            clientBusinessProperties: newClientBusinessProperties
        };

        updateSettings(newSettings);

    }

    return (
        <section>
            <Box sx={{display: 'flex', justifyContent: 'space-between'}}>
                <Typography variant='h6' component='h2'>
                    Налаштування
                </Typography>

                <Button variant='contained' onClick={() => setIsConfirmDialogOpen(true)}>
                    Зберегти
                </Button>
            </Box>
            <Box mt={2}>
                <Paper variant={'outlined'} sx={{p: 4}}>
                    <Typography variant='h6' component='h2'>
                        Логістика
                    </Typography>

                    <Grid container spacing={2} sx={{mt: 1}}>
                        <Grid item xs={3}>
                            <TextField required
                                       InputProps={{
                                           endAdornment: <InputAdornment position="start">хв.</InputAdornment>,
                                       }}
                                       label={"Максимальний час на маршрут"}
                                       sx={{width: '100%'}}
                                       variant={"outlined"}
                                       value={maxRouteTimeMinutes}
                                       onChange={(e) => setMaxRouteTimeMinutes(+e.target.value)}
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <TextField required
                                       InputProps={{
                                           endAdornment: <InputAdornment position="start">хв.</InputAdornment>,
                                       }}
                                       label={"Середній час розвантаження"}
                                       sx={{width: '100%'}}
                                       value={unloadingTimeMinutes}
                                       onChange={(e) =>
                                           setUnloadingTimeMinutes(+e.target.value
                                           )}

                            />
                        </Grid>
                        <Grid item xs={3}>
                            <TextField required
                                       label={"Максимальний час розрахування маршруту"}
                                       sx={{width: '100%'}}
                                       InputProps={{
                                           endAdornment: <InputAdornment position="start">сек.</InputAdornment>,
                                       }}
                                       value={maxTimeProcessingSolutionSeconds}
                                       onChange={(e) =>
                                           setMaxTimeProcessingSolutionSeconds(+e.target.value)
                                       }
                            />
                        </Grid>


                    </Grid>


                    <Grid container spacing={2} sx={{mt: 1}}>
                        <Grid item xs={3}>
                            <TextField required

                                       label={"Виїзд зі складу"}
                                       sx={{width: '100%'}}
                                       value={depotStartTime}
                                       onChange={(e) => setDepotStartTime(e.target.value)}
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <TextField required

                                       label={"Повернення на склад"}
                                       sx={{width: '100%'}}
                                       value={depotFinishTime}
                                       onChange={(e) => setDepotFinishTime(e.target.value)}
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <TextField required
                                       label={"Початок робочої зміни"}
                                       sx={{width: '100%'}}
                                       value={defaultDeliveryStartTime}
                                       onChange={(e) => setDefaultDeliveryStartTime(e.target.value)}

                            />
                        </Grid>
                        <Grid item xs={3}>
                            <TextField required
                                       label={"Кінець робочої зміни"}
                                       sx={{width: '100%'}}
                                       value={defaultDeliveryFinishTime}
                                       onChange={(e) => setDefaultDeliveryFinishTime(e.target.value)}
                            />
                        </Grid>
                    </Grid>

                    <Divider sx={{mt: 3, mb: 3}}/>

                    <Typography variant='h6' component='h2'>
                        Ключові слова для аналізу коментарів
                    </Typography>


                    <Grid container spacing={2} sx={{mt: 1, mb: 1}}>

                        <Grid item xs={6}>
                            <TextField required
                                       label={"Пошта"}
                                       sx={{width: '100%'}}
                                       variant={"outlined"}
                                       value={postal.join()}
                                       onChange={(e) => setPostal(e.target.value.split(","))}

                            />
                        </Grid>


                        <Grid item xs={6}>
                            <TextField required
                                       label={"Самовивіз"}
                                       sx={{width: '100%'}}
                                       variant={"outlined"}
                                       value={selfService.join()}
                                       onChange={(e) => setSelfService(e.target.value.split(","))}

                            />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField required
                                       label={"Фасовка"}
                                       sx={{width: '100%'}}
                                       variant={"outlined"}
                                       value={prePacking.join()}
                                       onChange={(e) => setPrePacking(e.target.value.split(","))}

                            />
                        </Grid>

                    </Grid>
                </Paper>
            </Box>

            {
                isConfirmDialogOpen && <ConfirmationDialog
                    title={"Змінити налаштування"}
                    text={"Ви впевнені, що бажаєте змінити налаштування?"}
                    open={isConfirmDialogOpen}
                    setOpen={setIsConfirmDialogOpen}
                    onAction={updateSettingsHandler}
                />
            }
        </section>
    )
}

