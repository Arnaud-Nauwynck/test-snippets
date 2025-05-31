import {PromptTextArea} from "./PromptText";
import App8 from "./App8";

let promptedText=`
also add labels to the resources, to show memory size
`;

export default function App8Prompt() {
    return (<div>
        <App8 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}