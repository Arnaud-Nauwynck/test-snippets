import React from 'react';
import { allocations, nodes } from './data';

const SVG_WIDTH = 1000;
const PIXELS_PER_GB = 2;
const NODE_SPACING = 30;
const TOP_MARGIN = 20;

const getTimeX = (time) => (time / 25) * SVG_WIDTH;

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
    const layoutPerNode = nodes.map((node) => {
        const nodeAllocs = allocations.filter((a) => a.nodeId === node.id);
        const positioned = layoutAllocationsByMemory(node, nodeAllocs);
        return { node, allocations: positioned };
    });

    const totalHeight = layoutPerNode.reduce(
        (sum, { node }) => sum + node.memory * PIXELS_PER_GB + NODE_SPACING,
        TOP_MARGIN
    );

    let currentY = TOP_MARGIN;

    return (
        <svg width={SVG_WIDTH} height={totalHeight}>
            {layoutPerNode.map(({ node, allocations }) => {
                const nodeHeight = node.memory * PIXELS_PER_GB;
                const groupY = currentY;
                currentY += nodeHeight + NODE_SPACING;

                return (
                    <g key={node.id} transform={`translate(0, ${groupY})`}>
                        {/* Node label */}
                        <text x={5} y={-5} fontSize={12} fontWeight="bold">
                            {node.id} ({node.memory} GB)
                        </text>

                        {/* Background rectangle */}
                        <rect width={SVG_WIDTH} height={nodeHeight} fill="#f8f8f8" stroke="#ccc" />

                        {/* Allocations */}
                        {allocations.map((alloc) => {
                            const x = getTimeX(alloc.start);
                            const width = getTimeX(alloc.end) - x;
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

export default function App8() {
    return (
        <div style={{ padding: 20 }}>
            <h2>Resource Allocation by Node</h2>
            <AllocationChart nodes={nodes} allocations={allocations} />
        </div>
    );
}
