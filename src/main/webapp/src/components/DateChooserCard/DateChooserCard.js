import Box from '@mui/material/Box';
import {Button, Card, CardActions, CardContent, Stack, TextField, Typography} from "@mui/material";

function DateChooserCard() {
    return (
        <Card sx={{minWidth: 200}}>
            <CardContent>
                <Stack spacing={1}>
                    <Typography variant="h6" gutterBottom component="div">
                        Вкажіть дату:
                    </Typography>

                    <TextField
                        id="date"
                        type="date"
                        defaultValue="2022-06-14"
                        InputLabelProps={{
                            shrink: true,
                        }}
                    />

                    {/*  Loading button https://mui.com/material-ui/react-button/ */}
                    <Button variant="contained">Розрахувати</Button>
                    <Button variant="contained" disabled>
                        Машини
                    </Button>
                </Stack>
            </CardContent>
        </Card>
    );
}

export default DateChooserCard;
