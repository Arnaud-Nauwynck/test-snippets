import {PromptTextArea} from "./PromptText";
import App11 from "./App11";

let promptedText=`
(switching to Claude Sonnet)

I have a react app, to display graphically in svg the allocations of resources per nodes. 
I have a list of nodes in time, with their memory capacity, and I have a list of resources allocated with their time and associated node.
Refactor the code below to place nodes on free rows, as resources within nodes.
... previous code...
`;

let response = `
I'll refactor your code to place nodes on free rows, similar to how resources are positioned within nodes. This will allow for a more compact vertical layout when nodes have non-overlapping time periods.Refactored Node Allocation ChartInteractive artifact The key changes I made to refactor your code:
Main Changes:

Added layoutNodesByRows() function: This works similarly to your allocation layout function but for nodes. It finds the first available row where a node doesn't time-overlap with existing nodes in that row.
Row-based positioning: Instead of stacking nodes vertically one after another, nodes are now positioned based on their assigned row index, allowing for much more compact layouts.
Dynamic height calculation: The total SVG height is now calculated based on the maximum row index rather than cumulative node heights.

Key Features:

Efficient space usage: Nodes with non-overlapping time periods are placed on the same row
Visual improvements: Added rounded corners, better colors, and a time axis at the bottom
Row indication: Node labels now show which row they're on for clarity
Maintained functionality: All your original allocation positioning logic within nodes remains intact

How it works:

Nodes are sorted by start time
For each node, the algorithm finds the first row where it doesn't time-overlap with existing nodes
Nodes are positioned at rowIndex * (maxNodeHeight + ROW_SPACING)
Allocations within each node continue to use your original memory-based positioning

This approach significantly reduces vertical space when you have many nodes with staggered time periods, while maintaining the clear visualization of resource allocations within each node.
`;


export default function App11Prompt() {
    return (<div>
        <App11 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}