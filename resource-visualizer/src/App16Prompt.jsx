import {PromptTextArea} from "./PromptText";
import App16 from "./App16";

let restoreContext_promptedText=`
I have a react app, to display graphically in svg the allocations of resources per nodes.
I have a list of nodes in time, with their memory capacity, andI have a list of resources allocated with their time and associated node
... file data.js,  App.jsx
`;

let promptedText=`
change the label of each node "t={node.start}-{node.end}" to be split in 3 labels, left for start time, middle for cost and duration, and right for end time.
`;

let response = `
I'll help you modify the node labels to display start time, cost/duration, and end time in three separate labels. Let me update the code to split the single label into three positioned labels.UntitledText
                            {/* Node time range indicators - split into 3 labels */}
                            {/* Start time label (left) */}
                            <text
                                x={nodeX}

Perfect! I've updated your React component to split the node labels into three separate parts:

Left label: Shows the start time (t=0, t=5, etc.) aligned to the left edge of the node
Center label: Shows the cost and duration (€2.16 (27h), for example) centered in the middle of the node
Right label: Shows the end time (t=27, t=28, etc.) aligned to the right edge of the node

The cost calculation uses your base rate of €1.00 for 128GB × 1hour, so it calculates: (duration × memory capacity) / 128. The duration is shown in parentheses next to the cost.
Each label is properly positioned using different textAnchor values:

start for left alignment
middle for center alignment
end for right alignment

This gives you a much cleaner and more informative display of the timing and cost information for each node!
`;

let responseText_file = `
{/* Node time range indicators - split into 3 labels */}
{/* Start time label (left) */}
<text
    x={nodeX}
    y={groupY + nodeHeight + 15}
    fontSize={10}
    fill="#666"
    textAnchor="start"
>
    t={node.start}
</text>

{/* Cost and duration label (center) */}
<text
    x={nodeX + nodeWidth / 2}
    y={groupY + nodeHeight + 15}
    fontSize={10}
    fill="#666"
    textAnchor="middle"
>
    €{((node.end - node.start) * node.memory / 128).toFixed(2)} ({node.end - node.start}h)
</text>

{/* End time label (right) */}
<text
    x={nodeX + nodeWidth}
    y={groupY + nodeHeight + 15}
    fontSize={10}
    fill="#666"
    textAnchor="end"
>
    t={node.end}
</text>
`;

let prompted2Text = ` 
reprint the full App.jsx file
`;

export default function App16Prompt() {
    return (<div>
        <App16 />
        <PromptTextArea promptedText={restoreContext_promptedText}></PromptTextArea>
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
        <PromptTextArea promptedText={prompted2Text}></PromptTextArea>
    </div>);
}