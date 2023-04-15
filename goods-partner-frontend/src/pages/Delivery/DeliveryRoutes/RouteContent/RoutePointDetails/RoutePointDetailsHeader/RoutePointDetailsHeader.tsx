import React from 'react'
import Box from '@mui/material/Box'
import {Button, Tooltip, Typography} from '@mui/material'
import {RoutePoint} from '../../../../../../model/RoutePoint'
import RoutePointSelect from '../RoutePointSelect/RoutePointSelect'
import RoutePointOrdersDialog from '../RoutePointOrdersDialog/RoutePointOrdersDialog'
import DocumentsDialog from "../../DocumentsDialog/DocumentsDialog";
import DownloadIcon from "@mui/icons-material/Download";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import {apiUrl} from "../../../../../../util/util";

interface RoutePointDetailsHeaderProps {
    routePoint: RoutePoint
    routePointNumber: number
}

const RoutePointDetailsHeader = ({
                                     routePoint,
                                     routePointNumber
                                 }: RoutePointDetailsHeaderProps) => {
    const [orderDialogOpen, setOrderDialogOpen] = React.useState(false)
    const [documentDialogOpen, setDocumentDialogOpen] = React.useState(false)

    return (
        <Box
            sx={{
                width: '100%',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}
        >

            <Box display={'flex'} alignItems={'center'}>
                <Typography
                    sx={{fontWeight: 'bold', maxWidth: '450px', mr: 2}}
                    variant='body2'
                    component='h2'
                >
                    №{routePointNumber}, {routePoint.mapPoint.address}
                </Typography>

                {!routePoint.matchingExpectedDeliveryTime
                    && <Tooltip
                        sx={{cursor:'pointer'}}
                        title={"Доставка не за розкладом"}
                        placement='top'
                        arrow
                    ><WarningAmberIcon color={'warning'}/>
                    </Tooltip>
                }
                {/*<Button variant="outlined" disabled>Змінити машину</Button>*/}
            </Box>

            <Box sx={{display: 'flex'}}>
                <Button
                    variant='text'
                    size={"small"}
                    sx={{mr: 2}}
                    onClick={() => setOrderDialogOpen(true)}
                >
                    Показати деталі
                </Button>
                <Button
                    size={"small"}
                    sx={{mr: 2, pr: 2}}
                    target={"_blank"}
                    href={`${apiUrl}/documents?routePointId=${routePoint.id}`}
                    variant='outlined'
                >
                    <DownloadIcon sx={{mr: 1}}/> Документи
                </Button>

                <RoutePointSelect routePoint={routePoint}/>
            </Box>

            {orderDialogOpen && (
                <RoutePointOrdersDialog
                    open={orderDialogOpen}
                    closeDialog={() => setOrderDialogOpen(false)}
                    routePoint={routePoint}
                />
            )}
            {documentDialogOpen &&
                <DocumentsDialog
                    isOpen={documentDialogOpen}
                    handleClose={() => setDocumentDialogOpen(false)}
                    type={'route-point'}
                    id={routePoint.id}
                />}
        </Box>
    )
}

export default RoutePointDetailsHeader
