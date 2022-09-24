import * as React from 'react';
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import CloseIcon from "@mui/icons-material/Close";
import Logo from "../Logo/Logo";

const Header = () => {
    const [open, setOpen] = React.useState(true);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    return (
        <div className="header">
            <AppBar position="fixed" open={open} sx={
                {
                    zIndex: (theme) => theme.zIndex.drawer + 1,
                    backgroundColor: '#536DFE',
                    padding: '0 8px'
                }}>
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerOpen}
                        edge="start"
                        sx={{mr: 3, ...(open && {display: 'none'})}}
                    >
                        <MenuIcon/>
                    </IconButton>

                    <IconButton
                        color="inherit"
                        aria-label="close drawer"
                        onClick={handleDrawerClose}
                        edge="start"
                        sx={{mr: 3, ...(!open && {display: 'none'})}}
                    >
                        <CloseIcon/>
                    </IconButton>
                    <Logo/>
                </Toolbar>
            </AppBar>
        </div>
    )
}

export default Header;