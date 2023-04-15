import React from 'react'
import ReactDOM from 'react-dom/client'
import './reset.css'
import './index.css'
import App from './App'
// TODO: [Tolik] What is web vitals?
// import reportWebVitals from './reportWebVitals';
import {BrowserRouter} from 'react-router-dom'
import {Provider} from 'react-redux'
import store from './redux/store'
import {createTheme, ThemeProvider} from '@mui/material'
import {AuthProvider} from './auth/AuthProvider'
import IconButton from "@mui/material/IconButton";
import {closeSnackbar, SnackbarProvider} from "notistack";
import CloseIcon from "@mui/icons-material/Close";

const muiTheme = createTheme({
    components: {
        MuiCssBaseline: {
            styleOverrides: `
          .pac-container {
            z-index: 1500 !important;
          }
        `
        }
    }
})
const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
    <React.StrictMode>
        <SnackbarProvider
            anchorOrigin={{
                vertical: 'top',
                horizontal: 'right'
            }}
            action={key => (
                <IconButton
                    aria-label="Close notification"
                    color="inherit"
                    onClick={() => closeSnackbar(key)}
                >
                    <CloseIcon fontSize="small"/>
                </IconButton>
            )}>

            <ThemeProvider theme={muiTheme}>
                <BrowserRouter>
                    <Provider store={store}>
                        <AuthProvider>
                            <App/>
                        </AuthProvider>
                    </Provider>

                </BrowserRouter>

            </ThemeProvider>
        </SnackbarProvider>
    </React.StrictMode>
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
//reportWebVitals();
