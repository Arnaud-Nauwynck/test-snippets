import {PromptTextArea} from "./PromptText";
import App3 from "./App3";

let promptedText=`
add spaces between nodes, and put label above svg rectangle
`;

export default function App3Prompt() {
    return (<div>
        <App3 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}