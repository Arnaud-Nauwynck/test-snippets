import {PromptTextArea} from "./PromptText";
import App13 from "./App13";

let promptedText=`
remove the Row indicator text
move bottom legend below, with gap space
`;

let response = `
Perfect! The changes have been made:
What was updated:

Removed: All "Row" indicator texts that were showing on the left side
Moved time axis: Positioned it lower with more gap space from the nodes
Added bottom legend: Cost calculation explanation is now below the chart in a separate styled box
Increased chart height: Added extra space to accommodate the repositioned time axis

The chart is now cleaner with the time axis properly separated at the bottom and the cost calculation legend clearly positioned below the entire visualization.
`;


let promptedText2=`
remove the Row indicators text on the left side: "Row {node.rowIndex}"
`;

let response2 = `
I see the issue - let me look for the exact text that needs to be removed. Let me search for where "Row {node.rowIndex}" appears in the code.Row-Based Node Allocation ChartInteractive artifact ∙ Version 11 Perfect! I've removed the "Row {node.rowIndex}" text that was appearing on the left side of each node. The chart is now cleaner without those row indicator labels cluttering the left margin.
`;


export default function App13Prompt() {
    return (<div>
        <App13 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
        <PromptTextArea promptedText={promptedText2}></PromptTextArea>
    </div>);
}