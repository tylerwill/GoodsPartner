import {FormControl, MenuItem, Select, SelectChangeEvent, styled} from '@mui/material'
import React, {useCallback} from 'react'
// import {
//     useCompleteRoutePointMutation,
//     useResetRoutePointMutation,
//     useSkipRoutePointMutation
// } from '../../../../../../api/routes/routes.api'
import {RoutePoint, RoutePointStatus} from "../../../../../../model/Route";

interface RoutePointSelectProps {
    routePoint: RoutePoint
}

const RoutePointSelect = ({routePoint}: RoutePointSelectProps) => {
    const {status} = routePoint
    const selectColor = getSelectColor(status)

    // TODO: [Tolik] Maybe we will need this for future
    // const [resetRoutePoint] = useResetRoutePointMutation()
    // const [skipRoutePoint] = useSkipRoutePointMutation()
    // const [completeRoutePoint] = useCompleteRoutePointMutation()

    // const handleChange = useCallback((event: SelectChangeEvent<unknown>) => {
    //     switch (event.target.value) {
    //         case RoutePointStatus.PENDING: {
    //             resetRoutePoint(routePoint.id)
    //             break
    //         }
    //         case RoutePointStatus.DONE: {
    //             completeRoutePoint(routePoint.id)
    //             break
    //         }
    //         case RoutePointStatus.SKIPPED: {
    //             skipRoutePoint(routePoint.id)
    //             break
    //         }
    //     }
    // }, [])
    const CustomSelect = styled(Select)(() => ({
        '&.MuiOutlinedInput-root': {
            '& fieldset': {
                borderColor: selectColor
            }
        },
        '& .MuiOutlinedInput-input': {
            padding: '4px 16px',
            textTransform: 'uppercase',
            fontSize: '13px',
            fontWeight: 500,
            color: selectColor,
            borderColor: selectColor
        }
    }))

    return (
        <div>
            <FormControl>
                <CustomSelect
                    value={status}

                    autoWidth
                    MenuProps={{MenuListProps: {disablePadding: true}}}
                >
                    <MenuItem value={'PENDING'}>В очікуванні</MenuItem>
                    <MenuItem value={'DONE'}>Готово</MenuItem>
                    <MenuItem value={'SKIPPED'}>Пропущено</MenuItem>
                    <MenuItem value={'INPROGRESS'}>В роботі</MenuItem>
                </CustomSelect>
            </FormControl>
        </div>
    )
}

function getSelectColor(status: string) {
    switch (status) {
        case 'PENDING':
            return '#1976D2'
        case 'DONE':
            return '#2E7D32'
        case 'SKIPPED':
            return '#ED0202FF'
        case 'INPROGRESS':
            return '#fd7200'
    }
}

export default RoutePointSelect
