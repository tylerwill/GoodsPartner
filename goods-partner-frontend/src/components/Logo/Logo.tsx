import * as React from 'react';
import Typography from "@mui/material/Typography";
import {Link} from "react-router-dom";

const Logo = () => {
    return (
        <Link to={"/"} >
            <Typography sx={{textTransform: 'uppercase', color: "rgba(0, 0, 0, 0.87)", fontWeight:"bold"}} noWrap component="div">
                Goods partner
            </Typography>
        </Link>
    )
}

export default Logo;