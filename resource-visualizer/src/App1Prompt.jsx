import {PromptTextArea} from "./PromptText";
import App1 from "./App1";

let promptedText=`
write me a web app in react, to display graphically in svg the allocations of resources per nodes.
I have a list of nodes in time, with their memory capacity, and I have a list of resources allocated with their time and associated node
`;

export default function App1Prompt() {
    return (<div>
        <App1 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}