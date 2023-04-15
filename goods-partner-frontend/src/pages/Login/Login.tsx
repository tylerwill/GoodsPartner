import * as React from 'react';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import Logo from "../../components/Logo/Logo";
import {useLoginMutation} from "../../api/auth/authApi";
import {useAppDispatch} from "../../hooks/redux-hooks";
import {setUserInfo} from "../../features/auth/authSlice";
import {useNavigate} from "react-router-dom";

const theme = createTheme();

export function Login() {
    const [login] = useLoginMutation();
    const appDispatch = useAppDispatch();
    const navigate = useNavigate()

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);
        const credentials = {
            username: data.get('email'),
            password: data.get('password'),
        };
        console.log(credentials);
        const userInfo = await login(credentials).unwrap()
        console.log("userInfo", userInfo)
        appDispatch(setUserInfo(userInfo))
        navigate('/')
    };

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs" sx={{
                "height": "100vh", display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
            }}>
                <CssBaseline/>
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >

                    <Logo/>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1}}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="email"
                            label="Логін"
                            name="email"
                            autoComplete="email"
                            autoFocus
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Пароль"
                            type="password"
                            id="password"
                            autoComplete="current-password"
                        />
                        <FormControlLabel
                            control={<Checkbox value="remember" color="primary"/>}
                            label="Запам'ятай мене"
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            Увійти
                        </Button>

                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
}