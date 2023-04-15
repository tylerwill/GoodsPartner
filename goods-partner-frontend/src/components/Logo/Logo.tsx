import Typography from '@mui/material/Typography'
import {Link} from 'react-router-dom'
import logo from '../../../public/favicon.ico'
import {Box} from "@mui/material";

const Logo = () => {
    return (
        <Link to={'/'}>
            <Box display={"flex"} alignItems={"center"}>
                <Box
                    component="img"
                    sx={{
                        height: 25,
                        width: 25,
                        mr: 1
                    }}
                    src={logo}
                />
                <Typography
                    sx={{
                        textTransform: 'uppercase',
                        color: 'rgba(0, 0, 0, 0.87)',
                        fontWeight: 'bold'
                    }}
                    noWrap
                    component='div'
                >
                    Goods partner
                </Typography>
            </Box>
        </Link>
    )
}

export default Logo
