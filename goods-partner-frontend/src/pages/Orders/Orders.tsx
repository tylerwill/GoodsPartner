import React from 'react'
import {Box, Typography} from '@mui/material'
// @ts-ignore
import BasicTabs from '../../hoc/BasicTabs/BasicTabs'
import {useActions, useAppSelector} from '../../hooks/redux-hooks'
import {DeliveredOrdersContainer} from "./DeliveredOrdersContainer/DeliveredOrdersContainer";
import { ScheduledOrdersContainer } from './ScheduledOrdersContainer/ScheduledOrdersContainer'
import {SkippedOrdersContainer} from "./SkippedOrdersContainer/SkippedOrdersContainer";

export const Orders = () => {
    const {tabIndex} = useAppSelector(state => state.orders)

    const {logout} = useActions();

    const tabLabels = [
        {name: 'Доставленні', enabled: true},
        {name: 'Не доставленні', enabled: true},
        {name: 'Заплановані', enabled: true},
    ]

    const styles = {p: 4}

    return (
        <section>
            <Box
                sx={{
                    mt: 2,
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                }}
            >
                <Box sx={{display: 'flex'}}>
                    <Typography sx={{mr: 2}} variant='h6' component='h2'>
                        Замовлення
                    </Typography>
                </Box>
            </Box>

            <Box sx={{marginTop: '16px'}}>
                <BasicTabs
                    labels={tabLabels}
                    tabIndex={tabIndex}
                    setTabIndex={(index: number) => setTabIndex(index)}
                    fullWidth={false}
                    styles={styles}
                >
                    <DeliveredOrdersContainer/>
                    <SkippedOrdersContainer/>
                    <ScheduledOrdersContainer/>
                </BasicTabs>
            </Box>
        </section>
    )
}
