# Resource-Visualizer

This project is a react webapp for displaying resource "Nodes"(=computers) 
and allocated "resources" (=programs)

This project was entierly coded using LLM prompts, 
and only editing few lines to re-adapt local context and import with local file names

```
npx create-react-app resource-visualizer
cd resource-visualizer
npm install -s react-router-dom
```

To test it on your local PC, run the following:
```
git clone --depth 1 https://github.com/Arnaud-Nauwynck/test-snippets
cd resource-visualizer
npm install
npm run start
```

Below are all the prompts that were used to develop the application.

You can see the component screenshots after each prompt.
So you might not need to run it on your local PC to understand all.

<img src="doc/images/app.png" alt="app with all prompt results" width="500"/>

  
## Prompt 1
```text
write me a web app in react, to display graphically in svg the allocations of resources per nodes.
I have a list of nodes in time, with their memory capacity, and I have a list of resources allocated with their time and associated node
```

<img src="doc/images/comp1.png" alt="component after prompt 1" width="500"/>

## Prompt 2

```text
improve the layout so that each resource allocation get a row without overlapping with others
```

<img src="doc/images/comp2.png" alt="component after prompt 2" width="500"/>


## Prompt 3

```text
add spaces between nodes, and put label above svg rectangle
```

<img src="doc/images/comp3.png" alt="component after prompt 3" width="500"/>

## Prompt 4

```text
the first label of node is not visible, missing spaces?
```

<img src="doc/images/comp4.png" alt="component after prompt 4" width="500"/>


## Prompt 5

```text
refactor layout so that each node and resource height is proportional to memory resource
```

<img src="doc/images/comp5.png" alt="component after prompt 5" width="500"/>

## Prompt 6

```text
after this refactoring, the resources now overlapped
```
<img src="doc/images/comp6.png" alt="component after prompt 6" width="500"/>


## Prompt 7

```text
reprint me the full corrected code for App.jsx 
```
( to check patches were applied correclty)


<img src="doc/images/comp7.png" alt="component after prompt 7" width="500"/>


## Prompt 8

```text
also add labels to the resources, to show memory size
```

<img src="doc/images/comp8.png" alt="component after prompt 8" width="500"/>

## Prompt 9

```text
also refactor to support start and end time on nodes, like below

export const nodes = [
    { id: 'node1', memory: 64, start: 0, end: 10 },
    { id: 'node2', memory: 128, start: 5, end: 30 },
    { id: 'node3', memory: 64, start: 2, end: 30  },
];
```

<img src="doc/images/comp9.png" alt="component after prompt 9" width="500"/>


## Prompt 10

(... daily ChatGPT Quota reached)

using MistralAI

```text
I have a react app, to display graphically in svg the allocations of resources per nodes.
I have a list of nodes in time, with their memory capacity, andI have a list of resources allocated with their time and associated node

refactor the code below to place nodes on free rows, as resources within nodes.
```

<img src="doc/images/comp10.png" alt="component after prompt 10" width="500"/>

## Prompt 11

```text
(switching to Claude Sonnet)

I have a react app, to display graphically in svg the allocations of resources per nodes. 
I have a list of nodes in time, with their memory capacity, and I have a list of resources allocated with their time and associated node.
Refactor the code below to place nodes on free rows, as resources within nodes.
... previous code...
```

<img src="doc/images/comp11.png" alt="component after prompt 11" width="500"/>


## Prompt 12

```text
simplify code, to remove the "row" labels and summary.
split the below label to be a label for startTime, and endTime, the end should also compute the duration.
also compute a approximated cost progress in euros, with 128Go x 1hour = 1 euro
```

<img src="doc/images/comp12.png" alt="component after prompt 12" width="500"/>


## Prompt 13

```text
remove the Row indicator text
move bottom legend below, with gap space
```text

retrying:
```text
remove the Row indicators text on the left side: "Row {node.rowIndex}"
```

<img src="doc/images/comp13.png" alt="component after prompt 13" width="500"/>


## Prompt 14

```text
move the bottom legend for time below, to avoid overlap with last node
```

<img src="doc/images/comp14.png" alt="component after prompt 14" width="500"/>


## Prompt 15

```text
to move the time axis legend below, I have 
I have increased the totalHeight

const totalHeight = (maxRowIndex + 1) * ROW_SPACING + TOP_MARGIN + 160;
Then offset to +20 the y coordinate of line and text
...
```

<img src="doc/images/comp15.png" alt="component after prompt 15" width="500"/>


## Prompt 16

... context exceeded, restarted new chat:
```text
I have a react app, to display graphically in svg the allocations of resources per nodes.
I have a list of nodes in time, with their memory capacity, andI have a list of resources allocated with their time and associated node
```

```text
change the label of each node "t={node.start}-{node.end}" to be split in 3 labels, left for start time, middle for cost and duration, and right for end time.
```

<img src="doc/images/comp16.png" alt="component after prompt 16" width="500"/>


## Prompt 17

```text
modify the code to add a chart graph within the rectangle of a resource allocation.
the data of the graph may be present in field graphData, as seen in below example

export const allocations = [
{ id: 'res1', nodeId: 'node1', start: 0, end: 10, memory: 16, graphData: [ 1, 2, 3, 3, 4, 5, 6, 4, 2, 3, 10 ] },
{ id: 'res2', nodeId: 'node1', start: 5, end: 15, memory: 32, graphData: [ 10, 12, 13, 13, 14, 25, 26, 24, 22, 23, 30 ] },
];
```

<img src="doc/images/comp17.png" alt="component after prompt 17" width="500"/>


## Additional Prompts for page layout

```text
refactor my standard index.js, so that I can choose between route App1 or App2, 
```

```text
refactor the main index.js so that I can click to choose between routes 1, 2, ...9
corresponding to components App1, App2, ..App9
```

```text
in index.jsx, add a textarea, with style to show it is a prompted text
```


