import React, {useState} from "react";
import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField} from "@mui/material";

interface Props {
    open: boolean,
    setOpen: (open: boolean) => void,
    onAction: () => void,
}

export default ({open, setOpen, onAction}: Props) => {
    const handleClose = () => {
        setOpen(false);
    };

    const handleAction = () => {
        onAction();
        setOpen(false);
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">
                Запланувати доставку
            </DialogTitle>
            <DialogContent sx={{mt: 2}}>
                <DialogContentText id="alert-dialog-description">
                    Ви впевнені, що бажаєте видалити замовлення?
                    Цю дію не можна буде відмінити.
                </DialogContentText>
            </DialogContent>
            <DialogActions sx={{pb: '16px', pr: '24px'}}>
                <Button variant='outlined' onClick={handleClose}>Скасувати</Button>
                <Button variant='contained' color={"error"} onClick={handleAction} autoFocus>
                    Видалити
                </Button>
            </DialogActions>
        </Dialog>
    );
}