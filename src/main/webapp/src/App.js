import logo from './logo.svg';
import './App.css';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import DateChooserCard from "./components/DateChooserCard/DateChooserCard";
import OrdersList from "./components/OrdersList/OrdersList";
import BasicTabs from "./components/BasicTabs/BasicTabs";

function App() {
    return (
        <div className="App">
            <Container>
                <Box sx={{flexGrow: 1}}>
                    <Grid container spacing={15}>
                        <Grid item xs={2}>
                            <DateChooserCard/>
                        </Grid>
                        <Grid item xs={10}>
                            <BasicTabs/>
                        </Grid>
                    </Grid>
                </Box>
            </Container>
        </div>
    );
}

export default App;
