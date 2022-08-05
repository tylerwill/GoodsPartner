import React from "react";
import {connect} from "react-redux";
import {compose} from "redux";
import {getCars} from "../../redux/thunks/calculate-thunk";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import CarTable from "./CarTable/CarTable";
import {AppBar, Button, Stack, TextField, Toolbar, Typography} from "@mui/material";
class Cars extends React.Component {


  getCars = () => {
    this.props.getCars();
  }

    componentDidMount() {
        this.getCars();
    }

    render() {
        return (<div className="App">
            <Container>
                <Box sx={{flexGrow: 1}}>
                    <Grid container spacing={3} direction="column">
                        <Grid item xs={2}>
                            <AppBar position="static">
                                <Toolbar>
                                    <Stack direction="row"
                                           spacing={2}>
                                    </Stack>
                                </Toolbar>
                            </AppBar>
                        </Grid>
                        <Grid item xs={12}>
                            <CarTable cars={this.props.cars}/>
                        </Grid>
                    </Grid>
                </Box>
            </Container>
        </div>)
    }
}

let mapStateToProps = (state) => {
  return {
    cars: state.cars
  }
}

export default compose(connect(mapStateToProps, {getCars}))(Cars);
