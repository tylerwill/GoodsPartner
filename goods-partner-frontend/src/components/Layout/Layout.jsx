import * as React from 'react';
import {styled} from '@mui/material/styles';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import {Container} from "@mui/material";
import Header from "../Header/Header";
import Sidebar from "../Sidebar/Sidebar";

const drawerWidth = 256;

const Main = styled('main', {shouldForwardProp: (prop) => prop !== 'open'})(
    ({theme, open}) => ({
        background: '#F5F5F5',
        mt: 2,
        minHeight: '100vh',
        flexGrow: 1,
        marginLeft: `-${drawerWidth}px`,
        padding: theme.spacing(3),
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),

        ...(open && {
            transition: theme.transitions.create('margin', {
                easing: theme.transitions.easing.easeOut,
                duration: theme.transitions.duration.enteringScreen,
            }),
        }),
    }),
);

export default function Layout(props) {

    const [open, setOpen] = React.useState(true);
    const [subMenuOpen, setSubMenuOpen] = React.useState(true);



    const changeCollapsedSubmenu = () => {
        setSubMenuOpen(!subMenuOpen);
    }

    return (
        <Box sx={{display: 'flex'}}>
            <Header open={open} setOpen={setOpen}/>
            <Sidebar open={open}/>
            <Main open={open}>
                <Toolbar/>
                <Container disableGutters maxWidth={"xl"}>
                    {props.children}
                </Container>
            </Main>
        </Box>
    );
}
