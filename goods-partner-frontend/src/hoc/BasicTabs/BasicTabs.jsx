import * as React from 'react';
import PropTypes from 'prop-types';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';

function TabPanel(props) {
    const {children, value, index, ...other} = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={{p: 3}}>
                    {children}
                </Box>
            )}
        </div>
    );
}

TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.number.isRequired,
    value: PropTypes.number.isRequired,
};

function a11yProps(index) {
    return {
        id: `simple-tab-${index}`,
        'aria-controls': `simple-tabpanel-${index}`,
    };
}

export default function BasicTabs({children, labels}) {
    const [value, setValue] = React.useState(0);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <Box sx={{width: '100%'}}>
            <Box >
                <Tabs value={value} onChange={handleChange} aria-label="basic tabs example">
                    {
                        children.map((element, index) => <Tab sx={{borderBottom: 1, borderColor: 'divider'}}
                                                              key={"tab" + labels[index]}
                                                              label={labels[index]} {...a11yProps(index)} />)
                    }
                </Tabs>
            </Box>
            {
                children.map((element, index) => {
                    return (<TabPanel key={"tabPanel" + labels[index]} value={value} index={index}>
                        {element}
                    </TabPanel>)
                })
            }
        </Box>
    );
}
