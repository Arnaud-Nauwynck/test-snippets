// index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

import reportWebVitals from './reportWebVitals';
import './index.css';
import App1Prompt from './App1Prompt';
import App2Prompt from './App2Prompt';
import App3Prompt from './App3Prompt';
import App4Prompt from './App4Prompt';
import App5Prompt from './App5Prompt';
import App6Prompt from './App6Prompt';
import App7Prompt from './App7Prompt';
import App8Prompt from './App8Prompt';
import App9Prompt from './App9Prompt';
import App10Prompt from './App10Prompt';
import App11Prompt from './App11Prompt';
import App12Prompt from './App12Prompt';
import App13Prompt from './App13Prompt';
import App14Prompt from './App14Prompt';
import App15Prompt from './App15Prompt';
import App16Prompt from './App16Prompt';
import App17Prompt from './App17Prompt';

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <React.StrictMode>
        <BrowserRouter>
            <div style={{ padding: 20 }}>
                <nav className="tabs">
                    <ul>
                        <li><Link to="/app1">1</Link></li>
                        <li><Link to="/app2">2</Link></li>
                        <li><Link to="/app3">3</Link></li>
                        <li><Link to="/app4">4</Link></li>
                        <li><Link to="/app5">5</Link></li>
                        <li><Link to="/app6">6</Link></li>
                        <li><Link to="/app7">7</Link></li>
                        <li><Link to="/app8">8</Link></li>
                        <li><Link to="/app9">9</Link></li>
                        <li><Link to="/app10">10</Link></li>
                        <li><Link to="/app11">11</Link></li>
                        <li><Link to="/app12">12</Link></li>
                        <li><Link to="/app13">13</Link></li>
                        <li><Link to="/app14">14</Link></li>
                        <li><Link to="/app15">15</Link></li>
                        <li><Link to="/app16">16</Link></li>
                        <li><Link to="/app17">17</Link></li>
                    </ul>
                </nav>

                <Routes>
                    <Route path="/app1" element={<App1Prompt />} />
                    <Route path="/app2" element={<App2Prompt />} />
                    <Route path="/app3" element={<App3Prompt />} />
                    <Route path="/app4" element={<App4Prompt />} />
                    <Route path="/app5" element={<App5Prompt />} />
                    <Route path="/app6" element={<App6Prompt />} />
                    <Route path="/app7" element={<App7Prompt />} />
                    <Route path="/app8" element={<App8Prompt />} />
                    <Route path="/app9" element={<App9Prompt />} />
                    <Route path="/app10" element={<App10Prompt />} />
                    <Route path="/app11" element={<App11Prompt />} />
                    <Route path="/app12" element={<App12Prompt />} />
                    <Route path="/app13" element={<App13Prompt />} />
                    <Route path="/app14" element={<App14Prompt />} />
                    <Route path="/app15" element={<App15Prompt />} />
                    <Route path="/app16" element={<App16Prompt />} />
                    <Route path="/app17" element={<App17Prompt />} />
                </Routes>
            </div>
        </BrowserRouter>
    </React.StrictMode>
);

reportWebVitals();
