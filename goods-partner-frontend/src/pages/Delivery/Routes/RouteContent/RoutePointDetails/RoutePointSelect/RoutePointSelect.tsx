import {FormControl, MenuItem, Select, SelectChangeEvent, styled} from "@mui/material";
import React from "react";
import {RoutePoint} from "../../../../../../model/RoutePoint";

interface RoutePointSelectProps {
    routePoint: RoutePoint,
    updateRoutePoint: (point: RoutePoint) => void
}

const RoutePointSelect = ({routePoint, updateRoutePoint}: RoutePointSelectProps) => {
    const {status} = routePoint;
    const selectColor = getSelectColor(status);

    const statusToActionMap = {
        'PENDING': 'RESET',
        'DONE': 'COMPLETE',
        'SKIPPED': 'SKIP',
    }

    const handleChange = (event: SelectChangeEvent<unknown>) => {
        // ts-ignore
        const updatedRoutePoint = {...routePoint, status: event.target.value as string};

        updateRoutePoint(updatedRoutePoint);
    }
    const CustomSelect = styled(Select)(() => ({
        "&.MuiOutlinedInput-root": {
            "& fieldset": {
                borderColor: selectColor
            }
        },
        '& .MuiOutlinedInput-input': {
            padding: '4px 16px',
            textTransform: 'uppercase',
            fontSize: '13px',
            fontWeight: 500,
            color: selectColor
        }
    }));


    return <div>
        <FormControl>
            <CustomSelect
                value={status}
                onChange={handleChange}
                autoWidth
                MenuProps={{MenuListProps: {disablePadding: true}}}
            >
                <MenuItem value={'PENDING'}>В очікуванні</MenuItem>
                <MenuItem value={'DONE'}>Готово</MenuItem>
                <MenuItem value={'SKIPPED'}>Пропущено</MenuItem>
            </CustomSelect>
        </FormControl>
    </div>
}

function getSelectColor(status: string) {
    switch (status) {
        case 'PENDING':
            return '#1976D2'
        case 'DONE':
            return '#2E7D32'
        case 'SKIPPED':
            return '#ED6C02'
    }
}

export default RoutePointSelect;
