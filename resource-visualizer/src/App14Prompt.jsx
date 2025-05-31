import {PromptTextArea} from "./PromptText";
import App14 from "./App14";

let promptedText=`
move the bottom legend for time below, to avoid overlap with last node
`;

let response = `
Perfect! I've moved the time axis further down to avoid any overlap with the last node's cost labels. The changes made:

Increased total height: Added 20 more pixels of space (from 100 to 120)
Moved time axis down: Positioned the time axis ticks and labels further from the nodes

Tick lines now at totalHeight - 100 to totalHeight - 90
Time labels now at totalHeight - 75



This ensures there's a clear gap between the node cost information and the time axis, preventing any visual overlap or confusion.
`;


export default function App14Prompt() {
    return (<div>
        <App14 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}