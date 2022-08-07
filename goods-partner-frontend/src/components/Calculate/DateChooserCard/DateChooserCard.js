import {AppBar, Button, Stack, TextField, Toolbar, Typography} from "@mui/material";
import {useState} from "react";
import {NavLink} from "react-router-dom";

function DateChooserCard({getCalculatedDataByDate}) {

    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-');
    const [calculationDate, setCalculationDate] = useState(defaultDate);
    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" gutterBottom component="div" sx={{flexGrow: 1}}>
                    <img src="https://images.prom.ua/2143227305_w350_h100_ingrediyenti-dlya-pekariv.jpg"/>
                </Typography>
                <Stack direction="row"
                       spacing={2}>
                    <Typography variant="h6" gutterBottom component="div">
                        Вкажіть дату:
                    </Typography>
                    <TextField
                        id="date"
                        type="date"
                        size="small"
                        defaultValue={defaultDate}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        onChange={(e) => setCalculationDate(e.target.value)}/>
                    <Button variant="contained"
                            onClick={() => {
                                getCalculatedDataByDate(calculationDate)
                            }}>
                        Розрахувати
                    </Button>
                    <NavLink to={"/cars"}>
                        <Button variant="contained" style={{width: '11em'}}>
                            МАШИНИ
                        </Button>
                    </NavLink>
                </Stack>
            </Toolbar>
        </AppBar>
    );
}

export default DateChooserCard;
