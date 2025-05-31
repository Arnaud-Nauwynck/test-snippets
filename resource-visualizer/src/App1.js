import React from 'react';
import { allocations, nodes } from './data';

const SVG_WIDTH = 800;
const SVG_HEIGHT = 400;
const TIME_SPAN = 25; // Total time to show
const NODE_HEIGHT = 100;
const MEMORY_SCALE = 1; // pixels per memory unit

const getNodeY = (index) => index * NODE_HEIGHT;
const getTimeX = (time) => (time / TIME_SPAN) * SVG_WIDTH;

function AllocationChart({ nodes, allocations }) {
  return (
      <svg width={SVG_WIDTH} height={nodes.length * NODE_HEIGHT}>
        {/* Draw node rows */}
        {nodes.map((node, index) => (
            <g key={node.id} transform={`translate(0, ${getNodeY(index)})`}>
              {/* Background row */}
              <rect width={SVG_WIDTH} height={NODE_HEIGHT} fill="#f9f9f9" stroke="#ccc" />
              {/* Node label */}
              <text x={5} y={15} fontSize={12}>{node.id} ({node.memory} GB)</text>

              {/* Allocations for this node */}
              {allocations
                  .filter((alloc) => alloc.nodeId === node.id)
                  .map((alloc) => {
                    const x = getTimeX(alloc.start);
                    const width = getTimeX(alloc.end) - x;
                    const height = alloc.memory * MEMORY_SCALE;
                    const y = NODE_HEIGHT - height;
                    return (
                        <rect
                            key={alloc.id}
                            x={x}
                            y={y}
                            width={width}
                            height={height}
                            fill="#4a90e2"
                            opacity="0.8"
                        >
                          <title>{alloc.id}: {alloc.memory}GB</title>
                        </rect>
                    );
                  })}
            </g>
        ))}
      </svg>
  );
}

export default function App1() {
  return (
      <div style={{ padding: 20 }}>
        <h2>Resource Allocation Visualization</h2>
        <AllocationChart nodes={nodes} allocations={allocations} />
      </div>
  );
}
