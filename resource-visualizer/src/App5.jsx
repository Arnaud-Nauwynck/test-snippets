import React from 'react';
import { allocations, nodes } from './data';

const SVG_WIDTH = 1000;
const NODE_PADDING = 10;
const TRACK_GAP = 2;
const TIME_SPAN = 25;
const PIXELS_PER_GB = 2;
const NODE_SPACING = 30;

const getTimeX = (time) => (time / TIME_SPAN) * SVG_WIDTH;


function layoutAllocationsByMemory(node, nodeAllocs) {
    // Greedy layout that stacks overlapping resources vertically
    const allocations = [];
    const tracks = [];

    // Sort by start time
    nodeAllocs.sort((a, b) => a.start - b.start);

    nodeAllocs.forEach((alloc) => {
        for (let i = 0; ; i++) {
            if (!tracks[i]) tracks[i] = [];
            const last = tracks[i][tracks[i].length - 1];
            if (!last || last.end <= alloc.start) {
                tracks[i].push(alloc);
                allocations.push({
                    ...alloc,
                    track: i,
                });
                break;
            }
        }
    });

    return allocations;
}

function AllocationChart({ nodes, allocations }) {
    const layoutPerNode = nodes.map((node) => {
        const nodeAllocs = allocations.filter((a) => a.nodeId === node.id);
        const positioned = layoutAllocationsByMemory(node, nodeAllocs);
        return { node, allocations: positioned };
    });

    const totalHeight = layoutPerNode.reduce(
        (sum, { node }) => sum + node.memory * PIXELS_PER_GB + NODE_SPACING,
        20
    );

    let currentY = 20;

    return (
        <svg width={SVG_WIDTH} height={totalHeight}>
            {layoutPerNode.map(({ node, allocations }) => {
                const nodeHeight = node.memory * PIXELS_PER_GB;
                const groupY = currentY;
                currentY += nodeHeight + NODE_SPACING;

                return (
                    <g key={node.id} transform={`translate(0, ${groupY})`}>
                        {/* Label above node */}
                        <text x={5} y={-5} fontSize={12} fontWeight="bold">
                            {node.id} ({node.memory} GB)
                        </text>

                        {/* Node background */}
                        <rect width={SVG_WIDTH} height={nodeHeight} fill="#f8f8f8" stroke="#ccc" />

                        {/* Allocations */}
                        {allocations.map((alloc) => {
                            const x = getTimeX(alloc.start);
                            const width = getTimeX(alloc.end) - x;
                            const y = 0; // We stack vertically, so simplify layout
                            const height = alloc.memory * PIXELS_PER_GB;

                            // Optional: Compute y-offset from previous stacked tracks (if multiple tracks)
                            // Here we just stack from top down, no tracks assumed overlapping
                            const offsetY = alloc.track * (height + 2);

                            return (
                                <g key={alloc.id}>
                                    <rect
                                        x={x}
                                        y={offsetY}
                                        width={width}
                                        height={height}
                                        fill="#4a90e2"
                                        rx={3}
                                        ry={3}
                                    />
                                    <text
                                        x={x + 3}
                                        y={offsetY + 12}
                                        fontSize={10}
                                        fill="white"
                                    >
                                        {alloc.id}
                                    </text>
                                </g>
                            );
                        })}
                    </g>
                );
            })}
        </svg>
    );
}

export default function App5() {
    return (
        <div style={{ padding: 20 }}>
            <h2>Resource Allocation Visualization</h2>
            <AllocationChart nodes={nodes} allocations={allocations} />
        </div>
    );
}
