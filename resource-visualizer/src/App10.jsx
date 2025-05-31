import React from 'react';
import { allocations, nodes } from './data';

const SVG_WIDTH = 1000;
const PIXELS_PER_GB = 2;
const NODE_SPACING = 30;
const TOP_MARGIN = 20;
const ROW_SPACING = 200; // Space between rows

// Helper: convert time to x-position in SVG space
const getTimeX = (time, globalStart, globalEnd) =>
    ((time - globalStart) / (globalEnd - globalStart)) * SVG_WIDTH;

// Layout function: assign memory offset to prevent vertical overlaps
function layoutAllocationsByMemory(node, nodeAllocs) {
    const sorted = [...nodeAllocs].sort((a, b) => a.start - b.start);
    const timeline = [];

    return sorted.map((alloc) => {
        let offset = 0;
        while (true) {
            const overlapping = timeline.some((existing) =>
                !(existing.end <= alloc.start || existing.start >= alloc.end) &&
                offset < existing.memoryOffset + existing.memory &&
                offset + alloc.memory > existing.memoryOffset
            );
            if (!overlapping) break;
            offset += 1;
        }

        timeline.push({ ...alloc, memoryOffset: offset });
        return { ...alloc, memoryOffset: offset };
    });
}

function AllocationChart({ nodes, allocations }) {
    // Global time range (for consistent horizontal scale)
    const globalStart = Math.min(...nodes.map((n) => n.start));
    const globalEnd = Math.max(...nodes.map((n) => n.end));

    // Sort nodes by start time
    const sortedNodes = [...nodes].sort((a, b) => a.start - b.start);

    // Layout per node
    const layoutPerNode = sortedNodes.map((node) => {
        const nodeAllocs = allocations.filter(
            (a) => a.nodeId === node.id && a.start >= node.start && a.end <= node.end
        );
        const positioned = layoutAllocationsByMemory(node, nodeAllocs);
        return { node, allocations: positioned };
    });

    // Calculate total height based on the number of rows needed
    const totalHeight = layoutPerNode.length * ROW_SPACING + TOP_MARGIN;

    return (
        <svg width={SVG_WIDTH} height={totalHeight}>
            {layoutPerNode.map(({ node, allocations }, index) => {
                const nodeHeight = node.memory * PIXELS_PER_GB;
                const nodeX = getTimeX(node.start, globalStart, globalEnd);
                const nodeWidth = getTimeX(node.end, globalStart, globalEnd) - nodeX;

                const groupY = index * ROW_SPACING + TOP_MARGIN;

                return (
                    <g key={node.id} transform={`translate(0, ${groupY})`}>
                        {/* Node label */}
                        <text x={nodeX + 5} y={-5} fontSize={12} fontWeight="bold">
                            {node.id} ({node.memory} GB)
                        </text>

                        {/* Node rectangle based on active time */}
                        <rect
                            x={nodeX}
                            y={0}
                            width={nodeWidth}
                            height={nodeHeight}
                            fill="#f8f8f8"
                            stroke="#ccc"
                        />

                        {/* Allocations inside node */}
                        {allocations.map((alloc) => {
                            const x = getTimeX(alloc.start, globalStart, globalEnd);
                            const width = getTimeX(alloc.end, globalStart, globalEnd) - x;
                            const y = alloc.memoryOffset * PIXELS_PER_GB;
                            const height = alloc.memory * PIXELS_PER_GB;

                            return (
                                <g key={alloc.id}>
                                    <rect
                                        x={x}
                                        y={y}
                                        width={width}
                                        height={height}
                                        fill="#4a90e2"
                                        opacity="0.85"
                                        rx={3}
                                        ry={3}
                                    />
                                    <text
                                        x={x + 3}
                                        y={y + Math.min(height, 12)}
                                        fontSize={10}
                                        fill="white"
                                    >
                                        {alloc.id} ({alloc.memory} GB)
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

export default function App10() {
    return (
        <div style={{ padding: 20 }}>
            <h2>Resource Allocation by Node</h2>
            <AllocationChart nodes={nodes} allocations={allocations} />
        </div>
    );
}
