import React from 'react';
import { nodes, allocations } from './data';

const SVG_WIDTH = 1000;
const PIXELS_PER_GB = 2;
const NODE_SPACING = 30;
const TOP_MARGIN = 20;
const ROW_SPACING = 200; // Space between rows

// Helper: convert time to x-position in SVG space
const getTimeX = (time, globalStart, globalEnd) =>
    ((time - globalStart) / (globalEnd - globalStart)) * SVG_WIDTH;

// Layout function: assign memory offset to prevent vertical overlaps within a node
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

// NEW: Layout function to assign row positions to nodes to prevent time overlaps
function layoutNodesByRows(nodes) {
    const sorted = [...nodes].sort((a, b) => a.start - b.start);
    const rows = [];

    return sorted.map((node) => {
        let rowIndex = 0;

        // Find the first row where this node doesn't time-overlap with existing nodes
        while (true) {
            const rowNodes = rows[rowIndex] || [];
            const overlapping = rowNodes.some((existing) =>
                !(existing.end <= node.start || existing.start >= node.end)
            );

            if (!overlapping) {
                // Found a free row
                if (!rows[rowIndex]) rows[rowIndex] = [];
                rows[rowIndex].push(node);
                break;
            }
            rowIndex++;
        }

        return { ...node, rowIndex };
    });
}

