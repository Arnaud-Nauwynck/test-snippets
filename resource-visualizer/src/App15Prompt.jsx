import {PromptTextArea} from "./PromptText";
import App15 from "./App15";

let promptedText=`
to move the time axis legend below, I have 
I have increased the totalHeight

const totalHeight = (maxRowIndex + 1) * ROW_SPACING + TOP_MARGIN + 160;
Then offset to +20 the y coordinate of line and text

<line
    x1={x}
    y1={totalHeight - 80}
    x2={x}
    y2={totalHeight - 70}
    stroke="#333"
    strokeWidth={2}
/>
<text
    x={x}
    y={totalHeight - 55}
    fontSize={12}
    fill="#333"
    textAnchor="middle"
    fontWeight="bold"
>
`;

let response = `
Perfect! I've applied your changes:

Increased total height to 160 pixels of extra space
Moved the time axis to your specified positions:

Line ticks: y1={totalHeight - 80} to y2={totalHeight - 70}
Text labels: y={totalHeight - 55}



This gives much more breathing room between the nodes and the time axis, ensuring no overlap with the cost labels on the nodes.
`;


export default function App15Prompt() {
    return (<div>
        <App15 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}