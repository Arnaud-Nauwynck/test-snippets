import {PromptTextArea} from "./PromptText";
import App10 from "./App10";

let promptedText=`
`;

export default function App10Prompt() {
    return (<div>
        <App10 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}