function AllocationChart({ nodes, allocations }) {
    // Global time range (for consistent horizontal scale)
    const globalStart = Math.min(...nodes.map((n) => n.start));
    const globalEnd = Math.max(...nodes.map((n) => n.end));

    // Layout nodes by rows to prevent time overlaps
    const nodesWithRows = layoutNodesByRows(nodes);

    // Layout allocations per node
    const layoutPerNode = nodesWithRows.map((node) => {
        const nodeAllocs = allocations.filter(
            (a) => a.nodeId === node.id && a.start >= node.start && a.end <= node.end
        );
        const positioned = layoutAllocationsByMemory(node, nodeAllocs);
        return { node, allocations: positioned };
    });

    // Calculate total height based on the maximum row index (with extra space for cost labels and time axis)
    const maxRowIndex = Math.max(...nodesWithRows.map(n => n.rowIndex), 0);
    const totalHeight = (maxRowIndex + 1) * ROW_SPACING + TOP_MARGIN + 160;

    return (
        <div>


            <svg width={SVG_WIDTH} height={totalHeight} style={{ border: '1px solid #ddd' }}>
                {/* Grid lines for time reference */}
                {Array.from({ length: Math.ceil((globalEnd - globalStart) / 5) + 1 }, (_, i) => {
                    const time = globalStart + i * 5;
                    if (time > globalEnd) return null;
                    const x = getTimeX(time, globalStart, globalEnd);
                    return (
                        <line
                            key={`grid-${time}`}
                            x1={x}
                            y1={0}
                            x2={x}
                            y2={totalHeight}
                            stroke="#f0f0f0"
                            strokeWidth={1}
                            strokeDasharray="2,2"
                        />
                    );
                })}

                {layoutPerNode.map(({ node, allocations }) => {
                    const nodeHeight = node.memory * PIXELS_PER_GB;
                    const nodeX = getTimeX(node.start, globalStart, globalEnd);
                    const nodeWidth = getTimeX(node.end, globalStart, globalEnd) - nodeX;

                    // Position based on row index instead of sequential stacking
                    const groupY = node.rowIndex * ROW_SPACING + TOP_MARGIN;

                    return (
                        <g key={node.id}>
                            {/* Node label */}
                            <text
                                x={nodeX + 5}
                                y={groupY - 5}
                                fontSize={12}
                                fontWeight="bold"
                                fill="#333"
                            >
                                {node.id} ({node.memory} GB)
                            </text>

                            {/* Node rectangle based on active time */}
                            <rect
                                x={nodeX}
                                y={groupY}
                                width={nodeWidth}
                                height={nodeHeight}
                                fill="#f8f8f8"
                                stroke="#ccc"
                                strokeWidth={2}
                                rx={5}
                                ry={5}
                            />

                            {/* Node time range indicators - split into 3 labels */}
                            {/* Start time label (left) */}
                            <text
                                x={nodeX}
                                y={groupY + nodeHeight + 15}
                                fontSize={10}
                                fill="#666"
                                textAnchor="start"
                            >
                                t={node.start}
                            </text>

                            {/* Cost and duration label (center) */}
                            <text
                                x={nodeX + nodeWidth / 2}
                                y={groupY + nodeHeight + 15}
                                fontSize={10}
                                fill="#666"
                                textAnchor="middle"
                            >
                                €{((node.end - node.start) * node.memory / 128).toFixed(2)} ({node.end - node.start}h)
                            </text>

                            {/* End time label (right) */}
                            <text
                                x={nodeX + nodeWidth}
                                y={groupY + nodeHeight + 15}
                                fontSize={10}
                                fill="#666"
                                textAnchor="end"
                            >
                                t={node.end}
                            </text>

                            {/* Allocations inside node */}
                            {allocations.map((alloc) => {
                                const x = getTimeX(alloc.start, globalStart, globalEnd);
                                const width = getTimeX(alloc.end, globalStart, globalEnd) - x;
                                const y = groupY + alloc.memoryOffset * PIXELS_PER_GB;
                                const height = alloc.memory * PIXELS_PER_GB;

                                // Generate mini chart path if graphData exists
                                let chartPath = '';
                                if (alloc.graphData && alloc.graphData.length > 0) {
                                    const chartPadding = 4;
                                    const chartWidth = width - (chartPadding * 2);
                                    const chartHeight = height - (chartPadding * 2) - 15; // Leave space for text
                                    const chartX = x + chartPadding;
                                    const chartY = y + chartPadding + 12; // Offset for text

                                    const maxValue = Math.max(...alloc.graphData);
                                    const minValue = Math.min(...alloc.graphData);
                                    const valueRange = maxValue - minValue || 1; // Avoid division by zero

                                    const points = alloc.graphData.map((value, index) => {
                                        const pointX = chartX + (index / (alloc.graphData.length - 1)) * chartWidth;
                                        const pointY = chartY + chartHeight - ((value - minValue) / valueRange) * chartHeight;
                                        return `${pointX},${pointY}`;
                                    });

                                    chartPath = `M${points.join(' L')}`;
                                }

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
                                            stroke="#2c5aa0"
                                            strokeWidth={1}
                                        />

                                        {/* Resource allocation text */}
                                        <text
                                            x={x + 3}
                                            y={y + Math.min(height - 2, 12)}
                                            fontSize={9}
                                            fill="white"
                                            fontWeight="bold"
                                        >
                                            {alloc.id} ({alloc.memory}GB)
                                        </text>

                                        {/* Mini line chart */}
                                        {chartPath && (
                                            <g>
                                                {/* Chart background */}
                                                <rect
                                                    x={x + 4}
                                                    y={y + 16}
                                                    width={width - 8}
                                                    height={height - 20}
                                                    fill="rgba(255,255,255,0.1)"
                                                    rx={2}
                                                    ry={2}
                                                />
                                                {/* Chart line */}
                                                <path
                                                    d={chartPath}
                                                    stroke="#ffffff"
                                                    strokeWidth={1.5}
                                                    fill="none"
                                                    opacity="0.9"
                                                />
                                                {/* Data points */}
                                                {alloc.graphData.map((value, index) => {
                                                    const chartPadding = 4;
                                                    const chartWidth = width - (chartPadding * 2);
                                                    const chartHeight = height - (chartPadding * 2) - 15;
                                                    const chartX = x + chartPadding;
                                                    const chartY = y + chartPadding + 12;

                                                    const maxValue = Math.max(...alloc.graphData);
                                                    const minValue = Math.min(...alloc.graphData);
                                                    const valueRange = maxValue - minValue || 1;

                                                    const pointX = chartX + (index / (alloc.graphData.length - 1)) * chartWidth;
                                                    const pointY = chartY + chartHeight - ((value - minValue) / valueRange) * chartHeight;

                                                    return (
                                                        <circle
                                                            key={index}
                                                            cx={pointX}
                                                            cy={pointY}
                                                            r={1.5}
                                                            fill="#ffffff"
                                                            opacity="0.8"
                                                        />
                                                    );
                                                })}
                                            </g>
                                        )}
                                    </g>
                                );
                            })}
                        </g>
                    );
                })}

                {/* Time axis */}
                <g>
                    {Array.from({ length: Math.ceil((globalEnd - globalStart) / 5) + 1 }, (_, i) => {
                        const time = globalStart + i * 5;
                        if (time > globalEnd) return null;
                        const x = getTimeX(time, globalStart, globalEnd);
                        return (
                            <g key={`axis-${time}`}>
                                <line
                                    x1={x}
                                    y1={totalHeight - 80}
                                    x2={x}
                                    y2={totalHeight - 70}
                                    stroke="#333"
                                    strokeWidth={2}
                                />
                                <text
                                    x={x}
                                    y={totalHeight - 55}
                                    fontSize={12}
                                    fill="#333"
                                    textAnchor="middle"
                                    fontWeight="bold"
                                >
                                    {time}
                                </text>
                            </g>
                        );
                    })}
                </g>
            </svg>

            {/* Cost calculation legend below the chart */}
            <div style={{ marginTop: '20px', padding: '10px', backgroundColor: '#f9f9f9', borderRadius: '5px', fontSize: '14px' }}>
                <strong>Cost Calculation:</strong> 128GB × 1hour = €1.00 base rate
            </div>
        </div>
    );
}

export default function App17() {
    return (
        <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
            <h2 style={{ color: '#333', marginBottom: '10px' }}>
                Resource Allocation by Node (Row-Based Layout)
            </h2>
            <p style={{ color: '#666', marginBottom: '20px', fontSize: '14px' }}>
                Nodes are automatically placed in rows to minimize vertical space.
            </p>
            <AllocationChart nodes={nodes} allocations={allocations} />
        </div>
    );
}