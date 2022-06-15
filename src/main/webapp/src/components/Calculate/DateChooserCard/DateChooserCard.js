import {Button, Card, CardContent, Stack, TextField, Typography} from "@mui/material";
import {useState} from "react";

function DateChooserCard({getCalculatedDataByDate}) {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);
  const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-');

  const [calculationDate, setCalculationDate] = useState(defaultDate);

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
                defaultValue={defaultDate}
                InputLabelProps={{
                  shrink: true,
                }}
                onChange={(e) => setCalculationDate(e.target.value)}
            />
            {/*  Loading button https://mui.com/material-ui/react-button/ */}
            <Button variant="contained"
                    onClick={() => {
                      getCalculatedDataByDate(calculationDate)
                    }}>
              Розрахувати
            </Button>
            <Button variant="contained" disabled>
              Машини
            </Button>
          </Stack>
        </CardContent>
      </Card>
  );
}

export default DateChooserCard;
