import * as React from 'react';
import Typography from "@mui/material/Typography";

const Logo = () => {
    return (
        <div className="logo">
            <Typography sx={{textTransform: 'uppercase', color: "rgba(0, 0, 0, 0.87)", fontWeight:"bold"}} noWrap component="div">
                Goods partner
            </Typography>
        </div>
    )
}

export default Logo;