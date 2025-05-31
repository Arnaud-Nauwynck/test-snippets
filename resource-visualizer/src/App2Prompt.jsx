import {PromptTextArea} from "./PromptText";
import App2 from "./App2";

let promptedText=`
improve the layout so that each resource allocation get a row without overlapping with others
`;

export default function App2Prompt() {
    return (<div>
        <App2 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}