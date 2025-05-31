import {PromptTextArea} from "./PromptText";
import App17 from "./App17";


let promptedText=`
modify the code to add a chart graph within the rectangle of a resource allocation.
the data of the graph may be present in field graphData, as seen in below example

export const allocations = [
{ id: 'res1', nodeId: 'node1', start: 0, end: 10, memory: 16, graphData: [ 1, 2, 3, 3, 4, 5, 6, 4, 2, 3, 10 ] },
{ id: 'res2', nodeId: 'node1', start: 5, end: 15, memory: 32, graphData: [ 10, 12, 13, 13, 14, 25, 26, 24, 22, 23, 30 ] },
];
`;

let response = `
I'll modify the code to add Highcharts graphs within the resource allocation rectangles. Since we're using React components, I'll use a simple approach with SVG paths to create mini line charts within each allocation rectangle.App15.jsx - Complete FileCode ∙ Version 2                             {/* Allocations inside node */}
                            {allocations.map((alloc) => {
                                const x = getTimeX(alloc.start, globalStart, globalEnd);
                                const widthPerfect! I've modified the code to add mini line charts within each resource allocation rectangle. Here's what the updated code does:
New Features Added:

Mini Line Charts: Each allocation rectangle now displays a small line chart if graphData is provided
Auto-scaling: The charts automatically scale to fit the available space within each rectangle
Visual Elements:

Semi-transparent background for the chart area
White line connecting data points
Small circular data points for each value
Proper padding to avoid overlapping with text labels



How it Works:

Data Processing: The code checks if alloc.graphData exists and has data points
Scaling: It finds the min/max values to properly scale the chart within the available rectangle space
Positioning: Charts are positioned with padding inside each allocation rectangle, leaving space for the resource label
Path Generation: Creates an SVG path string to draw the line chart
Data Points: Adds small circles at each data point for better visibility

Usage:
Now you can add graphData arrays to your allocations in the data file:
javascriptexport const allocations = [
    { id: 'res1', nodeId: 'node1', start: 0, end: 10, memory: 16, graphData: [1, 2, 3, 3, 4, 5, 6, 4, 2, 3, 10] },
    { id: 'res2', nodeId: 'node1', start: 5, end: 15, memory: 32, graphData: [10, 12, 13, 13, 14, 25, 26, 24, 22, 23, 30] },
    // Allocations without graphData will display normally without charts
];
The charts will automatically appear within the allocation rectangles, showing the trend of your data over time!
`;


export default function App17Prompt() {
    return (<div>
        <App17 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}