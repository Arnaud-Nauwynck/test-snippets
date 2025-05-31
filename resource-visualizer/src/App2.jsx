import React from 'react';
import { allocations, nodes } from './data';

const SVG_WIDTH = 1000;
const ROW_HEIGHT = 20; // Each allocation track height
const NODE_PADDING = 10;
const TRACK_GAP = 2;
const TIME_SPAN = 25;

const getTimeX = (time) => (time / TIME_SPAN) * SVG_WIDTH;

// === Core logic to stack allocations ===
function layoutAllocations(nodeAllocations) {
    const tracks = [];

    nodeAllocations.sort((a, b) => a.start - b.start);

    const positioned = nodeAllocations.map((alloc) => {
        for (let i = 0; ; i++) {
            if (!tracks[i]) tracks[i] = [];
            const lastInTrack = tracks[i][tracks[i].length - 1];
            if (!lastInTrack || lastInTrack.end <= alloc.start) {
                tracks[i].push(alloc);
                return { ...alloc, track: i };
            }
        }
    });

    return { allocations: positioned, trackCount: tracks.length };
}

// === Main SVG chart ===
function AllocationChart({ nodes, allocations }) {
    const layoutPerNode = nodes.map((node) => {
        const nodeAllocs = allocations.filter((a) => a.nodeId === node.id);
        const { allocations: positioned, trackCount } = layoutAllocations(nodeAllocs);
        return { node, allocations: positioned, trackCount };
    });

    // Compute total SVG height
    const totalHeight = layoutPerNode.reduce(
        (sum, { trackCount }) => sum + (trackCount * (ROW_HEIGHT + TRACK_GAP) + NODE_PADDING * 2),
        0
    );

    let currentY = 0;

    return (
        <svg width={SVG_WIDTH} height={totalHeight}>
            {layoutPerNode.map(({ node, allocations, trackCount }) => {
                const nodeHeight = trackCount * (ROW_HEIGHT + TRACK_GAP) + NODE_PADDING * 2;
                const groupY = currentY;
                currentY += nodeHeight;

                return (
                    <g key={node.id} transform={`translate(0, ${groupY})`}>
                        {/* Node background */}
                        <rect width={SVG_WIDTH} height={nodeHeight} fill="#f0f0f0" stroke="#ccc" />
                        {/* Node label */}
                        <text x={5} y={15} fontSize={12} fontWeight="bold">{node.id} ({node.memory} GB)</text>

                        {/* Allocations */}
                        {allocations.map((alloc) => {
                            const x = getTimeX(alloc.start);
                            const width = getTimeX(alloc.end) - x;
                            const y = NODE_PADDING + alloc.track * (ROW_HEIGHT + TRACK_GAP);
                            return (
                                <g key={alloc.id}>
                                    <rect
                                        x={x}
                                        y={y}
                                        width={width}
                                        height={ROW_HEIGHT}
                                        fill="#4a90e2"
                                        opacity="0.85"
                                        rx={3}
                                        ry={3}
                                    />
                                    <text
                                        x={x + 3}
                                        y={y + 14}
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

export default function App2() {
    return (
        <div style={{ padding: 20 }}>
            <h2>Resource Allocation Visualization</h2>
            <AllocationChart nodes={nodes} allocations={allocations} />
        </div>
    );
}
