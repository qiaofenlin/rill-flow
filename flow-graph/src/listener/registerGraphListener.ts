import { Cell, Graph } from '@antv/x6';
import { Channel } from '../common/transmit';
import { CustomEventTypeEnum } from '../common/enums';
import { OptEnum } from '../models/enums/optEnum';
import { Edge } from "../models/node";

export function registerGraphListener(graph: Graph, opt: OptEnum) {
  graph.on('node:mouseenter', ({ e, node, view }) => {
    if (opt === OptEnum.DISPLAY) {
      return;
    }
    const ports = view.container.querySelectorAll('.x6-port-body') as NodeListOf<SVGAElement>;
    showPorts(ports, true);
    node.addTools({
      name: 'button-remove',
      args: {
        x: node.store.data.size.width - 15,
        y: 3,
        offset: { x: 10, y: 10 },
      },
    });
  });
  graph.on('node:mouseleave', ({ e, node, view }) => {
    if (opt === OptEnum.DISPLAY) {
      return;
    }

    const ports = view.container.querySelectorAll('.x6-port-body') as NodeListOf<SVGAElement>;
    showPorts(ports, false);
    node.removeTools();
  });
  graph.on('node:collapse', ({ node, e }: any) => {
    e.stopPropagation();
    node.toggleCollapse();
    const collapsed = node.isCollapsed();
    const cells = node.getDescendants();
    cells.forEach((n: any) => {
      if (collapsed) {
        n.hide();
      } else {
        n.hide();
        n.show();
      }
    });
  });

  graph.on('node:click', ({ cell }) => {
    Channel.dispatchEvent(CustomEventTypeEnum.NODE_CLICK, cell);
  });

  graph.on(
    'cell:change:*',
    (args: {
      cell: Cell;
      key: string; // 通过 key 来确定改变项
      current: any; // 当前值，类型根据 key 指代的类型确定
      previous: any; // 改变之前的值，类型根据 key 指代的类型确定
      options: any; // 透传的 options
    }) => {
      if (args.key === 'label') {
        args.cell.setAttrs({ label: { text: args.current } });
        args.cell.getData().tooltip = args.current;
      } else if (args.key === 'nodeDelete') {
        // 节点删除
        args.cell.remove();
      }
    },
  );

  graph.on('node:added', ({ node }) => {
    Channel.dispatchEvent(CustomEventTypeEnum.NODE_ADD, node);
  });

  graph.on('edge:connected', ({ edge }) => {
    // 对新创建的边进行插入数据库等持久化操作
    const rillEdge = new Edge();
    rillEdge.sourceNodeId = edge.store.data.source.cell;
    rillEdge.targetNodeId = edge.store.data.target.cell;
    rillEdge.sourcePortId = edge.store.data.source.port;
    rillEdge.targetPortId = edge.store.data.target.port;
    Channel.dispatchEvent(CustomEventTypeEnum.EDGE_ADD, rillEdge);
  });

  graph.on('node:removed', ({ node, index, options }) => {
    Channel.dispatchEvent(CustomEventTypeEnum.NODE_REMOVE, node);
  });
  graph.on('edge:removed', ({ edge, index, options }) => {
    const rillEdge = new Edge();
    rillEdge.sourceNodeId = edge.store.data.source.cell;
    rillEdge.targetNodeId = edge.store.data.target.cell;
    rillEdge.sourcePortId = edge.store.data.source.port;
    rillEdge.targetPortId = edge.store.data.target.port;
    Channel.dispatchEvent(CustomEventTypeEnum.EDGE_REMOVE, rillEdge);
  });

  graph.on('edge:mouseenter', ({ cell }) => {
    cell.addTools(
      [
        { name: 'vertices' },
        {
          name: 'button-remove',
          args: { distance: 20  },
        },
      ],
    )
  })

  graph.on('edge:mouseleave', ({ cell }) => {
    if (cell.hasTool('button-remove')) {
      cell.removeTool('button-remove')
    }
  })

  graph.on('node:change:size', ({ node, options }) => {
    console.log('node:change:size', node, options)
    if (options.skipParentHandler) {
      return
    }

    const children = node.getChildren()
    if (children && children.length) {
      node.prop('originSize', node.getSize())
    }
  })

  graph.on('node:change:position', ({ node, options }) => {
    console.log('node:change:position', node.store.data.label, node, options)
    if (options.skipParentHandler) {
      return
    }

    const children = node.getChildren()
    if (children && children.length) {
      node.prop('originPosition', node.getPosition())
    }

    const parent = node.getParent()
    if (parent && parent.isNode()) {
      let originSize = parent.prop('originSize')
      if (originSize == null) {
        parent.prop('originSize', parent.getSize())
      }
      originSize = parent.prop('originSize')

      let originPosition = parent.prop('originPosition')
      if (originPosition == null) {
        parent.prop('originPosition', parent.getPosition())
      }
      originPosition = parent.prop('originPosition')

      let x = originPosition.x
      let y = originPosition.y
      let cornerX = originPosition.x + originSize.width
      let cornerY = originPosition.y + originSize.height
      let hasChange = false

      const children = parent.getChildren()
      if (children) {
        children.forEach((child) => {
          const bbox = child.getBBox()
          const corner = bbox.getCorner()

          if (bbox.x < x) {
            x = bbox.x
            hasChange = true
          }

          if (bbox.y < y) {
            y = bbox.y
            hasChange = true
          }

          if (corner.x > cornerX) {
            cornerX = corner.x
            hasChange = true
          }

          if (corner.y > cornerY) {
            cornerY = corner.y
            hasChange = true
          }
        })
      }


      if (hasChange) {
        parent.prop(
          {
            position: { x, y },
            size: { width: cornerX - x, height: cornerY - y },
          },
          // Note that we also pass a flag so that we know we shouldn't
          // adjust the `originPosition` and `originSize` in our handlers.
          { skipParentHandler: true },
        )
      }
    }
  })
}

function showPorts(ports: NodeListOf<SVGAElement>, show: boolean) {
  for (let i = 0, len = ports.length; i < len; i = i + 1) {
    ports[i].style.visibility = show ? 'visible' : 'hidden';
  }
}
