import React, {useState} from "react";
import {Box, Button, Card, CardContent, TextField, Typography} from "@mui/material";

const Orders = () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-');
    const [calculationDate, setCalculationDate] = useState(defaultDate);

    return <section>
        <Box>
            <Typography variant="h6" component="h2">
                Замовлення
            </Typography>
            <Box sx={{mt: 2, display: 'flex', alignItems: 'center'}}>
                <TextField
                    sx={{minWidth: '200px'}}
                    id="date"
                    type="date"
                    size="small"
                    defaultValue={calculationDate}
                    InputLabelProps={{
                        shrink: true,
                    }}
                    onChange={(e) => setCalculationDate(e.target.value)}/>
                <Button sx={{ml: 2}} variant={'contained'}>Показати</Button>
            </Box>

            <Box sx={{mt: 4}}>
                <Card variant="outlined">
                    <CardContent sx={{
                        minHeight: 'calc(100vh - 280px)', display: 'flex',
                        justifyContent: 'center', alignItems: 'center'
                    }}>

                        <Typography sx={{textTransform: 'uppercase'}} color="text.secondary">
                            Для відображення інформації Оберіть дату та натисніть "ПОКАЗАТИ"
                        </Typography>

                    </CardContent>
                </Card>
            </Box>
        </Box>
    </section>
}

export default Orders;
