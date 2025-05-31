import {PromptTextArea} from "./PromptText";
import App6 from "./App6";

let promptedText=`
reprint me the full corrected code for App.jsx
`;

export default function App6Prompt() {
    return (<div>
        <App6 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}