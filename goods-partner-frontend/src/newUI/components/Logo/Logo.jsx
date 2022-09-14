import * as React from 'react';
import Typography from "@mui/material/Typography";

const Logo = () => {
    return (
        <div className="logo">
            <Typography sx={{textTransform: 'uppercase'}} noWrap component="div">
                Goods partner
            </Typography>
        </div>
    )
}

export default Logo;