import {PromptTextArea} from "./PromptText";
import App9 from "./App9";

let promptedText=`
also refactor to support start and end time on nodes, like below

export const nodes = [
    { id: 'node1', memory: 64, start: 0, end: 10 },
    { id: 'node2', memory: 128, start: 5, end: 30 },
    { id: 'node3', memory: 64, start: 2, end: 30  },
];
`;

export default function App9Prompt() {
    return (<div>
        <App9 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}