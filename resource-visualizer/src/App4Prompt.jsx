import {PromptTextArea} from "./PromptText";
import App4 from "./App4";

let promptedText=`
the first label of node is not visible, missing spaces?
`;

export default function App4Prompt() {
    return (<div>
        <App4 />
        <PromptTextArea promptedText={promptedText}></PromptTextArea>
    </div>);
}