import {PromptTextArea} from "./PromptText";
import App6 from "./App6";

let promptedText=`
after this refactoring, the resources now overlapped
`;

export default function App6Prompt() {
    return (<div>
        <App6 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}