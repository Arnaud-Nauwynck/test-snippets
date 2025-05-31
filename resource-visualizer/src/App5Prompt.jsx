import {PromptTextArea} from "./PromptText";
import App5 from "./App5";

let promptedText=`
refactor layout so that each node and resource height is proportional to memory resource
`;

export default function App5Prompt() {
    return (<div>
        <App5 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}