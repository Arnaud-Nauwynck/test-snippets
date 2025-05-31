import {PromptTextArea} from "./PromptText";
import App12 from "./App12";

let promptedText=`
simplify code, to remove the "row" labels and summary.
split the below label to be a label for startTime, and endTime, the end should also compute the duration.
also compute a approximated cost progress in euros, with 128Go x 1hour = 1 euro
`;

let response = `
The code has been simplified with the following changes:
Removed:

Row labels and summary legend
Row indicators on the left side

Enhanced labels:

Start time: Shows at the left edge of each node
End time & Duration: Shows at the right edge with calculated duration in hours
Cost calculation: Shows centered below each node in red, calculated as (memory/128GB) × duration × €1

Cost calculation examples from your data:

node1: 64GB × 27h = €13.50 (half the rate of 128GB baseline)
node2: 128GB × 23h = €23.00 (full rate)
node4: 32GB × 3h = €0.75 (quarter rate)

The layout is now cleaner while providing more detailed timing and cost information for each node. The cost appears in red to make it stand out as an important metric.
`;


export default function App12Prompt() {
    return (<div>
        <App12 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}