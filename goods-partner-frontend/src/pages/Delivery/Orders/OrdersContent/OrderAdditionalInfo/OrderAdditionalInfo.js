import Grid from "@mui/material/Grid";
import {Checkbox, FormControlLabel, MenuItem, Select, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import React from "react";

const AdditionalInfo = ({order, updateOrder}) => {
    const from = order.deliveryStart ?? "09:00";
    const to = order.deliveryFinish ?? "18:00";

    const handleChangeFrom = (event) => {
        const newOrder = {...order, deliveryStart: event.target.value};
        updateOrder(newOrder);
    };
    const handleChangeTo = (event) => {
        const newOrder = {...order, deliveryFinish: event.target.value};
        updateOrder(newOrder);
    };

    const handleFreeze = (event) => {
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
                    <Select
                        value={from}
                        label="Age"
                        onChange={handleChangeFrom}
                        sx={{minWidth: "140px", height: "40px", mr: 1}}
                    >
                        <MenuItem value={"09:00"}>9:00</MenuItem>
                        <MenuItem value={"10:00"}>10:00</MenuItem>
                        <MenuItem value={"11:00"}>11:00</MenuItem>
                        <MenuItem value={"12:00"}>12:00</MenuItem>
                        <MenuItem value={"13:00"}>13:00</MenuItem>
                        <MenuItem value={"14:00"}>14:00</MenuItem>
                        <MenuItem value={"15:00"}>15:00</MenuItem>
                        <MenuItem value={"16:00"}>16:00</MenuItem>
                        <MenuItem value={"17:00"}>17:00</MenuItem>
                        <MenuItem value={"18:00"}>18:00</MenuItem>
                    </Select>
                    {/*TODO: [UI Max] Move select creation into different component*/}
                    <TimeSelect from={from} to={to} value={to} onChange={handleChangeTo}/>
                </Box>
            </Grid>
            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Заморозка
                </Typography>
                <Box>
                    <FormControlLabel onChange={(e) => handleFreeze(e)} checked={order.frozen} control={<Checkbox/>}
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

const TimeSelect = ({from, to, value, onChange}) => {
    const range = (from, to) => Array(to - from + 1)
        .fill(0)
        .map((_, i) => from + i)
        .map(e => <MenuItem value={`${e}:00`}>{`${e}:00`}</MenuItem>);
    return (<Select
        value={value}
        onChange={onChange}
        sx={{minWidth: "140px", height: "40px"}}
    >
        {range}
    </Select>)

}

export default AdditionalInfo;