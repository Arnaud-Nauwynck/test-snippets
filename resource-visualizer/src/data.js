// data.js
export const nodes = [
    { id: 'node1', memory: 64, start: 0, end: 27 },
    { id: 'node2', memory: 128, start: 5, end: 28 },
    { id: 'node3', memory: 64, start: 2, end: 30  },
    { id: 'node4', memory: 32, start: 28, end: 31  },
    { id: 'node5', memory: 128, start: 29, end: 35  },
];

export const allocations = [
    { id: 'res1', nodeId: 'node1', start: 0, end: 10, memory: 16, graphData: [ 1, 2, 3, 3, 4, 5, 6, 4, 2, 3, 10 ] },
    { id: 'res2', nodeId: 'node1', start: 5, end: 15, memory: 32, graphData: [ 10, 12, 13, 13, 14, 25, 26, 24, 22, 23, 30 ] },
    { id: 'res3', nodeId: 'node2', start: 8, end: 20, memory: 64 },
    { id: 'res4', nodeId: 'node3', start: 2, end: 12, memory: 32 },
    { id: 'res5', nodeId: 'node1', start: 12, end: 22, memory: 16 },
    { id: 'res6', nodeId: 'node2', start: 12, end: 22, memory: 32 },
    { id: 'res7', nodeId: 'node3', start: 12, end: 22, memory: 32 },
    { id: 'res8', nodeId: 'node3', start: 12, end: 22, memory: 32 },
    { id: 'res9', nodeId: 'node1', start: 14, end: 18, memory: 16 },
];
