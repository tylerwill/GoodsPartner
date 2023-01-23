import React from 'react'
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from '@mui/material'

interface Props {
    open: boolean
    setOpen: (open: boolean) => void
    onAction: () => void
    title: string
    text: string
}

export const ConfirmationDialog = ({ open, setOpen, onAction, title, text }: Props) => {
    const handleClose = () => {
        setOpen(false)
    }

    const handleAction = () => {
        onAction()
        setOpen(false)
    }

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            aria-labelledby='alert-dialog-title'
            aria-describedby='alert-dialog-description'
        >
            <DialogTitle id='alert-dialog-title'>{title}</DialogTitle>
            <DialogContent sx={{ mt: 2 }}>
                <DialogContentText id='alert-dialog-description'>
                    {text}
                </DialogContentText>
            </DialogContent>
            <DialogActions sx={{ pb: '16px', pr: '24px' }}>
                <Button variant='outlined' onClick={handleClose}>
                    Скасувати
                </Button>
                <Button
                    variant='contained'
                    color={'error'}
                    onClick={handleAction}
                    autoFocus
                >
                    Підтвердити
                </Button>
            </DialogActions>
        </Dialog>
    )
}
