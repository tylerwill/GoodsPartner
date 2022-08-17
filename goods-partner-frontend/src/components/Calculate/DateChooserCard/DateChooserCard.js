import {AppBar, Button, Stack, TextField, Toolbar, Typography} from "@mui/material";
import {useState} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import Box from "@mui/material/Box";


// , borderBottom: '1px solid #ccc'
function DateChooserCard({getCalculatedDataByDate}) {

    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-');
    const [calculationDate, setCalculationDate] = useState(defaultDate);

    let navigate = useNavigate();


    return (
        <AppBar position="static" style={{background: '#fff'}}>
            <Toolbar>
                <Box component="div" sx={{flexGrow: 1}}>
                    <NavLink to={"/"}>
                        <img src="https://images.prom.ua/2143227305_w350_h100_ingrediyenti-dlya-pekariv.jpg"/>
                    </NavLink>

                </Box>
                <Stack direction="row" alignItems={"center"}
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
                                getCalculatedDataByDate(calculationDate);
                                navigate("/");
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
