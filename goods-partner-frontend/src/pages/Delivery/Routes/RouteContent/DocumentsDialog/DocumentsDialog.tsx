import React from "react";
import {Button, Dialog, DialogActions, DialogContent, DialogTitle} from "@mui/material";
import Grid from "@mui/material/Grid";
import DownloadIcon from "@mui/icons-material/Download";
import {apiUrl} from "../../../../../util/util";

interface DocumentsDialogProps {
    isOpen: boolean,
    handleClose: () => void,
    id: number,
    type: string
}

const DocumentsDialog = ({isOpen, handleClose, id, type}: DocumentsDialogProps) => {
    return (<Dialog sx={{minWidth: '700px'}}
                    open={isOpen} onClose={handleClose}>
        <DialogTitle> Документи для маршруту</DialogTitle>
        <DialogContent>
            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <Button sx={{mr: 2, width: '100%'}} href={`${apiUrl}/document/save/bill/by-${type}/${id}`}
                            target={"_blank"} variant="outlined">
                        <DownloadIcon sx={{mr: 1}}/> Рахунки </Button>
                </Grid>
                <Grid item xs={4}>
                    <Button sx={{mr: 2, width: '100%'}} href={`${apiUrl}/document/save/quality/by-${type}/${id}`}
                            target={"_blank"} variant="outlined">
                        <DownloadIcon sx={{mr: 1}}/> Якісні </Button>

                </Grid>
                <Grid item xs={4}>
                    <Button sx={{mr: 2, width: '100%'}} href={`${apiUrl}/document/save/invoice/by-${type}/${id}`}
                            target={"_blank"} variant="outlined">
                        <DownloadIcon sx={{mr: 1}}/> Видаткові </Button>
                </Grid>
            </Grid>
        </DialogContent>
        <DialogActions>
            <Button onClick={handleClose}>Скасувати</Button>
        </DialogActions>
    </Dialog>);
}
export default DocumentsDialog;