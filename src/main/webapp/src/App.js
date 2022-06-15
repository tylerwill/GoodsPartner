import './App.css';
import {Route, Routes} from 'react-router-dom';
import CalculateContainer from "./components/Calculate/CalculateContainer";

function App() {
  return (
      <Routes>
        <Route path="/" element={<CalculateContainer/>}/>
      </Routes>
  );
}

export default App;
