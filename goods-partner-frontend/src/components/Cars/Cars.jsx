import React from "react";
import {connect} from "react-redux";
import {compose} from "redux";
import {getCars} from "../../redux/thunks/calculate-thunk";
import Grid from "@mui/material/Grid";
import CarTable from "./CarTable/CarTable";

class Cars extends React.Component {

    getCars = () => {
        this.props.getCars();
    }

    componentDidMount() {
        this.getCars();
    }

    render() {
        return (
            <Grid item xs={12}>
                <CarTable cars={this.props.cars}/>
            </Grid>
        )
    }
}

let mapStateToProps = (state) => {
    return {
        cars: state.cars
    }
}

export default compose(connect(mapStateToProps, {getCars}))(Cars);
