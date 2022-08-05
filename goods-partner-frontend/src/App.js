import './App.css';
import {Route, Routes} from 'react-router-dom';
import CalculateContainer from "./components/Calculate/CalculateContainer";
import Cars from "./components/Cars/Cars";


function App() {
    return (
        <Routes>
            <Route path="/" element={<CalculateContainer/>}/>
            <Route path="/cars" element={<Cars/>}/>
        </Routes>
    );
}

export default App;
