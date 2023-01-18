import React from 'react'
import {
	Bar,
	BarChart,
	CartesianGrid,
	Legend,
	Tooltip,
	XAxis,
	YAxis
} from 'recharts'

export default ({ xAxisName, yAxisName, data }) => {
	return (
		<BarChart width={900} height={400} data={data}>
			<CartesianGrid strokeDasharray='3 3' />
			<XAxis dataKey={xAxisName} tick={{ fontSize: 12 }} />
			<YAxis width={40} dataKey={yAxisName} tick={{ fontSize: 12 }} />
			<Tooltip />
			<Legend />
			<Bar dataKey={yAxisName} fill='#1976d2' barSize={10} />
		</BarChart>
	)
}
