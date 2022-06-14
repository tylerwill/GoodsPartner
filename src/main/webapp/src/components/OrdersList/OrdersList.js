import Box from '@mui/material/Box';
import {Button, Card, CardActions, CardContent, Collapse, Stack, TextField, Typography} from "@mui/material";
import Grid from "@mui/material/Grid";
import OrdersTable from "../OrdersTable/OrdersTable";

function OrdersList() {
    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    <Typography variant="h5" gutterBottom component="div">
                        Список замовлень на 14.06.2022
                    </Typography>
                    <Box sx={{flexGrow: 1}}>
                        <Grid container spacing={2}>
                            <Grid item xs={3}>
                                <Stack spacing={0.5}>
                                    <Button size="small" variant="contained">Замовлення №1</Button>
                                    <Button size="small" variant="outlined"> Замовлення №2 </Button>
                                    <Button size="small" variant="outlined">Замовлення №3</Button>
                                    <Button size="small" variant="outlined"> Замовлення №4 </Button>
                                </Stack>
                            </Grid>
                            <Grid item xs={9}>
                                <Stack spacing={2}>
                                    <Typography variant="h6" component="h1">

                                                <Stack>
                                                    <Typography variant="h6" gutterBottom component="div">
                                                        Замовлення №1 від 12.06.2022
                                                    </Typography>
                                                    <Grid container spacing={2}>
                                                        <Grid item xs={4}>
                                                            <Typography variant="body2" gutterBottom>
                                                                Клієнт:
                                                            </Typography>
                                                            <Typography variant="body2" gutterBottom>
                                                                ТОВ "Пекарня"
                                                            </Typography>
                                                        </Grid>
                                                        <Grid item xs={4}>
                                                            <Typography variant="body2" gutterBottom>
                                                                Адреса:
                                                            </Typography>
                                                            <Typography variant="body2" gutterBottom>
                                                                Київ, вул. Металістів, 13
                                                            </Typography>
                                                        </Grid>
                                                        <Grid item xs={4}>
                                                            <Typography variant="body2" gutterBottom>
                                                                Менеджер:
                                                            </Typography>

                                                            <Typography variant="body2" gutterBottom>
                                                                Винник О.О.
                                                            </Typography>
                                                        </Grid>
                                                    </Grid>

                                                    <OrdersTable/>
                                                </Stack>

                                    </Typography>
                                </Stack>
                            </Grid>
                        </Grid>
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    );
}

export default OrdersList;
