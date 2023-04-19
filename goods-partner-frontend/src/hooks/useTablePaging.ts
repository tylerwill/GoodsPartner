import {ChangeEvent, useMemo, useState} from "react";

export const useTablePaging = (rowsCount: number = 0): [number, number, (event: any, newPage: number) => void,
    (event: ChangeEvent<HTMLInputElement>) => void, number] => {

    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowsPerPage] = useState(25)

    const handleChangePage = (event: any, newPage: number) => {
        setPage(newPage)
    }

    const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10))
        setPage(0)
    }

    const emptyRows =
        useMemo(() => page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rowsCount) : 0,
        [page, rowsPerPage, rowsCount]);

    return [page, rowsPerPage, handleChangePage, handleChangeRowsPerPage, emptyRows]
}