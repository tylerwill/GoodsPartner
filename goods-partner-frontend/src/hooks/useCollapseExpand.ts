import {useCallback, useState} from "react";

export const useCollapseExpand = (): [boolean, boolean, () => void, () => void, () => void] => {
    const [collapseAll, setCollapseAll] = useState(false)
    const [expandAll, setExpandAll] = useState(false)


    const collapseAllHandler = useCallback(() => {
        setCollapseAll(true)
        setExpandAll(false)
    }, [])

    const expandAllHandler = useCallback(() => {
        setExpandAll(true)
        setCollapseAll(false)
    }, [])

    const reset = useCallback(() => {
        setExpandAll(false)
        setCollapseAll(false)
    }, [])

    return [collapseAll, expandAll, collapseAllHandler, expandAllHandler, reset];
}