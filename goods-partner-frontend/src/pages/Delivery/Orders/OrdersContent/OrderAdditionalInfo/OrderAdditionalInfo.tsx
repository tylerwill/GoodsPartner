import Grid from "@mui/material/Grid";
import {Checkbox, FormControlLabel, MenuItem, Select, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import React from "react";
import Order from "../../../../../model/Order";

interface AdditionalInfoProps {
    order: Order,
    updateOrder: (order: Order) => void
}

const AdditionalInfo = ({order, updateOrder}: AdditionalInfoProps) => {
    const from = order.deliveryStart ?? "09:00";
    const to = order.deliveryFinish ?? "18:00";

    const handleChangeFrom = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newOrder = {...order, deliveryStart: event.target.value};
        updateOrder(newOrder);
    }

    const handleChangeTo = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newOrder = {...order, deliveryFinish: event.target.value};
        updateOrder(newOrder);
    };

    const handleFreeze = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newOrder = {...order, frozen: event.target.checked};
        updateOrder(newOrder);
    };

    return (
        <Grid sx={{mt: 2, p: 2, background: 'rgba(0, 0, 0, 0.02)', borderRadius: '6px'}} container spacing={2}>

            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Діапазон доставки
                </Typography>
                <Box>
                    <TimeSelect from={9} to={19} value={from} onChange={handleChangeFrom}/>
                    <TimeSelect from={9} to={19} value={to} onChange={handleChangeTo}/>
                </Box>
            </Grid>

            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Заморозка
                </Typography>
                <Box>
                    <FormControlLabel checked={order.frozen} control={<Checkbox onChange={handleFreeze}/>}
                                      label="Потребує заморозки"/>
                </Box>
            </Grid>

            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Коментар
                </Typography>
                <Typography variant="caption" display="block" gutterBottom>
                    {order.comment?.length === 0 ? '-' : order.comment}
                </Typography>
            </Grid>
        </Grid>)
}

interface TimeSelectProperties {
    from: number,
    to: number,
    value: string,
    onChange: (e: any) => void
}

const TimeSelect = ({from, to, value, onChange}: TimeSelectProperties) => {
    const menuItems = range(from, to)
        .map(e => String(e))
        // transform 9:00 -> 09:00
        .map(e => e.length === 1 ? "0" + e : e)
        .map(e => <MenuItem key={value + e} value={`${e}:00`}>{`${e}:00`}</MenuItem>);
    return (<Select
        value={value}
        onChange={onChange}
        sx={{minWidth: "140px", height: "40px", mr: 1}}
    >
        {menuItems}
    </Select>)
}

const range = (from: number, to: number) => Array(to - from + 1)
    .fill(0)
    .map((_, i) => from + i);


export default AdditionalInfo;